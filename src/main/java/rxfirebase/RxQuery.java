package rxfirebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import rx.Emitter;
import rx.Observable;
import rx.functions.Func1;
import rxfirebase.results.FirebaseEvent;

public class RxQuery {
  private final Query query;

  RxQuery(final Query query) {
    this.query = query;
  }

  public Query toQuery() {
    return query;
  }

  public DatabaseReference toRef() {
    return query.getRef();
  }

  public Observable<FirebaseEvent> get() {
    return Observable.fromEmitter(emitter -> {
      final ValueEventListener listener = new ValueEventListener() {
        @Override public void onDataChange(final DataSnapshot dataSnapshot) {
          emitter.onNext(FirebaseEvent.create(dataSnapshot));
          emitter.onCompleted();
        }

        @Override public void onCancelled(final DatabaseError databaseError) {
          emitter.onNext(FirebaseEvent.create(databaseError));
          emitter.onCompleted();
        }
      };

      query.addListenerForSingleValueEvent(listener);

      emitter.setCancellation(() -> query.removeEventListener(listener));
    }, Emitter.BackpressureMode.BUFFER);
  }

  public <T> Observable<T> get(final Func1<DataSnapshot, T> snapshotMapper) {
    return get().filter(FirebaseEvent::isSuccessful)
        .map(FirebaseEvent::dataSnapshot)
        .filter(DataSnapshot::exists)
        .flatMap(dataSnapshot -> {
          try {
            return Observable.fromCallable(() -> snapshotMapper.call(dataSnapshot));
          } catch (Exception e) {
            return Observable.error(e);
          }
        })
        .onErrorResumeNext(error -> {
          error.printStackTrace();
          return Observable.empty(); // Ignoring error
        });
  }

  public <T> Observable<T> get(final Class<T> valueType) {
    return get(dataSnapshot -> dataSnapshot.getValue(valueType));
  }

  public Observable<FirebaseEvent> onValueEvent() {
    return Observable.fromEmitter(emitter -> {
      final ValueEventListener listener = new ValueEventListener() {
        @Override public void onDataChange(final DataSnapshot dataSnapshot) {
          emitter.onNext(FirebaseEvent.create(dataSnapshot));
        }

        @Override public void onCancelled(final DatabaseError databaseError) {
          emitter.onNext(FirebaseEvent.create(databaseError));
        }
      };

      query.addValueEventListener(listener);

      emitter.setCancellation(() -> query.removeEventListener(listener));
    }, Emitter.BackpressureMode.BUFFER);
  }

  public Observable<FirebaseEvent> onChildEvent() {
    return Observable.fromEmitter(emitter -> {
      final ChildEventListener listener = new ChildEventListener() {
        @Override public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
          emitter.onNext(FirebaseEvent.create(FirebaseEvent.Type.CHILD_ADDED, dataSnapshot,
              previousChildName));
        }

        @Override public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
          emitter.onNext(FirebaseEvent.create(FirebaseEvent.Type.CHILD_CHANGED, dataSnapshot,
              previousChildName));
        }

        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
          emitter.onNext(FirebaseEvent.create(FirebaseEvent.Type.CHILD_REMOVED, dataSnapshot));
        }

        @Override public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
          emitter.onNext(FirebaseEvent.create(FirebaseEvent.Type.CHILD_MOVED, dataSnapshot,
              previousChildName));
        }

        @Override public void onCancelled(DatabaseError databaseError) {
          emitter.onNext(FirebaseEvent.create(databaseError));
        }
      };

      query.addChildEventListener(listener);

      emitter.setCancellation(() -> query.removeEventListener(listener));
    }, Emitter.BackpressureMode.BUFFER);
  }

  public Observable<FirebaseEvent> onChildAdded() {
    return onChildEvent().filter(eventFilter(FirebaseEvent.Type.CHILD_ADDED));
  }

  public Observable<FirebaseEvent> onChildChanged() {
    return onChildEvent().filter(eventFilter(FirebaseEvent.Type.CHILD_CHANGED));
  }

  public Observable<FirebaseEvent> onChildRemoved() {
    return onChildEvent().filter(eventFilter(FirebaseEvent.Type.CHILD_REMOVED));
  }

  public Observable<FirebaseEvent> onChildMoved() {
    return onChildEvent().filter(eventFilter(FirebaseEvent.Type.CHILD_MOVED));
  }

  public static Func1<FirebaseEvent, Boolean> eventFilter(final FirebaseEvent.Type type) {
    return firebaseEvent -> firebaseEvent.type().equals(type);
  }
}

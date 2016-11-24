package rxfirebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import java.util.function.Function;
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

  public Single<DataSnapshot> get() {
    return Single.create(emitter -> {
      final ValueEventListener listener = new ValueEventListener() {
        @Override public void onDataChange(final DataSnapshot dataSnapshot) {
          emitter.onSuccess(dataSnapshot);
        }

        @Override public void onCancelled(final DatabaseError databaseError) {
          emitter.onError(databaseError.toException());
        }
      };

      query.addListenerForSingleValueEvent(listener);

      emitter.setCancellable(() -> query.removeEventListener(listener));
    });
  }

  public <T> Maybe<T> get(final Function<DataSnapshot, T> snapshotMapper) {
    return get().flatMapMaybe(dataSnapshot -> {
      if (dataSnapshot.exists()) {
        return Maybe.fromCallable(() -> snapshotMapper.apply(dataSnapshot));
      } else {
        return Maybe.empty();
      }
    });
  }

  public <T> Maybe<T> get(final Class<T> valueType) {
    return get(dataSnapshot -> dataSnapshot.getValue(valueType));
  }

  public Flowable<DataSnapshot> onValueEvent() {
    return Flowable.create(emitter -> {
      final ValueEventListener listener = new ValueEventListener() {
        @Override public void onDataChange(final DataSnapshot dataSnapshot) {
          emitter.onNext(dataSnapshot);
        }

        @Override public void onCancelled(final DatabaseError databaseError) {
          emitter.onError(databaseError.toException());
        }
      };

      query.addValueEventListener(listener);

      emitter.setCancellable(() -> query.removeEventListener(listener));
    }, BackpressureStrategy.BUFFER);
  }

  public Flowable<FirebaseEvent> onChildEvent() {
    return Flowable.create(emitter -> {
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

      emitter.setCancellable(() -> query.removeEventListener(listener));
    }, BackpressureStrategy.BUFFER);
  }

  public Flowable<FirebaseEvent> onChildAdded() {
    return onChildEvent().filter(eventFilter(FirebaseEvent.Type.CHILD_ADDED));
  }

  public Flowable<FirebaseEvent> onChildChanged() {
    return onChildEvent().filter(eventFilter(FirebaseEvent.Type.CHILD_CHANGED));
  }

  public Flowable<FirebaseEvent> onChildRemoved() {
    return onChildEvent().filter(eventFilter(FirebaseEvent.Type.CHILD_REMOVED));
  }

  public Flowable<FirebaseEvent> onChildMoved() {
    return onChildEvent().filter(eventFilter(FirebaseEvent.Type.CHILD_MOVED));
  }

  public static Predicate<FirebaseEvent> eventFilter(final FirebaseEvent.Type type) {
    return firebaseEvent -> firebaseEvent.type().equals(type);
  }
}

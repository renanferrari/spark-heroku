package rxfirebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.Transaction;
import com.google.firebase.tasks.Task;
import java.util.Map;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rxfirebase.results.TaskResult;
import rxfirebase.results.TransactionResult;

public class RxDatabaseReference extends RxQuery {
  private final DatabaseReference databaseReference;

  RxDatabaseReference(final DatabaseReference databaseReference) {
    super(databaseReference);
    this.databaseReference = databaseReference;
  }

  public RxDatabaseReference child(final String path) {
    return new RxDatabaseReference(databaseReference.child(path));
  }

  public RxDatabaseReference push() {
    return new RxDatabaseReference(databaseReference.push());
  }

  public <T> Observable<TaskResult<T>> set(final T value) {
    return observeTask(() -> databaseReference.setValue(value), value);
  }

  public <T> Observable<TaskResult<T>> set(final Func1<RxDatabaseReference, T> valueFactory) {
    return set(valueFactory.call(this));
  }

  public <T> Observable<TaskResult<T>> set(final Func1<RxDatabaseReference, T> valueFactory,
      final Func1<T, Object> valueMapper) {
    final T value = valueFactory.call(this);
    final Object firebaseValue = valueMapper.call(value);
    return set(rxRef -> firebaseValue).map(firebaseValueResult -> {
      if (firebaseValueResult.isSuccessful()) {
        return TaskResult.create(value);
      } else {
        return TaskResult.create(firebaseValueResult.error());
      }
    });
  }

  public Observable<TaskResult<Void>> remove() {
    return observeTask(databaseReference::removeValue);
  }

  public Observable<TaskResult<Map<String, Object>>> updateChildren(
      final Map<String, Object> updates) {
    return observeTask(() -> databaseReference.updateChildren(updates), updates);
  }

  public Observable<TransactionResult> runTransaction(final Action1<MutableData> onTransaction) {
    return runTransaction(mutableData -> {
      onTransaction.call(mutableData);
      return true;
    });
  }

  public Observable<TransactionResult> runTransaction(
      final Func1<MutableData, Boolean> onTransaction) {
    return Observable.fromEmitter(emitter -> {
      final Transaction.Handler handler = new Transaction.Handler() {
        @Override public Transaction.Result doTransaction(MutableData mutableData) {
          try {
            if (onTransaction.call(mutableData)) {
              return Transaction.success(mutableData);
            } else {
              return Transaction.abort();
            }
          } catch (Exception e) {
            return Transaction.abort();
          }
        }

        @Override public void onComplete(DatabaseError databaseError, boolean committed,
            DataSnapshot dataSnapshot) {
          if (databaseError == null || committed) {
            emitter.onNext(TransactionResult.create(dataSnapshot, committed));
            emitter.onCompleted();
          } else {
            emitter.onNext(TransactionResult.create(databaseError, committed));
            emitter.onCompleted();
          }
        }
      };

      databaseReference.runTransaction(handler);
    }, Emitter.BackpressureMode.BUFFER);
  }

  public Observable<TaskResult<Void>> onDisconnect(
      final Func1<OnDisconnect, Task<Void>> onDisconnectTaskFactory) {
    return observeTask(() -> onDisconnectTaskFactory.call(databaseReference.onDisconnect()));
  }

  private static <T> Observable<TaskResult<T>> observeTask(final Func0<Task<?>> taskFactory,
      final T returnValue) {
    return Observable.fromEmitter(
        emitter -> taskFactory.call().addOnCompleteListener(completedTask -> {
          if (completedTask.isSuccessful()) {
            emitter.onNext(TaskResult.create(returnValue));
            emitter.onCompleted();
          } else {
            emitter.onNext(TaskResult.create(completedTask.getException()));
            emitter.onCompleted();
          }
        }), Emitter.BackpressureMode.BUFFER);
  }

  private static <T> Observable<TaskResult<T>> observeTask(final Func0<Task<T>> taskFactory) {
    return Observable.fromEmitter(
        emitter -> taskFactory.call().addOnCompleteListener(completedTask -> {
          if (completedTask.isSuccessful()) {
            emitter.onNext(TaskResult.create(completedTask.getResult()));
            emitter.onCompleted();
          } else {
            emitter.onNext(TaskResult.create(completedTask.getException()));
            emitter.onCompleted();
          }
        }), Emitter.BackpressureMode.BUFFER);
  }
}
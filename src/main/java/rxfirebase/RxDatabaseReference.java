package rxfirebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.Transaction;
import com.google.firebase.tasks.Task;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
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

  public <T> Completable set(final T value) {
    return RxFirebaseTask.observeVoidTask(() -> databaseReference.setValue(value));
  }

  public <T> Completable set(final Function<RxDatabaseReference, T> valueFactory) {
    return set(valueFactory.apply(this));
  }

  public <T> Completable set(final Function<RxDatabaseReference, T> valueFactory,
      final Function<T, Object> valueMapper) {
    return set(rxRef -> valueMapper.apply(valueFactory.apply(this)));
  }

  public Completable remove() {
    return RxFirebaseTask.observeVoidTask(databaseReference::removeValue);
  }

  public Completable updateChildren(final Map<String, Object> updates) {
    return RxFirebaseTask.observeVoidTask(() -> databaseReference.updateChildren(updates));
  }

  public Single<TransactionResult> runTransaction(final Consumer<MutableData> onTransaction) {
    return runTransaction(mutableData -> {
      onTransaction.accept(mutableData);
      return true;
    });
  }

  public Single<TransactionResult> runTransaction(
      final Function<MutableData, Boolean> onTransaction) {
    return Single.create(emitter -> {
      final Transaction.Handler handler = new Transaction.Handler() {
        @Override public Transaction.Result doTransaction(MutableData mutableData) {
          try {
            if (onTransaction.apply(mutableData)) {
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
          emitter.onSuccess(new TransactionResult(dataSnapshot, databaseError, committed));
        }
      };

      databaseReference.runTransaction(handler);
    });
  }

  public Completable onDisconnect(
      final Function<OnDisconnect, Task<Void>> onDisconnectTaskFactory) {
    return RxFirebaseTask.observeVoidTask(
        () -> onDisconnectTaskFactory.apply(databaseReference.onDisconnect()));
  }
}
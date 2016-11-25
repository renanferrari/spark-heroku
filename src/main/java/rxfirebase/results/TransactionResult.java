package rxfirebase.results;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class TransactionResult extends FirebaseResult {

  private final boolean committed;

  public TransactionResult(DataSnapshot dataSnapshot, DatabaseError databaseError,
      boolean committed) {
    super(dataSnapshot, databaseError);
    this.committed = committed;
  }

  public static TransactionResult create(final DataSnapshot dataSnapshot, final boolean committed) {
    return new TransactionResult(dataSnapshot, null, committed);
  }

  public static TransactionResult create(final DatabaseError databaseError,
      final boolean committed) {
    return new TransactionResult(null, databaseError, committed);
  }

  public boolean committed() {
    return committed;
  }

  @Override public String toString() {
    return "TransactionResult{"
        + "dataSnapshot="
        + dataSnapshot()
        + ", databaseError="
        + databaseError()
        + ", committed="
        + committed
        + '}';
  }
}
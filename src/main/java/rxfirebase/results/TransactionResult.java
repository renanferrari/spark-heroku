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
package rxfirebase.results;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class FirebaseResult {
  private final DataSnapshot dataSnapshot;
  private final DatabaseError databaseError;

  protected FirebaseResult(final DataSnapshot dataSnapshot, final DatabaseError databaseError) {
    this.dataSnapshot = dataSnapshot;
    this.databaseError = databaseError;
  }

  public static FirebaseResult create(final DataSnapshot dataSnapshot) {
    return new FirebaseResult(dataSnapshot, null);
  }

  public static FirebaseResult create(final DatabaseError databaseError) {
    return new FirebaseResult(null, databaseError);
  }

  public DataSnapshot dataSnapshot() {
    return dataSnapshot;
  }

  public DatabaseError databaseError() {
    return databaseError;
  }

  public boolean isSuccessful() {
    return databaseError == null;
  }
}

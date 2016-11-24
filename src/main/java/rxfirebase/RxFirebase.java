package rxfirebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RxFirebase {

  public static RxDatabaseReference of(final DatabaseReference databaseReference) {
    return new RxDatabaseReference(databaseReference);
  }

  public static RxQuery of(final Query query) {
    return new RxQuery(query);
  }
}
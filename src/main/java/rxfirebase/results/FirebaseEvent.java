package rxfirebase.results;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class FirebaseEvent extends FirebaseResult {
  private final Type type;
  private final String previousChildName;

  private FirebaseEvent(final Type type, final DataSnapshot dataSnapshot,
      final String previousChildName, final DatabaseError error) {
    super(dataSnapshot, error);
    this.type = type;
    this.previousChildName = previousChildName;
  }

  public static FirebaseEvent create(final Type type, final DataSnapshot dataSnapshot,
      final String previousChildName) {
    return new FirebaseEvent(type, dataSnapshot, previousChildName, null);
  }

  public static FirebaseEvent create(final Type type, final DataSnapshot dataSnapshot) {
    return new FirebaseEvent(type, dataSnapshot, null, null);
  }

  public static FirebaseEvent create(final DataSnapshot dataSnapshot) {
    return new FirebaseEvent(Type.VALUE, dataSnapshot, null, null);
  }

  public static FirebaseEvent create(final DatabaseError databaseError) {
    return new FirebaseEvent(Type.DATABASE_ERROR, null, null, databaseError);
  }

  public Type type() {
    return type;
  }

  public String previousChildName() {
    return previousChildName;
  }

  public enum Type {
    VALUE, CHILD_ADDED, CHILD_CHANGED, CHILD_REMOVED, CHILD_MOVED, DATABASE_ERROR
  }
}

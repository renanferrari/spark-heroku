package rxfirebase.results;

public class TaskResult<T> {
  private final T value;
  private final Throwable error;

  private TaskResult(final T value, final Throwable error) {
    this.value = value;
    this.error = error;
  }

  public static <T> TaskResult<T> create(final T value) {
    return new TaskResult<>(value, null);
  }

  public static <T> TaskResult<T> create(final Throwable error) {
    return new TaskResult<>(null, error);
  }

  public T result() {
    return value;
  }

  public Throwable error() {
    return error;
  }

  public boolean isSuccessful() {
    return error == null;
  }
}


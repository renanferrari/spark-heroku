package rxfirebase;

import com.google.firebase.tasks.Task;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import java.util.concurrent.Callable;

public class RxFirebaseTask {

  static <T> Maybe<T> observeTask(final Callable<Task<T>> taskFactory) {
    return Maybe.create(emitter -> taskFactory.call().addOnCompleteListener(completedTask -> {
      if (completedTask.isSuccessful()) {
        final T result = completedTask.getResult();
        if (result != null) {
          emitter.onSuccess(result);
        }
        emitter.onComplete();
      } else {
        emitter.onError(completedTask.getException());
        emitter.onComplete();
      }
    }));
  }

  static Completable observeVoidTask(final Callable<Task<Void>> taskFactory) {
    return Completable.create(emitter -> taskFactory.call().addOnCompleteListener(completedTask -> {
      if (completedTask.isSuccessful()) {
        emitter.onComplete();
      } else {
        emitter.onError(completedTask.getException());
        emitter.onComplete();
      }
    }));
  }
}

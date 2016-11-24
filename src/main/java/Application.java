import security.AES;

import static spark.Spark.get;
import static spark.Spark.port;

public class Application {

  public static void main(final String[] args) {
    port(getHerokuAssignedPort());

    get("/hello", (req, res) -> "Hello World!");

    get("*", (request, response) -> request.pathInfo());

    final String message =
        "{ \"branch\": \"55555\", \"account\":\"666666\", \"password\": \"88888888\" }";

    for (int i = 0; i < 10; i++) {
      final String encrypt = AES.encrypt("Zid2BgrqDVboMXFVQtFJxUACllo2", message);

      final String decrypt = AES.decrypt("Zid2BgrqDVboMXFVQtFJxUACllo2", encrypt);

      System.out.println("Result: " + decrypt);
    }
  }

  private static int getHerokuAssignedPort() {
    final ProcessBuilder processBuilder = new ProcessBuilder();

    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }

    return 4567; // default Spark port
  }
}

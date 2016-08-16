import static spark.Spark.get;
import static spark.Spark.port;

public class Application {

  public static void main(String[] args) {
    port(getHerokuAssignedPort());

    get("/hello", (req, res) -> "Hello World!");

    get("*", (request, response) -> {
      response.status(404);
      return "404";
    });
  }

  private static int getHerokuAssignedPort() {
    final ProcessBuilder processBuilder = new ProcessBuilder();

    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }

    return 4567; // default Spark port
  }
}

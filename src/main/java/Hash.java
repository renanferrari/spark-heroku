import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class Hash {

  public static String md5(final String text) {
    return Hashing.md5().hashString(text, StandardCharsets.UTF_8).toString();
  }

  public static String sha1(final String text) {
    return Hashing.sha1().hashString(text, StandardCharsets.UTF_8).toString();
  }

  public static String sha256(final String text) {
    return Hashing.sha256().hashString(text, StandardCharsets.UTF_8).toString();
  }

  public static String sha512(final String text) {
    return Hashing.sha512().hashString(text, StandardCharsets.UTF_8).toString();
  }
}


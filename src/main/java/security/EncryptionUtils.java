package security;

import java.security.SecureRandom;
import org.bouncycastle.util.encoders.Hex;

public class EncryptionUtils {

  private static final SecureRandom RANDOM = new SecureRandom();

  /**
   * Generates a random byte array.
   *
   * Observation:
   * - SecureRandom should not be seeded.
   *
   * References:
   * http://stackoverflow.com/a/1905405/518179
   * http://nelenkov.blogspot.com.br/2012/04/using-password-based-encryption-on.html
   * http://android-developers.blogspot.com.br/2016/06/security-crypto-provider-deprecated-in.html
   */
  public static byte[] generateRandomBytes(final int length) {
    try {
      final byte[] randomByteArray = new byte[length];
      RANDOM.nextBytes(randomByteArray);
      return randomByteArray;
    } catch (Exception e) {
      throw new RuntimeException("Error while generating random byte array: " + e.getMessage(), e);
    }
  }

  public static String encode(final byte[] bytes) {
    return Hex.toHexString(bytes);
  }

  public static byte[] decode(final String string) {
    return Hex.decode(string);
  }
}

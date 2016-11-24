package security;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

public class KeyParameterFactory {

  /**
   * The Key size in bits.
   * 128, 192 or 256 bits.
   */
  private static final int KEY_LENGTH = 256;

  /**
   * The Key salt size in bytes.
   * Same size as the key.
   */
  static final int SALT_LENGTH = KEY_LENGTH / 8;

  /**
   * The Key iterations count.
   *
   * References:
   * http://security.stackexchange.com/a/3993/131476
   */
  private static final int KEY_ITERATIONS = 65000;
  private static final Digest SHA_DIGEST = new SHA256Digest();

  /**
   * Derives a key from a password and salt
   *
   * References:
   * http://stackoverflow.com/a/22621191/518179
   */
  public static KeyParameter create(final String password, final byte[] salt) {
    final PBEParametersGenerator generator = new PKCS5S2ParametersGenerator(SHA_DIGEST);

    final char[] passwordChars = password.toCharArray();
    final byte[] passwordBytes = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(passwordChars);

    generator.init(passwordBytes, salt, KEY_ITERATIONS);

    return (KeyParameter) generator.generateDerivedParameters(KEY_LENGTH);
  }
}

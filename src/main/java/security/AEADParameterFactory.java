package security;

import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Strings;

public class AEADParameterFactory {

  /**
   * The Nonce/IV size in bytes.
   * 12 bytes is the GCM default.
   *
   * References:
   * http://crypto.stackexchange.com/a/5818/41123
   * http://stackoverflow.com/a/31863209/518179
   */
  public static final int NONCE_LENGTH = 12;

  /**
   * The MAC/Authentication Tag size in bits.
   * From 64 to 128 bits, with 8 bit increments.
   *
   * References:
   * http://stackoverflow.com/a/31863209/518179
   */
  private static final int TAG_LENGTH = 128;

  /**
   * Observations:
   * - IV is the same as nonce
   * - MAC (Message Authentication Code) is the same as authentication tag
   * - Associated text can be null
   *
   * References:
   * http://crypto.stackexchange.com/a/6716/41123
   */
  public static AEADParameters create(final KeyParameter keyParameter, final byte[] nonce,
      final String associatedData) {
    return new AEADParameters(keyParameter, TAG_LENGTH, nonce,
        associatedData != null ? Strings.toUTF8ByteArray(associatedData) : null);
  }

  public static AEADParameters create(final KeyParameter keyParameter, final byte[] nonce) {
    return create(keyParameter, nonce, null);
  }
}

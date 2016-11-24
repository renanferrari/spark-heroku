package security;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;

public class Encryptor {

  /**
   * Encrypts a byte array and returns the encrypted message.
   */
  public static byte[] encrypt(final AEADBlockCipher cipher, final AEADParameters aeadParameters,
      final byte[] message) throws InvalidCipherTextException {
    cipher.init(true, aeadParameters);
    final int outputSize = cipher.getOutputSize(message.length);
    final byte[] encrypted = new byte[outputSize];
    final int offOut = cipher.processBytes(message, 0, message.length, encrypted, 0);
    cipher.doFinal(encrypted, offOut);
    return encrypted;
  }

  /**
   * Decrypts a byte array and returns the decrypted message.
   */
  public static byte[] decrypt(final AEADBlockCipher cipher, final AEADParameters aeadParameters,
      final byte[] message) throws InvalidCipherTextException {
    cipher.init(false, aeadParameters);
    final int outputSize = cipher.getOutputSize(message.length);
    final byte[] decrypted = new byte[outputSize];
    final int offOut = cipher.processBytes(message, 0, message.length, decrypted, 0);
    cipher.doFinal(decrypted, offOut);
    return decrypted;
  }
}

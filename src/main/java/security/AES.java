package security;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Strings;

public class AES {

  public static String encrypt(final String password, final String message) {
    try {
      final byte[] decrypted = Strings.toUTF8ByteArray(message);

      final byte[] salt = EncryptedInfo.generateSalt();
      final byte[] nonce = EncryptedInfo.generateNonce();

      final GCMBlockCipher gcmBlockCipher = buildGCMBlockCipher();
      final AEADParameters aeadParameters = buildAEADParameters(password, salt, nonce);

      final byte[] encrypted = Encryptor.encrypt(gcmBlockCipher, aeadParameters, decrypted);

      final EncryptedInfo encryptedInfo = EncryptedInfo.create(salt, nonce, encrypted);

      final byte[] concatenated = encryptedInfo.concatenate();

      return EncryptionUtils.encode(concatenated);
    } catch (InvalidCipherTextException e) {
      throw new RuntimeException("Error while encrypting: " + e.getMessage(), e);
    }
  }

  public static String decrypt(final String password, final String message) {
    try {
      final byte[] concatenated = EncryptionUtils.decode(message);

      final EncryptedInfo encryptedInfo = EncryptedInfo.extract(concatenated);

      final byte[] salt = encryptedInfo.salt();
      final byte[] nonce = encryptedInfo.nonce();
      final byte[] encrypted = encryptedInfo.encrypted();

      final GCMBlockCipher gcmBlockCipher = buildGCMBlockCipher();
      final AEADParameters aeadParameters = buildAEADParameters(password, salt, nonce);

      final byte[] decrypted = Encryptor.decrypt(gcmBlockCipher, aeadParameters, encrypted);

      return Strings.fromUTF8ByteArray(decrypted);
    } catch (InvalidCipherTextException e) {
      throw new RuntimeException("Error while decrypting: " + e.getMessage(), e);
    }
  }

  private static GCMBlockCipher buildGCMBlockCipher() {
    return GCMBlockCipherFactory.create();
  }

  private static AEADParameters buildAEADParameters(final String password, final byte[] salt,
      final byte[] nonce) {
    final KeyParameter keyParameter = KeyParameterFactory.create(password, salt);
    return AEADParameterFactory.create(keyParameter, nonce);
  }
}
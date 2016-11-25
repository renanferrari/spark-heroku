import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Strings;
import org.junit.Test;
import security.AEADParameterFactory;
import security.AES;
import security.EncryptedInfo;
import security.Encryptor;
import security.GCMBlockCipherFactory;
import security.KeyParameterFactory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Based on:
 * http://unafbapune.blogspot.com.br/2012/06/aesgcm-with-associated-data.html
 */
public class AESTest {
  private static final String MESSAGE = "Some message";
  private static final String PASSWORD = "Some password";
  private static final String ASSOCIATED_DATA = "Some associated data";

  private static final byte[] SALT = EncryptedInfo.generateSalt();
  private static final byte[] NONCE = EncryptedInfo.generateNonce();

  @Test public void testEncryptDecrypt() throws InvalidCipherTextException {
    final String encrypted = AES.encrypt(PASSWORD, MESSAGE);
    final String decrypted = AES.decrypt(PASSWORD, encrypted);

    assertTrue(MESSAGE.equals(decrypted));
  }

  @Test public void testWithAssociatedData() throws InvalidCipherTextException {
    final GCMBlockCipher gcmBlockCipher = GCMBlockCipherFactory.create();
    final KeyParameter keyParameter = KeyParameterFactory.create(PASSWORD, SALT);
    final AEADParameters aeadParameters =
        AEADParameterFactory.create(keyParameter, NONCE, ASSOCIATED_DATA);

    final byte[] encrypted =
        Encryptor.encrypt(gcmBlockCipher, aeadParameters, Strings.toUTF8ByteArray(MESSAGE));
    final byte[] decrypted = Encryptor.decrypt(gcmBlockCipher, aeadParameters, encrypted);

    assertTrue(MESSAGE.equals(Strings.fromUTF8ByteArray(decrypted)));
  }

  @Test(expected = InvalidCipherTextException.class) public void testWithInvalidKey()
      throws InvalidCipherTextException {
    try {
      final GCMBlockCipher gcmBlockCipher = GCMBlockCipherFactory.create();
      final KeyParameter keyParameter = KeyParameterFactory.create(PASSWORD, SALT);
      final AEADParameters aeadParameters =
          AEADParameterFactory.create(keyParameter, NONCE, ASSOCIATED_DATA);

      final byte[] encrypted =
          Encryptor.encrypt(gcmBlockCipher, aeadParameters, Strings.toUTF8ByteArray(MESSAGE));

      final KeyParameter badKeyParameter = KeyParameterFactory.create("Bad password!", SALT);
      final AEADParameters aeadParametersWithBadKey =
          AEADParameterFactory.create(badKeyParameter, new byte[AEADParameterFactory.NONCE_LENGTH],
              ASSOCIATED_DATA);

      Encryptor.decrypt(gcmBlockCipher, aeadParametersWithBadKey, encrypted);
      fail();
    } catch (InvalidCipherTextException ex) {
      assertTrue(ex.getMessage().contains("mac check in GCM failed"));
      throw ex;
    }
  }

  @Test(expected = InvalidCipherTextException.class) public void testWithInvalidNonce()
      throws InvalidCipherTextException {
    try {
      final GCMBlockCipher gcmBlockCipher = GCMBlockCipherFactory.create();
      final KeyParameter keyParameter = KeyParameterFactory.create(PASSWORD, SALT);
      final AEADParameters aeadParameters =
          AEADParameterFactory.create(keyParameter, NONCE, ASSOCIATED_DATA);

      final byte[] encrypted =
          Encryptor.encrypt(gcmBlockCipher, aeadParameters, Strings.toUTF8ByteArray(MESSAGE));

      final AEADParameters aeadParametersWithBadNonce =
          AEADParameterFactory.create(keyParameter, new byte[AEADParameterFactory.NONCE_LENGTH],
              ASSOCIATED_DATA);

      Encryptor.decrypt(gcmBlockCipher, aeadParametersWithBadNonce, encrypted);
      fail();
    } catch (InvalidCipherTextException ex) {
      assertTrue(ex.getMessage().contains("mac check in GCM failed"));
      throw ex;
    }
  }

  @Test(expected = InvalidCipherTextException.class) public void testInvalidAssociatedData()
      throws InvalidCipherTextException {
    try {
      final GCMBlockCipher gcmBlockCipher = GCMBlockCipherFactory.create();
      final KeyParameter keyParameter = KeyParameterFactory.create(PASSWORD, SALT);
      final AEADParameters aeadParameters =
          AEADParameterFactory.create(keyParameter, NONCE, ASSOCIATED_DATA);

      final byte[] encrypted =
          Encryptor.encrypt(gcmBlockCipher, aeadParameters, Strings.toUTF8ByteArray(MESSAGE));

      final AEADParameters aeadParametersWithBadAssociatedData =
          AEADParameterFactory.create(keyParameter, new byte[AEADParameterFactory.NONCE_LENGTH],
              "Bad associated data");

      Encryptor.decrypt(gcmBlockCipher, aeadParametersWithBadAssociatedData, encrypted);
      fail();
    } catch (InvalidCipherTextException ex) {
      assertTrue(ex.getMessage().contains("mac check in GCM failed"));
      throw ex;
    }
  }
}
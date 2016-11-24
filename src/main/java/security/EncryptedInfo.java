package security;

import org.bouncycastle.util.Arrays;

public class EncryptedInfo {

  private final byte[] salt;
  private final byte[] nonce;
  private final byte[] encrypted;

  private EncryptedInfo(final byte[] salt, final byte[] nonce, final byte[] encrypted) {
    this.salt = salt;
    this.nonce = nonce;
    this.encrypted = encrypted;
  }

  public static EncryptedInfo create(final byte[] salt, final byte[] nonce,
      final byte[] encrypted) {
    return new EncryptedInfo(salt, nonce, encrypted);
  }

  public static EncryptedInfo extract(final byte[] concatenated) {
    final int saltLength = KeyParameterFactory.SALT_LENGTH;
    final int nonceLength = AEADParameterFactory.NONCE_LENGTH;
    final int prefixLength = saltLength + nonceLength;
    final int totalLength = concatenated.length;

    final byte[] salt = Arrays.copyOfRange(concatenated, 0, saltLength);
    final byte[] nonce = Arrays.copyOfRange(concatenated, saltLength, prefixLength);
    final byte[] encrypted = Arrays.copyOfRange(concatenated, prefixLength, totalLength);

    return new EncryptedInfo(salt, nonce, encrypted);
  }

  public static byte[] generateSalt() {
    return EncryptionUtils.generateRandomBytes(KeyParameterFactory.SALT_LENGTH);
  }

  public static byte[] generateNonce() {
    return EncryptionUtils.generateRandomBytes(AEADParameterFactory.NONCE_LENGTH);
  }

  public byte[] salt() {
    return salt;
  }

  public byte[] nonce() {
    return nonce;
  }

  public byte[] encrypted() {
    return encrypted;
  }

  public byte[] concatenate() {
    return Arrays.concatenate(salt, nonce, encrypted);
  }
}

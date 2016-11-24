package security;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;

public class GCMBlockCipherFactory {

  private static final BlockCipher AES_ENGINE = new AESEngine();

  public static GCMBlockCipher create() {
    return new GCMBlockCipher(AES_ENGINE);
  }
}

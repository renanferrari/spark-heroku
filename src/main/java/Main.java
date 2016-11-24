import security.AES;

public class Main {

  public static void main(String[] args) {
    final String password = getPassword();
    final String message = getCredentials();

    System.out.println("Password: " + password);
    System.out.println("Message: " + message);

    System.out.println();

    final String encryptedString = AES.encrypt(password, message);

    System.out.println("Encrypted: " + encryptedString);

    final String decryptedString = AES.decrypt(password, encryptedString);

    System.out.println("Decrypted: " + decryptedString);

    System.out.println("Success: " + message.equals(decryptedString));
  }

  private static String getPassword() {
    return "Zid2BgrqDVboMXFVQtFJxUACllo2";
  }

  private static String getCredentials() {
    return "{\"userKey\":\"Zid2BgrqDVboMXFVQtFJxUACllo2\",\"bankId\":\"BB\",\"holder\":\"01\",\"agency\":\"55555\",\"account\":\"666666\",\"password\":\"88888888\"}";
  }
}

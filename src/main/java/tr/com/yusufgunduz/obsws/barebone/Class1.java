package tr.com.yusufgunduz.obsws.barebone;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class Class1 {

  public static void main(String[] args) {
    String destUri = "ws://localhost:4444";
    //        if (args.length > 0) {
    //            destUri = args[0];
    //        }

    WebSocketClient client = new WebSocketClient();
    TestWebSocket webSocket = new TestWebSocket();
    try {
      client.start();
      URI wsUri = new URI(destUri);
      ClientUpgradeRequest request = new ClientUpgradeRequest();
      client.connect(webSocket, wsUri, request);


//      Future<Void> fut;
//      fut = webSocket.getSession().getRemote().sendStringByFuture("");
//      fut.get(2, TimeUnit.SECONDS);
//
//      // wait for closed socket connection.
      webSocket.awaitClose(15, TimeUnit.SECONDS);
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      try {
        client.stop();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  private String generateAuthenticationResponseString(String password, String challenge, String salt) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Failed to perform password authentication with server");
      e.printStackTrace();
      return null;
    }

    String secretString = password + salt;
    byte[] secretHash = digest.digest(secretString.getBytes(StandardCharsets.UTF_8));
    String encodedSecret = Base64.getEncoder().encodeToString(secretHash);

    String authResponseString = encodedSecret + challenge;
    byte[] authResponseHash = digest.digest(authResponseString.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(authResponseHash);
  }

}

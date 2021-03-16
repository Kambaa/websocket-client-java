package tr.com.yusufgunduz.obsws.barebone;

import com.google.gson.Gson;
import net.twasi.obsremotejava.requests.GetVersion.GetVersionRequest;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@WebSocket(maxIdleTime = 360000000)
public class TestWebSocket {

  private final CountDownLatch closeLatch = new CountDownLatch(1);
  private Session session;

  private void getObsVersion() {

    String req = "{    \"request-type\": \"GetVersion\",\n" +
            "            \"message-id\": \"1\"\n" +
            "    }";
    try {
      Future<Void> fut;
      fut = session.getRemote().sendStringByFuture(req);
      fut.get(2, TimeUnit.SECONDS);
    } catch (Throwable t) {
      System.err.println("An error occurred while trying to get a session");
      t.printStackTrace();
    }
  }

  public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
    return this.closeLatch.await(duration, unit);
  }

  @OnWebSocketConnect
  public void onConnect(Session session) {
    System.out.printf("Got connect: %s%n", session);
    this.session = session;
    getObsVersion();
  }

  @OnWebSocketMessage
  public void onMessage(String msg) {
    System.out.printf("Got msg: %s%n", msg);
    if (msg.contains("Thanks")) {
      session.close(StatusCode.NORMAL, "I'm done");
    }
  }

  @OnWebSocketError
  public void onError(Throwable cause) {
    System.out.print("WebSocket Error: ");
    cause.printStackTrace(System.out);
  }

  @OnWebSocketClose
  public void onClose(int statusCode, String reason) {
    System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
    this.session = null;
    this.closeLatch.countDown(); // trigger latch
  }

  public Session getSession() {
    return session;
  }
}

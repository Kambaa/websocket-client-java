package tr.com.yusufgunduz.obsws.barebone;


import net.twasi.obsremotejava.requests.GetVersion.GetVersionRequest;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@WebSocket
public class Class2 {
    private final CountDownLatch closeLatch;

    @SuppressWarnings("unused")
    private Session session;

    public Class2() {
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("Got connect: %s%n", session);
        this.session = session;
        try {
            Future<Void> future;
            future = session.getRemote().sendStringByFuture("Hello");
            future.get(2, TimeUnit.SECONDS); // wait for send to complete.

            future = session.getRemote().sendStringByFuture("Thanks for the conversation.");
            future.get(2, TimeUnit.SECONDS); // wait for send to complete.
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
//    @OnWebSocketConnect
//    public void onConnect(Session session) {
//        this.session = session;
//        try {
//            Future<Void> fut;
//            fut = session.getRemote().sendStringByFuture(new Gson().toJson(new GetVersionRequest(this)));
//            fut.get(2, TimeUnit.SECONDS);
//        } catch (Throwable t) {
//            runOnError("An error occurred while trying to get a session", t);
//        }
//    }

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
}
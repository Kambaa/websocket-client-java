package tr.com.yusufgunduz.obsws;

import net.twasi.obsremotejava.OBSRemoteController;
import net.twasi.obsremotejava.objects.Scene;

public class Test {

    private static void obsSuccessfulPOC() {
//        OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false);
        OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false, "pass");
        if (controller.isFailed()) { // Awaits response from OBS
            // Here you can handle a failed connection request
            System.err.println("Controller is failed.");
        }
        controller.registerConnectionFailedCallback(message -> {
            System.err.println("Failed to connect: " + message);
        });
        controller.connect();
        controller.getScenes(response -> {
            System.out.println("Scenes List Are:\n");
            for (Scene scene : response.getScenes()) {
                System.out.println(scene.getName());
            }
        });
        controller.changeSceneWithTransition("[S] Yayın Başlıyor", null, response -> {
            System.out.println("Switcing to Yayın Başlıyor!");
            response.getMessageId();
        });
        // add extra operations here. scene switching, webcam positin changing, etc...
    }

    public static void main(String[] args) {
        obsSuccessfulPOC();
    }
}

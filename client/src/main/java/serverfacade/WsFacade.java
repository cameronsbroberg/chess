package serverfacade;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WsFacade extends Endpoint {

    Session session;

    public WsFacade(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println("Received a message from websocket: " + message);
//                    Notification notification = new Gson().fromJson(message, Notification.class);
//                    notificationHandler.notify(notification);
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException("500:" + e.getMessage());
        };
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}

package serverfacade;

import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.InGameClient;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WsFacade extends Endpoint {

    Session session;
    private Gson serializer;
    public InGameClient client; //FIXME this is bad

    public WsFacade(String url) throws ResponseException {
        try {
            this.serializer = new Gson();
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = serializer.fromJson(message,ServerMessage.class);
                    switch(serverMessage.getServerMessageType()){
                        case LOAD_GAME -> {
                            client.updateGame(serverMessage.getNewGame());
                        }
                        case ERROR -> {
                            System.out.println("Error!"); //FIXME: Shouldn't be printing to system in this class
                        }
                        case NOTIFICATION -> {
                            System.out.println("Received a message from server: " + message);
                        }
                    }
//                    Notification notification = new Gson().fromJson(message, Notification.class);
//                    notificationHandler.notify(notification);
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException("500:" + e.getMessage());
        };
    }

    public void send(UserGameCommand command) throws IOException {
        String message = serializer.toJson(command);
        session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}

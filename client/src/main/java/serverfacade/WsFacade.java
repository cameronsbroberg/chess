package serverfacade;

import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.InGameClient;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WsFacade extends Endpoint {

    Session session;
    private Gson serializer;
    private NotificationHandler notificationHandler;
    private InGameClient client;

    public WsFacade(String url, InGameClient client, NotificationHandler notifier) throws ResponseException {
        try {
            this.serializer = new Gson();
            this.notificationHandler = notifier;
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = serializer.fromJson(message,ServerMessage.class);
                    switch(serverMessage.getServerMessageType()){
                        case ServerMessage.ServerMessageType.LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = serializer.fromJson(message, LoadGameMessage.class);
                            notificationHandler.handleNotification(loadGameMessage);
                        }
                        case ServerMessage.ServerMessageType.ERROR -> {
                            ErrorMessage errorMessage = serializer.fromJson(message, ErrorMessage.class);
                            notificationHandler.handleNotification(errorMessage);
                        }
                        case ServerMessage.ServerMessageType.NOTIFICATION -> {
                            Notification notification = serializer.fromJson(message, Notification.class);
                            notificationHandler.handleNotification(notification);
                        }
                        default -> System.out.println("I'm going insane " + serverMessage.getServerMessageType());
                    }}
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

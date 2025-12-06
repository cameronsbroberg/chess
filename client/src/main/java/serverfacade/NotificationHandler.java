package serverfacade;

import ui.InGameClient;
import websocket.messages.*;

public class NotificationHandler {
    private final InGameClient client;

    public NotificationHandler(InGameClient client) {
        this.client = client;
    }

    public void handleNotification(ServerMessage serverMessage){
        switch(serverMessage){
            case LoadGameMessage loadGameMessage -> client.updateGame(((LoadGameMessage) serverMessage).getGame());
            case ErrorMessage errorMessage -> System.out.println("Error: " + ((ErrorMessage) serverMessage).getErrorMessage());
            case Notification notification -> System.out.println(((Notification) serverMessage).getMessage());
            default -> System.out.println("The server has gone mad and sent an impossible message." + serverMessage.getServerMessageType());
        }
    }
}

package serverfacade;

import ui.InGameClient;
import websocket.messages.ServerMessage;

public class NotificationHandler {
    private final InGameClient client;

    public NotificationHandler(InGameClient client) {
        this.client = client;
    }

    public void handleNotification(ServerMessage serverMessage){
        switch(serverMessage.getServerMessageType()){
            case LOAD_GAME -> client.updateGame(serverMessage.getNewGame());
            case ERROR -> System.out.println("Error: " + serverMessage.getMessage());
            case NOTIFICATION -> System.out.println(serverMessage.getMessage());
        }
    }
}

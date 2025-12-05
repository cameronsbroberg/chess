package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand{
    public ConnectCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}

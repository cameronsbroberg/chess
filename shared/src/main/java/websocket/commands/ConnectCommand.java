package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand{
    public ConnectCommand(String authToken, Integer gameID, ChessGame.TeamColor teamColor) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}

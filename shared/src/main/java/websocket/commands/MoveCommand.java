package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MoveCommand extends UserGameCommand{
    private final ChessMove chessMove;
    public MoveCommand(String authToken, Integer gameID, ChessGame.TeamColor teamColor, ChessMove chessMove) {
        super(CommandType.MAKE_MOVE, authToken, gameID, teamColor);
        this.chessMove = chessMove;
    }

    public ChessMove getChessMove() {
        return chessMove;
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveGenerator {
    private final ChessBoard board;
    private final ChessPosition startingPosition;
    private Collection<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor color;

    public KnightMoveGenerator(ChessBoard board, ChessPosition startingPosition) {
        this.board = board;
        this.startingPosition = startingPosition;
        this.color = board.getPiece(startingPosition).getTeamColor();
    }
    private void checkValidity(chess.ChessPosition currentPosition){
        if(board.getPiece(currentPosition) == null){
            validMoves.add(new ChessMove(startingPosition,currentPosition,null));
        }
        else if(board.getPiece(currentPosition).getTeamColor() != color){
            validMoves.add(new ChessMove(startingPosition,currentPosition,null));
        }
    }
    public Collection<ChessMove> getMoves(){
        int row = startingPosition.getRow();
        int col = startingPosition.getColumn();

        if (row < 8 && col > 2){
            checkValidity(new ChessPosition(row + 1,col - 2));
        }
        if (row < 7 && col > 1){
            checkValidity(new ChessPosition(row + 2, col - 1));
        }
        if (row < 7 && col < 8){
            checkValidity(new ChessPosition(row + 2, col + 1));
        }
        if (row < 8 && col < 7){
            checkValidity(new ChessPosition(row + 1, col + 2));
        }
        if (row > 2 && col < 8){
            checkValidity(new ChessPosition(row - 2, col + 1));
        }
        if (row > 1 && col < 7){
            checkValidity(new ChessPosition(row - 1, col + 2));
        }
        if (row > 2 && col > 1){
            checkValidity(new ChessPosition(row - 2, col - 1));
        }
        if (row > 1 && col > 2){
            checkValidity(new ChessPosition(row - 1, col - 2));
        }
        return validMoves;
    }

}

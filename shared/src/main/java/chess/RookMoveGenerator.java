package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveGenerator {
    private final ChessBoard board;
    private final ChessPosition startingPosition;
    private Collection<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor color;

    public RookMoveGenerator(ChessBoard board, ChessPosition startingPosition) {
        this.board = board;
        this.startingPosition = startingPosition;
        this.validMoves = validMoves;
        this.color = board.getPiece(startingPosition).getTeamColor();
    }

    private boolean checkValidityAndContinue(chess.ChessPosition currentPosition){
        if(board.getPiece(currentPosition) == null){
            validMoves.add(new ChessMove(startingPosition,currentPosition,null));
            return true;
        }
        else if(board.getPiece(currentPosition).getTeamColor() == color){
            return false;
        }
        else if(board.getPiece(currentPosition).getTeamColor() != color) {
            validMoves.add(new ChessMove(startingPosition, currentPosition, null));
            return false;
        }
        return false; //theoretically unreachable
    }

    public Collection<ChessMove> getMoves(){
        int row = startingPosition.getRow();
        int col = startingPosition.getColumn();

        while(row > 1) {//traverse down
            row--;
            ChessPosition currentPosition = new ChessPosition(row, col);
            if (!checkValidityAndContinue(currentPosition)) {
                break;
            }
        }
        row = startingPosition.getRow();
        while(row <8 ) {//awww it's a heart! traverse up
            row++;
            ChessPosition currentPosition = new ChessPosition(row, col);
            if (!checkValidityAndContinue(currentPosition)) {
                break;
            }
        }
        row = startingPosition.getRow();
        while(col > 1) {//traverse left
            col--;
            ChessPosition currentPosition = new ChessPosition(row, col);
            if (!checkValidityAndContinue(currentPosition)) {
                break;
            }
        }
        col = startingPosition.getColumn();
        while(col < 8) {//traverse right
            col++;
            ChessPosition currentPosition = new ChessPosition(row, col);
            if (!checkValidityAndContinue(currentPosition)) {
                break;
            }
        }
        return validMoves;
    }
}

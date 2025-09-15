package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveGenerator {
    private final ChessBoard board;
    private final ChessPosition startingPosition;
    private Collection<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor color;
    private final int row;
    private final int col;

    public PawnMoveGenerator(ChessBoard board, ChessPosition startingPosition) {
        this.board = board;
        this.startingPosition = startingPosition;
        this.color = board.getPiece(startingPosition).getTeamColor();
        this.row = startingPosition.getRow();
        this.col = startingPosition.getColumn();
    }
    public Collection<ChessMove> getMoves(){
        checkAdvances();
        checkCaptures();
        return validMoves;
    }
    private void checkPromotions(int target_row,int target_col){
        if(target_row == 8 || target_row == 1){//there should never be a case of a white pawn moving to row 1 or a black pawn moving to row 8, so this should be okay.
            validMoves.add(new ChessMove(startingPosition,new ChessPosition(target_row,target_col), ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(startingPosition,new ChessPosition(target_row,target_col), ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(startingPosition,new ChessPosition(target_row,target_col), ChessPiece.PieceType.KNIGHT));
            validMoves.add(new ChessMove(startingPosition,new ChessPosition(target_row,target_col), ChessPiece.PieceType.BISHOP));
        }
        else{
            validMoves.add(new ChessMove(startingPosition,new ChessPosition(target_row,target_col),null));
        }
    }
    private void checkAdvances(){
        int target_row;
        if(color == ChessGame.TeamColor.WHITE) {
            target_row = row + 1;
            if (row == 2) {
                if(board.getPiece(new ChessPosition(target_row,col)) == null){
                    if (board.getPiece(new ChessPosition(row + 2, col)) == null) {
                        validMoves.add(new ChessMove(startingPosition, new ChessPosition(row + 2, col), null)); //you shouldn't ever be able to promote from the second row as white
                    }
                }
            }
        }
        else {//BLACK
            target_row = row - 1;
            if (row == 7) {//check for double move
                if(board.getPiece(new ChessPosition(target_row,col)) == null){
                    if (board.getPiece(new ChessPosition(row - 2, col)) == null) {
                        validMoves.add(new ChessMove(startingPosition, new ChessPosition(row - 2, col), null)); //you shouldn't ever be able to promote from the seventh row as black
                    }
                }
            }
        }
        if(board.getPiece(new ChessPosition(target_row, col)) == null){
            checkPromotions(target_row, col);
        }
    }
    private void checkCaptures(){
        int target_row;
        if(color == ChessGame.TeamColor.WHITE){
            target_row = row + 1;
        }
        else{ //BLACK
            target_row = row - 1;
        }
        if(col > 1){
            if(board.getPiece(new ChessPosition(target_row, col - 1)) != null && board.getPiece(new ChessPosition(target_row, col - 1)).getTeamColor() != color){
                checkPromotions(target_row,col - 1);
            }
        }
        if(col < 8){
            if(board.getPiece(new ChessPosition(target_row, col + 1)) != null && board.getPiece(new ChessPosition(target_row, col + 1)).getTeamColor() != color){
                checkPromotions(target_row,col + 1);
            }
        }
    }
    //first: check the color. Then have different methods for white and black
    //Then check for moves
    //Then check for captures
    //Then check for promotion
    //Do I do a checkValidity? I think not.
}

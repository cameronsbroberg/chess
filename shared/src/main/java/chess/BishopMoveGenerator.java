package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveGenerator {
    private final ChessBoard board;
    private final ChessPosition startingPosition;
    private int row;
    private int col;
    private Collection<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor color;

    public BishopMoveGenerator(ChessBoard board, ChessPosition startingPosition) {
        this.board = board;
        this.startingPosition = startingPosition;
        this.color = board.getPiece(startingPosition).getTeamColor();
        this.row = startingPosition.getRow();
        this.col = startingPosition.getColumn();
    }

    public Collection<ChessMove> getMoves(){
        while(row > 1 & col > 1){ //Will traverse towards the bottom left. FIXME check the edge cases
            row--;
            col--;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(startingPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(startingPosition,currentPosition,null));
                break;
            }
        }
        row = startingPosition.getRow();
        col = startingPosition.getColumn();
        while(row < 8 & col < 8){ //Will traverse towards the upper right.
            row++;
            col++;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(startingPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(startingPosition,currentPosition,null));
                break;
            }
        }
        row = startingPosition.getRow();
        col = startingPosition.getColumn();
        while(row > 1 & col < 8){ //will traverse towards lower right
            row--;
            col++;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(startingPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(startingPosition,currentPosition,null));
                break;
            }
        }
        row = startingPosition.getRow();
        col = startingPosition.getColumn();
        while(row < 8 & col > 1){ // will traverse towards upper left
            row++;
            col--;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(startingPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(startingPosition,currentPosition,null));
                break;
            }
        }
        return validMoves;
    }
}

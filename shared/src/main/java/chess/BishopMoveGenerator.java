package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveGenerator {
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while(row > 1 & col > 1){ //Will traverse towards the bottom left. FIXME check the edge cases
            row--;
            col--;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while(row < 8 & col < 8){ //Will traverse towards the upper right.
            row++;
            col++;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while(row > 1 & col < 8){ //will traverse towards lower right
            row--;
            col++;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while(row < 8 & col > 1){ // will traverse towards upper left
            row++;
            col--;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
                break;
            }
        }
        return validMoves;
    }
}

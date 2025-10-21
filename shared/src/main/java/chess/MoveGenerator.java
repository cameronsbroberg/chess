package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MoveGenerator {
    protected ChessBoard board;
    protected ChessPiece piece;
    protected ChessPosition startingPosition;
    protected Collection<ChessMove> validMoves;

    public MoveGenerator(ChessBoard board, ChessPiece piece, ChessPosition startingPosition) {
        this.board = board;
        this.piece = piece;
        this.startingPosition = startingPosition;
        validMoves = new ArrayList<>();
    }
    public Collection<ChessMove> getMoves(){
        return validMoves;
    }
    protected void slide(int[][] directions){
        for(int[] dir : directions){
            int row = startingPosition.getRow();
            int col = startingPosition.getColumn();
            while(true){
                row = row + dir[0];
                col = col + dir[1];
                if(row > 8 || row < 1 || col > 8 || col < 1){
                    break;
                }
                ChessPosition targetPosition = new ChessPosition(row,col);
                if(board.getPiece(targetPosition) == null){
                    validMoves.add(new ChessMove(startingPosition,targetPosition,null));
                    continue;
                }
                if(board.getPiece(targetPosition).getTeamColor() != piece.getTeamColor()){
                    validMoves.add(new ChessMove(startingPosition,targetPosition,null));
                    break;
                }
                else{
                    break;
                }
            }
        }
    }
    protected void hop(int[][] directions){
        for(int[] dir : directions){
            int row = startingPosition.getRow();
            int col = startingPosition.getColumn();
            int targetRow = row + dir[0];
            int targetCol = col + dir[1];
            if(targetRow > 8 || targetRow < 1 || targetCol > 8 || targetCol < 1){
                continue;
            }
            ChessPosition targetPosition = new ChessPosition(targetRow,targetCol);
            if(board.getPiece(targetPosition) == null){
                validMoves.add(new ChessMove(startingPosition,targetPosition,null));
                continue;
            }
            if(board.getPiece(targetPosition).getTeamColor() != piece.getTeamColor()){
                validMoves.add(new ChessMove(startingPosition,targetPosition,null));
            }
        }
    }
}
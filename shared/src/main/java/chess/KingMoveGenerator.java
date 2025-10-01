
package chess;

import java.util.Collection;

public class KingMoveGenerator extends MoveGenerator{
    public KingMoveGenerator(ChessBoard board, ChessPiece piece, ChessPosition startingPosition) {
        super(board, piece, startingPosition);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        int[][] directions = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
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
        return validMoves;
    }
}

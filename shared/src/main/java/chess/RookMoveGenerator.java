package chess;

import java.util.Collection;

public class RookMoveGenerator extends MoveGenerator{
    public RookMoveGenerator(ChessBoard board, ChessPiece piece, ChessPosition startingPosition) {
        super(board, piece, startingPosition);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
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
        return validMoves;
    }
}
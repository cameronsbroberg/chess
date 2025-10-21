package chess;

import java.util.Collection;

public class KingMoveGenerator extends MoveGenerator{
    public KingMoveGenerator(ChessBoard board, ChessPiece piece, ChessPosition startingPosition) {
        super(board, piece, startingPosition);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        int[][] directions = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
        hop(directions);
        return validMoves;
    }
}

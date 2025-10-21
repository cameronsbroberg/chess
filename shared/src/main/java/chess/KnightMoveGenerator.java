package chess;

import java.util.Collection;

public class KnightMoveGenerator extends MoveGenerator{
    public KnightMoveGenerator(ChessBoard board, ChessPiece piece, ChessPosition startingPosition) {
        super(board, piece, startingPosition);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        int[][] directions = {{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}};
        hop(directions);
        return validMoves;
    }
}
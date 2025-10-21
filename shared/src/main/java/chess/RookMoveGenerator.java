package chess;

import java.util.Collection;

public class RookMoveGenerator extends MoveGenerator{
    public RookMoveGenerator(ChessBoard board, ChessPiece piece, ChessPosition startingPosition) {
        super(board, piece, startingPosition);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
        slide(directions);
        return validMoves;
    }
}
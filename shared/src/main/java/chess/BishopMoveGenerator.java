package chess;

import java.util.Collection;

public class BishopMoveGenerator extends MoveGenerator{
    public BishopMoveGenerator(ChessBoard board, ChessPiece piece, ChessPosition startingPosition) {
        super(board, piece, startingPosition);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        int[][] directions = {{-1,1},{1,1},{1,-1},{-1,-1}};
        slide(directions);
        return validMoves;
    }
}
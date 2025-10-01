package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveGenerator {
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
}
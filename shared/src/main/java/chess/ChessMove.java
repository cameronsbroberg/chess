package chess;

import java.util.Objects;

public class ChessMove {
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessPosition getStartPosition() {
        return startPosition;
    }

    public ChessPosition getEndPosition() {
        return endPosition;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        if(promotionPiece == null && ((ChessMove) o).promotionPiece == null){
            return startPosition.equals(((ChessMove) o).startPosition)
                    && endPosition.equals(((ChessMove) o).endPosition);
        }
        if(promotionPiece == null ^ ((ChessMove) o).promotionPiece == null){
            return false;
        }
        return startPosition.equals(((ChessMove) o).startPosition)
                && endPosition.equals(((ChessMove) o).endPosition)
                && promotionPiece.equals(((ChessMove) o).promotionPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                startPosition.toString() + "," +
                endPosition.toString() + "," +
                promotionPiece +
                '}';
    }
}
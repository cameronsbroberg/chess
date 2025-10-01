package chess;

import java.util.Objects;

public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
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
    public String toString() {
        return String.format("%s%s", startPosition, endPosition);
    }

    @Override
    public boolean equals(Object that) {
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        if (promotionPiece == null ^ ((ChessMove) that).promotionPiece == null){
            return false;
        }
        if (promotionPiece != null && ((ChessMove) that).promotionPiece != null){
            if (promotionPiece != ((ChessMove) that).promotionPiece){
                return false;
            }
        }
        return startPosition.equals(((ChessMove) that).startPosition) && endPosition.equals(((ChessMove) that).endPosition);
     }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}

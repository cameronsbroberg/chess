package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
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

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
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
        // This code was passing by reference instead of value! The source of all my problems!
        // return ((ChessMove) that).startPosition == startPosition && ((ChessMove) that).endPosition == endPosition && ((ChessMove) that).promotionPiece == promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}

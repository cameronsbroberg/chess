package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.BISHOP) {
            //return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            return getBishopMoves(board,myPosition,piece.pieceColor);
        }
        return List.of();
    }
    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while(row > 1 & col > 1){ //Will traverse towards the bottom left. FIXME check the edge cases
            row--;
            col--;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while(row < 8 & col < 8){ //Will traverse towards the upper right.
            row++;
            col++;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while(row > 1 & col < 8){ //will traverse towards lower right
            row--;
            col++;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while(row < 8 & col > 1){ // will traverse towards upper left
            row++;
            col--;
            ChessPosition currentPosition = new ChessPosition(row,col);
            if(board.getPiece(currentPosition) == null){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
            }
            else if(board.getPiece(currentPosition).getTeamColor() == color){
                break;
            }
            else if(board.getPiece(currentPosition).getTeamColor() != color){
                validMoves.add(new ChessMove(myPosition,currentPosition,null));
                break;
            }
        }
        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor.equals(that.pieceColor) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}

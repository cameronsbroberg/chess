package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveGenerator {
    public Collection<ChessMove> generatePieceMoves(ChessBoard board, ChessPosition myPosition){
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.BISHOP){
            return new BishopMoveGenerator().getMoves(board,myPosition);
        }if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.ROOK){
            return new RookMoveGenerator(board, myPosition).getMoves();
        }
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.QUEEN){
            Collection<ChessMove> queenMoves = new ArrayList<>();
            Collection<ChessMove> bishopMoves = new BishopMoveGenerator().getMoves(board,myPosition);
            Collection<ChessMove> rookMoves = new RookMoveGenerator(board,myPosition).getMoves();
            queenMoves.addAll(bishopMoves);
            queenMoves.addAll(rookMoves);
            return queenMoves;
        }
        return null;
    }
}

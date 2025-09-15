package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveGenerator {
    public Collection<ChessMove> generatePieceMoves(ChessBoard board, ChessPosition myPosition){
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.BISHOP){
            return new BishopMoveGenerator(board,myPosition).getMoves();
        }if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.ROOK){
            return new RookMoveGenerator(board, myPosition).getMoves();
        }
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.QUEEN){
            Collection<ChessMove> queenMoves = new ArrayList<>();
            Collection<ChessMove> bishopMoves = new BishopMoveGenerator(board,myPosition).getMoves();
            Collection<ChessMove> rookMoves = new RookMoveGenerator(board,myPosition).getMoves();
            queenMoves.addAll(bishopMoves);
            queenMoves.addAll(rookMoves);
            return queenMoves;
        }
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KNIGHT){
            return new KnightMoveGenerator(board, myPosition).getMoves();
        }
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KING){
            return new KingMoveGenerator(board, myPosition).getMoves();
        }
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.PAWN){
            return new PawnMoveGenerator(board, myPosition).getMoves();
        }

        return null;
    }
}

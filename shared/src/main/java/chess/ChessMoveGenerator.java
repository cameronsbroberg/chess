package chess;

import java.util.Collection;

public class ChessMoveGenerator {
    public Collection<ChessMove> generatePieceMoves(ChessBoard board, ChessPosition myPosition){
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.BISHOP){
            return new BishopMoveGenerator().getMoves(board,myPosition);
        }if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.ROOK){
            return new RookMoveGenerator(board, myPosition).getMoves();
        }
        return null;
    }
}

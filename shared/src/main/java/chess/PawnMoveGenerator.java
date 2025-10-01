package chess;

import java.util.Collection;

public class PawnMoveGenerator extends MoveGenerator{
    public PawnMoveGenerator(ChessBoard board, ChessPiece piece, ChessPosition startingPosition) {
        super(board, piece, startingPosition);
    }
    private void promote(ChessPosition targetPosition){
        if(targetPosition.getRow() == 1 || targetPosition.getRow() == 8){
            ChessPiece.PieceType[] promotions = new ChessPiece.PieceType[]{
                    ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.QUEEN};
            for(ChessPiece.PieceType promotion : promotions){
                validMoves.add(new ChessMove(startingPosition,targetPosition,promotion));
            }
        }
        else{
            validMoves.add(new ChessMove(startingPosition,targetPosition,null));
        }
    }
    @Override
    public Collection<ChessMove> getMoves() {
        int row = startingPosition.getRow();
        int col = startingPosition.getColumn();
        int targRow = row + 1;
        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            targRow = row - 1;
        }
        int[] cols = {-1,1};
        for(int colAdj : cols){
            int targCol = col + colAdj;
            if(targRow > 8 || targRow < 1 || targCol > 8 || targCol < 1){
                continue;
            }
            else{
                ChessPosition targetPosition = new ChessPosition(targRow, targCol);
                if(board.getPiece(targetPosition) == null){
                    continue;
                }
                else if(board.getPiece(targetPosition).getTeamColor() != piece.getTeamColor()){
                    promote(targetPosition);
                }
            }
        }
        //Moves!
        ChessPosition targetPosition = new ChessPosition(targRow, col);
        if(board.getPiece(targetPosition) == null){
            promote(targetPosition);
            if(row == 2 && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                ChessPosition doubleTargetPosition = new ChessPosition(row + 2, col);
                if(board.getPiece(doubleTargetPosition) == null){
                    validMoves.add(new ChessMove(startingPosition,doubleTargetPosition,null));
                }
            }
            if(row == 7 && piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                ChessPosition doubleTargetPosition = new ChessPosition(row - 2, col);
                if (board.getPiece(doubleTargetPosition) == null) {
                    validMoves.add(new ChessMove(startingPosition, doubleTargetPosition, null));
                }
            }
        }
        return validMoves;
    }
}
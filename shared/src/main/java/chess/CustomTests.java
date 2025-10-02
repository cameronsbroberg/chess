package chess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class CustomTests {
    @Test
    public void customTest1() {
        ChessGame game = new ChessGame();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                game.getBoard().addPiece(new ChessPosition(i,j),null);
            }
        }
        game.getBoard().addPiece(new ChessPosition(5,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        game.getBoard().addPiece(new ChessPosition(5,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        game.getBoard().addPiece(new ChessPosition(4,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        game.getBoard().addPiece(new ChessPosition(5,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        assert (game.isInCheck(ChessGame.TeamColor.WHITE)); // expect false
        System.out.print(game.isInCheck(ChessGame.TeamColor.WHITE));
    }
    @Test
    public void testExecuteMove() {
        ChessGame game = new ChessGame();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                game.getBoard().addPiece(new ChessPosition(i,j),null);
            }
        }
        game.getBoard().addPiece(new ChessPosition(5,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        game.getBoard().addPiece(new ChessPosition(5,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        game.getBoard().addPiece(new ChessPosition(5,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        game.getBoard().addPiece(new ChessPosition(5,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        game.executeMove(new ChessMove(new ChessPosition(5,7),new ChessPosition(4,7),null));

        ChessBoard postBoard = new ChessBoard();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                postBoard.addPiece(new ChessPosition(i,j),null);
            }
        }
        postBoard.addPiece(new ChessPosition(5,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        postBoard.addPiece(new ChessPosition(5,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        postBoard.addPiece(new ChessPosition(4,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        postBoard.addPiece(new ChessPosition(5,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        assert(game.getBoard().equals(postBoard));
    }
    public void testUndoMove(){

    }
}


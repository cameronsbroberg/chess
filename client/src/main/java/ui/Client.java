package ui;

import chess.ChessGame;
import serverfacade.ServerFacade;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

public abstract class Client {
    protected ServerFacade serverFacade;
    protected Repl repl;
    abstract String eval(String input);
    abstract String helpString();
    protected String chessBoard(ChessGame.TeamColor teamColor){
        if(teamColor == null){
            teamColor = ChessGame.TeamColor.WHITE; //Show white side if observer
        }
        String boardString = "";
        boardString += horizontalBorder(teamColor);
        int row_start = (teamColor == ChessGame.TeamColor.WHITE ? 8 : 1);
        int row_end = (teamColor == ChessGame.TeamColor.WHITE ? 1 : 8);

        int row_inc = (teamColor == ChessGame.TeamColor.WHITE ? -1 : 1);

        int col_start = (teamColor == ChessGame.TeamColor.WHITE ? 1 : 8);
        int col_end = (teamColor == ChessGame.TeamColor.WHITE ? 8 : 1);

        int col_inc = (teamColor == ChessGame.TeamColor.WHITE ? 1 : -1);

        for(int i = row_start; (row_inc < 0 ? i >= row_end : i <= row_end); i = i + row_inc){ //i is the row
            boardString += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + i + " ";
            for (int j = col_start; (col_inc < 0 ? j >= col_end : j <= col_end); j = j + col_inc) { //j is the column
                String background = ((i + j) % 2 == 0 ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY);
                boardString += background + " " + whichPiece(i,j) + " ";
            }
            boardString += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + " " + i;
            boardString += SET_BG_COLOR_WHITE + "\n";
        }
        boardString += horizontalBorder(teamColor);
        return boardString;
    }
    protected String horizontalBorder(ChessGame.TeamColor teamColor){
        String lineString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "  ";
        if(teamColor == ChessGame.TeamColor.WHITE){
            for(char c = 'A'; c <= 'H'; c++){
                lineString += " " + c + " ";
            }
        }
        else{
            for(char c = 'H'; c >= 'A'; c--){
                lineString += " " + c + " ";
            }
        }
        return lineString + "\n";
    }
    protected String whichPiece(int row, int col) {
        return null;
    }
}

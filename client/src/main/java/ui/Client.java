package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

public abstract class Client {
    protected ServerFacade serverFacade;
    protected Repl repl;
    abstract String eval(String input);
    abstract String helpString();
    protected String chessBoard(ChessPosition highlightPiece, ChessGame.TeamColor teamColor, ChessGame chessGame){
        if(highlightPiece != null){
            if (chessGame.validMoves(highlightPiece) == null){
                return "No piece at selected square";
            }
        }
        Collection<ChessMove> validMoves = (highlightPiece == null ? new ArrayList<>() : chessGame.validMoves(highlightPiece));
        Collection<ChessPosition> highlightSpots = new ArrayList<>();
        for(ChessMove move : validMoves){
            highlightSpots.add(move.getEndPosition());
        }

        if(teamColor == null){
            teamColor = ChessGame.TeamColor.WHITE; //Show white side if observer
        }
        String boardString = "";
        boardString += horizontalBorder(teamColor);
        int rowStart = (teamColor == ChessGame.TeamColor.WHITE ? 8 : 1);
        int rowEnd = (teamColor == ChessGame.TeamColor.WHITE ? 1 : 8);

        int rowInc = (teamColor == ChessGame.TeamColor.WHITE ? -1 : 1);

        int colStart = (teamColor == ChessGame.TeamColor.WHITE ? 1 : 8);
        int colEnd = (teamColor == ChessGame.TeamColor.WHITE ? 8 : 1);

        int colInc = (teamColor == ChessGame.TeamColor.WHITE ? 1 : -1);

        for(int i = rowStart; (rowInc < 0 ? i >= rowEnd : i <= rowEnd); i = i + rowInc){ //i is the row
            boardString += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + i + " ";
            for (int j = colStart; (colInc < 0 ? j >= colEnd : j <= colEnd); j = j + colInc) { //j is the column
                String background = ((i + j) % 2 == 0 ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY);
                ChessPosition here = new ChessPosition(i,j);
                if(here.equals(highlightPiece)){
                    background = SET_BG_COLOR_DARK_YELLOW;
                }
                else if(highlightSpots.contains(here)){
                    background = ((i + j) % 2 == 0 ? SET_BG_COLOR_DARK_BLUE : SET_BG_COLOR_MEDIUM_BLUE);
                }
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
    protected String handleException(Exception e){
        switch(e){
            case ResponseException responseException -> {
                return e.getMessage();
            }
            case IndexOutOfBoundsException indexOutOfBoundsException -> {
                return "Not enough arguments. Try the command again";
            }
            case  NullPointerException nullPointerException -> {
                return "Bad ID. Please try again";
            }
            default -> {
                return "Unknown error. Please try again";
            }
        }
    }
    protected String enterPostLoginUi(String authToken){
        Client client = new PostLoginClient(serverFacade,repl,authToken);
        repl.setClient(client);
        return client.helpString();
    }
}

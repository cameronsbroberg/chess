package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;
import serverfacade.WsFacade;
import websocket.commands.UserGameCommand;

import java.io.IOException;

import static ui.EscapeSequences.*;

public class InGameClient extends Client{
    private WsFacade wsFacade;
    private String authToken;
    private int gameId;
    private ChessGame chessGame;
    private ChessGame.TeamColor teamColor;

    public InGameClient(ServerFacade serverFacade, Repl repl, String authToken, int gameId,ChessGame.TeamColor teamColor) throws IOException {
        this.wsFacade = serverFacade.getWsFacade();
        wsFacade.client = this; //FIXME this is bad
        this.repl = repl;
        this.authToken = authToken;
        this.gameId = gameId;
        this.teamColor = teamColor;
        UserGameCommand connectCommand = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameId,
                teamColor
        );
        wsFacade.send(connectCommand);
    }

    @Override
    protected String whichPiece(int row, int col){
        ChessPosition atPosition = new ChessPosition(row,col);
        ChessPiece piece = chessGame.getBoard().getPiece(atPosition);
        if(piece == null){
            return EMPTY;
        }
        String pieceString = "";
        switch(piece.getTeamColor()){
            case WHITE -> {
                pieceString = SET_TEXT_COLOR_WHITE;
                switch(piece.getPieceType()){
                    case KING -> {
                        pieceString += WHITE_KING;
                    }
                    case QUEEN -> {
                        pieceString += WHITE_QUEEN;
                    }
                    case BISHOP -> {
                        pieceString += WHITE_BISHOP;
                    }
                    case KNIGHT -> {
                        pieceString += WHITE_KNIGHT;
                    }
                    case ROOK -> {
                        pieceString += WHITE_ROOK;
                    }
                    case PAWN -> {
                        pieceString += WHITE_PAWN;
                    }
                }
            }
            case BLACK -> {
                pieceString += SET_TEXT_COLOR_BLACK;
                switch(piece.getPieceType()){
                    case KING -> {
                        pieceString += BLACK_KING;
                    }
                    case QUEEN -> {
                        pieceString += BLACK_QUEEN;
                    }
                    case BISHOP -> {
                        pieceString += BLACK_BISHOP;
                    }
                    case KNIGHT -> {
                        pieceString += BLACK_KNIGHT;
                    }
                    case ROOK -> {
                        pieceString += BLACK_ROOK;
                    }
                    case PAWN -> {
                        pieceString += BLACK_PAWN;
                    }
                }
            }
        }
        return pieceString;
    }

    public void updateGame(ChessGame newGame){
        this.chessGame = newGame;
        System.out.println(chessBoard(this.teamColor) + "\n");
        System.out.println(helpString() + "Type a command");
    }

    @Override
    String helpString() {
        return SET_TEXT_COLOR_BLUE + "Redraw " + SET_TEXT_COLOR_BLACK + "--- to show the board again\n" +
                SET_TEXT_COLOR_BLUE + "Leave " + SET_TEXT_COLOR_BLACK + "--- to leave the game\n" +
                SET_TEXT_COLOR_BLUE + "Move " + SET_TEXT_COLOR_BLACK + "<STARTING ROW,COL>,<ENDING ROW,COL>\n" +
                SET_TEXT_COLOR_BLUE + "Resign " + SET_TEXT_COLOR_BLACK + "--- to forfeit the game\n" +
                SET_TEXT_COLOR_BLUE + "Highlight " + SET_TEXT_COLOR_BLACK + "<PIECE ROW>,<PIECE COL> --- to show legal moves\n" +
                SET_TEXT_COLOR_BLUE + "Help " + SET_TEXT_COLOR_BLACK + "--- to get a list of commands" +
                SET_TEXT_COLOR_BLUE + "\n";
    }

    @Override
    String eval(String input) {
        try{
            var tokens = input.split(" ");
            String command = tokens[0].toLowerCase();
            switch(command){
                case("help") -> {
                    return helpString();
                }
                case("redraw") -> {
                    return "";
                }
                case("leave") -> {
                    //TODO: Do something
                    return enterPostLoginUi(authToken);
                }
                case("move") -> {
                    return "";
                }
                case("resign") -> {
                    return "";
                }
                case("highlight") -> {
                    return "";
                }
                default -> {
                    return "Unknown command. Try 'help' for options";
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return "Not enough arguments. Try the command again";
        }
        catch (ResponseException e) {
            return e.getMessage();
        }
        catch (Exception e) {
            return "Unknown error. Please try again";
        }
    }
    private String enterPostLoginUi(String authToken){
        Client client = new PostLoginClient(serverFacade,repl,authToken);
        repl.setClient(client);
        return client.helpString();
    }
}

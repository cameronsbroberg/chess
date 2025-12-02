package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;
import serverfacade.WsFacade;
import websocket.commands.UserGameCommand;

import java.io.IOException;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class InGameClient extends Client{
    private WsFacade wsFacade;
    private String authToken;
    private int gameId;
    private ChessGame chessGame;

    public InGameClient(ServerFacade serverFacade, Repl repl, String authToken, int gameId) throws IOException {
        this.wsFacade = serverFacade.getWsFacade();
        wsFacade.client = this; //FIXME this is bad
        this.repl = repl;
        this.authToken = authToken;
        this.gameId = gameId;
        UserGameCommand connectCommand = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameId);
        wsFacade.send(connectCommand);
    }
    public void updateGame(ChessGame newGame){
        this.chessGame = newGame;
        System.out.println("New ChessGame received!");
    }
    @Override
    String helpString() {
        return SET_TEXT_COLOR_BLUE + "Redraw " + SET_TEXT_COLOR_BLACK + "--- to show the board again\n" +
                SET_TEXT_COLOR_BLUE + "Leave " + SET_TEXT_COLOR_BLACK + "--- to leave the game\n" +
                SET_TEXT_COLOR_BLUE + "Move " + SET_TEXT_COLOR_BLACK + "<STARTING ROW,COL>,<ENDING ROW,COL>\n" +
                SET_TEXT_COLOR_BLUE + "Resign " + SET_TEXT_COLOR_BLACK + "--- to forfeit the game\n" +
                SET_TEXT_COLOR_BLUE + "Highlight " + SET_TEXT_COLOR_BLACK + "<PIECE ROW>,<PIECE COL> --- to show legal moves\n" +
                SET_TEXT_COLOR_BLUE + "Help " + SET_TEXT_COLOR_BLACK + "--- to get a list of commands" +
                SET_TEXT_COLOR_BLUE;
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

package ui;

import chess.ChessGame;
import requests.CreateRequest;
import requests.JoinRequest;
import results.CreateResult;
import results.GameListResult;
import results.GameSummary;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;

import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.*;

public class PostLoginClient extends Client {
    private String authToken;
    private Map<Integer, Integer> listedGames;
    public PostLoginClient(ServerFacade serverFacade, Repl repl, String authToken){
        this.serverFacade = serverFacade;
        this.repl = repl;
        this.authToken = authToken;
        this.listedGames = new HashMap<>();
    }
    @Override
    public String helpString(){
        return SET_TEXT_COLOR_BLUE + "Logout " + SET_TEXT_COLOR_BLACK + "--- to exit\n" + SET_TEXT_COLOR_BLUE +
                "Create " + SET_TEXT_COLOR_BLACK + "<GAME NAME>\n" + SET_TEXT_COLOR_BLUE +
                "List " + SET_TEXT_COLOR_BLACK + "--- to get a list of games\n" + SET_TEXT_COLOR_BLUE +
                "Join " + SET_TEXT_COLOR_BLACK + "<WHITE / BLACK> <GAME NUMBER>\n" + SET_TEXT_COLOR_BLUE +
                "Observe " + SET_TEXT_COLOR_BLACK + "<GAME NUMBER>\n" + SET_TEXT_COLOR_BLUE +
                "Help " + SET_TEXT_COLOR_BLACK + "--- to get a list of commands" + SET_TEXT_COLOR_BLUE;
    }
    private String chessBoard(ChessGame.TeamColor teamColor){
        String boardString = "";
        boardString += horizontalBorder(teamColor);
        int start = (teamColor == ChessGame.TeamColor.WHITE ? 8 : 1);
        int end = (teamColor == ChessGame.TeamColor.WHITE ? 1 : 8);
        int inc = (teamColor == ChessGame.TeamColor.WHITE ? -1 : 1);

        for(int i = start; (inc < 0 ? i >= end : i <= end); i = i + inc){ //i is the row
            boardString += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + i + " ";
            for (int j = 1; j <= 8; j++) { //j is the column
                String background = ((i + j) % 2 == 0 ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY);
                boardString += background + " " + whichPiece(i,j) + " ";
            }
            boardString += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + " " + i;
            boardString += SET_BG_COLOR_WHITE + "\n";
        }
        boardString += horizontalBorder(teamColor);
        return boardString;
    }
    private String horizontalBorder(ChessGame.TeamColor teamColor){
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
    private String whichPiece(int row, int col){
        switch(row){
            case(8) -> {
                switch(col){
                    case 1,8 -> {
                        return SET_TEXT_COLOR_BLACK + BLACK_ROOK;
                    }
                    case 2,7 -> {
                        return SET_TEXT_COLOR_BLACK + BLACK_KNIGHT;
                    }
                    case 3,6 -> {
                        return SET_TEXT_COLOR_BLACK + BLACK_BISHOP;
                    }
                    case 4 ->{
                        return SET_TEXT_COLOR_BLACK + BLACK_QUEEN;
                    }
                    case 5 ->{
                        return SET_TEXT_COLOR_BLACK + BLACK_KING;
                    }
                }
            }
            case(7) -> {
                return SET_TEXT_COLOR_BLACK + BLACK_PAWN;
            }
            case(2) -> {
                return SET_TEXT_COLOR_WHITE + WHITE_PAWN;
            }
            case(1) -> {
                switch(col){
                    case 1,8 -> {
                        return SET_TEXT_COLOR_WHITE + WHITE_ROOK;
                    }
                    case 2,7 -> {
                        return SET_TEXT_COLOR_WHITE + WHITE_KNIGHT;
                    }
                    case 3,6 -> {
                        return SET_TEXT_COLOR_WHITE + WHITE_BISHOP;
                    }
                    case 4 ->{
                        return SET_TEXT_COLOR_WHITE + WHITE_QUEEN;
                    }
                    case 5 ->{
                        return SET_TEXT_COLOR_WHITE + WHITE_KING;
                    }
                }
            }
            default -> {
                return EMPTY;
            }
        }
        return EMPTY;
    }
    private String listGames(String authToken){
        GameListResult result = serverFacade.listGames(authToken);
        String returnString = "GAMES:\n";
        listedGames.clear();
        int gameIndex = 1;
        for(GameSummary gameSummary : result.games()){
            returnString = returnString +
                    SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + RESET_TEXT_COLOR + gameIndex + ":" + RESET_BG_COLOR + SET_BG_COLOR_WHITE + " " +
                    SET_TEXT_COLOR_BLUE + RESET_TEXT_BOLD_FAINT + gameSummary.gameName() + " " +
                    SET_TEXT_COLOR_BLACK + "Players: " +
                    SET_TEXT_COLOR_BLUE + "WHITE: " + SET_TEXT_COLOR_BLACK +
                    (gameSummary.whiteUsername() == null ? "available" : gameSummary.whiteUsername()) + " " +
                    SET_TEXT_COLOR_BLUE + "BLACK: " + SET_TEXT_COLOR_BLACK +
                    (gameSummary.blackUsername() == null ? "available" : gameSummary.blackUsername()) + "\n";
            listedGames.put(gameIndex,gameSummary.gameID());
            gameIndex = gameIndex + 1;
        }
        return returnString;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            String command = tokens[0].toLowerCase();
            switch (command) {
                case ("help") -> {
                    return helpString();
                }
                case ("logout") -> {
                    serverFacade.logout(authToken);
                    return "Logged out successfully\n" + enterPreLoginUi();
                }
                case ("create") -> {
                    CreateRequest createRequest = new CreateRequest(authToken,null,null,tokens[1],new ChessGame());
                    CreateResult result = serverFacade.createGame(createRequest);
                    return "Game created. Games:\n" + listGames(authToken);
                }
                case ("list") -> {
                    return listGames(authToken);
                }
                case ("join") -> {
                    ChessGame.TeamColor teamColor = null;
                    if(tokens[1].toLowerCase().equals("white")){
                        teamColor = ChessGame.TeamColor.WHITE;
                    }
                    else if(tokens[1].toLowerCase().equals("black")){
                        teamColor = ChessGame.TeamColor.BLACK;
                    }
                    int gameId = listedGames.get(Integer.parseInt(tokens[2]));
                    JoinRequest request = new JoinRequest(teamColor,gameId);
                    serverFacade.joinGame(authToken,request);
                    return "Joined successfully" + "\n" + chessBoard(teamColor);
                }
                case ("observe") -> {
                    try {
                        int gameId = listedGames.get(Integer.parseInt(tokens[1]));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return "Observing game\n" + chessBoard(ChessGame.TeamColor.WHITE);
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
        catch (NullPointerException e){
            return "Bad ID. Please try again";
        }
        catch (Exception e) {
            return "Unknown error. Please try again";
        }
    }
    private String enterPreLoginUi(){
        Client client = new PreLoginClient(serverFacade,repl);
        repl.setClient(client);
        return client.helpString();
    }
}

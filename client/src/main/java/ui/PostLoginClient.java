package ui;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import requests.CreateRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import results.CreateResult;
import results.GameListResult;
import results.GameSummary;
import serverFacade.ResponseException;
import serverFacade.ServerFacade;

public class PostLoginClient extends Client {
    private String authToken;
    public PostLoginClient(ServerFacade serverFacade, Repl repl, String authToken){
        this.serverFacade = serverFacade;
        this.repl = repl;
        this.authToken = authToken;
    }
    @Override
    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            String command = tokens[0].toLowerCase();
            switch (command) {
                case ("help") -> {
                    return "Type 'help' for command options";
                }
                case ("logout") -> {
                    serverFacade.logout(authToken);
                    enterPreLoginUi();
                    return "Logged out successfully";
                }
                case ("create") -> {
                    CreateRequest createRequest = new CreateRequest(authToken,null,null,tokens[1],new ChessGame());
                    CreateResult result = serverFacade.createGame(createRequest);
                    return result.toString();
                }
                case ("list") -> {
                    GameListResult result = serverFacade.listGames(authToken);
                    String returnString = "";
                    for(GameSummary gameSummary : result.games()){
                        returnString = returnString + gameSummary.gameName() + "\n";
                    }
                    return returnString;
                }
                case ("join") -> {
                    ChessGame.TeamColor teamColor = null;
                    if(tokens[1].toLowerCase().equals("white")){
                        teamColor = ChessGame.TeamColor.WHITE;
                    }
                    else if(tokens[1].toLowerCase().equals("black")){
                        teamColor = ChessGame.TeamColor.BLACK;
                    }
                    JoinRequest request = new JoinRequest(teamColor,Integer.parseInt(tokens[2]));
                    serverFacade.joinGame(authToken,request);
                    return "Joined successfully";
                }
                case ("observe") -> {

                }
                default -> {
                    return "Unknown command. Try 'help' for options";
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }
    private void enterPreLoginUi(){
        repl.setClient(new PreLoginClient(serverFacade,repl));
    }
}

package ui;

import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import serverFacade.ResponseException;
import serverFacade.ServerFacade;

public class PreLoginClient extends Client {
    public PreLoginClient(ServerFacade serverFacade, Repl repl){
        this.serverFacade = serverFacade;
        this.repl = repl;
    }
    @Override
    public String eval(String input){
        try{
            var tokens = input.split(" ");
            String command = tokens[0].toLowerCase();
            switch(command){
                case("help") -> {
                    return "Type 'help' for command options";
                }
                case("login") -> {
                    LoginRequest loginRequest = new LoginRequest(tokens[1],tokens[2]);
                    try {
                        String authToken = serverFacade.login(loginRequest).authToken();
                        enterPostLoginUi(authToken);
                        return "Login successful";
                    } catch (ResponseException e) {
                        return "Login failed. Try again";
                    }
                }
                case("register") -> {
                    UserData userData = new UserData(tokens[1],tokens[2],tokens[3]);
                    AuthData authData = serverFacade.register(userData);
                    enterPostLoginUi(authData.authToken());
                    return authData.toString();
                }
                case("quit") -> {
                    return "quit";
                }
                default -> {
                    return "Unknown command. Try 'help' for options";
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    private void enterPostLoginUi(String authToken){
        repl.setClient(new PostLoginClient(serverFacade,repl,authToken));
    }
}

package ui;

import model.AuthData;
import model.UserData;
import serverFacade.ServerFacade;

public class PreLoginClient {
    private final ServerFacade serverFacade;
    public PreLoginClient(ServerFacade serverFacade){
        this.serverFacade = serverFacade;
    }
    public String eval(String input){
        try{
            var tokens = input.split(" ");
            String command = tokens[0].toLowerCase();
            switch(command){
                case("help") -> {
                    return "Type 'help' for command options";
                }
                case("login") -> {
                    return "We'll log you in!";
                }
                case("register") -> {
                    UserData userData = new UserData(tokens[1],tokens[2],tokens[3]);
                    AuthData authData = serverFacade.register(userData);
                    return authData.toString();
                }
                default -> {
                    return "Unknown command. Try 'help' for options";
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

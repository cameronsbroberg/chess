package ui;

import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;

import static ui.EscapeSequences.*;

public class PreLoginClient extends Client {
    public PreLoginClient(ServerFacade serverFacade, Repl repl){
        this.serverFacade = serverFacade;
        this.repl = repl;
    }
    public String helpString(){
        return SET_TEXT_COLOR_BLUE + "Login " + SET_TEXT_COLOR_BLACK + "<USERNAME> <PASSWORD>\n" + SET_TEXT_COLOR_BLUE +
                "Register " + SET_TEXT_COLOR_BLACK + "<USERNAME> <PASSWORD> <EMAIL>\n" + SET_TEXT_COLOR_BLUE +
                "Quit " + SET_TEXT_COLOR_BLACK + "--- to exit the program\n" + SET_TEXT_COLOR_BLUE +
                "Help " + SET_TEXT_COLOR_BLACK + "--- to get a list of commands" + SET_TEXT_COLOR_BLUE;
    }
    @Override
    public String eval(String input){
        try{
            var tokens = input.split(" ");
            String command = tokens[0].toLowerCase();
            switch(command){
                case("help") -> {
                    return helpString();
                }
                case("login") -> {
                    LoginRequest loginRequest = new LoginRequest(tokens[1],tokens[2]);
                    try {
                        String authToken = serverFacade.login(loginRequest).authToken();
                        return "Login successful" + "\n" + enterPostLoginUi(authToken);
                    } catch (ResponseException e) {
                        return "Login failed. Try again";
                    }
                }
                case("register") -> {
                    UserData userData = new UserData(tokens[1],tokens[2],tokens[3]);
                    AuthData authData = serverFacade.register(userData);
                    return "Logged in successfully. Welcome, " + tokens[1] + "\n" + enterPostLoginUi(authData.authToken());
                }
                case("quit") -> {
                    return "quit";
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

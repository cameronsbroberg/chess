package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.CreateRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import results.CreateResult;

public class GameServiceTests {

    @Test
    @DisplayName("Successfully create a new game when authorized.")
    public void createNewGame(){
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        UserData user = new UserData("Alice","pass123","hello@byu.edu");
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        CreateResult createResult = Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));
        Assertions.assertEquals(createResult.gameID(), 1);
    }

    @Test
    @DisplayName("Unsuccessfully create a new game when not authorized.")
    public void createNewGameIllegally(){
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        UserData user = new UserData("Alice","pass123","hello@byu.edu");
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        userService.logout(userAuth.authToken());
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        Assertions.assertThrows(InvalidTokenException.class,() -> gameService.createGame(createRequest));
    }

    //TODO Add tests for list
    //TODO Add tests for join
    @Test
    @DisplayName("Successfully join a game")
    public void joinGame(){
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO); //TODO Make an initialization function that creates instances of all these objects
        UserData user = new UserData("Alice","pass123","hello@byu.edu");
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(userAuth.authToken(), new JoinRequest(ChessGame.TeamColor.WHITE, 1)));
    }
}

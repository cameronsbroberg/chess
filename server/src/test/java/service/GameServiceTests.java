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
import static chess.ChessGame.TeamColor.*;

public class GameServiceTests {
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private UserService userService = new UserService(this.memoryUserDAO, this.memoryAuthDAO);
    private GameService gameService = new GameService(this.memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    private UserData genericUser = new UserData("Alice","pass123","hello@byu.edu");

    private void initialize(){
        this.memoryUserDAO = new MemoryUserDAO();
        this.memoryAuthDAO = new MemoryAuthDAO();
        this.memoryGameDAO = new MemoryGameDAO();
        this.userService = new UserService(this.memoryUserDAO, this.memoryAuthDAO);
        this.gameService = new GameService(this.memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        this.genericUser = new UserData("Alice","pass123","hello@byu.edu");
    }

    @Test
    @DisplayName("Successfully create a new game when authorized.")
    public void createNewGame(){
        initialize();
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(genericUser));
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        CreateResult createResult = Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));
        Assertions.assertEquals(createResult.gameID(), 1);
    }

    @Test
    @DisplayName("Unsuccessfully create a new game when not authorized.")
    public void createNewGameIllegally(){
        initialize();
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(genericUser));
        userService.logout(userAuth.authToken());
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        Assertions.assertThrows(InvalidTokenException.class,() -> gameService.createGame(createRequest));
    }

    @Test
    @DisplayName("Successfully get list of games when there are no games")
    public void listGamesEmpty() {
        initialize();
        AuthData userAuth = userService.register(genericUser);
        Assertions.assertDoesNotThrow(() -> gameService.listGames(userAuth.authToken()));
    }

    @Test
    @DisplayName("Successfully get non-empty list of games")
    public void listGames() {
        initialize();
        AuthData userAuth = userService.register(genericUser);
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        CreateRequest createRequestTwo = new CreateRequest(userAuth.authToken(),
                null,null,
                "Second game",new ChessGame());
        gameService.createGame(createRequest);
        gameService.createGame(createRequestTwo);
        Assertions.assertDoesNotThrow(() -> gameService.listGames(userAuth.authToken()));
    }

    @Test
    @DisplayName("Unsuccessfully get list of games when unauthorized")
    public void listGamesNotAuthorized() {
        initialize();
        AuthData userAuth = userService.register(genericUser);
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        CreateRequest createRequestTwo = new CreateRequest(userAuth.authToken(),
                null,null,
                "Second game",new ChessGame());
        gameService.createGame(createRequest);
        gameService.createGame(createRequestTwo);
        Assertions.assertDoesNotThrow(() -> userService.logout(userAuth.authToken()));
        Assertions.assertThrows(InvalidTokenException.class, () -> gameService.listGames(userAuth.authToken()));
    }

    @Test
    @DisplayName("Successfully get list of games after logging off and on")
    public void listGamesReauthorized() {
        initialize();
        AuthData userAuth = userService.register(genericUser);
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        CreateRequest createRequestTwo = new CreateRequest(userAuth.authToken(),
                null,null,
                "Second game",new ChessGame());
        gameService.createGame(createRequest);
        gameService.createGame(createRequestTwo);
        Assertions.assertDoesNotThrow(() -> userService.logout(userAuth.authToken()));
        Assertions.assertThrows(InvalidTokenException.class, () -> gameService.listGames(userAuth.authToken()));
        AuthData loginAuth = Assertions.assertDoesNotThrow(() -> userService.login(new LoginRequest(genericUser.username(),genericUser.password())));
        Assertions.assertDoesNotThrow(() -> gameService.listGames(loginAuth.authToken()));
    }

    @Test
    @DisplayName("Successfully join a game")
    public void joinGame(){
        initialize();
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(genericUser));
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(userAuth.authToken(), new JoinRequest(WHITE, 1)));
    }

    @Test
    @DisplayName("Unsuccessfully join a nonexistent game")
    public void joinBadGame(){
        initialize();
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(genericUser));
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));
        Assertions.assertThrows(BadRequestException.class,
                () -> gameService.joinGame(userAuth.authToken(), new JoinRequest(WHITE, 2)));
    }

    @Test
    @DisplayName("Unsuccessfully join a game when logged out")
    public void joinGameBadAuth(){
        initialize();
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(genericUser));
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));
        userService.logout(userAuth.authToken());
        Assertions.assertThrows(InvalidTokenException.class,
                () -> gameService.joinGame(userAuth.authToken(), new JoinRequest(WHITE, 1)));
    }

    @Test
    @DisplayName("Successfully join a game as a second color")
    public void joinGameSecond(){
        initialize();
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(genericUser));
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));
        Assertions.assertDoesNotThrow(
                () -> gameService.joinGame(userAuth.authToken(), new JoinRequest(WHITE, 1)));
        UserData secondUser = new UserData("Bob", "bobpass123","bob@email.com");
        AuthData secondAuth = userService.register(secondUser);
        Assertions.assertDoesNotThrow(
                () -> gameService.joinGame(secondAuth.authToken(), new JoinRequest(BLACK, 1)));
    }

    @Test
    @DisplayName("Unsuccessfully join a game as a taken color")
    public void joinTakenGame(){
        initialize();
        AuthData userAuth = Assertions.assertDoesNotThrow(() -> userService.register(genericUser));
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));
        Assertions.assertDoesNotThrow(
                () -> gameService.joinGame(userAuth.authToken(), new JoinRequest(BLACK, 1)));
        UserData secondUser = new UserData("Bob", "bobpass123","bob@email.com");
        AuthData secondAuth = userService.register(secondUser);
        Assertions.assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(secondAuth.authToken(), new JoinRequest(BLACK, 1)));
    }

    @Test
    @DisplayName("Successfully clear")
    public void clearEverything(){
        initialize();
        AuthData userAuth = userService.register(genericUser);
        CreateRequest createRequest = new CreateRequest(userAuth.authToken(),
                null,null,
                "First game",new ChessGame());
        CreateRequest createRequestTwo = new CreateRequest(userAuth.authToken(),
                null,null,
                "Second game",new ChessGame());
        gameService.createGame(createRequest);
        gameService.createGame(createRequestTwo);
        gameService.clear();
        Assertions.assertThrows(InvalidTokenException.class,
                () -> userService.login(new LoginRequest(genericUser.username(), genericUser.password())));
        Assertions.assertThrows(InvalidTokenException.class,
                () -> gameService.listGames(userAuth.authToken()));

    }
}
package client;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import requests.CreateRequest;
import requests.LoginRequest;
import server.Server;
import serverFacade.ResponseException;
import serverFacade.ServerFacade;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeEach
    public void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Register a user")
    public void register(){
        UserData newUser = new UserData("charlie","pw","email@byu.edu");
        AuthData registerResponse = Assertions.assertDoesNotThrow(() -> serverFacade.register(newUser));
        Assertions.assertEquals(AuthData.class, registerResponse.getClass());
        Assertions.assertEquals("charlie",registerResponse.username());
    }
    @Test
    @DisplayName("Fail to register")
    public void registerBad(){
        UserData newUser = new UserData("cameron","pw","email@byu.edu");
        Assertions.assertDoesNotThrow(() -> serverFacade.register(newUser));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(newUser));
    }
    @Test
    @DisplayName("Fail to register incomplete data")
    public void registerBadInput(){
        UserData newUser = new UserData("cameron",null,null);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(newUser));
    }
    @Test
    @DisplayName("Logout a signed in user.")
    public void logout(){
        UserData newUser = new UserData("cameron","pw","user@mail.byu.edu");
        AuthData registerResponse = Assertions.assertDoesNotThrow(() -> serverFacade.register(newUser));
        Assertions.assertEquals(AuthData.class,registerResponse.getClass());
        Assertions.assertEquals(String.class, registerResponse.authToken().getClass());
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(registerResponse.authToken()));
    }
    @Test
    @DisplayName("Fail to logout a user twice.")
    public void badLogout(){
        UserData newUser = new UserData("cameron","pw","user@mail.byu.edu");
        AuthData registerResponse = Assertions.assertDoesNotThrow(() -> serverFacade.register(newUser));
        Assertions.assertEquals(AuthData.class,registerResponse.getClass());
        Assertions.assertEquals(String.class, registerResponse.authToken().getClass());
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(registerResponse.authToken()));
        Assertions.assertThrows(ResponseException.class,() -> serverFacade.logout(registerResponse.authToken()));
    }
    @Test
    @DisplayName("Login after logout")
    public void login(){
        logout();
        Assertions.assertDoesNotThrow(()->serverFacade.login(new LoginRequest("cameron","pw")));
    }
    @Test
    @DisplayName("Login with bad pw")
    public void loginBad(){
        logout();
        LoginRequest loginRequest = new LoginRequest("cameron","badPw");
        Assertions.assertThrows(ResponseException.class,()->serverFacade.login(loginRequest));
    }
    @Test
    @DisplayName("Create a game")
    public void createGame(){
        UserData newUser = new UserData("cameron","pw","user@mail.byu.edu");
        AuthData registerResponse = Assertions.assertDoesNotThrow(() -> serverFacade.register(newUser));
        CreateRequest createRequest = new CreateRequest(registerResponse.authToken(),null,null,"First game",new ChessGame());
        Assertions.assertDoesNotThrow(()->serverFacade.createGame(createRequest));
    }
    @Test
    @DisplayName("Create game unauthorized")
    public void createGameBadAuth(){
        CreateRequest createRequest = new CreateRequest("badAuth",null,null,"First game",new ChessGame());
        Assertions.assertThrows(ResponseException.class,()->serverFacade.createGame(createRequest));
    }
}

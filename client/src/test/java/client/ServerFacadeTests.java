package client;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import serverFacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
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
        UserData newUser = new UserData("cameron","pw","email@byu.edu");
        AuthData registerResponse = Assertions.assertDoesNotThrow(() -> serverFacade.register(newUser));
        Assertions.assertEquals(registerResponse.getClass(), AuthData.class);
        Assertions.assertEquals("cameron",registerResponse.username());
    }
    @Test
    @DisplayName("Fail to register")
    public void registerBad(){
        UserData newUser = new UserData("cameron","pw","email@byu.edu");
        Assertions.assertDoesNotThrow(() -> serverFacade.register(newUser));

        Assertions.assertThrows(Exception.class, () -> serverFacade.register(newUser));
    }
}

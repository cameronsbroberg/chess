package client;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import serverFacade.ServerFacade;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clear();
//        var clearRequest = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:" + port + "/db"))
//                .method("delete", null);
//        clearRequest.build();
//        serverFacade.client.send()

    }

    @AfterAll
    static void stopServer() {
        server.stop();
//        var request = HttpRequest.newBuilder()
//                .uri(URI.create(serverUrl + path))
//                .method(method, makeRequestBody(body));
//        if(body != null) {
//            request.setHeader("Content-Type","application/json"); //What does this do?
//        }
//        return request.build();
//        private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
//            try {
//                return client.send(request, HttpResponse.BodyHandlers.ofString());
//            } catch (Exception ex) {
//                throw new Exception(ex.getMessage());
//            }
//        }
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
        Assertions.assertEquals(registerResponse.getClass(), AuthData.class);
        Assertions.assertEquals("charlie",registerResponse.username());
    }
    @Test
    @DisplayName("Fail to register")
    public void registerBad(){
        UserData newUser = new UserData("cameron","pw","email@byu.edu");
        Assertions.assertDoesNotThrow(() -> serverFacade.register(newUser));
        Assertions.assertThrows(Exception.class, () -> serverFacade.register(newUser));
    }
}

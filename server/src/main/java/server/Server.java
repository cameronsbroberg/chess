package server;

import dataaccess.*;
import handler.GameHandler;
import handler.UserHandler;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {//A server takes HTTP requests and passes them to a handler
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        UserDAO userDAO = new MySqlUserDAO();
        AuthDAO authDAO = new MySqlAuthDAO();
        GameDAO gameDAO = new MySqlGameDAO();
        UserHandler userHandler = new UserHandler(userDAO, authDAO);
        GameHandler gameHandler = new GameHandler(userDAO, authDAO, gameDAO);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);
        javalin.post("/game",gameHandler::create);
        javalin.get("/game",gameHandler::list);
        javalin.put("/game",gameHandler::join);
        javalin.delete("/db",gameHandler::clear);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

package server;

import handler.UserHandler;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

    public Server() {//A server takes HTTP requests and passes them to a handler
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
//        javalin loginEndpoint = javalin.addEndpoint(.post("/user", new UserHandler().register(Context ctx)));
        UserHandler userHandler = new UserHandler();
        javalin.post("/user", userHandler::register);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

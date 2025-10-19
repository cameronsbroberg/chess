package handler;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import service.AlreadyTakenException;
import service.InvalidLoginException;
import service.UserService;

public class UserHandler {//Handlers handle jsons. They pass models to the service and get http requests from the Server.
    private Gson serializer;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    public UserHandler() {
        this.serializer = new Gson();
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.userService = new UserService(userDAO,authDAO);
    }

    public void register(Context ctx){
        String json = ctx.body();
        UserData userData = serializer.fromJson(json, UserData.class);
        AuthData result = null;
        try {
            result = userService.register(userData);
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        } catch (AlreadyTakenException e) {
//            String errorJson = String.format("{\"message\": \"%s\"}", e.getMessage());
            ctx.status(403);
            ctx.result(serializer.toJson(e.getMessage()));
        }
    }

    public void login(Context ctx){
        String json = ctx.body();
        LoginRequest loginRequest = serializer.fromJson(json, LoginRequest.class);
        AuthData result = null;
        try {
            result = userService.login(loginRequest);
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        } catch (InvalidLoginException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(e.getMessage()));
        }

    }
}

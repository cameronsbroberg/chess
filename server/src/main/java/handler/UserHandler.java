package handler;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.InvalidTokenException;
import service.UserService;

import java.util.Map;

public class UserHandler {//Handlers handle jsons. They pass models to the service and get http requests from the Server.
    final private Gson serializer;
    final private UserService userService;

    public UserHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.serializer = new Gson();
        this.userService = new UserService(userDAO,authDAO);
    }

    public void register(Context ctx){
        String json = ctx.body();
        UserData userData = serializer.fromJson(json, UserData.class);
        try {
            AuthData result = userService.register(userData);
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        }
    }

    public void login(Context ctx){
        String json = ctx.body();
        LoginRequest loginRequest = serializer.fromJson(json, LoginRequest.class);
        try {
            AuthData result = userService.login(loginRequest);
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (InvalidTokenException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (Exception e){
            ctx.status(500);
            ctx.result(serializer.toJson(e.getMessage()));
        }

    }

    public void logout(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            userService.logout(authToken);
            ctx.status(200);
            ctx.result("{}");
        } catch (InvalidTokenException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (Exception e){
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        }
    }
}

package handler;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import service.UserService;

public class UserHandler extends Handler{
    final private UserService userService;

    public UserHandler(UserDAO userDAO, AuthDAO authDAO) {
        super();
        this.userService = new UserService(userDAO,authDAO);
    }

    public void register(Context ctx){
        String json = ctx.body();
        UserData userData = serializer.fromJson(json, UserData.class);
        try {
            AuthData result = userService.register(userData);
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        } catch (Exception e) {
            handleException(e,ctx);
        }
    }

    public void login(Context ctx){
        String json = ctx.body();
        LoginRequest loginRequest = serializer.fromJson(json, LoginRequest.class);
        try {
            AuthData result = userService.login(loginRequest);
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        } catch (Exception e){
            handleException(e, ctx);
        }

    }

    public void logout(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            userService.logout(authToken);
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e){
            handleException(e, ctx);
        }
    }
}

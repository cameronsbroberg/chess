package handler;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.AlreadyTakenException;
import service.UserService;

public class UserHandler {//Handlers handle jsons. They pass models to the service and get http requests from the Server.
    private Gson serializer = new Gson();
    public String register(Context ctx){
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);

        String json = ctx.body(); //Sort of a shot in the dark
        UserData userData = serializer.fromJson(json, UserData.class);
        AuthData result = null;
        try {
            result = userService.register(userData);
            return serializer.toJson(result);
        } catch (AlreadyTakenException e) {
            String error_message = e.getMessage();
            return "Error: " + error_message;
        }
    }
}

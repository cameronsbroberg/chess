package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.InvalidTokenException;
import service.UserService;

import java.util.Map;

public abstract class Handler {
    final protected Gson serializer;

    public Handler() {
        this.serializer = new Gson();
    }
    protected void handleException(Exception e, Context ctx){
        switch (e) {
            case BadRequestException badRequestException -> ctx.status(400);
            case InvalidTokenException invalidTokenException -> ctx.status(401);
            case AlreadyTakenException alreadyTakenException -> ctx.status(403);
            case ResponseException responseException -> ctx.status(500);
            case null, default -> ctx.status(500);
        }
        ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
    }
}

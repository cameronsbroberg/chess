package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
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
        if (e instanceof BadRequestException) {
            ctx.status(400);
        }
        else if (e instanceof InvalidTokenException) {
            ctx.status(401);
        }
        else if (e instanceof AlreadyTakenException) {
            ctx.status(403);
        }
        else {
            ctx.status(500);
        }
        ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
    }
}

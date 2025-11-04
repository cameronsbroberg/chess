package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.InvalidTokenException;

import java.util.Map;

public abstract class Handler {
    final protected Gson serializer;

    public Handler() {
        this.serializer = new Gson();
    }
    protected void handleException(Exception e, Context ctx){
        switch (e) {
            case BadRequestException badRequestException -> ctx.status(400); //When the user's request is missing some part of it, or when the request points to something that isn't there.
            case InvalidTokenException invalidTokenException -> ctx.status(401); //When the user requires authorization and doesn't have it.
            case AlreadyTakenException alreadyTakenException -> ctx.status(403); //When the username is already taken.
            case DataAccessException dataAccessException -> ctx.status(500); //When the server tries to access the database and can't.
            case null, default -> ctx.status(500);
        }
        ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
    }
}

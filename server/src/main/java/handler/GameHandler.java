package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import requests.CreateRequest;
import service.GameService;
import service.InvalidTokenException;

public class GameHandler {
    final private Gson serializer;
    final private GameService gameService;

    public GameHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.serializer = new Gson();
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    public void create(Context ctx){
        String authToken = ctx.header("authorization");
        String gameName = ctx.formParam("gameName");
        CreateRequest createRequest = new CreateRequest(authToken, null, null, gameName,new ChessGame());
        try {
            int gameID = gameService.createGame(createRequest);
            ctx.status(200);
            ctx.result(serializer.toJson(gameID));
        } catch (InvalidTokenException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(e.getMessage()));
        }
    }
}

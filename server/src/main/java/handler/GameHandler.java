package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import requests.CreateRequest;
import requests.JoinRequest;
import results.CreateResult;
import results.GameListResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.GameService;
import service.InvalidTokenException;

import java.util.Map;

public class GameHandler extends Handler {
    final private GameService gameService;

    public GameHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super();
        this.gameService = new GameService(userDAO, authDAO, gameDAO);
    }

    public void create(Context ctx){
        String authToken = ctx.header("authorization");
        CreateRequest json = serializer.fromJson(ctx.body(), CreateRequest.class);
        CreateRequest createRequest = new CreateRequest(
                authToken,
                json.whiteUsername(),
                json.blackUsername(),
                json.gameName(),
                new ChessGame());
        try {
            CreateResult createResult = gameService.createGame(createRequest);
            ctx.status(200);
            ctx.result(serializer.toJson(createResult));
        } catch (Exception e) {
            handleException(e,ctx);
        }
    }

    public void list(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            GameListResult gameListResult = gameService.listGames(authToken);
            ctx.status(200);
            ctx.result(serializer.toJson(gameListResult));
        } catch (Exception e) {
            handleException(e,ctx);
        }
    }

    public void join(Context ctx){
        String authToken = ctx.header("authorization");
        JoinRequest joinRequest = serializer.fromJson(ctx.body(), JoinRequest.class);
        try {
            gameService.joinGame(authToken, joinRequest);
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e) {
            handleException(e,ctx);
        }
    }

    public void clear(Context ctx) {
        try {
            gameService.clear();
        } catch (Exception e) {
            handleException(e,ctx);
        }
    }
}

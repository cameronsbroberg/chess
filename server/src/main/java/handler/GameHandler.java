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

public class GameHandler {
    final private Gson serializer;
    final private GameService gameService;

    public GameHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.serializer = new Gson();
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
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (InvalidTokenException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        }
    }

    public void list(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            GameListResult gameListResult = gameService.listGames(authToken);
            ctx.status(200);
            ctx.result(serializer.toJson(gameListResult));
        } catch (InvalidTokenException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        }
    }

    public void join(Context ctx){
        String authToken = ctx.header("authorization");
        JoinRequest joinRequest = serializer.fromJson(ctx.body(), JoinRequest.class);
        try {
            gameService.joinGame(authToken, joinRequest);
            ctx.status(200);
            ctx.result("{}");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (InvalidTokenException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        }
    }

    public void clear(Context ctx) {
        try {
            gameService.clear();
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        }
    }
}

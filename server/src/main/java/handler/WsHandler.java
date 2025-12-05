package handler;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import server.Server;
import service.InvalidTokenException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WsHandler extends Handler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    /// TODO: Should this actually extend Handler? If it does, it gets:
    /// a serializer
    /// exception handling with error codes.
    /// yeah maybe I guess
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final ConnectionManager connectionManager;

    public WsHandler(GameDAO gameDAO, AuthDAO authDAO) {
        super();
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.connectionManager = new ConnectionManager();
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
        switch(command.getCommandType()){
            case CONNECT -> connect(ctx,command);
            case MAKE_MOVE -> {
            }
            case LEAVE -> leave(ctx,command);
            case RESIGN -> {
            }
        }
    }
    private void connect(@NotNull WsMessageContext ctx, UserGameCommand command) throws InvalidTokenException, DataAccessException, IOException {
        connectionManager.add(command.getGameID(),ctx.session);
        ServerMessage loadGameMessage = new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME,
                new ChessGame());
        String loadGameJson = serializer.toJson(loadGameMessage);
        ctx.send(loadGameJson);
        String username = authDAO.getAuth(command.getAuthToken()).username();
        ChessGame.TeamColor teamColor = command.getTeamColor();
        String asWhat;
        switch(teamColor){
            case WHITE -> {
                asWhat = "white";
            }
            case BLACK -> {
                asWhat = "black";
            }
            case null -> {
                asWhat = "an observer";
            }
        }
        ServerMessage joinMessage = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has joined the game as " + asWhat
        );
        String joinString = serializer.toJson(joinMessage);
        connectionManager.broadcast(ctx.session,command.getGameID(),joinString);
    }

    private void leave(@NotNull WsMessageContext ctx, UserGameCommand command) throws InvalidTokenException, DataAccessException, IOException {
        connectionManager.remove(command.getGameID(), ctx.session);
        if(command.getTeamColor() != null){
            GameData gameData = gameDAO.getGame(command.getGameID());
            String whiteUser = gameData.whiteUsername();
            String blackUser = gameData.blackUsername();
            String gameName = gameData.gameName();
            ChessGame game = gameData.game();
            if(command.getTeamColor() == ChessGame.TeamColor.WHITE){
                whiteUser = null;
            }
            else{
                blackUser = null;
            }
            GameData newGameData = new GameData(
                    gameData.gameId(),
                    whiteUser,
                    blackUser,
                    gameName,
                    game
            );
            gameDAO.updateGame(command.getGameID(), newGameData);
        }

        String username = authDAO.getAuth(command.getAuthToken()).username();
        ServerMessage leaveMessage = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has left the game"
        );
        String leaveString = serializer.toJson(leaveMessage);
        connectionManager.broadcast(ctx.session,command.getGameID(),leaveString);
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }
}

package handler;

import chess.ChessGame;
import chess.InvalidMoveException;
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
            case MAKE_MOVE -> move(ctx,command);
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

    private void move(@NotNull WsMessageContext ctx, UserGameCommand command) throws DataAccessException, IOException, InvalidTokenException {
        //make sure you're connected. Probably? My guess is unnecessary, but good practice.
        if(command.getTeamColor() == null){
            String error = serializer.toJson(new ServerMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    "Observers cannot move"
            ));
            ctx.send(error);
            return;
        }
        GameData gameData = gameDAO.getGame(command.getGameID());
        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();
        if(game.isInCheckmate(command.getTeamColor()) || game.isInStalemate(command.getTeamColor())){
            String error = serializer.toJson(new ServerMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    "Game is over. No moves allowed."
            ));
            ctx.send(error);
            return;
        }
        try {
            game.makeMove(command.getChessMove());
        }
        catch (InvalidMoveException e) {
            String error = serializer.toJson(new ServerMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    "Invalid move."
            ));
            ctx.send(error);
            return;
        }
        GameData newGameData = new GameData(
                gameData.gameId(),
                whiteUser,
                blackUser,
                gameName,
                game
        );
        gameDAO.updateGame(gameData.gameId(), newGameData);

        ServerMessage loadGameMessage = new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game);
        String loadGameJson = serializer.toJson(loadGameMessage);
        connectionManager.broadcast(null,command.getGameID(),loadGameJson);

        ChessGame.TeamColor otherPlayer = (command.getTeamColor() == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
        String username = authDAO.getAuth(command.getAuthToken()).username();
        if (game.isInCheckmate(otherPlayer)) {
            String gameOver = serializer.toJson(new ServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " won by checkmate. Game over."
            ));
            connectionManager.broadcast(null, command.getGameID(),gameOver);
        } else if (game.isInStalemate(otherPlayer)) {
            String gameOver = serializer.toJson(new ServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "Game ended by stalemate. Game over."
            ));
            connectionManager.broadcast(null, command.getGameID(),gameOver);
        }
        //try to execute the move
        //send an error if it's not valid for any reason
        //update the game using the DAO.
        //send a loadgame message to ALL players in game.
        //Send checkmate or stalemate notifications if necessary.
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

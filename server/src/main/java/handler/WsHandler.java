package handler;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.*;

import java.util.Collection;
import java.util.HashSet;

public class WsHandler extends Handler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final ConnectionManager connectionManager;
    private Collection<Integer> finishedGameIds = new HashSet<>();

    public WsHandler(GameDAO gameDAO, AuthDAO authDAO) {
        super();
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.connectionManager = new ConnectionManager();
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
        switch(command.getCommandType()){
            case CONNECT -> connect(ctx, serializer.fromJson(ctx.message(), ConnectCommand.class));
            case MAKE_MOVE -> move(ctx, serializer.fromJson(ctx.message(), MoveCommand.class));
            case LEAVE -> leave(ctx,command);
            case RESIGN -> resign(ctx,command);
        }
    }
    private void connect(@NotNull WsMessageContext ctx, ConnectCommand command) {
        try {
            String username = authDAO.getAuth(command.getAuthToken()).username();
            connectionManager.add(command.getGameID(),ctx.session);
            GameData gameData = gameDAO.getGame(command.getGameID());
            ServerMessage loadGameMessage = new LoadGameMessage(gameData.game());
            String loadGameJson = serializer.toJson(loadGameMessage);
            ctx.send(loadGameJson);

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
            ServerMessage joinMessage = new Notification(
                    username + " has joined the game as " + asWhat
            );
            String joinString = serializer.toJson(joinMessage);
            connectionManager.broadcast(ctx.session,command.getGameID(),joinString);
        } catch (Exception e) {
            ctx.send(serializer.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }
    private void move(@NotNull WsMessageContext ctx, MoveCommand command) {
        try {
            String username = authDAO.getAuth(command.getAuthToken()).username();
//            if(command.getTeamColor() == null){
//                String error = serializer.toJson(new ErrorMessage(
//                        "Observers cannot move"
//                ));
//                ctx.send(error);
//                return;
//            }
            GameData gameData = gameDAO.getGame(command.getGameID());
            String whiteUser = gameData.whiteUsername();
            String blackUser = gameData.blackUsername();
            String gameName = gameData.gameName();
            ChessGame game = gameData.game();
            if(finishedGameIds.contains(gameData.gameId())){
                String error = serializer.toJson(new ErrorMessage(
                        "Game is over. No moves allowed."
                ));
                ctx.send(error);
                return;
            }

            ChessMove move = command.getChessMove();
            try {
                game.makeMove(move);
            }
            catch (InvalidMoveException e) {
                String error = serializer.toJson(new ErrorMessage(
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

            ServerMessage loadGameMessage = new LoadGameMessage(game);
            String loadGameJson = serializer.toJson(loadGameMessage);
            connectionManager.broadcast(null,command.getGameID(),loadGameJson);
            String moveMessage = serializer.toJson(new Notification(
                    username + " moved from " +
                            parsePosition(move.getStartPosition()) + " to " +
                            parsePosition(move.getEndPosition())
            ));
            connectionManager.broadcast(ctx.session, command.getGameID(), moveMessage);

            ChessGame.TeamColor otherPlayer = (command.getTeamColor() == ChessGame.TeamColor.WHITE ?
                    ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
            if (game.isInCheckmate(otherPlayer)) {
                String gameOver = serializer.toJson(new Notification(
                        username + " won by checkmate. Game over."
                ));
                finishedGameIds.add(gameData.gameId());
                connectionManager.broadcast(null, command.getGameID(),gameOver);
            } else if (game.isInStalemate(otherPlayer)) {
                String gameOver = serializer.toJson(new Notification(
                        "Game ended by stalemate. Game over."
                ));
                finishedGameIds.add(gameData.gameId());
                connectionManager.broadcast(null, command.getGameID(),gameOver);
            } else if (game.isInCheck(otherPlayer)) {
                String check = serializer.toJson(new Notification("Check!"));
                connectionManager.broadcast(ctx.session, command.getGameID(),check); //TODO: double check the specs for exclude
            }
        } catch (Exception e) {
            ctx.send(serializer.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
        //try to execute the move
        //send an error if it's not valid for any reason
        //update the game using the DAO.
        //send a loadgame message to ALL players in game.
        //Send checkmate or stalemate notifications if necessary.
    }
    private void leave(@NotNull WsMessageContext ctx, UserGameCommand command){
        try {
            String username = authDAO.getAuth(command.getAuthToken()).username();
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
            ServerMessage leaveMessage = new Notification(username + " has left the game");
            String leaveString = serializer.toJson(leaveMessage);
            connectionManager.broadcast(ctx.session,command.getGameID(),leaveString);
        } catch (Exception e) {
            ctx.send(serializer.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }
    private void resign(@NotNull WsMessageContext ctx, UserGameCommand command) {
        try {
            String username = authDAO.getAuth(command.getAuthToken()).username();
            if(command.getTeamColor() == null){
                String error = serializer.toJson(new ErrorMessage("An observer cannot resign."));
                ctx.send(error);
            }
            finishedGameIds.add(command.getGameID());
            String resignation = serializer.toJson(new Notification(username + " has resigned. Game over."));
            connectionManager.broadcast(null,command.getGameID(),resignation);
        }  catch (Exception e) {
            ctx.send(serializer.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private String parsePosition(ChessPosition position){
        String row = String.valueOf(position.getRow());
        String col = "";
        switch(position.getColumn()){
            case(1) -> col = "a";
            case(2) -> col = "b";
            case(3) -> col = "c";
            case(4) -> col = "d";
            case(5) -> col = "e";
            case(6) -> col = "f";
            case(7) -> col = "g";
            case(8) -> col = "h";
        }
        return row + col;
    }
    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }
}

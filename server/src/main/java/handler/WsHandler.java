package handler;

import chess.ChessGame;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WsHandler extends Handler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    /// TODO: Should this actually extend Handler? If it does, it gets:
    /// a serializer
    /// exception handling with error codes.
    /// yeah maybe I guess
    final private GameDAO gameDAO;

    public WsHandler(GameDAO gameDAO) {
        super();
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("Someone connected to websocket!");
        ctx.send("You have connected to Cam's server via websocket");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
        switch(command.getCommandType()){
            case CONNECT -> {
                ServerMessage loadGameMessage = new ServerMessage(
                        ServerMessage.ServerMessageType.LOAD_GAME,
                        new ChessGame());
                String loadGameJson = serializer.toJson(loadGameMessage);
                ctx.send(loadGameJson);
            }
            case MAKE_MOVE -> {
            }
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }
}

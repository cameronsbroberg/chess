package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import serverfacade.NotificationHandler;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;
import serverfacade.WsFacade;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class InGameClient extends Client{
    private final WsFacade wsFacade;
    private final String authToken;
    private int gameId;
    private ChessGame chessGame;
    private final ChessGame.TeamColor teamColor;
    private final NotificationHandler notificationHandler;
    private boolean promptedResign = false;

    public InGameClient(ServerFacade serverFacade, Repl repl, String authToken, int gameId,ChessGame.TeamColor teamColor) throws IOException {
        this.notificationHandler = new NotificationHandler(this);
        this.serverFacade = serverFacade;
        this.wsFacade = new WsFacade(serverFacade.getServerUrl(), this,notificationHandler);
        this.repl = repl;
        this.authToken = authToken;
        this.gameId = gameId;
        this.teamColor = teamColor;
        UserGameCommand connectCommand = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameId,
                teamColor
        );
        wsFacade.send(connectCommand);
    }

    @Override
    protected String whichPiece(int row, int col){
        ChessPosition atPosition = new ChessPosition(row,col);
        ChessPiece piece = chessGame.getBoard().getPiece(atPosition);
        if(piece == null){
            return EMPTY;
        }
        String pieceString = "";
        switch(piece.getTeamColor()){
            case WHITE -> {
                pieceString = SET_TEXT_COLOR_WHITE;
                switch(piece.getPieceType()){
                    case KING -> {
                        pieceString += WHITE_KING;
                    }
                    case QUEEN -> {
                        pieceString += WHITE_QUEEN;
                    }
                    case BISHOP -> {
                        pieceString += WHITE_BISHOP;
                    }
                    case KNIGHT -> {
                        pieceString += WHITE_KNIGHT;
                    }
                    case ROOK -> {
                        pieceString += WHITE_ROOK;
                    }
                    case PAWN -> {
                        pieceString += WHITE_PAWN;
                    }
                }
            }
            case BLACK -> {
                pieceString += SET_TEXT_COLOR_BLACK;
                switch(piece.getPieceType()){
                    case KING -> {
                        pieceString += BLACK_KING;
                    }
                    case QUEEN -> {
                        pieceString += BLACK_QUEEN;
                    }
                    case BISHOP -> {
                        pieceString += BLACK_BISHOP;
                    }
                    case KNIGHT -> {
                        pieceString += BLACK_KNIGHT;
                    }
                    case ROOK -> {
                        pieceString += BLACK_ROOK;
                    }
                    case PAWN -> {
                        pieceString += BLACK_PAWN;
                    }
                }
            }
        }
        return pieceString;
    }

    public void updateGame(ChessGame newGame){
        this.chessGame = newGame;
        System.out.println(chessBoard(this.teamColor));
        System.out.println("Type a command");
    }

    @Override
    String helpString() {
        if(teamColor == null){
            return SET_TEXT_COLOR_BLUE + "Redraw " + SET_TEXT_COLOR_BLACK + "--- to show the board again\n" +
                    SET_TEXT_COLOR_BLUE + "Leave " + SET_TEXT_COLOR_BLACK + "--- to leave the game\n" +
                    SET_TEXT_COLOR_BLUE + "Highlight " + SET_TEXT_COLOR_BLACK + "<PIECE COL> <PIECE ROW> --- to show legal moves\n" +
                    SET_TEXT_COLOR_BLUE + "Help " + SET_TEXT_COLOR_BLACK + "--- to get a list of commands" +
                    SET_TEXT_COLOR_BLUE + "\n";
        }
        return SET_TEXT_COLOR_BLUE + "Redraw " + SET_TEXT_COLOR_BLACK + "--- to show the board again\n" +
                SET_TEXT_COLOR_BLUE + "Leave " + SET_TEXT_COLOR_BLACK + "--- to leave the game\n" +
                SET_TEXT_COLOR_BLUE + "Move " + SET_TEXT_COLOR_BLACK + "<STARTING COL ROW> <ENDING COL ROW> <PROMOTION PIECE (if needed)> \n" +
                SET_TEXT_COLOR_BLUE + "Resign " + SET_TEXT_COLOR_BLACK + "--- to forfeit the game\n" +
                SET_TEXT_COLOR_BLUE + "Highlight " + SET_TEXT_COLOR_BLACK + "<PIECE ROW> <PIECE COL> --- to show legal moves\n" +
                SET_TEXT_COLOR_BLUE + "Help " + SET_TEXT_COLOR_BLACK + "--- to get a list of commands" +
                SET_TEXT_COLOR_BLUE + "\n";
    }

    private ChessMove parseMove(String[] tokens) throws IllegalArgumentException {
        ChessPiece.PieceType promotionPiece = null;
        if(tokens.length >= 4){
            promotionPiece = ChessPiece.PieceType.valueOf(tokens[3].toUpperCase());
        }
        return new ChessMove(
                parsePosition(tokens[1]),
                parsePosition(tokens[2]),
                promotionPiece
        );
    }

    private ChessPosition parsePosition (String token){
        int row = Integer.parseInt(String.valueOf(token.charAt(1)));
        int col;
        switch(Character.toUpperCase(token.charAt(0))){
            case('A') -> col = 1;
            case('B') -> col = 2;
            case('C') -> col = 3;
            case('D') -> col = 4;
            case('E') -> col = 5;
            case('F') -> col = 6;
            case('G') -> col = 7;
            case('H') -> col = 8;
            default -> throw new IllegalArgumentException();
        }
        return new ChessPosition(row,col);
    }

    private String highlightBoard(ChessPosition highlightPiece, ChessGame.TeamColor teamColor){
        if (chessGame.validMoves(highlightPiece) == null){
            return chessBoard(teamColor) + "No piece at selected square";
        }
        Collection<ChessMove> validMoves = chessGame.validMoves(highlightPiece);
        Collection<ChessPosition> highlightSpots = new ArrayList<>();
        for(ChessMove move : validMoves){
            highlightSpots.add(move.getEndPosition());
        }

        if(teamColor == null){
            teamColor = ChessGame.TeamColor.WHITE; //Show white side if observer
        }
        String boardString = "";
        boardString += horizontalBorder(teamColor);
        int row_start = (teamColor == ChessGame.TeamColor.WHITE ? 8 : 1);
        int row_end = (teamColor == ChessGame.TeamColor.WHITE ? 1 : 8);

        int row_inc = (teamColor == ChessGame.TeamColor.WHITE ? -1 : 1);

        int col_start = (teamColor == ChessGame.TeamColor.WHITE ? 1 : 8);
        int col_end = (teamColor == ChessGame.TeamColor.WHITE ? 8 : 1);

        int col_inc = (teamColor == ChessGame.TeamColor.WHITE ? 1 : -1);

        for(int i = row_start; (row_inc < 0 ? i >= row_end : i <= row_end); i = i + row_inc){ //i is the row
            boardString += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + i + " ";
            for (int j = col_start; (col_inc < 0 ? j >= col_end : j <= col_end); j = j + col_inc) { //j is the column
                String background = ((i + j) % 2 == 0 ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY);
                ChessPosition here = new ChessPosition(i,j);
                if(here.equals(highlightPiece)){
                    background = SET_BG_COLOR_DARK_YELLOW;
                }
                else if(highlightSpots.contains(here)){
                    background = ((i + j) % 2 == 0 ? SET_BG_COLOR_DARK_BLUE : SET_BG_COLOR_MEDIUM_BLUE);
                }
                boardString += background + " " + whichPiece(i,j) + " ";
            }
            boardString += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + " " + i;
            boardString += SET_BG_COLOR_WHITE + "\n";
        }
        boardString += horizontalBorder(teamColor);
        return boardString;
    }

    @Override
    String eval(String input) {
        try{
            var tokens = input.split(" ");
            String command = tokens[0].toLowerCase();
            if(!command.equals("resign")){
                promptedResign = false;
            }
            switch(command){
                case("help") -> {
                    return helpString();
                }
                case("redraw") -> {
                    return chessBoard(teamColor);
                }
                case("leave") -> {
                    UserGameCommand userGameCommand = new UserGameCommand(
                            UserGameCommand.CommandType.LEAVE,
                            authToken,
                            gameId,
                            teamColor
                    );
                    wsFacade.send(userGameCommand);
                    return enterPostLoginUi(authToken);
                }
                case("move") -> {
                    if(teamColor == null){
                        return "Unknown command. Try 'help' for options";
                    }
                    ChessMove chessMove = null;
                    try {
                        chessMove = parseMove(tokens);
                    } catch (IllegalArgumentException e) {
                        return "Invalid promotion piece. Use Queen, Rook, Bishop, or Knight";
                    }
                    UserGameCommand userGameCommand = new UserGameCommand(
                            UserGameCommand.CommandType.MAKE_MOVE,
                            authToken,
                            gameId,
                            teamColor,
                            chessMove
                    );
                    wsFacade.send(userGameCommand);
                    return "";
                }
                case("resign") -> {
                    if(teamColor == null){
                        return "Unknown command. Try 'help' for options";
                    }
                    if(!promptedResign){
                        promptedResign = true;
                        return "Are you sure you want to resign? If so, type resign again";
                    }
                    UserGameCommand resignation = new UserGameCommand(
                            UserGameCommand.CommandType.RESIGN,
                            authToken,
                            gameId,
                            teamColor
                    );
                    wsFacade.send(resignation);
                    return "";
                }
                case("highlight") -> {
                    ChessPosition piecePosition = parsePosition(tokens[1]);
                    return highlightBoard(piecePosition,teamColor);
                }
                default -> {
                    return "Unknown command. Try 'help' for options";
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return "Not enough arguments. Try the command again";
        }
        catch (ResponseException e) {
            return e.getMessage();
        }
        catch (Exception e) {
            return "Unknown error. Please try again";
        }
    }
    private String enterPostLoginUi(String authToken){
        Client client = new PostLoginClient(serverFacade,repl,authToken);
        repl.setClient(client);
        return client.helpString();
    }
}

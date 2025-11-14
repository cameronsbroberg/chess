package requests;

import chess.ChessGame;

public record CreateRequest(String authToken, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}

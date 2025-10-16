package requests;

import chess.ChessGame;

public record JoinRequest(String authToken, ChessGame.TeamColor teamColor, int gameID) {
}

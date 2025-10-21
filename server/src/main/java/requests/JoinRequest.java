package requests;

import chess.ChessGame;

public record JoinRequest(ChessGame.TeamColor teamColor, int gameID) {
}

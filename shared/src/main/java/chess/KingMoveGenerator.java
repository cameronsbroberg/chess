package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveGenerator {
    private final ChessBoard board;
    private final ChessPosition startingPosition;
    private Collection<ChessMove> validMoves = new ArrayList<>();
    private final ChessGame.TeamColor color;

    public KingMoveGenerator(ChessBoard board, ChessPosition startingPosition) {
        this.board = board;
        this.startingPosition = startingPosition;
        this.color = board.getPiece(startingPosition).getTeamColor();
    }

    private void checkValidity(int row, int col) {
        ChessPosition targetPosition = new ChessPosition(row, col);
        if (targetPosition.getRow() < 1 || targetPosition.getColumn() < 1 || targetPosition.getRow() > 8 || targetPosition.getColumn() > 8) {
            return;
        }
        if (board.getPiece(targetPosition) == null) {
            validMoves.add(new ChessMove(startingPosition, targetPosition, null));
        } else if (board.getPiece(targetPosition).getTeamColor() != color) {
            validMoves.add(new ChessMove(startingPosition, targetPosition, null));
        }
    }

    public Collection<ChessMove> getMoves() {
        int row = startingPosition.getRow();
        int col = startingPosition.getColumn();
        checkValidity(row + 1, col - 1);
        checkValidity(row + 1, col);
        checkValidity(row + 1, col + 1);
        checkValidity(row, col + 1);
        checkValidity(row - 1, col + 1);
        checkValidity(row - 1, col);
        checkValidity(row - 1, col - 1);
        checkValidity(row, col - 1);
        return validMoves;
    }
}

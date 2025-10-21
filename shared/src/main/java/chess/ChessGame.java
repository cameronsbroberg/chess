package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    public ChessGame() {
        setBoard(new ChessBoard());
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }
    public TeamColor getTeamTurn() {
        return teamTurn;
    }
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }
    public enum TeamColor {
        WHITE,
        BLACK
    }
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return null;
        }
        Collection<ChessMove> proposedMoves = piece.pieceMoves(board,startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for(ChessMove move : proposedMoves){
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece targetPiece = board.getPiece(endPosition);
            executeMove(move);
            if(!(isInCheck(piece.getTeamColor()))){
                legalMoves.add(move);
            }
            board.addPiece(startPosition,piece);
            board.addPiece(endPosition,targetPiece);
        }
        return legalMoves;
    }
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            throw new InvalidMoveException("No piece found at starting position");
        }
        if(piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("Not your turn");
        }
        Collection<ChessMove> legalMoves = validMoves(startPosition);
        if(!legalMoves.contains(move)){
            throw new InvalidMoveException("Not a legal move");
        }
        executeMove(move);
        if(teamTurn == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else{
            setTeamTurn(TeamColor.WHITE);
        }
    }
    private void executeMove(ChessMove move){
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        board.addPiece(startPosition,null);
        if(move.getPromotionPiece() == null){
            board.addPiece(endPosition,piece);
        }
        else{
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(),move.getPromotionPiece());
            board.addPiece(endPosition,promotedPiece);
        }
    }
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition myKingPosition = getKingPosition(teamColor);
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition spaceToCheck = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(spaceToCheck);
                if(piece == null){
                    continue;
                }
                if(piece.getTeamColor() != teamColor){
                    Collection<ChessMove> enemyMoves = piece.pieceMoves(board,spaceToCheck);
                    if(enemyPieceCanTakeKing(enemyMoves,myKingPosition)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean enemyPieceCanTakeKing(Collection<ChessMove> enemyMoves, ChessPosition myKingPosition){
        for (ChessMove enemyMove : enemyMoves){
            if(enemyMove.getEndPosition().equals(myKingPosition)){
                return true;
            }
        }
        return false;
    }
    private Collection<ChessMove> getTeamMoves(TeamColor teamColor){
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition spaceToCheck = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(spaceToCheck);
                if(piece == null){
                    continue;
                }
                if(piece.getTeamColor() == teamColor){
                    legalMoves.addAll(validMoves(spaceToCheck));
                }
            }
        }
        return legalMoves;
    }
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return getTeamMoves(teamColor).isEmpty();
        }
        return false;
    }
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return getTeamMoves(teamColor).isEmpty();
        }
        return false;
    }
    public void setBoard(ChessBoard board) {
        this.board = board;
    }
    public ChessBoard getBoard() {
        return this.board;
    }
    private ChessPosition getKingPosition(TeamColor teamColor) {
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(currentPosition);
                if(piece == null){
                    continue;
                }
                if(piece.getPieceType() == ChessPiece.PieceType.KING){
                    if(piece.getTeamColor() == teamColor){
                        return(currentPosition);
                    }
                }
            }
        }
        throw new RuntimeException("King not found when checking for check");
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && board.equals(chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}

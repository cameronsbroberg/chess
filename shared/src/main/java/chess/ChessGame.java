package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;
    public ChessGame() {
        setBoard(new ChessBoard());
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
        setWhiteKingPosition(new ChessPosition(1,5));
        setBlackKingPosition(new ChessPosition(8,5));
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return null;
        }
        Collection<ChessMove> proposedMoves = piece.pieceMoves(board,startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessPosition initialWhiteKingPosition = new ChessPosition(getWhiteKingPosition().getRow(),getWhiteKingPosition().getColumn());
        ChessPosition initialBlackKingPosition = new ChessPosition(getBlackKingPosition().getRow(), getBlackKingPosition().getColumn());
        Collection<ChessMove> illegalMoves = new ArrayList<>();
        for(ChessMove move : proposedMoves){
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece targetPiece = board.getPiece(endPosition);
            executeMove(move);
            if(!(isInCheck(piece.getTeamColor()))){
                legalMoves.add(move);
            }
            else{
                illegalMoves.add(move);
            }
            //Undo the move:
            board.addPiece(startPosition,piece);
            board.addPiece(endPosition,targetPiece);
            setWhiteKingPosition(initialWhiteKingPosition);
            setBlackKingPosition(initialBlackKingPosition);
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
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
        executeMove(move);//makeMove checks legality, executeMove carries it out.
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
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == TeamColor.WHITE){
                setWhiteKingPosition(endPosition);
            }
            else{
                setBlackKingPosition(endPosition);
            }
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition myKingPosition;
        if(teamColor == TeamColor.BLACK){
            myKingPosition = getBlackKingPosition();
        }
        else{
            myKingPosition = getWhiteKingPosition();
        }
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition spaceToCheck = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(spaceToCheck);
                if(piece == null){
                    continue;
                }
                if(piece.getTeamColor() != teamColor){
                    Collection<ChessMove> enemyMoves = piece.pieceMoves(board,spaceToCheck);
                    for (ChessMove enemyMove : enemyMoves){
                        if(enemyMove.getEndPosition().equals(myKingPosition)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public ChessPosition getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public void setWhiteKingPosition(ChessPosition whiteKingPosition) {
        this.whiteKingPosition = whiteKingPosition;
    }

    public ChessPosition getBlackKingPosition() {
        return blackKingPosition;
    }

    public void setBlackKingPosition(ChessPosition blackKingPosition) {
        this.blackKingPosition = blackKingPosition;
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;
    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
        this.whiteKingPosition = new ChessPosition(1,5);
        this.blackKingPosition = new ChessPosition(8,5);
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
        for(ChessMove move : proposedMoves){
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece targetPiece = board.getPiece(endPosition);
            executeMove(move);
            if(!isInCheck(piece.getTeamColor())){
                legalMoves.add(move);
            }
            //Undo the move:
            ChessMove reverseMove = new ChessMove(endPosition,startPosition,piece.getPieceType());
            executeMove(reverseMove);
            board.addPiece(endPosition,targetPiece);
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
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            throw new InvalidMoveException("No piece found at starting position");
        }
        if(piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("Not your turn");
        }
        Collection<ChessMove> legalMoves = piece.pieceMoves(board,startPosition);
        if(!legalMoves.contains(move)){
            throw new InvalidMoveException("Not a legal move");
        }
        executeMove(move); //makeMove checks legality, executeMove carries it out.
    }
    private void executeMove(ChessMove move){
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        board.addPiece(startPosition,null); //Does this work?
        if(move.getPromotionPiece() == null){
            board.addPiece(endPosition,piece);
        }
        else{
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(),move.getPromotionPiece());
            board.addPiece(endPosition,promotedPiece);
        }
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == TeamColor.WHITE){
                whiteKingPosition = endPosition;
            }
            else{
                blackKingPosition = endPosition;
            }
        }
        if(teamTurn == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else{
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition myKingPosition = whiteKingPosition;
        if(teamColor == TeamColor.BLACK){
            myKingPosition = blackKingPosition;
        } //FIXME This probably might work lol
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition spaceToCheck = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(spaceToCheck);
                if(piece == null){
                    continue;
                }
                if(piece.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = piece.pieceMoves(board,spaceToCheck);
                    for (ChessMove move : moves){
                        if(move.getEndPosition() == myKingPosition){
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
}

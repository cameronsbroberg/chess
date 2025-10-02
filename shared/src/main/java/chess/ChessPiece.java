package chess;

import java.util.Collection;
import java.util.Objects;

public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch(type){
            case KING -> {
                return new KingMoveGenerator(board,this,myPosition).getMoves();
            }
            case QUEEN -> {
                Collection<ChessMove> moves = new BishopMoveGenerator(board,this,myPosition).getMoves();
                moves.addAll(new RookMoveGenerator(board,this,myPosition).getMoves());
                return moves;
            }
            case BISHOP -> {
                return new BishopMoveGenerator(board,this,myPosition).getMoves();
            }
            case KNIGHT -> {
                return new KnightMoveGenerator(board,this,myPosition).getMoves();
            }
            case ROOK -> {
                return new RookMoveGenerator(board,this,myPosition).getMoves();
            }
            case PAWN -> {
                return new PawnMoveGenerator(board,this,myPosition).getMoves();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        switch(this.getTeamColor()){
            case WHITE -> {
                switch(this.getPieceType()){
                    case KING -> {
                        return " K  ";
                    }
                    case QUEEN -> {
                        return " Q  ";
                    }
                    case BISHOP -> {
                        return " B  ";
                    }
                    case KNIGHT -> {
                        return " N  ";
                    }
                    case ROOK -> {
                        return " R  ";
                    }
                    case PAWN -> {
                        return " P  ";
                    }
                }
            }
            case BLACK -> {
                switch(this.getPieceType()){
                    case KING -> {
                        return " k  ";
                    }
                    case QUEEN -> {
                        return " q  ";
                    }
                    case BISHOP -> {
                        return " b  ";
                    }
                    case KNIGHT -> {
                        return " n  ";
                    }
                    case ROOK -> {
                        return " r  ";
                    }
                    case PAWN -> {
                        return " p  ";
                    }
                }
            }
        }
        return "NO PIECE TYPE";
    }
}
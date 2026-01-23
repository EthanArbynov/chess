package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) {
            return moves;
        }

        PieceType type = piece.getPieceType();
        ChessGame.TeamColor myColor = piece.getTeamColor();

        if (type == PieceType.BISHOP) {
            addLineMoves(board, myPosition, moves, myColor, 1, -1);
            addLineMoves(board, myPosition, moves, myColor, 1, 1);
            addLineMoves(board, myPosition, moves, myColor, -1, 1);
            addLineMoves(board, myPosition, moves, myColor, -1, -1);
        }
        else if (type == PieceType.ROOK) {
            addLineMoves(board, myPosition, moves, myColor, 0, 1);
            addLineMoves(board, myPosition, moves, myColor, 0, -1);
            addLineMoves(board, myPosition, moves, myColor, -1, 0);
            addLineMoves(board, myPosition, moves, myColor, 1, 0);
        }
        else if (type == PieceType.QUEEN) {
            addLineMoves(board, myPosition, moves, myColor, 0, 1);
            addLineMoves(board, myPosition, moves, myColor, 0, -1);
            addLineMoves(board, myPosition, moves, myColor, -1, 0);
            addLineMoves(board, myPosition, moves, myColor, 1, 0);
            addLineMoves(board, myPosition, moves, myColor, 1, -1);
            addLineMoves(board, myPosition, moves, myColor, 1, 1);
            addLineMoves(board, myPosition, moves, myColor, -1, 1);
            addLineMoves(board, myPosition, moves, myColor, -1, -1);
        }
        else if (type == PieceType.KNIGHT) {
            int[][] deltas = {{2, 1}, {-2, 1}, {2, -1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
            for (int[] d : deltas) {
                int r = myPosition.getRow() + d[0];
                int c = myPosition.getColumn() + d[1];
                if (inBounds(r, c)) {
                    addMoveIfEmptyOrCapture(board, myPosition, moves, myColor, r, c);
                }
            }
        }
        else if (type == PieceType.KING) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;

                    int r = myPosition.getRow() + dr;
                    int c = myPosition.getColumn() + dc;
                    if (inBounds(r, c)) {
                        addMoveIfEmptyOrCapture(board, myPosition, moves, myColor, r, c);
                    }
                }
            }
        }
        else if (type == PieceType.PAWN) {
            addPawnMoves(board, myPosition, moves, myColor);
        }

        return moves;
    }

    private void addLineMoves(ChessBoard board, ChessPosition from, List<ChessMove> moves, ChessGame.TeamColor myColor, int dr, int dc) {
        int r = from.getRow() + dr;
        int c = from.getColumn() + dc;

        while (inBounds(r, c)) {
            ChessPosition to = new ChessPosition(r, c);
            ChessPiece target = board.getPiece(to);

            if (target == null) {
                moves.add(new ChessMove(from, to, null));
            }
            else {
                if (target.getTeamColor() != myColor) {
                    moves.add(new ChessMove(from, to, null));
                }
                break;
            }
            r+= dr;
            c+= dc;
        }
    }

    private void addMoveIfEmptyOrCapture(ChessBoard board, ChessPosition from, List<ChessMove> moves, ChessGame.TeamColor myColor, int r, int c) {
        ChessPosition to = new ChessPosition(r,c);
        ChessPiece target = board.getPiece(to);

        if (target == null || target.getTeamColor() != myColor) {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition from, List<ChessMove> moves, ChessGame.TeamColor myColor) {
        int dir = (myColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (myColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promoteRow = (myColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int oneRow = from.getRow() + dir;
        int col = from.getColumn();

        if (inBounds(oneRow, col) && board.getPiece(new ChessPosition(oneRow, col)) == null) {
            addPawnMoveMaybePromote(from, new ChessPosition(oneRow, col), promoteRow, moves);

            if (from.getRow() == startRow) {
                int twoRow = from.getRow() + 2 * dir;
                if (inBounds(twoRow, col) && board.getPiece(new ChessPosition(twoRow, col)) == null) {
                    moves.add(new ChessMove(from, new ChessPosition(twoRow, col), null));
                }
            }
        }

        int[] captureCols = {col - 1, col + 1};
        for (int cc : captureCols) {
            int rr = from.getRow() + dir;
            if (!inBounds(rr, cc)) continue;

            ChessPosition to = new ChessPosition(rr, cc);
            ChessPiece target = board.getPiece(to);

            if (target != null && target.getTeamColor() != myColor) {
                addPawnMoveMaybePromote(from, to, promoteRow, moves);
            }
        }
    }

    private void addPawnMoveMaybePromote(ChessPosition from, ChessPosition to, int promoteRow, List<ChessMove> moves) {
        if (to.getRow() == promoteRow) {
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
        }
        else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private boolean inBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}

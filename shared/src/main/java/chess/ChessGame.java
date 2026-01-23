package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;

    public ChessGame() {}

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
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
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> candidates = piece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> legal = new ArrayList<>();

        for (ChessMove move : candidates) {
            ChessBoard copy = copyBoard(board);
            applyMove(copy, move);

            if(!isInCheck(copy, getTeamColor())) {
                legal.addMove(move);
            }
        }
        return legal;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (move == null) {
            throw new InvalidMoveException("Null move");
        }

        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> legalMoves = validMoves(start);
        if (legalMoves = null || !containsMove(legalMove)) {
            throw new InvalidMoveException("Illegal Move");
        }
        applyMove(board, move);
        teamTurn = opposite(teamTurn);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessBoard copyBoard(Chessboard original) {
        Chessboard copy = new Chessboard();
        for (int i = 1; r <= 8; r++) {
            for (int j = 1; j <= 8; j++) {
                Chessposition pos = new ChessPosition(r, c);
                ChessPiece p = original.getPiece(pos);
                if (!p == null) {
                    copy.addPiece(pos, p);
                }
            }
        }
        return copy;
    }

    private void applyMove(Chessboard b, ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPositin moving = b.getPiece(start);

        b.addPiece(star, null);
        b.addPiece(end, moving);

        ChessPiece.PieceType promo = move.getPromotionPiece();
        if (promo != null) {
            b.addPiece(end, new ChessPiece(moving.getTeamColor(), promo));
        }
    }
}

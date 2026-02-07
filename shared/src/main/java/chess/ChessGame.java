package chess;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;

    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteLeftRookMoved = false;
    private boolean whiteRightRookMoved = false;
    private boolean blackLeftRookMoved = false;
    private boolean blackRightRookMoved = false;

    private ChessPosition enPassantSquare = null;
    private ChessPosition enPassantPawnPos = null;
    private TeamColor enPassantPawnColor = null;

    public ChessGame() {
        board.resetBoard();
    }

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

            if (!isInCheck(copy, piece.getTeamColor())) {
                legal.add(move);
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            TeamColor color = piece.getTeamColor();

            if (color == TeamColor.WHITE && startPosition.equals(new ChessPosition(1, 5))) {
                if (!whiteKingMoved && !isInCheck(TeamColor.WHITE)) {
                    if (!whiteRightRookMoved && board.getPiece(new ChessPosition(1, 6)) == null &&
                            board.getPiece(new ChessPosition(1, 7)) == null && isSquareSafe(TeamColor.WHITE, new ChessPosition(1, 6)) &&
                    isSquareSafe(TeamColor.WHITE, new ChessPosition(1, 7)) && isWhiteRookAt(1, 8)) {
                        legal.add(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null));
                    }

                    if (!whiteLeftRookMoved && board.getPiece(new ChessPosition(1, 4)) == null &&
                            board.getPiece(new ChessPosition(1, 3)) == null && board.getPiece(new ChessPosition(1, 2)) == null && isSquareSafe(TeamColor.WHITE, new ChessPosition(1, 4)) &&
                            isSquareSafe(TeamColor.WHITE, new ChessPosition(1, 3)) && isWhiteRookAt(1, 1)) {
                        legal.add(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null));
                    }
                }
            }

            if (color == TeamColor.BLACK && startPosition.equals(new ChessPosition(8, 5))) {
                if (!blackKingMoved && !isInCheck(TeamColor.BLACK)) {
                    if (!blackRightRookMoved && board.getPiece(new ChessPosition(8, 6)) == null &&
                            board.getPiece(new ChessPosition(8, 7)) == null && isSquareSafe(TeamColor.BLACK, new ChessPosition(8, 6)) &&
                            isSquareSafe(TeamColor.BLACK, new ChessPosition(8, 7)) && isBlackRookAt(8, 8)) {
                        legal.add(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null));
                    }

                    if (!blackLeftRookMoved && board.getPiece(new ChessPosition(8, 4)) == null &&
                            board.getPiece(new ChessPosition(8, 3)) == null && board.getPiece(new ChessPosition(8, 2)) == null &&
                            isSquareSafe(TeamColor.BLACK, new ChessPosition(8, 4)) &&
                            isSquareSafe(TeamColor.BLACK, new ChessPosition(8, 3)) && isBlackRookAt(8, 1)) {
                        legal.add(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null));
                    }
                }
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            addEnPassantMoves(startPosition, piece.getTeamColor(), legal);
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

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> legalMoves = validMoves(start);
        if (legalMoves == null || !containsMove(legalMoves, move)) {
            throw new InvalidMoveException("Illegal move");
        }

        updateCastleFlags(move, piece);
        applyMove(board, move);
        updateEnPassantState(move, piece);
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
        if (!isInCheck(teamColor)) {
            return false;
        }
        return !hasAnyLegalMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return !hasAnyLegalMove(teamColor);
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

    private ChessBoard copyBoard(ChessBoard original) {
        ChessBoard copy = new ChessBoard();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece p = original.getPiece(pos);
                if (p != null) {
                    copy.addPiece(pos, p);
                }
            }
        }

        return copy;
    }

    private void applyMove(ChessBoard b, ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece moving = b.getPiece(start);

        if (moving != null && moving.getPieceType() == ChessPiece.PieceType.PAWN && start.getColumn() != end.getColumn() &&
            b.getPiece(end) == null && end.equals(enPassantSquare) && enPassantPawnPos != null) {
                b.addPiece(enPassantPawnPos, null);
            }


        b.addPiece(start, null);
        b.addPiece(end, moving);

        ChessPiece.PieceType promo = move.getPromotionPiece();
        if (promo != null) {
            b.addPiece(end, new ChessPiece(moving.getTeamColor(), promo));
        }

        if (moving != null && moving.getPieceType() == ChessPiece.PieceType.KING &&
                start.getRow() == end.getRow() && Math.abs(end.getColumn() - start.getColumn()) == 2) {
            int row = start.getRow();

            if (end.getColumn() == 7) {
                ChessPiece rook = b.getPiece(new ChessPosition(row, 8));
                b.addPiece(new ChessPosition(row, 8), null);
                b.addPiece(new ChessPosition(row, 6), rook);
            }

            if (end.getColumn() == 3) {
                ChessPiece rook = b.getPiece(new ChessPosition(row, 1));
                b.addPiece(new ChessPosition(row, 1), null);
                b.addPiece(new ChessPosition(row, 4), rook);
            }
        }
    }

    private boolean containsMove(Collection<ChessMove> moves, ChessMove wanted) {
        for (ChessMove m : moves) {
            if (m.equals(wanted)) {
                return true;
            }
        }
        return false;
    }

    private TeamColor opposite(TeamColor team) {
        if (team == TeamColor.WHITE) {
            return TeamColor.BLACK;
        } else {
            return TeamColor.WHITE;
        }
    }

    private boolean hasAnyLegalMove(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece p = board.getPiece(pos);

                if (p != null && p.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isInCheck(ChessBoard b, TeamColor teamColor) {
        ChessPosition kingPos = findKing(b, teamColor);
        TeamColor enemy = opposite(teamColor);

        if (kingPos == null) {
            return true;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition from = new ChessPosition(i, j);
                ChessPiece enemyPiece = b.getPiece(from);

                if (enemyPiece != null && enemyPiece.getTeamColor() == enemy) {
                    Collection<ChessMove> attacks = enemyPiece.pieceMoves(b, from);
                    for (ChessMove m : attacks) {
                        if (m.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private ChessPosition findKing(ChessBoard b, TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece p = b.getPiece(pos);

                if (p != null &&
                        p.getTeamColor() == teamColor &&
                        p.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    private boolean isWhiteRookAt(int row, int col) {
        ChessPiece p = board.getPiece(new ChessPosition(row, col));
        return p != null && p.getTeamColor() == TeamColor.WHITE && p.getPieceType() == ChessPiece.PieceType.ROOK;
    }

    private boolean isBlackRookAt(int row, int col) {
        ChessPiece p = board.getPiece(new ChessPosition(row, col));
        return p != null && p.getTeamColor() == TeamColor.BLACK && p.getPieceType() == ChessPiece.PieceType.ROOK;
    }

    private boolean isSquareSafe(TeamColor color, ChessPosition kingSquare) {
        ChessBoard copy = copyBoard(board);
        ChessPosition from = (color == TeamColor.WHITE) ? new ChessPosition(1, 5) : new ChessPosition(8, 5);
        ChessPiece king = copy.getPiece(from);
        copy.addPiece(from, null);
        copy.addPiece(kingSquare, king);
        return !isInCheck(copy, color);
    }

    private void updateCastleFlags(ChessMove move, ChessPiece moving) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        if (moving.getPieceType() == ChessPiece.PieceType.KING) {
            if (moving.getTeamColor() == TeamColor.WHITE) {
                whiteKingMoved = true;
            }
            else {
                blackKingMoved = true;
            }
        }

        if (moving.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (moving.getTeamColor() == TeamColor.WHITE) {
                if (start.equals(new ChessPosition(1, 1))) {
                    whiteLeftRookMoved = true;
                }
                if (start.equals(new ChessPosition(1, 8))) {
                    whiteRightRookMoved = true;
                }
            }
            else {
                if (start.equals(new ChessPosition(8, 1))) {
                    blackLeftRookMoved = true;
                }
                if (start.equals(new ChessPosition(8, 8))) {
                    blackRightRookMoved = true;
                }
            }
        }
        ChessPiece captured = board.getPiece(end);
        if (captured != null && captured.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (captured.getTeamColor() == TeamColor.WHITE) {
                if (end.equals(new ChessPosition(1, 1))) {
                    whiteLeftRookMoved = true;
                }
                if (end.equals(new ChessPosition(1, 8))) {
                    whiteRightRookMoved = true;
                }
            }
            else {
                if (end.equals(new ChessPosition(8, 1))) {
                    blackLeftRookMoved = true;
                }
                if (end.equals(new ChessPosition(8, 8))) {
                    blackRightRookMoved = true;
                }
            }
        }
    }

    private void addEnPassantMoves(ChessPosition from, TeamColor myColor, Collection<ChessMove> legal) {
        if (enPassantSquare == null) return;
        if (enPassantPawnColor == null) return;
        if (enPassantPawnPos == null) return;
        if (myColor == enPassantPawnColor) return;

        int dir = (myColor == TeamColor.WHITE) ? 1 : -1;
        int fromRow = from.getRow();
        int fromCol = from.getColumn();
        int toRow = enPassantSquare.getRow();
        int toCol = enPassantSquare.getColumn();

        if (toRow != fromRow + dir) return;
        if (Math.abs(toCol - fromCol) != 1) return;
        if (board.getPiece(enPassantSquare) != null) return;

        ChessPiece victim = board.getPiece(enPassantPawnPos);
        if (victim == null) return;
        if (victim.getPieceType() != ChessPiece.PieceType.PAWN) return;
        if (victim.getTeamColor() != enPassantPawnColor) return;
        if (enPassantPawnPos.getRow() != fromRow) return;
        if (Math.abs(enPassantPawnPos.getColumn() - fromCol) != 1) return;

        ChessMove epMove = new ChessMove(from, enPassantSquare, null);
        ChessBoard copy = copyBoard(board);
        applyMove(copy, epMove);

        if (!isInCheck(copy, myColor)) {
            legal.add(epMove);
        }
    }

    private void updateEnPassantState(ChessMove move, ChessPiece moving) {
        enPassantSquare = null;
        enPassantPawnPos = null;
        enPassantPawnColor = null;

        if (moving.getPieceType() != ChessPiece.PieceType.PAWN) return;

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        if (start.getColumn() != end.getColumn()) return;

        int dr = end.getRow() - start.getRow();
        if (Math.abs(dr) != 2) return;

        int midRow = (start.getRow() + end.getRow()) /2;
        int col = start.getColumn();

        enPassantSquare = new ChessPosition(midRow, col);
        enPassantPawnPos = end;
        enPassantPawnColor = moving.getTeamColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessGame other)) {
            return false;
        }
        return teamTurn == other.teamTurn && Objects.equals(board, other.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
package client;

import chess.*;
import ui.EscapeSequences;
import java.util.Collection;

public class BoardPrinter {

    public static void drawBoard(ChessBoard board, boolean blackPerspective) {
        int[] rows = blackPerspective
                ? new int[]{1,2,3,4,5,6,7,8}
                : new int[]{8,7,6,5,4,3,2,1};

        int[] cols = blackPerspective
                ? new int[]{8,7,6,5,4,3,2,1}
                : new int[]{1,2,3,4,5,6,7,8};

        printColumnLabels(cols);

        for (int row : rows) {
            System.out.print(" " + row);

            for (int col : cols) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                boolean light = (row + col) % 2 != 0;
                String bgColor = light
                        ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                String pieceText = getPieceText(piece);

                System.out.print(bgColor);
                System.out.print(pieceText);
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(" " + row);
        }

        printColumnLabels(cols);
    }

    private static void printColumnLabels(int[] cols) {
        System.out.print("   ");
        for (int col : cols) {
            char letter = (char) ('a' + col - 1);
            System.out.print(" " + letter + " ");
        }
        System.out.println();
    }

    private static String getPieceText(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        }
    }

    public static void drawBoardWithHighlights(ChessBoard board,
                                               boolean blackPerspective,
                                               ChessPosition selected,
                                               Collection<ChessMove> legalMoves) {
        int[] rows = blackPerspective
                ? new int[]{1,2,3,4,5,6,7,8}
                : new int[]{8,7,6,5,4,3,2,1};

        int[] cols = blackPerspective
                ? new int[]{8,7,6,5,4,3,2,1}
                : new int[]{1,2,3,4,5,6,7,8};

        printColumnLabels(cols);

        for (int row : rows) {
            System.out.print(" " + row);

            for (int col : cols) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                String bgColor;

                if (isSelectedSquare(row, col, selected)) {
                    bgColor = EscapeSequences.SET_BG_COLOR_YELLOW;
                } else if (isLegalMoveSquare(row, col, legalMoves)) {
                    bgColor = EscapeSequences.SET_BG_COLOR_GREEN;
                } else {
                    boolean light = (row + col) % 2 != 0;
                    bgColor = light
                            ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                            : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                }

                String pieceText = getPieceText(piece);

                System.out.print(bgColor);
                System.out.print(pieceText);
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(" " + row);
        }

        printColumnLabels(cols);
    }

    private static boolean isSelectedSquare(int row, int col, ChessPosition selected) {
        return selected != null
                && selected.getRow() == row
                && selected.getColumn() == col;
    }

    private static boolean isLegalMoveSquare(int row, int col, Collection<ChessMove> legalMoves) {
        if (legalMoves == null) {
            return false;
        }

        for (ChessMove move : legalMoves) {
            if (move.getEndPosition().getRow() == row &&
                    move.getEndPosition().getColumn() == col) {
                return true;
            }
        }

        return false;
    }
}

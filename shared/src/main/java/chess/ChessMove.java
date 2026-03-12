package chess;

public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessPosition getStartPosition() {
        return startPosition;
    }

    public ChessPosition getEndPosition() {
        return endPosition;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        return String.format("[%s%s]", startPosition, endPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessMove other)) {
            return false;
        }

        if (!startPosition.equals(other.startPosition)) {
            return false;
        }
        if (!endPosition.equals(other.endPosition)) {
            return false;
        }

        if (promotionPiece == null && other.promotionPiece == null) {
            return true;
        }
        if (promotionPiece == null || other.promotionPiece == null) {
            return false;
        }

        return promotionPiece == other.promotionPiece;
    }

    @Override
    public int hashCode() {
        int result = startPosition.hashCode();
        result = 31 * result + endPosition.hashCode();
        result = 31 * result + (promotionPiece == null ? 0 : promotionPiece.hashCode());
        return result;
    }
}

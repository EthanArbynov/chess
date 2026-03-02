package handler;

public class GameSummary {
    public int gameID;
    public String whiteUsername;
    public String blackUsername;
    public String gameName;

    public GameSummary(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }
}

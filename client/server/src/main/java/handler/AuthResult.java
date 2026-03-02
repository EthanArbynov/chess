package handler;

public class AuthResult {
    public String username;
    public String authToken;

    public AuthResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }
}

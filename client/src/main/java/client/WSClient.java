package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
import org.glassfish.tyrus.client.ClientManager;
import websocket.commands.UserGameCommand;

import java.net.URI;

@ClientEndpoint
public class WSClient {

    private Session session;
    private final Gson gson = new Gson();
    private final String authToken;
    private final int gameID;

    public WSClient(String authToken, int gameID) {
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void connect() throws Exception {
        ClientManager client = ClientManager.createClient();
        client.connectToServer(this, new URI("ws://localhost:8080/ws"));
    }

    @OnOpen
    public void onOpen(Session session) throws Exception {
        this.session = session;

        UserGameCommand command =
                new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);

        session.getBasicRemote().sendText(gson.toJson(command));
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }

    public void send(String json) throws Exception {
        session.getBasicRemote().sendText(json);
    }

    public void close() throws Exception {
        if (session != null) {
            session.close();
        }
    }
}
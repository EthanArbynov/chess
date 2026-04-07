package client;

import chess.ChessMove;
import chess.ChessGame;
import websocket.commands.MakeMoveCommand;
import com.google.gson.Gson;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;

@ClientEndpoint
public class WSClient {

    private Session session;
    private final Gson gson = new Gson();
    private final String authToken;
    private final int gameID;
    private final boolean blackPerspective;
    private ChessGame currentGame;

    public WSClient(String authToken, int gameID, boolean blackPerspective) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.blackPerspective = blackPerspective;
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
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
            ChessGame game = loadGameMessage.getGame();
            currentGame = game;
            BoardPrinter.drawBoard(game.getBoard(), blackPerspective);


        } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
            System.out.println(errorMessage.getErrorMessage());

        } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
            System.out.println(notificationMessage.getMessage());
        }
    }

    public void close() throws Exception {
        if (session != null) {
            session.close();
        }
    }

    public void sendLeave() throws Exception {
        UserGameCommand command =
                new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void sendResign() throws Exception {
        UserGameCommand command =
                new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void sendMove(ChessMove move) throws Exception {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public ChessGame getCurrentGame() {
        return currentGame;
    }

    public boolean isBlackPerspective() {
        return blackPerspective;
    }

}
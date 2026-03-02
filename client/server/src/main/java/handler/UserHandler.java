package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;

import java.util.HashMap;
import java.util.Map;

public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            UserData user = gson.fromJson(ctx.body(), UserData.class);
            UserService.RegisterResult result = userService.register(user);
            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e) {
            String msg = e.getMessage();

            Map<String, String> resp = new HashMap<>();

            if (msg.equals("bad request")) {
                resp.put("message", "Error: bad request");
                ctx.status(400);
                ctx.json(resp);
                return;
            }

            if (msg.equals("already taken")) {
                resp.put("message", "Error: already taken");
                ctx.status(403);
                ctx.json(resp);
                return;
            }

            resp.put("message", "Error: " + msg);
            ctx.status(500);
            ctx.json(resp);

        } catch (Exception e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("message", "Error: " + e.getMessage());
            ctx.status(500);
            ctx.json(resp);
        }
    }
}

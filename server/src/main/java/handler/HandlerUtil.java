package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class HandlerUtil {

    public static void sendError(Context ctx, int statusCode, String message) {
        Map<String, String> resp = new HashMap<>();
        resp.put("message", "Error: " + message);
        ctx.status(statusCode);
        ctx.json(resp);
    }

    public static void handleUnauthorizedOnly(Context ctx, DataAccessException e) {
        String msg = e.getMessage();

        if ("unauthorized".equals(msg)) {
            sendError(ctx, 401, "unauthorized");
            return;
        }

        sendError(ctx, 500, msg);
    }
}
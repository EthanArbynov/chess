package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;

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
            handleRegisterError(ctx, e);
        } catch (Exception e) {
            HandlerUtil.sendError(ctx, 500, e.getMessage());
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);

            if (req == null) {
                HandlerUtil.sendError(ctx, 400, "bad request");
                return;
            }

            AuthResult result = userService.login(req.username, req.password);
            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e) {
            handleLoginError(ctx, e);
        } catch(Exception e) {
            HandlerUtil.sendError(ctx, 500, e.getMessage());
        }
    }

    public void logout(Context ctx) {
        try {
            String token = ctx.header("authorization");
            userService.logout(token);
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            handleLogoutError(ctx, e);
        } catch(Exception e) {
            HandlerUtil.sendError(ctx, 500, e.getMessage());
        }
    }

    private void handleRegisterError(Context ctx, DataAccessException e) {
        String msg = e.getMessage();

        if ("bad request".equals(msg)) {
            HandlerUtil.sendError(ctx, 400, "bad request");
            return;
        }

        if ("forbidden".equals(msg)) {
            HandlerUtil.sendError(ctx, 403, "forbidden");
            return;
        }

        HandlerUtil.sendError(ctx, 500, msg);
    }

    private void handleLoginError(Context ctx, DataAccessException e) {
        String msg = e.getMessage();

        if ("bad request".equals(msg)) {
            HandlerUtil.sendError(ctx, 400, "bad request");
            return;
        }

        if ("unauthorized".equals(msg)) {
            HandlerUtil.sendError(ctx, 401, "unauthorized");
            return;
        }

        HandlerUtil.sendError(ctx, 500, msg);
    }

    private void handleLogoutError(Context ctx, DataAccessException e) {
        String msg = e.getMessage();

        if ("unauthorized".equals(msg)) {
            HandlerUtil.sendError(ctx, 401, "unauthorized");
            return;
        }

        HandlerUtil.sendError(ctx, 500, msg);
    }
}

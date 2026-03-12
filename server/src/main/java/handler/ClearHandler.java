package handler;

import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear (Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (Exception e) {
            HandlerUtil.sendError(ctx, 500, e.getMessage());
        }
    }
}

package handler;

import io.javalin.http.Context;
import service.ClearService;
import java.util.HashMap;
import java.util.Map;

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
            Map<String, String> resp = new HashMap<>();
            resp.put("message", "Error: " + e.getMessage());
            ctx.status(500);
            ctx.json(resp);
        }
    }
}

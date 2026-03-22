package client;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String register(String username, String password, String email) throws Exception {
        var request = new RegisterRequest(username, password, email);
        var response = makeRequest("POST", "/user", request, null, AuthResponse.class);
        return response.authToken();
    }

    public String login(String username, String password) throws Exception {
        var request = new LoginRequest(username, password);
        var response = makeRequest("POST", "/session", request, null, AuthResponse.class);
        return response.authToken();
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    private <T> T makeRequest(String method, String path, Object requestBody, String authToken,
                              Class<T> responseClass) throws Exception {
        URL url = new URI(serverUrl + path).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setDoInput(true);

        if (authToken != null) {
            http.setRequestProperty("authorization", authToken);
        }

        if (requestBody != null) {
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");

            String json = gson.toJson(requestBody);
            try (OutputStream outputStream = http.getOutputStream()) {
                outputStream.write(json.getBytes());
            }
        }

        http.connect();

        int status = http.getResponseCode();
        if (status >= 200 && status < 300) {
            if (responseClass == null) {
                return null;
            }

            try (InputStream inputStream = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(inputStream);
                return gson.fromJson(reader, responseClass);
            }
        } else {
            try (InputStream errorStream = http.getErrorStream()) {
                if (errorStream != null) {
                    InputStreamReader reader = new InputStreamReader(errorStream);
                    ErrorResponse error = gson.fromJson(reader, ErrorResponse.class);
                    throw new Exception(error.message());
                } else {
                    throw new Exception("Request failed");
                }
            }
        }
    }
}
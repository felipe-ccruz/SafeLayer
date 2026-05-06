package com.felp.frontvm.service;

import com.felp.frontvm.config.ApiConfig;
import com.felp.frontvm.model.UserModel;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserApiService {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static class ApiException extends RuntimeException {
        private final int status;

        public ApiException(int status, String message) {
            super(message);
            this.status = status;
        }

        public int getStatus() { return status; }
    }

    public UserModel register(String name, String email, String password) {
        String body = new JSONObject()
                .put("name", name)
                .put("email", email)
                .put("password", password)
                .toString();
        return execute("POST", "/api/users/register", body);
    }

    public UserModel login(String email, String password) {
        String body = new JSONObject()
                .put("email", email)
                .put("password", password)
                .toString();
        return execute("POST", "/api/users/login", body);
    }

    public UserModel changePlan(Long userId, String plan) {
        String body = new JSONObject().put("plan", plan).toString();
        return execute("PATCH", "/api/users/" + userId + "/plan", body);
    }

    private UserModel execute(String method, String path, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + path))
                    .header("Content-Type", "application/json");

            HttpRequest req = switch (method) {
                case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(body)).build();
                case "PATCH" -> builder.method("PATCH", HttpRequest.BodyPublishers.ofString(body)).build();
                default -> throw new IllegalArgumentException("método não suportado: " + method);
            };

            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status / 100 == 2) {
                return parse(new JSONObject(response.body()));
            }
            throw new ApiException(status, extractError(response.body(), status));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Erro de conexão: " + e.getMessage());
        }
    }

    private String extractError(String body, int status) {
        try {
            return new JSONObject(body).optString("error", "Erro " + status);
        } catch (Exception e) {
            return "Erro " + status;
        }
    }

    private UserModel parse(JSONObject json) {
        UserModel u = new UserModel();
        u.setId(json.getLong("id"));
        u.setName(json.getString("name"));
        u.setEmail(json.getString("email"));
        u.setPlan(json.getString("plan"));
        u.setPlanLabel(json.getString("planLabel"));
        u.setMaxVms(json.getInt("maxVms"));
        u.setUnlimited(json.getBoolean("unlimited"));
        return u;
    }
}

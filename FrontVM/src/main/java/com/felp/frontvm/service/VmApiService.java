package com.felp.frontvm.service;

import com.felp.frontvm.config.ApiConfig;
import com.felp.frontvm.model.VmModel;
import com.felp.frontvm.session.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VmApiService {

    private static final String USER_HEADER = "X-User-Id";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static class VmApiException extends RuntimeException {
        private final int status;

        public VmApiException(int status, String message) {
            super(message);
            this.status = status;
        }

        public int getStatus() { return status; }
    }

    public List<VmModel> findAll() {
        try {
            HttpRequest request = baseRequest("/api/vms").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                return Collections.emptyList();
            }
            JSONArray array = new JSONArray(response.body());
            List<VmModel> result = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                result.add(parseVm(array.getJSONObject(i)));
            }
            return result;
        } catch (Exception e) {
            System.err.println("Erro ao listar VMs: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public VmModel create(String name, String osType, String profile) {
        try {
            String body = new JSONObject()
                    .put("name", name)
                    .put("osType", osType)
                    .put("profile", profile)
                    .toString();

            HttpRequest request = baseRequest("/api/vms")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status / 100 == 2) {
                return parseVm(new JSONObject(response.body()));
            }
            throw new VmApiException(status, extractError(response.body(), status));
        } catch (VmApiException e) {
            throw e;
        } catch (Exception e) {
            throw new VmApiException(0, "Erro de conexão: " + e.getMessage());
        }
    }

    public VmModel start(Long id) {
        return patch(id, "start");
    }

    public VmModel pause(Long id) {
        return patch(id, "pause");
    }

    public VmModel stop(Long id) {
        return patch(id, "stop");
    }

    public boolean delete(Long id) {
        try {
            HttpRequest request = baseRequest("/api/vms/" + id).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            System.err.println("Erro ao deletar VM " + id + ": " + e.getMessage());
            return false;
        }
    }

    private VmModel patch(Long id, String action) {
        try {
            HttpRequest request = baseRequest("/api/vms/" + id + "/" + action)
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 == 2) {
                return parseVm(new JSONObject(response.body()));
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao executar " + action + " na VM " + id + ": " + e.getMessage());
            return null;
        }
    }

    private HttpRequest.Builder baseRequest(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + path));
        Long userId = Session.getUserId();
        if (userId != null) {
            builder.header(USER_HEADER, String.valueOf(userId));
        }
        return builder;
    }

    private String extractError(String body, int status) {
        try {
            return new JSONObject(body).optString("error", "Erro " + status);
        } catch (Exception e) {
            return "Erro " + status;
        }
    }

    private VmModel parseVm(JSONObject json) {
        VmModel vm = new VmModel();
        vm.setId(json.getLong("id"));
        vm.setName(json.getString("name"));
        vm.setOsType(json.getString("osType"));
        vm.setOsLabel(json.getString("osLabel"));
        vm.setProfile(json.getString("profile"));
        vm.setProfileLabel(json.getString("profileLabel"));
        vm.setStatus(json.getString("status"));
        vm.setStatusLabel(json.getString("statusLabel"));
        vm.setCpuCores(json.getInt("cpuCores"));
        vm.setRamGb(json.getInt("ramGb"));
        vm.setDiskGb(json.getInt("diskGb"));
        vm.setCreatedAt(json.getString("createdAt"));
        return vm;
    }
}

package com.felp.frontvm.service;

import com.felp.frontvm.config.ApiConfig;
import com.felp.frontvm.model.VmModel;
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

    private static final HttpClient client = HttpClient.newHttpClient();

    public List<VmModel> findAll() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/api/vms"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/api/vms"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseVm(new JSONObject(response.body()));
        } catch (Exception e) {
            System.err.println("Erro ao criar VM: " + e.getMessage());
            return null;
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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/api/vms/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            System.err.println("Erro ao deletar VM " + id + ": " + e.getMessage());
            return false;
        }
    }

    private VmModel patch(Long id, String action) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/api/vms/" + id + "/" + action))
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseVm(new JSONObject(response.body()));
        } catch (Exception e) {
            System.err.println("Erro ao executar " + action + " na VM " + id + ": " + e.getMessage());
            return null;
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

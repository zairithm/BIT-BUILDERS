package com.Bit_Builder.x_ray.app.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SolanaService {

    private final RestTemplate restTemplate;

    private static final String RPC_URL    = "https://api.devnet.solana.com";
    private static final String PROGRAM_ID = "FcsZDye6x3AAWheYgvBrz7MKTzx637M4MiVugrykcAcb";

    // Check if Solana Devnet is reachable
    public String getSolanaVersion() {
        try {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("jsonrpc", "2.0");
            request.put("id",      1);
            request.put("method",  "getVersion");
            request.put("params",  List.of());

            ResponseEntity<Map> response = postToSolana(request);
            Map result = (Map) response.getBody().get("result");
            return (String) result.get("solana-core");

        } catch (Exception e) {
            System.err.println("Solana version check failed: " + e.getMessage());
            return "unavailable";
        }
    }

    // Get latest blockhash - proves live connection
    public String getLatestBlockhash() {
        try {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("jsonrpc", "2.0");
            request.put("id",      1);
            request.put("method",  "getLatestBlockhash");
            request.put("params",  List.of());

            ResponseEntity<Map> response = postToSolana(request);
            Map result = (Map) response.getBody().get("result");
            Map value  = (Map) result.get("value");
            return (String) value.get("blockhash");

        } catch (Exception e) {
            System.err.println("Solana blockhash fetch failed: " + e.getMessage());
            return "devnet-unavailable";
        }
    }

    // Check if smart contract exists on devnet
    public Map<String, Object> getProgramInfo() {
        try {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("jsonrpc", "2.0");
            request.put("id",      1);
            request.put("method",  "getAccountInfo");
            request.put("params",  List.of(
                    PROGRAM_ID,
                    Map.of("encoding", "base64")
            ));

            ResponseEntity<Map> response = postToSolana(request);
            Map result = (Map) response.getBody().get("result");
            Map value  = (Map) result.get("value");

            Map<String, Object> info = new LinkedHashMap<>();
            info.put("programId",   PROGRAM_ID);
            info.put("exists",      value != null);
            info.put("executable",  value != null ? value.get("executable") : false);
            info.put("network",     "Solana Devnet");
            info.put("explorerUrl", "https://explorer.solana.com/address/"
                    + PROGRAM_ID + "?cluster=devnet");
            return info;

        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    // Get full network status - useful for demo
    public Map<String, Object> getNetworkStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("network",       "Solana Devnet");
        status.put("rpcUrl",        RPC_URL);
        status.put("programId",     PROGRAM_ID);
        status.put("solanaVersion", getSolanaVersion());
        status.put("blockhash",     getLatestBlockhash());
        status.put("explorerUrl",   "https://explorer.solana.com/address/"
                + PROGRAM_ID + "?cluster=devnet");
        status.put("timestamp",     System.currentTimeMillis());
        return status;
    }

    // Shared HTTP call method
    private ResponseEntity<Map> postToSolana(Map<String, Object> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForEntity(RPC_URL, entity, Map.class);
    }
}
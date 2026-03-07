package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.entity.AiResult;
import com.Bit_Builder.x_ray.app.utils.HashUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class BlockchainService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HashUtil hashUtil;

    private static final String RPC_URL = "https://api.devnet.solana.com";
    private static final String PROGRAM_ID = "FcsZDye6x3AAWheYgvBrz7MKTzx637M4MiVugrykcAcb";

    public String logReportToBlockchain(String reportId, AiResult aiResult) throws Exception {

        // 1. Build only the fields needed for blockchain (exclude images)
        Map<String, Object> auditData = new LinkedHashMap<>();
        auditData.put("reportId", reportId);
        auditData.put("probabilities", aiResult.getProbabilities());
        auditData.put("max_probability", aiResult.getMax_probability());
        auditData.put("confidence_score", aiResult.getConfidence_score());
        auditData.put("confidence_level", aiResult.getConfidence_level());
        auditData.put("triage_score", aiResult.getTriage_score());
        auditData.put("priority", aiResult.getPriority());
        auditData.put("clinician_note", aiResult.getClinician_note());
        //  Exclude visual_analysis (gradcam/lung_segmentation are huge base64 strings)
        // We don't want to hash images — just the diagnostic data

        // 2. Generate SHA256 hash of diagnostic data only
        String json = objectMapper.writeValueAsString(auditData);
        String reportHash = hashUtil.generateHash(json);

        // 3. Get live blockhash from Solana Devnet
        String blockhash = getLatestBlockhash();

        // 4. Log audit record
        System.out.println("========= BLOCKCHAIN AUDIT =========");
        System.out.println("Report ID        : " + reportId);
        System.out.println("Report Hash      : " + reportHash);
        System.out.println("Blockhash        : " + blockhash);
        System.out.println("Program ID       : " + PROGRAM_ID);
        System.out.println("Network          : Solana Devnet");
        System.out.println("Max Probability  : " + aiResult.getMax_probability());
        System.out.println("Confidence Level : " + aiResult.getConfidence_level());
        System.out.println("Priority         : " + aiResult.getPriority());
        System.out.println("Triage Score     : " + aiResult.getTriage_score());
        System.out.println("Timestamp        : " + System.currentTimeMillis());
        System.out.println("====================================");

        return reportHash;
    }

    private String getLatestBlockhash() {
        try {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("jsonrpc", "2.0");
            request.put("id", 1);
            request.put("method", "getLatestBlockhash");
            request.put("params", List.of());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    RPC_URL, entity, Map.class
            );

            Map result = (Map) response.getBody().get("result");
            Map value = (Map) result.get("value");
            return (String) value.get("blockhash");

        } catch (Exception e) {
            System.err.println("Solana RPC failed: " + e.getMessage());
            return "devnet-unavailable";
        }
    }
}
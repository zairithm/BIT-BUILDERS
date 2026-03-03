package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.entity.AiResult;
import com.Bit_Builder.x_ray.app.utils.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class AiService {
    @Value("${ai.url}")
    private String aiUrl;
    public AiResult analyzeXray(MultipartFile file) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(
                file.getInputStream(), file.getOriginalFilename()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<AiResult> response = restTemplate.postForEntity(
                aiUrl,
                requestEntity,
                AiResult.class
        );

        return response.getBody();
    }


    public String determineSeverity(String priority) {
        if (priority == null) return "NORMAL";
        return switch (priority.toUpperCase()) {
            case "CRITICAL" -> "SEVERE";
            case "HIGH"     -> "MODERATE";
            case "MEDIUM"   -> "MILD";
            case "LOW"      -> "NORMAL";
            default         -> "NORMAL";
        };
    }
}
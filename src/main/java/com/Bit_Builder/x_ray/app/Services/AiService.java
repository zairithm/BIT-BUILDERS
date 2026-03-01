package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.entity.AiResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@Service
public class AiService {
    //for now data is mocked hear
    public AiResult analyzeXray(MultipartFile file) {  // ← everything goes INSIDE this method

        AiResult result = new AiResult();
        result.setDetectedDiseases(List.of("Pneumonia", "Effusion")); // ← add method name
        result.setProbabilities(Map.of(
                "Pneumonia", 0.70,
                "Effusion", 0.64,
                "Atelectasis", 0.12,
                "No Finding", 0.05
        ));
        result.setMaxConfidence(0.10);
        result.setUncertaintyFlag(false);
        result.setWarning(null);
        result.setGradcamImage("mock_gradcam_output.jpg");
        result.setVoiceReport("mock_voice_report.mp3");
        result.setClinicianNote("The AI model detected Pneumonia and Effusion with high confidence.");

        return result;  // ← return inside method
    }

    public String determineSeverity(double maxConfidence) {
        if (maxConfidence >= 0.80) return "SEVERE";
        else if (maxConfidence >= 0.60) return "MODERATE";
        else if (maxConfidence >= 0.40) return "MILD";
        else return "NORMAL";
    }
}
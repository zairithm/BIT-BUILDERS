package com.Bit_Builder.x_ray.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResult {
    private List<String> detectedDiseases;        // ["Pneumonia", "Effusion"]
    private Map<String, Double> probabilities;     // {"Pneumonia": 0.82, ...}
    private double maxConfidence;                  // 0.82
    private boolean uncertaintyFlag;              // false
    private String warning;                        // null or some warning
    private String gradcamImage;                   // "gradcam_output.jpg"
    private String voiceReport;                    // "voice_abc123.mp3"
    private String clinicianNote;                  // "The AI model detected..."
}
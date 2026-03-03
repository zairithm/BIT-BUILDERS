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
    private Map<String, Double> probabilities;        // ["Pneumonia", "Effusion"]
    private double max_probability;
    private double confidence_score;
    private String confidence_level;
    private double triage_score;
    private String priority;
    private String clinician_note;
    private String heatmap;
}
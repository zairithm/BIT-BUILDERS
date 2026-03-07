package com.Bit_Builder.x_ray.app.Dto;

import com.Bit_Builder.x_ray.app.entity.AiResult;
import com.Bit_Builder.x_ray.app.enums.Severity;
import com.Bit_Builder.x_ray.app.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    // common fields — both doctor and patient see these
    private String reportId;
    private LocalDateTime uploadedAt;
    private Status status;
    private Severity severity;
    private AiResult aiResult;

    // patient sees these
    private String doctorName;      // instead of raw doctorId
    private String diagnosis;       // final diagnosis
    private String doctorNotes;     // doctor's notes

    // doctor sees these (null for patient)
    private String patientName;     // instead of raw patientId
    private String patientAge;
    private String bloodGroup;

    //blockchain feilds
    private String transactionHash;
    private String blockchainNetwork;
    private String explorerUrl;
    private Long blockchainTimestamp;
}

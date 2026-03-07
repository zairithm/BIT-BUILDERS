package com.Bit_Builder.x_ray.app.entity;

import com.Bit_Builder.x_ray.app.enums.Severity;
import com.Bit_Builder.x_ray.app.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "xray_report")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XRayReport {
    @Id
    private String id;

    private String patientId;
    private String doctorId;
    private String imageUrl;
    //object of result got from AI
    private AiResult aiResult;

    private Status status;
    private Severity severity;
    private String doctorNotes;
    private String diagnosis;
    private LocalDateTime uploadedAt;

    private String transactionHash; // blockchain tx hash
    private String blockchainNetwork;
    private String explorerUrl;
    private Long blockchainTimestamp;

}

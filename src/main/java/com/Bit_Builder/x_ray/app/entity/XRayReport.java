package com.Bit_Builder.x_ray.app.entity;

import com.Bit_Builder.x_ray.app.enums.Severity;
import com.Bit_Builder.x_ray.app.enums.Status;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "xray_report")
public class XRayReport {
    @Id
    private String id;

    private ObjectId patientId;
    private ObjectId doctorId;
    private String imageUrl;
    //object of result got from AI
    private AiResult aiResult;

    private Status status;
    private Severity severity;
    private String doctorNotes;
    private String diagnosis;
    private LocalDateTime uploadedAt;
}

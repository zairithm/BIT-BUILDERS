package com.Bit_Builder.x_ray.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    private String id;

    private String userId;
    private String specialization;
    private String licenceNumber;
    private String hospitalName;
    private int experience;
    private String phone;
}

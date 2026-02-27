package com.Bit_Builder.x_ray.app.entity;

import com.Bit_Builder.x_ray.app.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "patient")
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    private String id;

    private String doctorId;
    private String userId;

    private int age;
    private Gender gender;
    private Date dob;
    private String bloodGroup;
    private String phone;
}
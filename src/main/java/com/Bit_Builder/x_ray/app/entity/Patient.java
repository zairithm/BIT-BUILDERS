package com.Bit_Builder.x_ray.app.entity;

import com.Bit_Builder.x_ray.app.enums.Gender;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "patient")
public class Patient {
    @Id
    private ObjectId id;


    private String doctorid;
    private String userid;

    private int age;
    private Gender gender;
    private Date dob;
    private String bloodGroup;
    private String phone;
}

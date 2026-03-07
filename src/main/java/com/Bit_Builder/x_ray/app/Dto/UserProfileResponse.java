package com.Bit_Builder.x_ray.app.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    // common fields
    private String fullName;
    private String email;
    private String role;
    private String profilePicture;

    // doctor specific fields (null if patient)
    private String specialization;
    private String licenceNumber;
    private String hospitalName;
    private int experience;
    private String phone;

    // patient specific fields (null if doctor)
    private int age;
    private String gender;
    private String bloodGroup;
    private String doctorId;
}

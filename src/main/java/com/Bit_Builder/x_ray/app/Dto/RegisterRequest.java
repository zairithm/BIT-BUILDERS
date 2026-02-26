package com.Bit_Builder.x_ray.app.Dto;

import com.Bit_Builder.x_ray.app.enums.Gender;
import com.Bit_Builder.x_ray.app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    // Common fields for both
    private String fullName;
    private String email;
    private String password;
    private Role role;

    // Doctor specific fields
    private String specialization;
    private String licenceNumber;
    private String hospitalName;
    private int experience;
    private String phone;

    // Patient specific fields
    private int age;
    private Gender gender;
    private Date dob;
    private String bloodGroup;
}
package com.Bit_Builder.x_ray.app.entity;

import com.Bit_Builder.x_ray.app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;

    @NonNull
    private String FullName;
    @NonNull
    private String password;
    @Indexed(unique = true)
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private String profilePicture;
}
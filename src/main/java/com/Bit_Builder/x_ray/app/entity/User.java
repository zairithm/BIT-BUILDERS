package com.Bit_Builder.x_ray.app.entity;

import com.Bit_Builder.x_ray.app.enums.Role;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private ObjectId id;

    @NonNull
    @Indexed(unique = true)
    private String userName;
    @NonNull
    private String password;
    private String email;
    private Role role;
}

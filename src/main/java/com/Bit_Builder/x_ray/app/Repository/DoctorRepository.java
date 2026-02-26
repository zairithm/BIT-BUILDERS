package com.Bit_Builder.x_ray.app.Repository;

import com.Bit_Builder.x_ray.app.entity.Doctor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DoctorRepository extends MongoRepository<Doctor, ObjectId> {
    Optional<Doctor> findByUserId(String userId);
}

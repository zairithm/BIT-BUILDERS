package com.Bit_Builder.x_ray.app.Repository;

import com.Bit_Builder.x_ray.app.entity.Patient;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByUserId(String userId);
    List<Patient> findByDoctorId(String doctorId);
}
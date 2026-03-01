package com.Bit_Builder.x_ray.app.Repository;

import com.Bit_Builder.x_ray.app.entity.XRayReport;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface XRayReportRepository extends MongoRepository<XRayReport, String> {
    Optional<XRayReport> findByPatientId(String patientId);  // patient views their reports
    List<XRayReport> findByDoctorId(String doctorId);
}

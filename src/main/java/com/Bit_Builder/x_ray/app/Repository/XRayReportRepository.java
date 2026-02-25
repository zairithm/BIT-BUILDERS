package com.Bit_Builder.x_ray.app.Repository;

import com.Bit_Builder.x_ray.app.entity.XRayReport;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface XRayReportRepository extends MongoRepository<XRayReport, ObjectId> {
    List<XRayReport> findByPatientId(String patientId);  // patient views their reports
    List<XRayReport> findByDoctorId(String doctorId);
}

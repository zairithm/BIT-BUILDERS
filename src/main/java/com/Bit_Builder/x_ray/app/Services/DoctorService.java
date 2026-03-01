package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.Dto.ReportResponse;
import com.Bit_Builder.x_ray.app.Dto.UserProfileResponse;
import com.Bit_Builder.x_ray.app.Repository.DoctorRepository;
import com.Bit_Builder.x_ray.app.Repository.PatientRepository;
import com.Bit_Builder.x_ray.app.Repository.UserRepository;
import com.Bit_Builder.x_ray.app.Repository.XRayReportRepository;
import com.Bit_Builder.x_ray.app.entity.Doctor;
import com.Bit_Builder.x_ray.app.entity.Patient;
import com.Bit_Builder.x_ray.app.entity.User;
import com.Bit_Builder.x_ray.app.entity.XRayReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private XRayReportRepository xRayReportRepository;

    //get all my patients
    public List<Patient> getAllMyPatients(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found!"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(()-> new RuntimeException("doctor not found!"));

        // get all patients of this doctor
        List<Patient> patients = patientRepository.findByDoctorId(doctor.getId());

        // sort by severity
        List<Patient> sorted = patients.stream()
                .sorted((p1, p2) -> {
                    int s1 = getSeverityOrder(p1.getId());
                    int s2 = getSeverityOrder(p2.getId());
                    return Integer.compare(s1, s2);
                })
                .collect(Collectors.toList());

        return sorted;
    }

    // helper — gives severity a number for sorting
    private int getSeverityOrder(String patientId) {
        return xRayReportRepository.findByPatientId(patientId)
                .map(report -> switch (report.getSeverity()) {
                    case SEVERE -> 1;
                    case MODERATE -> 2;
                    case MILD -> 3;
                    case NORMAL -> 4;
                })
                .orElse(5); // no report yet → show last
    }

    //get patient
    public UserProfileResponse getPatientById(String patientId){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(()-> new RuntimeException("patient not found!"));


        User user = userRepository.findById(patient.getUserId())
                .orElseThrow(()-> new RuntimeException("user not found"));

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setFullName(user.getFullName());
        userProfileResponse.setEmail(user.getEmail());
        userProfileResponse.setAge(patient.getAge());
        userProfileResponse.setBloodGroup(patient.getBloodGroup());
        userProfileResponse.setRole(user.getRole().name());

        return userProfileResponse;
    }

    //get patient report
    public ReportResponse getPatientReport(String patientId) {

        XRayReport report = xRayReportRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("No report found"));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        User patientUser = userRepository.findById(patient.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ReportResponse response = new ReportResponse();
        response.setReportId(report.getId());
        response.setUploadedAt(report.getUploadedAt());
        response.setStatus(report.getStatus());
        response.setSeverity(report.getSeverity());
        response.setAiResult(report.getAiResult());
        response.setDiagnosis(report.getDiagnosis());
        response.setDoctorNotes(report.getDoctorNotes());

        // doctor sees patient details
        response.setPatientName(patientUser.getFullName());
        response.setPatientAge(String.valueOf(patient.getAge()));
        response.setBloodGroup(patient.getBloodGroup());

        return response;
    }
}

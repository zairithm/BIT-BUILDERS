package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.Dto.ReportResponse;
import com.Bit_Builder.x_ray.app.Repository.DoctorRepository;
import com.Bit_Builder.x_ray.app.Repository.PatientRepository;
import com.Bit_Builder.x_ray.app.Repository.UserRepository;
import com.Bit_Builder.x_ray.app.Repository.XRayReportRepository;
import com.Bit_Builder.x_ray.app.entity.*;
import com.Bit_Builder.x_ray.app.enums.Severity;
import com.Bit_Builder.x_ray.app.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PateintServices {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XRayReportRepository xRayReportRepository;

    @Autowired
    private AiService aiService;

    //getting all doctors for patient
    public List<Doctor> getAllDoctors(){
        List<Doctor> res = doctorRepository.findAll();
        return res;
    }

    //adding doctor
    @Transactional
    public String addDoctor(String doctorId, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(()-> new RuntimeException("patient not found"));

        if (patient.getDoctorId() != null){
            throw new RuntimeException("You already have doctor");
        }

        doctorRepository.findById(doctorId)
                .orElseThrow(()-> new RuntimeException("Doctor not foud"));

        //assign doctor
        patient.setDoctorId(doctorId);
        patientRepository.save(patient);
        return "Doctor assigned successfully";
    }

    //removing doctor
    @Transactional
    public String removeDoctor(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("user not found"));

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(()-> new RuntimeException("patient not found"));

        if (patient.getDoctorId() == null){
            throw new RuntimeException("You don't have any doctor");
        }

        patient.setDoctorId(null);
        patientRepository.save(patient);
        return "Your doctor is removed select new one!";
    }

    //patient uploads his x-ray
    public String uploadXray(MultipartFile file, String email) throws IOException {
        //get patient by user id
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("user not found!"));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(()-> new RuntimeException("patient not found!"));

       if(xRayReportRepository.findByPatientId(patient.getId()).isPresent()){
           throw new RuntimeException("Already have report");
       }
       //call ai result
        AiResult aiResult = aiService.analyzeXray(file);

       //determine seviearity

        Severity severity = Severity.valueOf(
                aiService.determineSeverity(aiResult.getPriority())
        );
        //create report
        XRayReport report = new XRayReport();
        report.setPatientId(patient.getId());
        report.setUploadedAt(LocalDateTime.now());
        report.setAiResult(aiResult);
        report.setSeverity(severity);
        report.setStatus(Status.ANALYZED);

        xRayReportRepository.save(report);

        return "X-Ray uploaded and analyzed by Rayzo!";
    }

    //patient gets his report
    public ReportResponse getMyReport(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        XRayReport report = xRayReportRepository.findByPatientId(patient.getId())
                .orElseThrow(() -> new RuntimeException("No report found"));


        ReportResponse response = new ReportResponse();

        response.setReportId(report.getId());
        response.setUploadedAt(report.getUploadedAt());
        response.setStatus(report.getStatus());
        response.setSeverity(report.getSeverity());
        response.setAiResult(report.getAiResult());
        response.setDiagnosis(report.getDiagnosis());
        response.setDoctorNotes(report.getDoctorNotes());

        // get doctor name if assigned
        if (report.getDoctorId() != null) {
            doctorRepository.findById(report.getDoctorId()).ifPresent(doctor -> {
                userRepository.findById(doctor.getUserId()).ifPresent(u ->
                        response.setDoctorName(u.getFullName())
                );
            });
        }
        return response;
    }
}
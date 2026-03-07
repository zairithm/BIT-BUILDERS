package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.Dto.UpdateReportRequest;
import com.Bit_Builder.x_ray.app.Dto.ReportResponse;
import com.Bit_Builder.x_ray.app.Dto.UserProfileResponse;
import com.Bit_Builder.x_ray.app.Repository.DoctorRepository;
import com.Bit_Builder.x_ray.app.Repository.PatientRepository;
import com.Bit_Builder.x_ray.app.Repository.UserRepository;
import com.Bit_Builder.x_ray.app.Repository.XRayReportRepository;
import com.Bit_Builder.x_ray.app.entity.*;
import com.Bit_Builder.x_ray.app.enums.Severity;
import com.Bit_Builder.x_ray.app.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private AiService aiService;

    @Autowired
    private  BlockchainService blockchainService;

    @Autowired
    private SolanaService solanaService;

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
                    double t1 = getTriageScore(p1.getId());
                    double t2 = getTriageScore(p2.getId());
                    return Double.compare(t2, t1); // higher triage score first
                }).toList();

        return sorted;
    }

    // helper — gives severity a number for sorting
    private double getTriageScore(String patientId) {
        Optional<XRayReport> reports = xRayReportRepository.findByPatientId(patientId);
        if (reports.isEmpty()) return -999;
        AiResult aiResult = reports.get().getAiResult();
        if (aiResult == null) return -999;
        return aiResult.getTriage_score();
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

        // Add these
        response.setTransactionHash(report.getTransactionHash());
        response.setBlockchainNetwork(report.getBlockchainNetwork());
        response.setExplorerUrl(report.getExplorerUrl());
        response.setBlockchainTimestamp(report.getBlockchainTimestamp());

        return response;
    }

    //upload patients x-ray
    public String uploadXRayForPatient(String patientId, MultipartFile file, String email) throws IOException {
        //verify doctorexist
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("can't find user"));
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(()-> new RuntimeException("can't find doctor"));

        //get patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("paient not found"));

        if (xRayReportRepository.findByPatientId(patientId).isPresent()) {
            throw new RuntimeException("Report already exists for this patient! Delete it first.");
        }

        //call ai
        AiResult aiResult = aiService.analyzeXray(file);

        Severity severity = Severity.valueOf(
                aiService.determineSeverity(aiResult.getPriority())
        );

        // create report
        XRayReport report = new XRayReport();
        report.setPatientId(patientId);
        report.setDoctorId(doctor.getId());
        report.setAiResult(aiResult);
        report.setSeverity(severity);
        report.setStatus(Status.ANALYZED);
        report.setUploadedAt(LocalDateTime.now());

        xRayReportRepository.save(report);

        // Call blockchain
        try {
            // Get live blockhash from Solana
            String blockhash = solanaService.getLatestBlockhash();

            // Submit report hash to blockchain
            String txHash = blockchainService.logReportToBlockchain(
                    report.getId(), aiResult
            );

            // Set blockchain fields on report
            report.setTransactionHash(txHash);
            report.setBlockchainNetwork("Solana Devnet");
            report.setBlockchainTimestamp(System.currentTimeMillis());
            report.setExplorerUrl(
                    "https://explorer.solana.com/address/" +
                            "FcsZDye6x3AAWheYgvBrz7MKTzx637M4MiVugrykcAcb" +
                            "?cluster=devnet"
            );

            System.out.println(" Blockchain audit complete");
            System.out.println("TX Hash   : " + txHash);
            System.out.println("Blockhash : " + blockhash);

        } catch (Exception e) {
            // Don't fail the upload if blockchain fails
            System.err.println(" Blockchain logging failed: " + e.getMessage());
            report.setTransactionHash("blockchain-unavailable");
            report.setBlockchainNetwork("Solana Devnet");
        }
        // save again with transaction hash
        xRayReportRepository.save(report);
        return "X-Ray uploaded and analyzed successfully!";
    }

    public String addNotes(String reportId, UpdateReportRequest request) {

        XRayReport report = xRayReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // update notes
        if (request.getDoctorNotes() != null) {
            report.setDoctorNotes(request.getDoctorNotes());
        }

        // update diagnosis
        if (request.getDiagnosis() != null) {
            report.setDiagnosis(request.getDiagnosis());
        }

        // update status if provided otherwise auto set REVIEWED
        if (request.getStatus() != null) {
            report.setStatus(request.getStatus());
        } else {
            report.setStatus(Status.REVIEWED);
        }

        xRayReportRepository.save(report);
        return "Report updated successfully!";
    }
}
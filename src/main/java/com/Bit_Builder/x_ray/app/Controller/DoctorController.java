package com.Bit_Builder.x_ray.app.Controller;

import com.Bit_Builder.x_ray.app.Dto.ReportResponse;
import com.Bit_Builder.x_ray.app.Dto.UpdateReportRequest;
import com.Bit_Builder.x_ray.app.Dto.UserProfileResponse;
import com.Bit_Builder.x_ray.app.Services.DoctorService;
import com.Bit_Builder.x_ray.app.entity.Patient;
import com.Bit_Builder.x_ray.app.entity.XRayReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@Slf4j
@Tag(name = "Doctor", description = "Doctor APIs")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @Operation(summary = "Get My Patients Sorted", description = "Returns all patients sorted by severity. SEVERE cases appear first.")
    @GetMapping("/my-patients")
    public ResponseEntity<List<Patient>> getMyPatients() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            List<Patient> patients = doctorService.getAllMyPatients(email);
            return new ResponseEntity<>(patients, HttpStatus.OK);
        } catch (Exception e) {
            log.error("failed to fetch patients", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get One Patient", description = "Returns single patient profile by patient ID.")
    @GetMapping("/get-one-patient/{patientId}")
    public ResponseEntity<UserProfileResponse> getOnePatient(@PathVariable String patientId) {
        try{
            UserProfileResponse userProfileResponse = doctorService.getPatientById(patientId);
            return  new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("can't fetch patient", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get Patient Report", description = "Doctor views full report of a specific patient.")
    @GetMapping("/{patientId}/report")
    public ResponseEntity<ReportResponse> getPatientReport(@PathVariable String patientId) {
        try {
            ReportResponse response = doctorService.getPatientReport(patientId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("failed to fetch report", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Upload X-Ray for Patient", description = "Doctor uploads X-Ray for a specific patient.")
    @PostMapping("/{patientId}/upload-patient-xray")
    public ResponseEntity<String> uploadXray(@PathVariable String patientId,
                                             @RequestParam("file") MultipartFile file){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            String response = doctorService.uploadXRayForPatient(patientId, file, email);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("failed to upload xray", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update Report", description = "Doctor adds notes, diagnosis and updates status.")
    @PutMapping("/update-report/{reportId}")
    public ResponseEntity<String> updateReport(@PathVariable String reportId,
                                               @RequestBody UpdateReportRequest request){
        try{
            String response = doctorService.addNotes(reportId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("can't change", e);
            return  new ResponseEntity<>("can't update Report", HttpStatus.BAD_REQUEST);
        }
    }
}

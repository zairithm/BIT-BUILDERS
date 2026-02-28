package com.Bit_Builder.x_ray.app.Controller;

import com.Bit_Builder.x_ray.app.Services.PateintServices;
import com.Bit_Builder.x_ray.app.entity.Doctor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@Slf4j
@Tag(name = "Patient", description = "Patient APIs")
public class PatientController {

    @Autowired
    private PateintServices pateintServices;

    @GetMapping("/get-all-doctor")
    @Operation(summary = "Get All Doctors", description = "Returns list of all registered doctors. Patient can choose from this list.")
    public ResponseEntity<List<Doctor>> getAllDoctors(){
        try {
            List<Doctor> responseList = pateintServices.getAllDoctors();
            return  new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("failed to fetch doctors list", e);
            throw new RuntimeException("failed to fetch all doctors", e);
        }
    }

    @PostMapping("/select-doctor/{doctorId}")
    @Operation(summary = "Select Doctor", description = "Patient selects a doctor. Only allowed if patient has no doctor assigned yet.")
    public ResponseEntity<String> selectDoctor(@PathVariable String doctorId){
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            String response = pateintServices.addDoctor(doctorId, email);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("failed in adding doctor");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/remove-doctor")
    @Operation(summary = "Remove Doctor", description = "Patient removes their currently assigned doctor.")
    public ResponseEntity<String> removeDoctor(){
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            String response = pateintServices.removeDoctor(email);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("can't remove your doctor!");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
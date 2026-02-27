package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.Repository.DoctorRepository;
import com.Bit_Builder.x_ray.app.Repository.PatientRepository;
import com.Bit_Builder.x_ray.app.Repository.UserRepository;
import com.Bit_Builder.x_ray.app.entity.Doctor;
import com.Bit_Builder.x_ray.app.entity.Patient;
import com.Bit_Builder.x_ray.app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
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
}

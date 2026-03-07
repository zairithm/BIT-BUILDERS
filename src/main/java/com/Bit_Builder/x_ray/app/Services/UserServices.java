package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.Dto.UserProfileResponse;
import com.Bit_Builder.x_ray.app.Repository.DoctorRepository;
import com.Bit_Builder.x_ray.app.Repository.PatientRepository;
import com.Bit_Builder.x_ray.app.Repository.UserRepository;
import com.Bit_Builder.x_ray.app.entity.Doctor;
import com.Bit_Builder.x_ray.app.entity.Patient;
import com.Bit_Builder.x_ray.app.entity.User;
import com.Bit_Builder.x_ray.app.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not foud!"));

        UserProfileResponse profile = new UserProfileResponse();
        if (user.getRole().equals(Role.PATIENT)) {
            Patient patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Patient not foud!"));
            profile.setFullName(user.getFullName());
            profile.setEmail(user.getEmail());
            profile.setRole(user.getRole().name());
            profile.setAge(patient.getAge());
            profile.setGender(patient.getGender().name());
            profile.setBloodGroup(patient.getBloodGroup());
            profile.setDoctorId(patient.getDoctorId());

        }else if(user.getRole().equals(Role.DOCTOR)){
            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Doctor not foud!"));

            profile.setFullName(user.getFullName());
            profile.setEmail(user.getEmail());
            profile.setRole(user.getRole().name());
            profile.setHospitalName(doctor.getHospitalName());
            profile.setLicenceNumber(doctor.getLicenceNumber());
            profile.setExperience(doctor.getExperience());
            profile.setSpecialization(doctor.getSpecialization());
        }
        return profile;
    }

    public String uploadProfilePicture(String email, MultipartFile file)throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not found"));

        String base64 = Base64.getEncoder().encodeToString(file.getBytes());
        user.setProfilePicture(base64);
        userRepository.save(user);

        return "pfp uploaded!";
    }

    public String deletePfp(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfilePicture(null);
        userRepository.save(user);

        return "pfp deleted!";
    }
}

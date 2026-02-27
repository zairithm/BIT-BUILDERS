package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.Dto.LoginRequest;
import com.Bit_Builder.x_ray.app.Dto.LoginResponse;
import com.Bit_Builder.x_ray.app.Dto.RegisterRequest;
import com.Bit_Builder.x_ray.app.Repository.DoctorRepository;
import com.Bit_Builder.x_ray.app.Repository.PatientRepository;
import com.Bit_Builder.x_ray.app.Repository.UserRepository;
import com.Bit_Builder.x_ray.app.entity.Doctor;
import com.Bit_Builder.x_ray.app.entity.Patient;
import com.Bit_Builder.x_ray.app.entity.User;
import com.Bit_Builder.x_ray.app.enums.Role;
import com.Bit_Builder.x_ray.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request){
        try{
            // Step 1 - save User
            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword())); // hash password
            user.setRole(request.getRole());
            user.setCreatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);


            // Step 2 - save Doctor or Patient based on role
            if (request.getRole() == Role.DOCTOR) {
                Doctor doctor = new Doctor();
                doctor.setUserId(savedUser.getId());
                doctor.setSpecialization(request.getSpecialization());
                doctor.setLicenceNumber(request.getLicenceNumber());
                doctor.setHospitalName(request.getHospitalName());
                doctor.setExperience(request.getExperience());
                doctor.setPhone(request.getPhone());
                doctorRepository.save(doctor);

            } else if (request.getRole() == Role.PATIENT) {
                Patient patient = new Patient();
                patient.setUserId(savedUser.getId());
                patient.setAge(request.getAge());
                patient.setGender(request.getGender());
                patient.setDob(request.getDob());
                patient.setBloodGroup(request.getBloodGroup());
                patientRepository.save(patient);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed while registering user:(", e);
        }
    }

    public LoginResponse logIn(LoginRequest request){
        //find by email
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("user not found!"));

        //check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid Password!");
        }

        //generate token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, user.getRole().name(), user.getFullName());
    }
}

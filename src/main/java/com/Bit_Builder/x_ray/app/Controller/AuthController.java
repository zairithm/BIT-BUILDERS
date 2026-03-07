package com.Bit_Builder.x_ray.app.Controller;

import com.Bit_Builder.x_ray.app.Dto.LoginRequest;
import com.Bit_Builder.x_ray.app.Dto.LoginResponse;
import com.Bit_Builder.x_ray.app.Dto.RegisterRequest;
import com.Bit_Builder.x_ray.app.Services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Auth", description = "Register and Login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register User", description = "Register as Doctor or Patient. Role field determines which type of user is created.")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        try{
            authService.register(request);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("can't save user", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/log-in")
    @Operation(summary = "Login", description = "Login with email and password. Returns JWT token, role and fullName.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        try{
            LoginResponse response = authService.logIn(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            log.error("failed to log in", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
package com.Bit_Builder.x_ray.app.Controller;

import com.Bit_Builder.x_ray.app.Dto.UserProfileResponse;
import com.Bit_Builder.x_ray.app.Services.UserServices;
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

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "User", description = "Common user APIs")
public class UserControllers {
    @Autowired
    private UserServices userServices;

    @GetMapping("/my-profile")
    @Operation(summary = "Get My Profile", description = "Returns profile of logged in user. Works for both Doctor and Patient roles.")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            UserProfileResponse myProfile = userServices.getMyProfile(email);
            return new ResponseEntity<>(myProfile, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error occured fething your profile");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload-pfp")
    @Operation(summary = "user can upload his x-ray", description = "Uploads profile picture of user in base64")
    public ResponseEntity<String> uploadPfp(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String res = userServices.uploadProfilePicture(authentication.getName(), file);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage(), "can't uplode pfp!");
            return new ResponseEntity<>("can't upload", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-pfp")
    @Operation(summary = "Delete pfp", description = "Deletes User Profile picture")
    public ResponseEntity<String> deletePfp(){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String res = userServices.deletePfp(auth.getName());
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), "can't upload pfp!");
            return new ResponseEntity<>("can't delete you! :(", HttpStatus.BAD_GATEWAY);
        }
    }
}
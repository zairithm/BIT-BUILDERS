package com.Bit_Builder.x_ray.app.Controller;

import com.Bit_Builder.x_ray.app.Dto.UserProfileResponse;
import com.Bit_Builder.x_ray.app.Services.UserServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserControllers {
    @Autowired
    private UserServices userServices;

    @GetMapping("/my-profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(){
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            UserProfileResponse myProfile = userServices.getMyProfile(email);
            return new ResponseEntity<>(myProfile, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error occured fething your profile");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

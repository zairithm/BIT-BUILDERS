package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.Repository.UserRepository;
import com.Bit_Builder.x_ray.app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // unwrap Optional properly
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())                          // use email
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(   // single role
                        "ROLE_" + user.getRole().name())))
                .build();
    }
}

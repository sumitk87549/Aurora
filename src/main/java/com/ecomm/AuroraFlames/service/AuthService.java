package com.ecomm.AuroraFlames.service;

import com.ecomm.AuroraFlames.config.JwtUtil;
import com.ecomm.AuroraFlames.dto.AuthRequest;
import com.ecomm.AuroraFlames.dto.AuthResponse;
import com.ecomm.AuroraFlames.dto.RegisterRequest;
import com.ecomm.AuroraFlames.entity.User;
import com.ecomm.AuroraFlames.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(User.Role.USER);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole().name());
    }

    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole().name());
    }

    public AuthResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // We reuse the existing token logic or just return the user info.
        // Since the frontend already has the token, we can just return a new one or the
        // same one.
        // For simplicity, let's generate a new one or expect the caller to have one.
        // Actually, the AuthResponse expects a token.
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole().name());
    }
}

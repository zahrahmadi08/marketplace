package com.ap.marketplace.service;

import com.ap.marketplace.domain.User;
import com.ap.marketplace.dto.auth.*;
import com.ap.marketplace.exception.ConflictException;
import com.ap.marketplace.repository.UserRepository;
import com.ap.marketplace.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new ConflictException("این نام کاربری قبلاً ثبت شده است");
        }
        if (userRepository.existsByPhone(req.phone())) {
            throw new ConflictException("این شماره موبایل قبلاً ثبت شده است");
        }
        User user = new User(req.username(), passwordEncoder.encode(req.password()),
                req.fullName(), req.email(), req.phone());
        userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest req) {
        // اعتبارسنجی رمز؛ در صورت نادرستی BadCredentialsException پرتاب می‌شود.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User user = userRepository.findByUsername(req.username()).orElseThrow();
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, UserResponse.from(user));
    }
}

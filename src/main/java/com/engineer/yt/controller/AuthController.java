package com.engineer.yt.controller;

import com.engineer.yt.configuration.config.JwtTokenProvider;
import com.engineer.yt.dto.request.LoginRequest;
import com.engineer.yt.dto.request.SignUpRequest;
import com.engineer.yt.dto.response.TokenResponse;
import com.engineer.yt.repository.UserRepository;
import com.engineer.yt.service.AuthService;
import io.quarkus.redis.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/account/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisClient redisClient;


    @PostMapping("/registry")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signup(signUpRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Sing up success");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse response = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody TokenResponse token) {
        TokenResponse response = authService.refreshToken(token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

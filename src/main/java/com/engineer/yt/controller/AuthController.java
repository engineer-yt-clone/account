package com.engineer.yt.controller;

import com.engineer.yt.configuration.config.JwtTokenProvider;
import com.engineer.yt.dto.request.LoginRequest;
import com.engineer.yt.dto.request.SignUpRequest;
import com.engineer.yt.dto.response.TokenResponse;
import com.engineer.yt.entity.User;
import com.engineer.yt.repository.UserRepository;
import com.engineer.yt.service.AuthService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

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
    private RedissonClient redissonClient;

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signup(signUpRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Sing up success");
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        User user = Optional.ofNullable(userRepository.findUserByEmail(loginRequest.getEmail()))
                .orElseThrow(() -> new RuntimeException("Email or password is incorrect"));
        int expiryAccessToken = 10000;
        int expiryRefreshToken = 1000000;
        String accessToken = tokenProvider.generateToken(user, 10000 );
        String refreshToken = tokenProvider.generateToken(user, 10000000 );
        TokenResponse tokenResponse = new TokenResponse(accessToken, expiryAccessToken, refreshToken, expiryRefreshToken
                , System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);

    }
}

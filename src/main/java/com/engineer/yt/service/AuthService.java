package com.engineer.yt.service;

import com.engineer.yt.dto.request.LoginRequest;
import com.engineer.yt.dto.request.SignUpRequest;
import com.engineer.yt.dto.response.TokenResponse;
import com.engineer.yt.entity.User;

public interface AuthService {

    public void signup(SignUpRequest signUpRequest);

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse refreshToken(TokenResponse token);
}

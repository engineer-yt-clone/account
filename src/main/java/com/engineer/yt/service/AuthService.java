package com.engineer.yt.service;

import com.engineer.yt.dto.request.SignUpRequest;
import com.engineer.yt.entity.User;

public interface AuthService {

    public void signup(SignUpRequest signUpRequest);
}

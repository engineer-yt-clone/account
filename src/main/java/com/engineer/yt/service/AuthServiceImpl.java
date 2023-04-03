package com.engineer.yt.service;

import com.engineer.yt.dto.request.SignUpRequest;
import com.engineer.yt.entity.User;
import com.engineer.yt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    UserRepository userRepository;
    @Override
    public void signup(SignUpRequest signUpRequest) {
        // TODO: validate user
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        //TODO : encode password.
        user.setPassword(signUpRequest.getPassword());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setRoleId(1L);


        userRepository.save(user);
    }
}

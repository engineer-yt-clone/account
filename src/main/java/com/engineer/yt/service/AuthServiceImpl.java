package com.engineer.yt.service;

import com.engineer.yt.common.constant.MessageConstant;
import com.engineer.yt.configuration.config.JwtTokenProvider;
import com.engineer.yt.configuration.security.PasswordSecurity;
import com.engineer.yt.dto.request.LoginRequest;
import com.engineer.yt.dto.request.SignUpRequest;
import com.engineer.yt.dto.response.TokenResponse;
import com.engineer.yt.entity.User;
import com.engineer.yt.repository.UserRepository;
import io.quarkus.redis.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value(value = "${jwt.accessExpires}")
    private int accessExpires;

    @Value(value = "${jwt.refreshExpires}")
    private int refreshExpires;



    @Override
    public void signup(SignUpRequest signUpRequest) {
        // TODO: validate user
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        String newPassword = PasswordSecurity.createHash(signUpRequest.getPassword());
        user.setPassword(newPassword);
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setRoleId(1L);

        userRepository.save(user);
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        User user = Optional.ofNullable(userRepository.findUserByEmail(loginRequest.getEmail()))
                .orElseThrow(() -> new RuntimeException("Email or password is incorrect"));
        boolean validate;
        try {
            validate = PasswordSecurity.verifyPassword(loginRequest.getPassword(), user.getPassword());
            if(Boolean.FALSE.equals(validate)) throw new RuntimeException();
        } catch (Exception e) {
            throw new RuntimeException("Phone number or password is incorrect.", e);
        }
        String accessToken = tokenProvider.generateToken(user, accessExpires );
        String refreshToken = tokenProvider.generateToken(user, refreshExpires );
        String accessTokenKey = MessageConstant.ACCOUNT + MessageConstant.ACCESS_TOKEN + user.getEmail();
        String refreshTokenKey = MessageConstant.ACCOUNT + MessageConstant.REFRESH_TOKEN + user.getEmail();
        this.redisClient.setex(accessTokenKey, String.valueOf(accessExpires), accessToken);
        this.redisClient.setex(refreshTokenKey, String.valueOf(refreshExpires), refreshToken);
        return new TokenResponse(accessToken, accessExpires, refreshToken, refreshExpires
                , System.currentTimeMillis());

    }

    @Override
    public TokenResponse refreshToken(TokenResponse token) {
        try{
            String email = tokenProvider.getEmailFromJWT(token.getRefreshToken());
            String key = this.getKeyOnRedis(email, MessageConstant.REFRESH_TOKEN);
            Long ttl = Optional.ofNullable(redisClient.ttl(key).toLong()).orElse(-2L);
            if (ttl == -2L) {
                throw new RuntimeException("The session has expired");
            }
            List<String> listKey = Arrays.asList(
                    this.getKeyOnRedis(email, MessageConstant.ACCESS_TOKEN),
                    this.getKeyOnRedis(email, MessageConstant.REFRESH_TOKEN));
            redisClient.del(listKey);

            User user = new User();
            user.setEmail(email);
            int accessExpire = accessExpires < ttl ? accessExpires : ttl.intValue();
            String accessToken = tokenProvider.generateToken(user, accessExpire);
            String refreshToken = tokenProvider.generateToken(user, Math.toIntExact(ttl));

            this.redisClient.setex(this.getKeyOnRedis(email, MessageConstant.ACCESS_TOKEN),
                    String.valueOf(accessExpire), accessToken);
            this.redisClient.setex(this.getKeyOnRedis(email, MessageConstant.REFRESH_TOKEN),
                    String.valueOf(ttl), refreshToken);
            return new TokenResponse(accessToken, accessExpires, refreshToken, refreshExpires
                    , System.currentTimeMillis());

        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getKeyOnRedis(String key, String type) {
        return MessageConstant.ACCOUNT + type + key;
    }
}

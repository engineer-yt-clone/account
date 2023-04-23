package com.engineer.yt.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TokenResponse implements Serializable {

    private static final long serialVersionUID = -6497697603656232884L;
    private String id;
    @JsonProperty("access_token")
    private String accessToken;
    private Integer accessExpires;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("refresh_expires")
    private Integer refreshExpires;
    private Long timestamp;

    public TokenResponse(String id, String accessToken, Integer accessExpires, String refreshToken, Integer refreshExpires, Long timestamp) {
        this.id = id;
        this.accessToken = accessToken;
        this.accessExpires = accessExpires;
        this.refreshToken = refreshToken;
        this.refreshExpires = refreshExpires;
        this.timestamp = timestamp;
    }

    public TokenResponse(String accessToken, Integer accessExpires, String refreshToken, Integer refreshExpires, Long timestamp) {
        this.accessToken = accessToken;
        this.accessExpires = accessExpires;
        this.refreshToken = refreshToken;
        this.refreshExpires = refreshExpires;
        this.timestamp = timestamp;
    }
}

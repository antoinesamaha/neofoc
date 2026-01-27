package com.foc.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("access_token_lifetime")
    private long accessTokenLifetime;

    @JsonProperty("refresh_token_lifetime")
    private long refreshTokenLifetime;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

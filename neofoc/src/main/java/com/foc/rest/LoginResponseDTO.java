package com.foc.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDTO {
    String access_token;
    String refresh_token;
    long access_token_lifetime;
    long refresh_token_lifetime;
}

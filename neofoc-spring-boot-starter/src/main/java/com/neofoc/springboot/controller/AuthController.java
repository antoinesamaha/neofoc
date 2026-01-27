package com.neofoc.springboot.controller;

import com.foc.admin.*;
import com.foc.rest.FocSimpleTokenAuth;
import com.foc.rest.LoginResponseDTO;
import com.neofoc.springboot.model.dto.LoginRequestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@RestController
@RequestMapping("foc/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final ObjectMapper objectMapper;

    public AuthController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping("login")
    protected ResponseEntity<LoginResponseDTO> doPost(HttpServletRequest request, @RequestBody LoginRequestDTO loginRequestDTO)
            throws ServletException, IOException {

        FocLoginAccess loginAccess = new FocLoginAccess();
        loginAccess.check(loginRequestDTO.getUsername(), loginRequestDTO.getPassword(), false, true);
        int status = loginAccess.getLoginStatus();

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();

        if(status == com.foc.Application.LOGIN_VALID){
            FocUser user = loginAccess.getUser();

            FocSimpleTokenAuth jwt = new FocSimpleTokenAuth();
            String token = jwt.generateToken(user.getName());
            loginResponseDTO.setAccessToken(token);
//            loginResponseDTO.setAccess_token(token);

            // Debug: Log the response body as JSON string
            String jsonBody = objectMapper.writeValueAsString(loginResponseDTO);
            System.out.println("Response body JSON: " + jsonBody);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(loginResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponseDTO);
        }
    }
}

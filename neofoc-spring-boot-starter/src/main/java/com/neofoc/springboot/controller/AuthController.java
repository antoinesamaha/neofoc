package com.neofoc.springboot.controller;

import com.foc.admin.*;
import com.foc.rest.FocSimpleTokenAuth;
import com.foc.rest.LoginResponseDTO;
import com.neofoc.springboot.model.dto.LoginRequestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("foc/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @PostMapping("login")
    protected ResponseEntity<LoginResponseDTO> doPost(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginRequestDTO loginRequestDTO)
            throws ServletException, IOException {

        FocLoginAccess loginAccess = new FocLoginAccess();
        loginAccess.check(loginRequestDTO.getUsername(), loginRequestDTO.getPassword(), false, true);
        int status = loginAccess.getLoginStatus();

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();

        if(status == com.foc.Application.LOGIN_VALID){
            FocUser user = loginAccess.getUser();

            FocSimpleTokenAuth jwt = new FocSimpleTokenAuth();
            String token = jwt.generateToken(user.getName());
            loginResponseDTO.setAccess_token(token);

            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
        }else{
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
        }

        return ResponseEntity.ok(loginResponseDTO);
    }

}

package com.neofoc.springboot.controller;

import com.foc.Globals;
import com.foc.admin.*;
import com.foc.list.FocList;
import com.foc.rest.FocSimpleTokenAuth;
import com.foc.rest.LoginResponseDTO;
import com.neofoc.springboot.model.dto.ChangePasswordDTO;
import com.neofoc.springboot.model.dto.LoginRequestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

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

    /**
     * POST /foc/auth/change-password
     *
     * Body: { "username": "FOCADMIN", "oldPassword": "current", "newPassword": "newPlainText" }
     *
     * - oldPassword is optional: if omitted the password is reset without verification (admin use).
     * - If oldPassword is provided it is verified before the change is applied.
     */
    @PostMapping("change-password")
    protected ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "username is required"));
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "newPassword is required"));
        }

        FocList userList = Globals.getApp().getFocDescMap().get(FocUserDesc.DB_TABLE_NAME).getFocList();
        if (userList == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "User list not available"));
        }

        FocUser user = (FocUser) userList.searchByPropertyStringValue(FocUserDesc.FLDNAME_NAME, dto.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        if (dto.getOldPassword() != null && !dto.getOldPassword().isBlank()) {
            boolean wrongPassword = user.checkEnteredPassword(dto.getOldPassword());
            if (wrongPassword) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Old password is incorrect"));
            }
        }

        user.setPassword_EncryptFirst(dto.getNewPassword());
        user.validate(true);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}

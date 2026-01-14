package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.AuthRequest;
import com.example.FurnitureShop.DTO.Request.UserRequest;
import com.example.FurnitureShop.DTO.Request.VerifyRequest;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.AuthResponse;
import com.example.FurnitureShop.DTO.Response.VerifyResponse;
import com.example.FurnitureShop.Service.Implement.AuthService;
import com.example.FurnitureShop.Util.JwtUtil;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/log-in")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successfully")
                .data(authService.authenticate(authRequest))
                .build());
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyResponse>> verify(@RequestBody @Valid VerifyRequest verifyRequest) throws ParseException, JOSEException {
        return ResponseEntity.ok(ApiResponse.<VerifyResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .data(jwtUtil.verify(verifyRequest.getToken()))
                .message("Verify successfully")
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody @Valid UserRequest userRequest) throws MethodArgumentNotValidException {
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Register successfully")
                .data(authService.register(userRequest))
                .build());
    }
}

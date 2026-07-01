package com.rahulsproject.connectu.user_service.controller;

import com.rahulsproject.connectu.user_service.dto.LoginRequestDto;
import com.rahulsproject.connectu.user_service.dto.LoginResponseDto;
import com.rahulsproject.connectu.user_service.dto.SignUpRequestDto;
import com.rahulsproject.connectu.user_service.dto.UserDto;
import com.rahulsproject.connectu.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        UserDto userDto = authService.signUp(signUpRequestDto);

        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }
}

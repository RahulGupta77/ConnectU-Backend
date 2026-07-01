package com.rahulsproject.connectu.user_service.service;

import com.rahulsproject.connectu.user_service.dto.LoginRequestDto;
import com.rahulsproject.connectu.user_service.dto.LoginResponseDto;
import com.rahulsproject.connectu.user_service.dto.SignUpRequestDto;
import com.rahulsproject.connectu.user_service.dto.UserDto;
import com.rahulsproject.connectu.user_service.entity.User;
import com.rahulsproject.connectu.user_service.exception.BadRequestException;
import com.rahulsproject.connectu.user_service.exception.ResourceNotFoundException;
import com.rahulsproject.connectu.user_service.repository.AuthRepository;
import com.rahulsproject.connectu.user_service.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {
        log.info("Signing up new User ");

        boolean exits = authRepository.existsByEmail(signUpRequestDto.getEmail());

        if(exits) throw new BadRequestException("User already exists with email: "+ signUpRequestDto.getEmail());

        User toBeSavedUser = modelMapper.map(signUpRequestDto, User.class);

        toBeSavedUser.setPassword(PasswordUtil.hashPassword(toBeSavedUser.getPassword()));

        User savedUser = authRepository.save(toBeSavedUser);

        return modelMapper.map(savedUser, UserDto.class);

    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("Logging in User  with email: {}", loginRequestDto.getEmail());
        User user = authRepository.findByEmail(loginRequestDto.getEmail());

        if(user == null){
            throw new ResourceNotFoundException("User not found with email: "+ loginRequestDto.getEmail());
        }

        boolean isPasswordSame = PasswordUtil.checkPassword(loginRequestDto.getPassword(), user.getPassword());

        if(!isPasswordSame) throw new BadRequestException("Password dont match with provided user with email: "+ loginRequestDto.getEmail());

        String token = jwtService.generateAccessToken(user);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setToken(token);

        return loginResponseDto;
    }
}

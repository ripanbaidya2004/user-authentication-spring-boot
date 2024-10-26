package com.ripan.production.controller;

import com.ripan.production.config.JwtProvider;
import com.ripan.production.exception.UserException;
import com.ripan.production.model.User;
import com.ripan.production.repository.UserRepository;
import com.ripan.production.request.LoginRequest;
import com.ripan.production.request.RegistrationRequest;
import com.ripan.production.response.LoginResponse;
import com.ripan.production.response.RegistrationResponse;
import com.ripan.production.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class LoginRegistrationAuthController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest request) throws UserException {
        User isExist = userRepository.findByEmail(request.getEmail());
        if(isExist != null) throw new UserException("user is already exist");

        User createUesrRequest = new User();
        createUesrRequest.setFullName(request.getFullName());
        createUesrRequest.setEmail(request.getEmail());
        createUesrRequest.setPassword(passwordEncoder.encode(request.getPassword()));

        User persistedUser = userRepository.save(createUesrRequest);

        Authentication authentication = new UsernamePasswordAuthenticationToken(createUesrRequest.getEmail(), createUesrRequest.getPassword());
        List<GrantedAuthority> authorityList = new ArrayList<>();
        String token = JwtProvider.generateToken(authentication);
        var response = new RegistrationResponse(token, "Registration Successfull");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws Exception {

        Authentication authentication = authenticate(request.getEmail(), request.getPassword());
        String token = JwtProvider.generateToken(authentication);
        var response = new LoginResponse(token, "Login Successfull");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Authentication authenticate(String email, String password) throws Exception {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if(userDetails == null) throw new BadCredentialsException("Invalid user");

        if(!passwordEncoder.matches(password, userDetails.getPassword())) throw new BadCredentialsException("Wrong password");
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

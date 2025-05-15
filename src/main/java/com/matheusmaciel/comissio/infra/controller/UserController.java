package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.dto.AuthenticationRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.LoginResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.UserRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.UserResponseDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.service.UserService;
import com.matheusmaciel.comissio.infra.config.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    public UserController(UserService userService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        var response = this.userService.createUser(userRequestDTO);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }



}

package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.dto.AuthenticationRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.LoginResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.UserRequestDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.repository.UserRepository;
import com.matheusmaciel.comissio.infra.config.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final TokenService tokenService;

  public UserController(AuthenticationManager authenticationManager,
                                  UserRepository repository,
                                  TokenService tokenService) {

    this.authenticationManager = authenticationManager;
    this.repository = repository;
    this.tokenService = tokenService;

  }

  @PostMapping("/login")
  public ResponseEntity login(@Valid @RequestBody AuthenticationRequestDTO data) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(),
                                                                data.password());
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var token = tokenService.generateToken((User) auth.getPrincipal());

    return ResponseEntity.ok(new LoginResponseDTO(token));
  }

  @PostMapping("/register")
  public ResponseEntity register(@Valid @RequestBody UserRequestDTO data) {
    if(this.repository.findByUsername(data.username()) != null){

      return ResponseEntity.badRequest().body("User already exists");
    }

    if(data.password() == null){
      return ResponseEntity.badRequest().body("Password cannot be null");
    }

    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
                                   
    User newUser = new User(data.name(),
                            data.username(),
                            data.email(),
                            encryptedPassword,
                            data.role());


    this.repository.save(newUser);

    return ResponseEntity.ok().build();
  }



}

package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.dto.LoginRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.LoginResponseDTO;
import com.matheusmaciel.comissio.core.domain.dto.UserRequestDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.repository.UserRepository;
import com.matheusmaciel.comissio.infra.config.security.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matheusmaciel.comissio.core.domain.dto.UserResponseDTO;

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
  public ResponseEntity login(@Valid @RequestBody LoginRequestDTO data) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(),
                                                                data.password());
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var token = tokenService.generateToken((User) auth.getPrincipal());

    return ResponseEntity.ok(new LoginResponseDTO(token));
  }


  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Tag(name = "Users", description = "Users register")
  @Operation(summary = "Register a new user", description = "This endpoint registers a new user in the system")
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
          @Content(schema = @Schema(implementation = User.class))
      })
  })
  @SecurityRequirement(name = "jwt_auth")
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

  @GetMapping("/list")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Tag(name = "Users", description = "Users list")
  @Operation(summary = "List all users", description = "This endpoint lists all users in the system")
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
          @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))
      })
  })
  @SecurityRequirement(name = "jwt_auth")
  public ResponseEntity getAllUsers() {
    return ResponseEntity.ok(this.repository.findAll());
  }



}

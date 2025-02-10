package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.LoginRequestDTO;
import com.ugustavob.finsuppapi.dto.LoginResponseDTO;
import com.ugustavob.finsuppapi.dto.RegisterRequestDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.security.TokenService;
import com.ugustavob.finsuppapi.useCases.user.CreateUserUseCase;
import com.ugustavob.finsuppapi.useCases.user.LoginUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints for login and register")
public class AuthController {
    private final LoginUserUseCase loginUserUseCase;
    private final TokenService tokenService;
    private final CreateUserUseCase createUserUseCase;

    @Operation(
            summary = "Login",
            description = "Authenticate a user using email and password, returning a JWT token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email or password",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Invalid email or password",
                                    summary = "Invalid email or password",
                                    name = "Invalid email or password"
                            ),
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Unauthorized",
                                    summary = "Unauthorized",
                                    name = "Unauthorized"
                            ),
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @Schema(name = "LoginRequestDTO", implementation = LoginRequestDTO.class)
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "Email and password", required = true)
            @Valid @RequestBody LoginRequestDTO loginRequest
    ) {
        try {
            UserEntity user = loginUserUseCase.execute(loginRequest);
            String token = tokenService.generateToken(user);

            return ResponseEntity.ok(new LoginResponseDTO(user.getName(), token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Register", description = "Create a new user account with name, email, and password.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Register successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "Email already exists",
                                    summary = "Email already exists",
                                    name = "Email already exists"
                            ),
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "Name is required",
                                            summary = "Name is required",
                                            name = "Name is required"
                                    ),
                                    @ExampleObject(
                                            value = "Invalid email",
                                            summary = "Invalid email",
                                            name = "Invalid email"
                                    ),
                                    @ExampleObject(
                                            value = "Password must have at least 6 characters",
                                            summary = "Password must have at least 6 characters",
                                            name = "Password must have at least 6 characters"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @Schema(name = "RegisterRequestDTO", implementation = RegisterRequestDTO.class)
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Parameter(description = "Name, email and password", required = true)
            @Valid @RequestBody RegisterRequestDTO body
    ){
        try {
            UserEntity newUser = createUserUseCase.execute(body);
            String token = tokenService.generateToken(newUser);

            return ResponseEntity.created(null).body(new LoginResponseDTO(newUser.getName(), token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

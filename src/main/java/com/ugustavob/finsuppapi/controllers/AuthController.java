package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.users.LoginRequestDTO;
import com.ugustavob.finsuppapi.dto.users.LoginResponseDTO;
import com.ugustavob.finsuppapi.dto.users.RegisterRequestDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
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
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Login successful",
                                        "type": "Success",
                                        "data": {
                                            "name": "User Name",
                                            "token": "jwt_token_here"
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email or password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Invalid email or password",
                                        "type": "Error"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Unauthorized",
                                        "type": "Error"
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "Email and password", required = true)
            @Valid @RequestBody LoginRequestDTO loginRequest
    ) {
        UserEntity user = loginUserUseCase.execute(loginRequest);
        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "Login successful",
                        new LoginResponseDTO(user.getName(), token)
                )
        );
    }

    @Operation(summary = "Register", description = "Create a new user account with name, email, and password.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Register successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "User created successfully",
                                        "type": "Success",
                                        "data": {
                                            "name": "User Name",
                                            "token": "jwt_token_here"
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "User already exists",
                                        "type": "Error",
                                        "field": "Email"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                                "message": "Name is required",
                                                "type": "Error"
                                            }
                                            """
                                    ),
                                    @ExampleObject(value = """
                                            {
                                                "message": "Invalid email",
                                                "type": "Error"
                                            }
                                            """
                                    ),
                                    @ExampleObject(value = """
                                            {
                                                "message": "Password must have at least 6 characters",
                                                "type": "Error"
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Parameter(description = "Name, email and password", required = true)
            @Valid @RequestBody RegisterRequestDTO body
    ) {
        UserEntity newUser = createUserUseCase.execute(body);
        String token = tokenService.generateToken(newUser);

        return ResponseEntity.created(null).body(
                new SuccessResponseDTO<>(
                        "User created successfully",
                        new LoginResponseDTO(newUser.getName(), token)
                )
        );
    }
}

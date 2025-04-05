package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.roles.AssignRoleRequestDTO;
import com.ugustavob.finsuppapi.dto.users.GetAllUsersResponseDTO;
import com.ugustavob.finsuppapi.dto.users.GetUserResponseDTO;
import com.ugustavob.finsuppapi.dto.users.RegisterRequestDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.SelfDelectionException;
import com.ugustavob.finsuppapi.exception.UserNotFoundException;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for users")
public class UsersController {
    private final BaseService baseService;
    private final UserService userService;

    @GetMapping("/me/")
    @Operation(summary = "Get user", description = "Get the authenticated user's details.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "User found",
                                              "type": "Success",
                                              "data": {
                                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                                "name": "João Silva",
                                                "email": "joao.silva@example.com"
                                              },
                                              "dataList": null,
                                              "pagination": null
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
                            examples = @ExampleObject(
                                    value = """
                                                {
                                                  "message": "Unauthorized",
                                                  "type": "Error",
                                                  "field": null
                                                }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                                {
                                                    "message": "User not found",
                                                    "type": "Error",
                                                    "field": null
                                                }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<GetUserResponseDTO>> getUser(HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        UserEntity user = userService.getUserById(userId);

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "User found",
                        new GetUserResponseDTO(user.getId(), user.getName(), user.getEmail())
                )
        );
    }

    @GetMapping("/")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users (Admin access required).")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Users found",
                                              "type": "Success",
                                              "data": null,
                                              "dataList": [
                                                {
                                                  "id": "123e4567-e89b-12d3-a456-426614174000",
                                                  "name": "João Silva",
                                                  "email": "joao.silva@example.com"
                                                },
                                                {
                                                  "id": "123e4567-e89b-12d3-a456-426614174001",
                                                  "name": "Maria Souza",
                                                  "email": "maria.souza@example.com"
                                                }
                                              ],
                                              "pagination": {
                                                "currentPage": 0,
                                                "pageSize": 10,
                                                "totalPages": 5,
                                                "totalElements": 50
                                              }
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
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Unauthorized",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "User is not an admin",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Users not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Users not found",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "bearer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponseDTO<GetAllUsersResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        Page<GetAllUsersResponseDTO> usersPage = userService.getAllUsers(page, size);

        if (usersPage == null) {
            throw new UserNotFoundException();
        }

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "Users found",
                        usersPage
                )
        );
    }

    @PutMapping("/me/")
    @Operation(summary = "Update user", description = "Update user by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "User updated",
                                              "type": "Success",
                                              "data": {
                                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                                "name": "João Silva",
                                                "email": "joao.silva@example.com"
                                              },
                                              "dataList": null,
                                              "pagination": null
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
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Unauthorized",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "User not found",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable entity",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "Name is required",
                                                      "type": "Error",
                                                      "field": "name"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "Email is required",
                                                      "type": "Error",
                                                      "field": "email"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "Invalid email",
                                                      "type": "Error",
                                                      "field": "email"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<GetUserResponseDTO>> updateUser(
            @Valid @RequestBody
            RegisterRequestDTO registerRequestDTO,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        UserEntity user = userService.updateUser(
                new UserEntity(
                        userId,
                        registerRequestDTO.name(),
                        registerRequestDTO.email(),
                        registerRequestDTO.password(),
                        null));
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "User updated",
                        new GetUserResponseDTO(user.getId(), user.getName(), user.getEmail())
                )
        );
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete user", description = "Delete a user (Restricted to admins. Users cannot delete " +
            "themselves).")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "User deleted",
                                              "type": "Success",
                                              "data": null,
                                              "dataList": null,
                                              "pagination": null
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
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Unauthorized",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "User is not an admin",
                                                      "type": "Error",
                                                      "field": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "You can't delete yourself",
                                                      "type": "Error",
                                                      "field": null
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "User not found",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "bearer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<SuccessResponseDTO<?>> deleteUser(HttpServletRequest request, @PathVariable UUID uuid) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        if (userId.toString().equals(uuid.toString())) {
            throw new SelfDelectionException();
        }

        userService.deleteUser(uuid);

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "User deleted"
                )
        );
    }

    @PostMapping("/{uuid}/roles")
    @Operation(summary = "Assign role", description = "Assign a new role to a user (Restricted to admins. A user " +
            "cannot be assigned a role they already have).")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role assigned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Role assigned successfully",
                                              "type": "Success",
                                              "data": {
                                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                                "name": "João Silva",
                                                "email": "joao.silva@example.com"
                                              },
                                              "dataList": null,
                                              "pagination": null
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
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Unauthorized",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "User not found",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "User already has role",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable entity",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Role is required",
                                              "type": "Error",
                                              "field": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "bearer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponseDTO<GetUserResponseDTO>> assignRole(
            HttpServletRequest request,
            @Valid @RequestBody AssignRoleRequestDTO assignRoleRequestDTO,
            @PathVariable UUID uuid
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        UserEntity updatedUser = userService.assignRole(assignRoleRequestDTO, uuid);

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "Role assigned successfully",
                        new GetUserResponseDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail())
                )
        );
    }


}

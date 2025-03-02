package com.ugustavob.finsuppapi.controllers;

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
import com.ugustavob.finsuppapi.useCases.role.AssignRoleUseCase;
import com.ugustavob.finsuppapi.useCases.user.DeleteUserUseCase;
import com.ugustavob.finsuppapi.useCases.user.GetUserUseCase;
import com.ugustavob.finsuppapi.useCases.user.UpdateUserUseCase;
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
    private final GetUserUseCase getUserUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
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
                            schema = @Schema(implementation = UserEntity.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            summary = "Unauthorized",
                                            value = "Unauthorized"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "User not found",
                                            summary = "User not found",
                                            value = "User not found"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @Schema(name = "UserEntity", implementation = UserEntity.class)
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        var id = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        UserEntity user = getUserUseCase.execute(id);

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "User found",
                        new GetUserResponseDTO(user.getId(),user.getName(),user.getEmail())
                )
        );
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieve a list of all users (Admin access required)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetAllUsersResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            summary = "Unauthorized",
                                            value = "Unauthorized"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Forbidden",
                                            summary = "User is not an admin",
                                            value = "User is not an admin"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Users not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Users not found",
                                            summary = "Users not found",
                                            value = "Users not found"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @Schema(name = "UserEntity", implementation = UserEntity.class)
    @SecurityRequirement(name = "bearer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/")
    public ResponseEntity<?> getAllUsers(
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
                            examples = {
                                    @ExampleObject(
                                            name = "User updated",
                                            summary = "User updated",
                                            value = "User updated successfully"
                                    )
                            },
                            schema = @Schema(implementation = GetUserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            summary = "Unauthorized",
                                            value = "Unauthorized"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "User not found",
                                            summary = "User not found",
                                            value = "User not found"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable entity",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Unprocessable entity",
                                            summary = "Name is required",
                                            value = "Name is required"
                                    ),
                                    @ExampleObject(
                                            name = "Unprocessable entity",
                                            summary = "Email is required",
                                            value = "Email is required"
                                    ),
                                    @ExampleObject(
                                            name = "Unprocessable entity",
                                            summary = "Invalid email",
                                            value = "Invalid email"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @Schema(name = "UserEntity", implementation = UserEntity.class)
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> updateUser(
            @Valid @RequestBody
            RegisterRequestDTO registerRequestDTO,
            HttpServletRequest request
    ) {
        var id = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        UserEntity user = updateUserUseCase.execute(
                new UserEntity(
                        id,
                        registerRequestDTO.name(),
                        registerRequestDTO.email(),
                        registerRequestDTO.password(),
                        null));
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "User updated",
                        new GetUserResponseDTO(user.getId(),user.getName(),user.getEmail())
                )
        );
    }

    @Operation(
            summary = "Delete user",
            description = "Delete a user (Restricted to admins. Users cannot delete themselves)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "User deleted",
                                            summary = "User deleted",
                                            value = "User deleted successfully"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            summary = "Unauthorized",
                                            value = "Unauthorized"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Forbidden",
                                            summary = "User is not an admin",
                                            value = "User is not an admin"
                                    ),
                                    @ExampleObject(
                                            name = "Forbidden",
                                            summary = "You can't delete yourself",
                                            value = "You can't delete yourself"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "User not found",
                                            summary = "User not found",
                                            value = "User not found"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, @PathVariable UUID uuid) {
        var id = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        if (id.toString().equals(uuid.toString())) {
            throw new SelfDelectionException();
        }

        deleteUserUseCase.execute(uuid);

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "User deleted"
                )
        );
    }

    @Operation(
            summary = "Assign role",
            description = "Assign a new role to a user (Restricted to admins. A user cannot be assigned a role they already have)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role assigned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserEntity.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            summary = "Unauthorized",
                                            value = "Unauthorized"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "User not found",
                                            summary = "User not found",
                                            value = "User not found"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Conflict",
                                            summary = "User already has role",
                                            value = "User already has role"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable entity",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Unprocessable entity",
                                            summary = "Role is required",
                                            value = "Role is required"
                                    )
                            },
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @Schema(name = "AssignRoleRequestDTO", implementation = AssignRoleRequestDTO.class)
    @SecurityRequirement(name = "bearer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{uuid}/roles")
    public ResponseEntity<Object> assignRole(
            HttpServletRequest request,
            @Valid @RequestBody AssignRoleRequestDTO assignRoleRequestDTO,
            @PathVariable UUID uuid
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        UserEntity updatedUser = assignRoleUseCase.execute(assignRoleRequestDTO, uuid);

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        "Role assigned successfully",
                        new GetUserResponseDTO(updatedUser.getId(),updatedUser.getName(),updatedUser.getEmail())
                )
        );
    }
}

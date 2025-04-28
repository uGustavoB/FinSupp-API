package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.banks.BankFilterDTO;
import com.ugustavob.finsuppapi.dto.banks.CreateBankRequestDTO;
import com.ugustavob.finsuppapi.entities.bank.BankEntity;
import com.ugustavob.finsuppapi.services.BankService;
import com.ugustavob.finsuppapi.services.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bank")
@RequiredArgsConstructor
@Tag(name = "Bank", description = "Bank API")
public class BankController {
    private final BaseService baseService;
    private final BankService bankService;

    @Operation(description = "Get all banks")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Banks retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Banks retrieved successfully",
                                            value = """
                                                    {
                                                      "message": "Banks retrieved",
                                                      "type": "Success",
                                                      "dataList": [
                                                        {
                                                          "id": 1,
                                                          "name": "Nubank"
                                                        },
                                                        {
                                                          "id": 2,
                                                          "name": "Banco Do Brasil"
                                                        },
                                                        {
                                                          "id": 3,
                                                          "name": "Santander"
                                                        },
                                                        {
                                                          "id": 4,
                                                          "name": "Itaú"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated.",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            ),
                                    }
                            )
                    }
            )
    })
    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<BankEntity>> getBankByName(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name,
            HttpServletRequest request
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        BankFilterDTO filter = new BankFilterDTO(id, name);

        List<BankEntity> banks = bankService.findAll(filter);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Banks retrieved",
                banks
        ));
    }

    @Operation(description = "Create a new bank")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Bank created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Bank created successfully",
                                            value = """
                                                    {
                                                      "message": "Bank created",
                                                      "type": "Success",
                                                      "data": {
                                                        "id": 1,
                                                        "name": "Nubank"
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated.",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the request is invalid.",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Validation error",
                                                              "type": "Error",
                                                              "dataList": [
                                                                {
                                                                  "description": "Nome do banco deve ter no máximo 20 caracteres",
                                                                  "field": "name"
                                                                }
                                                              ]
                                                            }
                                                            """,
                                                    summary = "Invalid request"
                                            ),
                                    }
                            )
                    }
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<BankEntity>> createBank(
        @Valid @RequestBody CreateBankRequestDTO createBankRequestDTO,
        HttpServletRequest request
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        BankEntity bank = bankService.createBank(createBankRequestDTO.name());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(bank.getId())
                .toUri();

        return ResponseEntity.created(location).body(new SuccessResponseDTO<>(
                "Bank created",
                bank
        ));
    }
}

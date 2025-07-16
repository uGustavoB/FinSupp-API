package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.dashboard.CategoriesSummaryResponseDTO;
import com.ugustavob.finsuppapi.dto.dashboard.SummaryResponseDTO;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "9. Dashboard", description = "Endpoints for dashboard data retrieval")
@RequiredArgsConstructor
public class DashboardController {
    private final BaseService baseService;
    private final DashboardService dashboardService;

    @Operation(summary = "Get Dashboard Summary", description = "Retrieve a summary of the user's dashboard.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard summary retrieved successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    value = """
                                                            {
                                                              "message": "Dashboard summary retrieved successfully",
                                                              "type": "Success",
                                                              "data": {
                                                                "monthlyIncome": 2500.00,
                                                                "monthlyExpense": 1000.00,
                                                                "netBalance": 1500.00,
                                                                "totalSavings": 5000.00,
                                                                "totalInvestments": 3000.00,
                                                                "totalValueForActiveSubscriptions": 54.50
                                                              }
                                                            }
                                                            """,
                                                    summary = "Successful response"
                                            )
                                    }
                            )
                    }
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
                                            )
                                    }
                            )
                    }
            )
    })
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public Object getDashboardSummary(
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        SummaryResponseDTO summary = dashboardService.getSummary(userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Dashboard summary retrieved successfully",
                summary
        ));
    }

    @Operation(summary = "Get Dashboard Categories Summary", description = "Retrieve a summary of the user's " +
            "dashboard by categories.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard categories summary retrieved successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    value = """
                                                            {
                                                               "message": "Dashboard summary retrieved successfully",
                                                               "type": "Success",
                                                               "dataList": [
                                                                 {
                                                                   "category": "Food",
                                                                   "totalAmount": 150
                                                                 },
                                                                 {
                                                                   "category": "Gift",
                                                                   "totalAmount": 290
                                                                 },
                                                                 {
                                                                   "category": "Other",
                                                                   "totalAmount": 25
                                                                 },
                                                                 {
                                                                   "category": "Shopping",
                                                                   "totalAmount": 40
                                                                 },
                                                                 {
                                                                   "category": "Transport Car",
                                                                   "totalAmount": 570
                                                                 }
                                                               ]
                                                             }
                                                            """,
                                                    summary = "Successful response"
                                            )
                                    }
                            )
                    }
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
                                            )
                                    }
                            )
                    }
            )
    })
    @GetMapping("/categories")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public Object getCategoriesSummary(
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        List<CategoriesSummaryResponseDTO> summary = dashboardService.getCategoriesSummary(userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Dashboard summary retrieved successfully",
                summary
        ));
    }
}



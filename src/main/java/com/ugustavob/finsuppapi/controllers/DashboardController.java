package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.dashboard.CategoriesSummaryResponseDTO;
import com.ugustavob.finsuppapi.dto.dashboard.SummaryResponseDTO;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequiredArgsConstructor
public class DashboardController {
    private final BaseService baseService;
    private final DashboardService dashboardService;

    @Operation(summary = "Get Dashboard Summary", description = "Retrieve a summary of the user's dashboard.")
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public Object getDashboardSummary(
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        SummaryResponseDTO summary =  dashboardService.getSummary(userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Dashboard summary retrieved successfully",
                summary
        ));
    }

    @Operation(summary = "Get Dashboard Summary", description = "Retrieve a summary of the user's dashboard.")
    @GetMapping("/categories")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public Object getCategoriesSummary(
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        List<CategoriesSummaryResponseDTO> summary =  dashboardService.getCategoriesSummary(userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Dashboard summary retrieved successfully",
                summary
        ));
    }
}

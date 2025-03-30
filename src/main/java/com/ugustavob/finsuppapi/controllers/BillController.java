package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.bills.BillFilterDTO;
import com.ugustavob.finsuppapi.dto.bills.BillResponseDTO;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.BillService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bills")
@Tag(name = "Bill", description = "Endpoints for bill management")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;
    private final BaseService baseService;

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> searchBills(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) BillStatus status,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            HttpServletRequest request
    ) {
        var userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        BillFilterDTO filter = new BillFilterDTO(userId, id, status, accountId, month, year);

        List<BillEntity> bills = billService.findAll(filter);

        List<BillResponseDTO> billsResponse = bills.stream()
                .map(billService::entityToResponseDto)
                .toList();

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Bills retrieved successfully",
                billsResponse
        ));
    }
}

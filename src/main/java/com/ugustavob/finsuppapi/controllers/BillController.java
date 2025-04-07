package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.bills.BillFilterDTO;
import com.ugustavob.finsuppapi.dto.bills.BillItemResponseDTO;
import com.ugustavob.finsuppapi.dto.bills.BillPayRequestDTO;
import com.ugustavob.finsuppapi.dto.bills.BillResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import com.ugustavob.finsuppapi.exception.BillAreadyPaidException;
import com.ugustavob.finsuppapi.exception.BillNotFoundException;
import com.ugustavob.finsuppapi.repositories.BillRepository;
import com.ugustavob.finsuppapi.services.AccountService;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.BillService;
import com.ugustavob.finsuppapi.services.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bills")
@Tag(name = "Bill", description = "Endpoints for bill management")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;
    private final BaseService baseService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BillRepository billRepository;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> searchBills(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) BillStatus status,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        BillFilterDTO filter = new BillFilterDTO(userId, id, status, accountId, month, year);

        List<BillEntity> bills = billService.findAll(filter);

        if (bills.isEmpty()) {
            throw new BillNotFoundException("No bills found with the provided filters");
        }

        List<BillResponseDTO> billsResponse = bills.stream()
                .map(billService::entityToResponseDto)
                .toList();

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Bills retrieved successfully",
                billsResponse
        ));
    }

    @GetMapping("/{id}/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getBillItems(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        BillEntity bill = billRepository.findById(id).orElseThrow(BillNotFoundException::new);

        Page<BillItemResponseDTO> billItems = billService.findBillItemsByBill(bill, userId, page, size);

        if (billItems.isEmpty()) {
            throw new BillNotFoundException("No items found for this bill");
        }

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Bill items retrieved successfully",
                billItems
        ));
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> payBill(
            @PathVariable Integer id,
            @RequestBody BillPayRequestDTO billStatusRequestDTO,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        AccountEntity account = accountService.getAccountByIdAndCompareWithUserId(billStatusRequestDTO.payWithAccount(), userId);

        BillEntity bill = billRepository.findById(id).orElseThrow(BillNotFoundException::new);

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BillAreadyPaidException();
        }

        BillEntity billPaid = transactionService.billPaymentTransaction(bill, account);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Bill paid successfully",
                billService.entityToResponseDto(billPaid)
        ));
    }
}

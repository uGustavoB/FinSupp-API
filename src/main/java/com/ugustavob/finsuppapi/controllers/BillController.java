package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bills")
@Tag(name = "8. Bill", description = "Endpoints for bill management")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;
    private final BaseService baseService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BillRepository billRepository;

    @Operation(summary = "Get all bills")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bills retrieved successfully",
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
                                                              "message": "Bills retrieved successfully",
                                                              "type": "Success",
                                                              "dataList": [
                                                                {
                                                                  "id": 1,
                                                                  "status": "OPEN",
                                                                  "totalAmount": 112,
                                                                  "accountId": 1,
                                                                  "startDate": "2025-04-04",
                                                                  "endDate": "2025-05-03",
                                                                  "dueDate": "2025-05-10"
                                                                },
                                                                {
                                                                  "id": 2,
                                                                  "status": "OPEN",
                                                                  "totalAmount": 50,
                                                                  "accountId": 1,
                                                                  "startDate": "2025-05-04",
                                                                  "endDate": "2025-06-03",
                                                                  "dueDate": "2025-06-10"
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
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No bills found with the provided filters",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when no bills are found",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "No bills found with the provided filters",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "No bills found with the provided filters"
                                            )
                                    }
                            )
                    }
            )
    })
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

    @Operation(summary = "Get bill items")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bill items retrieved successfully",
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
                                                              "message": "Bill items retrieved successfully",
                                                              "type": "Success",
                                                              "dataList": [
                                                                {
                                                                  "id": 1,
                                                                  "description": "Spotify Subscription",
                                                                  "amount": 12,
                                                                  "installmentNumber": 1,
                                                                  "billId": 1,
                                                                  "transactionId": null,
                                                                  "subscriptionId": 1
                                                                },
                                                                {
                                                                  "id": 3,
                                                                  "description": "Minecraft Movie",
                                                                  "amount": 50,
                                                                  "installmentNumber": 1,
                                                                  "billId": 1,
                                                                  "transactionId": 1,
                                                                  "subscriptionId": null
                                                                },
                                                                {
                                                                  "id": 4,
                                                                  "description": "New Clothes",
                                                                  "amount": 50,
                                                                  "installmentNumber": 1,
                                                                  "billId": 1,
                                                                  "transactionId": 2,
                                                                  "subscriptionId": null
                                                                }
                                                              ],
                                                              "pagination": {
                                                                "currentPage": 0,
                                                                "pageSize": 10,
                                                                "totalPages": 1,
                                                                "totalElements": 3
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
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No items found for this bill",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the bill is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Bill not found.",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Bill not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when no items are found for the bill",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "No items found for this bill",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "No items found for this bill"
                                            )
                                    }
                            )
                    }
            )
    })
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

    @Operation(summary = "Pay bill")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bill paid successfully",
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
                                                              "message": "Bill paid successfully",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "status": "PAID",
                                                                "totalAmount": 112,
                                                                "accountId": 1,
                                                                "startDate": "2025-04-04",
                                                                "endDate": "2025-05-03",
                                                                "dueDate": "2025-05-10"
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
                    responseCode = "400",
                    description = "Bad Request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the bill is Canceled.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "Bill is canceled",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Bill is canceled"
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
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bill or Card not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the bill is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Bill not found.",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Bill not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the card ID does not exist",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Card not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Card not found"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Bill already paid",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the bill is already paid.",
                                                    value = """
                                                            {
                                                              "code": 409,
                                                              "message": "Bill already paid",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Bill already paid"
                                            )
                                    }
                            )
                    }
            )
    })
    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> payBill(
            @PathVariable Integer id,
            @Valid @RequestBody BillPayRequestDTO billStatusRequestDTO,
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

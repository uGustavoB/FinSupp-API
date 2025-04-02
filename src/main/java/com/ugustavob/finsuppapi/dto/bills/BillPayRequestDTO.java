package com.ugustavob.finsuppapi.dto.bills;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BillPayRequestDTO(
        @NotNull UUID accountId
        ) {
}

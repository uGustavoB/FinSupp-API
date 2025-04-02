package com.ugustavob.finsuppapi.dto.bills;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record BillPayRequestDTO(
        @Schema(description = "Bill ID", example = "1") 
        @NotNull Integer accountId
        ) {
}

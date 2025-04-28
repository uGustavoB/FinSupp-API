package com.ugustavob.finsuppapi.dto.banks;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateBankRequestDTO(
        @NotBlank(message = "Nome do banco não pode ser nulo")
        @Length(min = 1, max = 20, message = "Nome do banco deve ter no máximo 20 caracteres")
        String name
) {
}

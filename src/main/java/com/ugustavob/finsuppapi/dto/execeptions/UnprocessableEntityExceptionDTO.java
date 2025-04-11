package com.ugustavob.finsuppapi.dto.execeptions;

public record UnprocessableEntityExceptionDTO(
        String description,
        String field
) {
}

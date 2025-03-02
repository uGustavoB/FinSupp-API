package com.ugustavob.finsuppapi.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    private String message;
    @Builder.Default
    private String type = "Error";
    private String field;
}

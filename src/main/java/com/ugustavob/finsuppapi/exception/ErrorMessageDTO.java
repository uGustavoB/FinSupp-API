package com.ugustavob.finsuppapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessageDTO {
    private String message;
    @Builder.Default
    private String type = "Error";
    private String field;
}

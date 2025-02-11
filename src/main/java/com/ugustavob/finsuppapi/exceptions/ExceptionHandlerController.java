package com.ugustavob.finsuppapi.exceptions;

import org.hibernate.annotations.NotFound;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerController {

    private final MessageSource messageSource;

    public ExceptionHandlerController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessageDTO> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDTO(e.getMessage(), ""));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorMessageDTO> dto = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(err -> {
            String message = messageSource.getMessage(err, LocaleContextHolder.getLocale());

            dto.add(new ErrorMessageDTO(message, err.getField()));
        });

        return new ResponseEntity<>(dto, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessageDTO> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(e.getMessage(), ""), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler
    public ResponseEntity<ErrorMessageDTO> handleUserAlreadyHasRoleException(UserAlreadyHasRoleException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(e.getMessage(), ""), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessageDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(e.getMessage(), ""), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessageDTO> handleAccountAlreadyExistsException(AccountAlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(e.getMessage(), ""), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessageDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(e.getLocalizedMessage(), ""), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
    }
}

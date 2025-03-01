package com.ugustavob.finsuppapi.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorMessageDTO> dto = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(err -> {
            String message = messageSource.getMessage(err, LocaleContextHolder.getLocale());

            dto.add(new ErrorMessageDTO(message, err.getField()));
        });

        return new ResponseEntity<>(dto, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SelfDelectionException.class)
    public ResponseEntity<ErrorMessageDTO> handleSelfDelectionException(SelfDelectionException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getMessage()).build(), HttpStatus.FORBIDDEN);
    }

//  User Exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyHasRoleException.class)
    public ResponseEntity<ErrorMessageDTO> handleUserAlreadyHasRoleException(UserAlreadyHasRoleException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

//  Account Exceptions
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleAccountAlreadyExistsException(AccountAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleAccountNotFoundException(AccountNotFoundException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getLocalizedMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageDTO> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(ErrorMessageDTO.builder().message(e.getMessage()).build(),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
    }
}

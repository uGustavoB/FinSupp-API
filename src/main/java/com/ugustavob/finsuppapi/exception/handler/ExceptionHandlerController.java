package com.ugustavob.finsuppapi.exception.handler;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.exception.*;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    private final MessageSource messageSource;

    public ExceptionHandlerController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponseDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorResponseDTO> dto = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(err -> {
            String message = messageSource.getMessage(err, LocaleContextHolder.getLocale());

            dto.add(new ErrorResponseDTO(message,"Error", err.getField().substring(0,1).toUpperCase() + err.getField().substring(1)));
        });

        return new ResponseEntity<>(dto, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SelfDelectionException.class)
    public ResponseEntity<ErrorResponseDTO> handleSelfDelectionException(SelfDelectionException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(), HttpStatus.FORBIDDEN);
    }

//  User Exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyHasRoleException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyHasRoleException(UserAlreadyHasRoleException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).field("Email").build(),
                HttpStatus.CONFLICT);
    }

//  Account Exceptions
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountAlreadyExistsException(AccountAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountNotFoundException(AccountNotFoundException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

//  Category Exceptions
    @ExceptionHandler(CategoryDescriptionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleCategoryDescriptionAlreadyExistsException(CategoryDescriptionAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).field("Description").build(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCategoryNotFoundException(CategoryNotFoundException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

//  Transaction Exceptions
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTransactionNotFoundException(TransactionNotFoundException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

//  Bill Exceptions
    @ExceptionHandler(BillNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBillNotFoundException(BillNotFoundException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BillAreadyPaidException.class)
    public ResponseEntity<ErrorResponseDTO> handleBillAreadyPaidException(BillAreadyPaidException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.CONFLICT);
    }

//  Card Exceptions
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardNotFoundException(CardNotFoundException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardAlreadyExistsException(CardAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.CONFLICT);
    }

//  Subscription Exceptions
    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleSubscriptionNotFoundException(SubscriptionNotFoundException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SubscriptionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleSubscriptionAlreadyExistsException(SubscriptionAlreadyExistsException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.CONFLICT);
    }

//  Others Exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(BusinessException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalStateException(IllegalStateException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getLocalizedMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(ErrorResponseDTO.builder().message(e.getMessage()).build(),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}

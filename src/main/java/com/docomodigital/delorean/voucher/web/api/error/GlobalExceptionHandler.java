package com.docomodigital.delorean.voucher.web.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleBadRequest(BadRequestException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDetails(
                exception.getErrorCode(),
                exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDetails(
                "MISSING_FIELD",
                "Invalid " + exception.getBindingResult().getObjectName() + ", " + exception.getBindingResult().getFieldError().getField() + " is mandatory"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(MissingServletRequestParameterException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDetails(
                "MISSING_REQUEST_PARAM",
                "Invalid request, parameter " + exception.getParameterName() + " is mandatory"));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(MissingServletRequestPartException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDetails(
                "MISSING_REQUEST_PARAM",
                "Invalid request, parameter " + exception.getRequestPartName() + " is mandatory"));
    }
}

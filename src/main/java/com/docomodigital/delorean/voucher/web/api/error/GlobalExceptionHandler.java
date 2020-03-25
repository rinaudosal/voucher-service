package com.docomodigital.delorean.voucher.web.api.error;

import com.docomodigital.delorean.voucher.config.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MISSING_PARAMETER = "Invalid request, parameter %s is mandatory";

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
                String.format("Invalid %s, %s is mandatory", exception.getBindingResult().getObjectName(), exception.getBindingResult().getFieldError().getField())));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(MissingServletRequestParameterException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDetails(
                "MISSING_REQUEST_PARAM",
                String.format(MISSING_PARAMETER, exception.getParameterName())));
    }


    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(MissingServletRequestPartException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDetails(
                "MISSING_REQUEST_PARAM",
                String.format(MISSING_PARAMETER, exception.getRequestPartName())));
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(HttpMessageConversionException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDetails("MISSING_REQUEST_PARAM", exception.getLocalizedMessage()));
    }
}

package az.ubank.msauth.controller.error;

import az.ubank.msauth.dto.response.ErrorDto;

import az.ubank.msauth.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static az.ubank.msauth.exception.ErrorCodes.*;

@ControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<Object> handleWrongPasswordException(WrongPasswordException ex,
                                                               WebRequest webRequest) {
        log.info(ex.toString());

        return handleExceptionInternal(ex, ErrorDto.builder()
                        .code(WRONG_PASSWORD_EXCEPTION)
                        .message(ex.getMessage())
                        .build(),
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, webRequest);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex,
                                                               WebRequest webRequest) {
        log.info(ex.toString());

        return handleExceptionInternal(ex, ErrorDto.builder()
                        .code(NOT_FOUND)
                        .message(ex.getMessage())
                        .build(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler(UserExistedException.class)
    public ResponseEntity<Object> handleUserExistedException(UserExistedException ex,
                                                               WebRequest webRequest) {
        log.info(ex.toString());

        return handleExceptionInternal(ex, ErrorDto.builder()
                        .code(EXISTED_USER_FOUND)
                        .message(ex.getMessage())
                        .build(),
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, webRequest);
    }

    @ExceptionHandler(EmailNotCorrectException.class)
    public ResponseEntity<Object> handleEmailNotCorrectException(EmailNotCorrectException ex,
                                                             WebRequest webRequest) {
        log.info(ex.toString());

        return handleExceptionInternal(ex, ErrorDto.builder()
                        .code(EMAIL_WRONG)
                        .message(ex.getMessage())
                        .build(),
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, webRequest);
    }

    @ExceptionHandler(NullBlankFoundException.class)
    public ResponseEntity<Object> handleNullFiledFoundException(NullBlankFoundException ex,
                                                                WebRequest webRequest) {
        log.info(ex.toString());

        return handleExceptionInternal(ex, ErrorDto.builder()
                        .code(NULL_FIELD)
                        .message(ex.getMessage())
                        .build(),
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, webRequest);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception ex,
                                                     WebRequest webRequest) {
        log.info(ex.getMessage());

        return handleExceptionInternal(ex, ErrorDto.builder()
                        .code(UNEXPECTED_EXCEPTION)
                        .message(ex.getMessage())
                        .build(),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }
}
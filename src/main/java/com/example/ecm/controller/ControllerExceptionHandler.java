package com.example.ecm.controller;

import com.example.ecm.exception.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Обработчик исключений, который обрабатывает стандартные и пользовательские ошибки в приложении.
 * Предоставляет обработку исключений для валидации, нарушения целостности данных и ошибок доступа.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Обрабатывает исключения {@link MethodArgumentNotValidException}, возникающие при ошибке валидации.
     *
     * @param ex Исключение, содержащее информацию о полях, не прошедших валидацию.
     * @return Карта с ошибками и статусом 400 BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключения {@link DataIntegrityViolationException}, возникающие при нарушении целостности данных.
     *
     * @param ex Исключение целостности данных.
     * @return Сообщение об ошибке и статус 409 CONFLICT.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ApiResponse(responseCode = "409", description = "Конфликт целостности данных")
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Обрабатывает исключения {@link NotFoundException}, когда запрашиваемый ресурс не найден.
     *
     * @param ex Исключение "ресурс не найден".
     * @return Сообщение об ошибке и статус 404 NOT_FOUND.
     */
    @ExceptionHandler(NotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Ресурс не найден")
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключения {@link ServerException}, возникающие при внутренней ошибке сервера.
     *
     * @param ex Исключение внутренней ошибки сервера.
     * @return Сообщение об ошибке и статус 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(ServerException.class)
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    public ResponseEntity<String> handleServerException(ServerException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает исключения {@link ForbiddenException}, когда доступ к ресурсу запрещен.
     *
     * @param ex Исключение "доступ запрещен".
     * @return Сообщение об ошибке и статус 403 FORBIDDEN.
     */
    @ExceptionHandler(ForbiddenException.class)
    @ApiResponse(responseCode = "403", description = "Доступ к ресурсу запрещен")
    public ResponseEntity<String> handleForbiddenException(ForbiddenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}

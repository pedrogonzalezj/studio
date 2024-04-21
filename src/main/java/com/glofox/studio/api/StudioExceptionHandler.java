package com.glofox.studio.api;

import com.glofox.studio.domain.classes.AlreadyBookedClassException;
import com.glofox.studio.domain.classes.DateAlreadyTakenException;
import com.glofox.studio.domain.classes.ClassNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class StudioExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { AlreadyBookedClassException.class })
    protected ResponseEntity<Object> handleAlreadyBookedClassException(AlreadyBookedClassException ex, WebRequest request) {
        log.error("user request {} failed, class already booked.\n<{}>", request, ex.getMember());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.RESOURCE_TAKEN.name());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new GenericResponse<>(error, null));
    }

    @ExceptionHandler(value = { ClassNotFoundException.class })
    protected ResponseEntity<Object> handleClassNotFoundException(ClassNotFoundException ex, WebRequest request) {
        log.error("user request {} failed, class {} not found.\n<{}>", request, ex.getClassIdentifier(), ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.ENTITY_NOT_FOUND.name());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse<>(error, null));
    }

    @ExceptionHandler(value = { DateAlreadyTakenException.class })
    protected ResponseEntity<Object> handleDateAlreadyTakenException(DateAlreadyTakenException ex, WebRequest request) {
        log.error("user request {} failed, date already taken by another class.\n<{}>", request, ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.RESOURCE_TAKEN.name());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new GenericResponse<>(error, null));
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("user request {} failed.\n<{}>", request, ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.BAD_PARAMS.name());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericResponse<>(error, null));
    }

    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("user request {} failed.\n something failed while processing user request.\n<{}>", request, ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.SERVICE_ERROR.name());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(error, null));
    }

    public enum ErrorTypes {
        ENTITY_NOT_FOUND,
        BAD_PARAMS,
        RESOURCE_TAKEN,
        SERVICE_ERROR
    }

}

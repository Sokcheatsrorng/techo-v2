package co.istad.techco.techco.exception;

import co.istad.techco.techco.base.BasedError;
import co.istad.techco.techco.base.BasedErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ServiceException {

    // ✅ Handle validation errors (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BasedErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        BasedError<Map<String, String>> basedError = new BasedError<>();
        basedError.setCode("400");
        basedError.setDescription(errors);

        BasedErrorResponse basedErrorResponse = new BasedErrorResponse();
        basedErrorResponse.setError(basedError);

        return ResponseEntity.badRequest().body(basedErrorResponse);
    }

    // ✅ Handle custom service exceptions (ResponseStatusException)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BasedErrorResponse> handleServiceError(ResponseStatusException e) {
        BasedError<String> basedError = new BasedError<>();
        basedError.setCode(String.valueOf(e.getStatusCode().value()));
        basedError.setDescription(e.getReason());

        BasedErrorResponse basedErrorResponse = new BasedErrorResponse();
        basedErrorResponse.setError(basedError);

        return ResponseEntity.status(e.getStatusCode()).body(basedErrorResponse);
    }

    // ✅ Handle generic exceptions (fallback for any other errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasedErrorResponse> handleGenericException(Exception e) {
        BasedError<String> basedError = new BasedError<>();
        basedError.setCode("500");
        basedError.setDescription("An unexpected error occurred: " + e.getMessage());

        BasedErrorResponse basedErrorResponse = new BasedErrorResponse();
        basedErrorResponse.setError(basedError);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(basedErrorResponse);
    }
}

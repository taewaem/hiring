package gabia.hiring.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HiringException.class)
    public ResponseEntity<ErrorResponse> handlerBusinessException(HiringException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode()));
    }

}

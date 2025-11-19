package com.aissummarizer.jennet.exceptions;

/**
 * Global exception handler for the application.
 * <p>
 * Converts custom exceptions into well-structured HTTP responses.
 * Ensures consistent error formatting across all controllers.
 */
//@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Handles cases where a requested resource was not found.
     *
     * @param ex thrown exception
     * @return HTTP 404 with error details


    /*@ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(java.util.Map.of("error", ex.getMessage()));
    }
    */

    /**
     * Handles input validation and similar client-side errors.
     *
     * @param ex thrown exception
     * @return HTTP 400 with error details
     */
    /*@ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", ex.getMessage()));
    }*/

    /**
     * Handles any business rule violation that isn't a bad request or not found.
     *
     * @param ex thrown exception
     * @return HTTP 422 with error details
     */
   /* @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(java.util.Map.of("error", ex.getMessage()));
    }*/

    /**
     * Catches any unexpected system-level failure.
     * <p>
     * We avoid returning exception details intentionally
     * — users should not see stack traces.
     *
     * @param ex thrown exception
     * @return HTTP 500 with generic error message
     */
    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        // NOTE: logging should be added here in real apps
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Internal server error"));
    }*/
}

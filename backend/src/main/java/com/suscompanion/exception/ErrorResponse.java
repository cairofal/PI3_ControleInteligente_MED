package com.suscompanion.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response for the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * Error message.
     */
    private String message;

    /**
     * Validation errors (field name -> error message).
     */
    private Map<String, String> errors;

    /**
     * Timestamp of the error.
     */
    private LocalDateTime timestamp;
}
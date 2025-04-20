package com.suscompanion.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Create a new ResourceNotFoundException with the specified message.
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Create a new ResourceNotFoundException for a resource with the specified ID.
     * @param resourceName the name of the resource
     * @param id the ID of the resource
     * @return a new ResourceNotFoundException
     */
    public static ResourceNotFoundException forResource(String resourceName, Object id) {
        return new ResourceNotFoundException(resourceName + " não encontrado com ID: " + id);
    }

    /**
     * Create a new ResourceNotFoundException for a resource with the specified field value.
     * @param resourceName the name of the resource
     * @param fieldName the name of the field
     * @param value the value of the field
     * @return a new ResourceNotFoundException
     */
    public static ResourceNotFoundException forResourceWithField(String resourceName, String fieldName, Object value) {
        return new ResourceNotFoundException(resourceName + " não encontrado com " + fieldName + ": " + value);
    }
}
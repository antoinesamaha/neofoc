package com.neofoc.springboot.service;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Validates filter requests to prevent SQL injection and invalid queries
 * Implements security through field whitelisting and operator validation
 */
@Service
public class FilterValidator {

    /**
     * Whitelist of allowed filter operators
     * Only these operators can be used in filter conditions
     */
    private static final Set<String> ALLOWED_OPERATORS = Set.of(
        "=", ">=", "<=", ">", "<", "!=",
        "in", "between", "like", "contains",
        "isNull", "isNotNull"
    );

    /**
     * Whitelist of allowed sort directions
     */
    private static final Set<String> ALLOWED_DIRECTIONS = Set.of("ASC", "DESC");

    /**
     * Validate that a field exists in the FocDesc
     * This prevents filtering on non-existent fields and potential SQL injection
     *
     * @param focDesc The entity descriptor
     * @param fieldName The field name to validate
     * @throws IllegalArgumentException if field doesn't exist
     */
    public void validateField(FocDesc focDesc, String fieldName) {
        if (focDesc == null) {
            throw new IllegalArgumentException("FocDesc cannot be null");
        }

        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }

        // Check if field exists in FocDesc (whitelist approach)
        FField field = focDesc.getFieldByName(fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName + " does not exist in entity " + focDesc.getName());
        }
    }

    /**
     * Validate that an operator is in the allowed list
     * This prevents SQL injection through operator manipulation
     *
     * @param operator The operator to validate
     * @throws IllegalArgumentException if operator is not allowed
     */
    public void validateOperator(String operator) {
        if (operator == null || operator.trim().isEmpty()) {
            throw new IllegalArgumentException("Operator cannot be null or empty");
        }

        if (!ALLOWED_OPERATORS.contains(operator)) {
            throw new IllegalArgumentException("Invalid operator: " + operator + ". Allowed operators: " + ALLOWED_OPERATORS);
        }
    }

    /**
     * Validate sort direction
     *
     * @param direction The direction to validate (ASC or DESC)
     * @throws IllegalArgumentException if direction is not allowed
     */
    public void validateDirection(String direction) {
        if (direction == null || direction.trim().isEmpty()) {
            throw new IllegalArgumentException("Sort direction cannot be null or empty");
        }

        String upperDirection = direction.toUpperCase();
        if (!ALLOWED_DIRECTIONS.contains(upperDirection)) {
            throw new IllegalArgumentException("Invalid sort direction: " + direction + ". Allowed: ASC, DESC");
        }
    }

    /**
     * Validate that a value is appropriate for the given field type
     *
     * @param field The field definition
     * @param value The value to validate
     * @throws IllegalArgumentException if value type doesn't match field type
     */
    public void validateValueType(FField field, Object value) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }

        if (value == null) {
            return; // Null is valid for all types
        }

        String fieldType = field.getClass().getSimpleName();

        // Basic type checking - actual conversion will be done by FilterParser
        // This is just an early validation to catch obvious mismatches
        if (fieldType.contains("Int") || fieldType.contains("Num") || fieldType.contains("Double")) {
            if (!(value instanceof Number)) {
                // Try to parse as number
                try {
                    Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Field " + field.getName() + " expects numeric value, got: " + value);
                }
            }
        } else if (fieldType.contains("Bool")) {
            if (!(value instanceof Boolean)) {
                String strValue = value.toString().toLowerCase();
                if (!strValue.equals("true") && !strValue.equals("false") && !strValue.equals("1") && !strValue.equals("0")) {
                    throw new IllegalArgumentException("Field " + field.getName() + " expects boolean value, got: " + value);
                }
            }
        }
        // Other types (String, Date, Object) are validated during formatting
    }

    /**
     * Validate pagination parameters
     *
     * @param start Starting offset
     * @param count Number of records
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void validatePagination(int start, int count) {
        if (start < 0) {
            throw new IllegalArgumentException("Pagination start must be >= 0, got: " + start);
        }

        if (count <= 0) {
            throw new IllegalArgumentException("Pagination count must be > 0, got: " + count);
        }

        if (count > 10000) {
            throw new IllegalArgumentException("Pagination count cannot exceed 10000, got: " + count);
        }
    }
}

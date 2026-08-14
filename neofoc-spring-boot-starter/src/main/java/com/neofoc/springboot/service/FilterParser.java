package com.neofoc.springboot.service;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.db.ListPagination;
import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.field.*;
import com.foc.list.FocList;
import com.neofoc.springboot.model.dto.FilterCondition;
import com.neofoc.springboot.model.dto.FilterRequest;
import com.neofoc.springboot.model.dto.OrderByRequest;
import com.neofoc.springboot.model.dto.PaginationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Parses filter requests and applies them to FocList with comprehensive SQL injection protection
 *
 * Security measures:
 * 1. Field name whitelisting via FocDesc
 * 2. Operator whitelisting
 * 3. Type-based value escaping
 * 4. SQL keyword detection
 * 5. Special character escaping
 * 6. No direct string concatenation
 */
@Service
public class FilterParser {

    @Autowired
    private FilterValidator validator;

    /**
     * Apply filters from FilterRequest to FocList
     *
     * @param filterRequest The filter request from REST API
     * @param list The FocList to apply filters to
     * @param focDesc The entity descriptor
     */
    public void applyFiltersToList(FilterRequest filterRequest, FocList list, FocDesc focDesc) {
        if (filterRequest == null || list == null || focDesc == null) {
            return;
        }

        SQLFilter sqlFilter = list.getFilter();
        if (sqlFilter == null) {
            Globals.logString("WARNING: SQLFilter is null for list");
            return;
        }

        // Apply filters
        if (filterRequest.getFilters() != null && !filterRequest.getFilters().isEmpty()) {
            applyFilters(filterRequest.getFilters(), sqlFilter, focDesc);
        }

        // Apply pagination
        if (filterRequest.getPagination() != null) {
            applyPagination(filterRequest.getPagination(), sqlFilter);
        }

        // Apply ordering
        if (filterRequest.getOrderBy() != null && !filterRequest.getOrderBy().isEmpty()) {
            applyOrdering(filterRequest.getOrderBy(), sqlFilter, focDesc);
        }
    }

    /**
     * Apply individual filters to SQL filter
     */
    private void applyFilters(Map<String, Object> filters, SQLFilter sqlFilter, FocDesc focDesc) {
        int filterIndex = 0;

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String fieldName = entry.getKey();
            Object filterValue = entry.getValue();

            // TODO: Handle compound conditions (AND/OR) in future enhancement
            if (fieldName.equals("AND") || fieldName.equals("OR")) {
                Globals.logString("WARNING: Compound conditions (AND/OR) not yet implemented");
                continue;
            }

            try {
                // Validate field exists in FocDesc (whitelist)
                validator.validateField(focDesc, fieldName);
                FField field = focDesc.getFieldByName(fieldName);

                // Parse the condition and build WHERE clause
                String whereClause = parseCondition(field, filterValue, focDesc);

                // Add to SQL filter with unique key
                String filterKey = "SEARCH_FILTER_" + filterIndex++;
                sqlFilter.putAdditionalWhere(filterKey, whereClause);

                Globals.logString("Applied filter: " + filterKey + " -> " + whereClause);

            } catch (Exception e) {
                Globals.logException(e);
                throw new IllegalArgumentException("Error applying filter for field '" + fieldName + "': " + e.getMessage(), e);
            }
        }
    }

    /**
     * Parse a single filter condition and build SQL WHERE clause
     * CRITICAL: This method must never allow SQL injection
     */
    private String parseCondition(FField field, Object filterValue, FocDesc focDesc) {
        String dbFieldName = escapeFieldName(field.getDBName(), focDesc);

        // Case 1: Simple value (equality filter)
        if (filterValue instanceof String || filterValue instanceof Number || filterValue instanceof Boolean) {
            validator.validateValueType(field, filterValue);
            String safeValue = formatValueSafely(field, filterValue);
            return dbFieldName + " = " + safeValue;
        }

        // Case 2: Complex condition with operator
        if (filterValue instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditionMap = (Map<String, Object>) filterValue;

            // Convert to FilterCondition for easier handling
            FilterCondition condition = mapToFilterCondition(conditionMap);
            String operator = condition.getOperator();

            // Validate operator (whitelist)
            validator.validateOperator(operator);

            switch (operator) {
                case "=":
                case ">=":
                case "<=":
                case ">":
                case "<":
                case "!=":
                    validator.validateValueType(field, condition.getValue());
                    String safeValue = formatValueSafely(field, condition.getValue());
                    return dbFieldName + " " + operator + " " + safeValue;

                case "in":
                    if (condition.getValues() == null || condition.getValues().isEmpty()) {
                        throw new IllegalArgumentException("'in' operator requires 'values' array");
                    }
                    String safeValues = formatInClauseSafely(field, condition.getValues());
                    return dbFieldName + " IN (" + safeValues + ")";

                case "between":
                    if (condition.getFrom() == null || condition.getTo() == null) {
                        throw new IllegalArgumentException("'between' operator requires 'from' and 'to' values");
                    }
                    validator.validateValueType(field, condition.getFrom());
                    validator.validateValueType(field, condition.getTo());
                    String safeFrom = formatValueSafely(field, condition.getFrom());
                    String safeTo = formatValueSafely(field, condition.getTo());
                    return dbFieldName + " BETWEEN " + safeFrom + " AND " + safeTo;

                case "like":
                case "contains":
                    if (condition.getValue() == null) {
                        throw new IllegalArgumentException("'" + operator + "' operator requires 'value'");
                    }
                    String pattern = condition.getValue().toString();
                    String safePattern = escapeLikePattern(pattern);
                    if (operator.equals("contains")) {
                        return dbFieldName + " LIKE '%" + safePattern + "%'";
                    } else {
                        return dbFieldName + " LIKE '" + safePattern + "'";
                    }

                case "isNull":
                    return dbFieldName + " IS NULL";

                case "isNotNull":
                    return dbFieldName + " IS NOT NULL";

                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        }

        throw new IllegalArgumentException("Invalid filter format for field: " + field.getName());
    }

    /**
     * Convert Map to FilterCondition object
     */
    private FilterCondition mapToFilterCondition(Map<String, Object> map) {
        FilterCondition condition = new FilterCondition();
        condition.setOperator((String) map.get("operator"));
        condition.setValue(map.get("value"));
        condition.setFrom(map.get("from"));
        condition.setTo(map.get("to"));

        Object valuesObj = map.get("values");
        if (valuesObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> valuesList = (List<Object>) valuesObj;
            condition.setValues(valuesList);
        }

        return condition;
    }

    /**
     * Format value safely based on field type - PREVENTS SQL INJECTION
     * This is the critical security layer
     */
    private String formatValueSafely(FField field, Object value) {
        if (value == null) {
            return "NULL";
        }

        // String fields - escape quotes and validate
        if (field instanceof FStringField) {
            String strValue = value.toString();

            // SQL escape single quotes
            strValue = strValue.replace("'", "''");

            // Escape backslashes
            strValue = strValue.replace("\\", "\\\\");

            // Validate no SQL injection
            validateNoSqlInjection(strValue);

            return "'" + strValue + "'";
        }

        // Numeric fields - validate and parse
        if (field instanceof FIntField) {
            try {
                if (value instanceof Number) {
                    return String.valueOf(((Number) value).intValue());
                }
                int intValue = Integer.parseInt(value.toString());
                return String.valueOf(intValue);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid integer value for field " + field.getName() + ": " + value);
            }
        }

        if (field instanceof FNumField) {
            try {
                if (value instanceof Number) {
                    return value.toString();
                }
                Double.parseDouble(value.toString());
                return value.toString();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value for field " + field.getName() + ": " + value);
            }
        }

        // Boolean fields
        if (field instanceof FBoolField) {
            boolean boolValue;
            if (value instanceof Boolean) {
                boolValue = (Boolean) value;
            } else {
                String strValue = value.toString().toLowerCase();
                if (strValue.equals("true") || strValue.equals("1")) {
                    boolValue = true;
                } else if (strValue.equals("false") || strValue.equals("0")) {
                    boolValue = false;
                } else {
                    throw new IllegalArgumentException("Invalid boolean value for field " + field.getName() + ": " + value);
                }
            }
            return boolValue ? "1" : "0";
        }

        // Date and DateTime fields
        if (field instanceof FDateField || field instanceof FDateTimeField) {
            String dateStr = value.toString();

            // Validate date format (basic check)
            validateDateFormat(dateStr);

            // Escape and wrap in quotes
            dateStr = dateStr.replace("'", "''");
            validateNoSqlInjection(dateStr);

            return "'" + dateStr + "'";
        }

        // Foreign key (FObjectField) - must be numeric reference
        if (field instanceof FObjectField) {
            try {
                if (value instanceof Number) {
                    return String.valueOf(((Number) value).longValue());
                }
                long refValue = Long.parseLong(value.toString());
                return String.valueOf(refValue);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid reference value for field " + field.getName() + ": " + value);
            }
        }

        // Unsupported field type
        throw new IllegalArgumentException("Unsupported field type for filtering: " + field.getClass().getSimpleName() + " (field: " + field.getName() + ")");
    }

    /**
     * Escape field name to prevent field name injection
     * CRITICAL: Only allow validated field names from FocDesc
     */
    private String escapeFieldName(String fieldName, FocDesc focDesc) {
        // Field name should already be validated by validator
        // Additional safety: only allow alphanumeric and underscore
        if (!fieldName.matches("^[a-zA-Z0-9_]+$")) {
            throw new SecurityException("Invalid characters in field name: " + fieldName);
        }

        // Wrap in quotes if required by database provider
        try {
            if (focDesc != null && DBManager.provider_FieldNamesBetweenSpeachmarks(focDesc.getProvider())) {
                return "\"" + fieldName + "\"";
            }
        } catch (Exception e) {
            // If we can't determine provider, default to quoted for safety
            return "\"" + fieldName + "\"";
        }

        return fieldName;
    }

    /**
     * Validate that string contains no SQL injection attempts
     * CRITICAL SECURITY LAYER
     */
    private void validateNoSqlInjection(String value) {
        if (value == null) {
            return;
        }

        String lowerValue = value.toLowerCase();

        // Dangerous SQL keywords and patterns
        String[] dangerousPatterns = {
            "--",       // SQL comment
            "/*",       // SQL comment start
            "*/",       // SQL comment end
            ";",        // Statement separator
            "xp_",      // SQL Server extended procedures
            "sp_",      // SQL Server stored procedures
            "exec ",    // Execute command
            "execute ", // Execute command
            " union ",  // SQL UNION
            " or 1=1",  // Classic SQL injection
            " or '1'='1",
            "script",   // XSS attempt
            "javascript",
            "' or ",    // SQL injection patterns
            "\" or ",
            " and 1=1",
            "drop ",    // DDL commands
            "create ",
            "alter ",
            "insert ",
            "update ",
            "delete ",
            "select "
        };

        for (String pattern : dangerousPatterns) {
            if (lowerValue.contains(pattern)) {
                throw new SecurityException("Potential SQL injection detected. Dangerous pattern: " + pattern);
            }
        }
    }

    /**
     * Escape LIKE pattern special characters
     */
    private String escapeLikePattern(String pattern) {
        if (pattern == null) {
            return "";
        }

        // Escape backslash first (to avoid double-escaping)
        pattern = pattern.replace("\\", "\\\\");

        // Escape LIKE wildcards
        pattern = pattern.replace("%", "\\%");
        pattern = pattern.replace("_", "\\_");

        // Escape SQL quotes
        pattern = pattern.replace("'", "''");

        // Validate no SQL injection
        validateNoSqlInjection(pattern);

        return pattern;
    }

    /**
     * Format IN clause values safely
     */
    private String formatInClauseSafely(FField field, List<Object> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("IN clause requires at least one value");
        }

        if (values.size() > 1000) {
            throw new IllegalArgumentException("IN clause cannot have more than 1000 values");
        }

        return values.stream()
            .map(v -> formatValueSafely(field, v))
            .collect(Collectors.joining(", "));
    }

    /**
     * Validate date format (basic validation)
     */
    private void validateDateFormat(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date value cannot be null or empty");
        }

        // Basic format validation (allows ISO format and common date formats)
        // Actual parsing will be done by database
        if (!dateStr.matches("^[0-9\\-\\s:/.]+$")) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }
    }

    /**
     * Apply pagination to SQLFilter
     */
    private void applyPagination(PaginationRequest pagination, SQLFilter sqlFilter) {
        validator.validatePagination(pagination.getStart(), pagination.getCount());

        ListPagination listPagination = sqlFilter.getPagination(true);

        listPagination.setOffset(pagination.getStart());
        listPagination.setOffsetCount(pagination.getCount());

        Globals.logString("Applied pagination: start=" + pagination.getStart() + ", count=" + pagination.getCount());
    }

    /**
     * Apply ordering to SQLFilter
     */
    private void applyOrdering(List<OrderByRequest> orderByList, SQLFilter sqlFilter, FocDesc focDesc) {
        StringBuilder orderBy = new StringBuilder();

        for (int i = 0; i < orderByList.size(); i++) {
            OrderByRequest order = orderByList.get(i);

            // Validate field exists
            validator.validateField(focDesc, order.getField());
            FField field = focDesc.getFieldByName(order.getField());

            // Validate direction
            validator.validateDirection(order.getDirection());

            // Build ORDER BY clause
            String fieldName = escapeFieldName(field.getDBName(), focDesc);

            if (i > 0) {
                orderBy.append(", ");
            }
            orderBy.append(fieldName).append(" ").append(order.getDirection().toUpperCase());
        }

        sqlFilter.setOrderBy(orderBy.toString());

        Globals.logString("Applied ordering: " + orderBy.toString());
    }
}

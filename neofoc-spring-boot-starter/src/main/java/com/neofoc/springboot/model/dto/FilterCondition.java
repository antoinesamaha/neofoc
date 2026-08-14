package com.neofoc.springboot.model.dto;

import lombok.Data;
import java.util.List;

/**
 * Represents a filter condition with an operator
 * Used for complex filtering beyond simple equality
 */
@Data
public class FilterCondition {
    /**
     * Operator for the filter
     * Allowed values: "=", ">=", "<=", ">", "<", "!=", "in", "between", "like", "contains", "isNull", "isNotNull"
     */
    private String operator;

    /**
     * Value for single-value operators (=, >=, <=, >, <, !=, like, contains)
     */
    private Object value;

    /**
     * Start value for "between" operator
     */
    private Object from;

    /**
     * End value for "between" operator
     */
    private Object to;

    /**
     * List of values for "in" operator
     */
    private List<Object> values;
}

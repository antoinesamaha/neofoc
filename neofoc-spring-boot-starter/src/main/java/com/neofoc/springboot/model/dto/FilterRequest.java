package com.neofoc.springboot.model.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Main request body for POST /foc/obj/{entityName}/search endpoint
 * Contains filters, pagination, and ordering parameters
 */
@Data
public class FilterRequest {
    /**
     * Map of field names to filter values
     * Value can be:
     * - Simple value (String, Number, Boolean) for equality filter
     * - FilterCondition object for complex filters with operators
     * - Special keys "AND" or "OR" for compound conditions
     */
    private Map<String, Object> filters;

    /**
     * Pagination parameters (optional)
     */
    private PaginationRequest pagination;

    /**
     * List of fields to order by (optional)
     */
    private List<OrderByRequest> orderBy;
}

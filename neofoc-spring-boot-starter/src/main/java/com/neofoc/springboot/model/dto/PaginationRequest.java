package com.neofoc.springboot.model.dto;

import lombok.Data;

/**
 * Pagination parameters for search requests
 */
@Data
public class PaginationRequest {
    /**
     * Starting offset (0-based index)
     */
    private int start;

    /**
     * Number of records to return
     */
    private int count;
}

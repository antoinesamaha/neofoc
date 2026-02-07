package com.neofoc.springboot.model.dto;

import lombok.Data;

/**
 * Ordering specification for search results
 */
@Data
public class OrderByRequest {
    /**
     * Field name to order by
     */
    private String field;

    /**
     * Sort direction: "ASC" or "DESC"
     */
    private String direction;
}

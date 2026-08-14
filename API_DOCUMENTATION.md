# NeoFOC REST API Documentation

## Overview

NeoFOC provides a RESTful API for accessing entities defined in your Spring Boot application. The framework automatically generates endpoints for all JPA entities, providing CRUD operations and advanced search capabilities.

**Base URL**: `/foc`

**Authentication**: Configure according to your Spring Security setup

---

## Table of Contents

1. [Standard CRUD Endpoints](#standard-crud-endpoints)
2. [Advanced Search Endpoint](#advanced-search-endpoint)
3. [Error Responses](#error-responses)
4. [Security](#security)

---

## Standard CRUD Endpoints

### Get Entity List or Single Entity

**Endpoint**: `GET /foc/obj/{entityName}`

**Description**: Retrieve all entities or a single entity by reference.

**Query Parameters**:
- `ref` (optional): Entity reference ID for single entity retrieval
- `start` (optional): Pagination offset (0-based)
- `count` (optional): Number of records to return

**Response** (List):
```json
{
  "data": [
    {
      "ref": 1,
      "field1": "value1",
      "field2": "value2"
    }
  ],
  "totalCount": 100
}
```

**Response** (Single Entity):
```json
{
  "ref": 1,
  "field1": "value1",
  "field2": "value2"
}
```

### Create Entity

**Endpoint**: `POST /foc/obj/{entityName}`

**Request Body**:
```json
{
  "field1": "value1",
  "field2": "value2"
}
```

**Response**: Created entity with `ref` assigned

### Update Entity

**Endpoint**: `PUT /foc/obj/{entityName}`

**Request Body**:
```json
{
  "ref": 1,
  "field1": "updated_value1",
  "field2": "updated_value2"
}
```

**Response**: Updated entity

### Delete Entity

**Endpoint**: `DELETE /foc/obj/{entityName}/{ref}`

**Response**: Deleted entity

---

## Advanced Search Endpoint

### Overview

The advanced search endpoint provides powerful filtering, pagination, and sorting capabilities for **non-cacheable entities**. Filter criteria are sent in the request body to protect sensitive data.

### Endpoint

**URL**: `POST /foc/obj/{entityName}/search`

**Method**: `POST`

**Content-Type**: `application/json`

**Restrictions**:
- ✅ Works with non-cacheable entities only
- ❌ Returns `400 Bad Request` for cacheable entities
- 🔒 Comprehensive SQL injection protection

---

### Request Format

```json
{
  "filters": {
    "fieldName": "simpleValue",
    "fieldName2": {
      "operator": "operatorName",
      "value": "operatorValue",
      "from": "startValue",
      "to": "endValue",
      "values": ["value1", "value2"]
    }
  },
  "pagination": {
    "start": 0,
    "count": 50
  },
  "orderBy": [
    {
      "field": "fieldName",
      "direction": "ASC|DESC"
    }
  ]
}
```

#### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `filters` | Object | No | Map of field names to filter values or filter conditions |
| `pagination` | Object | No | Pagination parameters |
| `orderBy` | Array | No | List of sorting specifications |

---

### Filter Syntax

#### Simple Equality Filter

For simple equality checks, provide the value directly:

```json
{
  "filters": {
    "status": "ACTIVE",
    "customer_ref": 123,
    "is_verified": true
  }
}
```

#### Complex Filter with Operator

For advanced filtering, use the operator syntax:

```json
{
  "filters": {
    "amount": {
      "operator": ">=",
      "value": 1000
    }
  }
}
```

---

### Supported Operators

| Operator | Description | Required Fields | Example |
|----------|-------------|-----------------|---------|
| `=` | Equality (explicit) | `value` | `{"operator": "=", "value": "ACTIVE"}` |
| `>=` | Greater than or equal | `value` | `{"operator": ">=", "value": 100}` |
| `<=` | Less than or equal | `value` | `{"operator": "<=", "value": 100}` |
| `>` | Greater than | `value` | `{"operator": ">", "value": 0}` |
| `<` | Less than | `value` | `{"operator": "<", "value": 100}` |
| `!=` | Not equal | `value` | `{"operator": "!=", "value": "CANCELLED"}` |
| `in` | IN clause (multiple values) | `values` | `{"operator": "in", "values": [1, 2, 3]}` |
| `between` | Range (inclusive) | `from`, `to` | `{"operator": "between", "from": "2024-01-01", "to": "2024-12-31"}` |
| `like` | SQL LIKE pattern | `value` | `{"operator": "like", "value": "John%"}` |
| `contains` | Text contains (adds % wildcards) | `value` | `{"operator": "contains", "value": "search"}` |
| `isNull` | IS NULL check | None | `{"operator": "isNull"}` |
| `isNotNull` | IS NOT NULL check | None | `{"operator": "isNotNull"}` |

---

### Pagination

```json
{
  "pagination": {
    "start": 0,      // 0-based offset
    "count": 50      // Number of records (max: 10,000)
  }
}
```

**Constraints**:
- `start` must be >= 0
- `count` must be > 0 and <= 10,000

---

### Sorting

```json
{
  "orderBy": [
    {"field": "created_date", "direction": "DESC"},
    {"field": "amount", "direction": "ASC"}
  ]
}
```

**Direction values**: `ASC` or `DESC`

Multiple sort fields are applied in order.

---

### Response Format

```json
{
  "data": [
    {
      "ref": 1,
      "field1": "value1",
      "field2": "value2"
    },
    {
      "ref": 2,
      "field1": "value3",
      "field2": "value4"
    }
  ],
  "totalCount": 42
}
```

| Field | Type | Description |
|-------|------|-------------|
| `data` | Array | Array of entity objects matching the filter |
| `totalCount` | Integer | Total number of matching records (may differ from `data.length` if paginated) |

---

## Request Examples

### Example 1: Simple Status Filter

**Request**:
```http
POST /foc/obj/orders/search
Content-Type: application/json

{
  "filters": {
    "status": "PENDING"
  }
}
```

**Response**:
```json
{
  "data": [
    {
      "ref": 123,
      "status": "PENDING",
      "amount": 1500.00,
      "created_date": "2024-06-15"
    }
  ],
  "totalCount": 1
}
```

### Example 2: Multiple Filters with Comparison Operators

**Request**:
```http
POST /foc/obj/orders/search
Content-Type: application/json

{
  "filters": {
    "customer_ref": 456,
    "amount": {
      "operator": ">=",
      "value": 1000
    },
    "status": {
      "operator": "in",
      "values": ["PENDING", "PROCESSING"]
    }
  },
  "pagination": {
    "start": 0,
    "count": 50
  },
  "orderBy": [
    {"field": "created_date", "direction": "DESC"}
  ]
}
```

**Response**:
```json
{
  "data": [
    {
      "ref": 789,
      "customer_ref": 456,
      "amount": 2500.00,
      "status": "PROCESSING",
      "created_date": "2024-06-20"
    },
    {
      "ref": 790,
      "customer_ref": 456,
      "amount": 1200.00,
      "status": "PENDING",
      "created_date": "2024-06-19"
    }
  ],
  "totalCount": 2
}
```

### Example 3: Date Range Filter

**Request**:
```http
POST /foc/obj/invoices/search
Content-Type: application/json

{
  "filters": {
    "invoice_date": {
      "operator": "between",
      "from": "2024-01-01",
      "to": "2024-03-31"
    },
    "total_amount": {
      "operator": ">",
      "value": 500
    }
  },
  "orderBy": [
    {"field": "invoice_date", "direction": "DESC"}
  ]
}
```

### Example 4: Text Search

**Request**:
```http
POST /foc/obj/products/search
Content-Type: application/json

{
  "filters": {
    "name": {
      "operator": "contains",
      "value": "laptop"
    },
    "price": {
      "operator": "<=",
      "value": 2000
    },
    "discontinued": {
      "operator": "isNull"
    }
  },
  "orderBy": [
    {"field": "price", "direction": "ASC"}
  ]
}
```

### Example 5: Null Checks

**Request**:
```http
POST /foc/obj/users/search
Content-Type: application/json

{
  "filters": {
    "deleted_at": {
      "operator": "isNull"
    },
    "email_verified_at": {
      "operator": "isNotNull"
    }
  }
}
```

### Example 6: IN Clause with Multiple Values

**Request**:
```http
POST /foc/obj/orders/search
Content-Type: application/json

{
  "filters": {
    "status": {
      "operator": "in",
      "values": ["PENDING", "PROCESSING", "SHIPPED"]
    },
    "priority": {
      "operator": "in",
      "values": [1, 2]
    }
  }
}
```

### Example 7: Complex Multi-Field Search with Pagination

**Request**:
```http
POST /foc/obj/transactions/search
Content-Type: application/json

{
  "filters": {
    "account_ref": 100,
    "amount": {
      "operator": "between",
      "from": 100,
      "to": 5000
    },
    "transaction_type": {
      "operator": "in",
      "values": ["DEBIT", "CREDIT"]
    },
    "transaction_date": {
      "operator": ">=",
      "value": "2024-01-01"
    },
    "description": {
      "operator": "contains",
      "value": "payment"
    }
  },
  "pagination": {
    "start": 0,
    "count": 100
  },
  "orderBy": [
    {"field": "transaction_date", "direction": "DESC"},
    {"field": "amount", "direction": "DESC"}
  ]
}
```

---

## Error Responses

All error responses follow this format:

```json
{
  "message": "Error description"
}
```

### HTTP Status Codes

| Status Code | Scenario | Example Response |
|-------------|----------|------------------|
| `200 OK` | Success | `{"data": [...], "totalCount": 10}` |
| `400 Bad Request` | Invalid field name | `{"message": "Validation error: Invalid field name: invalid_field does not exist in entity orders"}` |
| `400 Bad Request` | Invalid operator | `{"message": "Validation error: Invalid operator: <> Allowed operators: [=, >=, <=, >, <, !=, in, between, like, contains, isNull, isNotNull]"}` |
| `400 Bad Request` | Invalid value type | `{"message": "Validation error: Field amount expects numeric value, got: abc"}` |
| `400 Bad Request` | Cacheable entity | `{"message": "Search not supported for cacheable entities. Use GET /foc/obj/countries instead."}` |
| `400 Bad Request` | Pagination limit exceeded | `{"message": "Validation error: Pagination count cannot exceed 10000, got: 50000"}` |
| `403 Forbidden` | SQL injection detected | `{"message": "Security violation detected"}` |
| `403 Forbidden` | Permission denied | `{"message": "Read permission denied"}` |
| `403 Forbidden` | Search not allowed | `{"message": "Search not allowed for this entity"}` |
| `404 Not Found` | Entity not found | `{"message": "Entity not found"}` |
| `500 Internal Server Error` | Server error | `{"message": "Internal server error: ..."}` |

### Common Validation Errors

#### Invalid Field Name
```json
{
  "message": "Validation error: Invalid field name: xyz does not exist in entity orders"
}
```

**Cause**: Field name doesn't exist in the entity's FocDesc.

**Solution**: Use valid field names from the entity definition.

#### Invalid Operator
```json
{
  "message": "Validation error: Invalid operator: EQUALS. Allowed operators: [=, >=, <=, >, <, !=, in, between, like, contains, isNull, isNotNull]"
}
```

**Cause**: Used an operator that's not in the whitelist.

**Solution**: Use one of the allowed operators.

#### Type Mismatch
```json
{
  "message": "Validation error: Field amount expects numeric value, got: abc"
}
```

**Cause**: Value type doesn't match field type.

**Solution**: Ensure values match the field's data type.

#### SQL Injection Attempt
```json
{
  "message": "Security violation detected"
}
```

**Cause**: Filter value contains dangerous SQL patterns.

**Solution**: Remove SQL keywords, comments, or special characters from filter values.

#### Cacheable Entity
```json
{
  "message": "Search not supported for cacheable entities. Use GET /foc/obj/countries instead."
}
```

**Cause**: Attempted to search a cacheable (lookup) entity.

**Solution**: Use `GET /foc/obj/{entityName}` for cacheable entities.

---

## Security

### SQL Injection Protection

The search endpoint implements multiple layers of SQL injection protection:

1. **Field Name Whitelisting**: Only fields defined in the entity's `FocDesc` can be filtered
2. **Operator Whitelisting**: Only 12 predefined operators are allowed
3. **Type-Based Value Escaping**: Values are escaped based on field type:
   - Strings: Single quotes escaped (`'` → `''`)
   - Numbers: Strict parsing and validation
   - Dates: Format validation and escaping
   - Booleans: Converted to 1/0
   - Foreign keys: Numeric validation only
4. **SQL Keyword Detection**: Blocks values containing dangerous patterns:
   - SQL comments: `--`, `/*`, `*/`
   - Statement separators: `;`
   - SQL commands: `union`, `exec`, `drop`, `insert`, `update`, `delete`, `select`
   - Injection patterns: `or 1=1`, `' or '1'='1'`
5. **Special Character Escaping**: Handles backslashes, wildcards, and quotes
6. **No String Concatenation**: All SQL is built through safe formatters

### Field-Level Security

- Only fields marked as filterable in the entity definition can be used in filters
- The backend validates all field names against the entity schema
- Attempting to filter on non-existent fields returns `400 Bad Request`

### Authorization

The search endpoint respects the same authorization rules as GET endpoints:
- `allowSearch()` hook can be overridden for custom authorization
- Mobile module rights are checked (`mobileModule_HasRead()`)
- Entity-level permissions are enforced

### Rate Limiting

Consider implementing rate limiting on the search endpoint to prevent:
- Denial of service attacks
- Resource exhaustion from complex queries
- Automated scraping attempts

---

## Best Practices

### When to Use POST /search vs GET

| Use Case | Recommended Endpoint |
|----------|---------------------|
| Simple ID lookup | `GET /foc/obj/{entity}?ref=123` |
| List all cacheable entities | `GET /foc/obj/{entity}` |
| Complex multi-field filtering | `POST /foc/obj/{entity}/search` |
| Sensitive filter values | `POST /foc/obj/{entity}/search` |
| Range queries | `POST /foc/obj/{entity}/search` |
| Text search | `POST /foc/obj/{entity}/search` |
| Pagination with sorting | `POST /foc/obj/{entity}/search` |

### Performance Optimization

1. **Use Pagination**: Always paginate large result sets
2. **Create Indexes**: Add database indexes on frequently filtered fields
3. **Limit Filters**: Avoid excessive filters in a single request
4. **Use Specific Operators**: Prefer equality over LIKE when possible
5. **Avoid Leading Wildcards**: `name LIKE '%value'` is slower than `name LIKE 'value%'`

### Filter Construction

1. **Use Simple Values** for equality checks (more concise)
2. **Use Explicit Operators** for clarity in complex queries
3. **Combine Multiple Filters** in a single request (they are AND'ed together)
4. **Use `in` Operator** instead of multiple equality checks
5. **Use `between`** for ranges instead of `>=` and `<=` combinations

### Date Handling

- Use ISO 8601 format: `YYYY-MM-DD` for dates
- Use ISO 8601 format: `YYYY-MM-DD HH:mm:ss` for timestamps
- Backend validates date formats before querying

---

## Limitations

1. **Non-Cacheable Only**: Search endpoint does not work with cacheable entities
2. **AND Logic Only**: Multiple filters are combined with AND (no OR support yet)
3. **Pagination Limit**: Maximum 10,000 records per request
4. **IN Clause Limit**: Maximum 1,000 values in an IN clause
5. **No Compound Conditions**: AND/OR grouping not yet implemented
6. **No Aggregations**: COUNT, SUM, AVG not supported (use separate endpoint)
7. **No Joins**: Cannot filter on related entity fields (use foreign key references)

---

## Field Name Reference

### Standard Field Names

All entities have these standard fields:

- `ref`: Entity reference ID (primary key)
- `created_at`: Creation timestamp (if workflow enabled)
- `updated_at`: Last update timestamp (if workflow enabled)
- `created_by`: User who created (if workflow enabled)
- `updated_by`: User who last updated (if workflow enabled)

### Custom Field Names

Use the **database column names** as defined in your JPA entities:

```java
@Entity
@Table(name = "orders")
public class Order {
    @Column(name = "customer_ref")  // Use "customer_ref" in filters
    private Long customerRef;

    @Column(name = "order_date")    // Use "order_date" in filters
    private LocalDate orderDate;
}
```

**Note**: Use snake_case database column names, not camelCase Java property names.

---

## API Versioning

Current API version: **2.0**

The search endpoint was introduced in version 2.0.

For version information, use: `GET /foc/meta/version`

---

## Support

For issues or questions:
- Check server logs for detailed error information
- Verify entity is non-cacheable before using search
- Ensure field names match database column names
- Validate filter values match field types

All search operations are logged on the backend for debugging purposes.

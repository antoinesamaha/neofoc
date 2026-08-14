# NeoFOC Framework - Claude Code Context Guide

## What is NeoFOC?

NeoFOC is a Spring Boot ORM framework that:
- Automatically scans JPA entities and converts them to **FocDesc** (FOC Descriptors)
- Provides a **single REST controller** (`FocController`) for all entities
- Creates in-memory model metadata that allows dynamic manipulation of entities
- Generates helper classes under `/target` folder for each entity
- Supports creating custom `Foc[EntityName].java` classes for entity-specific logic

## Core Concepts

### 1. FocDesc (FOC Descriptor)
- **Location**: `neofoc/src/main/java/com/foc/desc/FocDesc.java`
- **Purpose**: In-memory metadata representation of an entity
- **Contains**: Field definitions, relationships, validation rules, storage name
- **Created by**: `ScanSpringBootEntitiesAndConvert2FocDesc` service at startup

### 2. FocObject
- **Location**: `neofoc/src/main/java/com/foc/desc/FocObject.java`
- **Purpose**: Base class for entity objects in the FOC ORM
- **Custom Classes**: You can create `Foc[EntityName].java` extending `FocObject` for entity-specific behavior
- **Example**: If you have a `User` entity, you can create `FocUser.java`

### 3. FocList
- **Location**: `neofoc/src/main/java/com/foc/list/FocList.java`
- **Purpose**: Container for collections of FocObjects
- **Features**: Loading, filtering, sorting, validation
- **Generated**: Loaders are generated under `/target` folder

### 4. FocController (Single Controller for All Entities)
- **Location**: `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/controller/FocController.java`
- **Purpose**: Universal REST API controller for ALL entities
- **Endpoints**:
  - `GET /foc/obj/{entityName}` - List all entities or get by ID
  - `POST /foc/obj/{entityName}` - Create new entity
  - `PUT /foc/obj/{entityName}` - Update entity
  - `DELETE /foc/obj/{entityName}/{id}` - Delete entity

### 5. Entity Scanning
- **Location**: `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/service/ScanSpringBootEntitiesAndConvert2FocDesc.java`
- **When**: Runs at application startup (via `RunAfterStartup`)
- **Process**:
  1. Scans all JPA `@Entity` classes using EntityManager
  2. Reads field metadata (type, nullable, length from `@Column`)
  3. Creates FocDesc with appropriate FField types
  4. Looks for custom `Foc[EntityName].java` classes
  5. Registers FocDesc in `FocDescMap`

### 6. Cacheable vs Non-Cacheable Lists (Critical Concept)

**Overview**: FOC distinguishes between cacheable and non-cacheable entities, which fundamentally changes how they are stored and accessed.

**Cacheable Lists** (Lookup Tables):
- **Annotation**: Mark entity with JPA `@Cacheable` annotation
- **Storage**: Single instance stored in `DataStore` singleton, shared across all requests
- **Lifecycle**: Created once at first access, reused for all subsequent requests
- **Memory**: Entire list kept in memory permanently
- **Access Pattern**: `getFocList()` returns the same list instance every time
- **Performance**: Fast - no database queries after initial load
- **Use Cases**:
  - Lookup tables: Countries, Currencies, Units of Measure
  - Reference data: Status codes, Categories, Types
  - Small, rarely changing datasets (typically < 1000 records)
  - Data that needs to be available quickly everywhere

**Non-Cacheable Lists** (Transactional Data):
- **Annotation**: No `@Cacheable` annotation (or explicitly set to false)
- **Storage**: New list instance created for each request
- **Lifecycle**: Created, used, then disposed after each request
- **Memory**: Not kept in memory between requests
- **Access Pattern**: `getFocList()` returns null - must create via `newFocList()`
- **Performance**: Slower - queries database on each access
- **Use Cases**:
  - Transactional tables: Orders, Invoices, Payments, Logs
  - Large, frequently changing datasets
  - Data that grows continuously
  - User-specific or session-specific data

**How It Works in FocDesc**:
```java
// Location: neofoc/src/main/java/com/foc/desc/FocDesc.java:1537

public FocList getFocList(String context) {
    FocList focList = null;
    if (isListInCache()) {
        // CACHEABLE: Get singleton from DataStore
        focList = DataStore.getInstance().getList(getStorageName(), ...);
        if (focList == null) {
            // First time - create and store
            focList = newFocList();
            DataStore.getInstance().putList(getStorageName(), focList, ...);
        }
    }
    // NON-CACHEABLE: returns null - caller must create new list
    return focList;
}
```

**How It Works in FocController**:
```java
// Location: neofoc-spring-boot-starter/.../controller/FocController.java

// Override this method to control caching per entity
public boolean useCachedList(FocRestAPICall focRequest) {
    return true; // Default: use cached if available
}

// In doGet() method:
if (useCachedList(null)) {
    // CACHEABLE: Search in singleton list
    focObject = list.searchByReference(filterRef);
} else {
    // NON-CACHEABLE: Get first object from new list
    if (list.size() == 1) {
        focObject = list.getFocObject(0);
    }
}

// Cleanup:
if (!useCachedList(null)) {
    // NON-CACHEABLE: Dispose list after use
    disposeFocList(focRequest, list);
    list = null;
}
// CACHEABLE lists are never disposed - kept in DataStore
```

**Configuration During Entity Scan**:
```java
// Location: ScanSpringBootEntitiesAndConvert2FocDesc.java:48,71

String tableName = getTableName(type);
boolean cacheable = isEntityCacheable(type); // Checks @Cacheable annotation

FocDesc focDesc = new FocDesc(...);
focDesc.setListInCache(cacheable); // Sets the caching behavior
```

**Memory and Performance Trade-offs**:

| Aspect | Cacheable | Non-Cacheable |
|--------|-----------|---------------|
| Memory Usage | High (all records in RAM) | Low (query on demand) |
| First Access | Slow (loads all records) | Fast (queries specific records) |
| Subsequent Access | Very Fast (in-memory) | Slow (database query) |
| Scalability | Limited by memory | Limited by database |
| Data Freshness | Stale until refresh | Always current |
| Best For | ≤ 1000 records, read-heavy | > 1000 records, write-heavy |

**Important Notes**:
- **DO NOT** mark large transactional tables as `@Cacheable` - will cause memory issues
- **DO** mark small lookup tables as `@Cacheable` - significant performance boost
- Cached lists can be refreshed programmatically via `refreshCachedListFocObject(ref)`
- Cached lists support `setCheckIfRecentEnough()` for auto-refresh logic
- In production, monitor memory usage when marking entities as cacheable

## Project Structure

```
neofoc/                                    # Root project (multi-module Maven)
├── neofoc/                                # Core FOC framework
│   └── src/main/java/com/foc/
│       ├── desc/                          # Model descriptors
│       │   ├── FocDesc.java              # Entity descriptor
│       │   ├── FocObject.java            # Base entity object
│       │   ├── FocModule.java            # Module organization
│       │   └── field/                    # Field types
│       ├── list/                         # Collection handling
│       │   └── FocList.java              # Entity collections
│       └── Globals.java                  # Global application context
│
├── neofoc-spring-boot-starter/            # Spring Boot integration
│   └── src/main/java/com/neofoc/springboot/
│       ├── config/
│       │   ├── RunAfterStartup.java      # Startup initialization
│       │   └── FocModuleScanner.java     # Module scanner
│       ├── service/
│       │   └── ScanSpringBootEntitiesAndConvert2FocDesc.java  # Entity scanner
│       └── controller/
│           ├── FocController.java        # Universal REST controller
│           ├── MetaController.java       # Metadata endpoints
│           └── AuthController.java       # Authentication
│
├── neofoc-datasource/                     # Data source abstraction
├── neofoc-officelink/                     # Office document integration
├── NeoFocApp/                            # Example application
│   └── src/main/java/com/neofoc/app/
│       ├── entity/                       # Your JPA entities go here
│       ├── model/                        # Foc[EntityName] classes go here
│       └── controller/                   # Custom controllers (if needed)
└── foc-ui/                               # Flutter UI client
```

## Where to Make Changes

### Adding a New Entity

1. **Create JPA Entity**:
   - **Location**: `NeoFocApp/src/main/java/com/neofoc/app/entity/[YourEntity].java`
   - Use standard JPA annotations (`@Entity`, `@Table`, `@Column`, `@ManyToOne`, etc.)
   - The framework will auto-scan this at startup

2. **Create Custom Foc Class (Optional)**:
   - **Location**: `NeoFocApp/src/main/java/com/neofoc/app/model/Foc[YourEntity].java`
   - Must extend `FocObject` or `FocObjectGeneral`
   - Must be named `Foc` + entity name (e.g., `FocUser` for `User` entity)
   - Add custom business logic, computed fields, validation

3. **Generated Files**:
   - **Location**: `NeoFocApp/target/generated-sources/`
   - Contains getters, setters, and FocList loaders
   - **DO NOT EDIT** - regenerated on build

### Modifying Entity Scanning Logic

- **File**: `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/service/ScanSpringBootEntitiesAndConvert2FocDesc.java`
- **When to modify**:
  - Add support for new JPA annotations
  - Change field type mapping (e.g., add support for `BigDecimal`)
  - Customize FocDesc creation logic
  - Handle special relationship types

### Modifying REST API Behavior

- **File**: `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/controller/FocController.java`
- **When to modify**:
  - Add custom endpoints
  - Change JSON serialization
  - Add authorization logic
  - Modify CRUD behavior (methods: `doGet`, `doPost`, `doPut`, `doDelete`)
  - Add custom filters or validation

### Adding Field Types

- **Location**: `neofoc/src/main/java/com/foc/desc/field/`
- **Existing types**: `FStringField`, `FIntField`, `FDateField`, `FObjectField`, etc.
- **When to add**: If you need a new field type not covered by existing ones
- **Steps**:
  1. Create new `F[Type]Field` class extending `FField`
  2. Update `ScanSpringBootEntitiesAndConvert2FocDesc` to recognize it
  3. Update JSON serialization in `B01JsonBuilder`

### Creating FocModules

- **Location**: Create in your app package (e.g., `NeoFocApp/src/main/java/com/neofoc/app/module/`)
- **Purpose**: Organize entities into logical modules
- **Steps**:
  1. Extend `FocModule`
  2. Add `@FocDeclareModule` annotation
  3. Implement `declareFocObjectsOnce()`
  4. Module is auto-scanned by `FocModuleScanner`

### Database Schema Changes

- **Automatic**: FOC can auto-adapt database schema
- **Triggered by**: `Globals.getApp().adaptDataModel(false, false)` in `RunAfterStartup`
- **Configuration**: Set in `ConfigInfo` or application properties
- **Manual**: Use standard Spring Boot/Hibernate schema management

## Advanced Filtering with POST /search Endpoint

### Overview

NeoFOC provides a powerful filtering endpoint for **non-cacheable entities** that allows complex queries with filters, pagination, and sorting. Filter data is sent in the request body to protect sensitive information from appearing in URLs or server logs.

### Endpoint

```http
POST /foc/obj/{entityName}/search
Content-Type: application/json
```

### Important Restrictions

- ✅ **Works with**: Non-cacheable entities only
- ❌ **Does NOT work with**: Cacheable entities (use GET instead)
- 🔒 **Security**: Comprehensive SQL injection protection built-in

### Request Body Structure

```json
{
  "filters": {
    // Simple equality filters
    "status": "ACTIVE",
    "customer_ref": 123,

    // Comparison operators
    "amount": {"operator": ">=", "value": 1000},
    "quantity": {"operator": "<", "value": 50},

    // Range filtering
    "created_date": {
      "operator": "between",
      "from": "2024-01-01",
      "to": "2024-12-31"
    },

    // IN clause
    "category": {
      "operator": "in",
      "values": ["Electronics", "Books", "Clothing"]
    },

    // Text search
    "description": {
      "operator": "contains",
      "value": "urgent"
    },

    // Null checks
    "deleted_at": {"operator": "isNull"}
  },

  "pagination": {
    "start": 0,
    "count": 50
  },

  "orderBy": [
    {"field": "created_date", "direction": "DESC"},
    {"field": "amount", "direction": "ASC"}
  ]
}
```

### Supported Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `=` | Equality (default) | `"status": "ACTIVE"` |
| `>=` | Greater than or equal | `{"operator": ">=", "value": 100}` |
| `<=` | Less than or equal | `{"operator": "<=", "value": 100}` |
| `>` | Greater than | `{"operator": ">", "value": 0}` |
| `<` | Less than | `{"operator": "<", "value": 100}` |
| `!=` | Not equal | `{"operator": "!=", "value": "CANCELLED"}` |
| `in` | IN clause | `{"operator": "in", "values": [1,2,3]}` |
| `between` | Range | `{"operator": "between", "from": "A", "to": "Z"}` |
| `like` | SQL LIKE | `{"operator": "like", "value": "John%"}` |
| `contains` | Contains text | `{"operator": "contains", "value": "search"}` |
| `isNull` | IS NULL | `{"operator": "isNull"}` |
| `isNotNull` | IS NOT NULL | `{"operator": "isNotNull"}` |

### Response Format

```json
{
  "data": [
    {
      "ref": 1,
      "status": "ACTIVE",
      "amount": 1500.00,
      // ... other fields
    }
  ],
  "totalCount": 42
}
```

### Security Features

The `/search` endpoint includes comprehensive security measures:

1. **Field Whitelisting**: Only fields that exist in `FocDesc` can be filtered
2. **Operator Whitelisting**: Only allowed operators can be used
3. **SQL Injection Protection**: Multiple layers of protection:
   - Type-based value escaping
   - SQL keyword detection
   - Special character escaping
   - No direct string concatenation
4. **Value Validation**: Values are validated against field types
5. **Pagination Limits**: Maximum 10,000 records per request

### Implementation Files

| Component | Location | Purpose |
|-----------|----------|---------|
| **DTOs** | `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/model/dto/` | Request/response structures |
| **FilterValidator** | `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/service/FilterValidator.java` | Validates fields, operators, values |
| **FilterParser** | `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/service/FilterParser.java` | Parses filters, applies to SQL |
| **FocController** | `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/controller/FocController.java` | `doSearch()` endpoint implementation |

### Usage Examples

**Example 1: Simple Filter with Pagination**
```bash
curl -X POST http://localhost:8080/foc/obj/orders/search \
  -H "Content-Type: application/json" \
  -d '{
    "filters": {
      "status": "PENDING"
    },
    "pagination": {
      "start": 0,
      "count": 50
    }
  }'
```

**Example 2: Complex Multi-Field Filter**
```bash
curl -X POST http://localhost:8080/foc/obj/orders/search \
  -H "Content-Type: application/json" \
  -d '{
    "filters": {
      "customer_ref": 123,
      "amount": {"operator": ">=", "value": 1000},
      "status": {"operator": "in", "values": ["PENDING", "PROCESSING"]},
      "created_date": {
        "operator": "between",
        "from": "2024-01-01",
        "to": "2024-12-31"
      }
    },
    "pagination": {"start": 0, "count": 100},
    "orderBy": [
      {"field": "created_date", "direction": "DESC"}
    ]
  }'
```

**Example 3: Text Search**
```bash
curl -X POST http://localhost:8080/foc/obj/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "filters": {
      "name": {"operator": "contains", "value": "laptop"},
      "price": {"operator": "<=", "value": 2000}
    },
    "orderBy": [{"field": "price", "direction": "ASC"}]
  }'
```

### Customizing Filter Behavior

**Override `allowSearch()` for Authorization**:
```java
// In your custom controller extending FocController
@Override
protected boolean allowSearch(FocRestAPICall focRequest) {
    String entityName = focRequest.getFocDesc().getName();

    // Example: Only allow search on specific entities
    if (entityName.equals("sensitive_data")) {
        return getCurrentUser().hasRole("ADMIN");
    }

    return true;
}
```

### Error Handling

| Status Code | Scenario | Response |
|-------------|----------|----------|
| `200 OK` | Success | Data array with totalCount |
| `400 Bad Request` | Validation error | `{"message": "Validation error: ..."}` |
| `400 Bad Request` | Cacheable entity | `{"message": "Search not supported for cacheable entities..."}` |
| `403 Forbidden` | SQL injection detected | `{"message": "Security violation detected"}` |
| `403 Forbidden` | No permission | `{"message": "Read permission denied"}` |
| `404 Not Found` | Entity doesn't exist | `{"message": "Entity not found"}` |
| `500 Internal Error` | Server error | `{"message": "Internal server error..."}` |

### Comparison: GET vs POST /search

| Feature | GET /foc/obj/{entity} | POST /foc/obj/{entity}/search |
|---------|----------------------|------------------------------|
| **Cacheable Lists** | ✅ Supported | ❌ Not allowed |
| **Non-Cacheable Lists** | ✅ Supported | ✅ Supported |
| **Simple Filters** | Via URL params | Via JSON body |
| **Complex Filters** | ❌ Not supported | ✅ Full support |
| **Sensitive Data** | ⚠️ Exposed in URL | ✅ Hidden in body |
| **Operators** | Equality only | All operators |
| **Pagination** | `?start=0&count=50` | JSON body |
| **Sorting** | Limited | Full control |

### Best Practices

1. **Use POST /search for**:
   - Complex filtering with multiple conditions
   - Sensitive filter values (SSN, account numbers, etc.)
   - Large result sets requiring pagination
   - Non-cacheable transactional data

2. **Use GET for**:
   - Simple lookups by ID or simple equality
   - Cacheable lookup tables
   - Public, non-sensitive data
   - RESTful resource retrieval

3. **Security**:
   - Never disable SQL injection protection
   - Always validate filter values in your application
   - Use `allowSearch()` to implement entity-level authorization
   - Monitor logs for suspicious filter patterns

4. **Performance**:
   - Use pagination for large datasets
   - Create database indexes on frequently filtered fields
   - Limit the number of filters per request
   - Consider caching frequently used filter combinations

## Common Development Patterns

### Pattern 1: Adding a New Entity with Custom Logic

```java
// 1. Create JPA Entity
// Location: NeoFocApp/src/main/java/com/neofoc/app/entity/Product.java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Double price;
}

// 2. Create Custom FocObject (optional)
// Location: NeoFocApp/src/main/java/com/neofoc/app/model/FocProduct.java
public class FocProduct extends FocObject {
    public Double getPriceWithTax() {
        Double price = getPropertyDouble("price");
        return price * 1.2; // 20% tax
    }

    @Override
    public void beforeValidate() {
        // Custom validation logic
        if (getPropertyDouble("price") < 0) {
            throw new RuntimeException("Price cannot be negative");
        }
    }
}
```

### Pattern 2: Customizing Controller Behavior

```java
// Location: neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/controller/FocController.java

// Override these methods for custom behavior:

@Override
protected boolean allowPost(FocRestAPICall focRequest) {
    // Add custom authorization logic
    return super.allowPost(focRequest);
}

@Override
protected String doPost_CheckError(FocRestAPICall focRequest, JSONObject jsonObj) {
    // Add custom validation before save
    return null; // or return error JSON
}
```

### Pattern 3: Adding Support for New JPA Annotation

```java
// Location: neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/service/ScanSpringBootEntitiesAndConvert2FocDesc.java

// In scanEntitiesAndCreateFocDesc() method, add:
} else if (attributeClass == BigDecimal.class) {
    FNumField fld = new FNumField(fieldName, title, fieldID++, !isNullable, 20, 5);
    focDesc.addField(fld);
}
```

## Key Files Reference

### Startup & Initialization
- `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/config/RunAfterStartup.java` - Application startup hook
- `neofoc-spring-boot-starter/src/main/java/com/neofoc/springboot/config/FocModuleScanner.java` - Module registration

### Core Framework
- `neofoc/src/main/java/com/foc/Globals.java` - Global application state
- `neofoc/src/main/java/com/foc/desc/FocDescMap.java` - Registry of all FocDesc
- `neofoc/src/main/java/com/foc/desc/FocConstructor.java` - Object factory

### Data Access
- `neofoc/src/main/java/com/foc/list/FocList.java` - List operations
- `neofoc/src/main/java/com/foc/db/DBManager.java` - Database operations
- `neofoc/src/main/java/com/foc/dataSource/` - Data source abstraction

### JSON Serialization
- `neofoc/src/main/java/com/foc/shared/json/B01JsonBuilder.java` - JSON conversion

## Important Notes

1. **DO NOT** edit files in `/target` folder - they are auto-generated
2. **ALWAYS** run the application after adding new entities to trigger scanning
3. **Custom Foc classes** must follow naming convention: `Foc` + entity name
4. **FocController** handles ALL entities - you rarely need custom controllers
5. **FocDesc** is created at startup - changes require application restart
6. **Database schema** can be auto-adapted by FOC framework
7. **@Column annotations** (nullable, length) are respected by the scanner
8. **Relationships** (`@ManyToOne`, `@OneToMany`) are automatically detected

## Debugging Tips

1. **Entity not found**: Check `Globals.getApp().getFocDescMap()` - entity might not be scanned
2. **Field missing**: Verify JPA annotation and check scanner logic
3. **Custom Foc class not used**: Ensure naming matches `Foc[EntityName]` exactly
4. **REST API issues**: Check `FocController` methods and override points
5. **Startup errors**: Review `RunAfterStartup` and `ScanSpringBootEntitiesAndConvert2FocDesc` logs

## Quick Reference: Common Modifications

| Task | File to Modify | Line/Method |
|------|----------------|-------------|
| Add new entity | `NeoFocApp/src/main/java/.../entity/` | Create new file |
| Add custom logic | `NeoFocApp/src/main/java/.../model/Foc[Entity].java` | Create new file |
| Change field mapping | `ScanSpringBootEntitiesAndConvert2FocDesc.java` | `scanEntitiesAndCreateFocDesc()` |
| Modify REST API | `FocController.java` | `doGet/doPost/doPut/doDelete` |
| Add authorization | `FocController.java` | `allowGet/allowPost/mobileModule_Has*` |
| Change JSON format | `B01JsonBuilder.java` | Various methods |
| Add startup logic | `RunAfterStartup.java` | `runAfterStartup()` |

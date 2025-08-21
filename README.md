# Pet Store Spring Boot Application - Week 15

This Spring Boot application implements a pet store management system with full CRUD operations for pet stores, employees, and customers.

## Features Implemented

### 1. Pet Store Management
- Create a new pet store
- Retrieve all pet stores (summary data)
- Retrieve a pet store by ID (with full employee and customer data)
- Delete a pet store (cascades to employees and customer relationships)

### 2. Employee Management
- Add employees to existing pet stores
- One-to-many relationship with pet stores
- Employees are automatically deleted when their pet store is deleted

### 3. Customer Management
- Add customers to existing pet stores
- Many-to-many relationship with pet stores
- Customer records persist when pet stores are deleted (only relationships are removed)

## API Endpoints

### Pet Store Operations
- `POST /pet_store` - Create a new pet store
- `GET /pet_store` - Get all pet stores (summary data)
- `GET /pet_store/{id}` - Get pet store by ID (with full data)
- `DELETE /pet_store/{id}` - Delete pet store by ID

### Employee Operations
- `POST /pet_store/{petStoreId}/employee` - Add employee to pet store

### Customer Operations
- `POST /pet_store/{petStoreId}/customer` - Add customer to pet store

## Testing the Application

### Prerequisites
1. Ensure your MySQL database is running
2. Update `application.yaml` with your database credentials
3. Start the Spring Boot application

### Test Steps

#### 1. Create a Pet Store
```bash
curl -X POST http://localhost:8080/pet_store \
  -H "Content-Type: application/json" \
  -d '{
    "storeName": "Happy Pets Store"
  }'
```

#### 2. Add an Employee to the Pet Store
```bash
curl -X POST http://localhost:8080/pet_store/1/employee \
  -H "Content-Type: application/json" \
  -d '{
    "employeeName": "John Smith"
  }'
```

#### 3. Add a Customer to the Pet Store
```bash
curl -X POST http://localhost:8080/pet_store/1/customer \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jane Doe",
    "customerEmail": "jane.doe@email.com"
  }'
```

#### 4. Get All Pet Stores (Summary)
```bash
curl -X GET http://localhost:8080/pet_store
```

#### 5. Get Pet Store by ID (Full Data)
```bash
curl -X GET http://localhost:8080/pet_store/1
```

#### 6. Delete Pet Store
```bash
curl -X DELETE http://localhost:8080/pet_store/1
```

## Sample JSON Data

See `src/main/resources/test-data.json` for sample JSON payloads.

## Database Schema

### PetStore Entity
- `id` (Primary Key)
- `storeName`
- `employees` (One-to-Many with Employee)
- `customers` (Many-to-Many with Customer)

### Employee Entity
- `id` (Primary Key)
- `employeeName`
- `petStore` (Many-to-One with PetStore)

### Customer Entity
- `id` (Primary Key)
- `customerName`
- `customerEmail`
- `petStores` (Many-to-Many with PetStore)

## Key Implementation Details

### Relationships
- **PetStore ↔ Employee**: One-to-Many with cascade ALL (employees deleted when store deleted)
- **PetStore ↔ Customer**: Many-to-Many with cascade PERSIST (customers persist when store deleted)

### Transaction Management
- All service methods are properly annotated with `@Transactional`
- Read operations use `readOnly = true`
- Write operations use `readOnly = false`

### Error Handling
- Proper exception handling for not found scenarios
- Validation of relationships (e.g., employee belongs to correct pet store)
- Meaningful error messages

### Logging
- All controller methods include logging for request tracking
- Uses SLF4J with Lombok `@Slf4j` annotation

## Testing with ARC or Postman

1. **Create Pet Store**: POST to `http://localhost:8080/pet_store`
2. **Add Employee**: POST to `http://localhost:8080/pet_store/{id}/employee`
3. **Add Customer**: POST to `http://localhost:8080/pet_store/{id}/customer`
4. **Get All Stores**: GET `http://localhost:8080/pet_store`
5. **Get Store by ID**: GET `http://localhost:8080/pet_store/{id}`
6. **Delete Store**: DELETE `http://localhost:8080/pet_store/{id}`

## Expected Behavior

- When you delete a pet store, all its employees are automatically deleted
- Customer records remain in the database, but their relationship with the deleted pet store is removed
- Summary view (GET all) shows pet stores without employee and customer details
- Detail view (GET by ID) shows pet stores with full employee and customer information

# User Registration Flow Sequence Diagram

## Overview
This sequence diagram illustrates the complete user registration flow from frontend to backend, including validation, database operations, and response handling.

## Sequence Flow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Frontend  │    │   Backend   │    │ Validation  │    │   Database  │    │   Redis     │
│   (React)   │    │  (Express)  │    │  (Joi/Zod)  │    │ (PostgreSQL)│    │   Cache     │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │                   │                   │
       │ 1. User fills     │                   │                   │                   │
       │ registration form │                   │                   │                   │
       │                   │                   │                   │                   │
       │ 2. Submit form    │                   │                   │                   │
       │──────────────────▶│                   │                   │                   │
       │                   │                   │                   │                   │
       │                   │ 3. Validate input │                   │                   │
       │                   │──────────────────▶│                   │                   │
       │                   │                   │                   │                   │
       │                   │ 4. Validation     │                   │                   │
       │                   │ result            │                   │                   │
       │                   │◀──────────────────│                   │                   │
       │                   │                   │                   │                   │
       │                   │ 5. Check if user  │                   │                   │
       │                   │ already exists    │                   │                   │
       │                   │──────────────────────────────────────▶│                   │
       │                   │                   │                   │                   │
       │                   │ 6. User existence │                   │                   │
       │                   │ check result      │                   │                   │
       │                   │◀──────────────────────────────────────│                   │
       │                   │                   │                   │                   │
       │                   │ 7. Hash password  │                   │                   │
       │                   │ (bcrypt)          │                   │                   │
       │                   │                   │                   │                   │
       │                   │ 8. Create user    │                   │                   │
       │                   │ record            │                   │                   │
       │                   │──────────────────────────────────────▶│                   │
       │                   │                   │                   │                   │
       │                   │ 9. User created   │                   │                   │
       │                   │ successfully      │                   │                   │
       │                   │◀──────────────────────────────────────│                   │
       │                   │                   │                   │                   │
       │                   │ 10. Generate JWT  │                   │                   │
       │                   │ tokens            │                   │                   │
       │                   │                   │                   │                   │
       │                   │ 11. Store refresh │                   │                   │
       │                   │ token in cache    │                   │                   │
       │                   │──────────────────────────────────────────────────────────▶│
       │                   │                   │                   │                   │
       │                   │ 12. Cache stored  │                   │                   │
       │                   │ successfully      │                   │                   │
       │                   │◀──────────────────────────────────────────────────────────│                   │
       │                   │                   │                   │                   │
       │                   │ 13. Log audit     │                   │                   │
       │                   │ event             │                   │                   │
       │                   │──────────────────────────────────────▶│                   │
       │                   │                   │                   │                   │
       │                   │ 14. Audit logged  │                   │                   │
       │                   │ successfully      │                   │                   │
       │                   │◀──────────────────────────────────────│                   │
       │                   │                   │                   │                   │
       │ 15. Registration  │                   │                   │                   │
       │ success response  │                   │                   │                   │
       │◀──────────────────│                   │                   │                   │
       │                   │                   │                   │                   │
       │ 16. Store tokens  │                   │                   │                   │
       │ in localStorage   │                   │                   │                   │
       │                   │                   │                   │                   │
       │ 17. Redirect to   │                   │                   │                   │
       │ dashboard         │                   │                   │                   │
```

## Error Scenarios

### Validation Error
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Frontend  │    │   Backend   │    │ Validation  │
│   (React)   │    │  (Express)  │    │  (Joi/Zod)  │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │ 1. Submit form    │                   │
       │ with invalid data │                   │
       │──────────────────▶│                   │
       │                   │                   │
       │                   │ 2. Validate input │
       │                   │──────────────────▶│
       │                   │                   │
       │                   │ 3. Validation     │
       │                   │ errors            │
       │                   │◀──────────────────│
       │                   │                   │
       │ 4. Error response │                   │
       │ with details      │                   │
       │◀──────────────────│                   │
       │                   │                   │
       │ 5. Display error  │                   │
       │ messages to user  │                   │
```

### User Already Exists
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Frontend  │    │   Backend   │    │   Database  │
│   (React)   │    │  (Express)  │    │ (PostgreSQL)│
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │ 1. Submit form    │                   │
       │ with existing     │                   │
       │ email             │                   │
       │──────────────────▶│                   │
       │                   │                   │
       │                   │ 2. Check if user  │
       │                   │ already exists    │
       │                   │──────────────────▶│
       │                   │                   │
       │                   │ 3. User found     │
       │                   │ (already exists)  │
       │                   │◀──────────────────│
       │                   │                   │
       │ 4. Error response │                   │
       │ (email exists)    │                   │
       │◀──────────────────│                   │
       │                   │                   │
       │ 5. Display error  │                   │
       │ message           │                   │
```

## Implementation Notes

### Frontend Responsibilities
- Form validation (client-side)
- API call handling
- Error message display
- Token storage
- Navigation after success

### Backend Responsibilities
- Input validation (server-side)
- Password hashing
- Database operations
- JWT token generation
- Audit logging
- Error handling

### Security Considerations
- Password hashing with bcrypt
- JWT token expiration
- Input sanitization
- Rate limiting
- Audit trail maintenance

### Performance Optimizations
- Database connection pooling
- Redis caching for sessions
- Async/await for non-blocking operations
- Proper error handling to prevent crashes 
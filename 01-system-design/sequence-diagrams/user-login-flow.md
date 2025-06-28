# User Login Flow Sequence Diagram

## Overview
This sequence diagram illustrates the complete user login flow, including authentication, token generation, and session management.

## Sequence Flow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Frontend  │    │   Backend   │    │ Validation  │    │   Database  │    │   Redis     │
│   (React)   │    │  (Express)  │    │  (Joi/Zod)  │    │ (PostgreSQL)│    │   Cache     │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │                   │                   │
       │ 1. User enters    │                   │                   │                   │
       │ credentials       │                   │                   │                   │
       │                   │                   │                   │                   │
       │ 2. Submit login   │                   │                   │                   │
       │ form              │                   │                   │                   │
       │──────────────────▶│                   │                   │                   │
       │                   │                   │                   │                   │
       │                   │ 3. Validate input │                   │                   │
       │                   │ (email/password)  │                   │                   │
       │                   │──────────────────▶│                   │                   │
       │                   │                   │                   │                   │
       │                   │ 4. Validation     │                   │                   │
       │                   │ result            │                   │                   │
       │                   │◀──────────────────│                   │                   │
       │                   │                   │                   │                   │
       │                   │ 5. Find user by   │                   │                   │
       │                   │ email             │                   │                   │
       │                   │──────────────────────────────────────▶│                   │
       │                   │                   │                   │                   │
       │                   │ 6. User data      │                   │                   │
       │                   │ (with password    │                   │                   │
       │                   │ hash)             │                   │                   │
       │                   │◀──────────────────────────────────────│                   │
       │                   │                   │                   │                   │
       │                   │ 7. Compare        │                   │                   │
       │                   │ password with     │                   │                   │
       │                   │ hash (bcrypt)     │                   │                   │
       │                   │                   │                   │                   │
       │                   │ 8. Password       │                   │                   │
       │                   │ verification      │                   │                   │
       │                   │ result            │                   │                   │
       │                   │                   │                   │                   │
       │                   │ 9. Check if user  │                   │                   │
       │                   │ is active         │                   │                   │
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
       │                   │ 13. Update last   │                   │                   │
       │                   │ login timestamp   │                   │                   │
       │                   │──────────────────────────────────────▶│                   │
       │                   │                   │                   │                   │
       │                   │ 14. Last login    │                   │                   │
       │                   │ updated           │                   │                   │
       │                   │◀──────────────────────────────────────│                   │
       │                   │                   │                   │                   │
       │                   │ 15. Log audit     │                   │                   │
       │                   │ event             │                   │                   │
       │                   │──────────────────────────────────────▶│                   │
       │                   │                   │                   │                   │
       │                   │ 16. Audit logged  │                   │                   │
       │                   │ successfully      │                   │                   │
       │                   │◀──────────────────────────────────────│                   │
       │                   │                   │                   │                   │
       │ 17. Login success │                   │                   │                   │
       │ response with     │                   │                   │                   │
       │ tokens            │                   │                   │                   │
       │◀──────────────────│                   │                   │                   │
       │                   │                   │                   │                   │
       │ 18. Store tokens  │                   │                   │                   │
       │ in localStorage   │                   │                   │                   │
       │                   │                   │                   │                   │
       │ 19. Redirect to   │                   │                   │                   │
       │ dashboard         │                   │                   │                   │
```

## Error Scenarios

### Invalid Credentials
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Frontend  │    │   Backend   │    │   Database  │
│   (React)   │    │  (Express)  │    │ (PostgreSQL)│
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │ 1. Submit login   │                   │
       │ with wrong        │                   │
       │ password          │                   │
       │──────────────────▶│                   │
       │                   │                   │
       │                   │ 2. Find user by   │                   │
       │                   │ email             │                   │
       │                   │──────────────────▶│
       │                   │                   │
       │                   │ 3. User found     │                   │
       │                   │◀──────────────────│
       │                   │                   │
       │                   │ 4. Compare        │                   │
       │                   │ password (fails)  │                   │
       │                   │                   │
       │ 5. Error response │                   │
       │ (invalid          │                   │
       │ credentials)      │                   │
       │◀──────────────────│                   │
       │                   │                   │
       │ 6. Display error  │                   │
       │ message           │                   │
```

### User Not Found
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Frontend  │    │   Backend   │    │   Database  │
│   (React)   │    │  (Express)  │    │ (PostgreSQL)│
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │ 1. Submit login   │                   │
       │ with non-existent │                   │
       │ email             │                   │
       │──────────────────▶│                   │
       │                   │                   │
       │                   │ 2. Find user by   │                   │
       │                   │ email             │                   │
       │                   │──────────────────▶│
       │                   │                   │
       │                   │ 3. User not found │                   │
       │                   │◀──────────────────│
       │                   │                   │
       │ 4. Error response │                   │
       │ (user not found)  │                   │
       │◀──────────────────│                   │
       │                   │                   │
       │ 5. Display error  │                   │
       │ message           │                   │
```

### Account Deactivated
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Frontend  │    │   Backend   │    │   Database  │
│   (React)   │    │  (Express)  │    │ (PostgreSQL)│
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │ 1. Submit login   │                   │
       │ with valid        │                   │
       │ credentials       │                   │
       │──────────────────▶│                   │
       │                   │                   │
       │                   │ 2. Find user by   │                   │
       │                   │ email             │                   │
       │                   │──────────────────▶│
       │                   │                   │
       │                   │ 3. User found     │                   │
       │                   │ (is_active=false) │                   │
       │                   │◀──────────────────│
       │                   │                   │
       │                   │ 4. Check if user  │                   │
       │                   │ is active (fails) │                   │
       │                   │                   │
       │ 5. Error response │                   │
       │ (account          │                   │
       │ deactivated)      │                   │
       │◀──────────────────│                   │
       │                   │                   │
       │ 6. Display error  │                   │
       │ message           │                   │
```

## Implementation Notes

### Frontend Responsibilities
- Form validation (client-side)
- API call handling
- Error message display
- Token storage in localStorage
- Automatic token refresh
- Navigation after successful login

### Backend Responsibilities
- Input validation (server-side)
- User authentication
- Password verification
- JWT token generation
- Session management
- Audit logging
- Rate limiting for login attempts

### Security Considerations
- Password hashing verification with bcrypt
- JWT token expiration (short-lived access tokens)
- Refresh token rotation
- Account lockout after failed attempts
- Secure cookie settings
- CSRF protection

### Performance Optimizations
- Database indexing on email field
- Redis caching for active sessions
- Connection pooling
- Async password comparison
- Efficient token generation

### Token Management
- Access token: 15 minutes expiration
- Refresh token: 7 days expiration
- Token rotation on refresh
- Secure storage in Redis
- Automatic cleanup of expired tokens 
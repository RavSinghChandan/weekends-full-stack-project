# Entity Relationship Diagram (ERD)

## Database Schema Overview

This document provides a visual representation of the database entities and their relationships for the Fullstack System Design App.

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              DATABASE SCHEMA                                │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                                USERS                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ PK: id (BIGINT AUTO_INCREMENT)                                             │
│     email (VARCHAR(255) UNIQUE NOT NULL)                                   │
│     password_hash (VARCHAR(255) NOT NULL)                                  │
│     first_name (VARCHAR(100) NOT NULL)                                     │
│     last_name (VARCHAR(100) NOT NULL)                                      │
│     role (ENUM: USER, ADMIN, MODERATOR) DEFAULT 'USER'                     │
│     is_active (BOOLEAN) DEFAULT TRUE                                       │
│     email_verified (BOOLEAN) DEFAULT FALSE                                 │
│     created_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP                       │
│     updated_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP ON UPDATE             │
│     last_login (TIMESTAMP) NULL                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 1:1
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                             USER_PROFILES                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│ PK: id (BIGINT AUTO_INCREMENT)                                             │
│ FK: user_id (BIGINT NOT NULL) → users.id                                   │
│     avatar_url (VARCHAR(500))                                              │
│     bio (TEXT)                                                             │
│     phone (VARCHAR(20))                                                    │
│     date_of_birth (DATE)                                                   │
│     address (JSON)                                                         │
│     preferences (JSON)                                                     │
│     created_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP                       │
│     updated_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP ON UPDATE             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                                USERS                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ PK: id (BIGINT AUTO_INCREMENT)                                             │
│     email (VARCHAR(255) UNIQUE NOT NULL)                                   │
│     password_hash (VARCHAR(255) NOT NULL)                                  │
│     first_name (VARCHAR(100) NOT NULL)                                     │
│     last_name (VARCHAR(100) NOT NULL)                                      │
│     role (ENUM: USER, ADMIN, MODERATOR) DEFAULT 'USER'                     │
│     is_active (BOOLEAN) DEFAULT TRUE                                       │
│     email_verified (BOOLEAN) DEFAULT FALSE                                 │
│     created_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP                       │
│     updated_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP ON UPDATE             │
│     last_login (TIMESTAMP) NULL                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 1:N
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                                SESSIONS                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ PK: id (BIGINT AUTO_INCREMENT)                                             │
│ FK: user_id (BIGINT NOT NULL) → users.id                                   │
│     token_hash (VARCHAR(255) NOT NULL)                                     │
│     expires_at (TIMESTAMP NOT NULL)                                        │
│     is_active (BOOLEAN) DEFAULT TRUE                                       │
│     created_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP                       │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                                USERS                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ PK: id (BIGINT AUTO_INCREMENT)                                             │
│     email (VARCHAR(255) UNIQUE NOT NULL)                                   │
│     password_hash (VARCHAR(255) NOT NULL)                                  │
│     first_name (VARCHAR(100) NOT NULL)                                     │
│     last_name (VARCHAR(100) NOT NULL)                                      │
│     role (ENUM: USER, ADMIN, MODERATOR) DEFAULT 'USER'                     │
│     is_active (BOOLEAN) DEFAULT TRUE                                       │
│     email_verified (BOOLEAN) DEFAULT FALSE                                 │
│     created_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP                       │
│     updated_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP ON UPDATE             │
│     last_login (TIMESTAMP) NULL                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 1:N (Optional)
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              AUDIT_LOGS                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ PK: id (BIGINT AUTO_INCREMENT)                                             │
│ FK: user_id (BIGINT) → users.id (NULL allowed)                             │
│     action (VARCHAR(100) NOT NULL)                                         │
│     resource_type (VARCHAR(50))                                            │
│     resource_id (BIGINT)                                                   │
│     details (JSON)                                                         │
│     ip_address (VARCHAR(45))                                               │
│     user_agent (TEXT)                                                      │
│     created_at (TIMESTAMP) DEFAULT CURRENT_TIMESTAMP                       │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Relationship Details

### 1. Users ↔ User_Profiles (1:1)
- **Relationship**: One-to-One
- **Cardinality**: Each user has exactly one profile, each profile belongs to exactly one user
- **Foreign Key**: `user_profiles.user_id` references `users.id`
- **Cascade**: DELETE CASCADE (if user is deleted, profile is deleted)
- **Business Rule**: Profile is created automatically when user registers

### 2. Users ↔ Sessions (1:N)
- **Relationship**: One-to-Many
- **Cardinality**: Each user can have multiple active sessions, each session belongs to exactly one user
- **Foreign Key**: `sessions.user_id` references `users.id`
- **Cascade**: DELETE CASCADE (if user is deleted, all sessions are deleted)
- **Business Rule**: Sessions are automatically cleaned up when expired

### 3. Users ↔ Audit_Logs (1:N Optional)
- **Relationship**: One-to-Many (Optional)
- **Cardinality**: Each user can have multiple audit logs, each audit log can optionally belong to a user
- **Foreign Key**: `audit_logs.user_id` references `users.id` (NULL allowed)
- **Cascade**: DELETE SET NULL (if user is deleted, audit logs remain but user_id becomes NULL)
- **Business Rule**: Audit logs are kept for compliance and security purposes

## Index Strategy

### Primary Indexes
- All tables use `id` as primary key with AUTO_INCREMENT
- Ensures unique identification and fast lookups

### Foreign Key Indexes
- `user_profiles.user_id` → `users.id`
- `sessions.user_id` → `users.id`
- `audit_logs.user_id` → `users.id`

### Performance Indexes
- `users.email` (UNIQUE) - Fast login lookups
- `users.role` - Role-based queries
- `users.is_active` - Active user filtering
- `users.created_at` - Time-based queries
- `sessions.token_hash` - Session validation
- `sessions.expires_at` - Expired session cleanup
- `sessions.is_active` - Active session filtering
- `audit_logs.action` - Action-based filtering
- `audit_logs.created_at` - Time-based audit queries

## Data Flow Diagram

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Angular   │    │ Spring Boot │    │   MySQL     │
│  Frontend   │    │   Backend   │    │  Database   │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │ 1. User Action    │                   │
       │──────────────────▶│                   │
       │                   │                   │
       │                   │ 2. Validate       │
       │                   │ Request           │
       │                   │                   │
       │                   │ 3. Process        │
       │                   │ Business Logic    │
       │                   │                   │
       │                   │ 4. Database       │
       │                   │ Operations        │
       │                   │──────────────────▶│
       │                   │                   │
       │                   │ 5. Database       │
       │                   │ Response          │
       │                   │◀──────────────────│
       │                   │                   │
       │ 6. API Response   │                   │
       │◀──────────────────│                   │
       │                   │                   │
       │ 7. Update UI      │                   │
       │                   │                   │
```

## Normalization

### First Normal Form (1NF)
- All attributes contain atomic values
- No repeating groups
- Primary key identified for each table

### Second Normal Form (2NF)
- All tables are in 1NF
- No partial dependencies on primary key
- User profiles separated from users table

### Third Normal Form (3NF)
- All tables are in 2NF
- No transitive dependencies
- Audit logs reference users but don't duplicate user data

## Denormalization Considerations

### When to Denormalize
1. **Performance**: Frequently joined data
2. **Reporting**: Complex aggregations
3. **Caching**: Read-heavy operations

### Potential Denormalizations
- User name in audit logs (for faster reporting)
- Session count in user table (for dashboard)
- Last activity timestamp in user table

## Data Integrity Constraints

### Entity Integrity
- Primary keys are NOT NULL and UNIQUE
- AUTO_INCREMENT ensures uniqueness

### Referential Integrity
- Foreign key constraints with appropriate CASCADE rules
- ON DELETE CASCADE for dependent data
- ON DELETE SET NULL for audit logs

### Domain Integrity
- ENUM constraints for role values
- CHECK constraints for email format
- NOT NULL constraints for required fields

### Business Rules
- Email must be unique across all users
- Password hash must be encrypted
- Sessions must have expiration dates
- Audit logs must have action descriptions 
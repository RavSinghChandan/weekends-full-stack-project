# Low-Level Design

## Component Interactions

### Backend Architecture Flow
```
Client Request → API Gateway → Authentication Middleware → Route Handler → Service Layer → Repository Layer → Database
```

### Frontend Architecture Flow
```
User Action → Component → Redux Action → API Call → Backend → Response → Redux Reducer → Component Update
```

## Database Schema Design

### Users Table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) DEFAULT 'user',
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);
```

### User Profiles Table
```sql
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    avatar_url VARCHAR(500),
    bio TEXT,
    phone VARCHAR(20),
    date_of_birth DATE,
    address JSONB,
    preferences JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Sessions Table
```sql
CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);
```

### Audit Logs Table
```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50),
    resource_id UUID,
    details JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## API Endpoints Specification

### Authentication Endpoints

#### POST /api/v1/auth/register
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "user"
    },
    "token": "jwt_token_here"
  },
  "message": "User registered successfully"
}
```

#### POST /api/v1/auth/login
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "user"
    },
    "token": "jwt_token_here",
    "refreshToken": "refresh_token_here"
  },
  "message": "Login successful"
}
```

#### POST /api/v1/auth/logout
**Headers:** `Authorization: Bearer <token>`

**Response:**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

#### POST /api/v1/auth/refresh
```json
{
  "refreshToken": "refresh_token_here"
}
```

### User Management Endpoints

#### GET /api/v1/users/profile
**Headers:** `Authorization: Bearer <token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "user",
      "profile": {
        "avatarUrl": "https://example.com/avatar.jpg",
        "bio": "Software Developer",
        "phone": "+1234567890"
      }
    }
  }
}
```

#### PUT /api/v1/users/profile
**Headers:** `Authorization: Bearer <token>`

```json
{
  "firstName": "John",
  "lastName": "Smith",
  "profile": {
    "bio": "Updated bio",
    "phone": "+1234567890"
  }
}
```

### Health Check Endpoint

#### GET /api/v1/health
**Response:**
```json
{
  "success": true,
  "data": {
    "status": "healthy",
    "timestamp": "2024-01-01T00:00:00Z",
    "version": "1.0.0",
    "uptime": 3600,
    "database": "connected",
    "redis": "connected"
  }
}
```

## Service Layer Implementation

### User Service
```typescript
interface UserService {
  register(userData: RegisterUserDto): Promise<UserResponse>;
  login(credentials: LoginDto): Promise<AuthResponse>;
  logout(userId: string, token: string): Promise<void>;
  refreshToken(refreshToken: string): Promise<AuthResponse>;
  getProfile(userId: string): Promise<UserProfile>;
  updateProfile(userId: string, profileData: UpdateProfileDto): Promise<UserProfile>;
  changePassword(userId: string, passwordData: ChangePasswordDto): Promise<void>;
}
```

### Authentication Service
```typescript
interface AuthService {
  generateTokens(userId: string): Promise<TokenPair>;
  validateToken(token: string): Promise<DecodedToken>;
  refreshTokens(refreshToken: string): Promise<TokenPair>;
  invalidateToken(token: string): Promise<void>;
  hashPassword(password: string): Promise<string>;
  comparePassword(password: string, hash: string): Promise<boolean>;
}
```

### Audit Service
```typescript
interface AuditService {
  logAction(action: string, userId: string, details: any): Promise<void>;
  getAuditLogs(filters: AuditLogFilters): Promise<PaginatedAuditLogs>;
}
```

## Middleware Implementation

### Authentication Middleware
```typescript
const authenticateToken = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) {
      return res.status(401).json({
        success: false,
        error: {
          code: 'UNAUTHORIZED',
          message: 'Access token required'
        }
      });
    }

    const decoded = await authService.validateToken(token);
    req.user = decoded;
    next();
  } catch (error) {
    return res.status(401).json({
      success: false,
      error: {
        code: 'INVALID_TOKEN',
        message: 'Invalid or expired token'
      }
    });
  }
};
```

### Role-Based Authorization Middleware
```typescript
const authorizeRole = (roles: string[]) => {
  return (req: Request, res: Response, next: NextFunction) => {
    if (!req.user) {
      return res.status(401).json({
        success: false,
        error: {
          code: 'UNAUTHORIZED',
          message: 'Authentication required'
        }
      });
    }

    if (!roles.includes(req.user.role)) {
      return res.status(403).json({
        success: false,
        error: {
          code: 'FORBIDDEN',
          message: 'Insufficient permissions'
        }
      });
    }

    next();
  };
};
```

### Validation Middleware
```typescript
const validateRequest = (schema: Joi.Schema) => {
  return (req: Request, res: Response, next: NextFunction) => {
    const { error } = schema.validate(req.body);
    if (error) {
      return res.status(400).json({
        success: false,
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid request data',
          details: error.details.map(detail => ({
            field: detail.path.join('.'),
            message: detail.message
          }))
        }
      });
    }
    next();
  };
};
```

## Error Handling Strategy

### Global Error Handler
```typescript
const globalErrorHandler = (error: Error, req: Request, res: Response, next: NextFunction) => {
  console.error('Error:', error);

  if (error instanceof ValidationError) {
    return res.status(400).json({
      success: false,
      error: {
        code: 'VALIDATION_ERROR',
        message: 'Invalid input data',
        details: error.details
      }
    });
  }

  if (error instanceof AuthenticationError) {
    return res.status(401).json({
      success: false,
      error: {
        code: 'AUTHENTICATION_ERROR',
        message: error.message
      }
    });
  }

  if (error instanceof AuthorizationError) {
    return res.status(403).json({
      success: false,
      error: {
        code: 'AUTHORIZATION_ERROR',
        message: error.message
      }
    });
  }

  return res.status(500).json({
    success: false,
    error: {
      code: 'INTERNAL_SERVER_ERROR',
      message: 'An unexpected error occurred'
    }
  });
};
```

## Security Implementation

### Password Security
```typescript
const passwordConfig = {
  saltRounds: 12,
  minLength: 8,
  requireUppercase: true,
  requireLowercase: true,
  requireNumbers: true,
  requireSpecialChars: true
};

const hashPassword = async (password: string): Promise<string> => {
  return bcrypt.hash(password, passwordConfig.saltRounds);
};

const validatePassword = (password: string): boolean => {
  const hasUpperCase = /[A-Z]/.test(password);
  const hasLowerCase = /[a-z]/.test(password);
  const hasNumbers = /\d/.test(password);
  const hasSpecialChars = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  
  return password.length >= passwordConfig.minLength &&
         hasUpperCase && hasLowerCase && hasNumbers && hasSpecialChars;
};
```

### JWT Configuration
```typescript
const jwtConfig = {
  accessToken: {
    secret: process.env.JWT_ACCESS_SECRET,
    expiresIn: '15m'
  },
  refreshToken: {
    secret: process.env.JWT_REFRESH_SECRET,
    expiresIn: '7d'
  }
};

const generateTokens = (userId: string): TokenPair => {
  const accessToken = jwt.sign(
    { userId, type: 'access' },
    jwtConfig.accessToken.secret,
    { expiresIn: jwtConfig.accessToken.expiresIn }
  );

  const refreshToken = jwt.sign(
    { userId, type: 'refresh' },
    jwtConfig.refreshToken.secret,
    { expiresIn: jwtConfig.refreshToken.expiresIn }
  );

  return { accessToken, refreshToken };
};
```

## Performance Optimizations

### Database Query Optimization
```typescript
// Use indexes for frequently queried fields
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

// Implement pagination for large datasets
const getPaginatedResults = async (page: number, limit: number) => {
  const offset = (page - 1) * limit;
  const results = await db.query(
    'SELECT * FROM table_name LIMIT $1 OFFSET $2',
    [limit, offset]
  );
  return results;
};
```

### Caching Strategy
```typescript
const cacheConfig = {
  userProfile: {
    ttl: 300, // 5 minutes
    key: (userId: string) => `user:profile:${userId}`
  },
  userPermissions: {
    ttl: 600, // 10 minutes
    key: (userId: string) => `user:permissions:${userId}`
  }
};

const getUserProfile = async (userId: string) => {
  const cacheKey = cacheConfig.userProfile.key(userId);
  let profile = await redis.get(cacheKey);
  
  if (!profile) {
    profile = await userRepository.findById(userId);
    await redis.setex(cacheKey, cacheConfig.userProfile.ttl, JSON.stringify(profile));
  }
  
  return JSON.parse(profile);
};
```

## Testing Strategy

### Unit Tests
```typescript
describe('UserService', () => {
  describe('register', () => {
    it('should create a new user successfully', async () => {
      const userData = {
        email: 'test@example.com',
        password: 'Password123!',
        firstName: 'John',
        lastName: 'Doe'
      };

      const result = await userService.register(userData);
      
      expect(result.success).toBe(true);
      expect(result.data.user.email).toBe(userData.email);
      expect(result.data.token).toBeDefined();
    });
  });
});
```

### Integration Tests
```typescript
describe('Auth API', () => {
  describe('POST /api/v1/auth/register', () => {
    it('should register a new user', async () => {
      const response = await request(app)
        .post('/api/v1/auth/register')
        .send({
          email: 'test@example.com',
          password: 'Password123!',
          firstName: 'John',
          lastName: 'Doe'
        });

      expect(response.status).toBe(201);
      expect(response.body.success).toBe(true);
      expect(response.body.data.user.email).toBe('test@example.com');
    });
  });
});
```

## Monitoring and Logging

### Application Logging
```typescript
const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.File({ filename: 'error.log', level: 'error' }),
    new winston.transports.File({ filename: 'combined.log' })
  ]
});

// Log all requests
app.use((req, res, next) => {
  logger.info('HTTP Request', {
    method: req.method,
    url: req.url,
    ip: req.ip,
    userAgent: req.get('User-Agent')
  });
  next();
});
```

### Health Check Implementation
```typescript
const healthCheck = async (req: Request, res: Response) => {
  try {
    // Check database connection
    await db.query('SELECT 1');
    
    // Check Redis connection
    await redis.ping();
    
    res.json({
      success: true,
      data: {
        status: 'healthy',
        timestamp: new Date().toISOString(),
        version: process.env.APP_VERSION,
        uptime: process.uptime(),
        database: 'connected',
        redis: 'connected'
      }
    });
  } catch (error) {
    res.status(503).json({
      success: false,
      data: {
        status: 'unhealthy',
        timestamp: new Date().toISOString(),
        error: error.message
      }
    });
  }
};
```

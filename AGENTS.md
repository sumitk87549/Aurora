# Aurora Flames - E-commerce Platform

## Project Overview

**Aurora Flames** is a full-stack e-commerce application for selling candles. It features a Spring Boot backend with Angular frontend, implementing a complete online shopping experience with payment processing, order management, and admin capabilities.

### Tech Stack

#### Backend
- **Java 21** with **Spring Boot 4.0.1**
- **PostgreSQL** database
- **Spring Security** with JWT authentication
- **Spring Data JPA** for data access
- **Lombok** for reducing boilerplate
- **Razorpay** integration for payments
- **Spring Mail** with Thymeleaf for email templates
- Server runs on port **8081**

#### Frontend
- **Angular 17.3.0** (standalone components)
- **TypeScript 5.4.2**
- **SCSS** for styling
- **RxJS** for reactive programming
- Development server runs on port **4200**

## Project Structure

### Backend (`src/main/java/com/ecomm/AuroraFlames/`)

```
config/          - Security, JWT, and initialization configuration
controller/      - REST API endpoints
dto/            - Data Transfer Objects for API communication
entity/         - JPA entities (database models)
repository/     - Spring Data JPA repositories
service/        - Business logic layer
util/           - Utility classes (DTOMapper)
```

### Frontend (`client/src/app/`)

```
auth/           - Login and registration components
admin/          - Admin dashboard
candles/        - Product listing and detail views
cart/           - Shopping cart
checkout/       - Checkout process
orders/         - Order management
profile/        - User profile
wishlist/       - Wishlist functionality
services/       - API services and guards
shared/         - Shared components (toast notifications)
data/           - Static data (location data for forms)
```

## Key Features

- **Authentication & Authorization**: JWT-based auth with USER and ADMIN roles
- **Product Management**: Browse candles, view details, images
- **Shopping Cart**: Add/remove items, quantity management
- **Wishlist**: Save favorite items for later
- **Order Processing**: Complete checkout flow with Razorpay payment integration
- **Email Notifications**: Order confirmations and updates
- **Admin Dashboard**: Product management, order tracking, statistics
- **User Profiles**: Manage shipping addresses and personal information

## Development Setup

### Prerequisites
- Java 21
- Node.js and npm
- PostgreSQL database
- Maven (wrapper included)

### Database Configuration
1. Create PostgreSQL database: `auroraflames`
2. Update credentials in `src/main/resources/application.properties`:
   - Database: `spring.datasource.url`, `username`, `password`
   - Default values: `localhost:5432`, user: `postgres`, password: `0000`

### Backend Setup
```bash
# Run Spring Boot application
./mvnw spring-boot:run

# Or build and run
./mvnw clean package
java -jar target/AuroraFlames-0.0.1-SNAPSHOT.jar
```

### Frontend Setup
```bash
cd client
npm install
npm start  # Runs on http://localhost:4200
```

## Important Configuration

### Required Environment Variables / Configuration

⚠️ **Before deploying or testing payments/emails, update these in `application.properties`:**

#### Razorpay (Payment Gateway)
```properties
razorpay.key.id=YOUR_RAZORPAY_KEY_ID
razorpay.key.secret=YOUR_RAZORPAY_SECRET
razorpay.webhook.secret=YOUR_WEBHOOK_SECRET
```
Get keys from: https://dashboard.razorpay.com/

#### Email Configuration (Gmail)
```properties
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
aurora.business.email=YOUR_EMAIL@gmail.com
aurora.admin.email=YOUR_ADMIN_EMAIL@gmail.com
```

**Important**: For Gmail, enable 2FA and generate an App Password at https://myaccount.google.com/apppasswords

#### JWT Secret
```properties
jwt.secret=YOUR_SECURE_SECRET_KEY_HERE
```
Change the default secret for production!

### Business Configuration
Update business details in `application.properties`:
- `aurora.business.name`
- `aurora.business.phone`
- `aurora.business.instagram`
- `aurora.business.address`
- `aurora.delivery.estimated-days`
- `aurora.delivery.free-shipping-threshold`

## API Endpoints

### Public Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/candles/**` - Browse candles (public access)
- `GET /uploads/**` - Access uploaded images

### Authenticated Endpoints
- `/api/cart/**` - Cart management
- `/api/wishlist/**` - Wishlist operations
- `/api/orders/**` - Order management
- `/api/user/**` - User profile

### Admin Endpoints (ADMIN role required)
- `/api/admin/**` - Admin operations (product management, dashboard stats)

## Code Conventions & Best Practices

### Backend

#### Entity Classes
- Use **Lombok** annotations: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Add `@PrePersist` and `@PreUpdate` for timestamp management
- Use proper JPA relationships: `@OneToMany`, `@ManyToOne`, etc.
- Mark sensitive fields with `@JsonIgnore` (e.g., passwords)

#### Controllers
- Use `@RestController` and `@RequestMapping` for REST APIs
- Follow RESTful conventions (GET, POST, PUT, DELETE)
- Use `@PreAuthorize` for method-level security
- Return appropriate HTTP status codes
- Use DTOs for request/response bodies (don't expose entities directly)

#### Services
- Keep business logic in service layer
- Use `@Transactional` for operations that modify data
- Inject repositories via constructor injection
- Handle exceptions appropriately

#### Security
- JWT tokens expire after 24 hours (configurable via `jwt.expiration`)
- Passwords encrypted with BCrypt
- Stateless session management
- CORS configured for `localhost:4200`

#### DTOs
- Create separate DTOs for requests and responses
- Use `DTOMapper` utility for entity-to-DTO conversions
- Keep DTOs simple and focused

### Frontend

#### Components
- Use **standalone components** (Angular 17+ pattern)
- Follow component naming: `component-name.component.ts`
- Organize by feature folders
- Keep templates and styles in separate files

#### Services
- Use `providedIn: 'root'` for singleton services
- API base URLs: `http://localhost:8081/api`
- Implement proper error handling
- Use RxJS operators for data transformation

#### Authentication
- JWT token stored in localStorage as `currentUser`
- `AuthInterceptor` automatically adds token to requests
- `AuthGuard` protects routes requiring authentication
- `AdminGuard` protects admin-only routes

#### State Management
- Use `BehaviorSubject` for shared state (e.g., `AuthService.currentUser`)
- Observable patterns for reactive updates
- Toast notifications for user feedback

#### Routing
- Define routes in `app.routes.ts`
- Use guards for protected routes: `canActivate: [AuthGuard]`
- Redirect unknown routes to home: `path: '**'`

## File Upload Handling

- Uploaded files stored in `/uploads` directory
- Candle images in `/uploads/candles/`
- Max file size: **10MB**
- Supported formats: Images (JPG, PNG, SVG)

## Database Schema

### Key Entities
- **User**: Authentication, profile, shipping addresses
- **Candle**: Product information, pricing, stock
- **CandleImage**: Product images (one-to-many with Candle)
- **Cart** / **CartItem**: Shopping cart
- **Order** / **OrderItem**: Order tracking
- **Wishlist** / **WishlistItem**: Saved items

### Relationships
- User has one Cart, one Wishlist, many Orders
- Candle has many CandleImages
- Cart has many CartItems
- Order has many OrderItems
- Wishlist has many WishlistItems

## Testing

### Backend
```bash
./mvnw test
```

### Frontend
```bash
cd client
npm test
```

## Common Tasks

### Adding a New Product Feature
1. Create entity in `entity/`
2. Create repository in `repository/`
3. Create DTOs in `dto/`
4. Implement service in `service/`
5. Create controller in `controller/`
6. Add Angular service in `client/src/app/services/`
7. Create component(s) in `client/src/app/`
8. Update routes if needed

### Adding Admin Functionality
1. Add method to `AdminController` with `@PreAuthorize("hasRole('ADMIN')")`
2. Update `AdminGuard` if needed
3. Add UI in `admin-dashboard` component

### Customizing Email Templates
1. Create/edit Thymeleaf templates in `src/main/resources/templates/`
2. Update `EmailService` to use new template
3. Test with configured email credentials

## Security Considerations

- ⚠️ **Never commit** sensitive credentials (database passwords, API keys, JWT secrets)
- Use environment variables for production secrets
- Change default JWT secret before deployment
- Enable HTTPS in production
- Implement rate limiting for auth endpoints (recommended)
- Validate all user inputs
- Sanitize data before database operations
- Keep dependencies updated

## CORS Configuration

- Development: Allows `http://localhost:4200`
- Production: Update `SecurityConfig` and `application.properties` with production URLs

## Known TODOs

Check `application.properties` for configuration TODOs:
- Replace Razorpay keys with actual credentials
- Replace email credentials with actual Gmail app password
- Update business information
- Change JWT secret for production

## Troubleshooting

### Backend won't start
- Check PostgreSQL is running
- Verify database credentials
- Check port 8081 is available
- Review application logs for errors

### Frontend can't connect to backend
- Verify backend is running on port 8081
- Check CORS configuration
- Inspect browser console for errors
- Verify API URLs in services

### Authentication issues
- Clear localStorage and try again
- Check JWT token expiration
- Verify security configuration
- Review JWT secret configuration

### Payment/Email not working
- Verify Razorpay/Gmail credentials are set
- Check application logs for API errors
- Test with Razorpay test mode first
- For Gmail, ensure app password is generated correctly

## Resources

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Angular Documentation: https://angular.io/docs
- Razorpay API: https://razorpay.com/docs/api/
- PostgreSQL: https://www.postgresql.org/docs/

## Additional Notes

- This is a demo/learning project - enhance security for production use
- Consider adding features: product reviews, search, filtering, analytics
- Implement proper logging and monitoring for production
- Add API documentation (Swagger/OpenAPI)
- Consider containerization (Docker) for easier deployment

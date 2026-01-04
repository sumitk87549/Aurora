# 🕯️ Aurora Flames

<div align="center">

![Aurora Flames Logo](https://img.shields.io/badge/Aurora-Flames-orange?style=for-the-badge&logo=fire&logoColor=white)

**A premium e-commerce platform for exquisite candles**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17.3.0-red?style=flat-square&logo=angular)](https://angular.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)

[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](CONTRIBUTING.md)

</div>

## ✨ About Aurora Flames

Aurora Flames is a sophisticated full-stack e-commerce application designed for selling premium candles online. Built with modern technologies, it provides a seamless shopping experience with secure payment processing, intuitive order management, and comprehensive admin capabilities.

### 🎯 Key Features

- **🛍️ Product Catalog**: Browse and discover beautiful candle collections
- **🔐 Secure Authentication**: JWT-based authentication with role-based access
- **🛒 Shopping Cart**: Add, remove, and manage items with real-time updates
- **❤️ Wishlist**: Save favorite products for later purchase
- **💳 Payment Integration**: Secure checkout with Razorpay payment gateway
- **📧 Email Notifications**: Automated order confirmations and updates
- **👤 User Profiles**: Manage shipping addresses and personal information
- **📊 Admin Dashboard**: Complete product and order management
- **📱 Responsive Design**: Beautiful UI that works on all devices

---

## 🏗️ Architecture

### Backend Stack
- **Java 21** with **Spring Boot 4.0.1**
- **PostgreSQL** database with JPA/Hibernate
- **Spring Security** with JWT authentication
- **Spring Mail** with Thymeleaf templates
- **Razorpay** payment integration
- **Lombok** for clean code

### Frontend Stack
- **Angular 17.3.0** with standalone components
- **TypeScript 5.4.2** for type safety
- **SCSS** for modern styling
- **RxJS** for reactive programming
- **Lucide Icons** for beautiful UI elements

---

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Node.js** 18+ and npm
- **PostgreSQL** database
- **Git** for version control

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/sumitk87549/Aurora.git
   cd Aurora
   ```

2. **Database Setup**
   ```bash
   # Create PostgreSQL database
   createdb auroraflames
   
   # Update credentials in src/main/resources/application.properties
   # Default: localhost:5432, user: postgres, password: 0000
   ```

3. **Backend Setup**
   ```bash
   # Using Maven wrapper
   ./mvnw spring-boot:run
   
   # Or build and run
   ./mvnw clean package
   java -jar target/AuroraFlames-0.0.1-SNAPSHOT.jar
   ```
   
   🌐 Backend runs on: **http://localhost:8081**

4. **Frontend Setup**
   ```bash
   cd client
   npm install
   npm start
   ```
   
   🌐 Frontend runs on: **http://localhost:4200**

---

## ⚙️ Configuration

### Required Environment Variables

⚠️ **Important**: Before deploying, update these configurations in `application.properties`:

#### 🔑 Payment Gateway (Razorpay)
```properties
razorpay.key.id=YOUR_RAZORPAY_KEY_ID
razorpay.key.secret=YOUR_RAZORPAY_SECRET
razorpay.webhook.secret=YOUR_WEBHOOK_SECRET
```
Get your keys from: [Razorpay Dashboard](https://dashboard.razorpay.com/)

#### 📧 Email Configuration (Gmail)
```properties
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
aurora.business.email=YOUR_EMAIL@gmail.com
aurora.admin.email=YOUR_ADMIN_EMAIL@gmail.com
```

**Note**: Enable 2FA and generate an App Password at [Google App Passwords](https://myaccount.google.com/apppasswords)

#### 🔐 Security Configuration
```properties
jwt.secret=YOUR_SECURE_SECRET_KEY_HERE
```
Change the default secret for production security!

#### 🏢 Business Information
```properties
aurora.business.name=Your Business Name
aurora.business.phone=+1234567890
aurora.business.instagram=@yourbusiness
aurora.business.address=Your Business Address
aurora.delivery.estimated-days=3-5
aurora.delivery.free-shipping-threshold=500
```

---

## 📁 Project Structure

```
Aurora/
├── src/main/java/com/ecomm/AuroraFlames/
│   ├── config/          # Security, JWT, and initialization
│   ├── controller/      # REST API endpoints
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # JPA entities
│   ├── repository/     # Spring Data repositories
│   ├── service/        # Business logic layer
│   └── util/           # Utility classes
├── src/main/resources/
│   ├── templates/      # Email templates (Thymeleaf)
│   └── uploads/        # File upload directory
├── client/src/app/
│   ├── auth/           # Authentication components
│   ├── admin/          # Admin dashboard
│   ├── candles/        # Product pages
│   ├── cart/           # Shopping cart
│   ├── checkout/       # Checkout flow
│   ├── orders/         # Order management
│   ├── profile/        # User profiles
│   ├── wishlist/       # Wishlist functionality
│   ├── services/       # API services and guards
│   └── shared/         # Shared components
└── README.md
```

---

## 🔌 API Documentation

### Public Endpoints
```
POST /api/auth/login          # User authentication
POST /api/auth/register       # User registration
GET  /api/candles/**          # Browse products
GET  /uploads/**              # Access uploaded images
```

### Authenticated Endpoints
```
/api/cart/**                  # Cart management
/api/wishlist/**              # Wishlist operations
/api/orders/**                # Order management
/api/user/**                  # User profile
```

### Admin Endpoints (ADMIN role)
```
/api/admin/**                 # Admin operations
```

---

## 🎨 UI/UX Features

### Design Highlights
- **Modern Interface**: Clean, intuitive design with smooth animations
- **Responsive Layout**: Optimized for desktop, tablet, and mobile
- **Dark Mode Support**: Comfortable viewing in any lighting
- **Toast Notifications**: Real-time feedback for user actions
- **Loading States**: Smooth loading indicators for better UX

### Key Components
- **Product Cards**: Beautiful product display with hover effects
- **Shopping Cart**: Real-time cart updates with quantity controls
- **Checkout Flow**: Multi-step checkout with form validation
- **Admin Dashboard**: Comprehensive analytics and management tools

---

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access**: USER and ADMIN role management
- **Password Encryption**: BCrypt hashing for secure storage
- **CORS Configuration**: Proper cross-origin resource sharing
- **Input Validation**: Comprehensive input sanitization
- **Session Management**: Stateless authentication with 24-hour expiry

---

## 🧪 Testing

### Backend Tests
```bash
./mvnw test
```

### Frontend Tests
```bash
cd client
npm test
```

### End-to-End Testing
```bash
cd client
npm run e2e
```

---

## 📊 Database Schema

### Core Entities
- **User**: Authentication, profiles, addresses
- **Candle**: Product information, pricing, inventory
- **CandleImage**: Product images (one-to-many)
- **Cart/CartItem**: Shopping cart management
- **Order/OrderItem**: Order tracking and history
- **Wishlist/WishlistItem**: Saved items

### Relationships
```
User 1→N Cart
User 1→N Wishlist  
User 1→N Orders
Candle 1→N CandleImages
Cart 1→N CartItems
Order 1→N OrderItems
```

---

## 🚀 Deployment

### Production Deployment

1. **Environment Setup**
   ```bash
   # Set production environment variables
   export SPRING_PROFILES_ACTIVE=prod
   export DATABASE_URL=your-production-db-url
   ```

2. **Build Application**
   ```bash
   ./mvnw clean package -Pprod
   ```

3. **Database Migration**
   ```bash
   # Run database migrations
   ./mvnw flyway:migrate
   ```

4. **Deploy**
   ```bash
   # Run production server
   java -jar target/AuroraFlames-0.0.1-SNAPSHOT.jar
   ```

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d
```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Guidelines
- Follow existing code style and conventions
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🆘 Support & Troubleshooting

### Common Issues

#### Backend won't start
- Check PostgreSQL is running
- Verify database credentials
- Ensure port 8081 is available
- Review application logs

#### Frontend connection issues
- Verify backend is running on port 8081
- Check CORS configuration
- Inspect browser console for errors

#### Authentication problems
- Clear localStorage and retry
- Check JWT token expiration
- Verify security configuration

#### Payment/Email issues
- Confirm Razorpay/Gmail credentials
- Check application logs for API errors
- Test with test mode first

### Getting Help

- 📧 **Email Support**: support@auroraflames.com
- 💬 **Discord Community**: [Join our Discord](https://discord.gg/auroraflames)
- 📖 **Documentation**: [View Full Docs](https://docs.auroraflames.com)
- 🐛 **Bug Reports**: [Open an Issue](https://github.com/sumitk87549/Aurora/issues)

---

## 🌟 Acknowledgments

- **Spring Team** for the amazing Spring Boot framework
- **Angular Team** for the powerful frontend framework
- **Razorpay** for seamless payment integration
- **PostgreSQL** for the reliable database system
- **Open Source Community** for inspiration and support

---

<div align="center">

**Made with ❤️ by [Aurora Flames Team](https://github.com/sumitk87549)**

[⭐ Star this repo](https://github.com/sumitk87549/Aurora) • [🐛 Report Issues](https://github.com/sumitk87549/Aurora/issues) • [📖 View Docs](https://docs.auroraflames.com)

</div>
 

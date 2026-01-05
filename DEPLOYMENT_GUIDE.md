# Production Deployment Guide - Aurora Flames Backend

## Overview
This guide will help you deploy your Spring Boot backend to Render using Neon PostgreSQL as the database.

## Files Created
1. `application-prod.properties` - Production configuration
2. `render.yaml` - Render service configuration

## Step 1: Update pom.xml for Production Profile

Add profile support to your pom.xml if not already present:

```xml
<profiles>
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <excludeDevtools>true</excludeDevtools>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

## Step 2: Deploy to Render

### Option A: Using render.yaml (Recommended)
1. Push your code to GitHub
2. Go to Render Dashboard
3. Click "New +" → "Blueprint"
4. Connect your GitHub repository
5. Render will automatically detect and use `render.yaml`

### Option B: Manual Setup
1. Go to Render Dashboard
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Configure:
   - **Name**: aurora-flames-backend
   - **Environment**: Java
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/AuroraFlames-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`
   - **Plan**: Free (or paid as needed)

## Step 3: Configure Environment Variables in Render

In your Render service dashboard, set these environment variables:

### Database Configuration (Already set in render.yaml)
```
DATABASE_URL=jdbc:postgresql://postgres:npg_FcAUEC4QL8hy@ep-morning-queen-a1mzyf4v-pooler.ap-southeast-1.aws.neon.tech/auroraflames?sslmode=require&channel_binding=require
DB_USERNAME=postgres
DB_PASSWORD=npg_FcAUEC4QL8hy
```

### Required Environment Variables
```
JWT_SECRET=your_secure_jwt_secret_here
FRONTEND_URL=https://your-frontend-domain.onrender.com
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_gmail_app_password
BUSINESS_EMAIL=your_business_email@gmail.com
ADMIN_EMAIL=your_admin_email@gmail.com
```

### Payment Configuration (Razorpay)
```
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_secret
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
```

### OAuth2 Configuration (Optional)
```
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
FACEBOOK_CLIENT_ID=your_facebook_app_id
FACEBOOK_CLIENT_SECRET=your_facebook_app_secret
```

## Step 4: Update CORS Configuration

Before deploying, update the `FRONTEND_URL` environment variable in Render to match your deployed frontend URL.

## Step 5: Health Check Endpoint

Add a health check endpoint if not already present:

```java
@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Aurora Flames Backend");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(response);
    }
}
```

## Step 6: Test the Deployment

1. Once deployed, your backend will be available at: `https://aurora-flames-backend.onrender.com`
2. Test the health endpoint: `https://aurora-flames-backend.onrender.com/api/health`
3. Test API endpoints: `https://aurora-flames-backend.onrender.com/api/candles`

## Important Notes

### Database
- Using Neon PostgreSQL as configured
- Connection string includes SSL requirements for security
- JPA will auto-create/update tables on first deployment

### Security
- JWT secret should be unique and secure
- All sensitive data is stored in environment variables
- SSL is enforced for database connections

### Performance
- DevTools are disabled in production
- Thymeleaf cache is enabled for better performance
- SQL logging is disabled in production

### Monitoring
- Render provides built-in monitoring
- Health check endpoint for service monitoring
- Logs available in Render dashboard

## Troubleshooting

### Common Issues
1. **Database Connection**: Ensure Neon database is running and credentials are correct
2. **CORS Issues**: Update FRONTEND_URL to match your deployed frontend
3. **Memory Issues**: Consider upgrading to paid plan if hitting memory limits
4. **Build Failures**: Check build logs in Render dashboard

### Environment Variable Issues
- Ensure all required environment variables are set
- Check for typos in variable names
- Verify sensitive data (passwords, keys) are correct

## Next Steps

1. Deploy frontend to Render or another hosting service
2. Update frontend API URLs to point to deployed backend
3. Configure custom domain if needed
4. Set up monitoring and alerts
5. Regularly update dependencies and security patches

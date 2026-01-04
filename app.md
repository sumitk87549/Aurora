I'll create a simple step-by-step guide for beginners to completely fill the [application.properties](cci:7://file:///home/prachi/Documents/GitHub/Aurora/src/main/resources/application.properties:0:0-0:0) file. Let me first read the current state to see what's still missing.

# 📖 Beginner's Guide to Fill application.properties

## 🎯 Overview
This guide will help you fill all the required credentials in the [application.properties](cci:7://file:///home/prachi/Documents/GitHub/Aurora/src/main/resources/application.properties:0:0-0:0) file step by step. You've already done some parts, so let's complete what's missing.

## ✅ What's Already Done
- ✅ JWT secret (line 18) - **Good!**
- ✅ Gmail username (line 47) - **Good!**
- ✅ Business email (line 65) - **Good!**
- ✅ Admin email (line 75) - **Good!**

## 🚧 What Still Needs to Be Filled

### 1. Gmail App Password (Line 48)
**Status**: ❌ Still needs `YOUR_APP_PASSWORD`

**How to Get Gmail App Password:**
1. **Enable 2FA** on your Gmail account
2. Go to [Google App Passwords](https://myaccount.google.com/apppasswords)
3. Select "Mail" → "Other (Custom name)"
4. Name it "Aurora Flames"
5. Click "Generate"
6. **Copy the 16-character password** (it looks like: `izlt otws odps lgfd`)

**Update Line 48:**
```properties
spring.mail.password=${MAIL_PASSWORD:your-16-character-app-password}
```

---

### 2. Razorpay Payment Keys (Lines 35-37)
**Status**: ❌ All three need to be filled

**How to Get Razorpay Keys:**
1. Go to [Razorpay Dashboard](https://dashboard.razorpay.com/)
2. **Sign up** (free for development)
3. Go to **Settings** → **API Keys**
4. Click **"Generate Key"** for **Test Mode**
5. You'll get:
   - `Key ID` (starts with `rzp_test_`)
   - `Key Secret` (long random string)
6. For webhook secret:
   - Go to **Webhooks** section
   - Create webhook for `order.paid` event
   - Copy the webhook secret

**Update Lines 35-37:**
```properties
razorpay.key.id=${RAZORPAY_KEY_ID:rzp_test_YOUR_ACTUAL_KEY_ID}
razorpay.key.secret=${RAZORPAY_KEY_SECRET:YOUR_ACTUAL_SECRET}
razorpay.webhook.secret=${RAZORPAY_WEBHOOK_SECRET:YOUR_WEBHOOK_SECRET}
```

---

### 3. Optional: Social Login (Lines 55-61)
**Status**: ❌ Optional - Skip if you don't want Google/Facebook login

**You can skip this section** if you only want email/password login.

---

## 🗄️ Database Setup (Lines 6-8)
**Status**: ✅ Already configured with defaults

**If you want to change database settings:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/auroraflames
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
```

---

## 🎯 Quick Start Guide

### Step 1: Gmail App Password (5 minutes)
```bash
# 1. Go to: https://myaccount.google.com/apppasswords
# 2. Generate app password for "Aurora Flames"
# 3. Copy the 16-character password
# 4. Replace YOUR_APP_PASSWORD in line 48
```

### Step 2: Razorpay Keys (10 minutes)
```bash
# 1. Go to: https://dashboard.razorpay.com/
# 2. Sign up and verify email
# 3. Generate Test API keys
# 4. Copy keys to lines 35-37
```

### Step 3: Test the Application
```bash
# Start backend
./mvnw spring-boot:run

# In another terminal, start frontend
cd client
npm start
```

---

## 📋 Complete Fill-in Template

Copy and paste these values to replace the placeholders:

```properties
# Line 48 - Gmail App Password
spring.mail.password=${MAIL_PASSWORD:your-16-character-app-password}

# Lines 35-37 - Razorpay Keys
razorpay.key.id=${RAZORPAY_KEY_ID:rzp_test_your_actual_key_id}
razorpay.key.secret=${RAZORPAY_KEY_SECRET:your_actual_secret_key}
razorpay.webhook.secret=${RAZORPAY_WEBHOOK_SECRET:your_webhook_secret}
```

---

## 🔧 Commands to Help You

### Generate Test JWT Secret (if needed):
```bash
openssl rand -base64 64
```

### Test Gmail Connection:
```bash
# After filling Gmail credentials, test with:
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.mail.debug=true"
```

### Create Database (if needed):
```bash
createdb auroraflames
```

---

## ⚠️ Important Notes

1. **Never commit** real credentials to Git
2. **Use Test Mode** for Razorpay during development
3. **Keep secrets safe** - don't share them
4. **Gmail App Password** is different from your regular password
5. **Razorpay Test Mode** is free and safe for development

---

## 🎉 Minimum Required to Run

For basic functionality, you only need:
1. ✅ JWT secret (already done)
2. ❌ Gmail app password (line 48)
3. ❌ Razorpay keys (lines 35-37) - only if you want payments

**Everything else is optional for basic testing!**

---

## 🆘 Troubleshooting

### Gmail Issues:
- Make sure 2FA is enabled
- Use App Password, not regular password
- Check "Less secure app access" is allowed

### Razorpay Issues:
- Use Test Mode keys (start with `rzp_test_`)
- Verify email address is confirmed
- Check webhook URL is correct

### Database Issues:
- Ensure PostgreSQL is running
- Verify database name `auroraflames` exists
- Check username/password are correct

---

## 📞 Need Help?

- **Gmail Issues**: https://support.google.com/accounts/answer/185833
- **Razorpay Support**: https://razorpay.com/support/
- **PostgreSQL Help**: https://www.postgresql.org/docs/

That's it! Fill in these 3 values and your Aurora Flames application will be ready to run! 🚀
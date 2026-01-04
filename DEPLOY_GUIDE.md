# 🚀 Aurora Flames - FREE Deployment Guide (Testing Over Internet)

> **Last Updated:** 4 Jan 2025  
> **Difficulty:** Beginner-friendly (No DevOps experience required)  
> **Cost:** FREE (for testing/demo purposes)

This guide shows you how to expose your **locally running application** to the internet so clients and developers can test it from anywhere.

---

## 📋 What You Need Running

Before starting, make sure these are running on your laptop:

| Service      | Command                          | Port  |
|--------------|----------------------------------|-------|
| PostgreSQL   | Already running as a service     | 5432  |
| Spring Boot  | `mvn spring-boot:run`            | 8081  |
| Angular      | `ng serve`                       | 4200  |

---

## 🎯 EASIEST METHOD: Using ngrok (Recommended)

**ngrok** is the industry-standard tool for exposing local servers to the internet. Free tier is perfect for testing.

### Step 1: Install ngrok

```bash
# Option A: Using Snap (Easiest on Ubuntu/Linux)
sudo snap install ngrok

# Option B: Download directly
# Go to https://ngrok.com/download and download for Linux
# Extract and move to /usr/local/bin
```

### Step 2: Create a FREE ngrok Account

1. Go to **https://ngrok.com**
2. Click **"Sign up for free"**
3. Sign up with Google/GitHub (fastest)
4. After signup, go to **Dashboard → Your Authtoken**
5. Copy your authtoken

### Step 3: Connect ngrok to Your Account

Run this once (replace `YOUR_AUTH_TOKEN` with your actual token):

```bash
ngrok config add-authtoken 37lhnet5Qwqc5fTxwtfidGVWw5L_Rg1doDrPEM4UWJ67X34x
```

### Step 4: Expose Your Backend (Spring Boot)

Open a **new terminal** and run:

```bash
ngrok http 8081
```

You'll see output like:
```
Forwarding    https://abc123.ngrok-free.app -> http://localhost:8081
```

✅ **Copy the `https://...ngrok-free.app` URL** - This is your public backend URL!

### Step 5: Update Angular to Use the Public Backend

Edit `client/src/app/config/api.config.ts`:

```typescript
// Change this line temporarily:
export const API_URL = 'https://abc123.ngrok-free.app/api';  // Use YOUR ngrok URL
```

### Step 6: Expose the Angular Frontend

Open **another new terminal** and run:

```bash
ngrok http 4200
```

You'll get another URL like `https://xyz789.ngrok-free.app`

✅ **Share this Frontend URL with your testers!**

---

## 🔥 ALTERNATIVE: Single URL Method (All-in-One)

If you prefer a **single URL** for the entire app (easier for testers), build the frontend into the backend:

### Step 1: Build Angular for Production

```bash
cd client
ng build --configuration production
cd ..
```

### Step 2: Copy Frontend to Backend

```bash
rm -rf src/main/resources/static/*
cp -r client/dist/client/browser/* src/main/resources/static/
```

### Step 3: Package Everything

```bash
mvn clean package -DskipTests
```

### Step 4: Run the Combined App

```bash
java -jar target/AuroraFlames-0.0.1-SNAPSHOT.jar
```

### Step 5: Expose with ngrok

```bash
ngrok http 8081
```

Now share the single ngrok URL - it serves both frontend and backend!

---

## 📱 Quick Reference

| What You're Sharing | ngrok Command |
|---------------------|---------------|
| Backend API only    | `ngrok http 8081` |
| Frontend only       | `ngrok http 4200` |
| Combined App        | Build frontend → Run JAR → `ngrok http 8081` |

---

## ⚠️ Important Notes

### Free Tier Limitations
- URLs change each time you restart ngrok
- Sessions expire after ~2 hours (just restart ngrok)
- Random subdomain names (upgrade for custom domains)

### For Testers
When someone opens your ngrok link for the first time, they'll see a **"Visit Site"** button - this is normal, just click it.

### Database
Your PostgreSQL database stays on your laptop. All data is stored locally.

---

## 🚑 Troubleshooting

| Problem | Solution |
|---------|----------|
| "ngrok not found" | Run `sudo snap install ngrok` |
| "Connection refused" | Make sure Spring Boot is running first |
| Images not loading | Check if backend is using correct port |
| CORS errors | The backend should already allow your origins |
| Session expired | Just restart ngrok and share new URL |

---

## 🎉 You're Done!

Your Aurora Flames website is now accessible from anywhere in the world via the ngrok URL. Share it with your clients and developers for testing!

**Pro Tip:** Keep the terminal with ngrok running. If you close it, the public URL stops working.

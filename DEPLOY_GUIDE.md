# 🚀 Aurora Flames Deployment Guide

Follow these simple steps to deploy your application locally and make it accessible from the internet for free.

## Prerequisites
- Limit: 75% of your daily internet patience 😅
- Tool: `ssh` (pre-installed on Linux)

## Step 1: Build the Frontend
Open your terminal in the `client` folder and run:

```bash
cd client
ng build --configuration production
cd ..
```

## Step 2: Bundle Frontend with Backend
Copy the built frontend files into the backend's static resource folder so they can be served together as one unit.

```bash
# Removing old static files if any
rm -rf src/main/resources/static/*

# Copy new files (Note: check if 'browser' folder exists, otherwise use 'dist/client/*')
cp -r client/dist/client/browser/* src/main/resources/static/
```

## Step 3: Package the Backend
Build the Spring Boot application into a single executable JAR file.

```bash
./mvnw clean package -DskipTests
```
*(Use `./mvnw` or `mvn` depending on your setup)*

## Step 4: Run the Application
Start your application.

```bash
java -jar target/AuroraFlames-0.0.1-SNAPSHOT.jar
```
Your app is now running locally at `http://localhost:8081`.

## Step 5: Expose to Internet
Open a **new terminal window** and run this magic command to create a secure tunnel. This uses `serveo.net` (no installation required).

```bash
ssh -R 80:localhost:8081 serveo.net
```

**Output:**
You will see a URL like `https://rando-mname.serveo.net`.
**Copy this URL and share it!** Anyone can now access your full app (Frontend + Backend) via this link.

---

### Troubleshooting
- **Images not loading?** Ensure the `uploads` directory is accessible or use the default images provided.
- **Login fails?** Check the terminal where Java is running for error logs.
- **Connection refused?** Make sure the Java app (Step 4) is running before starting the tunnel (Step 5).

# Deploying RailNova on Render with MySQL

Step-by-step instructions for deploying the **RailNova Railway Reservation Web Application** to [Render](https://render.com/).

---

## 1. Architecture Overview

RailNova is built as an all-in-one production Spring Boot application serving both:
- **Backend:** Spring Boot REST APIs with Spring Security & JPA
- **Frontend:** Responsive HTML5 / Bootstrap 5 / JavaScript SPA (served from `/static`)

This allows running the entire application on **a single Render Web Service**, completely eliminating Cross-Origin Resource Sharing (CORS) complexity and minimizing hosting costs.

```
+-------------------------------------------------------------+
|                     Render Cloud Service                    |
|                                                             |
|   +-------------------+          +-----------------------+  |
|   |   Static Web UI   | <=====>  |   Spring Boot APIs    |  |
|   |  (HTML, CSS, JS)  |          | (Port ${PORT:-8080})  |  |
|   +-------------------+          +-----------------------+  |
+----------------------------------------------|--------------+
                                               |
                                               v
                                    +----------------------+
                                    |    Cloud MySQL DB    |
                                    | (Aiven/Clever Cloud) |
                                    +----------------------+
```

---

## 2. Setting Up a Free Cloud MySQL Database

If you don't already have a cloud MySQL database, you can create one in 2 minutes for free:

### Option A: Aiven for MySQL (Recommended - Free Forever Tier)
1. Sign up at [Aiven.io](https://aiven.io/).
2. Create a new service: select **MySQL** (Free plan).
3. Under service overview, copy:
   - **Host**
   - **Port** (e.g. `12345`)
   - **User** (e.g. `avnadmin`)
   - **Password**
   - **Database Name** (e.g. `defaultdb` or `railnova_db`)
4. Construct the JDBC connection URL:
   ```
   jdbc:mysql://<HOST>:<PORT>/<DATABASE>?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   ```

### Option B: Clever Cloud (Free 20MB MySQL Add-on)
1. Sign up at [Clever Cloud](https://www.clever-cloud.com/).
2. Click **Create** -> **Add-on** -> **MySQL**.
3. Copy the provided JDBC URL, username, and password.

---

## 3. Deploying to Render via GitHub

### Step 1: Push Code to GitHub
Ensure your repository is pushed to your GitHub account:
```bash
git remote add origin https://github.com/Itssameer666/IRCTC.git
git branch -M main
git push -u origin main
```

### Step 2: Create a Web Service on Render
1. Log in to [Render.com](https://dashboard.render.com/).
2. Click **New +** -> **Web Service**.
3. Connect your GitHub repository `Itssameer666/IRCTC`.
4. Configure the service:
   - **Name:** `railnova-platform`
   - **Region:** Choose nearest (e.g. *Frankfurt* or *Singapore* or *Oregon*)
   - **Branch:** `main`
   - **Runtime:** `Docker` (Render will detect the included multi-stage `Dockerfile`)
   - **Plan:** `Free`

### Step 3: Configure Production Environment Variables
Under the **Environment Variables** tab, add the following variables:

| Variable Name | Value Description | Example Value |
|---|---|---|
| `PORT` | Web port used by Render | `8080` |
| `SPRING_PROFILES_ACTIVE` | Activates production MySQL profile | `prod` |
| `DB_URL` | Cloud MySQL JDBC URL | `jdbc:mysql://mysql-svc.aivencloud.com:12345/railnova_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USERNAME` | Cloud MySQL user | `avnadmin` |
| `DB_PASSWORD` | Cloud MySQL password | `YourSecurePassword123` |
| `JWT_SECRET` | 256-bit secret key for token signing | `RailNovaSuperSecureSecretKeyWithMinimum256BitsRequiredForHS256Signature2026` |
| `PAYMENT_KEY_ID` | Sandbox Payment Gateway Key ID | `rzp_test_railnova_sandbox_key` |
| `PAYMENT_KEY_SECRET` | Sandbox Payment Secret Key | `railnova_secret_sandbox_signature_key_2026` |

*(Optional Native Java build command without Docker)*:
- Build Command: `./mvnw clean package -DskipTests`
- Start Command: `java -Dserver.port=$PORT -jar target/railnova-backend-1.0.0.jar`

### Step 4: Click "Deploy Web Service"
1. Render will build the container and start the Spring Boot app.
2. In the deployment logs, look for:
   ```
   RailNova seed data successfully loaded! Ready for production and testing.
   Started RailNovaApplication in 8.42 seconds
   ```
3. Your live application will be available at:
   `https://railnova-platform.onrender.com/`

---

## 4. Verifying the Live Deployed Application

Once deployed, test the following:
1. **Frontend App:** Visit `https://railnova-platform.onrender.com/`
2. **Interactive Swagger Docs:** Visit `https://railnova-platform.onrender.com/swagger-ui.html`
3. **Search Trains:** Perform a search between New Delhi (`NDLS`) and Varanasi (`BSB`).
4. **Log In with Demo Accounts:**
   - **Admin:** `admin@railnova.com` / `Admin@123`
   - **Staff:** `staff@railnova.com` / `Staff@123`
   - **Passenger:** `user@railnova.com` / `User@123`
5. **PNR Check:** Search `8492019384` to verify the pre-seeded Vande Bharat confirmed ticket.

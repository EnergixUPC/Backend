# 📖 Backend-SEMS API Documentation

This documentation provides a comprehensive overview of all REST endpoints and WebSockets available in the Backend-SEMS application. This guide is tailored for the Frontend team to integrate the newly implemented features smoothly.

---

## 🔐 1. Authentication & IAM (`/api/v1/authentication`)
Handles user identity, registration, and session management using JWT.

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/sign-up` | Registers a new user account. | `{ "email": "...", "password": "...", "roles": ["ROLE_USER"] }` | `201 Created` - Returns user info. |
| `POST` | `/sign-in` | Authenticates a user and returns a JWT. | `{ "email": "...", "password": "..." }` | `200 OK` - Returns user info and JWT in `token` field. |
| `POST` | `/sign-out` | Securely logs out the user by revoking the current JWT. | **Header:** `Authorization: Bearer <token>` | `200 OK` - Invalidates the token in the backend. |

> [!IMPORTANT]
> The `/sign-out` endpoint is a new feature (US03). After calling this endpoint, the backend will reject any further requests using that specific token. You must also clear the token from the client's local storage.

### Users & Roles
- `GET /api/v1/users` - Fetch all users.
- `GET /api/v1/users/{id}` - Fetch a specific user.
- `PUT /api/v1/users/{id}/plan` - Update a user's subscription plan.
- `GET /api/v1/roles` - Fetch all roles.
- `GET /api/v1/roles/{id}` - Fetch a specific role.

---

## 👤 2. Profiles (`/api/v1/profiles`)
Manages user profile data.

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `GET` | `/me` | Get the profile of the currently authenticated user. | None | `200 OK` - Profile details. |
| `PATCH`| `/me/language` | Update the user's preferred platform language (US14). | `{ "language": "en" }` (or `"es"`) | `200 OK` |

---

## 🔌 3. Devices (`/api/v1/devices`)
Manages smart devices connected to the user's home.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Connect/Register a new smart device (US04). |
| `GET` | `/` | Get all devices (Admin). |
| `GET` | `/{deviceId}` | Get specific device details. |
| `GET` | `/users/{userId}` | Get all devices belonging to a specific user. |
| `GET` | `/{deviceId}/consumption` | Get aggregated consumption for a specific device. |
| `PATCH`| `/{deviceId}` | Update device details (e.g., status, name). |
| `DELETE`| `/{deviceId}` | Remove a connected device. |

---

## ⚡ 4. Consumptions (`/api/v1/consumptions`)
Endpoints to manually log or retrieve historical consumption data.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Record a new consumption entry (used mostly by devices/IoT). |
| `GET` | `/` | Get all consumption records. |
| `GET` | `/devices/{deviceId}` | Get all raw consumption records for a device. |
| `GET` | `/weekly/users/{userId}` | Get raw weekly consumption data by user. |

---

## 📊 5. Reports & Analytics (`/api/v1/reports`)
Provides aggregated data and analytics for user consumption. 

> [!TIP]
> The new PDF generation feature (US08, US11) allows the frontend to request downloadable PDF files directly from the backend by appending `?format=pdf`.

| Method | Endpoint | Description | Query Params |
|--------|----------|-------------|--------------|
| `GET` | `/top-devices` | Returns the top 3 highest consuming devices (US13). | None |
| `GET` | `/weekly-consumption` | Returns the weekly consumption summary (US08). | `?format=pdf` (Optional - downloads PDF) |
| `GET` | `/monthly-history` | Returns the historical consumption grouped by month (US11). | `?format=pdf` (Optional - downloads PDF) |
| `GET` | `/compare` | Compares consumption between two specified periods (US09). | `?period1=YYYY-MM&period2=YYYY-MM` |

---

## 📈 6. Dashboard (`/api/v1/dashboard`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Returns the monthly bill projection and basic dashboard stats (US10). |

---

## ⚙️ 7. Settings & Alert Thresholds (`/api/v1/settings`)
Manages custom thresholds for high consumption alerts.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Get global settings. |
| `PUT` | `/` | Update global settings. |
| `POST` | `/rules` | Create a new custom alert threshold rule (US07). |
| `PUT` | `/rules/{ruleId}` | Update an existing alert threshold rule. |
| `DELETE`| `/rules/{ruleId}` | Delete an alert threshold rule. |

---

## 🔔 8. Notifications (`/api/v1/notifications`)
Retrieves system notifications and alerts.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Create a manual notification. |
| `GET` | `/device/{deviceId}` | Get notifications related to a specific device. |
| `GET` | `/user/{userId}` | Get notifications for a specific user. |
| `GET` | `/` | Get all notifications. |

---

## 📰 9. Help Center & News (New Features)
New endpoints to serve content to the frontend for US12 and US15.

### Help Center (`/api/v1/help-center/articles`)
| Method | Endpoint | Description | Query Params |
|--------|----------|-------------|--------------|
| `GET` | `/` | Get a list of FAQ articles and help guides (US12). | `?query=keyword` (Optional search) |
| `POST` | `/` | Create a new help center article (Admin). | None |

### News & Tips (`/api/v1/news`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Get news articles and personalized energy-saving tips (US15). |
| `POST` | `/` | Publish a new tip or news article (Admin). |

---

## 📡 10. Real-time WebSockets (US05 & US06)
Real-time streaming for consumption data and instant alerts using STOMP over WebSockets.

**Connection URL**: `ws://<backend-domain>/ws`
**Protocol**: STOMP

### Available Topics (Subscriptions)
1. **Real-time Consumption**: 
   - **Topic**: `/topic/consumptions`
   - **Payload**: Pushes JSON updates every time a new consumption record is logged.
2. **Instant Alerts**:
   - **Topic**: `/topic/alerts`
   - **Payload**: Pushes JSON alert notifications instantly when a device exceeds its configured threshold.

> [!NOTE]
> Frontend teams should use libraries like `@stomp/stompjs` to connect to the `/ws` endpoint and subscribe to these topics.

---
*Generated by the Backend Team for seamless integration.*

# Postman collection — Backend-SEMS API

Cobertura completa de los 68 endpoints REST del backend (79 requests, algunos endpoints
se prueban con más de un caso), con foco especial en los endpoints usados por las
To-Be user stories (US20–US25):

- `⭐ US21 — Validate Receipt` → `POST /api/v1/dashboard/validate-receipt`
- `⭐ US22 — Compare Consumption` → `GET /api/v1/reports/compare`
- `⭐ US23 — Peak Hour Summary` → `GET /api/v1/reports/peak-hour-summary`
- `⭐ US23 — Update Settings (peak hour window)` → `PUT /api/v1/settings`

(US20 y US24 no tienen endpoint dedicado — la demo pública y el tutorial funcionan
enteramente en el Frontend. US25 se cubre indirectamente vía `POST /sign-up`.)

## Cómo importar

1. Abrir Postman → **Import** → seleccionar `Energix-Backend.postman_collection.json`
   y `Energix-Local.postman_environment.json`.
2. Seleccionar el environment **Energix Local** en la esquina superior derecha.
3. Levantar el backend local (`docker compose up -d` en `Backend/` + `./mvnw spring-boot:run`).
4. Click derecho en la colección → **Run collection**, dejando el orden de carpetas tal
   cual (1 → 16). Las carpetas están numeradas porque cada una depende de variables que
   dejan las anteriores (`token`, `userId`, `deviceId`, `ruleId`, `reportId`, `exportId`).
5. La carpeta **16. Auth - Sign Out** debe correr siempre al final: revoca el token de
   sesión, así que cualquier request después de esa carpeta fallará con 401.

## Cómo correrla por CLI (Newman)

```bash
cd Backend/postman
npx newman run Energix-Backend.postman_collection.json -e Energix-Local.postman_environment.json
```

## Notas de la última corrida

- 79/79 requests, 163/163 aserciones en verde contra el backend local.
- `POST /api/v1/authentication/reset-password` no requiere autenticación y resetea la
  contraseña de **cualquier** cuenta a un valor fijo (`password123`) con solo conocer el
  email — vale la pena revisarlo desde el punto de vista de seguridad.
- `POST /api/v1/reports/generate` y `/reports/create` no validan el `Content-Type` del
  body contra un shape definido (reciben `Map<String,Object>` libre) — cualquier campo
  desconocido se ignora silenciosamente en vez de rechazarse.

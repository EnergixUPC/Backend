# Diseño: variante de experimento forzada por entorno de despliegue

**Fecha:** 2026-07-05
**Repos afectados:** Backend (principal), Frontend, Landing-Page
**Contexto:** Capítulo 8 (Experiment-Driven Development) del Reporte — `Reporte/docs/Capitulo_8_Experiment_Driven_Development/Capitulo_8.md`

## Objetivo

El usuario quiere desplegar dos versiones de la aplicación (una desde `main`, otra desde
`develop`) donde cada una muestre de forma consistente una variante distinta de los
experimentos A/B ya implementados, para poder mostrar/evaluar ambas por separado
(producción = variante A/control, pruebas = variante B/tratamiento).

## Alcance

De las 6 historias de usuario del Capítulo 8 (US20-US25), solo **2 experimentos** tienen
una variante A/B real con lógica de código distinta entre variantes:

- **`demo-onboarding`** (Q1/US20): variante `A` = registro directo (`/register`), variante
  `B` = flujo demo (`/demo`).
- **`personalized-recommendations`** (Q3/US22): variante `control` = recomendaciones
  genéricas (`generateLegacyRecommendations`), variante `treatment` = recomendaciones
  personalizadas (`generatePersonalizedRecommendations`).

Las otras 4 historias (US21 validar consumo, US23 alertas hora pico, US24 tutorial, US25
botón "Probar ahora" del landing + `demo-conversion`) son features únicas ya implementadas,
sin una "variante A" alternativa en el código. **Quedan sin cambios, activas e idénticas en
ambas ramas.**

## Decisiones ya tomadas (confirmadas con el usuario)

1. **Mecanismo:** override configurable vía variables de entorno, no código distinto por
   rama. El comportamiento por defecto (sin configurar nada) es el actual: bucketing
   aleatorio 50/50 determinístico por `subjectId`.
2. **Topología de Backend:** un solo Backend compartido (el que ya corre en Render, con la
   data demo ya sembrada), no dos despliegues independientes. Esto requiere que cada
   request "diga" desde qué entorno viene (`production` o `test`), porque un solo proceso
   no puede tener dos configuraciones activas a la vez.
3. **Sin headers custom:** el tag de entorno viaja en el body (para POST) o como query
   param (para GET), no como header HTTP custom, para no requerir cambios de CORS.

## Diseño técnico

### Backend (`com.backendsems.experiments` + `SEMS.interfaces.rest.ReportsController`)

**Config nueva** (`application.properties`, default vacío en ambas ramas):

```properties
experiments.force-variant.production.demo-onboarding=${EXPERIMENTS_FORCE_VARIANT_PRODUCTION_DEMO_ONBOARDING:}
experiments.force-variant.production.personalized-recommendations=${EXPERIMENTS_FORCE_VARIANT_PRODUCTION_RECOMMENDATIONS:}
experiments.force-variant.test.demo-onboarding=${EXPERIMENTS_FORCE_VARIANT_TEST_DEMO_ONBOARDING:}
experiments.force-variant.test.personalized-recommendations=${EXPERIMENTS_FORCE_VARIANT_TEST_RECOMMENDATIONS:}
```

**Cambios de código:**

- `AssignVariantCommand`: gana un campo opcional `deploymentEnv` (`String`, nullable —
  valores esperados `"production"` / `"test"` / `null`).
- `ExperimentCommandServiceImpl.handle(AssignVariantCommand)`: antes de la lógica de
  bucketing/idempotencia existente, si `deploymentEnv` no es nulo y existe un valor
  configurado para `experiments.force-variant.<deploymentEnv>.<experimentKey>`, se
  devuelve ese valor directamente (y se persiste como una `ExperimentAssignment` normal,
  igual que hoy, para que `GET /api/v1/experiments/{key}/results` siga reportando datos
  reales). Si no hay override, cae al comportamiento actual sin cambios.
- `ExperimentsController.assign(...)`: `AssignmentRequestResource` gana el campo
  `deploymentEnv` (junto a `subjectId`), se pasa tal cual al command.
- `ReportsController`:
  - `resolveRecommendations(UserId userId)` → `resolveRecommendations(UserId userId, String deploymentEnv)`.
  - `compareConsumption(...)` (el único caller en el flujo en vivo que golpea el Frontend)
    gana `@RequestParam(required = false) String deploymentEnv` y lo pasa a
    `resolveRecommendations`.
  - **Fuera de alcance:** el segundo caller de `resolveRecommendations` dentro de
    `downloadReport(...)` (regeneración de PDF de un reporte ya guardado) NO recibe el
    parámetro — no es el flujo en vivo que se va a mostrar en las dos apps desplegadas, y
    añadir el tag ahí no tiene una señal "actual" con la que asociarlo. Sigue usando
    bucketing aleatorio como hoy.

**Test:** unit test para `ExperimentCommandServiceImpl.handle(AssignVariantCommand)`
cubriendo: (a) sin `deploymentEnv` → bucketing aleatorio (comportamiento actual sin
regresión), (b) con `deploymentEnv` y override configurado → devuelve el override y lo
persiste, (c) con `deploymentEnv` pero sin override configurado para ese experimento →
cae a bucketing aleatorio.

### Frontend (Angular)

- `src/environments/environment.prod.ts`: nuevo campo `deploymentEnv`.
  - En `main`: `deploymentEnv: 'production'`.
  - En `develop`: `deploymentEnv: 'test'`.
  - Esta es la **única línea que diverge intencionalmente entre ramas** en todo el
    diseño; todo lo demás es código idéntico y mergeable.
- `src/environments/environments.ts` (config de dev local, `ng serve`): `deploymentEnv: 'local'`. Como el Backend nunca tendrá configurado un override para `"local"`, esto cae siempre al bucketing aleatorio de siempre al desarrollar en el equipo local — mismo efecto que "sin forzado", pero sin ambigüedad de tipo (string explícito, no `undefined`).
- `ExperimentService.getVariant(experimentKey)`: agrega `deploymentEnv: environment.deploymentEnv` al body del `POST /api/v1/experiments/{key}/assignment`.
- `ReportResource.getCompare(...)`: agrega `deploymentEnv` como `HttpParams` (si está definido) a la llamada `GET /api/v1/reports/compare`.

### Landing-Page

- `experiment.js`: nueva constante `DEPLOYMENT_ENV`.
  - En `main`: `'production'`.
  - En `develop`: `'test'`.
  - Se agrega al body del `POST /api/v1/experiments/demo-onboarding/assignment` ya
    existente.
  - Nota aparte (no forma parte de este diseño, solo se documenta): `API_BASE` en este
    archivo sigue apuntando a `http://localhost:8080`, desactualizado respecto al Backend
    real en Render. No se toca en este cambio salvo que el usuario lo pida explícitamente.

## Configuración a aplicar en despliegue (responsabilidad del usuario)

En el único servicio de Backend en Render (compartido por ambas apps desplegadas):

```
EXPERIMENTS_FORCE_VARIANT_PRODUCTION_DEMO_ONBOARDING=A
EXPERIMENTS_FORCE_VARIANT_PRODUCTION_RECOMMENDATIONS=control
EXPERIMENTS_FORCE_VARIANT_TEST_DEMO_ONBOARDING=B
EXPERIMENTS_FORCE_VARIANT_TEST_RECOMMENDATIONS=treatment
```

En Vercel: el proyecto/deploy que sirve `main` no necesita env vars nuevas (usa
`environment.prod.ts` de esa rama). El proyecto/deploy que sirve `develop` tampoco
necesita env vars nuevas — el valor `deploymentEnv: 'test'` ya queda compilado en el
bundle de esa rama. Ambos apuntan al mismo `apiUrl` (el Backend único en Render).

## Fuera de alcance (explícitamente)

- No se crean segundos servicios/deploys de Backend.
- No se tocan las 4 historias sin variante A/B real (US21, US23, US24, US25).
- No se corrige el `API_BASE` desactualizado de `experiment.js` en Landing-Page.
- No se gestiona la creación de los proyectos/servicios en Render o Vercel — el usuario
  los administra directamente.
- El caller de `resolveRecommendations` dentro de `downloadReport` no recibe el tag de
  entorno.

## Verificación

- Backend: `./mvnw test` (nuevo test de `ExperimentCommandServiceImpl` + suite existente
  sin regresiones).
- Manual: con las env vars configuradas, `POST /api/v1/experiments/demo-onboarding/assignment`
  con `deploymentEnv: "production"` debe devolver siempre `"A"`, y con `"test"` siempre
  `"B"`, para distintos `subjectId`. Sin `deploymentEnv`, debe seguir repartiendo ~50/50
  entre varios `subjectId` distintos (comportamiento no roto).

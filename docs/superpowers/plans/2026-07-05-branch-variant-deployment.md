# Branch-Based Experiment Variant Deployment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let the two experiments with real A/B logic (`demo-onboarding`, `personalized-recommendations`) be forced to a fixed variant per deployment environment ("production" on `main`, "test" on `develop`), instead of the existing random 50/50 bucketing, so `main` and `develop` can each be deployed as a consistent, single-arm demo.

**Architecture:** A single shared Backend (already running on Render) resolves the forced variant from a `deploymentEnv` tag ("production" | "test" | absent) sent with each assignment request, checked against env-var-configured overrides, falling back to the existing random bucketing when absent or unconfigured. The Frontend and Landing-Page send that tag; only one line differs between `main` and `develop` in each of those two repos (the compiled-in tag value). Backend source is 100% identical between branches.

**Tech Stack:** Spring Boot 3 / Java 25 (Backend), Angular (Frontend), plain JS (Landing-Page), JUnit 5 + Mockito (Backend tests).

## Global Constraints

- Default behavior (no `deploymentEnv`, or no override configured) must stay byte-for-byte the existing random 50/50 bucketing — zero regression risk for any caller that doesn't send the new field.
- No new HTTP headers (avoids CORS allowlist changes) — the tag travels as a JSON body field (POST) or query param (GET).
- Only 2 experiments are in scope: `demo-onboarding` and `personalized-recommendations`. US21/US23/US24/US25 and `demo-conversion` are untouched.
- The `downloadReport` PDF-regeneration call site in `ReportsController` is explicitly out of scope (stays on random bucketing).
- The Landing-Page's stale `API_BASE` constant is explicitly out of scope — not touched.
- Full spec: `docs/superpowers/specs/2026-07-05-branch-variant-deployment-design.md` (this repo).
- Backend commands (`./mvnw ...`) must be run with the JDK 25.0.2 toolchain already installed via mise (the default `java` on PATH is 21, which this project's `pom.xml` rejects):
  ```bash
  JAVA_HOME="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2" PATH="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2\bin:$PATH" ./mvnw ...
  ```

---

### Task 1: Backend — forced-variant override in `ExperimentCommandServiceImpl`

**Files:**
- Modify: `Backend/src/main/java/com/backendsems/experiments/domain/model/commands/AssignVariantCommand.java`
- Modify: `Backend/src/main/java/com/backendsems/experiments/application/internal/commandservices/ExperimentCommandServiceImpl.java`
- Modify: `Backend/src/main/java/com/backendsems/experiments/interfaces/rest/resources/AssignmentRequestResource.java`
- Modify: `Backend/src/main/java/com/backendsems/experiments/interfaces/rest/ExperimentsController.java`
- Modify: `Backend/src/main/java/com/backendsems/SEMS/interfaces/rest/ReportsController.java`
- Modify: `Backend/src/main/resources/application.properties`
- Create: `Backend/src/test/java/com/backendsems/experiments/application/internal/commandservices/ExperimentCommandServiceImplTest.java`

**Interfaces:**
- Produces: `AssignVariantCommand(String experimentKey, String subjectId, String deploymentEnv)`; `ExperimentCommandServiceImpl` constructor `(ExperimentRepository, ExperimentAssignmentRepository, ExperimentEventRepository, String forcedProductionDemoOnboarding, String forcedProductionRecommendations, String forcedTestDemoOnboarding, String forcedTestRecommendations)`.
- Consumes: existing `ExperimentRepository.findById`, `ExperimentAssignmentRepository.findByExperimentKeyAndSubjectId`/`save`, `Experiment.parseVariants()`, `Experiment(String,String,boolean)` constructor, `ExperimentAssignment(String,String,String)` constructor — all unchanged.

- [ ] **Step 1: Update `AssignVariantCommand` to carry the deployment tag**

Replace the full file `Backend/src/main/java/com/backendsems/experiments/domain/model/commands/AssignVariantCommand.java` with:

```java
package com.backendsems.experiments.domain.model.commands;

/**
 * AssignVariantCommand
 * Solicita (o recupera, si ya existe) la variante asignada a un sujeto dentro de un experimento.
 * {@code deploymentEnv} es opcional ("production" | "test" | null): si coincide con un override
 * configurado (ver application.properties: experiments.force-variant.<deploymentEnv>.<experimentKey>),
 * fuerza esa variante en vez del bucketing aleatorio 50/50 por defecto.
 */
public record AssignVariantCommand(String experimentKey, String subjectId, String deploymentEnv) {
    public AssignVariantCommand {
        if (experimentKey == null || experimentKey.isBlank()) {
            throw new IllegalArgumentException("experimentKey cannot be null or blank");
        }
        if (subjectId == null || subjectId.isBlank()) {
            throw new IllegalArgumentException("subjectId cannot be null or blank");
        }
    }
}
```

- [ ] **Step 2: Fix the two existing callers so the project still compiles**

In `Backend/src/main/java/com/backendsems/SEMS/interfaces/rest/ReportsController.java`, change the `resolveRecommendations` method (around line 100) from:

```java
    private Map<String, Object> resolveRecommendations(UserId userId) {
        String variant = experimentCommandService.handle(
                new AssignVariantCommand(RECOMMENDATIONS_EXPERIMENT_KEY, userId.id().toString()));
```

to:

```java
    private Map<String, Object> resolveRecommendations(UserId userId, String deploymentEnv) {
        String variant = experimentCommandService.handle(
                new AssignVariantCommand(RECOMMENDATIONS_EXPERIMENT_KEY, userId.id().toString(), deploymentEnv));
```

Then update its two call sites. In `compareConsumption(...)` (around line 300):

```java
            var recommendationData = resolveRecommendations(userId);
```
→
```java
            var recommendationData = resolveRecommendations(userId, deploymentEnv);
```

(The `deploymentEnv` parameter for this one is added in Step 8 below.)

In `downloadReport(...)` (around line 534) — out of scope, pass `null` explicitly:

```java
                var recommendationData = resolveRecommendations(userId);
```
→
```java
                var recommendationData = resolveRecommendations(userId, null);
```

- [ ] **Step 3: Compile to confirm nothing else is broken**

Run:
```bash
cd Backend
JAVA_HOME="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2" PATH="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2\bin:$PATH" ./mvnw -q -o compile
```
Expected: no output, exit code 0 (it will fail here until Step 8's `deploymentEnv` param is added to `compareConsumption` — if so, add that param now rather than waiting, then re-run).

- [ ] **Step 4: Write the failing test for the override logic**

Create `Backend/src/test/java/com/backendsems/experiments/application/internal/commandservices/ExperimentCommandServiceImplTest.java`:

```java
package com.backendsems.experiments.application.internal.commandservices;

import com.backendsems.experiments.domain.model.aggregates.Experiment;
import com.backendsems.experiments.domain.model.commands.AssignVariantCommand;
import com.backendsems.experiments.domain.model.entities.ExperimentAssignment;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentAssignmentRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentEventRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperimentCommandServiceImplTest {

    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private ExperimentAssignmentRepository assignmentRepository;

    @Mock
    private ExperimentEventRepository eventRepository;

    private ExperimentCommandServiceImpl service(String forcedProdDemo, String forcedProdRecs,
                                                  String forcedTestDemo, String forcedTestRecs) {
        return new ExperimentCommandServiceImpl(
                experimentRepository, assignmentRepository, eventRepository,
                forcedProdDemo, forcedProdRecs, forcedTestDemo, forcedTestRecs);
    }

    @Test
    void handle_withProductionDeploymentEnvAndOverrideConfigured_returnsForcedVariantAndPersistsIt() {
        when(assignmentRepository.findByExperimentKeyAndSubjectId("demo-onboarding", "visitor-1"))
                .thenReturn(Optional.empty());

        ExperimentCommandServiceImpl service = service("A", "control", "B", "treatment");
        String variant = service.handle(new AssignVariantCommand("demo-onboarding", "visitor-1", "production"));

        assertEquals("A", variant);
        verify(assignmentRepository).save(any(ExperimentAssignment.class));
        verify(experimentRepository, never()).findById(any());
    }

    @Test
    void handle_withTestDeploymentEnvAndOverrideConfigured_returnsForcedVariant() {
        when(assignmentRepository.findByExperimentKeyAndSubjectId("personalized-recommendations", "42"))
                .thenReturn(Optional.empty());

        ExperimentCommandServiceImpl service = service("A", "control", "B", "treatment");
        String variant = service.handle(new AssignVariantCommand("personalized-recommendations", "42", "test"));

        assertEquals("treatment", variant);
    }

    @Test
    void handle_withoutDeploymentEnv_fallsBackToRandomBucketing() {
        when(assignmentRepository.findByExperimentKeyAndSubjectId("demo-onboarding", "visitor-2"))
                .thenReturn(Optional.empty());
        when(experimentRepository.findById("demo-onboarding"))
                .thenReturn(Optional.of(new Experiment("demo-onboarding", "A:50,B:50", true)));

        ExperimentCommandServiceImpl service = service("A", "control", "B", "treatment");
        String variant = service.handle(new AssignVariantCommand("demo-onboarding", "visitor-2", null));

        assertTrue(variant.equals("A") || variant.equals("B"));
        verify(experimentRepository).findById("demo-onboarding");
    }

    @Test
    void handle_withDeploymentEnvButNoOverrideConfiguredForThatExperiment_fallsBackToRandomBucketing() {
        when(assignmentRepository.findByExperimentKeyAndSubjectId("personalized-recommendations", "43"))
                .thenReturn(Optional.empty());
        when(experimentRepository.findById("personalized-recommendations"))
                .thenReturn(Optional.of(new Experiment("personalized-recommendations", "control:50,treatment:50", true)));

        // Simula @Value("...:") sin configurar: llega como string vacío.
        ExperimentCommandServiceImpl service = service("A", "", "B", "treatment");
        String variant = service.handle(new AssignVariantCommand("personalized-recommendations", "43", "production"));

        assertTrue(variant.equals("control") || variant.equals("treatment"));
    }

    @Test
    void handle_calledTwiceWithSameSubjectAndForcedVariant_returnsSameVariantWithoutSecondSave() {
        ExperimentAssignment existingAssignment = new ExperimentAssignment("demo-onboarding", "visitor-3", "A");
        when(assignmentRepository.findByExperimentKeyAndSubjectId("demo-onboarding", "visitor-3"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingAssignment));

        ExperimentCommandServiceImpl service = service("A", "control", "B", "treatment");
        String first = service.handle(new AssignVariantCommand("demo-onboarding", "visitor-3", "production"));
        String second = service.handle(new AssignVariantCommand("demo-onboarding", "visitor-3", "production"));

        assertEquals("A", first);
        assertEquals("A", second);
        verify(assignmentRepository, times(1)).save(any(ExperimentAssignment.class));
    }
}
```

- [ ] **Step 5: Run the test to verify it fails**

Run:
```bash
cd Backend
JAVA_HOME="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2" PATH="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2\bin:$PATH" ./mvnw -q -o test -Dtest=ExperimentCommandServiceImplTest
```
Expected: **compile error** — `ExperimentCommandServiceImpl` has no 7-arg constructor yet. That confirms the test is exercising code that doesn't exist yet.

- [ ] **Step 6: Implement the override logic**

Replace the full file `Backend/src/main/java/com/backendsems/experiments/application/internal/commandservices/ExperimentCommandServiceImpl.java` with:

```java
package com.backendsems.experiments.application.internal.commandservices;

import com.backendsems.experiments.domain.model.aggregates.Experiment;
import com.backendsems.experiments.domain.model.commands.AssignVariantCommand;
import com.backendsems.experiments.domain.model.commands.RecordExperimentEventCommand;
import com.backendsems.experiments.domain.model.commands.SeedExperimentsCommand;
import com.backendsems.experiments.domain.model.entities.ExperimentAssignment;
import com.backendsems.experiments.domain.model.entities.ExperimentEvent;
import com.backendsems.experiments.domain.services.ExperimentCommandService;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentAssignmentRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentEventRepository;
import com.backendsems.experiments.infrastructure.persistence.jpa.repositories.ExperimentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * ExperimentCommandServiceImpl
 * Bucketing determinístico e idempotente de sujetos a variantes, y registro de eventos.
 */
@Service
public class ExperimentCommandServiceImpl implements ExperimentCommandService {

    private final ExperimentRepository experimentRepository;
    private final ExperimentAssignmentRepository assignmentRepository;
    private final ExperimentEventRepository eventRepository;

    private final String forcedProductionDemoOnboarding;
    private final String forcedProductionRecommendations;
    private final String forcedTestDemoOnboarding;
    private final String forcedTestRecommendations;

    public ExperimentCommandServiceImpl(
            ExperimentRepository experimentRepository,
            ExperimentAssignmentRepository assignmentRepository,
            ExperimentEventRepository eventRepository,
            @Value("${experiments.force-variant.production.demo-onboarding:}") String forcedProductionDemoOnboarding,
            @Value("${experiments.force-variant.production.personalized-recommendations:}") String forcedProductionRecommendations,
            @Value("${experiments.force-variant.test.demo-onboarding:}") String forcedTestDemoOnboarding,
            @Value("${experiments.force-variant.test.personalized-recommendations:}") String forcedTestRecommendations) {
        this.experimentRepository = experimentRepository;
        this.assignmentRepository = assignmentRepository;
        this.eventRepository = eventRepository;
        this.forcedProductionDemoOnboarding = blankToNull(forcedProductionDemoOnboarding);
        this.forcedProductionRecommendations = blankToNull(forcedProductionRecommendations);
        this.forcedTestDemoOnboarding = blankToNull(forcedTestDemoOnboarding);
        this.forcedTestRecommendations = blankToNull(forcedTestRecommendations);
    }

    @Override
    public String handle(AssignVariantCommand command) {
        var existing = assignmentRepository.findByExperimentKeyAndSubjectId(command.experimentKey(), command.subjectId());
        if (existing.isPresent()) {
            return existing.get().getVariant();
        }

        String forcedVariant = resolveForcedVariant(command.deploymentEnv(), command.experimentKey());
        String variant = forcedVariant != null
                ? forcedVariant
                : pickVariant(mustFindExperiment(command.experimentKey()), command.subjectId());

        assignmentRepository.save(new ExperimentAssignment(command.experimentKey(), command.subjectId(), variant));
        return variant;
    }

    @Override
    public void handle(RecordExperimentEventCommand command) {
        String variant = command.variant();
        if (variant == null || variant.isBlank()) {
            variant = assignmentRepository
                    .findByExperimentKeyAndSubjectId(command.experimentKey(), command.subjectId())
                    .map(ExperimentAssignment::getVariant)
                    .orElse(null);
        }
        eventRepository.save(new ExperimentEvent(
                command.experimentKey(), command.subjectId(), variant, command.eventName(), command.metadata()));
    }

    @Override
    public void handle(SeedExperimentsCommand command) {
        seedIfMissing("demo-onboarding", "A:50,B:50");
        seedIfMissing("personalized-recommendations", "control:50,treatment:50");
        seedIfMissing("demo-conversion", "default:100");
    }

    private void seedIfMissing(String key, String variantsCsv) {
        if (experimentRepository.existsById(key)) return;
        experimentRepository.save(new Experiment(key, variantsCsv, true));
    }

    private Experiment mustFindExperiment(String experimentKey) {
        return experimentRepository.findById(experimentKey)
                .orElseThrow(() -> new IllegalArgumentException("Unknown experiment: " + experimentKey));
    }

    /**
     * Devuelve la variante forzada para (deploymentEnv, experimentKey) según
     * experiments.force-variant.*, o {@code null} si no aplica (deploymentEnv ausente, o sin
     * override configurado para ese par) — en cuyo caso se usa el bucketing aleatorio.
     */
    private String resolveForcedVariant(String deploymentEnv, String experimentKey) {
        if ("production".equals(deploymentEnv)) {
            if ("demo-onboarding".equals(experimentKey)) return forcedProductionDemoOnboarding;
            if ("personalized-recommendations".equals(experimentKey)) return forcedProductionRecommendations;
        } else if ("test".equals(deploymentEnv)) {
            if ("demo-onboarding".equals(experimentKey)) return forcedTestDemoOnboarding;
            if ("personalized-recommendations".equals(experimentKey)) return forcedTestRecommendations;
        }
        return null;
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    /** Hash estable de subjectId+experimentKey en [0,100) contra el peso acumulado de cada variante. */
    private String pickVariant(Experiment experiment, String subjectId) {
        var variants = experiment.parseVariants();
        int bucket = Math.floorMod((subjectId + "::" + experiment.getKey()).hashCode(), 100);
        int cumulative = 0;
        for (var variant : variants) {
            cumulative += variant.weight();
            if (bucket < cumulative) {
                return variant.name();
            }
        }
        return variants.get(variants.size() - 1).name();
    }
}
```

- [ ] **Step 7: Run the test to verify it passes**

Run:
```bash
cd Backend
JAVA_HOME="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2" PATH="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2\bin:$PATH" ./mvnw -q -o test -Dtest=ExperimentCommandServiceImplTest
```
Expected: `Tests run: 5, Failures: 0, Errors: 0` (check `Backend/target/surefire-reports/com.backendsems.experiments.application.internal.commandservices.ExperimentCommandServiceImplTest.txt`).

- [ ] **Step 8: Wire `deploymentEnv` through the two REST entry points**

In `Backend/src/main/java/com/backendsems/experiments/interfaces/rest/resources/AssignmentRequestResource.java`, replace the file with:

```java
package com.backendsems.experiments.interfaces.rest.resources;

/**
 * AssignmentRequestResource
 * Cuerpo de la petición para pedir/recuperar la variante de un sujeto en un experimento.
 * {@code deploymentEnv} es opcional: "production" o "test" para forzar la variante de ese
 * entorno de despliegue (ver ExperimentCommandServiceImpl); si se omite, se usa bucketing aleatorio.
 */
public record AssignmentRequestResource(String subjectId, String deploymentEnv) {
}
```

In `Backend/src/main/java/com/backendsems/experiments/interfaces/rest/ExperimentsController.java`, change the `assign` method:

```java
    @PostMapping("/{key}/assignment")
    @Operation(summary = "Assign (or retrieve) the variant for a subject in an experiment")
    public ResponseEntity<Map<String, String>> assign(@PathVariable String key,
                                                        @RequestBody AssignmentRequestResource request) {
        String variant = commandService.handle(new AssignVariantCommand(key, request.subjectId()));
        return ResponseEntity.ok(Map.of("experimentKey", key, "variant", variant));
    }
```
→
```java
    @PostMapping("/{key}/assignment")
    @Operation(summary = "Assign (or retrieve) the variant for a subject in an experiment")
    public ResponseEntity<Map<String, String>> assign(@PathVariable String key,
                                                        @RequestBody AssignmentRequestResource request) {
        String variant = commandService.handle(new AssignVariantCommand(key, request.subjectId(), request.deploymentEnv()));
        return ResponseEntity.ok(Map.of("experimentKey", key, "variant", variant));
    }
```

In `Backend/src/main/java/com/backendsems/SEMS/interfaces/rest/ReportsController.java`, add the query param to `compareConsumption` (around line 278-287):

```java
    @GetMapping("/compare")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Compare consumption", description = "Compare consumption between two periods")
    public ResponseEntity<?> compareConsumption(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End,
            @RequestParam(required = false) String format) {
```
→
```java
    @GetMapping("/compare")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Compare consumption", description = "Compare consumption between two periods")
    public ResponseEntity<?> compareConsumption(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period2End,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String deploymentEnv) {
```

(This is the param referenced by Step 2's `resolveRecommendations(userId, deploymentEnv)` call — if Step 2 was done first, this makes it compile.)

- [ ] **Step 9: Add the config properties**

Append to `Backend/src/main/resources/application.properties`:

```properties

# Experiment variant forcing per deployment environment (production/test).
# See docs/superpowers/specs/2026-07-05-branch-variant-deployment-design.md
experiments.force-variant.production.demo-onboarding=${EXPERIMENTS_FORCE_VARIANT_PRODUCTION_DEMO_ONBOARDING:}
experiments.force-variant.production.personalized-recommendations=${EXPERIMENTS_FORCE_VARIANT_PRODUCTION_RECOMMENDATIONS:}
experiments.force-variant.test.demo-onboarding=${EXPERIMENTS_FORCE_VARIANT_TEST_DEMO_ONBOARDING:}
experiments.force-variant.test.personalized-recommendations=${EXPERIMENTS_FORCE_VARIANT_TEST_RECOMMENDATIONS:}
```

- [ ] **Step 10: Run the full Backend test suite to check for regressions**

Run:
```bash
cd Backend
JAVA_HOME="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2" PATH="C:\Users\nanak\AppData\Local\mise\installs\java\25.0.2\bin:$PATH" ./mvnw -q -o test
```
Expected: no `FAILURE`/`ERROR` in the output (existing `NewsControllerTest`, `DevicesControllerTest`, etc. all still pass).

- [ ] **Step 11: Manual smoke test against the running local Backend**

The Backend is already running locally on port 8080 (started earlier this session). With no override configured (fresh `application.properties` defaults are all empty), confirm the endpoint still behaves exactly as before:

```bash
curl -s -X POST http://localhost:8080/api/v1/experiments/demo-onboarding/assignment \
  -H 'Content-Type: application/json' \
  -d '{"subjectId":"plan-smoke-test-1","deploymentEnv":"production"}'
```
Expected: `{"experimentKey":"demo-onboarding","variant":"A"}` or `"B"` (random — no override configured yet, so this just proves the new field doesn't break the request).

- [ ] **Step 12: Commit** (do this once develop is checked out — see Task 4)

**Files to stage:** all 6 modified files + the new test file from this task.

---

### Task 2: Frontend — send `deploymentEnv` with experiment requests

**Files:**
- Modify: `Frontend/src/environments/environments.ts`
- Modify: `Frontend/src/environments/environment.prod.ts`
- Modify: `Frontend/src/app/shared/infrastructure/services/experiment.service.ts`
- Modify: `Frontend/src/app/sems/energy-management/infrastructure/resources/report.resource.ts`

**Interfaces:**
- Consumes: `environment.deploymentEnv` (new field, both env files); Backend's `POST /api/v1/experiments/{key}/assignment` and `GET /api/v1/reports/compare` (both already accept the optional field/param from Task 1).
- Produces: no new exported symbols — this task only changes request payloads.

- [ ] **Step 1: Add `deploymentEnv` to both environment files**

`Frontend/src/environments/environments.ts` (full file):
```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  tokenKey: 'sems_token',
  refreshTokenKey: 'sems_refresh_token',
  userKey: 'sems_user',
  stripeapiurl: 'https://paypal-integration-plum.vercel.app',
  deploymentEnv: 'local',
};
```

`Frontend/src/environments/environment.prod.ts` (full file) — **this is the value for the `develop` branch; Task 4 flips it to `'production'` on `main` only**:
```ts
export const environment = {
  production: true,
  apiUrl: 'https://backend-latest-rplh.onrender.com',
  tokenKey: 'sems_token',
  refreshTokenKey: 'sems_refresh_token',
  userKey: 'sems_user',
  stripeapiurl: 'https://paypal-integration-plum.vercel.app',
  deploymentEnv: 'test',
};
```

- [ ] **Step 2: Send the tag from `ExperimentService.getVariant()`**

In `Frontend/src/app/shared/infrastructure/services/experiment.service.ts`, change:

```ts
    return this.http.post<{ experimentKey: string; variant: string }>(
      `${environment.apiUrl}/api/v1/experiments/${experimentKey}/assignment`,
      { subjectId: this.getVisitorId() }
    ).pipe(
```
→
```ts
    return this.http.post<{ experimentKey: string; variant: string }>(
      `${environment.apiUrl}/api/v1/experiments/${experimentKey}/assignment`,
      { subjectId: this.getVisitorId(), deploymentEnv: environment.deploymentEnv }
    ).pipe(
```

- [ ] **Step 3: Send the tag from `ReportResource.getCompare()`**

In `Frontend/src/app/sems/energy-management/infrastructure/resources/report.resource.ts`, change:

```ts
  getCompare(
    period1Start: string, period1End: string,
    period2Start: string, period2End: string
  ): Observable<CompareConsumptionResponse> {
    const params = new HttpParams()
      .set('period1Start', period1Start)
      .set('period1End', period1End)
      .set('period2Start', period2Start)
      .set('period2End', period2End);

    return this.http.get<CompareConsumptionResponse>(`${this.apiUrl}/compare`, {
      params,
      headers: this.getHeaders()
    });
  }
```
→
```ts
  getCompare(
    period1Start: string, period1End: string,
    period2Start: string, period2End: string
  ): Observable<CompareConsumptionResponse> {
    let params = new HttpParams()
      .set('period1Start', period1Start)
      .set('period1End', period1End)
      .set('period2Start', period2Start)
      .set('period2End', period2End);

    if (environment.deploymentEnv) {
      params = params.set('deploymentEnv', environment.deploymentEnv);
    }

    return this.http.get<CompareConsumptionResponse>(`${this.apiUrl}/compare`, {
      params,
      headers: this.getHeaders()
    });
  }
```

- [ ] **Step 4: Type-check/compile**

Run:
```bash
cd Frontend
npx tsc --noEmit -p tsconfig.app.json
```
Expected: no errors.

> Note: this codebase has no existing Jest/HTTP-mock test harness for either `ExperimentService` or `ReportResource` (no `.spec.ts` exists for them, and no file in the repo uses `HttpClientTestingModule`/`HttpTestingController`). Introducing that test infrastructure from scratch is out of scope for this change — verified manually instead (Step 5).

- [ ] **Step 5: Manual verification against the local Backend**

The Backend from Task 1 is already running on `localhost:8080`, and a demo user already exists (`demo.energix@energix-test.dev` / `Testing123!`, created earlier this session).

```bash
cd Frontend
npm start
```
Then in a browser:
1. Open devtools → Network tab.
2. Log in as the demo user, navigate to register/simulation or the "Impacto de mis recomendaciones" page.
3. Confirm the request to `/api/v1/experiments/demo-onboarding/assignment` has a JSON body containing `"deploymentEnv":"local"`.
4. Confirm the request to `/api/v1/reports/compare` includes `?...&deploymentEnv=local` in its URL.
5. Confirm both still return a valid variant (no override is configured for `"local"`, so behavior is unchanged from before this task).

- [ ] **Step 6: Commit** (do this once develop is checked out — see Task 4)

**Files to stage:** `environments.ts`, `environment.prod.ts`, `experiment.service.ts`, `report.resource.ts`.

---

### Task 3: Landing-Page — tag the demo-onboarding assignment call

**Files:**
- Modify: `Landing-Page/experiment.js`

**Interfaces:**
- Consumes: Backend's `POST /api/v1/experiments/demo-onboarding/assignment` (already accepts the optional field from Task 1).
- Produces: none (script only, no exports).

- [ ] **Step 1: Add the `DEPLOYMENT_ENV` constant**

In `Landing-Page/experiment.js`, after the existing constant declarations (after line 13, `var VARIANT_CACHE_KEY = ...;`), add:

```js
  // 'production' en la rama main, 'test' en develop — ver
  // Backend/docs/superpowers/specs/2026-07-05-branch-variant-deployment-design.md
  var DEPLOYMENT_ENV = 'test';
```

- [ ] **Step 2: Send it in the assignment request body**

Change:
```js
  fetch(API_BASE + '/api/v1/experiments/' + EXPERIMENT_KEY + '/assignment', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ subjectId: getVisitorId() })
  })
```
→
```js
  fetch(API_BASE + '/api/v1/experiments/' + EXPERIMENT_KEY + '/assignment', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ subjectId: getVisitorId(), deploymentEnv: DEPLOYMENT_ENV })
  })
```

- [ ] **Step 3: Syntax-check the file**

Run:
```bash
cd Landing-Page
node --check experiment.js
```
Expected: no output (valid syntax).

- [ ] **Step 4: Verify the request shape against the local Backend**

This file's `API_BASE` constant is hardcoded to `http://localhost:8080` (a pre-existing, out-of-scope staleness noted in the spec), so it already targets the same local Backend used in Tasks 1-2. With the Backend running:

```bash
curl -s -X POST http://localhost:8080/api/v1/experiments/demo-onboarding/assignment \
  -H 'Content-Type: application/json' \
  -d '{"subjectId":"landing-smoke-test-1","deploymentEnv":"test"}'
```
Expected: `{"experimentKey":"demo-onboarding","variant":"B"}` — wait, no override is configured yet (Task 4 only sets env vars in the real Render deployment, not locally), so expect either `"A"` or `"B"` at random. This step only confirms the request shape `experiment.js` now sends is accepted by the Backend.

- [ ] **Step 5: Commit** (do this once develop is checked out — see Task 4)

**Files to stage:** `experiment.js`.

---

### Task 4: Sync all three repos (`develop` → `main`) and apply the production flip

**Files:** none new — this task is git operations across the three repos using the commits prepared in Tasks 1-3.

**Interfaces:** none.

> **Known consequence, read before starting:** after this task, `main` and `develop` will be **permanently 1 commit apart** in the Frontend and Landing-Page repos (the "flip" commit only exists on `main`). Future `develop` → `main` syncs for those two repos can no longer use `git merge --ff-only` — they need a real `git merge`, which will conflict on exactly the `deploymentEnv`/`DEPLOYMENT_ENV` line each time; always keep `main`'s side (`'production'`) when resolving. The Backend repo is unaffected — its source is identical on both branches forever, so it stays fast-forwardable.

- [ ] **Step 1: Backend — commit, push, fast-forward `main`**

```bash
cd Backend
git checkout develop
git add src/main/java/com/backendsems/experiments src/main/java/com/backendsems/SEMS/interfaces/rest/ReportsController.java src/main/resources/application.properties src/test/java/com/backendsems/experiments
git commit -m "feat: force experiment variant per deployment environment

Adds an optional deploymentEnv tag (production|test) to experiment
assignment requests. When a matching override is configured via
experiments.force-variant.<env>.<experimentKey>, it's returned instead
of the random 50/50 bucketing. No override configured -> unchanged
behavior."
git push origin develop
git checkout main
git merge --ff-only develop
git push origin main
```

- [ ] **Step 2: Frontend — commit `develop` with the `'test'` tag, push, fast-forward `main`**

```bash
cd Frontend
git checkout develop
git add src/environments/environments.ts src/environments/environment.prod.ts src/app/shared/infrastructure/services/experiment.service.ts src/app/sems/energy-management/infrastructure/resources/report.resource.ts
git commit -m "feat: tag experiment requests with deploymentEnv

environment.prod.ts carries deploymentEnv: 'test' on this branch (the
test/beta deployment). See Backend's
docs/superpowers/specs/2026-07-05-branch-variant-deployment-design.md."
git push origin develop
git checkout main
git merge --ff-only develop
git push origin main
```

- [ ] **Step 3: Frontend — flip to `'production'` on `main` only**

```bash
cd Frontend
```
Edit `src/environments/environment.prod.ts`, changing only the last field:
```ts
  deploymentEnv: 'test',
```
→
```ts
  deploymentEnv: 'production',
```
Then:
```bash
git add src/environments/environment.prod.ts
git commit -m "chore: set deploymentEnv to 'production' for the main deployment"
git push origin main
```

- [ ] **Step 4: Landing-Page — commit `develop` with the `'test'` tag, push, fast-forward `main`**

```bash
cd Landing-Page
git checkout develop
git add experiment.js
git commit -m "feat: tag demo-onboarding assignment with DEPLOYMENT_ENV

experiment.js carries DEPLOYMENT_ENV = 'test' on this branch. See
Backend's docs/superpowers/specs/2026-07-05-branch-variant-deployment-design.md."
git push origin develop
git checkout main
git merge --ff-only develop
git push origin main
```

- [ ] **Step 5: Landing-Page — flip to `'production'` on `main` only**

```bash
cd Landing-Page
```
Edit `experiment.js`, changing only:
```js
  var DEPLOYMENT_ENV = 'test';
```
→
```js
  var DEPLOYMENT_ENV = 'production';
```
Then:
```bash
git add experiment.js
git commit -m "chore: set DEPLOYMENT_ENV to 'production' for the main deployment"
git push origin main
```

- [ ] **Step 6: Verify final branch state across all three repos**

```bash
for repo in Backend Frontend Landing-Page; do
  echo "=== $repo ==="
  (cd "C:/Users/nanak/Dev/Energix/$repo" && git status --porcelain && git log --oneline -1 main && git log --oneline -1 develop)
done
```
Expected: clean working trees everywhere; Backend's `main`/`develop` at the same commit; Frontend's and Landing-Page's `main` exactly one commit ahead of `develop`.

- [ ] **Step 7: Tell the user which env vars to set in Render**

Report back (not a git step — just a reminder to relay to the user, per the spec's "Configuración a aplicar en despliegue" section):

```
EXPERIMENTS_FORCE_VARIANT_PRODUCTION_DEMO_ONBOARDING=A
EXPERIMENTS_FORCE_VARIANT_PRODUCTION_RECOMMENDATIONS=control
EXPERIMENTS_FORCE_VARIANT_TEST_DEMO_ONBOARDING=B
EXPERIMENTS_FORCE_VARIANT_TEST_RECOMMENDATIONS=treatment
```
All four go on the **same** existing Render Backend service (it serves both the `main`-deployed and `develop`-deployed frontends).

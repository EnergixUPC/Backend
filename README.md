# Backend-SEMS - Estado de User Stories (Filtrado)

Este documento **filtra** las user stories `No implementada` y muestra solo las que estan en estado `Implementada` o `Parcial`.

## Criterio de filtrado

- **Implementada**: existe flujo backend funcional (endpoint + logica principal).
- **Parcial**: existe implementacion relacionada, pero faltan piezas clave para cerrar la historia.
- **No implementada**: **excluida** de este README.

## Resumen

- Total analizadas: **35**
- Implementadas: **6**
- Parciales: **8**
- Excluidas por filtro (`No implementada`): **21**

## User Stories implementadas

| ID | Titulo | Evidencia principal |
|---|---|---|
| US01 | Registro de cuenta | `src/main/java/com/backendsems/iam/interfaces/rest/AuthenticationController.java` (`POST /api/v1/authentication/sign-up`) |
| US02 | Inicio de sesion | `src/main/java/com/backendsems/iam/interfaces/rest/AuthenticationController.java` (`POST /api/v1/authentication/sign-in`) |
| US06 | Conectar dispositivos | `src/main/java/com/backendsems/SEMS/interfaces/rest/DevicesController.java` (`POST /api/v1/devices`) |
| US12 | Consultar reporte semanal de consumo | `src/main/java/com/backendsems/SEMS/interfaces/rest/ReportsController.java` (`GET /api/v1/reports/weekly-consumption`) |
| US14 | Proyeccion de factura mensual | `src/main/java/com/backendsems/SEMS/application/internal/queryservices/DashboardQueryServiceImpl.java` + `src/main/java/com/backendsems/SEMS/interfaces/rest/DashboardController.java` |
| US26 | Identificar dispositivos de alto consumo | `src/main/java/com/backendsems/SEMS/interfaces/rest/ReportsController.java` (`GET /api/v1/reports/top-devices`) |

## User Stories parciales

| ID | Titulo | Que existe | Falta principal |
|---|---|---|---|
| US03 | Configuracion de perfil inicial | Perfil basico + alta de dispositivos | Tipo de vivienda y flujo inicial completo |
| US08 | Monitorear consumo en tiempo real | Consulta de consumo por dispositivo | Tiempo real real (streaming/push/frecuencia alta) |
| US09 | Generar alertas de consumo elevado | Handler de alertas por consumo | Flujo completo disparado de forma consistente (event publishing pendiente) |
| US16 | Recomendaciones personalizadas de ahorro | Alertas/consejos en dashboard | Motor de recomendaciones personalizadas dedicado |
| US17 | Establecer metas de ahorro energetico | Uso de meta en dashboard | API explicita para definir y gestionar metas |
| US24 | Agrupar dispositivos por categorias | Campo `category` en dispositivo | Gestion formal de grupos/categorias por usuario |
| US25 | Consultar consumo por categoria | `categoryConsumption` en dashboard | Reporte dedicado por categoria y periodo |
| US28 | Definir roles de acceso en el hogar | Roles IAM globales | Roles por hogar (admin/invitado por hogar) |


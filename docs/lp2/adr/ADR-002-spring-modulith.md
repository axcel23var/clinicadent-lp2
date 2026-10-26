# ADR-002 - Verificación de módulos con Spring Modulith

## Estado

Aprobada.

## Contexto

La [ADR-001](ADR-001-arquitectura-backend.md) reemplazó el reactor Maven
multi-módulo por un único proyecto organizado en paquetes por módulo de
negocio (`catalogo`, `ventas`, `seguridad`, ...). Un límite de módulo
expresado solo como convención de paquete tiene el mismo problema que el
reactor Maven intentaba resolver: nada impide en tiempo de compilación que
`ventas` importe directamente `ProductoRepository` de `catalogo` en lugar de
usar su `ProductoService` público.

## Decisión

Usar [Spring Modulith](https://docs.spring.io/spring-modulith/reference/index.html)
para verificar mecánicamente los límites entre módulos, en vez de confiar
solo en la disciplina de quien programa o en un reactor Maven.

- Cada paquete directo bajo `pe.edu.upeu.bomerp` (`catalogo`, `ventas`,
  `seguridad`, ...) es un módulo de aplicación Modulith.
- `ModularityTests` (`lp2/bomerp-backend/src/test/java/pe/edu/upeu/bomerp/ModularityTests.java`)
  ejecuta `ApplicationModules.of(BomErpApplication.class).verify()`: falla el
  build si un módulo accede a paquetes internos de otro que no fueron
  expuestos explícitamente.
- Los módulos siguen comunicándose mediante servicios Java públicos (como ya
  definía la ADR-001), no mediante eventos de aplicación — Modulith no obliga
  a un modelo basado en eventos, solo lo habilita si en el futuro (p. ej.
  auditoría en U3) conviene desacoplar así.
- Dependencia agregada: `spring-modulith-starter-core` (compile) y
  `spring-modulith-starter-test` (test), gestionadas por
  `spring-modulith-bom` en `dependencyManagement` (versión **2.0.7**,
  compatible con Spring Boot 4.0.x — ver [ADR-003](ADR-003-spring-boot-4.md)
  para por qué 2.0.7 y no la última versión publicada, 2.1.0).
- Al generar el proyecto desde Spring Initializr con Spring Web + Spring
  Data JPA + Actuator + Spring Modulith seleccionados a la vez (3.2.1 de
  S1), el asistente agrega automáticamente, además de
  `spring-modulith-starter-core`: `spring-modulith-starter-jpa` (compile) y,
  en runtime, `spring-modulith-actuator` y `spring-modulith-observability`.
  De estas tres, **se conservan `spring-modulith-actuator` y
  `spring-modulith-observability`** (exponen información de módulos vía
  Actuator y trazas de observabilidad; no requieren ninguna tabla nueva) y
  **se elimina `spring-modulith-starter-jpa`** del `pom.xml` después de
  generar el proyecto — ver más abajo por qué, a diferencia de las otras
  dos, esta sí se remueve.

### Por qué se elimina `spring-modulith-starter-jpa`

Esta dependencia trae transitivamente `spring-modulith-events-jpa`, el
**registro de eventos de Modulith respaldado por JPA**: una tabla propia
(`event_publication`) donde Modulith persiste cada evento de aplicación
publicado entre módulos, para garantizar entrega al menos una vez. Como
esta ADR ya establece que los módulos se comunican mediante servicios Java
públicos, **no eventos de aplicación**, esa tabla no tiene ningún uso hoy.

El problema no es solo que sobre: con `ddl-auto: validate` (ver S1, 3.2.3),
Hibernate valida el esquema completo al arrancar y **falla el arranque**
con `SchemaManagementException: Schema validation: missing table
[event_publication]`, porque la tabla no existe en Oracle y no hay ninguna
sesión de BD2 que la cree. Se comprobó agregando la dependencia contra la
Oracle real de `compose-dev.yml`: el `SessionFactory` no llega a
construirse y la aplicación no levanta.

La corrección no es crear esa tabla a mano — eso adelantaría alcance de
comunicación por eventos que ni ADR-002 ni el sílabo piden todavía. La
corrección es remover `spring-modulith-starter-jpa`: sin ella, Modulith
sigue verificando límites de módulo (`spring-modulith-starter-core`, el
propósito real de esta ADR) sin exigir ninguna tabla adicional.

> **Pendiente explícito (no perder de vista):** cuando una sesión futura
> adopte eventos de aplicación entre módulos (candidato: S14,
> auditoría/integración — ver `lp2/CLAUDE.md`, sección "Arquitectura del
> backend"), hay que **reincorporar `spring-modulith-starter-jpa`** al
> `pom.xml` junto con la migración de BD2 que cree la tabla
> `event_publication`. No antes: agregarla hoy vuelve a romper el arranque
> de la app, como ya se comprobó.

## Alternativas consideradas

| Alternativa | Por qué se descarta |
|---|---|
| Solo convención de paquetes, sin herramienta de verificación | Nada impide que un módulo importe el repositorio interno de otro; el límite existe únicamente en la documentación. |
| Volver al reactor Maven multi-módulo (ADR-001) | Sí impediría el acceso entre módulos que no declaren la dependencia Maven, pero solo a nivel de artefacto completo (todo público queda expuesto) y con el costo de build ya descartado en la ADR-001. |
| ArchUnit con reglas propias | Requiere escribir y mantener las reglas de dependencia a mano; Spring Modulith ya las infiere de la estructura de paquetes y además genera documentación de módulos. |

## Consecuencias

- Una dependencia más en el `pom.xml`, sin reactor ni módulos Maven
  adicionales.
- `ModularityTests` sirve como evidencia técnica verificable para la
  sustentación de cada sesión (un test que falla si se violan los límites
  de módulo).
- Al agregar un módulo nuevo (`ventas` en S4, `seguridad` en S10) basta con
  crear el paquete; Modulith lo detecta automáticamente en la siguiente
  ejecución de `ModularityTests`.

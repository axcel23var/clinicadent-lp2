# ADR-003 - Versión exacta de Spring Boot: 4.0.7

## Estado

Aprobada.

## Contexto

Al verificar el paso de creación de proyecto con Spring Initializr (3.2.1)
contra el servicio real (`start.spring.io`), se confirmó que **el generador
no ofrece ninguna versión 3.x**: las únicas opciones son `4.0.7`, `4.0.8`
(SNAPSHOT), `4.1.0` y `4.1.1` (SNAPSHOT). Spring Boot 3.5.x sigue funcionando
perfectamente vía Maven/Maven Central, pero el asistente de creación de
proyectos ya no lo lista, así que cualquiera que siga la sesión S1 genera el
proyecto en alguna versión 4.x.

Dentro de la línea 4.x, SpringDoc OpenAPI declara en `start.spring.io` un
rango de compatibilidad `[4.0.0.RELEASE, 4.1.0.M1)`: al seleccionar Boot
`4.1.0`, el propio buscador de dependencias muestra en rojo, sobre "SpringDoc
OpenAPI", el mensaje *"Requires Spring Boot >= 4.0.0 and < 4.1.0-M1"*. Es una
restricción de compatibilidad real declarada por el propio proyecto
SpringDoc, no una limitación de qué tan grande es la lista de dependencias
que ofrece el asistente. Por eso el proyecto fija Boot en **4.0.7**, dentro
del rango compatible.

Comparando el `pom.xml` real que genera Initializr (extensión de VS Code,
proyecto `lp2/bomerp-backend`) contra Maven Central, se confirman además
estos detalles exactos para este patch de Boot:

- `spring-boot-starter-web` **está deprecado en Boot 4.0.7** a favor de
  `spring-boot-starter-webmvc` (así lo declara textualmente su propio
  `.pom`: *"deprecated in favor of spring-boot-starter-webmvc"*). Initializr
  ya genera `-webmvc` directamente.
- `springdoc-openapi-starter-webmvc-ui` resuelve en **3.0.2** para este
  patch exacto de Boot — el rango de compatibilidad amplio
  (`[4.0.0.RELEASE, 4.1.0.M1)`) no fija la versión exacta de SpringDoc, solo
  qué línea de Boot acepta en general.
- `spring-modulith-bom` resuelve en **2.0.7** para este patch (ver
  [ADR-002](ADR-002-spring-modulith.md)) — 2.1.0 es la última versión
  publicada y su rango de compatibilidad (`[4.0.0.RELEASE, 4.2.0.M1)`) sí
  cubre Boot 4.0.7, pero Initializr resuelve 2.0.7 como la versión probada
  para este patch exacto.
- Los starters de prueba son **granulares por starter de producción**
  (`spring-boot-starter-actuator-test`, `-data-jpa-test`,
  `-validation-test`, `-webmvc-test`) en vez del histórico
  `spring-boot-starter-test` único — este último sigue existiendo en Maven
  Central, pero Initializr ya no lo genera. `spring-modulith-starter-test`
  sí se agrega automáticamente al seleccionar "Spring Modulith".

## Decisión

Fijar `lp2/bomerp-backend` en **Spring Boot 4.0.7**, con:

- `spring-boot-starter-webmvc` (`spring-boot-starter-web` queda deprecado en
  Boot 4.0.7 a favor de este).
- `spring-modulith-bom` en **2.0.7** (línea compatible con Boot 4.x, versión
  exacta que resuelve Initializr para este patch — ver ADR-002).
- `springdoc-openapi-starter-webmvc-ui` en **3.0.2** (verificada contra el
  `pom.xml` real generado por Initializr y contra `maven-metadata.xml` de
  Maven Central), dentro del rango de compatibilidad
  `[4.0.0.RELEASE, 4.1.0.M1)` declarado por SpringDoc.
- Starters de prueba: en vez de un único `spring-boot-starter-test`,
  Initializr genera uno granular por cada starter de producción
  seleccionado (`-actuator-test`, `-data-jpa-test`, `-validation-test`,
  `-webmvc-test`), más `spring-modulith-starter-test` (agregado
  automáticamente, no a mano).
- Java se mantiene en **21** (Boot 4.0 solo eleva el mínimo a Java 17; no
  obliga a subir de versión).

Verificado con `mvnw clean test`: build limpio, sin errores ni warnings,
`ModularityTests` en verde. El código de S1 (entidades JPA, DTO como
`record`, Lombok, controladores REST simples) no usa ninguna API afectada
por el salto a Jakarta EE 11 / Jackson 3 que trae Spring Framework 7.

## Alternativas consideradas

| Alternativa | Por qué se descarta |
|---|---|
| Quedarse en Boot 3.5.x y bajar la versión a mano después de generar con el Initializr | Cualquier estudiante nuevo generaría 4.1.0 o 4.0.7 por defecto y tendría que editar el `pom.xml` a mano para forzar 3.5.12 — paso confuso de explicar y con riesgo de dependencias iniciales que no bajen limpio a esa línea. |
| Usar Spring Boot 4.1.x | SpringDoc OpenAPI declara explícitamente en `start.spring.io` que requiere `>= 4.0.0 y < 4.1.0-M1`. Con 4.1.x no solo no aparece como seleccionable: no es la versión que el propio proyecto SpringDoc soporta todavía. |
| Esperar a que 3.x reaparezca en el generador | El generador de Spring Initializr solo mantiene generables las líneas activas más recientes; no hay indicio de que 3.5.x vuelva a listarse. El curso empieza en pocos días, no es una espera razonable. |

## Consecuencias

- El código, los tests y `compose-dev.yml` de S1 no requieren ningún
  ajuste por esta versión: solo se fijan las versiones exactas en el
  `pom.xml`.
- Las sesiones futuras (S2 en adelante) parten de Spring Boot 4.0.7.
- Cuando SpringDoc publique una versión compatible con Boot 4.1.x, esta
  ADR debe revisarse; hasta entonces, `4.0.7` es la versión soportada.
- Si en una sesión posterior aparece una API removida o cambiada por
  Jakarta EE 11 / Jackson 3 (por ejemplo en Seguridad, S10), se documenta
  puntualmente en la sesión correspondiente, no aquí.

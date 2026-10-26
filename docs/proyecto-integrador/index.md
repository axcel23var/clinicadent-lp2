# Guía del Proyecto Integrador del Ciclo 4

## 1. Propósito

El Proyecto Integrador del Ciclo 4 articula **Análisis y Diseño de Sistemas de Información (ADS)**, **Administración de Base de Datos II (BD2)** y **Lenguaje de Programación II (LP2)** alrededor de un mismo sistema empresarial.

```text
Diseño técnico -> Base Oracle administrada -> Backend REST -> Frontend SPA -> Integración -> Sustentación
```

ADS define el diseño técnico profesional. BD2 administra y fortalece la base Oracle que soporta el sistema. LP2 implementa la aplicación full-stack empresarial usando el diseño técnico y la base de datos operativa.

### Composición de equipos e integración

No todos los integrantes de un equipo llevan necesariamente los 3 cursos (hay estudiantes irregulares que solo cursan ADS, solo BD2, solo LP2, o 2 de los 3). Por eso:

- **Cada sesión de cada curso se evalúa de forma autónoma**, sin depender de que el estudiante lleve los otros dos cursos en paralelo — la integración entre ADS, BD2 y LP2 nunca es requisito para aprobar la rúbrica de una sesión individual.
- **La integración se evalúa únicamente a nivel del Proyecto Integrador, en la sustentación de Unidad 3** ([Guía de Sustentación Final](u3/guia-sustentacion.md), pregunta 6 de la defensa individual: "cómo su trabajo se conecta con los otros cursos"; ver también el [Checklist final](u3/checklist-final.md)), y se aplica sobre el **equipo como conjunto**, no sobre cada estudiante individual: si el equipo tiene integrantes en los 3 cursos, esa evidencia de integración existe naturalmente porque cada uno construyó su parte; si el equipo no cubre los 3 cursos, se evalúa sobre lo que el equipo sí integró (por ejemplo, solo ADS+LP2, o solo BD2), sin penalizar la ausencia de un curso que ningún integrante lleva.
- La sustentación (Unidad 3, [Guía de Sustentación Final](u3/guia-sustentacion.md)) se reparte igual: cada integrante defiende la parte que efectivamente construyó, según los cursos que lleva.

### Competencia o capacidad del proyecto

Al finalizar el Proyecto Integrador, el equipo demuestra que puede diseñar, implementar, operar y defender una aplicación full-stack empresarial, integrando arquitectura técnica, base Oracle administrada, backend REST, frontend SPA, seguridad, operación, validación, documentación y sustentación integral del producto.

### Competencias relacionadas

| Código | Curso asociado | Competencia | Relación con el proyecto |
|---|---|---|---|
| CE021 | ADS | Ingeniería de Requerimientos | Evidencia diseño técnico, arquitectura, UML, decisiones y trazabilidad. |
| CE022 | BD2 | Ingeniería de la Información | Evidencia base Oracle administrada, segura, optimizada, auditada y resiliente. |
| CE023 | LP2 | Programación | Evidencia backend REST, frontend SPA e integración full-stack empresarial. |
| CE024 | Transversal | Calidad de Software | Evidencia pruebas, operación, documentación, repositorio, estándares, reproducibilidad y sustentación integral. |

Fuente oficial de los códigos: [Transcripción de evidencias por competencia — Ingeniería de Software](https://upeuoficial.github.io/planb/transcripcion/#c-area-de-ingenieria-de-software).

## 2. El Proyecto

El producto integrador es una **Aplicación Full-Stack Empresarial con Diseño Técnico Profesional y Base de Datos Oracle Administrada**.

El caso de referencia continúa el producto final de Ciclo 3: `Producto`, `Categoria`, `Venta`, `DetalleVenta` y `Usuario`. Ciclo 4 no reinicia el dominio; transforma el sistema Web MVC en una base Full-Stack modular de BomERP con una aplicación Spring Boot, una SPA, seguridad JWT, operación Oracle y preparación académica para producción. No pretende cubrir todos los procesos de un ERP comercial. Catálogo y Ventas forman el flujo funcional obligatorio; Inventario y Compras se delimitan como módulos de evolución sin ampliar artificialmente la evaluación.

BD2 no se integra como curso de diseño inicial de base de datos. Ese rol se trabajó en BD1. En ciclo 4, BD2 se integra como administración empresarial de la base que sostiene la aplicación: operación, rendimiento, seguridad, auditoría, respaldo, recuperación y monitoreo.

No se considera proyecto integrador:

- Diseño técnico que no se refleja en la aplicación.
- Base Oracle trabajada como ejercicios aislados.
- Backend y frontend sin trazabilidad con ADS.
- Evidencias de BD2 que no correspondan al sistema empresarial.
- Tres productos separados sin integración técnica.

## 3. Evolución del Proyecto

| Curso | Aporte principal | Producto |
|---|---|---|
| ADS | Arquitectura, vistas, modelo de dominio, UML, patrones, APIs, integraciones, ADRs y trazabilidad. | Diseño Técnico Profesional Documentado. |
| BD2 | PL/SQL, administración Oracle, seguridad, auditoría, optimización, backup, recovery y monitoreo. | Base Oracle operativa, administrada, optimizada, auditada y resiliente. |
| LP2 | Una aplicación Spring Boot única organizada en módulos verificados con Spring Modulith, una SPA modular, seguridad, persistencia, optimización, monitoreo, pruebas y estabilización. | Base Full-Stack modular de BomERP integrada, optimizada, monitoreada y estabilizada. |

```mermaid
flowchart TB
    A[ADS: arquitectura y diseño técnico] --> B[BD2: base Oracle administrada]
    A --> C[LP2: una aplicación Spring Boot modular]
    B --> C
    C --> D[LP2: frontend SPA seguro]
    D --> E[Aplicación full-stack integrada]
    B --> E
    A --> E
    E --> F[Proyecto integrador sustentado]
```

### Alineamiento por sesiones

Este alineamiento sirve como referencia metodológica para coordinar los avances de los tres cursos sin convertir el documento principal en una lista extensa de sesiones.

| Sesiones | ADS | BD2 | LP2 | Integración esperada |
|---|---|---|---|---|
| S1-S2 | Fundamentos de arquitectura, stakeholders, atributos de calidad, modelo C4 y vistas arquitectónicas. | PL/SQL aplicado al negocio y triggers DML con auditoría básica. | Un Spring Boot ejecutable, módulos delimitados, conexión, verificación, DTO, OpenAPI y CRUD REST de `Producto`. | El diseño de ADS orienta el monolito modular; BD2 soporta la persistencia y LP2 expone por REST el primer recurso heredado de Ciclo 3. |
| S3-S4 | Diseño estructural, principios SOLID y arquitecturas modernas. | Excepciones, robustez y optimización de consultas con CBO, Explain Plan y DBMS_STATS. | Asociación `Categoria–Producto` y operación transaccional `Venta–DetalleVenta`. | Las decisiones de diseño se convierten en objetos relacionados y en el proceso transaccional de la base de BomERP. |
| S5-S6 | Evaluación U1 de arquitectura documentada. | Índices y evaluación U1 del motor transaccional Oracle optimizado. | Consultas y reportes REST, reglas, trazabilidad, CORS y evaluación del backend. | Primer corte integrado: arquitectura, motor transaccional y backend preparado para el consumo frontend. |
| S7-S8 | Modelado del dominio y diseño de clases. | Arquitectura Oracle, instancia, usuarios, roles y privilegios. | Una SPA con `core`, `shared`, módulos funcionales, layout, menú, rutas, servicios y CRUD. | La SPA modular implementa el diseño y consume recursos reales sobre la base administrada. |
| S9-S10 | Diagramas dinámicos y patrones de diseño (GoF y GRASP). | Almacenamiento, auditoría, optimización de rendimiento, AWR e índices. | Formularios cabecera-detalle, consultas/reportes y seguridad backend con JWT, roles y permisos. | Los flujos empresariales se implementan y se protegen en el backend. |
| S11-S12 | Integración empresarial y evaluación U2 del catálogo UML con patrones e integración. | Particionamiento, escalabilidad y evaluación U2 de la base administrada, optimizada y asegurada. | Seguridad frontend con guards e interceptores y evaluación de la SPA segura integrada. | Segundo corte integrado: diseño de integración, base administrada y aplicación full-stack segura y funcional. |
| S13-S15 | Integración del diseño técnico, ADRs, trazabilidad y sustentación. | Backup, recovery, monitoreo, diagnóstico y sustentación del producto final BD2. | Lazy Loading, Code Splitting, caché, Redis cuando corresponda, logging, monitoreo, paginación de alto volumen, auditoría, pruebas end-to-end, estabilización y sustentación. | Consolidación final de la base de BomERP con evidencias técnicas de diseño, datos, aplicación y operación. |
| S16 | Evaluación final. | Evaluación final individual. | Evaluación final individual teórico-práctica. | Cierre académico y verificación individual de competencias. |

## 4. Cronograma

| Hito | Momento | Producto esperado |
|---|---|---|
| S2 | Brief técnico | Dominio, arquitectura inicial, recursos REST, objetos Oracle iniciales y alcance full-stack. |
| S6 | Primer corte integrado | Arquitectura base, motor transaccional Oracle optimizado y backend REST preparado para la SPA. |
| S12 | Producto intermedio | Catálogo UML, base Oracle administrada y SPA segura integrada al backend. |
| S15 | Producto final | Diseño técnico, base Oracle resiliente y base Full-Stack de BomERP optimizada, monitoreada y sustentada. |
| S16 | Cierre individual | Evaluación final individual teórico-práctica, separada de la sustentación del producto. |

### Entregables de Unidad 1

La Unidad 1 debe cerrar con un backend empresarial funcional y preparado para la SPA, sustentado por arquitectura y motor transaccional Oracle. La autenticación JWT se incorpora en U2; no es requisito de este primer corte.

Los artefactos desarrollados como ejemplo base se encuentran en [Unidad 1 - Producto integrado](u1/index.md).

| Curso | Producto U1 del curso | Artefactos mínimos | Evidencia de integración |
|---|---|---|---|
| ADS | **Arquitectura documentada mediante vistas arquitectónicas y principios de diseño aplicados.** | Contexto técnico, atributos de calidad, C4, componentes, principios SOLID y ADRs iniciales. | La arquitectura define endpoints, componentes y decisiones que LP2 implementa y BD2 soporta. |
| BD2 | **Motor transaccional Oracle optimizado.** | Tablas, paquetes PL/SQL, triggers, excepciones, auditoría básica, consultas optimizadas e índices. | Los objetos Oracle sostienen reglas, transacciones y consultas del backend. |
| LP2 | **Aplicación Spring Boot REST modular para Categoria–Producto y Venta–DetalleVenta.** | Un ejecutable, módulos delimitados, DTO, OpenAPI, CRUD maestro, proceso cabecera–detalle, consultas, CORS, logs, pruebas y demo API. | La API conserva el dominio de Ciclo 3, respeta límites ADS, consume estructuras Oracle alineadas con BD2 y queda preparada para una SPA. |

### Entregables de Unidad 2

La Unidad 2 debe cerrar con una aplicación full-stack funcional, donde el diseño UML, la administración Oracle y la SPA segura ya se evidencian como un solo sistema empresarial.

Los artefactos desarrollados como ejemplo base se encuentran en [Unidad 2 - Producto integrado](u2/index.md).

| Curso | Producto U2 del curso | Artefactos mínimos | Evidencia de integración |
|---|---|---|---|
| ADS | **Catálogo UML con patrones de diseño e integración aplicados.** | Modelo de dominio, clases, secuencia, actividad, patrones y diseño de integración. | Los modelos orientan servicios, DTO, endpoints, tablas y flujos SPA. |
| BD2 | **Base de datos empresarial administrada, optimizada y asegurada.** | Usuarios, roles, privilegios, almacenamiento, auditoría, rendimiento, particionamiento y scripts. | Oracle sostiene seguridad, rendimiento y operación de la aplicación. |
| LP2 | **Una SPA empresarial modular y segura, conectada a la aplicación Spring Boot.** | `core`, `shared`, módulos funcionales, layout, menú, rutas, CRUD, cabecera–detalle, consultas, JWT, roles, guards e interceptores. | La SPA única consume endpoints y evidencia flujos funcionales y protegidos con datos consistentes. |

### Entregables de Unidad 3

La Unidad 3 cierra el producto final del ciclo. No repite la funcionalidad de U2; agrega optimización, caché, observabilidad, paginación de alto volumen, auditoría, pruebas end-to-end, resiliencia, estabilización y defensa técnica del producto.

Los artefactos desarrollados como ejemplo base se encuentran en [Unidad 3 - Producto integrado](u3/index.md).

| Curso | Producto U3 del curso | Artefactos mínimos | Evidencia de integración |
|---|---|---|---|
| ADS | **Diseño Técnico Profesional Documentado.** | Arquitectura final, UML, patrones, ADRs y matriz de trazabilidad. | El diseño explica y justifica la aplicación, la base Oracle y las decisiones finales. |
| BD2 | **Base Oracle operativa, administrada, optimizada, auditada y resiliente.** | Backup, recovery, monitoreo, diagnóstico, seguridad, auditoría y rendimiento. | Oracle se muestra como soporte operable y recuperable del sistema. |
| LP2 | **Base Full-Stack modular de BomERP integrada, optimizada, monitoreada y estabilizada.** | Una SPA, una aplicación Spring Boot única con módulos verificados por Spring Modulith, esquemas funcionales Oracle, optimización, caché, logging, monitoreo, paginación, auditoría, E2E y guía de ejecución. | La aplicación ejecuta el flujo final respetando límites modulares (verificados con `ModularityTests`) y se sustenta en S15. |

## 5. Repositorio académico y topics

Desde la primera presentación del proyecto, el repositorio debe estar creado y configurado con los topics académicos mínimos. Esta configuración es obligatoria porque permite identificar campus, semestre, línea, tipo de proyecto, cursos participantes, sección y grupo.

El detalle oficial del estándar se encuentra en [Estándar transversal de topics para repositorios académicos](https://upeuoficial.github.io/planb/anexos/estandar-topics-repositorios/).

Ejemplo base para el Proyecto Integrador del Ciclo 4:

```text
campus-juliaca
semestre-2026-2
linea-software
tipo-pi
ads
bd2
lp2
seccion-g1
grupo-<numero>-<nombre-proyecto>
```

## 6. Producto y evaluación por unidad

La evaluación por competencias de cada curso vive en la rúbrica de su propio producto de unidad (columna `CE / Nivel`, etiqueta `CG` para la Competencia General), no en una matriz genérica del PI — evaluar así entregables de proyecto en vez de las competencias reales de cada curso fue el problema del esquema anterior de esta sección. La sustentación final se conduce con la [Guía de Sustentación Final](u3/guia-sustentacion.md), que incluye los 7 subaspectos de la sustentación integral exigibles desde Unidad 1.

**Tabla. Producto y evaluación por curso y unidad**

| Curso | Unidad 1 (con rúbrica) | Unidad 2 | Unidad 3 | Matriz de competencias |
|---|---|---|---|---|
| ADS | [ads-producto.md](u1/ads-producto.md) | [ads-producto.md](u2/ads-producto.md) | [ads-producto.md](u3/ads-producto.md) | [ads-matriz-competencias.md](u1/ads-matriz-competencias.md) |
| BD2 | [bd2-producto.md](u1/bd2-producto.md) | [bd2-producto.md](u2/bd2-producto.md) | [bd2-producto.md](u3/bd2-producto.md) | [bd2-matriz-competencias.md](u1/bd2-matriz-competencias.md) |
| LP2 | [lp2-demo.md](u1/lp2-demo.md) | [lp2-demo.md](u2/lp2-demo.md) | [lp2-producto.md](u3/lp2-producto.md) | [lp2-matriz-competencias.md](u1/lp2-matriz-competencias.md) |

Nota: solo Unidad 1 de los tres cursos tiene cerrada la columna `CE/Nivel` y su matriz de competencias; las rúbricas de Unidad 2 y Unidad 3 quedan pendientes.

## 7. Resultado Esperado

Al cierre del ciclo, el estudiante debe demostrar que puede convertir un diseño técnico profesional en una solución full-stack empresarial operativa.

```text
Diseño técnico -> Oracle administrado -> Backend REST -> Frontend SPA -> Sistema empresarial -> Sustentación
```

El valor del proyecto integrador está en evidenciar que la arquitectura, la base Oracle y la aplicación full-stack pertenecen al mismo sistema y evolucionaron de manera coordinada.









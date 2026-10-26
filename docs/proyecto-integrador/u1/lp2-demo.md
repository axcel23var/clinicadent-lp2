# LP2 - Producto de Unidad 1

**Esta es la plantilla-ejemplo del producto de Unidad 1 de LP2.** La estructura (alcance arquitectónico, contrato REST, DTO principales, arquitectura backend, casos de prueba y trazabilidad con ADS/BD2) es exigible a todos: monolito modular verificado, un módulo transaccional con cabecera-detalle real, persistencia, consultas, CORS, logs y pruebas. El contenido de BomERP (`catalogo`/`ventas`, sus endpoints y DTO concretos) es el ejemplo real que muestra cómo se ve terminada — cada sede (Lima, Juliaca, Tarapoto) y cada grupo reemplaza ese contenido por sus propios módulos transaccional y no transaccional, definidos en su propio [brief.md](../brief.md) de S2, sin cambiar la estructura.

## Producto

**Backend REST modular ensamblado como una sola aplicación Spring Boot, conectado a Oracle, con persistencia ORM, CRUD, una operación cabecera–detalle, consultas, CORS, logs y pruebas.**

La demo representa una consola académica de API REST y muestra el flujo obligatorio `Categoria–Producto–Venta–DetalleVenta`, heredado del sistema MVC de Ciclo 3. El backend real se organiza como monolito modular: `catalogo` y `ventas` quedan funcionales en U1; `inventario`, `compras` y `seguridad` conservan límites preparados para su evolución. Para publicarse en MkDocs sin servidor, la demo simula servicios, repositorios y persistencia en el navegador; no reemplaza la aplicación Spring Boot conectada a Oracle. La autenticación JWT no forma parte de U1 y se incorpora en S10.

## 1. Alcance arquitectónico del corte

```text
backend/                     # un solo proyecto Maven, sin reactor multi-módulo
└── src/main/java/pe/edu/upeu/bomerp/
    ├── BomErpApplication.java   # único Spring Boot ejecutable
    ├── catalogo/                # funcional en U1
    ├── ventas/                  # funcional en U1
    ├── inventario/              # aún no existe, se agrega cuando su sesión le dé contenido
    ├── compras/                 # aún no existe, se agrega cuando su sesión le dé contenido
    └── seguridad/               # se implementa en U2
```

Cada paquete directo bajo `pe.edu.upeu.bomerp` es un módulo de aplicación con sus límites verificados automáticamente por Spring Modulith, no un artefacto Maven separado.

Compras no es un segundo flujo obligatorio de U1. Su límite se documenta para evitar que el código de compras termine mezclado con ventas, pero la evaluación mantiene una sola operación cabecera–detalle implementada con profundidad.

## 2. Demo ejecutable

[Abrir consola API U1](demo-api/index.html)

## 3. Contrato REST de referencia

**Tabla 1. Contrato REST de referencia**

| Método | Endpoint | Propósito | Sesión relacionada |
|---|---|---|---|
| `GET` | `/api/v1/categorias` | Listar categorías. | S1-S3 |
| `GET` | `/api/v1/productos` | Listar productos con categoría. | S1-S3 |
| `POST` | `/api/v1/productos` | Registrar un producto. | S2 |
| `POST` | `/api/v1/ventas` | Registrar cabecera y colección de detalles. | S4 |
| `GET` | `/api/v1/ventas` | Consultar ventas mediante filtros y ordenamiento. | S5 |
| `GET` | `/api/v1/ventas/resumen` | Devolver agregaciones y respuestas resumidas. | S5 |

## 4. DTO principales

```json
{
  "detalles": [
    { "productoId": 1, "cantidad": 2 },
    { "productoId": 2, "cantidad": 3 }
  ]
}
```

```json
{
  "id": 1001,
  "fecha": "2026-09-01T10:15:00",
  "estado": "REGISTRADA",
  "total": 145.50,
  "detalles": [
    { "productoId": 1, "nombreProducto": "Producto de prueba", "precioUnitario": 50.00, "cantidad": 2, "subtotal": 100.00 }
  ]
}
```

## 5. Arquitectura backend U1

**Figura 1. Arquitectura backend U1**

```mermaid
flowchart LR
    APP[BomErpApplication<br/>único ejecutable]
    CAT[catalogo<br/>Categoria–Producto]
    VEN[ventas<br/>Venta–DetalleVenta]
    FUT["inventario, compras, seguridad<br/>(aún no creados como paquetes)"]

    SCAT[(BOM_CATALOGO)]
    SVEN[(BOM_VENTAS)]

    APP --> CAT
    APP --> VEN
    APP -. se agregan cuando su sesión les da contenido .-> FUT
    VEN -->|servicio público| CAT
    CAT --> SCAT
    VEN --> SVEN
```

Todos los módulos se ejecutan en la misma JVM y utilizan un datasource. No existe Feign ni comunicación HTTP interna. Cada módulo conserva sus controllers, casos de uso, entidades y repositorios; los repositorios no se comparten.

## 6. Casos de prueba de la demo

**Tabla 2. Casos de prueba de la demo**

| Caso | Accion | Resultado esperado |
|---|---|---|
| Verificar backend | Ejecutar con el ambiente local. | Conecta con Oracle y responde en `/health` o equivalente. |
| Verificar límites | Revisar dependencias y paquetes de negocio. | Existe un solo ejecutable y ningún módulo accede a repositorios ajenos. |
| CRUD maestro | Crear, consultar, actualizar y eliminar un producto. | Las operaciones persisten con respuestas HTTP consistentes. |
| Crear venta válida | Dos detalles válidos. | Se registra venta `REGISTRADA` con total y stock consistentes. |
| Crear venta inválida | Cantidad cero o stock insuficiente. | Se devuelve error `400`/`409` sin persistencia parcial (rollback completo). |
| Filtrar ventas | Filtrar por estado y rango de fecha. | La lista muestra solo las ventas coincidentes. |

## 7. Trazabilidad con ADS y BD2

**Tabla 3. Trazabilidad con ADS y BD2**

| Elemento LP2 | ADS | BD2 |
|---|---|---|
| Configuración por ambientes | Un ejecutable desplegable y configuración externa | Conexión Oracle sin credenciales versionadas. |
| Monolito modular con capas internas | Vista C3, límites y dependencias | Esquemas y tablas con propiedad funcional definida. |
| Validación de total y stock | Regla de integridad | Restricciones y excepciones PL/SQL. |
| Filtros por fecha | Atributo rendimiento | Índice `IX_VENTAS_FECHA`. |

## 8. Rúbrica de Evaluación

**Tabla 4. Rúbrica de evaluación de la Unidad 1**

| Criterio | Peso | CE / Nivel | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Calificación obtenida |
|---|---:|---|---|---|---|---|---:|
| 1. Crea y configura el proyecto backend con ORM, conexión a la base de datos, recurso REST inicial, DTO y documentación de API | 16% | CE023-N2 (parcial) | Proyecto ejecutable, conectado a Oracle, con contrato y versionado de API documentados y verificables en vivo. | Proyecto ejecutable y conectado, con documentación parcial. | Proyecto ejecutable con conexión o documentación incompleta. | No presenta un proyecto backend ejecutable. | |
| 2. Implementa un CRUD REST completo, con validaciones, excepciones, logs y pruebas transversales | 16% | CE023-N2 (parcial) | CRUD completo con validación, manejo de errores y trazabilidad probados con casos reales. | CRUD completo con validación parcial o trazabilidad incompleta. | CRUD incompleto o sin manejo de errores. | No presenta CRUD funcional. | |
| 3. Gestiona objetos relacionados mediante ORM, DTO y reglas de asociación | 16% | CE023-N2 (parcial) | Asociación entre entidades con DTO relacionado y navegación controlada, verificada en vivo. | Asociación funcional, con detalles menores en la navegación o el DTO. | Asociación incompleta o sin control de referencias. | No implementa objetos relacionados. | |
| 4. Implementa una operación cabecera-detalle con registro atómico, cálculos, estados, consistencia, commit y rollback | 16% | CE023-N2 (parcial) | Operación completa, con caso de éxito y caso de rollback probados y explicados. | Operación completa, con un caso probado. | Operación presente, sin evidencia clara de atomicidad. | No implementa la operación cabecera-detalle. | |
| 5. Implementa consultas, filtros, ordenamiento, agregaciones, reportes y configuración CORS | 16% | CE023-N2 (parcial) | Filtros combinados, reporte agregado y CORS configurado por propiedad, probados en vivo. | La mayoría de estos elementos funciona, con detalles menores. | Consultas o CORS incompletos. | No implementa consultas ni CORS. | |
| 6. Sustentación | 20% | CG | Sustenta con claridad y profesionalismo su aporte individual, respondiendo con precisión las preguntas del jurado. | Sustenta con solvencia, con detalles menores en claridad, orden o precisión. | Sustenta con dificultad; claridad, orden o precisión insuficientes. | No sustenta adecuadamente ni demuestra su aporte individual. | |

Nota final = suma de (`Peso` × `Puntos de la calificación obtenida`) / 100 × 20.

`CE023-N2 (parcial)` = porción de backend REST del Nivel 2 de CE023 (Programación) — la otra porción (frontend SPA, JWT, integración full-stack) se completa en Unidad 2 de LP2. `CG` = Competencia General "Innovación y solución de problemas" del sílabo de LP2 — no es CE023: los criterios 1-5 ya son la evidencia técnica, incluida su verificación en vivo; el criterio 6 verifica aporte individual y comunicación.

**Tabla 5. Subaspectos de la sustentación (Unidad 1)**

El criterio 6 se evalúa con los mismos 6 subaspectos de la sustentación integral del Proyecto Integrador ([Guía de Sustentación Final](../u3/guia-sustentacion.md#subaspectos-de-la-sustentacion-integral)) — exigibles desde esta primera sustentación de unidad, no solo en la sustentación final del ciclo (Unidad 3).

| Subaspecto | Qué observa en Unidad 1 |
|---|---|
| 1. Aporte individual | Cada integrante demuestra lo que construyó de su propio backend. |
| 2. Comunicación y orden | Claridad, estructura, tiempo y lenguaje técnico durante la presentación. |
| 3. Presentación personal y actitud | Puntualidad, vestimenta limpia y adecuada, higiene, cabello ordenado, actitud profesional, respeto, honestidad y coherencia con los valores y principios cristianos de la institución. |
| 4. Repositorio y estándares | Topics académicos configurados desde S2, organización, commits y reproducibilidad del backend. |
| 5. MkDocs o equivalente | Documentación de Unidad 1 publicada, navegable y alineada con `lp2-demo.md`. |
| 6. Pitch/demo ejecutiva | Introducción breve del backend y su avance, con apoyo visual (.pptx, Canva o equivalente) — no reemplaza la demo técnica de S06, la precede. |

Para usar la rúbrica con IA, solicita:

```text
Evalúa la sustentación y el producto (lp2-demo.md o la sección 2 de la guía S06) usando la rúbrica de esta sección.
Para cada criterio selecciona la calificación obtenida: A=20, B=15, C=10, D=5.
Justifica brevemente cada nivel con evidencia concreta (endpoints, código, pruebas en vivo).
Calcula la nota final con la fórmula: suma de (Peso × Puntos de la calificación obtenida) / 100 × 20.
Indica 2 fortalezas y 2 recomendaciones para lo que sigue en Unidad II.
```

## 9. Trazabilidad y procedencia de la rúbrica

Los primeros cinco criterios son cita literal del resultado de aprendizaje de la Unidad I en el sílabo de LP2; el sexto (Sustentación) corresponde a la sustentación exigida por el mismo sílabo (sesión 6, actividad 2).

**Con la malla curricular:** los criterios 1-5 corresponden a la porción de backend REST del **Nivel 2 de CE023** (Programación) — la otra porción de ese nivel, el frontend SPA, la seguridad JWT y la integración full-stack completa, se completa en la Unidad 2 de LP2, no aquí. El criterio 6 (Sustentación) es transversal y no forma parte de la definición de la competencia.

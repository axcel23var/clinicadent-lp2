# Brief Técnico del Proyecto Integrador

Este documento es el hito de **S2** del cronograma (ver [Alineamiento](index.md), sección 4): el punto donde el equipo declara, por escrito, qué proyecto va a construir durante el ciclo — antes de que ADS, BD2 y LP2 avancen más allá de sus primeras sesiones. No reemplaza el informe final; es la ficha corta que fija el rumbo desde el principio, para que "tu propio proyecto" (mencionado en la actividad autónoma de cada sesión) sea el mismo proyecto de S2 a S16, no algo que cada estudiante interpreta por su cuenta.

Cada equipo llena una sola copia de este brief, la publica en su repositorio (o en su MkDocs) y la actualiza solo si el alcance cambia de verdad — no en cada sesión.

## 1. Datos del equipo

- Nombre del equipo:
- Sección:
- Repositorio (URL):
- Topics del repositorio configurados (sí/no):

**Integrantes y curso(s) que lleva cada uno** — no todos los integrantes llevan necesariamente los 3 cursos (ver [Alineamiento](index.md), "Composición de equipos e integración"); esta tabla es la que define, desde el brief, quién evidencia qué:

| Integrante | ADS | BD2 | LP2 |
|---|---|---|---|
| | | | |
| | | | |
| | | | |
| | | | |

## 2. Dominio del proyecto

- Nombre del proyecto:
- Problema o necesidad que resuelve (2-4 líneas):
- Dominio de negocio (breve):
- Usuarios / actores principales (roles que interactúan con el sistema, ej. cliente, administrador, vendedor — mapa completo de stakeholders en ADS S1, 3.2):
- ¿Continúa un proyecto de un ciclo anterior, o es un dominio nuevo? Si continúa, indicar cuál:

## 3. Módulos de negocio y alcance full-stack esperado

**Regla de asignación de módulos:** cada integrante propone (o hereda) **dos módulos de negocio**, no uno solo. De esos dos, **al menos uno debe ser transaccional** — una operación cabecera-detalle real, con cálculos y al menos una regla de negocio verdadera, equivalente a `ventas` (`Venta`/`DetalleVenta`) en BomERP. El segundo módulo **no necesariamente** es transaccional — puede ser un CRUD simple, relacionado o no, equivalente a `catalogo` (`Categoria`/`Producto`) en BomERP. Ningún integrante se queda con dos módulos no transaccionales.

| Integrante | Módulo transaccional (tipo `ventas`) | Módulo no transaccional (tipo `catalogo`) |
|---|---|---|
| | | |
| | | |
| | | |

**Ficha por módulo** — completa un bloque como este por cada módulo de la tabla anterior (uno por celda, repite el bloque tantas veces como módulos tenga el equipo):

### Módulo: ______ (integrante: ______ · tipo: transaccional / no transaccional)

- Descripción breve (2-3 líneas): qué hace este módulo y por qué existe en el proyecto.
- Entidad principal (si no es transaccional, equivalente a `Categoria`/`Producto`) o cabecera-detalle (si es transaccional, equivalente a `Venta`/`DetalleVenta`):
- Lista inicial de requisitos (mínimo 3, redactados como "el sistema debe..."):
    1.
    2.
    3.

### Módulo: ______ (integrante: ______ · tipo: transaccional / no transaccional)

- Descripción breve (2-3 líneas):
- Entidad principal o cabecera-detalle:
- Lista inicial de requisitos:
    1.
    2.
    3.

*(repite este bloque por cada módulo restante del equipo, hasta cubrir la tabla completa)*

- Qué SÍ cubre este proyecto en conjunto:
- Qué NO cubre — fuera de alcance, explícito (mismo criterio que BomERP delimita `inventario`/`compras` sin implementarlos):

**Pendiente para S3:** arquitectura inicial (ADS), recursos REST iniciales previstos (LP2) y objetos Oracle iniciales previstos (BD2) — este brief todavía no los pide porque ese contenido recién lo entrega cada curso esa misma semana; se completan como anexo a este mismo documento en S3, no antes.

## 4. Aprobación

- Docente ADS:
- Docente BD2:
- Docente LP2:
- Fecha:

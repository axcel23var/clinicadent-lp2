# Guía de Sustentación Final

## Propósito

Orientar la presentación final del Proyecto Integrador del Ciclo 4. La sustentación debe demostrar que el producto funciona, está diseñado, opera sobre Oracle, fue probado y puede ser defendido por el equipo. Las menciones a `Producto–Categoria`/`Venta–DetalleVenta` son la nomenclatura del ejemplo BomERP; cada equipo sustenta sus propios módulos.

## Secuencia sugerida

| Momento | Tiempo | Evidencia |
|---|---:|---|
| Pitch ejecutivo | 1-3 min | Problema, solución y valor. |
| Diseño ADS | 3 min | Arquitectura, UML, patrones y ADRs. |
| Operación BD2 | 3 min | Seguridad, optimización, backup, recovery y monitoreo. |
| Aplicación LP2 | 5 min | Módulos propios (equivalentes a Producto–Categoria, Venta–DetalleVenta), backend, SPA, JWT, optimización, caché, observabilidad, paginación y auditoría. |
| Demo final | 5 min | Flujo end-to-end autenticado, transacción, persistencia, consulta y evidencia de operación. |
| Preguntas individuales | 5 min | Aporte y defensa técnica por integrante. |

## Defensa individual

Cada integrante debe poder responder:

1. Qué parte desarrolló.
2. Qué decisión técnica tomó.
3. Qué evidencia demuestra su aporte.
4. Qué error corrigió.
5. Qué limitación reconoce.
6. Cómo su trabajo se conecta con los otros cursos.

## Subaspectos de la sustentación integral

La sustentación integral debe representar como mínimo el 30% de la evaluación del proyecto. Se revisa mediante los siguientes subaspectos:

| Subaspecto | Qué observa |
|---|---|
| 1. Defensa técnica | Explicación de arquitectura, base de datos, código, decisiones, limitaciones, evidencias y trazabilidad del sistema. |
| 2. Comunicación y orden | Claridad, estructura, tiempo y lenguaje técnico. |
| 3. Presentación personal y actitud | Puntualidad, vestimenta limpia y adecuada, higiene, cabello ordenado, actitud profesional, respeto, honestidad y coherencia con los valores y principios cristianos de la institución. |
| 4. Aporte individual | Cada integrante demuestra lo que hizo. |
| 5. Repositorio y estándares | Topics, organización, commits, documentación y reproducibilidad. |
| 6. MkDocs o equivalente | Documentación publicada, navegable y alineada al producto. |
| 7. Pitch/demo ejecutiva | Introducción clara del problema, solución y valor, seguida de una demo funcional. |

La sustentación profesional forma parte de la evaluación porque el producto final no solo debe funcionar; también debe ser presentado, explicado y defendido con responsabilidad académica, ética, respeto, honestidad y coherencia con los valores y principios cristianos de la institución.

Estos mismos 7 subaspectos son los que evalúa el criterio de "Sustentación y defensa técnica" en cada rúbrica de Unidad 1 (`ads-producto.md`, `lp2-demo.md`, `bd2-producto.md`) — no son exclusivos de la sustentación final.

## Evidencia obligatoria

- Repositorio con topics académicos.
- MkDocs o equivalente actualizado.
- Demo ejecutable o evidencia técnica reproducible.
- Matriz final de trazabilidad.
- Pruebas y bitácora de estabilización.
- Evidencias Oracle de operación y resiliencia.
- Build optimizado, estrategia de caché y decisión sobre Redis.
- Logs estructurados, endpoint de salud y monitoreo básico.
- Paginación de alto volumen, auditoría y pruebas end-to-end.
- Guía reproducible de configuración y despliegue.

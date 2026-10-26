# BD2 - Producto de Unidad 2

**Este documento es el ejemplo BomERP del docente, no una plantilla obligatoria.** Cada sede (Lima, Juliaca, Tarapoto) y cada grupo dentro de una misma sede administra su propio esquema Oracle, definido desde su propio [brief.md](../brief.md) de S2 y consolidado en su [Producto BD2 U1](../u1/bd2-producto.md). Los scripts y roles concretos de este documento son los del ejemplo BomERP; cada equipo los reemplaza por los de su propio proyecto. Lo exigible a todos es la estructura: arquitectura e instancia, seguridad (usuarios, roles, privilegios), almacenamiento y auditoría, optimización de rendimiento y particionamiento.

## Producto

**Base de datos empresarial administrada, optimizada y asegurada.**

## 1. Scripts del producto

**Tabla 1. Scripts del producto U2**

| Script | Sesión BD2 | Uso |
|---|---|---|
| [01_security.sql](oracle/01_security.sql) | [S8](../../bd2/sesiones/S08_Usuarios_Roles_Privilegios.md) | Usuarios, roles y privilegios bajo mínimo privilegio. |
| [02_storage_audit.sql](oracle/02_storage_audit.sql) | [S9](../../bd2/sesiones/S09_Almacenamiento_Seguridad_Auditoria.md) | Tablespace, estructura física y auditoría de acceso/sentencias. |
| [03_partition_perf.sql](oracle/03_partition_perf.sql) | [S10](../../bd2/sesiones/S10_Optimizacion_Rendimiento.md)-[S11](../../bd2/sesiones/S11_Particionamiento_Escalabilidad.md) | Estadísticas, AWR, índices (S10) y particionamiento Range/Hash/List/Composite con *pruning* verificado (S11). |

Pendiente (se agrega cuando BD2 lo dicte, Unidad 3): RMAN, Data Pump y PITR (S13).

## 2. Evidencias esperadas

**Tabla 2. Evidencias esperadas U2**

| Dimensión | Evidencia |
|---|---|
| Arquitectura e instancia | ORACLE_HOME, ORACLE_SID, SGA, PGA y procesos *background* documentados, administración desde Oracle Linux como `SYSDBA` (S7). |
| Seguridad | Usuarios, roles, privilegios y mínimo privilegio, con matriz de accesos del proyecto (S8). |
| Almacenamiento | Tablespace, datafile, segmentos, estrategia de crecimiento y `archivelog` (S9). |
| Auditoría | Auditoría de sentencias o de acceso, con evidencia en Enterprise Manager Express (S9). |
| Rendimiento | `EXPLAIN PLAN`, Cost Based Optimizer, `DBMS_STATS`, AWR (*Automatic Workload Repository*, dentro del límite de licencia de Oracle Database Free) e índices sobre consultas críticas (S10). |
| Escalabilidad | Particionamiento (Range, Hash, List o Composite) con *partition pruning* verificado contra `EXPLAIN PLAN`, o justificación técnica cuando no aplique (S11). |

## 3. Integración con ADS y LP2

**Tabla 3. Integración con ADS y LP2**

| BD2 | ADS | LP2 |
|---|---|---|
| Rol `rol_app_erp` | Atributo seguridad | Backend usa usuario de aplicación. |
| Auditoría de ventas | Requisito de auditabilidad | SPA ejecuta registro o anulación auditable. |
| AWR + índice/partición por fecha | Atributo rendimiento | Filtros de ventas por estado/fecha, reporte agregado. |
| Estadísticas actualizadas | Decisión de optimización | Consultas de API más estables. |
| Particionamiento de ventas históricas | Diseño de integración (ADS S11) | Patrón equivalente, referenciado desde ADS S11 (3.6) — sin ejecución propia de LP2. |

## 4. Rúbrica de Evaluación

**Tabla 4. Rúbrica de evaluación de la Unidad 2**

| Criterio | Peso | CE / Nivel | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Calificación obtenida |
|---|---:|---|---|---|---|---|---:|
| 1. Reconoce la arquitectura Oracle y administra la instancia desde Linux | 16% | CE022-N2 (parcial) | Componentes de instancia (SGA, PGA, procesos *background*) identificados y administración real como `SYSDBA` desde Oracle Linux, verificada en vivo. | Componentes identificados, con administración `SYSDBA` parcialmente verificada. | Componentes mencionados sin administración real verificable. | No presenta administración de instancia verificable. | |
| 2. Configura usuarios, roles y privilegios bajo mínimo privilegio | 16% | CE022-N2 (parcial) | Usuarios y roles creados con privilegios mínimos justificados, con matriz de accesos verificable contra el proyecto real. | Usuarios y roles creados, con la justificación de mínimo privilegio incompleta. | Usuarios creados con privilegios excesivos o sin matriz de accesos. | No presenta usuarios ni roles configurados. | |
| 3. Administra almacenamiento, seguridad y auditoría | 16% | CE022-N2 (parcial) | Estructura de almacenamiento documentada y auditoría de sentencias o acceso activa, verificada con evidencia real (Enterprise Manager Express o vistas `DBA_AUDIT_*`). | Estructura y auditoría presentes, con verificación parcial. | Estructura o auditoría incompleta. | No presenta administración de almacenamiento ni auditoría. | |
| 4. Optimiza rendimiento mediante estadísticas, planes, AWR e índices | 16% | CE022-N2 (parcial) | Comparación real de `EXPLAIN PLAN` antes/después de estadísticas e índices, con AWR consultado dentro del límite de licencia de Oracle Database Free. | Comparación presente, con AWR o interpretación parcial. | Un solo `EXPLAIN PLAN`, sin comparación ni AWR. | No presenta evidencia de optimización de rendimiento. | |
| 5. Aplica particionamiento para escalabilidad | 16% | CE022-N2 (parcial) | Tabla particionada (Range, Hash, List o Composite) con *partition pruning* verificado contra `EXPLAIN PLAN`, incluido el caso en que una consulta mal escrita lo desactiva. | Tabla particionada y verificada, sin reproducir el caso que desactiva el *pruning*. | Particionamiento mencionado sin verificación real del *pruning*. | No presenta particionamiento ni justificación técnica de por qué no aplica. | |
| 6. Sustentación | 20% | CG | Sustenta con claridad y profesionalismo su aporte individual, respondiendo con precisión las preguntas del jurado. | Sustenta con solvencia, con detalles menores en claridad, orden o precisión. | Sustenta con dificultad; claridad, orden o precisión insuficientes. | No sustenta adecuadamente ni demuestra su aporte individual. | |

Nota final = suma de (`Peso` × `Puntos de la calificación obtenida`) / 100 × 20.

`CE022-N2 (parcial)` = porción de **administración, almacenamiento, auditoría, optimización y particionamiento** del Nivel 2 de CE022 (Ingeniería de la Información) — la porción restante de ese mismo Nivel (continuidad del negocio: respaldo y recuperación con RMAN) se completa en la Unidad 3 de BD2. `CG` = Competencia General del sílabo de BD2 (ver la nota sobre la inconsistencia de nombre entre secciones III y VIII del propio sílabo, documentada en [Producto U1](../u1/bd2-producto.md#7-rubrica-de-evaluacion)). No es CE022: los criterios 1-5 ya son la evidencia técnica, incluida su verificación en vivo; el criterio 6 verifica aporte individual y comunicación.

**Tabla 5. Subaspectos de la sustentación (Unidad 2)**

El criterio 6 se evalúa con los mismos 6 subaspectos de la sustentación integral del Proyecto Integrador ([Guía de Sustentación Final](../u3/guia-sustentacion.md#subaspectos-de-la-sustentacion-integral)) — exigibles también en esta segunda sustentación de unidad, igual que en la Unidad 1.

| Subaspecto | Qué observa en Unidad 2 |
|---|---|
| 1. Aporte individual | Cada integrante demuestra al menos un objeto Oracle real que administró (un rol, una política de auditoría, un índice justificado, o una partición). |
| 2. Comunicación y orden | Claridad, estructura, tiempo y lenguaje técnico durante la presentación. |
| 3. Presentación personal y actitud | Puntualidad, vestimenta limpia y adecuada, higiene, cabello ordenado, actitud profesional, respeto, honestidad y coherencia con los valores y principios cristianos de la institución. |
| 4. Repositorio y estándares | Organización, commits y reproducibilidad de los scripts Oracle de administración desde S7. |
| 5. MkDocs o equivalente | Documentación de Unidad 2 publicada, navegable y alineada con `bd2-producto.md`. |
| 6. Pitch/demo ejecutiva | Avance de la base de datos administrada y optimizada, con apoyo visual (.pptx, Canva o equivalente) — no reemplaza la demo técnica de S12, la precede. |

Para usar la rúbrica con IA, solicita:

```text
Evalúa la sustentación y el producto (bd2-producto.md o la sección 2 de la guía S12) usando la rúbrica de esta sección.
Para cada criterio selecciona la calificación obtenida: A=20, B=15, C=10, D=5.
Justifica brevemente cada nivel con evidencia concreta (objetos Oracle, planes de ejecución, AWR, particionamiento).
Calcula la nota final con la fórmula: suma de (Peso × Puntos de la calificación obtenida) / 100 × 20.
Indica 2 fortalezas y 2 recomendaciones para lo que sigue en Unidad III.
```

## 5. Trazabilidad y procedencia de la rúbrica

Los primeros cinco criterios son cita literal de los "Criterios de evaluación del producto" de la Unidad 2 en el sílabo de BD2; el sexto (Sustentación) corresponde a la sustentación exigida por el mismo sílabo (sesión 12, actividad 2).

**Con la malla curricular:** los criterios 1-5 corresponden a la porción de **administración empresarial** del **Nivel 2 de CE022** (Ingeniería de la Información) — usuarios/privilegios, almacenamiento, auditoría, optimización (AWR, índices) y particionamiento. La porción restante de ese mismo Nivel (continuidad del negocio: respaldo y recuperación con RMAN) se completa en la Unidad 3 de BD2, no aquí. El criterio 6 (Sustentación) es transversal y no forma parte de la definición de la competencia.

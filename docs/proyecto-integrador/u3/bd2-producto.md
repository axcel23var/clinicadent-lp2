# BD2 - Producto de Unidad 3

**Este documento es el ejemplo BomERP del docente, no una plantilla obligatoria.** Cada sede (Lima, Juliaca, Tarapoto) y cada grupo dentro de una misma sede opera su propio esquema Oracle sobre su propio dominio. Los scripts y evidencias concretas de este documento son las del ejemplo BomERP; cada equipo las reemplaza por las de su propio proyecto. Lo exigible a todos es la estructura: backup, recovery, monitoreo, diagnóstico y seguridad/rendimiento final.

## Producto

**Base Oracle operativa, administrada, optimizada, auditada y resiliente.**

## Scripts y evidencias

| Archivo | Propósito |
|---|---|
| [01_rman_backup.md](oracle/01_rman_backup.md) | Guía de backup completo e incremental con RMAN. |
| [02_recovery_scenario.md](oracle/02_recovery_scenario.md) | Escenario de recuperación y validación posterior. |
| [03_monitoring_queries.sql](oracle/03_monitoring_queries.sql) | Consultas de monitoreo y diagnóstico. |
| [04_operational_checklist.md](oracle/04_operational_checklist.md) | Checklist operativo de BD2. |

## Evidencias mínimas

| Dimensión | Evidencia |
|---|---|
| Backup | Comando, captura o log de backup completo/incremental. |
| Recovery | Escenario simulado y evidencia de recuperación. |
| Monitoreo | Consultas `V$`, `DBA_`, sesiones, bloqueos o rendimiento. |
| Diagnóstico | Hallazgo, causa probable y acción correctiva. |
| Seguridad | Usuarios, roles, privilegios y auditoría final. |
| Rendimiento | Explain Plan, estadísticas, índices o AWR. |

## Integración con el producto final

| Evidencia BD2 | Relación con ADS | Relación con LP2 |
|---|---|---|
| Backup y recovery | Atributo de resiliencia | La aplicación puede recuperarse ante pérdida de datos. |
| Monitoreo de sesiones | Operación del sistema | Diagnóstico de conexiones backend. |
| Auditoría | Decisión de auditabilidad | Evidencia de acciones ejecutadas desde la aplicación. |
| Rendimiento | Atributo de eficiencia | Respuesta de consultas y filtros del sistema. |

## Cierre BD2

BD2 está completo cuando la base no solo almacena datos, sino que puede ser administrada, auditada, optimizada, respaldada, recuperada y diagnosticada como soporte real del sistema empresarial.

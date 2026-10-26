# Escenario de Recovery - Evidencia U3

## Objetivo

Demostrar que el equipo conoce el procedimiento básico para recuperar la base ante una falla controlada.

## Escenario sugerido

1. Registrar datos de prueba desde la aplicación.
2. Ejecutar backup.
3. Simular pérdida o alteración controlada de datos en ambiente académico.
4. Ejecutar recuperación.
5. Validar que la aplicación puede consultar nuevamente los datos esperados.

## Recovery básico de referencia

```sql
RMAN> CONNECT TARGET /
RMAN> RESTORE DATABASE;
RMAN> RECOVER DATABASE;
```

## Evidencia requerida

| Evidencia | Descripción |
|---|---|
| Estado antes de la falla | Consulta o captura. |
| Falla simulada | Descripción controlada del escenario. |
| Comando de recuperación | Restore/recover o alternativa usada. |
| Validación posterior | Consulta y prueba desde la aplicación. |
| Lección aprendida | Riesgo y mejora identificada. |

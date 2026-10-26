# RMAN Backup - Evidencia U3

## Objetivo

Demostrar que la base Oracle del proyecto puede respaldarse como parte de la continuidad del negocio.

## Backup completo

```sql
RMAN> CONNECT TARGET /
RMAN> BACKUP DATABASE PLUS ARCHIVELOG;
```

## Backup incremental nivel 0

```sql
RMAN> BACKUP INCREMENTAL LEVEL 0 DATABASE PLUS ARCHIVELOG;
```

## Backup incremental nivel 1

```sql
RMAN> BACKUP INCREMENTAL LEVEL 1 DATABASE PLUS ARCHIVELOG;
```

## Evidencia requerida

| Evidencia | Descripción |
|---|---|
| Fecha y hora | Momento de ejecución. |
| Comando ejecutado | Backup completo o incremental. |
| Log o captura | Resultado exitoso o error documentado. |
| Ubicación del backup | Ruta o catálogo usado. |
| Responsable | Integrante que ejecutó y explicó el proceso. |

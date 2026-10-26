-- BD2 - S05 Indices para Optimizacion
-- Script completo y listo para ejecutar (ver explicacion en
-- docs/bd2/sesiones/S05_Indices_Optimizacion.md, seccion 3).
--
-- Requiere haber ejecutado antes, en orden: S01_01_esquemas.sql,
-- S01_02_tablas.sql, S01_03_plsql.sql, S02_triggers_dml_auditoria.sql
-- (LOG_ERRORES creada en S3, no en un script propio - ver
-- S03_Excepciones_Robustez.md, 3.2), S04_01_esquemas.sql,
-- S04_02_tablas.sql y el volumen de VENTAS/DETALLE_VENTAS de
-- S04_optimizacion_consultas.sql (bloque 1).
--
-- Bloques que requieren conexion como BOM_CATALOGO: 1), 4).
-- Bloques que requieren conexion como BOM_VENTAS: 5), 6), 7).

-- ============================================================
-- 1) Volumen adicional en LOG_ERRORES (conectado como BOM_CATALOGO)
--    Con solo tres filas (una por caso de S3, 3.6) no hay suficiente
--    volumen para medir selectividad con sentido.
-- ============================================================
BEGIN
    FOR i IN 1..300 LOOP
        INSERT INTO BOM_CATALOGO.LOG_ERRORES (OBJETO, CODIGO_ERROR, MENSAJE_ERROR)
        VALUES (
            CASE MOD(i, 3)
                WHEN 0 THEN 'SP_REGISTRAR_PRODUCTO'
                WHEN 1 THEN 'SP_APLICAR_DESCUENTO_PRODUCTO'
                ELSE 'FN_OBTENER_PRECIO_PRODUCTO'
            END,
            -20010 - MOD(i, 3),
            'Volumen de prueba para selectividad (S5)'
        );
    END LOOP;
    COMMIT;
END;
/

SELECT COUNT(*) AS TOTAL_LOG FROM BOM_CATALOGO.LOG_ERRORES;

-- Actualiza estadisticas antes de medir selectividad o capturar
-- EXPLAIN PLAN (S4, 2.4) - el volumen recien cargado no cuenta
-- todavia para el optimizador sin este paso.
EXEC DBMS_STATS.GATHER_TABLE_STATS('BOM_CATALOGO', 'LOG_ERRORES');

-- ============================================================
-- 2) Selectividad de las columnas candidatas
-- ============================================================
SELECT 'VENTAS.FECHA' AS COLUMNA,
       COUNT(DISTINCT FECHA) AS DISTINTOS,
       COUNT(*) AS TOTAL,
       ROUND(COUNT(DISTINCT FECHA) / COUNT(*), 4) AS SELECTIVIDAD
FROM BOM_VENTAS.VENTAS
UNION ALL
SELECT 'VENTAS.ESTADO',
       COUNT(DISTINCT ESTADO),
       COUNT(*),
       ROUND(COUNT(DISTINCT ESTADO) / COUNT(*), 4)
FROM BOM_VENTAS.VENTAS
UNION ALL
SELECT 'LOG_ERRORES.OBJETO',
       COUNT(DISTINCT OBJETO),
       COUNT(*),
       ROUND(COUNT(DISTINCT OBJETO) / COUNT(*), 4)
FROM BOM_CATALOGO.LOG_ERRORES;

-- ============================================================
-- 3) Indice B-Tree sobre VENTAS.FECHA (conectado como BOM_VENTAS)
-- ============================================================
CREATE INDEX ix_ventas_fecha ON BOM_VENTAS.VENTAS (FECHA);

-- Consulta acotada (alta selectividad de predicado): ventas de hoy.
EXPLAIN PLAN FOR
SELECT p.NOMBRE, SUM(d.CANTIDAD) AS TOTAL_UNIDADES, SUM(d.SUBTOTAL) AS TOTAL_VENDIDO
FROM BOM_VENTAS.DETALLE_VENTAS d
JOIN BOM_VENTAS.VENTAS v ON v.ID = d.ID_VENTA
JOIN BOM_CATALOGO.PRODUCTOS p ON p.ID = d.ID_PRODUCTO
WHERE v.FECHA >= TRUNC(SYSDATE)
GROUP BY p.NOMBRE
ORDER BY TOTAL_VENDIDO DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Comparacion con la consulta de 30 dias de S4 (3.7) - fraccion
-- de filas demasiado grande para que el indice sea siempre mejor.
EXPLAIN PLAN FOR
SELECT p.NOMBRE, SUM(d.CANTIDAD) AS TOTAL_UNIDADES, SUM(d.SUBTOTAL) AS TOTAL_VENDIDO
FROM BOM_VENTAS.DETALLE_VENTAS d
JOIN BOM_VENTAS.VENTAS v ON v.ID = d.ID_VENTA
JOIN BOM_CATALOGO.PRODUCTOS p ON p.ID = d.ID_PRODUCTO
WHERE v.FECHA >= TRUNC(SYSDATE) - 30
GROUP BY p.NOMBRE
ORDER BY TOTAL_VENDIDO DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ============================================================
-- 4) Indice Bitmap sobre LOG_ERRORES.OBJETO (conectado como BOM_CATALOGO)
-- ============================================================
CREATE BITMAP INDEX ix_log_errores_objeto ON BOM_CATALOGO.LOG_ERRORES (OBJETO);

EXPLAIN PLAN FOR
SELECT OBJETO, COUNT(*) AS TOTAL_ERRORES
FROM BOM_CATALOGO.LOG_ERRORES
WHERE OBJETO = 'SP_REGISTRAR_PRODUCTO'
GROUP BY OBJETO;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ============================================================
-- 5) Indice Function-Based sobre TRUNC(VENTAS.FECHA) (conectado como BOM_VENTAS)
--    Reporte por dia calendario - la funcion es requisito real,
--    no una mala practica a corregir (a diferencia de S4, 3.6).
-- ============================================================
CREATE INDEX ix_ventas_fecha_dia ON BOM_VENTAS.VENTAS (TRUNC(FECHA));

EXPLAIN PLAN FOR
SELECT TRUNC(FECHA) AS DIA, COUNT(*) AS VENTAS_DEL_DIA, SUM(TOTAL) AS MONTO_DEL_DIA
FROM BOM_VENTAS.VENTAS
WHERE TRUNC(FECHA) = TRUNC(SYSDATE) - 5
GROUP BY TRUNC(FECHA);

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ============================================================
-- 6) Indice que no conviene: VENTAS.ESTADO (conectado como BOM_VENTAS)
--    Selectividad cercana a 0 (un unico valor, EstadoVenta.REGISTRADA
--    en LP2 S4) - el optimizador no deberia preferirlo.
-- ============================================================
CREATE INDEX ix_ventas_estado ON BOM_VENTAS.VENTAS (ESTADO);

EXPLAIN PLAN FOR
SELECT * FROM BOM_VENTAS.VENTAS WHERE ESTADO = 'REGISTRADA';

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Confirmado que no aporta beneficio: se elimina, no se deja
-- "por si acaso" (costo de mantenimiento en cada INSERT, sin
-- ningun beneficio medido).
DROP INDEX ix_ventas_estado;

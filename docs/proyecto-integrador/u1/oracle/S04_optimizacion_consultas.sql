-- BD2 - S04 Optimizacion de Consultas SQL
-- Script completo y listo para ejecutar (ver explicacion en
-- docs/bd2/sesiones/S04_Optimizacion_Consultas_SQL.md, seccion 3).
--
-- Requiere haber ejecutado antes, en orden: S01_01_esquemas.sql,
-- S01_02_tablas.sql, S01_03_plsql.sql (BOM_CATALOGO.PRODUCTOS con al
-- menos un producto real cargado desde LP2 S1-S3), S04_01_esquemas.sql
-- y S04_02_tablas.sql (esquema y tablas de BOM_VENTAS).
--
-- Bloques que requieren una cuenta DBA: 3).
-- Bloques que requieren conexion como BOMERP_APP: 1), 2) y 4).

-- ============================================================
-- 1) Volumen de prueba (conectado como BOMERP_APP)
--    Ajusta el rango de ID_PRODUCTO si tu BOM_CATALOGO.PRODUCTOS
--    tiene ids muy distintos - el bloque usa MOD para no depender
--    de un id fijo.
-- ============================================================
DECLARE
    v_id_venta      NUMBER;
    v_max_producto  NUMBER;
BEGIN
    SELECT MAX(ID) INTO v_max_producto FROM BOM_CATALOGO.PRODUCTOS;

    FOR i IN 1..500 LOOP
        INSERT INTO BOM_VENTAS.VENTAS (FECHA, ESTADO, TOTAL)
        VALUES (SYSTIMESTAMP - MOD(i, 90), 'REGISTRADA', 0)
        RETURNING ID INTO v_id_venta;

        FOR j IN 1..(MOD(i, 3) + 1) LOOP
            INSERT INTO BOM_VENTAS.DETALLE_VENTAS
                (ID_VENTA, ID_PRODUCTO, NOMBRE_PRODUCTO, PRECIO_UNITARIO, CANTIDAD, SUBTOTAL)
            VALUES
                (v_id_venta, MOD(i + j, v_max_producto) + 1, 'Producto de prueba', 50.00, j,
                 50.00 * j);
        END LOOP;
    END LOOP;
    COMMIT;
END;
/

-- Verificacion del volumen cargado:
SELECT COUNT(*) AS TOTAL_VENTAS FROM BOM_VENTAS.VENTAS;
SELECT COUNT(*) AS TOTAL_DETALLES FROM BOM_VENTAS.DETALLE_VENTAS;

-- ============================================================
-- 2) EXPLAIN PLAN antes de optimizar (conectado como BOMERP_APP)
--    Consulta representativa: reporte de ventas por producto,
--    ultimos 30 dias - con la funcion sobre FECHA que 2.5 senala
--    como mala practica, a proposito, para comparar despues.
-- ============================================================
EXPLAIN PLAN FOR
SELECT p.NOMBRE, SUM(d.CANTIDAD) AS TOTAL_UNIDADES, SUM(d.SUBTOTAL) AS TOTAL_VENDIDO
FROM BOM_VENTAS.DETALLE_VENTAS d
JOIN BOM_VENTAS.VENTAS v ON v.ID = d.ID_VENTA
JOIN BOM_CATALOGO.PRODUCTOS p ON p.ID = d.ID_PRODUCTO
WHERE TRUNC(SYSDATE) - TRUNC(v.FECHA) <= 30
GROUP BY p.NOMBRE
ORDER BY TOTAL_VENDIDO DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ============================================================
-- 3) DBMS_STATS: actualizar estadisticas (cuenta DBA)
--    Gathering stats sobre tablas de otro esquema requiere
--    privilegio elevado.
-- ============================================================
EXEC DBMS_STATS.GATHER_TABLE_STATS('BOM_VENTAS', 'VENTAS');
EXEC DBMS_STATS.GATHER_TABLE_STATS('BOM_VENTAS', 'DETALLE_VENTAS');
EXEC DBMS_STATS.GATHER_TABLE_STATS('BOM_CATALOGO', 'PRODUCTOS');

-- Repite el EXPLAIN PLAN, sin cambiar la consulta todavia -
-- el unico cambio hasta este punto son las estadisticas:
EXPLAIN PLAN FOR
SELECT p.NOMBRE, SUM(d.CANTIDAD) AS TOTAL_UNIDADES, SUM(d.SUBTOTAL) AS TOTAL_VENDIDO
FROM BOM_VENTAS.DETALLE_VENTAS d
JOIN BOM_VENTAS.VENTAS v ON v.ID = d.ID_VENTA
JOIN BOM_CATALOGO.PRODUCTOS p ON p.ID = d.ID_PRODUCTO
WHERE TRUNC(SYSDATE) - TRUNC(v.FECHA) <= 30
GROUP BY p.NOMBRE
ORDER BY TOTAL_VENDIDO DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- ============================================================
-- 4) Consulta reescrita (buena practica SQL) y EXPLAIN PLAN final
--    (conectado como BOMERP_APP)
--    Mismo resultado que 2)/3) - "ultimos 30 dias" - pero sin
--    envolver la columna FECHA en una funcion.
-- ============================================================
SELECT p.NOMBRE, SUM(d.CANTIDAD) AS TOTAL_UNIDADES, SUM(d.SUBTOTAL) AS TOTAL_VENDIDO
FROM BOM_VENTAS.DETALLE_VENTAS d
JOIN BOM_VENTAS.VENTAS v ON v.ID = d.ID_VENTA
JOIN BOM_CATALOGO.PRODUCTOS p ON p.ID = d.ID_PRODUCTO
WHERE v.FECHA >= TRUNC(SYSDATE) - 30
GROUP BY p.NOMBRE
ORDER BY TOTAL_VENDIDO DESC;

-- Antes de seguir: confirma que esta version devuelve el mismo
-- numero de productos y los mismos totales que la de 2)/3).

EXPLAIN PLAN FOR
SELECT p.NOMBRE, SUM(d.CANTIDAD) AS TOTAL_UNIDADES, SUM(d.SUBTOTAL) AS TOTAL_VENDIDO
FROM BOM_VENTAS.DETALLE_VENTAS d
JOIN BOM_VENTAS.VENTAS v ON v.ID = d.ID_VENTA
JOIN BOM_CATALOGO.PRODUCTOS p ON p.ID = d.ID_PRODUCTO
WHERE v.FECHA >= TRUNC(SYSDATE) - 30
GROUP BY p.NOMBRE
ORDER BY TOTAL_VENDIDO DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

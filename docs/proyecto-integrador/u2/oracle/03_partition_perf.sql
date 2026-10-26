CREATE INDEX idx_venta_estado_fecha_u2 ON venta (estado, fecha);

BEGIN
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'VENTA');
    DBMS_STATS.GATHER_TABLE_STATS(USER, 'DETALLE_VENTA');
END;
/

EXPLAIN PLAN FOR
SELECT id_venta, cliente, estado, total, fecha
FROM venta
WHERE estado = 'ACTIVA'
ORDER BY fecha DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

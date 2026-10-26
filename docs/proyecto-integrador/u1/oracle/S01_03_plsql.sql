-- BD2 - S01 PL/SQL Aplicado al Negocio
-- Paso 3: funcion (IN, IN), procedimiento (IN, OUT), procedimiento (IN OUT).
-- Requiere haber ejecutado antes S01_01_esquemas.sql y S01_02_tablas.sql.

CREATE OR REPLACE FUNCTION BOM_CATALOGO.FN_VALOR_INVENTARIO_PRODUCTO(
    p_precio IN NUMBER,
    p_stock IN NUMBER
) RETURN NUMBER IS
BEGIN
    RETURN p_precio * p_stock;
END;
/

CREATE OR REPLACE PROCEDURE BOM_CATALOGO.SP_REGISTRAR_PRODUCTO(
    p_id_categoria IN NUMBER,
    p_nombre IN VARCHAR2,
    p_precio IN NUMBER,
    p_stock IN NUMBER,
    p_id_producto OUT NUMBER
) IS
BEGIN
    INSERT INTO BOM_CATALOGO.PRODUCTOS (ID_CATEGORIA, NOMBRE, PRECIO, STOCK)
    VALUES (p_id_categoria, p_nombre, p_precio, p_stock)
    RETURNING ID INTO p_id_producto;
END;
/

CREATE OR REPLACE PROCEDURE BOM_CATALOGO.SP_APLICAR_DESCUENTO_PRODUCTO(
    p_precio IN OUT NUMBER,
    p_porcentaje_descuento IN NUMBER
) IS
BEGIN
    p_precio := p_precio - (p_precio * p_porcentaje_descuento / 100);
END;
/

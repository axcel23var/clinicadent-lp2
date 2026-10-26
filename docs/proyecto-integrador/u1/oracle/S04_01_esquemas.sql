-- BD2 - S04 Optimizacion de Consultas SQL
-- Paso 1: esquema BOM_VENTAS (usuario propietario). Ejecutar con una
-- cuenta DBA (ej. system).
--
-- BOMERP_APP NO se vuelve a crear: ya existe desde S01_01_esquemas.sql.
-- Solo se crea el esquema dueno de los objetos nuevos, mismo criterio
-- que el catalogo en S01.
--
-- Requiere haber ejecutado antes S01_01_esquemas.sql y S01_02_tablas.sql
-- (crea BOMERP_APP y BOM_CATALOGO.PRODUCTOS, con al menos un producto
-- real cargado desde LP2 S1-S3).

CREATE USER BOM_VENTAS IDENTIFIED BY "123456" QUOTA UNLIMITED ON USERS;
GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE PROCEDURE, CREATE TRIGGER TO BOM_VENTAS;

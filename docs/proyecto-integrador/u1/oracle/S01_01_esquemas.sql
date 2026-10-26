-- BD2 - S01 PL/SQL Aplicado al Negocio
-- Paso 1: esquemas (usuarios propietarios). Ejecutar con una cuenta DBA
-- (ej. system).
--
-- Contraseñas en texto plano (123456): ambiente DEV, sin secretos que
-- proteger, mismo criterio que application-dev.yml/compose-dev.yml
-- de LP2 (ver lp2/CLAUDE.md, seccion "Ambientes"). La de BOMERP_APP debe
-- coincidir exactamente con la de application-dev.yml. Ver la
-- explicacion del enfoque alternativo (ACCEPT/HIDE, contraseña
-- interactiva por persona) en docs/bd2/sesiones/S01_PLSQL_Aplicado_Negocio.md,
-- seccion 3.2.
--
-- Nota de alcance: solo se crea el usuario/esquema (QUOTA UNLIMITED ON
-- USERS, el tablespace por defecto). No se crea un tablespace dedicado
-- por esquema: eso es contenido de BD2 U2 (administración de
-- almacenamiento, sesión 9) y no aporta nada para lo que LP2 necesita
-- hoy — se revisita ahí si corresponde, no antes.

CREATE USER BOM_CATALOGO IDENTIFIED BY "123456" QUOTA UNLIMITED ON USERS;
CREATE USER BOMERP_APP IDENTIFIED BY "123456";

GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE PROCEDURE, CREATE TRIGGER TO BOM_CATALOGO;
GRANT CREATE SESSION TO BOMERP_APP;

-- ============================================================
-- Vista previa (NO ejecutar aqui): asi se veria agregar un esquema
-- nuevo cuando llegue la sesion que lo necesite (BOM_VENTAS, cuando
-- LP2 llegue a Venta-DetalleVenta). No se crea antes de esa sesion
-- (mismo criterio de "no crear por si acaso" de ADR-002).
--
-- Notar que BOMERP_APP NO se vuelve a crear: ya existe desde este
-- mismo script. Solo se crea el esquema dueno de los objetos nuevos.
--
-- CREATE USER BOM_VENTAS IDENTIFIED BY "123456" QUOTA UNLIMITED ON USERS;
-- GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE PROCEDURE, CREATE TRIGGER TO BOM_VENTAS;

CREATE USER app_erp IDENTIFIED BY app_erp_pwd;
CREATE ROLE rol_app_erp;
GRANT CREATE SESSION TO rol_app_erp;
GRANT SELECT, INSERT, UPDATE ON venta TO rol_app_erp;
GRANT SELECT, INSERT, UPDATE ON detalle_venta TO rol_app_erp;
GRANT SELECT, UPDATE ON producto TO rol_app_erp;
GRANT SELECT ON categoria TO rol_app_erp;
GRANT SELECT ON usuario_app TO rol_app_erp;
GRANT rol_app_erp TO app_erp;

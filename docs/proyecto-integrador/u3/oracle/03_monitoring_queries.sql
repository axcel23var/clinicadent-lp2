-- Sesiones activas
SELECT sid, serial#, username, status, machine, program
FROM v$session
WHERE username IS NOT NULL
ORDER BY username, status;

-- Bloqueos
SELECT s.sid, s.serial#, s.username, l.type, l.lmode, l.request, l.block
FROM v$lock l
JOIN v$session s ON s.sid = l.sid
WHERE s.username IS NOT NULL;

-- Objetos principales del esquema
SELECT object_name, object_type, status
FROM user_objects
ORDER BY object_type, object_name;

-- Tamaño por segmento
SELECT segment_name, segment_type, bytes / 1024 / 1024 AS mb
FROM user_segments
ORDER BY mb DESC;

-- Auditoría básica si está disponible
SELECT username, action_name, obj_name, timestamp
FROM dba_audit_trail
WHERE obj_name IN ('VENTA', 'DETALLE_VENTA', 'PRODUCTO', 'CATEGORIA', 'USUARIO_APP')
ORDER BY timestamp DESC;

-- Consultas lentas o recientes según vistas disponibles
SELECT sql_id, executions, elapsed_time, cpu_time
FROM v$sql
WHERE sql_text LIKE '%VENTA%'
ORDER BY elapsed_time DESC;

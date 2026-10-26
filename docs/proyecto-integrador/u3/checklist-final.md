# Checklist final del Proyecto Integrador

Los ítems que mencionan `Producto`, `Categoria`, `Venta` o `DetalleVenta` usan la nomenclatura del ejemplo BomERP; cada equipo los lee en clave de su propio módulo transaccional y no transaccional.

## 1. Producto integrado

| Ítem | Cumple | Evidencia |
|---|---|---|
| El producto resuelve el problema definido. |  |  |
| ADS, BD2 y LP2 trabajan sobre el mismo dominio. |  |  |
| Existe flujo full-stack demostrable. |  |  |
| La base Oracle soporta el flujo real del sistema. |  |  |
| La documentación está publicada y navegable. |  |  |

## 2. Diseño técnico

| Ítem | Cumple | Evidencia |
|---|---|---|
| C4 actualizado. |  |  |
| UML estructural y dinámico. |  |  |
| Patrones aplicados. |  |  |
| ADRs finales. |  |  |
| Matriz de trazabilidad. |  |  |

## 3. Base Oracle

| Ítem | Cumple | Evidencia |
|---|---|---|
| Usuarios, roles y privilegios. |  |  |
| Auditoría. |  |  |
| Optimización. |  |  |
| Backup. |  |  |
| Recovery. |  |  |
| Monitoreo y diagnóstico. |  |  |

## 4. Aplicación full-stack

| Ítem | Cumple | Evidencia |
|---|---|---|
| Backend REST funcional. |  |  |
| Frontend SPA funcional. |  |  |
| Existe un único Spring Boot ejecutable que ensambla los módulos del backend. |  |  |
| La SPA es única y está organizada mediante `core`, `shared` y funcionalidades. |  |  |
| Catálogo y Ventas están funcionales; Inventario, Compras y Seguridad tienen límites explícitos según su alcance. |  |  |
| Los módulos no comparten repositorios ni usan llamadas HTTP internas. |  |  |
| Seguridad backend/frontend. |  |  |
| CRUD maestro y dependiente. |  |  |
| Módulo no transaccional y módulo transaccional propios operativos (equivalentes a Producto–Categoria y Venta–DetalleVenta en BomERP). |  |  |
| Consultas y reportes de ventas protegidos. |  |  |
| Lazy Loading o Code Splitting con build optimizado. |  |  |
| Caché del navegador y decisión documentada sobre Redis. |  |  |
| Logging estructurado, endpoint de salud y monitoreo básico. |  |  |
| Paginación justificada de listados con alto volumen. |  |  |
| Auditoría con actor, fecha, acción y cambio relevante. |  |  |
| Pruebas end-to-end, regresión y estabilización. |  |  |
| Configuración por ambientes y guía de despliegue. |  |  |
| La documentación diferencia funcionalidad obligatoria, módulo preparado y ampliación opcional. |  |  |

## 5. Sustentación

| Ítem | Cumple | Evidencia |
|---|---|---|
| Demo preparada. |  |  |
| Pitch o introducción ejecutiva. |  |  |
| Defensa técnica por integrante. |  |  |
| Repositorio con topics. |  |  |
| MkDocs o equivalente actualizado. |  |  |
| Las correcciones y la estabilización concluyeron antes de S15. |  |  |

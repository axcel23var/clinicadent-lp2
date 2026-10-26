# ADR-004 - JWT se implementa recién en S10, no en U1

## Estado

Aprobada.

## Contexto

El sílabo de LP2 pide seguridad con JWT recién en la Unidad 2 (S10 en
adelante). Durante U1 (S1-S6) el backend expone endpoints REST del módulo
`catalogo` (y `ventas` desde S4) sin autenticación ni autorización.

Una alternativa sería adelantar JWT desde S1, protegiendo los endpoints
desde el inicio. Eso obligaría a construir infraestructura de seguridad
(filtros, tokens, roles) antes de tener claro qué recursos existen y qué
roles los necesitan — seguridad sin nada concreto que proteger todavía.

## Decisión

Los endpoints del backend quedan **sin autenticación durante toda la U1**
(S1-S6). JWT, roles y protección de endpoints se implementan recién en S10,
cuando ya existen los módulos `catalogo` y `ventas` funcionales sobre los
que definir permisos reales.

## Alternativas consideradas

| Alternativa | Por qué se descarta |
|---|---|
| Adelantar JWT desde S1 | Construye infraestructura de seguridad sin recursos ni roles concretos que proteger todavía; agrega fricción a cada sesión de U1 sin que el sílabo lo evalúe ahí. |
| Seguridad básica (Basic Auth) como paso intermedio antes de JWT | El sílabo no pide un paso intermedio; agregar y luego reemplazar Basic Auth es trabajo doble sin justificación pedagógica. |

## Consecuencias

- U1 expone endpoints sin autenticación — no es el estado final del
  proyecto, solo el de esta unidad.
- `BOMERP_APP` (usuario técnico de Oracle) ya existe desde S1 con
  privilegios mínimos, aunque el backend todavía no valide identidad de
  usuario final — son dos capas de seguridad distintas (acceso a datos vs.
  autenticación de usuario).
- S10 parte de módulos y endpoints ya estables, evitando rediseñar el
  contrato REST por causa de la capa de seguridad.

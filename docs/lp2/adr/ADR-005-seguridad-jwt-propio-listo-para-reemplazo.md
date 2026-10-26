# ADR-005 - S10 construye JWT propio (no Keycloak), con arquitectura lista para reemplazo en U3

## Estado

Aprobada, **parcialmente superada**. Esta ADR documenta correctamente la decisión y el diseño de S10 (JWT propio, "listo para reemplazo"). Lo que ya no refleja la realidad es el supuesto de que ese reemplazo quedaría "fuera de alcance... sin agendarse en ninguna sesión concreta": una decisión posterior adelantó la migración a Keycloak dentro de la propia Unidad 2, documentada en [S10b](../sesiones/S10b_Seguridad_Backend_Keycloak.md) (ejecución obligatoria, no opcional) y asumida como prerrequisito por [S11](../sesiones/S11_Seguridad_Frontend_Keycloak.md). Pendiente: una ADR nueva que formalice esa decisión — esta queda como registro histórico de por qué S10 se diseñó así en primer lugar.

## Contexto

El sílabo de LP2 pide para S10, explícitamente: "Seguridad backend: usuarios,
hash de contraseñas, autenticación JWT, roles, permisos y protección de
endpoints" (`docs/lp2/silabo_lp2_2026_2.md`) — es decir, **construir** la
pieza de seguridad, no delegarla a un tercero.

La práctica profesional actual tiende a delegar identidad a un proveedor
externo (Keycloak, Auth0, Amazon Cognito): custodia de credenciales fuera del
código propio, SSO (*Single Sign-On*) entre varias aplicaciones y login
social. Es una recomendación válida — y exactamente la que ya sigue
`proyecto-sello` (Sistema Universitario Integrado) con Keycloak como IAM
(*Identity and Access Management*) externo.

El curso paralelo de Aplicaciones Distribuidas (Sistemas Distribuidos, en
adelante DIST) enfrentó la misma decisión en su sesión S7
(`docs/sesiones/S07_Seguridad_Distribuida_Control_Acceso.md` del repositorio
`pagatu`). Su resolución: construir `pagatu-auth-ms`, un emisor de JWT
**propio y explícitamente temporal**, firmado con clave asimétrica (RS256),
con roles y claims nombrados exactamente igual que los que emite Keycloak
(`sub`, `realm_access.roles`), diseñado para que Keycloak lo reemplace
después cambiando una sola propiedad de configuración (`jwk-set-uri` →
`issuer-uri`), sin tocar el código de los microservicios que validan el
token. A la fecha de esta ADR, DIST ya entregó esa sesión (S7) y avanzó a S8
(mensajería); la sesión que reemplazaría `pagatu-auth-ms` por Keycloak (junto
al cliente Angular) todavía **no existe como guía escrita** — es una
dirección documentada (su Tabla 6), no un entregable construido.

LP2 además es un monolito modular por decisión explícita
([ADR-001](ADR-001-arquitectura-backend.md),
[ADR-002](ADR-002-spring-modulith.md)): no hay Gateway ni microservicios
separados, y el backend sigue siendo **un único proceso Spring Boot**
desplegable. Una primera versión de esta ADR asumió que, al ser un solo
proceso, el emisor y el validador del JWT podían compartir directamente el
mismo *bean* de clave en memoria, sin publicar nada por HTTP. Dos
correcciones, hechas durante el diseño de esta sesión, revisaron ese punto:

1. **Acoplamiento oculto del validador.** Inyectar el `JwtDecoder` directo
   desde un `@Bean RSAKey` ata `SecurityConfig` al código Java de
   `JwtKeyConfig` — reemplazar el emisor después exigiría tocar esa clase,
   no solo una propiedad, contradiciendo la promesa de "reemplazo barato"
   que esta misma ADR hace. La corrección: `seguridad` publica su clave
   pública en `/.well-known/jwks.json` (igual que `pagatu-auth-ms` en DIST),
   y `SecurityConfig` la consume por la propiedad
   `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`, apuntando hoy a
   `http://localhost:8080/.well-known/jwks.json` (la propia aplicación). Un
   IdP externo después solo exige cambiar esa URL.
2. **Dónde vive el dato de identidad.** Guardar `USUARIOS`/`ROLES` como un
   esquema más de Oracle (`BOM_SEGURIDAD`, junto a `BOM_CATALOGO` y
   `BOM_VENTAS`) enseña un modelo que ningún IdP real sigue: Keycloak (y
   `proyecto-sello`, que ya lo usa) nunca comparte motor de base de datos
   con la aplicación que protege — corre con su propio Postgres. Si
   `seguridad` naciera sobre Oracle, reemplazar el emisor después exigiría
   además migrar datos entre motores, la fricción exacta que esta ADR busca
   evitar.

## Decisión

S10 construye un módulo `seguridad` propio (paquete
`pe.edu.upeu.bomerp.seguridad`): usuarios y roles en tablas propias,
contraseñas con BCrypt, JWT firmado con clave asimétrica RS256 (misma
elección que DIST y por la misma razón: ningún punto de validación guarda
ningún secreto), roles `VENDEDOR`/`SUPERVISOR`/`ADMIN` ya diseñados por ADS
(S8 Tabla 10, RN5/RN8; S9 Tabla 8), endpoints de `catalogo` y `ventas`
protegidos por rol con Spring Security, y `vendedorId` tomado del claim
`sub` del JWT ya validado — nunca de un campo libre del request.

A diferencia de los demás módulos de BomERP, `seguridad` **no** persiste en
Oracle: vive en un Postgres propio (`bomerp-seguridad-db`, contenedor nuevo
en `compose-dev.yml`), con su propio `DataSource`/`EntityManagerFactory`
dentro del mismo proceso Spring Boot (dos pares explícitos — Oracle para
`catalogo`/`ventas`, Postgres para `seguridad` —, en vez del único datasource
autoconfigurado que ADR-002 describe para el resto del backend). El proceso
sigue siendo uno solo (ADR-001/ADR-002 no se tocan); lo que se divide es
dónde vive **el dato**, no cuántos ejecutables existen.

**No se integra Keycloak en S10**, ni se agenda su reemplazo en ninguna
sesión concreta de U3: el sílabo de S13-S16 no lo menciona. Queda como
dirección arquitectónica documentada, no como entregable — igual que en DIST
hoy.

"Listo para reemplazo" significa, en términos concretos (mismo criterio que
la Tabla 6 de DIST S07):

- Nombres de claim estándar: `sub` (identificador del usuario) y
  `realm_access.roles` (lista de roles) — el mismo formato que emite
  Keycloak.
- La clave de firma se genera en un `@Bean` propio (`JwtKeyConfig`), nunca
  hardcodeada, y su mitad pública se publica en `/.well-known/jwks.json` —
  nada depende de inyectar ese *bean* directamente.
- `SecurityConfig` valida contra esa URL por configuración
  (`jwk-set-uri`), no por una referencia de código a `JwtKeyConfig`. Es la
  **única** propiedad que cambiaría si el emisor fuera otro.
- `seguridad` persiste en su propio Postgres, nunca en el Oracle de
  `catalogo`/`ventas` — el mismo aislamiento de datos que tendría un IdP
  externo real, sin migración de datos pendiente si ese reemplazo llega.
- Ningún controller ni service de `catalogo` o `ventas` conoce JWT: solo
  reciben un rol ya resuelto por Spring Security (`hasRole`) o, cuando
  necesitan el identificador del usuario (RN5/RN8), un `Jwt` ya verificado
  vía `@AuthenticationPrincipal`.

Reemplazar el emisor después (por Keycloak u otro IdP) sería cambiar la
propiedad `jwk-set-uri` y apuntar los clientes a un *endpoint* de login
distinto — nunca recompilar ni tocar `catalogo` o `ventas`. El mismo
argumento que ya usa DIST (Tabla 6) para justificar su propio diseño.

## Alternativas consideradas

| Alternativa | Por qué se descarta |
|---|---|
| Integrar Keycloak directamente en S10 | El sílabo pide construir hash+JWT+roles, no delegarlos; además adelantaría contenido que el propio curso de Sistemas Distribuidos (más avanzado en este tema) todavía no ha construido como guía escrita — sin ninguna ADR ni sesión propia que lo valide primero. |
| Construir un servidor OAuth2 completo con Spring Authorization Server (`spring-boot-starter-oauth2-authorization-server`) | DIST ya evaluó esta opción (S07, 2.4) y la descartó con una razón que aplica igual aquí: implementar *client registration*, *consent*, *refresh tokens* y el resto del protocolo completo "equivale a reescribir Keycloak". Lo que hace "reemplazable" al emisor es que el **validador** lea la clave por una URL configurable, no que el **emisor** implemente el estándar entero — ya se logra sin este costo adicional. |
| Replicar el patrón de DIST con un servicio de autenticación aparte (`auth` como proceso independiente) | LP2 es un monolito modular por decisión explícita (ADR-001, ADR-002); separar `seguridad` en otro **proceso**, con su propio `pom.xml` y su propio puerto, contradice esa decisión sin que el sílabo lo pida ni exista el problema (varios procesos sin memoria compartida) que ese patrón resuelve en DIST. El aislamiento que sí hacía falta (identidad separada del resto) se logra separando el **dato** (Postgres propio), no el proceso. |
| Guardar `USUARIOS`/`ROLES` como un esquema más de Oracle (`BOM_SEGURIDAD`) | Es más simple (un solo motor de base de datos, sin contenedor nuevo), pero enseña un modelo que ningún IdP real sigue, y dejaría una migración de datos pendiente el día de un reemplazo real — contradice la razón de ser de esta ADR. |
| No dejar ninguna previsión de reemplazo, tratar el JWT propio como definitivo | Contradice sin necesidad la recomendación profesional válida de delegar identidad a un IdP externo (SSO multi-app, login social, custodia de credenciales): nombrar los claims en el formato estándar y aislar el dato hoy cuesta lo mismo que no hacerlo, y evita un retrabajo si el proyecto lo adopta más adelante. |

## Consecuencias

- Nuevo paquete `seguridad`, con sus propias tablas — pero en Postgres, no
  en Oracle: `docs/bd2/index.md` reserva `BOM_SEGURIDAD` como esquema
  Oracle del módulo, y esta ADR decide explícitamente **no** usarlo para
  persistencia; queda documentado aquí como la excepción al patrón de los
  demás módulos de BomERP.
- `compose-dev.yml` gana un segundo contenedor (`bomerp-seguridad-db`,
  Postgres) además de `bomerp-oracle` — el ambiente DEV deja de depender de
  un solo motor de base de datos.
- `bomerp-backend` deja de tener un único `DataSource` autoconfigurado
  (la simplificación que ADR-002 destacaba): gana dos pares explícitos de
  `DataSource`/`EntityManagerFactory`/`PlatformTransactionManager`, uno para
  Oracle (`catalogo`, `ventas`, y los módulos futuros que se sumen a ese
  mismo motor) y otro para Postgres (`seguridad`, exclusivamente). Es
  infraestructura que se configura una vez en S10 y no se vuelve a tocar.
- Nueva dependencia de runtime: `org.postgresql:postgresql`.
- `Venta` gana la columna `VENDEDOR_ID` (en Oracle, `BOM_VENTAS`), poblada
  únicamente por el servidor a partir del JWT (RN5 de ADS S8) — nunca
  existió como campo del request, así que no hay nada que "quitarle" al DTO
  de entrada (a diferencia de `idCliente` en DIST, que sí tuvo que
  removerse de `OrdenRequest`).
- La migración real a un IdP externo (Keycloak u otro) queda fuera de
  alcance de esta ADR: requiere su propia ADR y su propia sesión si el curso
  decide adoptarla, con el mismo criterio que ADR-004 ya fijó para diferir
  JWT a S10 — no se adelanta trabajo sin que una sesión concreta lo pida.
  El procedimiento concreto de ese reemplazo (descarga, configuración del
  realm, *protocol mapper* de `vendedorId` y cambios exactos en
  `bomerp-backend`) ya está documentado como guía de referencia, fuera del
  sílabo: [S10b_Seguridad_Backend_Keycloak.md](../sesiones/S10b_Seguridad_Backend_Keycloak.md).
- La propiedad `jwk-set-uri` (y, del lado del cliente, la URL de login) quedan
  como el único punto de cambio documentado si esa decisión futura se toma.

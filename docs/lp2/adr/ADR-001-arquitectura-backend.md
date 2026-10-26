# ADR-001 - Arquitectura del backend LP2: proyecto único por capas, no reactor multi-módulo

## Estado

Aprobada.

## Contexto

El sílabo de LP2 (`docs/lp2/silabo_lp2_2026_2.md`) pide un backend REST
"organizado por capas" con ORM, CRUD, objetos relacionados, una operación
cabecera-detalle, consultas/reportes, CORS, logs y pruebas (U1); luego una SPA
segura con JWT (U2); luego optimización, paginación, auditoría, e2e y
sustentación (U3). El sílabo **no** pide microservicios ni un reactor Maven
multi-módulo.

Un enfoque posible sería calcar la estructura de `producto-ms` (el reactor
Maven multi-módulo usado como referencia en Aplicaciones Distribuidas — ADS),
separando cada módulo de negocio (`catalogo`, `ventas`, `inventario`,
`compras`, `seguridad`) en su propio artefacto Maven (`bomerp-app`,
`bomerp-catalogo`, ...). Esa estructura tiene sentido en ADS porque ahí se
estudian despliegue distribuido y límites de versionado entre artefactos; en
LP2 el sílabo no evalúa nada de eso, así que replicarla aquí solo agregaría
fricción de build (orden de módulos, versionado entre `pom.xml`) sin ningún
beneficio real, porque todo se despliega igual como un único ejecutable.

## Decisión

Usar **un único proyecto Spring Boot (un solo `pom.xml`)**, organizado por
paquetes de módulo de negocio y, dentro de cada uno, por capas, en vez de un
reactor Maven multi-módulo:

```text
lp2/bomerp-backend/
├── pom.xml                          # un solo proyecto, no reactor
└── src/main/java/pe/edu/upeu/bomerp/
    ├── BomErpApplication.java
    ├── OpenApiConfig.java           # compartido, en el paquete raíz
    ├── catalogo/
    │   ├── categoria/{controller,dto,entity,repository,service}
    │   └── producto/{controller,dto,entity,repository,service}
    ├── ventas/                      # se agrega en S4
    └── seguridad/                   # se agrega en S10
```

- Regla de dependencia (la misma que impondría un reactor multi-módulo, sin
  su costo de build): `ventas` puede invocar el `ProductoService` público de
  `catalogo`, pero nunca su `ProductoRepository`.
- `inventario` y `compras` **no se crean como carpetas vacías**; se agregan
  recién cuando una sesión concreta les dé contenido, o quedan documentadas
  como fuera de alcance (ver `docs/proyecto-integrador/u3/lp2-producto.md`).
- La verificación mecánica de esta regla de dependencia se resuelve con
  Spring Modulith — ver [ADR-002](ADR-002-spring-modulith.md).
- Dentro de cada módulo, las capas siguen un flujo único:
  `controller` → `service` → `repository` → `entity`. Cada capa solo conoce
  la inmediatamente inferior; el controller nunca accede directo al
  repository, ni el service expone entidades JPA como respuesta HTTP (para
  eso existe el DTO). Esta separación simple, sin interfaces ni capas de
  indirección adicionales, alcanza para lo que pide el sílabo de LP2 en
  esta unidad.

## Alternativas consideradas

| Alternativa | Por qué se descarta |
|---|---|
| Reactor Maven multi-módulo (como `producto-ms`) | Paga el costo de separación (versionado, orden de build, límites entre `pom.xml`) sin ninguno de sus beneficios, porque todo se despliega como un único ejecutable. Ninguna sesión ni rúbrica del sílabo evalúa límites de módulo Maven. |
| Microservicios reales (uno por módulo) | Fuera del alcance explícito de LP2; ese aprendizaje corresponde a Aplicaciones Distribuidas (`producto-ms`, Eureka, Feign, Resilience4j). |
| Un solo paquete plano (`controller/`, `service/`, `repository/` globales) | Pierde el límite entre módulos de negocio (`catalogo` vs `ventas` vs `seguridad`) que sí es útil pedagógicamente y que el propio sílabo espera ver crecer sesión a sesión. |

## Consecuencias

- Menos fricción de build: un solo `pom.xml`, sin orden de reactor ni
  dependencias inter-módulo que declarar.
- Los límites de módulo siguen existiendo (como paquetes) y se pueden seguir
  evaluando en la rúbrica de cada sesión.
- Cada sesión que introduce un nuevo módulo de negocio (S4: `ventas`, S10:
  `seguridad`) agrega su paquete recién en ese momento, no antes.

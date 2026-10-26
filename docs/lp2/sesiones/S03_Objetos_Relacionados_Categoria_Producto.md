# S3 - Objetos Relacionados Categoria-Producto

*Por: Angel Sullon Macalupu @asullom - 2026*

## 1. Introducción

Tiempo: 20 min.

### 1.1 Presentación de la sesión

Un CRUD REST sobre una sola entidad, aislada, alcanza hasta que el dominio empieza a tener entidades relacionadas entre sí — la mayoría de los recursos reales de un ERP dependen de al menos otro (un producto pertenece a una categoría, una venta a un cliente, y así sucesivamente). Relacionar dos entidades trae preguntas nuevas que un CRUD aislado no enfrenta: cómo modelar la asociación en el ORM, qué forma darle al DTO relacionado que sale por la API, cómo navegar de una entidad a la otra sin cargar todo de una vez, cómo validar que la referencia recibida realmente exista, y cómo evitar que la relación produzca un ciclo infinito al serializar. Esta sesión construye esa asociación sobre las dos entidades del proyecto que ya están en esa situación.

### 1.2 Índice

1. Asociaciones entre entidades y DTO relacionados.
2. Validación de referencias.
3. Prevención de ciclos de serialización.
4. Navegación controlada y CRUD de la entidad relacionada.
5. Consultas eficientes entre entidades relacionadas.
6. Transacciones que cubren toda la operación.

### 1.3 Propósito de aprendizaje

Al concluir la clase, estarás en condiciones de:

- **Modelar y exponer** una asociación muchos a uno entre dos entidades JPA mediante un DTO relacionado, validando referencias inexistentes, evitando ciclos de serialización, y habilitando una navegación controlada entre los dos recursos, completando además el CRUD de la entidad relacionada.

### 1.4 Producto de sesión

API de `Categoria`-`Producto` con asociación ORM (`@ManyToOne`/`@JoinColumn`), `CategoriaResumen` embebido en `ProductoResponse`, validación de `categoriaId`, navegación controlada (`GET /api/v1/productos?categoriaId={id}`), listado optimizado con `@EntityGraph` (sin N+1), y CRUD completo de `Categoria` (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`).

### 1.5 Metodología

**Tabla 1. Metodología de la sesión**

| Actividades a Realizar en el Periodo | Orientaciones generales (Orientaciones Metodológicas) | Material de estudio recomendado |
|---|---|---|
| Revisión previa individual | Repasar S2 (`Producto` con CRUD completo). Revisar la definición real de `PRODUCTOS` en Oracle — `ID_CATEGORIA` y `FK_PRODUCTO_CATEGORIA` ya existen desde S1, esta sesión recién los usa. Repasar MapStruct (S2, 3.8). | S2, `docs/proyecto-integrador/u1/oracle/S01_02_tablas.sql`. |
| Clase presencial | Construcción guiada de la asociación `Categoria`-`Producto`: entidad, DTO relacionado, mapeo con MapStruct multi-fuente, validación de referencia, navegación controlada, consulta optimizada con `@EntityGraph` y CRUD completo de `Categoria`. Trabajo individual, siguiendo al docente paso a paso. | `pom.xml` (MapStruct ya configurado desde S2), backend ejecutable, cliente REST. |
| Evaluación formativa | Verificación en clase de `POST`/`PUT`/`DELETE` sobre `/api/v1/categorias`, la asociación reflejada en `/api/v1/productos`, el caso `categoriaId` inexistente (`404`), y `GET /api/v1/productos?categoriaId={id}`. La evidencia se completa y sustenta de forma individual, fuera del aula, según los criterios mínimos de la sección 4.4. | Indicaciones de entrega (4.3), rúbrica de evaluación (4.6). |

### 1.6 Motivación de la sesión

#### 1.6.1 Caso: relacionar sin exponer de más

Casi cualquier entidad de un dominio real pertenece a otra: un producto pertenece a una categoría, un empleado a un departamento, una factura a un cliente. Modelar esa relación como "muchos a uno" (muchas instancias de una entidad apuntan a una sola instancia de otra) parece trivial hasta que esa relación cruza la frontera de una API REST: hay que decidir qué tanto del dato relacionado exponer en el DTO de salida, qué hacer si el id relacionado que llega en una petición no corresponde a ningún registro real, y cómo evitar que, si la entidad relacionada también necesitara "ver" de vuelta hacia todas sus instancias relacionadas, eso termine en un ciclo infinito al serializar.

Ninguna de estas decisiones depende de si la relación "funciona" a nivel de base de datos — una llave foránea bien definida no las resuelve por sí sola. Son decisiones de diseño de API y de mapeo objeto-relacional, y esta sesión las resuelve una por una.

**Preguntas de análisis**

**Activación de conocimientos previos**

1. ¿Qué significa que una relación sea "muchos a uno"? Da un ejemplo distinto al de esta sesión.

**Comprensión de asociaciones ORM**

1. ¿Por qué el DTO de salida de una entidad no debería incluir la entidad relacionada completa, sino una versión reducida?

### 1.7 Ubicación en el curso

- Unidad: U1 - Base backend REST modular de BomERP.
- Producto del curso: base Full-Stack modular de BomERP.
- Producto de unidad: base backend modular de BomERP, con módulos de Catálogo, Inventario, Ventas y Compras delimitados.
- Avance del producto en esta sesión: `Categoria`-`Producto` asociados mediante ORM, con DTO relacionados, validación de referencias y navegación controlada; `Categoria` con CRUD completo.

Roadmap del producto de la unidad:

**Figura 1. Roadmap del producto de la unidad**

```mermaid
flowchart TB
    S1["`**S1:** Arquitectura backend REST profesional`"]
    S2["`**S2:** CRUD REST completo de Producto`"]
    S3["`**S3:** Objetos relacionados Categoria-Producto`"]
    S4["`**S4:** Operación cabecera-detalle`"]
    S5["`**S5:** Consultas y reportes`"]
    S6["`**S6:** Producto U1`"]

    S1 --> S2 --> S3 --> S4 --> S5 --> S6

    classDef today fill:#ffe08a,stroke:#9a6b00,stroke-width:2px,color:#111;
    class S3 today;
```

## 2. Explica

Tiempo: 25 min.

### 2.1 Arquitectura de la sesión

**Figura 2. Flujo de una petición que crea un `Producto` asociado a una `Categoria`**

```mermaid
flowchart LR
    Client["Cliente"]
    Controller["ProductoController"]
    Service["ProductoServiceImpl"]
    CatRepo["CategoriaRepository"]
    Mapper["ProductoMapper (MapStruct)"]
    Repo["ProductoRepository"]
    DB[("Oracle - PRODUCTOS.ID_CATEGORIA")]

    Client -->|"ProductoRequest{categoriaId}"| Controller
    Controller --> Service
    Service -->|"resolver y validar"| CatRepo
    CatRepo -->|"Categoria o excepción"| Service
    Service -->|"mapear"| Mapper
    Mapper --> Service
    Service --> Repo
    Repo --> DB
    Service --> Controller
    Controller -->|"ProductoResponse{categoria}"| Client
```

Lectura del diagrama: el service **resuelve** la categoría antes de mapear — el mapper nunca consulta la base de datos, solo transforma objetos que ya recibió resueltos. Esa separación es la misma de S2 (2.4, punto 4): el service es la única capa que valida algo que depende del estado actual del sistema, no de la forma del dato. Esta es la forma general del patrón — el paso a paso exacto de cuántas veces se llama a cada capa se construye en 3.9 (Figura 11), cuando toca escribir el código.

### 2.2 Asociación de entidades y DTO relacionados

**Asociación de entidades (ORM):** cuando dos entidades del dominio están relacionadas en la base de datos mediante una llave foránea, el ORM necesita una anotación que declare esa relación también en el lado Java — sin ella, la columna existe en la tabla pero ninguna consulta JPA la usa. La anotación para el lado "muchos" de una relación uno-a-muchos indica, además, qué columna física guarda la referencia.

**Figura 3. Esquema real en Oracle: `CATEGORIAS` y `PRODUCTOS`**

```mermaid
erDiagram
    CATEGORIAS o|--o{ PRODUCTOS : "FK_PRODUCTO_CATEGORIA"
    CATEGORIAS {
        NUMBER ID PK
        VARCHAR2 NOMBRE
        VARCHAR2 DESCRIPCION
    }
    PRODUCTOS {
        NUMBER ID PK
        NUMBER ID_CATEGORIA FK
        VARCHAR2 NOMBRE
        NUMBER PRECIO
        NUMBER STOCK
    }
```

Este esquema no es una propuesta de esta sesión — ya existe en Oracle desde S1 (`S01_02_tablas.sql`, 1.5), con `FK_PRODUCTO_CATEGORIA` protegiendo la integridad referencial desde el primer día. Lo que esta sesión hace es llevar al código Java una relación que la base de datos ya conocía (1.6.1) — el `@ManyToOne`/`@JoinColumn` de 3.6 es, literalmente, la traducción de esta misma flecha (`ID_CATEGORIA`) al lado de la aplicación.

El lado de `CATEGORIAS` lleva `o|` (cero o uno), no `||` (exactamente uno): `ID_CATEGORIA` en Oracle **no tiene `NOT NULL`** — un producto podría, a nivel de base de datos, no tener categoría. `@JoinColumn(nullable = false)` (3.6) sí exige que sea obligatorio, pero esa es una regla del lado Java, más estricta que lo que Oracle realmente permite — este esquema representa lo que la base de datos hace cumplir hoy, no lo que la aplicación decide exigir además.

**DTO relacionados:** cuando el DTO de salida de una entidad principal necesita mostrar información de su entidad relacionada, hay dos caminos: reutilizar el DTO que ya existe para esa entidad relacionada, o crear uno más chico, pensado solo para lo que se necesita mostrar embebido (por ejemplo, un combo o una etiqueta). El segundo camino no es obligatorio ni es, necesariamente, para ocultar datos sensibles — es una decisión de **desacoplar contratos**: que el DTO de salida de una entidad no cambie de forma solo porque el DTO de la entidad relacionada cambió por una razón que no tiene nada que ver con la primera.

**Ejemplo de referencia (LP2).** `@ManyToOne` declara "muchos `Producto` pueden apuntar a una `Categoria`"; `@JoinColumn(name = "ID_CATEGORIA")` le dice a Hibernate qué columna física de `PRODUCTOS` guarda esa referencia — la misma que ya existe en Oracle desde S1 (1.5). La relación es **unidireccional**: `Producto` conoce a su `Categoria`, pero `Categoria` no mantiene una lista de sus `Producto` como campo de la entidad. Eso no es una limitación, es una decisión — se retoma en 2.4. Del lado del DTO, en vez de reutilizar `CategoriaResponse` (que ya existe, con `id`, `nombre` y `descripcion`), esta sesión introduce `CategoriaResumen` — un `record` con solo `id` y `nombre` —, para que el contrato de `/productos` no dependa de todo lo que `CategoriaResponse` decida mostrar en el futuro. Como `descripcion` no es un dato sensible, la razón no es ocultarlo: es que el contrato de un producto no debería cambiar de forma solo porque el contrato de una categoría cambió por un motivo ajeno a los productos.

**Qué cambia en el código al introducir un DTO relacionado nuevo:**

1. Una clase nueva: `CategoriaResumen` (3.2), sin lógica propia — solo los campos que el cliente necesita ver embebidos.
2. `CategoriaMapper` gana un método nuevo, `toResumen(Categoria): CategoriaResumen` (3.3) — el mismo mapper que ya genera `toResponse`, una conversión más.
3. El campo `categoria` de `ProductoResponse` pasa a tener el tipo `CategoriaResumen`, no `CategoriaResponse` (3.7).
4. `ProductoMapper` no escribe esa conversión a mano: al declarar `uses = CategoriaMapper.class` (3.8), MapStruct encuentra `toResumen` automáticamente y lo usa para resolver `Producto.categoria` → `ProductoResponse.categoria`.
5. Nada cambia en la entidad `Categoria` ni en `CategoriaController`: el DTO relacionado es un asunto exclusivo de cómo `Producto` expone su categoría, no de cómo `Categoria` se expone a sí misma.

Esta cadena — DTO nuevo, mapper con un método más, mapper compuesto vía `uses` — se repite cada vez que una entidad necesita mostrar una versión reducida de otra; se construye paso a paso en 3.2, 3.3, 3.7 y 3.8.

**Figura 4. Diagrama de clases completo: entidades, DTO y mappers de `catalogo`**

```mermaid
classDiagram
    class Producto {
        -Long id
        -String nombre
        -BigDecimal precio
        -Integer stock
        -Categoria categoria
    }
    class Categoria {
        -Long id
        -String nombre
        -String descripcion
    }
    class ProductoRequest {
        -String nombre
        -BigDecimal precio
        -Integer stock
        -Long categoriaId
    }
    class ProductoResponse {
        -Long id
        -String nombre
        -BigDecimal precio
        -Integer stock
        -CategoriaResumen categoria
    }
    class CategoriaRequest {
        -String nombre
        -String descripcion
    }
    class CategoriaResponse {
        -Long id
        -String nombre
        -String descripcion
    }
    class CategoriaResumen {
        -Long id
        -String nombre
    }

    Producto "muchos" --> "uno" Categoria : @ManyToOne, unidireccional
    ProductoRequest ..> Producto : ProductoMapper.toEntity
    Producto ..> ProductoResponse : ProductoMapper.toResponse
    ProductoResponse --> CategoriaResumen : DTO relacionado
    CategoriaRequest ..> Categoria : CategoriaMapper.toEntity
    Categoria ..> CategoriaResponse : CategoriaMapper.toResponse
    Categoria ..> CategoriaResumen : CategoriaMapper.toResumen
```

La flecha sólida (`Producto` → `Categoria`) es la única asociación real entre entidades — mapeada a `ID_CATEGORIA` (Figura 3), unidireccional (se retoma en 2.4). Todas las demás flechas son punteadas porque no son asociaciones del dominio: son conversiones que hacen los mappers, en un solo sentido, entre un DTO y una entidad, o entre una entidad y otro DTO. `ProductoResponse` → `CategoriaResumen` es la única flecha entre dos DTO — el DTO relacionado explicado arriba, que existe solo para la API y ni siquiera tiene `descripcion`, aunque `Categoria` sí.

### 2.3 Validación de referencias

Que un campo llegue con un valor (`@NotNull`) no significa que ese valor corresponda a un registro real — validar que el id recibido exista en la base es responsabilidad de la capa de servicio, no de Bean Validation, que solo valida la forma del dato, nunca su existencia.

**Ejemplo de referencia (LP2).** `ProductoRequest` declara `categoriaId` con `@NotNull` — eso solo garantiza que el campo no llegue vacío, no que el id corresponda a una `Categoria` real. Esa segunda verificación es responsabilidad del service (mismo criterio de S2, 2.3, punto 4): antes de guardar o actualizar un `Producto`, `ProductoServiceImpl` busca la `Categoria` por id y lanza `ResourceNotFoundException` (ya creada en S2, 3.2.1) si no existe — el mismo manejador global responde `404`, sin código nuevo en `GlobalExceptionHandler`.

Esta búsqueda no es solo para tener el objeto `Categoria` que pide `@ManyToOne` — es lo que evita que un `categoriaId` inválido llegue a violar `FK_PRODUCTO_CATEGORIA` directamente en el `INSERT`. Si esa validación no existiera, Oracle igual rechazaría el dato (la restricción sigue ahí), pero el error llegaría como `DataIntegrityViolationException` sin traducir — exactamente el mismo hueco que `DELETE /api/v1/categorias/{id}` sí deja sin resolver hoy (3.4, "Error frecuente"). `crear`/`actualizar` responden `404` limpio; `eliminar` responde `500` crudo — la diferencia es solo esta validación explícita, no la restricción de la base de datos, que en ambos casos es la misma.

**Figura 5. Validación de referencias, paso a paso**

```mermaid
flowchart TD
    Req["ProductoRequest.categoriaId"]
    NotNull{"@NotNull cumplido?"}
    Bad["400 Bad Request"]
    Buscar["Service: buscarCategoriaOFallar(id)"]
    Existe{"Categoria existe<br/>en la base de datos?"}
    NotFound["404 Not Found"]
    Continuar["Continua: guardar Producto"]

    Req --> NotNull
    NotNull -->|"no"| Bad
    NotNull -->|"si"| Buscar
    Buscar --> Existe
    Existe -->|"no"| NotFound
    Existe -->|"si"| Continuar
```

`@NotNull` (Bean Validation) solo corta el camino si el campo llega vacío — nunca llega a preguntarle nada a la base de datos. La pregunta "¿existe de verdad?" es la segunda mitad del diagrama, y es exclusivamente responsabilidad del service.

### 2.4 Prevención de ciclos de serialización

Un problema clásico de JPA: si una entidad relacionada mantiene una colección de vuelta hacia la entidad principal (una relación bidireccional), y ambas se serializan directamente a JSON, cada instancia intentaría serializar su relación, que intentaría serializar su colección de vuelta, indefinidamente — un `StackOverflowError`. El riesgo se evita con dos decisiones tomadas antes de que el problema pueda aparecer: mantener la relación unidireccional (sin una colección de vuelta en la entidad relacionada) y no serializar entidades directamente, siempre a través de un DTO construido campo por campo.

**Ejemplo de referencia (LP2).** Esta sesión nunca llega a ese problema, por esas mismas dos decisiones:

1. **La relación es unidireccional** (2.2): `Categoria` no tiene un campo `List<Producto>`, así que no hay ciclo posible en el modelo de entidades.
2. **Ninguna entidad se serializa directamente**: el patrón Controller-Service-Mapper-DTO (S2, 2.1) ya garantiza que Jackson nunca ve una `Producto` ni una `Categoria` — siempre ve un `ProductoResponse`/`CategoriaResponse`, construidos a mano (o por MapStruct) campo por campo.

**Error frecuente**: agregar `@OneToMany` en `Categoria` "por si se necesita después" es exactamente el tipo de anticipación que este curso evita (ver `CLAUDE.md`, "no adelantar alcance") — y además reintroduce el riesgo de ciclo que esta sesión evitó a propósito. Si una sesión futura necesita navegar de `Categoria` a sus `Producto`, la forma correcta es una consulta explícita (2.5), no una colección cargada automáticamente en la entidad.

**Figura 6. Por qué no hay ciclo posible**

```mermaid
flowchart LR
    DTO["ProductoResponse"]
    P["Producto"]
    C["Categoria (sin lista de vuelta)"]

    DTO -.->|"nunca serializa la entidad"| P
    P -->|"@ManyToOne"| C
```

Las dos flechas de la Figura 6 son las dos decisiones de arriba: la sólida (`Producto` → `Categoria`, unidireccional) es la primera; la punteada (`ProductoResponse` nunca serializa la entidad) es la segunda. Ninguna de las dos puede retroalimentarse a sí misma — por eso no hay ciclo posible.

### 2.5 Navegación controlada y CRUD completo de `Categoria`

**Navegación controlada** significa: para ir de una entidad a las instancias de otra que la referencian, se expone una consulta explícita bajo demanda (un endpoint dedicado), nunca una colección que el ORM carga automáticamente cada vez que se lee la entidad principal — eso sería costoso si esa lista rara vez se necesita, y reintroduce el riesgo de ciclo evitado en 2.4.

**Ejemplo de referencia (LP2).** `GET /api/v1/productos?categoriaId={id}` es esa consulta explícita — no una colección cargada dentro de la entidad `Categoria`. Es un filtro sobre la colección de `Producto`, no un recurso anidado de `Categoria`: `Producto` tiene existencia propia (su propio CRUD completo, desde S2), no le pertenece a `Categoria` como una línea le pertenece a un pedido — por eso la consulta vive en `ProductoController`, no en `CategoriaController` (3.9). De paso, `Categoria` completa en esta sesión el mismo patrón CRUD que S2 aplicó a `Producto`: `CategoriaRequest` (entrada validada), `CategoriaResponse` (salida, ahora clase con `@Builder` en vez de `record` — mismo motivo que S2 documentó para `Producto`, 2.2), y `CategoriaMapper`.

**Figura 7. Navegación controlada: consulta explícita, no colección automática**

```mermaid
flowchart LR
    subgraph Evitado["Evitado: coleccion automatica en la entidad"]
        direction LR
        Cat1["Categoria"] -->|"@OneToMany (no existe)"| Lista["List~Producto~ cargada siempre que se lee Categoria"]
    end
    subgraph Real["Real: navegacion controlada"]
        direction LR
        Cliente["Cliente"] -->|"GET /productos?categoriaId=id"| ProdCtrl["ProductoController"]
        ProdCtrl --> ProdSvc["ProductoService.listarPorCategoria"]
        ProdSvc --> Repo["ProductoRepository.findByCategoriaId"]
    end
```

El bloque "Evitado" es la alternativa que 2.4 ya descartó por el riesgo de ciclo; acá se ve además su otro costo: cargaría la lista de productos **cada vez** que alguien lee una categoría, aunque nadie la necesite. El bloque "Real" solo consulta bajo demanda, cuando el cliente pide explícitamente ese filtro — y queda enteramente dentro de `ProductoController`/`ProductoService`, sin que `CategoriaController` participe (3.5, 3.9).

### 2.6 Consultas eficientes con relaciones: JPQL y `@EntityGraph`

Cuando una consulta trae una lista de entidades que a su vez tienen una relación `LAZY` (2.2), y algo en el camino (el mapper, el DTO) necesita el dato de esa relación para cada fila, Hibernate no lo trae todo de una vez — dispara una consulta adicional por cada fila. Esto se llama el problema **N+1**: una consulta para traer la lista (1), más una consulta extra por cada elemento de la lista (N) para resolver su relación. Con pocos registros no se nota; con miles, sí.

**JPQL** (*Jakarta Persistence Query Language*) es el lenguaje de consulta de JPA — se parece a SQL, pero opera sobre **entidades y sus campos** (`Producto`, `p.categoria`), no sobre tablas y columnas (`PRODUCTOS`, `ID_CATEGORIA`); Hibernate lo traduce a SQL real al ejecutar. La cláusula `JOIN FETCH` le dice a JPQL que traiga la entidad relacionada en la **misma** consulta, con un solo `JOIN` SQL, en vez de dejar que la relación se resuelva sola, más tarde, una vez por fila.

**`@EntityGraph`** hace lo mismo que un `JOIN FETCH`, pero de forma declarativa: en vez de escribir JPQL a mano, le dices a Spring Data JPA qué caminos (`attributePaths`) cargar junto con la entidad principal, y él arma la consulta optimizada por ti — sin tocar el service ni el mapper.

**Tabla 2. JPQL con `JOIN FETCH` vs. `@EntityGraph`**

| | JPQL con `JOIN FETCH` | `@EntityGraph` |
|---|---|---|
| Cómo se escribe | Consulta completa a mano, con `@Query` | Solo se declaran los caminos a cargar |
| Cuándo conviene | Consultas con condiciones o proyecciones propias | El método ya existe (`findAll`, `findById`) y solo falta evitar el N+1 |
| Resultado en SQL | Un `JOIN` explícito en la consulta que escribiste | Un `JOIN` que Spring Data genera por ti |

**Ejemplo de referencia (LP2).** `ProductoServiceImpl.listar()` (3.9) llama a `productoRepository.findAll()`, y el mapper después pide `categoria.getNombre()` para cada producto al construir su `CategoriaResumen` — una consulta extra por producto, el N+1 de arriba. Se soluciona en 3.10 agregando `@EntityGraph` al método, sin cambiar el service ni el mapper.

**Figura 8. Una sola consulta con `@EntityGraph` vs. N+1**

```mermaid
flowchart TB
    subgraph UnaSola["Una sola consulta: con EntityGraph"]
        direction TB
        Q3["1 consulta: SELECT ... FROM PRODUCTOS JOIN CATEGORIAS ON ..."]
    end
    subgraph NMasUno["N+1: findAll() sin EntityGraph"]
        direction TB
        Q1["1 consulta: SELECT * FROM PRODUCTOS"]
        Q2["N consultas: SELECT * FROM CATEGORIAS WHERE ID = ? (una por producto)"]
        Q1 --> Q2
    end
```

Con 1000 productos, la versión N+1 ejecuta 1001 consultas contra Oracle; la versión con `@EntityGraph` ejecuta una sola, con un `JOIN`. El resultado (los mismos datos) es idéntico — lo que cambia es cuántas veces se viaja a la base de datos, y eso es justo lo que le importa a "grandes volúmenes de datos".

**Ejemplo esperado en consola** (con dos productos de categorías distintas; el formato exacto puede variar según la versión de Hibernate, pero el número de sentencias es lo que importa).

**Con `@EntityGraph(attributePaths = "categoria")`** (la versión que se construye en 3.10) — `findAll()` se sobrescribe explícitamente:

```java
package pe.edu.upeu.bomerp.catalogo.producto.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Override
    @EntityGraph(attributePaths = "categoria")
    List<Producto> findAll();

    List<Producto> findByCategoriaId(Long categoriaId);
}
```

`productoRepository.findAll()` genera una sola consulta, con `JOIN`:

```text
select p1_0.id, c1_0.id, c1_0.descripcion, c1_0.nombre, p1_0.nombre, p1_0.precio, p1_0.stock
from productos p1_0
join categorias c1_0 on c1_0.id=p1_0.id_categoria
```

**Sin `@EntityGraph`** — `ProductoRepository` hereda `findAll()` de `JpaRepository` tal cual, sin sobrescribirlo:

```java
package pe.edu.upeu.bomerp.catalogo.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaId(Long categoriaId);
}
```

La misma llamada, `productoRepository.findAll()`, genera ahora una consulta por `Producto` y una más por cada `Categoria` que necesita resolver:

```text
select p1_0.id, p1_0.id_categoria, p1_0.nombre, p1_0.precio, p1_0.stock from productos p1_0
select c1_0.id, c1_0.descripcion, c1_0.nombre from categorias c1_0 where c1_0.id=?
select c1_0.id, c1_0.descripcion, c1_0.nombre from categorias c1_0 where c1_0.id=?
```

Mismos datos, mismas dos categorías resueltas — la diferencia es que la primera versión no vuelve a preguntarle a Oracle por cada fila. Verifica el número real de sentencias en tu propia consola al hacer 3.10 (`show-sql: true` ya está activo desde S1).

### 2.7 Transacciones: por qué `@Transactional` debe cubrir toda la operación

**Transacción**: una unidad de trabajo que Spring/Hibernate abre y cierra como un bloque — mientras está abierta, hay una sesión de Hibernate activa, capaz de resolver relaciones `LAZY` (2.2); en cuanto se cierra, cualquier proxy que haya quedado sin resolver ya no se puede completar.

Sin `@Transactional` en el método de servicio, cada llamada a un repositorio de Spring Data JPA abre y cierra su propia transacción por separado — el método en sí no tiene ninguna transacción propia que las una. Si ese método necesita leer una relación `LAZY` **después** de que el repositorio que la resolvió ya cerró su transacción, la sesión ya no existe para completarla.

**Regla práctica:** cualquier método de servicio que toca una relación `LAZY` y necesita leerla para construir la respuesta (por ejemplo, al mapear a un DTO) debe llevar `@Transactional` — de forma que la transacción cubra desde la primera consulta hasta que termina de construirse esa respuesta, no solo una llamada aislada al repositorio. Los métodos de solo lectura usan `@Transactional(readOnly = true)` (una optimización, ver 2.6); los que escriben (`crear`, `actualizar`, `eliminar`) usan `@Transactional` sin ese atributo.

**Ejemplo de referencia (LP2).** `ProductoServiceImpl.crear()` (3.9) hace tres cosas que deben ocurrir en la misma sesión: resolver la `Categoria` (`buscarCategoriaOFallar`), guardar el `Producto`, y mapear la respuesta (`toResponse`, que lee `categoria.getNombre()` para el `CategoriaResumen`, 2.2). Sin `@Transactional` en `crear()`, las dos primeras corren en transacciones separadas, y la tercera se queda sin sesión — este fue un error real, no hipotético, al probar esta sesión.

**Figura 9. Una transacción por método, no una por llamada**

```mermaid
flowchart TB
    subgraph SinTx["Sin @Transactional: tres sesiones separadas"]
        direction LR
        T1["Sesion 1: buscarCategoriaOFallar"] --> T2["Sesion 2: productoRepository.save"] --> T3["toResponse: sesion ya cerrada -> LazyInitializationException"]
    end
    subgraph ConTx["Con @Transactional: una sola sesion"]
        direction LR
        U["Una sesion: resolver categoria, guardar, mapear respuesta"]
    end
```

La mitad de arriba es lo que pasaba antes de corregirlo: tres transacciones cortas, cada una con su propia sesión — para cuando `toResponse` necesita `categoria.getNombre()`, ya no queda ninguna sesión abierta. La mitad de abajo es la corrección: una sola transacción, abierta desde el principio del método hasta que termina de construir la respuesta.

## 3. Aplica: actividad práctica guiada

Tiempo: 2h.

**Actividad:** construcción guiada de la asociación `Categoria`-`Producto` y el CRUD completo de `Categoria` (Producto de la sesión en 1.4).

**Propósito de la actividad:** asociar `Producto` a `Categoria` mediante ORM, con DTO relacionado, validación de referencia y navegación controlada — completando de paso el CRUD de `Categoria` — verificando cada incremento antes de continuar al siguiente.

**Orientaciones metodológicas:** en el laboratorio, el docente construye la asociación paso a paso frente a la clase, ejecutando cada prueba antes de avanzar; los estudiantes replican cada paso en su propio equipo.

**Actividades para realizar:**

- **3.1** Verificar el punto de partida.
- **3.2** Completar los DTO de `Categoria`.
- **3.3** Automatizar el mapeo de `Categoria`.
- **3.4** Habilitar la lógica de negocio de `Categoria`.
- **3.5** Exponer el CRUD de `Categoria` como API REST.
- **3.6** Asociar `Producto` con `Categoria` mediante el ORM.
- **3.7** Adaptar los DTO de `Producto` a la asociación.
- **3.8** Resolver el mapeo de la asociación con MapStruct.
- **3.9** Validar la referencia y habilitar la navegación controlada.
- **3.10** Optimizar la consulta de listado con `@EntityGraph`.
- **3.11** Verificar la asociación completa, de punta a punta.
- **3.12** Cubrir la asociación con pruebas automatizadas (opcional).
- **3.13** Relacionar con ADS y BD2.

### 3.1 Verificar el punto de partida

**Punto de partida común:** clona la rama de cierre de S2:

```bash
git clone --branch s02-crud-producto https://github.com/262ciclo4/bomerp.git
```

**Producto del paso:** confirmación de que `Producto` tiene CRUD completo (S2) y de que `Categoria` solo lista (`GET`).

**Requisito antes de continuar:** confirma que `http://localhost:8080/api/v1/productos` y `http://localhost:8080/api/v1/categorias` responden antes de tocar código nuevo.

### 3.2 Completar los DTO de `Categoria`

**Producto del paso:** `CategoriaRequest`, `CategoriaResumen`, y `CategoriaResponse` migrado de `record` a clase.

**`catalogo/categoria/dto/CategoriaRequest.java`**

```java
package pe.edu.upeu.bomerp.catalogo.categoria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaRequest {

    @NotBlank
    @Size(max = 80)
    private String nombre;

    @Size(max = 200)
    private String descripcion;
}
```

**`catalogo/categoria/dto/CategoriaResponse.java`** (reemplaza el `record` de S1):

```java
package pe.edu.upeu.bomerp.catalogo.categoria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
}
```

**`catalogo/categoria/dto/CategoriaResumen.java`** (nuevo — el DTO relacionado de 2.2, sin `descripcion`):

```java
package pe.edu.upeu.bomerp.catalogo.categoria.dto;

public record CategoriaResumen(Long id, String nombre) {
}
```

`CategoriaResumen` sigue siendo `record`: es un dato de salida simple, sin necesidad de `@Builder` ni mutabilidad — el mismo criterio que ya usaste en S1 para los DTO de solo lectura.

### 3.3 Automatizar el mapeo de `Categoria`

**Producto del paso:** mapper de `Categoria`, con MapStruct — el mismo camino que S2 (3.8, opcional) terminó adoptando de verdad para `ProductoMapper`, no la versión manual.

**`catalogo/categoria/mapper/CategoriaMapper.java`**

```java
package pe.edu.upeu.bomerp.catalogo.categoria.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaRequest;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResponse;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResumen;
import pe.edu.upeu.bomerp.catalogo.categoria.entity.Categoria;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    @Mapping(target = "id", ignore = true)
    Categoria toEntity(CategoriaRequest request);
    CategoriaResponse toResponse(Categoria categoria);
    CategoriaResumen toResumen(Categoria categoria);
}
```

Como `Categoria`, `CategoriaRequest`/`CategoriaResponse` y `CategoriaResumen` comparten los nombres `nombre`/`descripcion` (donde aplica), MapStruct genera `toResponse`/`toResumen` sin ninguna anotación `@Mapping` adicional. `toEntity` sí necesita una: `CategoriaRequest` no trae `id` (lo genera Oracle al insertar, S1), así que sin `@Mapping(target = "id", ignore = true)` MapStruct advierte `Unmapped target property: id` en cada build — el mismo caso que S2 (3.8) ya resolvió para `ProductoMapper`.

### 3.4 Habilitar la lógica de negocio de `Categoria`

**Producto del paso:** las cuatro operaciones CRUD, con el mismo patrón `buscarOFallar` de S2.

**`catalogo/categoria/service/CategoriaService.java`**

```java
package pe.edu.upeu.bomerp.catalogo.categoria.service;

import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaRequest;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResponse;
import java.util.List;

public interface CategoriaService {
    List<CategoriaResponse> listar();
    CategoriaResponse obtener(Long id);
    CategoriaResponse crear(CategoriaRequest request);
    CategoriaResponse actualizar(Long id, CategoriaRequest request);
    void eliminar(Long id);
}
```

**`catalogo/categoria/service/CategoriaServiceImpl.java`**

```java
package pe.edu.upeu.bomerp.catalogo.categoria.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaRequest;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResponse;
import pe.edu.upeu.bomerp.catalogo.categoria.entity.Categoria;
import pe.edu.upeu.bomerp.catalogo.categoria.mapper.CategoriaMapper;
import pe.edu.upeu.bomerp.catalogo.categoria.repository.CategoriaRepository;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(categoriaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return categoriaMapper.toResponse(buscarOFallar(id));
    }

    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = categoriaMapper.toEntity(request);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarOFallar(id);
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        categoriaRepository.delete(buscarOFallar(id));
    }

    private Categoria buscarOFallar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada: " + id));
    }
}
```

**Error frecuente (para probar, no para corregir en esta sesión):** si intentas `DELETE /api/v1/categorias/{id}` sobre una categoría que ya tiene productos asociados, Oracle rechaza el borrado por `FK_PRODUCTO_CATEGORIA` (`ORA-02292: integrity constraint violated - child record found`) — y `GlobalExceptionHandler` **todavía no maneja** `DataIntegrityViolationException` (S2, 2.3, ya lo dejó anotado como hallazgo pendiente). El cliente recibe un `500` genérico en vez de un `409`/`400` claro. Repórtalo como el "error o hallazgo" de tu evidencia (4.3.1) si te toca reproducirlo.

Nota la asimetría con `crear`/`actualizar` (2.3, 3.9): ahí `buscarCategoriaOFallar` valida **antes** de llegar a la base de datos y responde `404` limpio; acá nadie valida antes del `DELETE`, así que el mismo tipo de restricción (`FK_PRODUCTO_CATEGORIA`) produce un error crudo en un caso y uno controlado en el otro. No es que la FK proteja distinto — es que solo un lado tiene la validación explícita en el service.

### 3.5 Exponer el CRUD de `Categoria` como API REST

**Producto del paso:** los cinco endpoints del CRUD completo de `Categoria`.

**`catalogo/categoria/controller/CategoriaController.java`**

```java
package pe.edu.upeu.bomerp.catalogo.categoria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaRequest;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResponse;
import pe.edu.upeu.bomerp.catalogo.categoria.service.CategoriaService;
import java.util.List;

@Tag(name = "Categorías")
@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @Operation(summary = "Lista las categorías registradas")
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @Operation(summary = "Consulta una categoría por id")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtener(id));
    }

    @Operation(summary = "Registra una categoría nueva")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest request) {
        return categoriaService.crear(request);
    }

    @Operation(summary = "Actualiza una categoría existente")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    @Operation(summary = "Elimina una categoría")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
    }
}
```

`CategoriaController` depende solo de `CategoriaService` — nada de `producto` aparece acá. La navegación controlada (2.5) no vive en este controller: `Producto` tiene existencia propia, así que filtrarlo por categoría es una consulta sobre `/productos`, no un recurso anidado de `/categorias`. Se construye en `ProductoController`, dentro de 3.9.

Esta decisión evita a propósito el acoplamiento que tendría agregar un `listarProductos` acá (`CategoriaController` dependiendo de `ProductoService`, en sentido opuesto al que 3.9 sí agrega, de `producto` hacia `categoria` vía `ProductoServiceImpl` → `CategoriaRepository`, analizada en ADS S3, 3.4). No son dependencias bidireccionales: la única flecha real entre los dos paquetes va de `producto` hacia `categoria` — `categoria` nunca necesita conocer `producto`.

### 3.6 Asociar `Producto` con `Categoria` mediante el ORM

**Producto del paso:** `Producto.categoria`, mapeada a la columna `ID_CATEGORIA` que ya existe en Oracle desde S1 (1.5).

Así queda `catalogo/producto/entity/Producto.java` completo, con las dos líneas nuevas (`@ManyToOne`/`@JoinColumn`) agregadas al final de la clase, después de `stock`:

```java
package pe.edu.upeu.bomerp.catalogo.producto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.bomerp.catalogo.categoria.entity.Categoria;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCTOS", schema = "BOM_CATALOGO")
@Getter
@Setter
@NoArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 120)
    private String nombre;

    @Column(name = "PRECIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "STOCK", nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEGORIA", nullable = false)
    private Categoria categoria;
}
```

`fetch = FetchType.LAZY` evita que cada consulta de `Producto` traiga también su `Categoria` si nadie la va a usar — se carga recién cuando algo llama a `producto.getCategoria()` (por ejemplo, dentro del mapper, en 3.8).

### 3.7 Adaptar los DTO de `Producto` a la asociación

**Producto del paso:** `ProductoRequest` con `categoriaId`, `ProductoResponse` con `CategoriaResumen`.

Así queda `ProductoRequest.java` completo, con `categoriaId` agregado al final:

```java
package pe.edu.upeu.bomerp.catalogo.producto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductoRequest {

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precio;

    @NotNull
    @PositiveOrZero
    private Integer stock;

    @NotNull
    private Long categoriaId;
}
```

Así queda `ProductoResponse.java` completo, con `categoria` agregado al final (el import nuevo es `pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResumen`):

```java
package pe.edu.upeu.bomerp.catalogo.producto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResumen;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private CategoriaResumen categoria;
}
```

### 3.8 Resolver el mapeo de la asociación con MapStruct

**Producto del paso:** `ProductoMapper` recibe la `Categoria` ya resuelta como segundo parámetro, y compone con `CategoriaMapper` para el DTO relacionado.

```java
package pe.edu.upeu.bomerp.catalogo.producto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.bomerp.catalogo.categoria.entity.Categoria;
import pe.edu.upeu.bomerp.catalogo.categoria.mapper.CategoriaMapper;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoRequest;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoResponse;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;

@Mapper(componentModel = "spring", uses = CategoriaMapper.class)
public interface ProductoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", source = "request.nombre")
    @Mapping(target = "categoria", source = "categoria")
    Producto toEntity(ProductoRequest request, Categoria categoria);

    ProductoResponse toResponse(Producto producto);
}
```

`uses = CategoriaMapper.class` es la pieza nueva: cuando MapStruct necesita convertir el `Categoria` de `Producto.categoria` al `CategoriaResumen` de `ProductoResponse.categoria` (en `toResponse`), busca un método compatible entre los mappers declarados en `uses` — encuentra `CategoriaMapper.toResumen(Categoria): CategoriaResumen` y lo usa automáticamente, sin que escribas una sola línea para esa conversión. Así es como MapStruct resuelve "DTO relacionados" (2.2) en la práctica.

**Error frecuente real:** al escribir `toEntity(ProductoRequest request, Categoria categoria)` con dos parámetros, la primera compilación falla:

```text
[ERROR] .../ProductoMapper.java:[15,14] Several possible source properties for target property "nombre".
```

`ProductoRequest` y `Categoria` **ambos** tienen un campo `nombre` — MapStruct no puede adivinar de cuál de los dos parámetros viene el `nombre` de `Producto`. Con un solo parámetro fuente (como en S2) esto nunca pasa, porque no hay ambigüedad posible. La solución es la de arriba: `@Mapping(target = "nombre", source = "request.nombre")` desambigua explícitamente, calificando el origen con el nombre del parámetro (`request.nombre`, no solo `nombre`).

**Error frecuente real, más peligroso que el anterior porque no avisa de ninguna forma:** sin `@Mapping(target = "id", ignore = true)`, el código compila limpio, sin error ni advertencia — y aun así `Producto.id` queda mal asignado. La diferencia con `nombre` es justamente la ausencia de ambigüedad: `ProductoRequest` no tiene ningún campo `id`, así que MapStruct no ve dos candidatos entre los que dudar — encuentra un único origen que calza por nombre, `Categoria.getId()`, y lo asigna a `Producto.id` sin preguntar nada. Cada producto nuevo nace, en silencio, con el `id` de su categoría en vez de `null`. Como `Producto.id` usa `GenerationType.IDENTITY` (S1), Hibernate espera que una entidad nueva llegue con `id` en `null` para dejar que Oracle lo genere; con el `id` ya poblado por este error, Hibernate deja de tratarla como una entidad nueva y el `save()` falla — el mensaje de error aparece recién ahí, al guardar, lejos de la causa real (el mapeo), que es justamente lo que hace este error difícil de encontrar a simple vista.

`precio` y `stock` **no** necesitan `@Mapping`: solo existen en `request`, así que no hay ninguna ambigüedad que resolver — MapStruct los mapea solo. `categoria`, en cambio, sí necesita su propio `@Mapping`, pero por una razón distinta: no es un campo ambiguo, es un parámetro completo (`categoria`) que debe asignarse directo a un campo del mismo nombre en `Producto`. MapStruct no hace esa conexión automáticamente — sin `@Mapping(target = "categoria", source = "categoria")`, el compilador solo avisa con una advertencia (`Unmapped target property: categoria`), no con un error, y `producto.categoria` queda `null` en silencio.

**Figura 10. Por qué `nombre` es ambiguo, y cómo se resuelve**

```mermaid
flowchart TD
    PR["ProductoRequest.nombre"]
    Cat["Categoria.nombre"]
    Amb{"MapStruct: dos posibles origenes<br/>para Producto.nombre"}
    Fix["@Mapping(target='nombre', source='request.nombre')"]
    Target["Producto.nombre"]

    PR --> Amb
    Cat --> Amb
    Amb -->|"sin @Mapping: error de compilacion"| Fix
    Fix -->|"resuelto"| Target
```

La ambigüedad no es un error de MapStruct — es correcto que se detenga: con dos parámetros fuente y un campo `nombre` en ambos, no hay forma automática de saber cuál querías. `@Mapping` es la respuesta explícita a esa pregunta, campo por campo.

### 3.9 Validar la referencia y habilitar la navegación controlada

**Producto del paso:** `ProductoServiceImpl` resuelve y valida `categoriaId` antes de guardar; `listarPorCategoria` y el filtro en `ProductoController` para la navegación controlada de 2.5.

**Figura 11. `ProductoServiceImpl.crear()`, paso a paso — lo que el código de abajo implementa**

```mermaid
flowchart LR
    Req["ProductoRequest{categoriaId}"]
    CatRepo["CategoriaRepository"]
    Mapper["ProductoMapper"]
    Repo["ProductoRepository"]
    DB[("Oracle")]
    Resp["ProductoResponse{categoria}"]

    Req -->|"1. resolver y validar"| CatRepo
    CatRepo -->|"Categoria"| Mapper
    Req -->|"2. mapear a entidad<br/>toEntity(request, categoria)"| Mapper
    Mapper -->|"Producto"| Repo
    Repo -->|"3. guardar"| DB
    DB -->|"Producto guardado"| Mapper
    Mapper -->|"4. mapear a respuesta<br/>toResponse(producto)"| Resp
```

Esta es la versión exacta de la Figura 2 (2.1) — cuatro pasos, no dos, porque el mapper se usa dos veces: el paso 2 arma la entidad antes de guardar; el paso 4, después de guardar, arma la respuesta — y es ahí, en el paso 4, donde `CategoriaMapper.toResumen()` se compone automáticamente (vía `uses`, 3.8) para embeber el `CategoriaResumen`. El código de abajo implementa exactamente estos cuatro pasos, en este orden.

```java
package pe.edu.upeu.bomerp.catalogo.producto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.bomerp.catalogo.categoria.entity.Categoria;
import pe.edu.upeu.bomerp.catalogo.categoria.repository.CategoriaRepository;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoRequest;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoResponse;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import pe.edu.upeu.bomerp.catalogo.producto.mapper.ProductoMapper;
import pe.edu.upeu.bomerp.catalogo.producto.repository.ProductoRepository;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(productoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return productoMapper.toResponse(buscarOFallar(id));
    }

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = buscarCategoriaOFallar(request.getCategoriaId());
        Producto producto = productoMapper.toEntity(request, categoria);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarOFallar(id);
        Categoria categoria = buscarCategoriaOFallar(request.getCategoriaId());
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        productoRepository.delete(buscarOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorCategoria(Long categoriaId) {
        buscarCategoriaOFallar(categoriaId);
        return productoRepository.findByCategoriaId(categoriaId).stream().map(productoMapper::toResponse).toList();
    }

    private Producto buscarOFallar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }

    private Categoria buscarCategoriaOFallar(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada: " + categoriaId));
    }
}
```

**Error frecuente real:** si `crear`/`actualizar`/`eliminar` no llevaran `@Transactional` (como `listar`/`obtener`/`listarPorCategoria` sí tienen, con `readOnly = true`), cada llamada a un repositorio abriría y cerraría su propia sesión de Hibernate por separado. `buscarCategoriaOFallar` y `productoRepository.save` quedarían en sesiones distintas, y para cuando `productoMapper.toResponse` intenta leer `categoria.getNombre()` (para armar el `CategoriaResumen`, 2.2), la sesión que resolvió esa relación ya se cerró — `LazyInitializationException: could not initialize proxy ... no session`. `@Transactional` (sin `readOnly`, porque estas operaciones sí escriben) mantiene una sola sesión abierta durante todo el método, desde que se resuelve la categoría hasta que se construye la respuesta.

`listarPorCategoria` valida la categoría **antes** de consultar sus productos — así, pedir los productos de una categoría inexistente responde `404`, no una lista vacía que confundiría "categoría sin productos" con "categoría que no existe".

Así queda `ProductoRepository.java` completo, con `findByCategoriaId` agregado (todavía sin `@EntityGraph` — eso llega en 3.10):

```java
package pe.edu.upeu.bomerp.catalogo.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaId(Long categoriaId);
}
```

Así queda `ProductoService.java` completo, con `listarPorCategoria` agregado al final:

```java
package pe.edu.upeu.bomerp.catalogo.producto.service;

import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoRequest;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoResponse;
import java.util.List;

public interface ProductoService {
    List<ProductoResponse> listar();
    ProductoResponse obtener(Long id);
    ProductoResponse crear(ProductoRequest request);
    ProductoResponse actualizar(Long id, ProductoRequest request);
    void eliminar(Long id);
    List<ProductoResponse> listarPorCategoria(Long categoriaId);
}
```

Por último, actualiza `listar()` en `ProductoController` para aceptar el filtro opcional — así queda `ProductoController.java` completo, con el `listar()` modificado (`@RequestParam` nuevo) y el resto del CRUD de S2 sin cambios:

```java
package pe.edu.upeu.bomerp.catalogo.producto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoRequest;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoResponse;
import pe.edu.upeu.bomerp.catalogo.producto.service.ProductoService;
import java.util.List;

@Tag(name = "Productos")
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @Operation(summary = "Lista los productos registrados, opcionalmente filtrados por categoria")
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(@RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
        }
        return ResponseEntity.ok(productoService.listar());
    }

    @Operation(summary = "Consulta un producto por id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtener(id));
    }

    @Operation(summary = "Registra un producto nuevo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    @Operation(summary = "Actualiza un producto existente")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @Operation(summary = "Elimina un producto")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
```

Con esto, `GET /api/v1/productos?categoriaId={id}` es la navegación controlada de 2.5 — resuelta enteramente dentro de `producto`, sin que `CategoriaController` (3.5) necesite conocer `ProductoService`.

### 3.10 Optimizar la consulta de listado con `@EntityGraph`

**Producto del paso:** `ProductoRepository.findAll()` con `@EntityGraph`, evitando el N+1 de 2.6.

Agrega a `ProductoRepository`:

```java
package pe.edu.upeu.bomerp.catalogo.producto.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Override
    @EntityGraph(attributePaths = "categoria")
    List<Producto> findAll();

    List<Producto> findByCategoriaId(Long categoriaId);
}
```

`@Override` es necesario porque estás sobrescribiendo el `findAll()` que `JpaRepository` ya declara, solo para agregarle el `@EntityGraph` — el resto de la interfaz no cambia.

**Producto del paso (verificación):** confirmar en la consola que `listar()` ahora ejecuta una sola consulta con `JOIN`, no una por producto.

`application-dev.yml` ya tiene esta configuración desde S1 — no hace falta agregar nada, solo confirmarla y mirar la consola:

```yaml
spring:
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
    show-sql: true
```

`show-sql: true` es lo que imprime cada sentencia SQL real en la consola; `format_sql: true` la imprime formateada (varias líneas, indentada), más fácil de leer que una sola línea larga. Llama a `GET /api/v1/productos` con al menos dos productos de categorías distintas y confirma: **una sola consulta** con `join categorias` en el `FROM`, no varias consultas repetidas `select ... from categorias where id=?`, una por producto.

### 3.11 Verificar la asociación completa, de punta a punta

**Producto del paso:** evidencia de la asociación funcionando end-to-end.

PowerShell:

```powershell
# Crear categoria (201) - anota el id devuelto
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/categorias" -ContentType "application/json" -Body '{"nombre":"Electrodomesticos","descripcion":"Linea blanca y pequenos electrodomesticos"}'

# Crear producto asociado a esa categoria (201) - reemplaza {categoriaId}
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/productos" -ContentType "application/json" -Body '{"nombre":"Licuadora","precio":150.00,"stock":10,"categoriaId":{categoriaId}}'

# Listar productos de esa categoria (200)
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/v1/productos?categoriaId={categoriaId}"

# Caso invalido: categoriaId inexistente (404)
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/productos" -ContentType "application/json" -Body '{"nombre":"Licuadora","precio":150.00,"stock":10,"categoriaId":999999}'
```

bash macOS/Linux:

```bash
# Crear categoria (201) - anota el id devuelto
curl -i -X POST http://localhost:8080/api/v1/categorias -H "Content-Type: application/json" -d '{"nombre":"Electrodomesticos","descripcion":"Linea blanca y pequenos electrodomesticos"}'

# Crear producto asociado a esa categoria (201) - reemplaza {categoriaId}
curl -i -X POST http://localhost:8080/api/v1/productos -H "Content-Type: application/json" -d '{"nombre":"Licuadora","precio":150.00,"stock":10,"categoriaId":{categoriaId}}'

# Listar productos de esa categoria (200)
curl -i "http://localhost:8080/api/v1/productos?categoriaId={categoriaId}"

# Caso invalido: categoriaId inexistente (404)
curl -i -X POST http://localhost:8080/api/v1/productos -H "Content-Type: application/json" -d '{"nombre":"Licuadora","precio":150.00,"stock":10,"categoriaId":999999}'
```

**Tabla 3. Verificación de la asociación antes de continuar**

| Caso | Método | Resultado esperado |
|---|---|---|
| Crear categoría válida | `POST /api/v1/categorias` | `201 Created` |
| Crear producto con `categoriaId` válido | `POST /api/v1/productos` | `201 Created`, cuerpo con `categoria.nombre` |
| Listar productos de una categoría | `GET /api/v1/productos?categoriaId={id}` | `200 OK`, lista con el producto creado |
| `categoriaId` inexistente en `POST`/`PUT` de producto | cualquiera | `404`, cuerpo con `error: "Not Found"` |
| Categoría inexistente en `GET /productos?categoriaId=...` | `GET` | `404` (no una lista vacía) |
| Eliminar categoría con productos asociados | `DELETE /api/v1/categorias/{id}` | `500` (hallazgo conocido, 3.4) |

### 3.12 Cubrir la asociación con pruebas automatizadas (opcional)

!!! note "3.12 es opcional"
    Si el tiempo de clase no alcanza, la sesión cierra igual sin este paso — la asociación ya quedó verificada manualmente en 3.11. Completarlo suma como evidencia adicional, pero no es requisito para cerrar la sesión ni se evalúa en la rúbrica (4.6).

**Producto del paso:** `CategoriaControllerTest`, y `ProductoControllerTest` (S2) actualizado — `ProductoRequest` ahora exige `categoriaId`, así que el caso "datos válidos" de S2 necesita incluirlo o pasa a fallar con `400`; y `ProductoControllerTest` suma los dos casos de navegación (2.5, 3.9), porque el filtro vive en `ProductoController`, no en `CategoriaController`.

Actualiza el `crear_conDatosValidos_respondeCreated` de S2 (`ProductoControllerTest`) agregando `request.setCategoriaId(1L);` antes de enviarlo, y agrega estos casos nuevos:

```java
@Test
void crear_conCategoriaIdNulo_respondeBadRequestSinLlegarAlService() throws Exception {
    ProductoRequest request = new ProductoRequest();
    request.setNombre("Teclado mecánico");
    request.setPrecio(new BigDecimal("180.50"));
    request.setStock(25);
    // sin categoriaId

    mockMvc.perform(post("/api/v1/productos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
}

@Test
void listar_conCategoriaIdExistente_respondeOkFiltrado() throws Exception {
    when(productoService.listarPorCategoria(1L)).thenReturn(List.of(
            ProductoResponse.builder().id(10L).nombre("Teclado mecánico").precio(new BigDecimal("180.50")).stock(25).build()
    ));

    mockMvc.perform(get("/api/v1/productos").param("categoriaId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(10));
}

@Test
void listar_conCategoriaIdInexistente_respondeNotFound() throws Exception {
    when(productoService.listarPorCategoria(999L)).thenThrow(new ResourceNotFoundException("Categoria no encontrada: 999"));

    mockMvc.perform(get("/api/v1/productos").param("categoriaId", "999"))
            .andExpect(status().isNotFound());
}
```

**`src/test/java/pe/edu/upeu/bomerp/catalogo/categoria/controller/CategoriaControllerTest.java`** (mismo patrón que `ProductoControllerTest` de S2 — sin `ProductoService`, `CategoriaController` no lo necesita):

```java
package pe.edu.upeu.bomerp.catalogo.categoria.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaRequest;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResponse;
import pe.edu.upeu.bomerp.catalogo.categoria.service.CategoriaService;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoriaService categoriaService;

    @Test
    void crear_conDatosValidos_respondeCreated() throws Exception {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Electrodomesticos");
        request.setDescripcion("Linea blanca y pequenos electrodomesticos");

        when(categoriaService.crear(any())).thenReturn(
                CategoriaResponse.builder().id(1L).nombre("Electrodomesticos").descripcion("Linea blanca y pequenos electrodomesticos").build()
        );

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_conNombreVacio_respondeBadRequest() throws Exception {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("");

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtener_conIdInexistente_respondeNotFound() throws Exception {
        when(categoriaService.obtener(999L)).thenThrow(new ResourceNotFoundException("Categoria no encontrada: 999"));

        mockMvc.perform(get("/api/v1/categorias/999"))
                .andExpect(status().isNotFound());
    }
}
```

Ejecuta las pruebas:

```powershell
.\mvnw.cmd test
```

```bash
./mvnw test
```

Deberías ver `ModularityTests`, `ProductoControllerTest` y `CategoriaControllerTest` en verde — `ModularityTests` sigue pasando porque `categoria` y `producto` son paquetes dentro del mismo módulo `catalogo`, no módulos distintos; la regla que Spring Modulith verifica es entre módulos de negocio (`catalogo` vs. `ventas`, por ejemplo), no dentro de uno.

### 3.13 Relacionar con ADS y BD2

**Producto del paso:** matriz de integración actualizada.

**Tabla 4. Matriz de integración LP2-ADS-BD2 (S3)**

| Endpoint LP2 | Componente ADS | Objeto BD2 |
|---|---|---|
| `POST /api/v1/productos` (con `categoriaId`) | Asociación entre entidades del modelo de dominio (ADS) | `FK_PRODUCTO_CATEGORIA` sobre `PRODUCTOS.ID_CATEGORIA` (BD2 S1) |
| `GET /api/v1/productos?categoriaId={id}` | Navegación controlada entre componentes (ADS) | Consulta con `JOIN` implícito vía `ID_CATEGORIA` |
| `DELETE /api/v1/categorias/{id}` | — | `FK_PRODUCTO_CATEGORIA` como restricción de integridad referencial (BD2 S1) |

Sesión equivalente en los otros dos cursos, misma semana: [ADS - S3 Diseño Estructural y Principios SOLID](../../ads/sesiones/S03_Diseno_Estructural_Principios_SOLID.md) y [BD2 - S3 Manejo de Excepciones y Robustez](../../bd2/sesiones/S03_Excepciones_Robustez.md).

**Evidencia de aprendizaje:**

- `Producto` asociado a `Categoria` mediante `@ManyToOne`/`@JoinColumn`, con `CategoriaResumen` embebido en `ProductoResponse`.
- Validación de `categoriaId` inexistente probada (`404`).
- `GET /api/v1/productos?categoriaId={id}` funcionando, con el caso de categoría inexistente probado.
- `ProductoRepository.findAll()` con `@EntityGraph`, verificado en consola con una sola consulta (sin N+1).
- `Categoria` con CRUD completo (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`).
- `CategoriaControllerTest` en verde; `ProductoControllerTest` de S2 actualizado y en verde.

## 4. Crea: actividad autónoma

Tiempo: 2h fuera del aula.

### 4.1 Actividad

Replicación autónoma de una asociación `@ManyToOne` entre la entidad principal del dominio del equipo y su entidad de clasificación relacionada, documentada en evidencia individual.

Completa y evidencia estas tareas:

1. Agregar la asociación `@ManyToOne`/`@JoinColumn` en tu entidad principal, hacia su entidad de clasificación.
2. Agregar el DTO relacionado (equivalente a `CategoriaResumen`) embebido en el DTO de salida de tu entidad principal.
3. Validar que el id de la entidad relacionada exista antes de crear/actualizar, respondiendo `404` si no.
4. Implementar una navegación controlada (equivalente a `GET /api/v1/productos?categoriaId={id}`) sobre tu propio dominio — como filtro del recurso principal, no como recurso anidado del relacionado.
5. Completar el CRUD de tu entidad de clasificación, si todavía no lo tenía.
6. Probar al menos un caso válido y uno inválido de la asociación.

### 4.2 Propósito

Que cada estudiante demuestre, de forma individual y fuera del aula, que puede reproducir el patrón de asociación ORM y DTO relacionados construido en clase sin el acompañamiento del docente.

### 4.3 Indicaciones

Entrega un PDF con el siguiente nombre:

```text
S03_LP2_Equipo##_ApellidoNombre.pdf
```

Cada captura de pantalla del informe debe mostrar, sin recortar, el reloj del sistema (fecha y hora) y tu usuario o foto de perfil (Windows, VS Code o navegador) visibles en pantalla.

#### 4.3.1 Estructura del informe

**Datos del estudiante**

- Nombre:
- Equipo:
- Sesión: S03 - Objetos Relacionados Categoria-Producto
- Rol o aporte realizado:
- Link de GitHub:

**Evidencia técnica**

Incluye capturas o salidas de consola con una breve explicación debajo de cada una, organizadas en los mismos 4 bloques de la rúbrica (4.6):

1. *Asociación ORM y DTO relacionados*
    - Entidad con `@ManyToOne`/`@JoinColumn` y DTO relacionado embebido en la respuesta.
2. *Validación de referencias*
    - Caso de id relacionado inexistente respondiendo `404`.
3. *Navegación controlada*
    - Endpoint que lista los elementos de la entidad principal a partir de su entidad relacionada.
4. *CRUD de la entidad de clasificación*
    - `Categoria` (o tu equivalente) con CRUD completo funcional.

**Error o hallazgo**

Describe un error o hallazgo: ambigüedad de mapeo, referencia inválida no controlada, o restricción de integridad de la base de datos no manejada.

**Reflexión técnica breve**

Responde en 5 a 8 líneas:

```text
¿Por qué la relación entre tu entidad principal y su entidad de clasificación
es unidireccional, y qué problema evita esa decisión?
```

**Anexo: Feedback de la sesión**

Pega esta página como la última hoja del PDF, con tus respuestas.

1. ¿Cuál es el aprendizaje más importante que te llevas de la clase de hoy?
2. ¿Qué punto de la clase te resultó más confuso o te dejó con dudas?
3. ¿Tienes alguna pregunta que te gustaría que sea respondida la siguiente clase?
4. Sobre tu nivel de comprensión de la clase de hoy, marca una opción:
    - ¡Entendido! - Lo domino y podría explicarlo.
    - Más o menos. - Entendí la idea general, pero tengo dudas.
    - Necesito ayuda. - Me siento perdido/a con este tema.
5. ¿Cómo puedo ayudarte a comprender mejor el tema?
6. Pensando en tu participación y esfuerzo en la clase de hoy, ¿cómo te autoevaluarías? Marca una opción:
    - Muy Comprometido/a: Me esforcé al máximo.
    - Comprometido/a: Sé que podría haberme esforzado un poco más.
    - Poco Comprometido/a: Hoy no di mi mejor esfuerzo.
7. Mi satisfacción con la clase fue... (califica del 1 al 10, donde 1 es insatisfecho y 10 es muy satisfecho).

### 4.4 Criterios mínimos de aceptación

La evidencia individual se considera completa si:

- El archivo respeta el nombre solicitado.
- La entidad principal tiene una asociación `@ManyToOne`/`@JoinColumn` funcional hacia su entidad de clasificación.
- El DTO de salida embebe un DTO relacionado (no la entidad completa ni solo un id suelto).
- Valida que el id relacionado exista, respondiendo `404` si no.
- Implementa un endpoint de navegación controlada, probado con al menos un caso válido.
- Completa el CRUD de la entidad de clasificación (o confirma que ya estaba completo).
- Cada captura de la evidencia técnica muestra el reloj del sistema y el usuario/perfil visible, sin recortar.
- Las fechas y horas de las capturas son coherentes con el historial de commits de su repositorio en GitHub.
- Incluye un error o hallazgo técnico diagnosticado.
- Incluye la reflexión técnica breve solicitada.
- Incluye el Anexo de feedback de la sesión respondido, como última página del PDF.

### 4.5 Preguntas de defensa

1. ¿Por qué `ProductoResponse` embebe un `CategoriaResumen` y no la entidad `Categoria` completa?
2. ¿Qué garantiza que esta sesión nunca produzca un ciclo de serialización, aunque no uses `@JsonIgnore` en ningún lado?
3. ¿Por qué el mapper no consulta la base de datos para resolver `categoriaId`, y quién lo hace en su lugar?
4. ¿Qué significa "navegación controlada" y en qué se diferencia de un `@OneToMany` cargado automáticamente?
5. ¿Por qué `ModularityTests` sigue pasando aunque `producto` ahora dependa de `categoria`? ¿Y por qué la navegación filtrada quedó en `ProductoController`, y no en `CategoriaController`?

### 4.6 Rúbrica de evaluación

**Tabla 5. Rúbrica de evaluación**

| Criterio | Peso (%) | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Nivel obtenido |
|---|---:|---|---|---|---|---:|
| 1. Asociación ORM y DTO relacionados* | 25 | Asociación `@ManyToOne` correcta, con DTO relacionado bien diseñado (sin sobre-exponer ni sub-exponer datos). | Asociación y DTO relacionado funcionales, con detalles menores. | Asociación incompleta o DTO relacionado ausente. | No implementa la asociación. | |
| 2. Validación de referencias* | 25 | Valida la referencia con `404` claro, con evidencia del caso inválido ejecutado. | Valida la referencia correctamente, con evidencia parcial. | Validación parcial o inconsistente. | No valida la referencia. | |
| 3. Navegación controlada* | 25 | Endpoint de navegación funcional, con evidencia de ejecución, incluido el caso "entidad relacionada inexistente". | Endpoint funcional, sin ese caso cubierto. | Navegación incompleta o poco clara. | No implementa navegación controlada. | |
| 4. CRUD de la entidad de clasificación* | 25 | CRUD completo, con evidencia de ejecución de la entidad de clasificación. | CRUD funcional, con evidencia parcial. | CRUD incompleto. | No completa el CRUD. | |

\* Agregado manual.

Nota final = suma de (`Peso` / 100 × `Puntos del nivel obtenido`) = ____ / 20.

Para usar la rúbrica con IA, solicita:

```text
Evalúa el PDF usando la rúbrica de la sesión.
Para cada criterio selecciona el nivel obtenido usando la escala A=20, B=15, C=10, D=5 puntos.
Justifica brevemente cada nivel asignado.
Verifica que cada captura muestre reloj del sistema y usuario/perfil visible, y que las fechas sean coherentes con el historial de commits de GitHub. Si falta esta evidencia o hay inconsistencias, indícalo explícitamente antes de calificar.
Calcula la nota final con la fórmula: suma de (Peso/100 × Puntos del nivel obtenido), directamente sobre 20.
Indica 2 fortalezas y 2 recomendaciones.
```

## 5. Cierre

Tiempo: 5 min.

**Resumen breve:** hoy `Producto` y `Categoria` se asociaron de verdad, usando una columna (`ID_CATEGORIA`) que existía en Oracle desde S1 pero que ningún código Java había mapeado hasta hoy — con un DTO relacionado (no la entidad completa), validación de la referencia, navegación controlada explícita, y `Categoria` completando su propio CRUD.

**Dinámica participativa:** en una ronda rápida, cada estudiante comparte qué le pareció más contraintuitivo: que la relación exista en la base de datos sin estar en el código, o que evitar el ciclo de serialización no requiriera ninguna anotación especial.

**Metacognición:** cada estudiante responde el Anexo de feedback de la sesión, incluido en su evidencia individual (ver 4.3.1).

**Proyección:** el patrón de asociación y DTO relacionado de hoy se repite en S4, cuando `Venta`-`DetalleVenta` se relacionen entre sí y con `Producto` (para descontar stock) — con la diferencia de que ahí la operación completa (cabecera + detalles) debe ser atómica, no solo una referencia válida.

## Bibliografía

1. Spring. (2024). *Spring Data JPA reference documentation*. VMware. https://docs.spring.io/spring-data/jpa/reference/
2. Hibernate. (2024). *Hibernate ORM User Guide - Association Mappings*. Red Hat. https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#associations
3. MapStruct. (2024). *MapStruct Reference Guide*. https://mapstruct.org/documentation/stable/reference/html/

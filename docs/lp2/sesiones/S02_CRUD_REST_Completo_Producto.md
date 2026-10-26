# S2 - CRUD REST Completo de Producto

*Por: Angel Sullon Macalupu @asullom - 2026*

## 1. Introducción

Tiempo: 20 min.

### 1.1 Presentación de la sesión

En la sesión 1, el recurso principal solo exponía un listado de solo lectura. Esta sesión lo convierte en un recurso REST completo: crear, consultar uno por id, actualizar y eliminar, con un DTO de entrada validado, mapeo explícito entre capas y manejo global de errores y trazabilidad de peticiones (construidos en 3.2 de esta misma sesión), puesto a prueba con casos reales de validación y de recurso no encontrado.

### 1.2 Índice

1. Excepciones y filtro de trazabilidad.
2. Entidad, repositorio, DTO y mapeo.
3. Servicio de aplicación con las cuatro operaciones CRUD.
4. Validación de entrada con Bean Validation.
5. Controlador REST completo, manejo de errores aplicado y pruebas automatizadas.

### 1.3 Propósito de aprendizaje

Al concluir la clase, estarás en condiciones de:

- **Construir y probar** un recurso REST completo (crear, consultar, actualizar, eliminar), con DTO de entrada validado, mapeo explícito entidad-DTO mediante una clase dedicada, manejo global de errores verificado con casos reales, y trazabilidad por petición mediante logs.

### 1.4 Producto de sesión

API REST completa de `Producto` (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`), con entidad, repositorio, DTO de entrada (`ProductoRequest`) y de salida (`ProductoResponse`), un `ProductoMapper` dedicado, validaciones con Bean Validation, y manejo de errores probado con casos válidos e inválidos.

### 1.5 Metodología

**Tabla 1. Metodología de la sesión**

| Actividades a Realizar en el Periodo | Orientaciones generales (Orientaciones Metodológicas) | Material de estudio recomendado |
|---|---|---|
| Revisión previa individual | Si no completaste el 3.4.2 opcional de S1, revisa esa sección antes de clase (`Producto` con `listar()` debe existir). Repasar Bean Validation (`@NotBlank`, `@Size`, `@NotNull`). Trabajo individual, antes de clase. | S1 (3.4.2), documentación de Jakarta Bean Validation. |
| Clase presencial | Construcción guiada del CRUD REST completo de `Producto`: DTO, mapper, servicio y controlador, con casos de prueba válidos e inválidos. Trabajo individual en la propia laptop, siguiendo al docente paso a paso; consulta inmediata ante errores de compilación o de mapeo. | `pom.xml` ya configurado (S1), backend ejecutable, cliente REST para verificar endpoints. |
| Evaluación formativa | Verificación en clase de `POST`/`GET`/`PUT`/`DELETE` sobre `/api/v1/productos`, incluidos los casos `400` (validación) y `404` (no encontrado). La evidencia se completa y sustenta de forma individual, fuera del aula, según los criterios mínimos de la sección 4.4. | Indicaciones de entrega (4.3), rúbrica de evaluación (4.6). |

### 1.6 Motivación de la sesión

#### 1.6.1 Caso: el catálogo que nadie podía corregir

En la sesión 1 bastaba con listar productos para verificar que el backend funcionaba. Pero un catálogo real no se mantiene solo — el equipo de compras necesita registrar productos nuevos, corregir un precio mal cargado, o dar de baja un producto discontinuado. Sin esas operaciones, `ventas` (sesión 4) no tendría de dónde tomar un precio o un stock reales para descontar. Esta sesión completa ese ciclo de vida para `Producto`.

**Preguntas de análisis**

**Activación de conocimientos previos**

1. En la sesión 1, `CategoriaResponse`/`ProductoResponse` eran `record`. ¿Por qué esta sesión los convierte en clases con `@Builder`?
2. Si dos peticiones actualizan el mismo producto casi al mismo tiempo, ¿qué garantiza JPA (y qué no garantiza) sobre cuál gana?

**Comprensión de CRUD REST**

1. ¿Por qué el DTO de entrada (`ProductoRequest`) es una clase distinta del de salida (`ProductoResponse`), en vez de reutilizar uno solo?
2. ¿Qué responde la API si se intenta actualizar o eliminar un `id` que no existe?
3. ¿Qué responde la API si el `nombre` llega vacío en el `POST`?

### 1.7 Ubicación en el curso

- Unidad: U1 - Base backend REST modular de BomERP.
- Producto del curso: base Full-Stack modular de BomERP.
- Producto de unidad: base backend modular de BomERP, con módulos de Catálogo, Inventario, Ventas y Compras delimitados.
- Avance del producto en esta sesión: `Producto` con CRUD completo, validado y con manejo de errores probado.

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
    class S2 today;
```

## 2. Explica

Tiempo: 25 min.

### 2.1 Arquitectura de la sesión

**Figura 2. Flujo de una petición de escritura sobre `Producto`**

```mermaid
flowchart LR
    Client["Cliente - PowerShell / bash / Swagger"]
    Controller["ProductoController"]
    Mapper["ProductoMapper"]
    Service["ProductoServiceImpl"]
    Repo["ProductoRepository"]
    DB[("Oracle - BOM_CATALOGO.PRODUCTOS")]

    Client -->|"ProductoRequest + @Valid"| Controller
    Controller --> Service
    Service --> Mapper
    Mapper -->|"toEntity"| Service
    Service --> Repo
    Repo --> DB
    Service --> Mapper
    Mapper -->|"toResponse"| Service
    Service --> Controller
    Controller -->|"ProductoResponse"| Client
```

Lectura del diagrama:

- El controller nunca construye ni lee la entidad `Producto` directamente: siempre entra y sale por un DTO, y el `ProductoMapper` es el único punto que conoce ambos lados (DTO y entidad).
- `@Valid` en el controller corta las peticiones inválidas **antes** de que lleguen al service — el service asume que todo lo que recibe ya pasó validación de forma.
- Integración (referencia, no requisito para esta sesión): ADS diseñó este mismo patrón (Controller-Service-Mapper-Repository-DTO) en su S8 como diseño de clases por capas; aquí se implementa contra Oracle real. **Error frecuente**: mezclar la validación de forma (`@NotBlank`, `@Size`) con reglas de negocio (por ejemplo, "el precio no puede bajar de la mitad") — esta sesión solo cubre la primera; reglas de negocio más complejas se tratan en sesiones posteriores.

Este diagrama es el mapa que guía el resto de la explicación: cada apartado siguiente desarrolla uno de sus componentes, en el mismo orden del Índice (1.2).

### 2.2 Entidad, repositorio, DTO y mapeo

La entidad `Producto` y `ProductoRepository` ya existen desde S1 (3.4.2) y no cambian en esta sesión. Lo que cambia es el DTO de salida y cómo se construye.

En S1, con solo `listar()`, un `record CategoriaResponse`/`ProductoResponse` alcanzaba: inmutable, sin Lombok, una sola forma de construirlo. Ahora `Producto` necesita dos DTO distintos (`ProductoRequest` para lo que entra, `ProductoResponse` para lo que sale) y un mapeo en ambas direcciones (`toEntity`/`toResponse`) — un `record` no tiene setters ni encaja con `@Builder`, así que `ProductoResponse` pasa a ser una clase con Lombok (`@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor`), igual que ya lo hace Distribuidas desde su S1.

**Nota de alcance:** `Categoria` sigue siendo `record` en esta sesión — solo `Producto` recibe CRUD completo en S2. `Categoria` recibe su propio CRUD progresivamente en S2-S3 (ver Tabla 10 de S1).

### 2.3 Validación de entrada con Bean Validation

`ProductoRequest` declara las reglas de forma directamente sobre sus campos (`@NotBlank`, `@Size`, `@NotNull`, `@DecimalMin`), y el controller las activa con `@Valid` en el parámetro `@RequestBody`. Si algo no cumple, Spring nunca llega a ejecutar el método del controller: responde `400` directo, con el `GlobalExceptionHandler` (3.2.1) manejando `MethodArgumentNotValidException`.

**Error frecuente**: olvidar `@Valid` en el `@RequestBody` del controller. Sin esa anotación, Spring ignora por completo las anotaciones de `ProductoRequest` y deja pasar datos inválidos hasta el service.

**La DTO no es la única capa que valida — son cuatro en el backend, cada una protegiendo algo distinto:**

1. **DTO (`ProductoRequest` + `@Valid`)**: la más rápida — responde `400` antes de ejecutar el controller. Solo protege lo que pasa por este endpoint.
2. **Entity (`Producto`)**: hoy no tiene anotaciones de Bean Validation, a propósito — el único camino para crear/modificar un `Producto` ya es `ProductoController` con `@Valid`, así que repetir las mismas reglas en la entidad sería validar dos veces sin ganar nada.
3. **Restricciones de Oracle** (`NOMBRE NOT NULL`, `CK_PRODUCTO_PRECIO`, `CK_PRODUCTO_STOCK`, ver BD2 S1): la última línea de defensa, dentro del motor mismo — protege sin importar qué aplicación o cliente escriba en `PRODUCTOS`, no solo este backend. Se repiten a propósito las mismas reglas que la DTO, porque protegen contra clientes distintos (ver 3.6 de BD2 S02, mismo criterio con los triggers).
4. **Service (`ProductoServiceImpl`)**: la única capa que puede validar algo que **no** está en la forma del dato, sino en el estado actual del sistema — necesita consultar la base. Ya lo usas en `buscarOFallar(id)`: ninguna anotación de Bean Validation puede responder "¿existe un `Producto` con este id?", porque eso depende de lo que haya guardado en ese momento, no de la forma del `id` en sí.

**Una quinta capa llega recién en S7, y no es parte de este backend**: la SPA Angular también validará sus propios formularios (campo requerido, tipo numérico, etc.) antes de enviar la petición. Esa validación mejora la experiencia del usuario (feedback inmediato, sin esperar al servidor) pero **nunca reemplaza** a las cuatro de arriba — cualquiera puede saltarse el formulario de Angular y mandar la petición directo (como ya hiciste con `Invoke-RestMethod`/Postman en 3.7), así que el backend tiene que validar igual, como si el frontend no existiera.

**Error frecuente**: si algún día una regla se saltara la validación de la DTO y llegara a violar un `CHECK` de Oracle, Hibernate lanzaría una `DataIntegrityViolationException` — y `GlobalExceptionHandler` (3.2.1) **no la maneja hoy**, así que el cliente recibiría un `500` genérico en vez de un `400` claro. No es un problema mientras `@Valid` siga cubriendo las mismas reglas que los `CHECK`, pero es la clase de hallazgo que vale documentar en la evidencia (4.3.1) si tu equipo decide profundizar en esto.

### 2.4 Manejo de errores aplicado

`GlobalExceptionHandler` y `ResourceNotFoundException` se crean en 3.2.1 de esta misma sesión, antes de tocar el CRUD — pedir, actualizar o eliminar un `id` que no existe lanza `ResourceNotFoundException`, capturado por ese mismo manejador global.

### 2.5 Logs y trazabilidad en operaciones de escritura

El `CorrelationIdFilter` (3.2.2) asigna un `traceId` a cada petición HTTP, sin importar el método — incluidas las peticiones `POST`/`PUT`/`DELETE` que este CRUD agrega, útil para rastrear, por ejemplo, quién creó o eliminó un producto específico revisando los logs por ese identificador.

## 3. Aplica: actividad práctica guiada

Tiempo: 2h.

**Actividad:** construcción guiada del CRUD REST completo de `Producto` (Producto de la sesión en 1.4).

**Propósito de la actividad:** convertir `Producto` de un listado de solo lectura a un recurso REST completo — con DTO de entrada validado, mapeo explícito mediante `ProductoMapper`, y las cuatro operaciones CRUD probadas con casos válidos e inválidos — verificando cada incremento antes de continuar al siguiente.

**Orientaciones metodológicas:** en el laboratorio, el docente construye el CRUD completo de `Producto` paso a paso frente a la clase, ejecutando cada prueba (válida e inválida) antes de avanzar; los estudiantes replican cada paso en su propio equipo, verificando la respuesta HTTP exacta antes de continuar.

**Actividades para realizar:**

- **3.1** Verificar el punto de partida.
- **3.2** Crear las excepciones y el filtro de trazabilidad.
- **3.3** Migrar `ProductoResponse` a clase y crear `ProductoRequest`.
- **3.4** Crear `ProductoMapper`.
- **3.5** Ampliar `ProductoService`/`ProductoServiceImpl`.
- **3.6** Ampliar `ProductoController`.
- **3.7** Probar el CRUD completo.
- **3.8** Cómo lo resuelve la industria: MapStruct (opcional, referencia).
- **3.9** Escribir pruebas automatizadas del controller.
- **3.10** Relacionar con ADS y BD2.

### 3.1 Verificar el punto de partida

**Punto de partida común:** todo el equipo debe comenzar exactamente desde donde quedó S1, no desde su propio avance individual. Clona la rama `s01-arquitectura-base` (el snapshot de cierre de S1):

```bash
git clone --branch s01-arquitectura-base https://github.com/262ciclo4/bomerp.git
```

**Producto del paso:** confirmación de que `Producto` (entidad, repositorio y `listar()`) ya existe.

!!! note "Si no completaste el 3.4.2 opcional de S1"
    Esta sesión asume que `catalogo/producto/entity/Producto.java` y `catalogo/producto/repository/ProductoRepository.java` ya existen (S1, 3.4.2). Si en S1 solo hiciste `Categoria` y dejaste `Producto` pendiente por ser opcional, créalos ahora siguiendo exactamente 3.4.2 de S1 antes de continuar — son idénticos, esta sesión no los repite.

**Requisito antes de continuar:** confirma que `http://localhost:8080/api/v1/productos` responde (lista vacía o con datos) antes de tocar código nuevo. Si falla, el problema es de S1 (conexión a Oracle o tablas faltantes, ver 3.4 de S1), no de esta sesión.

### 3.2 Crear las excepciones y el filtro de trazabilidad

**Producto del paso:** manejo de errores centralizado y filtro de trazabilidad (`traceId` en cada log) funcionando en `bomerp-backend`, antes de ampliar el CRUD de `Producto`. El sílabo de LP2 lista "excepciones" y "logs" como parte del alcance oficial de **esta** sesión (ver 1.4), no de S1 — por eso se crean aquí, no en S1.

#### 3.2.1 Excepciones y manejador global de errores

**`exception/ResourceNotFoundException.java`**

```java
package pe.edu.upeu.bomerp.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
```

**`exception/GlobalExceptionHandler.java`**

```java
package pe.edu.upeu.bomerp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Error de validación en los datos enviados");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
```

En S1, `catalogo` solo listaba (`GET`), así que `ResourceNotFoundException` todavía no se lanzaba desde ningún service — quedó lista para el DTO de entrada y las operaciones `POST`/`PUT`/`DELETE` que esta misma sesión implementa (3.3 en adelante).

#### 3.2.2 Filtro de trazabilidad `CorrelationIdFilter` y configuración de logs

Este filtro agrega un identificador de trazabilidad a cada request usando el header `X-Trace-ID`. Si el cliente no lo envía, el filtro genera un UUID.

**`filter/CorrelationIdFilter.java`**

```java
package pe.edu.upeu.bomerp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-ID";
    public static final String MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        try {
            MDC.put(MDC_KEY, traceId);
            response.setHeader(TRACE_ID_HEADER, traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
```

Y `src/main/resources/logback-spring.xml`, que define el formato de logs e incluye el `traceId` en cada línea (`[%X{traceId}]`), con salida por consola y por archivo en `logs/bomerp.log`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <property name="LOG_PATTERN"
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{traceId}] %-5level %logger{36} - %msg%n"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/bomerp.log</file>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/bomerp-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

#### 3.2.3 Ejecutar y probar

Si ya hiciste esto en S1, salta la ejecución — ya está probado. Si no, arranca la aplicación y verifica el cambio de formato: antes de `logback-spring.xml`, la terminal muestra el formato por defecto de Spring Boot; después, cambia al patrón definido (`[%X{traceId}]` en vez de PID/app/hilo):

```text
2026-08-16 18:07:09.078 [] INFO  o.s.web.servlet.DispatcherServlet - Completed initialization in 4 ms
2026-08-16 18:07:21.448 [4ded4f1b-efc8-4ef4-8462-435c7a3779ef] INFO  o.s.api.AbstractOpenApiResource - Init duration for springdoc-openapi is: 589 ms
Hibernate: 
    select
        c1_0.id,
        c1_0.descripcion,
        c1_0.nombre 
    from
        bom_catalogo.categorias c1_0
```

La línea de arranque muestra `[]` vacío: todavía no hay ninguna petición HTTP en curso. En cambio, al hacer clic en "Try it out" sobre `GET /api/v1/categorias` en Swagger, la petición ya trae un `traceId` real (`4ded4f1b-...`): el `CorrelationIdFilter` generó el UUID y lo puso en el `MDC`. La consulta `Hibernate: select...` que aparece justo después es la evidencia de que esa misma petición llegó hasta `CategoriaRepository.findAll()` y consultó Oracle de verdad — Hibernate la imprime con su propio formato (`show-sql`), sin el prefijo `[traceId]`, porque usa un logger aparte del patrón de `logback-spring.xml`.

### 3.3 Migrar `ProductoResponse` a clase y crear `ProductoRequest`

**Producto del paso:** DTO de entrada y de salida, ambos como clases con Lombok.

Reemplaza el contenido de `catalogo/producto/dto/ProductoResponse.java` (el `record` de S1) por:

**`catalogo/producto/dto/ProductoResponse.java`**

```java
package pe.edu.upeu.bomerp.catalogo.producto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
}
```

**`catalogo/producto/dto/ProductoRequest.java`**

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
}
```

### 3.4 Crear `ProductoMapper`

**Producto del paso:** clase dedicada al mapeo `Producto` ↔ DTO, en ambas direcciones.

**`catalogo/producto/mapper/ProductoMapper.java`**

```java
package pe.edu.upeu.bomerp.catalogo.producto.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoRequest;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoResponse;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        return producto;
    }

    public ProductoResponse toResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .build();
    }
}
```

`toEntity` usa `new` + setters, no `@Builder`, porque `Producto` (la entidad JPA) mantiene `@Getter @Setter @NoArgsConstructor` desde S1, sin `@Builder` — mezclar `@Builder` en la entidad persistente no aporta nada aquí y agrega una anotación más a mantener.

### 3.5 Ampliar `ProductoService`/`ProductoServiceImpl`

**Producto del paso:** las cuatro operaciones CRUD en el service, con `ResourceNotFoundException` en las que buscan por `id`.

**`catalogo/producto/service/ProductoService.java`**

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
}
```

**`catalogo/producto/service/ProductoServiceImpl.java`**

```java
package pe.edu.upeu.bomerp.catalogo.producto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = productoMapper.toEntity(request);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarOFallar(id);
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public void eliminar(Long id) {
        productoRepository.delete(buscarOFallar(id));
    }

    private Producto buscarOFallar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }
}
```

`pe.edu.upeu.bomerp.exception` es el paquete compartido creado en 3.2.1 de esta sesión — `ResourceNotFoundException` no se repite aquí, se reutiliza tal cual.

### 3.6 Ampliar `ProductoController`

**Producto del paso:** los cinco endpoints (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`) sobre `/api/v1/productos`.

**`catalogo/producto/controller/ProductoController.java`**

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

    @Operation(summary = "Lista los productos registrados")
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
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

### 3.7 Probar el CRUD completo

**Producto del paso:** evidencia de los cinco endpoints, incluidos los casos de error.

PowerShell:

**Error frecuente**: en Windows PowerShell, `Invoke-RestMethod -Body` con caracteres acentuados ("mecánico") los codifica en ANSI, no UTF-8, y Jackson no puede parsear ese body — responde `400` aunque los datos sean por lo demás válidos. Más simple que convertir a bytes UTF-8: evita tildes en los datos de prueba, igual que ya haces con las categorías (`Electrodomesticos`, no `Electrodomésticos`). Si necesitas mandar un valor con tilde sí o sí, convierte el JSON a bytes UTF-8 explícitamente antes de enviarlo:

```powershell
$json = '{"nombre":"Teclado mecánico","precio":180.50,"stock":25}'
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/productos" -ContentType "application/json" -Body ([System.Text.Encoding]::UTF8.GetBytes($json))
```

```powershell
# Crear (201)
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/productos" -ContentType "application/json" -Body '{"nombre":"Teclado mecanico","precio":180.50,"stock":25}'

# Consultar por id (200) - reemplaza {id} por el id devuelto arriba
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/v1/productos/{id}"

# Actualizar (200)
Invoke-RestMethod -Method Put -Uri "http://localhost:8080/api/v1/productos/{id}" -ContentType "application/json" -Body '{"nombre":"Teclado mecanico RGB","precio":199.90,"stock":20}'

# Eliminar (204)
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/v1/productos/{id}"

# Caso inválido: nombre vacío (400)
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/productos" -ContentType "application/json" -Body '{"nombre":"","precio":10,"stock":1}'

# Caso no encontrado: id inexistente (404)
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/v1/productos/999999"
```

bash macOS/Linux:

```bash
# Crear (201)
curl -i -X POST http://localhost:8080/api/v1/productos -H "Content-Type: application/json" -d '{"nombre":"Teclado mecanico","precio":180.50,"stock":25}'

# Consultar por id (200) - reemplaza {id} por el id devuelto arriba
curl -i http://localhost:8080/api/v1/productos/{id}

# Actualizar (200)
curl -i -X PUT http://localhost:8080/api/v1/productos/{id} -H "Content-Type: application/json" -d '{"nombre":"Teclado mecanico RGB","precio":199.90,"stock":20}'

# Eliminar (204)
curl -i -X DELETE http://localhost:8080/api/v1/productos/{id}

# Caso inválido: nombre vacío (400)
curl -i -X POST http://localhost:8080/api/v1/productos -H "Content-Type: application/json" -d '{"nombre":"","precio":10,"stock":1}'

# Caso no encontrado: id inexistente (404)
curl -i http://localhost:8080/api/v1/productos/999999
```

**Tabla 2. Verificación del CRUD antes de continuar**

| Caso | Método | Resultado esperado |
|---|---|---|
| Crear producto válido | `POST` | `201 Created`, cuerpo con `id` asignado |
| Consultar por id existente | `GET /{id}` | `200 OK`, datos del producto |
| Actualizar producto existente | `PUT /{id}` | `200 OK`, datos actualizados |
| Eliminar producto existente | `DELETE /{id}` | `204 No Content` |
| Nombre vacío en `POST` | `POST` | `400`, cuerpo con `error: "Bad Request"` (3.2.1) |
| Id inexistente en `GET`/`PUT`/`DELETE` | cualquiera | `404`, cuerpo con `error: "Not Found"` (3.2.1) |
| Trazabilidad | cualquiera | Log de la petición muestra `[traceId]` no vacío (3.2.2) |

### 3.8 Cómo lo resuelve la industria: MapStruct (opcional, referencia)

!!! note "Alternativa opcional, no reemplaza 3.4 en la evidencia de la sesión"
    Esta sección es informativa: muestra cómo un equipo profesional evita
    escribir a mano el cuerpo de un mapper como `ProductoMapper` (3.4). La
    implementación que se evalúa en 4.4-4.6 es la manual de 3.4. Adoptar
    MapStruct es una decisión de equipo, no un requisito de S2.

**Producto del paso:** el mismo `ProductoMapper` de 3.4, generado en compilación en vez de escrito a mano.

Con [MapStruct](https://mapstruct.org/), un mapper es una **interfaz** — el procesador de anotaciones genera la implementación (`ProductoMapperImpl`) al compilar.

En el `pom.xml`, agrega la dependencia:

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>
```

Y en el `maven-compiler-plugin`, junto al `annotationProcessorPaths` que ya tiene Lombok, agrega el de MapStruct **después** de Lombok — MapStruct necesita ver los getters/setters que Lombok genera, y el orden de los `<path>` determina en qué orden corren los procesadores:

```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.6.3</version>
    </path>
</annotationProcessorPaths>
```

**El proyecto que genera VS Code trae este mismo bloque `annotationProcessorPaths` dos veces dentro de `maven-compiler-plugin`**: una vez en la ejecución `default-compile` (compila `src/main`) y otra en `default-testCompile` (compila `src/test`), esta última con Lombok solo. Agrega el `<path>` de MapStruct en **las dos**, no solo en `default-compile`. Dejar `default-testCompile` sin MapStruct deja el proyecto en un estado inconsistente entre main y test que el compilador incremental del propio IDE no siempre resuelve solo — un síntoma real: agregar `@Mapping` correctamente a un mapper y que el aviso `Unmapped target property` siga apareciendo, sin que el código tenga ningún error.

`ProductoMapper` pasa de clase a interfaz:

```java
package pe.edu.upeu.bomerp.catalogo.producto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoRequest;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoResponse;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    @Mapping(target = "id", ignore = true)
    Producto toEntity(ProductoRequest request);
    ProductoResponse toResponse(Producto producto);
}
```

`nombre`, `precio` y `stock` coinciden exactamente entre `Producto`, `ProductoRequest` y `ProductoResponse`, así que MapStruct los relaciona sin anotaciones adicionales — el mapeo de 3.4, que hoy tiene diez líneas de `set`/`get` repetidos, se reduce a dos firmas de método sin cuerpo. `id` es la excepción: `ProductoRequest` no lo trae (lo genera Oracle al insertar, S1), así que `toEntity` sí necesita `@Mapping(target = "id", ignore = true)` — sin ella, MapStruct advierte `Unmapped target property: id` en cada build.

**Ventajas frente al mapeo manual (3.4):**

- **Menos código que mantener**: si mañana `Producto` agrega un campo con el mismo nombre en el DTO, MapStruct lo mapea solo — nadie edita `ProductoMapper` a mano.
- **Errores en compilación, no en producción**: si un campo del DTO no tiene de dónde mapearse, MapStruct falla el build con un mensaje claro, en vez de dejarlo en `null` silenciosamente.
- **Sin costo de reflexión en tiempo de ejecución**: el código generado es Java plano, igual de rápido que el mapeo manual — a diferencia de librerías que mapean por reflexión en cada llamada (por ejemplo, ModelMapper).

### 3.9 Escribir pruebas automatizadas del controller

**Producto del paso:** `ProductoControllerTest`, con los casos de 3.7 automatizados en vez de ejecutados a mano.

Las pruebas manuales de 3.7 verifican el CRUD una vez; una prueba automatizada verifica lo mismo **cada vez que compilas**, sin depender de que alguien recuerde ejecutar los `curl`/`Invoke-RestMethod` uno por uno. `@WebMvcTest` levanta solo la capa web (controller, `@Valid`, `GlobalExceptionHandler`) — sin Oracle, sin `ProductoServiceImpl` real — y `@MockitoBean` reemplaza `ProductoService` por un doble de prueba controlado por el test.

**Error frecuente**: importar `@WebMvcTest` desde `org.springframework.boot.test.autoconfigure.web.servlet` (el paquete de Spring Boot 2.x/3.x) — en Spring Boot **4.0.7** ese paquete ya no existe. La anotación se movió a `org.springframework.boot.webmvc.test.autoconfigure` como parte de la modularización de los starters de test (`spring-boot-starter-webmvc-test`, ver `pom.xml`). El import correcto para este proyecto es el que usa el código de abajo.

**Error frecuente**: importar `ObjectMapper` desde `com.fasterxml.jackson.databind` (Jackson 2, "clásico") y autoinyectarlo con `@Autowired` — en Spring Boot **4.0.7** (con Spring Framework 7), el `ObjectMapper` que Spring autoconfigura por defecto es de **Jackson 3**, un paquete completamente nuevo: `tools.jackson.databind`. Jackson 2 sigue presente en el classpath (lo usan otras librerías de testing), pero Spring ya no registra un bean de ese tipo — por eso `@Autowired private ObjectMapper objectMapper` con el import viejo falla con `No qualifying bean of type 'com.fasterxml.jackson.databind.ObjectMapper' available`. El import correcto es `tools.jackson.databind.ObjectMapper` (el método `writeValueAsString(...)` se usa exactamente igual).

**`src/test/java/pe/edu/upeu/bomerp/catalogo/producto/controller/ProductoControllerTest.java`**

```java
package pe.edu.upeu.bomerp.catalogo.producto.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoRequest;
import pe.edu.upeu.bomerp.catalogo.producto.dto.ProductoResponse;
import pe.edu.upeu.bomerp.catalogo.producto.service.ProductoService;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void listar_respondeOkConLosProductosDelService() throws Exception {
        when(productoService.listar()).thenReturn(List.of(
                ProductoResponse.builder().id(1L).nombre("Teclado mecánico").precio(new BigDecimal("180.50")).stock(25).build()
        ));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Teclado mecánico"));
    }

    @Test
    void crear_conDatosValidos_respondeCreated() throws Exception {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Teclado mecánico");
        request.setPrecio(new BigDecimal("180.50"));
        request.setStock(25);

        when(productoService.crear(any())).thenReturn(
                ProductoResponse.builder().id(1L).nombre("Teclado mecánico").precio(new BigDecimal("180.50")).stock(25).build()
        );

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_conNombreVacio_respondeBadRequestSinLlegarAlService() throws Exception {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("");
        request.setPrecio(new BigDecimal("10"));
        request.setStock(1);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtener_conIdInexistente_respondeNotFound() throws Exception {
        when(productoService.obtener(999L)).thenThrow(new ResourceNotFoundException("Producto no encontrado: 999"));

        mockMvc.perform(get("/api/v1/productos/999"))
                .andExpect(status().isNotFound());
    }
}
```

`@WebMvcTest` incluye automáticamente los `@RestControllerAdvice` del proyecto (como `GlobalExceptionHandler`, 3.2.1) — por eso el caso de nombre vacío responde `400` real, generado por el mismo manejador que usa la aplicación completa, no un mock. `crear_conNombreVacio_respondeBadRequestSinLlegarAlService` verifica implícitamente que `@Valid` corta la petición antes del controller: como nunca se programó un `when(productoService.crear(...))` para ese caso, si la validación fallara y la petición llegara igual al service mockeado, Mockito respondería `null` y el test fallaría por una razón distinta — la prueba está diseñada para fallar ruidosamente si `@Valid` deja de funcionar.

**Error frecuente**: usar `@SpringBootTest` en vez de `@WebMvcTest` para probar un controller — `@SpringBootTest` levanta el contexto completo (incluida la conexión a Oracle), mucho más lento y con una dependencia que esta prueba no necesita. `@WebMvcTest` es la porción mínima suficiente para probar contrato HTTP, validación y manejo de errores.

Ejecuta las pruebas:

```powershell
.\mvnw.cmd test -Dtest=ProductoControllerTest
```

```bash
./mvnw test -Dtest=ProductoControllerTest
```

### 3.10 Relacionar con ADS y BD2

**Producto del paso:** matriz de integración actualizada.

**Tabla 3. Matriz de integración LP2-ADS-BD2 (S2)**

| Endpoint LP2 | Componente ADS | Objeto BD2 |
|---|---|---|
| `POST /api/v1/productos` | Patrón Controller-Service-Mapper-Repository (ADS S8) | `SP_REGISTRAR_PRODUCTO` (BD2 S1), regla equivalente |
| `PUT /api/v1/productos/{id}` | Contrato REST versionado (ADS S8) | `SP_APLICAR_DESCUENTO_PRODUCTO` (BD2 S1), regla equivalente |
| `DELETE /api/v1/productos/{id}` | — | `TRG_PRODUCTO_AUDITORIA` sobre `PRODUCTOS` (BD2 S2) |

Sesión equivalente en los otros dos cursos, misma semana: [ADS - S2 Modelo C4 y Vistas Arquitectónicas](../../ads/sesiones/S02_Modelo_C4_Vistas_Arquitectonicas.md) y [BD2 - S2 Triggers DML y Auditoría](../../bd2/sesiones/S02_Triggers_DML_Auditoria.md).

**Evidencia de aprendizaje:**

- `Producto` con CRUD REST completo (`GET`, `GET /{id}`, `POST`, `PUT`, `DELETE`), DTO de entrada/salida y `ProductoMapper` dedicado.
- Validaciones de entrada probadas con al menos un caso inválido (`400`).
- Manejo de errores probado con al menos un caso de recurso no encontrado (`404`).
- `ProductoControllerTest` en verde, con los casos de éxito, validación y no encontrado automatizados.

## 4. Crea: actividad autónoma

Tiempo: 2h fuera del aula.

### 4.1 Actividad

Replicación autónoma del CRUD REST completo en la entidad principal del dominio elegido por el equipo, documentada en evidencia individual.

Completa y evidencia estas tareas:

1. Definir el DTO de entrada y de salida de tu entidad principal.
2. Crear el `Mapper` dedicado, con `toEntity`/`toResponse`.
3. Implementar las cuatro operaciones CRUD en el service.
4. Exponer los cinco endpoints en el controller.
5. Probar al menos un caso válido y uno inválido por operación.
6. Escribir al menos una prueba automatizada (`@WebMvcTest`) del controller.

### 4.2 Propósito

Que cada estudiante demuestre, de forma individual y fuera del aula, que puede reproducir el patrón CRUD construido en clase sin el acompañamiento del docente.

Cada estudiante documenta el CRUD de la entidad principal de su dominio.

### 4.3 Indicaciones

Entrega un PDF con el siguiente nombre:

```text
S02_LP2_Equipo##_ApellidoNombre.pdf
```

Cada captura de pantalla del informe debe mostrar, sin recortar, el reloj del sistema (fecha y hora) y tu usuario o foto de perfil (Windows, VS Code o navegador) visibles en pantalla — es lo que permite verificar que la evidencia es tuya y que corresponde al momento real de tu trabajo.

#### 4.3.1 Estructura del informe

**Datos del estudiante**

- Nombre:
- Equipo:
- Sesión: S02 - CRUD REST Completo de Producto
- Rol o aporte realizado:
- Link de GitHub:

**Evidencia técnica**

Incluye capturas o salidas de consola con una breve explicación debajo de cada una, organizadas en los mismos 4 bloques de la rúbrica (4.6) — así queda claro qué evidencia corresponde a cada criterio evaluado:

1. *DTO, mapeo y validación*
    - `ProductoRequest`/`ProductoResponse` (o los DTO de tu entidad) y el `Mapper` dedicado.
    - Caso inválido respondiendo `400`.
2. *Operaciones CRUD funcionales*
    - `POST`, `GET /{id}`, `PUT`, `DELETE` respondiendo con el código HTTP esperado.
    - Prueba automatizada del controller (`@WebMvcTest`) en verde.
3. *Manejo de errores y trazabilidad*
    - Caso de `id` inexistente respondiendo `404`.
    - Log de una petición mostrando `traceId`.
4. *Separación de responsabilidades*
    - Estructura de paquetes (`dto`, `mapper`, `service`, `controller`).

**Error o hallazgo**

Describe un error o hallazgo: mapeo incompleto, validación faltante, código HTTP incorrecto o regla de negocio no contemplada.

**Reflexión técnica breve**

Responde en 5 a 8 líneas:

```text
¿Por qué separar el mapeo entidad-DTO en una clase propia, en vez de hacerlo directamente en el controller o el service?
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
- Implementa las cuatro operaciones CRUD sobre la entidad principal de su dominio.
- El DTO de entrada valida al menos dos reglas de forma (`@NotBlank`, `@Size`, `@NotNull`, etc.).
- Usa un `Mapper` dedicado, no mapeo manual disperso en el controller o el service.
- Prueba al menos un caso inválido (`400`) y un caso de recurso no encontrado (`404`).
- Incluye al menos una prueba automatizada del controller (`@WebMvcTest`) en verde.
- Cada captura de la evidencia técnica muestra el reloj del sistema y el usuario/perfil visible, sin recortar.
- Las fechas y horas de las capturas son coherentes con el historial de commits de su repositorio en GitHub.
- Incluye un error o hallazgo técnico diagnosticado.
- Incluye la reflexión técnica breve solicitada.
- Incluye el Anexo de feedback de la sesión respondido, como última página del PDF.

### 4.5 Preguntas de defensa

1. ¿Por qué `ProductoResponse` pasó de `record` a clase en esta sesión?
2. ¿Qué diferencia hay entre la validación de forma (`@Valid`) y una regla de negocio?
3. ¿Qué código HTTP corresponde a "recurso no encontrado" y quién lo genera en tu backend?
4. ¿Qué pasaría si el controller construyera la entidad directamente, sin pasar por el `Mapper`?
5. ¿Por qué tu prueba de controller usa `@WebMvcTest` y no `@SpringBootTest`?

### 4.6 Rúbrica de evaluación

**Tabla 4. Rúbrica de evaluación**

| Criterio | Peso (%) | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Nivel obtenido |
|---|---:|---|---|---|---|---:|
| 1. DTO, mapeo y validación* | 25 | DTO de entrada/salida separados, `Mapper` dedicado y validaciones correctas, con caso inválido evidenciado. | DTO y mapeo funcionales, con validación parcial. | DTO o mapeo incompletos. | No presenta DTO ni mapeo. | |
| 2. Operaciones CRUD funcionales* | 25 | Las cuatro operaciones CRUD funcionan con los códigos HTTP correctos, con al menos una prueba automatizada (`@WebMvcTest`) en verde. | La mayoría de las operaciones funciona, con detalles menores; prueba automatizada ausente o incompleta. | Menos de la mitad de las operaciones funciona. | Ninguna operación funciona. | |
| 3. Manejo de errores y trazabilidad* | 25 | Casos `400` y `404` evidenciados, con `traceId` visible en logs. | Al menos un caso de error evidenciado. | Manejo de errores parcial o sin evidencia. | No maneja errores. | |
| 4. Separación de responsabilidades* | 25 | Estructura por capas (`dto`, `mapper`, `service`, `controller`) clara y consistente con el patrón de la sesión. | Estructura presente, con acoplamiento menor. | Estructura confusa o incompleta. | No separa responsabilidades. | |

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

**Resumen breve:** hoy `Producto` pasó de un listado de solo lectura a un recurso REST completo — creación, consulta individual, actualización y eliminación, con validación de entrada, mapeo explícito y el manejo global de errores puesto a prueba con casos reales.

**Dinámica participativa:** en una ronda rápida (o con una herramienta digital tipo formulario o encuesta en vivo), cada estudiante comparte en una frase qué caso de error (`400` o `404`) le costó más reproducir.

**Metacognición:** cada estudiante responde el Anexo de feedback de la sesión, incluido en su evidencia individual (ver 4.3.1). El docente analiza esas respuestas con IA para identificar temas recurrentes o dudas comunes del equipo, y con esos indicadores construye el cierre real de la sesión — que se entrega al inicio de S3, no al final de esta clase.

**Proyección:** el `Mapper` y el patrón CRUD de hoy se repiten en S3 cuando `Categoria` y `Producto` se relacionen, y en cada módulo nuevo del curso (`ventas` en S4, `seguridad` en S10) — es el mismo patrón, aplicado una y otra vez a entidades distintas.

## Bibliografía

1. Spring. (2024). *Validation, data binding, and type conversion*. VMware. https://docs.spring.io/spring-framework/reference/core/validation.html
2. Jakarta EE. (2024). *Jakarta Bean Validation specification*. Eclipse Foundation. https://beanvalidation.org/
3. Spring. (2024). *Spring Data JPA reference documentation*. VMware. https://docs.spring.io/spring-data/jpa/reference/

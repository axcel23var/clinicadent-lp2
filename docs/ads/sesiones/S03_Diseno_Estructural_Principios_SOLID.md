# S3 - Diseño Estructural y Principios SOLID

## 1. Introducción

Tiempo: 20 min.

### 1.1 Presentación de la sesión

En la sesión 2, el equipo definió la estructura del sistema con el modelo C4 (C1-C4), sin cuestionar todavía si esa estructura estaba bien diseñada — solo si estaba bien representada. Esta sesión aplica sobre esa misma estructura un criterio distinto: los cinco principios SOLID, más cohesión, acoplamiento, modularidad y abstracción — el vocabulario que permite distinguir una clase o un módulo bien diseñado de uno que solo se ve ordenado en el diagrama, y que sirve tanto para evaluar código que ya existe como para decidir cómo estructurar el que todavía no se ha escrito.

### 1.2 Índice

1. Principios SOLID.
2. Cohesión y acoplamiento.
3. Modularidad y abstracción.

### 1.3 Propósito de aprendizaje

Al concluir la clase, estarás en condiciones de:

- **Evaluar** un módulo de software real contra los cinco principios SOLID, su cohesión, su acoplamiento y su nivel de abstracción, distinguiendo una tensión de diseño justificada de una violación real.

### 1.4 Producto de sesión

Evaluación documentada de `catalogo/categoria` y `catalogo/producto` (LP2, código real a la fecha de esta sesión) contra SOLID, cohesión, acoplamiento, modularidad y abstracción, con al menos un hallazgo real identificado y su justificación o su corrección propuesta.

### 1.5 Metodología

**Tabla 1. Metodología de la sesión**

| Actividades a Realizar en el Periodo | Orientaciones generales (Orientaciones Metodológicas) | Material de estudio recomendado |
|---|---|---|
| Revisión previa individual | Repasar la vista C3/C4 de S2 (Figuras 11-12) y el código real de `lp2/bomerp-backend/.../catalogo/`. Trabajo individual, antes de clase; identificar qué clases existen hoy en cada paquete. | S2 (Tabla 4, Tabla 5), `lp2/bomerp-backend/src/main/java/.../catalogo/`. |
| Clase presencial | Evaluación guiada de `categoria` y `producto` contra los cinco principios SOLID, cohesión, acoplamiento, modularidad y abstracción. Trabajo individual, siguiendo al docente paso a paso; consulta inmediata ante dudas sobre si algo es una violación real o una tensión aceptable. | Pasos 3.1 a 3.7 de esta guía, código real de LP2. |
| Evaluación formativa | Revisión en clase de la tabla de evaluación SOLID y de los hallazgos identificados. La evidencia se completa y sustenta de forma individual, fuera del aula, según los criterios mínimos de la sección 4.4. | Indicaciones de entrega (4.3), rúbrica de evaluación (4.6). |

### 1.6 Motivación de la sesión

#### 1.6.1 Caso: el service que fue creciendo

En muchos proyectos reales, un service que "solo hacía una cosa" al inicio termina absorbiendo tareas que no le correspondían: valida datos de entrada, arma el mensaje para un log de auditoría, decide si notificar por correo, y persiste — todo en el mismo método. Cada tarea nueva parece razonable agregarla ahí ("ya está el código, es rápido"), hasta que cambiar una sola regla de negocio obliga a tocar una clase que ahora tiene varias razones distintas para cambiar, y ninguna prueba pequeña la cubre completa.

Esto no es un problema de sintaxis ni de que el código "no funcione" — funciona. Es un problema de diseño: la clase perdió cohesión (mezcla responsabilidades no relacionadas) y probablemente ganó acoplamiento (otras clases empiezan a depender de sus detalles internos para no duplicar lógica). Los principios SOLID, junto con cohesión, acoplamiento, modularidad y abstracción, son el vocabulario que permite detectar este tipo de problema **antes** de que sea costoso corregirlo — y son exactamente los criterios que esta sesión aplica sobre código real en la sección 3.

**Preguntas de análisis**

**Activación de conocimientos previos**

1. ¿Has visto (o escrito) alguna vez una clase o función que terminó "haciendo de todo"? ¿Qué la hizo crecer así?
2. De los cinco principios SOLID, ¿cuál crees que es el más fácil de violar sin darte cuenta al agregar una funcionalidad "rápida"?

**Comprensión de SOLID aplicado**

1. ¿Por qué una clase con alta cohesión suele ser más fácil de probar que una con baja cohesión?
2. ¿Qué diferencia hay entre un acoplamiento aceptable (depender de una interfaz o contrato) y uno problemático (depender de los detalles internos de otra clase)?

### 1.7 Ubicación en el curso

- Unidad: U1 - Arquitectura y Diseño Estructural.
- Producto del curso: Diseño Técnico Profesional Documentado.
- Producto de unidad: arquitectura documentada mediante vistas arquitectónicas y principios de diseño aplicados.
- Avance del producto en esta sesión: evaluación SOLID/cohesión/acoplamiento de los módulos reales de LP2, con hallazgos documentados.

Roadmap del producto de la unidad:

**Figura 1. Roadmap del producto de la unidad**

```mermaid
flowchart TB
    S1["`**S1:** Fundamentos de arquitectura`"]
    S2["`**S2:** Modelo C4 y vistas`"]
    S3["`**S3:** Diseño estructural y SOLID`"]
    S4["`**S4:** Arquitecturas modernas`"]
    S5["`**S5:** Producto U1`"]

    S1 --> S2 --> S3 --> S4 --> S5

    classDef today fill:#ffe08a,stroke:#9a6b00,stroke-width:2px,color:#111;
    class S3 today;
```

## 2. Explica

Tiempo: 25 min.

### 2.1 Arquitectura de la sesión

**Figura 2. De C3/C4 (S2) a la evaluación SOLID (S3)**

```mermaid
flowchart LR
    C3C4["C3/C4 de S2<br/>estructura ya dibujada"]
    SOLID["Cinco principios SOLID<br/>por clase/interfaz"]
    CohAcop["Cohesión y acoplamiento<br/>por paquete"]
    ModAbs["Modularidad y abstracción<br/>por módulo"]
    Hallazgo["Hallazgo real<br/>justificado o corregido"]

    C3C4 --> SOLID --> CohAcop --> ModAbs --> Hallazgo

    classDef today fill:#ffe08a,stroke:#9a6b00,stroke-width:2px,color:#111;
    class SOLID,CohAcop,ModAbs,Hallazgo today;
```

Esta sesión no dibuja una vista nueva: toma la estructura que C3/C4 (S2) ya documentó y la somete a un criterio de calidad distinto — no "¿está bien dibujado?", sino "¿está bien diseñado?".

### 2.2 Principios SOLID

**Tabla 2. Los cinco principios SOLID**

| Principio | Pregunta que responde | Señal de violación |
|---|---|---|
| **S** — Responsabilidad única (*Single Responsibility*) | ¿Esta clase tiene una sola razón para cambiar? | Una clase que valida, persiste y formatea salida al mismo tiempo. |
| **O** — Abierto/cerrado (*Open/Closed*) | ¿Puedo extender el comportamiento sin modificar código existente? | Agregar un caso nuevo obliga a editar un `if`/`switch` ya existente en vez de agregar una clase nueva. |
| **L** — Sustitución de Liskov (*Liskov Substitution*) | ¿Cualquier implementación de una interfaz puede reemplazar a otra sin romper nada? | Una implementación que lanza una excepción no documentada por el contrato, o que exige un estado adicional que otras no necesitan. |
| **I** — Segregación de interfaces (*Interface Segregation*) | ¿La interfaz expone solo lo que el cliente necesita? | Una interfaz con métodos que la mayoría de sus clientes no usa nunca. |
| **D** — Inversión de dependencias (*Dependency Inversion*) | ¿La clase depende de abstracciones (interfaces) o de implementaciones concretas? | Un `service` que hace `new AlgoRepositoryImpl()` en vez de recibir `AlgoRepository` inyectado. |

Los cinco principios no son reglas aisladas: **S** evita que una clase crezca sin control, **O**/**L** garantizan que se pueda extender sin romper lo existente, **I** evita interfaces infladas, y **D** es el que hace posible **O** y **L** en la práctica — sin depender de abstracciones, no hay forma de sustituir una implementación por otra sin tocar el código que la usa.

**Ejemplo de referencia.** En `catalogo/producto` (LP2): `ProductoController` solo traduce HTTP a llamadas al service, sin persistir ni validar reglas de negocio (S); `ProductoService` es una interfaz con una única implementación hoy, lista para aceptar una segunda sin que el controller cambie (O/L); y `ProductoServiceImpl` recibe `ProductoRepository` y `ProductoMapper` como interfaces inyectadas por el framework, nunca las crea con `new` (D). Este código real se evalúa con más detalle en 3.1-3.3.

**Figura 3. Diagrama de clases UML — dependencias reales de `ProductoServiceImpl`**

```mermaid
classDiagram
    class ProductoController {
        -ProductoService productoService
    }
    class ProductoService {
        <<interface>>
        +listar() List~ProductoResponse~
        +obtener(id) ProductoResponse
        +crear(request) ProductoResponse
    }
    class ProductoServiceImpl {
        -ProductoRepository productoRepository
        -CategoriaRepository categoriaRepository
        -ProductoMapper productoMapper
    }
    class ProductoRepository {
        <<interface>>
    }
    class CategoriaRepository {
        <<interface>>
    }
    class ProductoMapper {
        <<interface>>
    }

    ProductoController --> ProductoService : depende de la interfaz, no de la impl. (D)
    ProductoService <|.. ProductoServiceImpl : implementa el contrato (O, L)
    ProductoServiceImpl --> ProductoRepository : interfaz inyectada (D)
    ProductoServiceImpl --> CategoriaRepository : interfaz inyectada (D)
    ProductoServiceImpl --> ProductoMapper : interfaz inyectada (D)
```

Cada flecha de la Figura 3 es evidencia de un principio distinto: la de `ProductoController` hacia `ProductoService` y las tres que salen de `ProductoServiceImpl` apuntan todas a interfaces, nunca a una clase concreta — esa es Inversión de Dependencias (D) hecha visible. La flecha punteada `ProductoService <|.. ProductoServiceImpl` es la relación de implementación que hace posible O y L: mientras el contrato no cambie, `ProductoServiceImpl` puede reemplazarse por otra clase sin tocar `ProductoController`. Responsabilidad única (S) no se ve como una flecha — se ve en lo que **no** aparece dentro de `ProductoServiceImpl`: ningún método de formateo, envío de correo o logging mezclado con la lógica de `Producto`. Segregación de interfaces (I) tampoco es una flecha: es que `ProductoService` solo declara los métodos que `ProductoController` realmente usa, sin métodos de más.

### 2.3 Cohesión y acoplamiento

**Cohesión**: qué tan relacionado está lo que vive *dentro* de un mismo paquete o clase. Alta cohesión = todo lo que está junto tiene un propósito común.

**Acoplamiento**: qué tan dependiente es una parte del sistema de los detalles internos de otra. Bajo acoplamiento = un cambio interno en un módulo no obliga a cambiar otro que solo consume su contrato.

**Tabla 3. Cohesión y acoplamiento, en una frase**

| | Alta cohesión / bajo acoplamiento (deseable) | Baja cohesión / alto acoplamiento (evitar) |
|---|---|---|
| Cohesión | Un paquete que solo contiene clases sobre una misma entidad del dominio. | Un paquete `utils/` con validación, formateo de fechas y lógica de negocio mezclados. |
| Acoplamiento | Un controller depende de la interfaz de su service, no de la implementación concreta. | Un controller construye un repositorio directamente, saltándose el service. |

No son principios independientes de SOLID — son la razón *por qué* SOLID funciona: **S** produce alta cohesión (una responsabilidad por clase); **D** produce bajo acoplamiento (depender de interfaces, no de implementaciones).

**Ejemplo de referencia.** En LP2: `catalogo/producto` solo contiene clases sobre `Producto` (alta cohesión); `ProductoController` depende de la interfaz `ProductoService`, nunca de `ProductoServiceImpl` directamente (bajo acoplamiento). El caso más interesante para evaluar es el acoplamiento **entre** `producto` y `categoria` — dos paquetes hermanos del mismo módulo — que se analiza con el código real en 3.4.

El acoplamiento no es solo un asunto entre clases (una clase que depende de otra) — también aparece **entre contratos**: si el DTO de salida de una entidad reutiliza tal cual el DTO de otra entidad relacionada, el primero queda acoplado a cada cambio que sufra el segundo, aunque ese cambio no tenga nada que ver con lo que el primero necesita mostrar. Introducir un DTO más chico, dedicado solo a lo que se embebe, es la misma estrategia de bajo acoplamiento (Tabla 3) aplicada a contratos en vez de a clases — este caso también se evalúa con código real en 3.4.

**Figura 4. Dos formas de acoplamiento evaluadas en esta sesión**

```mermaid
flowchart TB
    subgraph Clases["Acoplamiento entre clases"]
        direction TB
        PSI["ProductoServiceImpl"]
        CR["CategoriaRepository<br/>(actual)"]
        CS["CategoriaService<br/>(alternativa)"]
        PSI -->|"actual"| CR
        PSI -.->|"alternativa, mas desacoplada"| CS
    end

    subgraph Contratos["Acoplamiento entre contratos"]
        direction TB
        PR["ProductoResponse"]
        CRes["CategoriaResumen<br/>(actual)"]
        CResp["CategoriaResponse<br/>(alternativa: reutilizar)"]
        PR -->|"actual"| CRes
        PR -.->|"alternativa, mas acoplada"| CResp
    end
```

Las dos mitades de la Figura 4 son el mismo patrón en dos niveles distintos, pero con un resultado opuesto — vale la pena notarlo, porque "actual" no significa lo mismo en las dos: en **"Acoplamiento entre clases"** (derecha), lo que el código real hace hoy (`ProductoServiceImpl` → `CategoriaRepository`) es la opción **más acoplada** de las dos; la de bajo acoplamiento (`CategoriaService`) es la alternativa que **no** se implementó — una tensión de diseño real, sin resolver, no un error. En **"Acoplamiento entre contratos"** (izquierda), en cambio, lo que el código real hace hoy (`CategoriaResumen`) **ya es** la opción de bajo acoplamiento; la alternativa (reutilizar `CategoriaResponse`) habría sido la más acoplada, y LP2 evitó tomarla. En ambos casos la flecha sólida es el código real y la punteada es la alternativa comparada en la Tabla 5 y la Tabla 6 (3.4) — pero solo uno de los dos casos ya está resuelto del lado del bajo acoplamiento. Ninguna de las dos mitades tiene una alternativa objetivamente "correcta": ambas son tensiones de diseño, no violaciones, y se documentan como tales en 3.6.

### 2.4 Modularidad y abstracción

**Modularidad**: el sistema se divide en unidades (módulos) con un límite explícito y verificable — no basta con una convención de carpetas; el límite debe poder comprobarse, idealmente de forma automática, para que nadie lo cruce sin darse cuenta.

**Abstracción**: exponer solo lo necesario, ocultando el detalle de implementación. Un DTO es abstracción sobre una entidad (el cliente HTTP nunca ve columnas de la base de datos, solo los campos que el contrato REST decide mostrar); una interfaz de servicio es abstracción sobre su implementación.

**Ejemplo de referencia.** En LP2, el límite de módulo es el paquete bajo `pe.edu.upeu.bomerp` (`catalogo`, `ventas`, ...), verificado automáticamente por Spring Modulith (`ModularityTests`, ver ADR-002 de LP2): un módulo solo puede llamar al `Service` público de otro, nunca a su `Repository` ni a su `Entity` directamente. `categoria` y `producto` (evaluados en esta sesión) son **paquetes dentro del mismo módulo** `catalogo`, no dos módulos distintos — la regla anterior es la que Spring Modulith verifica **entre módulos** (`catalogo` vs. `ventas`), no necesariamente dentro de uno mismo. En abstracción, `ProductoResponse` embebe `CategoriaResumen` (`id` y `nombre`) en vez de la entidad `Categoria` completa — el contrato REST expone solo lo que un cliente necesita. Estos dos casos reales se retoman en 3.4 y 3.5.

**Figura 5. Modularidad (límite entre módulos) y abstracción (DTO sobre entidad)**

```mermaid
flowchart TB
    subgraph Mod["Modularidad: limite verificado por Spring Modulith"]
        direction TB
        subgraph Catalogo["Modulo catalogo"]
            direction LR
            CatPkg["categoria"]
            ProdPkg["producto"]
        end
        Ventas["Modulo ventas (S4, futuro)"]
        Ventas -->|"permitido: Service publico"| CatPkg
        Ventas -.->|"prohibido, ModularityTests lo bloquea"| CatPkg
    end

    subgraph Abs["Abstraccion: el cliente HTTP nunca ve la entidad"]
        direction LR
        Cliente["Cliente HTTP"]
        DTO["ProductoResponse (DTO)"]
        Entidad["Producto (entidad JPA / columnas Oracle)"]
        Cliente --> DTO
        DTO -.->|"oculta"| Entidad
    end
```

La mitad "Modularidad" de la Figura 5 muestra por qué `producto` y `categoria` (dentro de `catalogo`) pueden acoplarse entre sí sin que `ModularityTests` proteste (2.3, 3.4): la regla que sí bloquea es la flecha punteada, cuando un módulo **distinto** (`ventas`, todavía inexistente) intenta saltarse el `Service` público de `catalogo`. La mitad "Abstracción" es el mismo principio de la Figura 4 (derecha) visto un nivel más abajo: el cliente HTTP nunca llega a `Producto` (la entidad), solo a `ProductoResponse` — el DTO es la frontera que oculta cada columna interna de la tabla `PRODUCTOS`.

## 3. Aplica: actividad práctica guiada

Tiempo: 2h.

**Actividad:** evaluación guiada de `catalogo/categoria` y `catalogo/producto` (código real de LP2, S1-S3) contra SOLID, cohesión, acoplamiento, modularidad y abstracción (Producto de la sesión en 1.4).

**Propósito de la actividad:** aplicar los criterios de 2.2-2.4 sobre clases reales, no hipotéticas, distinguiendo qué es un diseño correcto, qué es una tensión aceptable, y qué sería una violación real.

**Orientaciones metodológicas:** en el laboratorio, el docente evalúa `categoria` y `producto` paso a paso frente a la clase, con el código real de LP2 abierto; los estudiantes replican la misma evaluación sobre el módulo de su propio proyecto (sección 4).

**Actividades para realizar:**

- **3.1** Evaluar responsabilidad única (S) por clase.
- **3.2** Evaluar abierto/cerrado, Liskov e interfaces (O, L, I).
- **3.3** Evaluar inversión de dependencias (D).
- **3.4** Evaluar cohesión y acoplamiento entre `categoria` y `producto`.
- **3.5** Evaluar modularidad y abstracción.
- **3.6** Documentar el hallazgo real.
- **3.7** Relacionar con LP2 y BD2.

### 3.1 Evaluar responsabilidad única (S) por clase

**Producto del paso:** tabla de responsabilidad única sobre las clases reales de `producto`.

**Tabla 4. Responsabilidad única — `catalogo/producto`**

| Clase | Responsabilidad única | ¿Cumple? |
|---|---|---|
| `ProductoController` | Traducir HTTP ↔ llamadas al service; aplicar `@Valid`. | Sí — no contiene lógica de negocio ni acceso a datos. |
| `ProductoServiceImpl` | Orquestar la regla de negocio de `Producto` (CRUD + validar que la `Categoria` referenciada exista). | Sí, con matiz — ver 3.4: validar la referencia es parte de la responsabilidad de `Producto` ("un producto no puede crearse con una categoría inexistente"), no una responsabilidad ajena. |
| `ProductoRepository` | Acceso a datos de `Producto` vía Spring Data JPA. | Sí — una interfaz, sin lógica propia. |
| `ProductoMapper` | Convertir entre `ProductoRequest`/`Producto`/`ProductoResponse`. | Sí — MapStruct genera la implementación, cero lógica de negocio dentro. |
| `Producto` (entidad) | Representar los datos persistentes de un producto. | Sí — sin comportamiento de negocio, solo campos y su mapeo JPA. |

Repite la misma tabla para `categoria` (`CategoriaController`, `CategoriaServiceImpl`, `CategoriaRepository`, `CategoriaMapper`, `Categoria`) — el patrón es idéntico.

**Figura 6. S visto por contraste: hipotético vs. real**

```mermaid
classDiagram
    class Hipotetico["ProductoServiceImpl (hipotetico, viola S)"] {
        +crear(request)
        +validarFormato(request)
        +formatearLogAuditoria(producto)
        +enviarCorreoNotificacion(producto)
    }
    class Real["ProductoServiceImpl (real, Tabla 4)"] {
        +crear(request)
        +obtener(id)
        +actualizar(id, request)
        +eliminar(id)
    }
```

La versión hipotética junta persistencia, formato de logs y envío de correos — tres razones de cambio distintas. La real solo orquesta la regla de negocio de `Producto`; log y notificación no aparecen porque no son su responsabilidad.

### 3.2 Evaluar abierto/cerrado, Liskov e interfaces (O, L, I)

**Producto del paso:** análisis de extensibilidad sobre `ProductoService`/`CategoriaService`.

`ProductoService`/`CategoriaService` son **interfaces**, con una sola implementación cada una (`ProductoServiceImpl`/`CategoriaServiceImpl`) hoy. Eso ya deja el diseño **abierto** a extensión: si mañana se necesitara, por ejemplo, una versión con caché (`ProductoServiceCacheado implements ProductoService`), `ProductoController` no cambiaría una sola línea — solo cambiaría qué implementación se inyecta.

**Liskov (L):** cualquier implementación futura de `ProductoService` debe poder sustituir a `ProductoServiceImpl` sin que `ProductoController` note la diferencia — mismo contrato (misma firma, mismas excepciones esperadas: `ResourceNotFoundException` cuando el id no existe). Hoy no hay una segunda implementación, así que L se cumple **por diseño de la interfaz**, no por evidencia de sustitución real todavía.

**Segregación de interfaces (I):** `ProductoService` expone exactamente `listar`, `obtener`, `crear`, `actualizar`, `eliminar`, `listarPorCategoria` — todos usados por `ProductoController`. No hay un método "de más" que ningún cliente use. Compara esto con una interfaz hipotética `ServicioGenerico<T>` con quince métodos: forzaría a `ProductoServiceImpl` a implementar operaciones que `ProductoController` nunca llama.

**Figura 7. O y L: interfaz con una implementación real y una hipotética**

```mermaid
classDiagram
    class ProductoService {
        <<interface>>
        +listar()
        +obtener(id)
        +crear(request)
    }
    class ProductoServiceImpl["ProductoServiceImpl (real)"]
    class ProductoServiceCacheado["ProductoServiceCacheado (hipotetico)"]

    ProductoController --> ProductoService
    ProductoService <|.. ProductoServiceImpl
    ProductoService <|.. ProductoServiceCacheado
```

`ProductoController` solo conoce la interfaz; una segunda implementación (hipotética, en gris) podría sumarse sin tocarlo — eso es O. Que ambas puedan sustituirse sin romper el contrato de `ProductoController` es L.

### 3.3 Evaluar inversión de dependencias (D)

**Producto del paso:** verificación de que las dependencias reales van hacia abstracciones, no implementaciones.

```java
@RestController
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService; // interfaz, no ProductoServiceImpl
    ...
}

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;   // interfaz Spring Data JPA
    private final CategoriaRepository categoriaRepository; // interfaz Spring Data JPA
    private final ProductoMapper productoMapper;           // interfaz MapStruct
    ...
}
```

Las tres dependencias de `ProductoServiceImpl` son interfaces (`ProductoRepository`, `CategoriaRepository`, `ProductoMapper`), inyectadas por Spring vía `@RequiredArgsConstructor` — ninguna se instancia con `new`. Esto es Inversión de Dependencias real, no solo declarada: `ProductoServiceImpl` no sabe (ni le importa) si `ProductoRepository` lo implementa Spring Data JPA u otra tecnología de persistencia.

**Figura 8. D acotado: las tres dependencias inyectadas de `ProductoServiceImpl`**

```mermaid
classDiagram
    class ProductoServiceImpl
    class ProductoRepository {
        <<interface>>
    }
    class CategoriaRepository {
        <<interface>>
    }
    class ProductoMapper {
        <<interface>>
    }

    ProductoServiceImpl --> ProductoRepository : interfaz, no new
    ProductoServiceImpl --> CategoriaRepository : interfaz, no new
    ProductoServiceImpl --> ProductoMapper : interfaz, no new
```

Mismo recorte de la Figura 3 (2.2), acotado solo a D: ninguna flecha llega a una clase concreta.

### 3.4 Evaluar cohesión y acoplamiento entre `categoria` y `producto`

**Producto del paso:** justificación explícita del acoplamiento real entre los dos paquetes.

`ProductoServiceImpl` depende de `CategoriaRepository` directamente (no de `CategoriaService`):

```java
private Categoria buscarCategoriaOFallar(Long categoriaId) {
    return categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada: " + categoriaId));
}
```

**¿Es esto una violación?** No, y la razón importa más que la conclusión: `categoria` y `producto` son paquetes **dentro del mismo módulo** (`catalogo`), no dos módulos separados verificados por Spring Modulith (2.4). La regla "solo `Service` público, nunca `Repository` ajeno" protege el límite **entre módulos** (para que, por ejemplo, `ventas` nunca dependa del `Repository` interno de `catalogo`) — dentro de un mismo módulo, acoplar dos paquetes hermanos mediante sus repositorios es una decisión de diseño interna, no una violación arquitectónica.

**Dicho eso, es acoplamiento real y vale la pena nombrarlo:** si `producto` necesita validar contra `categoria` cada vez con más frecuencia (por ejemplo, en S4 al construir `ventas`, que también necesitará consultar `Producto`), la alternativa más desacoplada sería que `ProductoServiceImpl` dependiera de `CategoriaService` (la abstracción pública del paquete `categoria`) en vez de `CategoriaRepository` directamente — mismo principio de Inversión de Dependencias (2.2), aplicado ahora entre paquetes hermanos, no solo entre capas.

Esto es **deuda técnica intencional**, no accidental: la deuda accidental aparece sin que nadie la decida ni la documente; la intencional (o "prudente", en el cuadrante de deuda técnica de Fowler — ver Bibliografía) se toma a propósito, con la condición que la haría exigible ya identificada — en este caso, que `categoria` gane lógica de negocio propia que `producto` no debería saltarse. Documentar esa condición (como se hizo arriba) es lo que separa una tensión de diseño aceptable de una violación sin justificar.

**Tabla 5. Acoplamiento actual vs. alternativa más desacoplada**

| | Acoplamiento actual (real, LP2 S3) | Alternativa (vía `CategoriaService`) |
|---|---|---|
| Dependencia de `ProductoServiceImpl` | `CategoriaRepository` (acceso directo a datos) | `CategoriaService.obtener(id)` (contrato público) |
| Qué expone `categoria` hacia afuera | Su capa de persistencia completa | Solo lo que `CategoriaService` decide exponer |
| Costo de cambiar `categoria` internamente | Podría afectar a `producto` si cambia el `Repository` | `producto` no se entera mientras el contrato de `CategoriaService` no cambie |

**Figura 9. Acoplamiento entre clases, acotado a este caso**

```mermaid
flowchart LR
    PSI["ProductoServiceImpl"]
    CR["CategoriaRepository (actual)"]
    CS["CategoriaService (alternativa)"]
    PSI -->|"actual"| CR
    PSI -.->|"alternativa, mas desacoplada"| CS
```

La flecha alternativa no elimina la dependencia — la cambia de un detalle interno (`CategoriaRepository`, acceso a datos) a un contrato público y estable (`CategoriaService`). Menos acoplamiento no es cero dependencias: es depender de algo que cambia con menos frecuencia.

**Error frecuente al leer este diagrama:** pensar que la alternativa (vía `CategoriaService`) es "más acoplada" porque `CategoriaServiceImpl` internamente llama a `CategoriaRepository` (y quizás a más cosas). Eso no cuenta: el acoplamiento se mide por lo que `ProductoServiceImpl` conoce **directamente**, no por lo que su dependencia hace por dentro. `ProductoServiceImpl` nunca ve, importa ni compila contra lo que `CategoriaServiceImpl` usa internamente — solo contra el contrato de `CategoriaService`. Si `categoria` agregara mañana una regla de negocio (por ejemplo, "solo categorías activas"), esa regla llegaría automáticamente a `ProductoServiceImpl` si depende de `CategoriaService`, pero **no** si depende de `CategoriaRepository` directamente — ese es el riesgo real de la opción actual, no al revés.

No hay una respuesta única "correcta" — es una tensión real de diseño, y documentarla (con el criterio de arriba) es uno de los hallazgos que se espera en 3.6.

**Un segundo caso, esta vez de acoplamiento entre contratos:** `ProductoResponse` no reutiliza `CategoriaResponse` (el DTO que ya existe para exponer una categoría) — introduce `CategoriaResumen`, un DTO más chico, dedicado solo a lo que se embebe en un producto (`id`, `nombre`, sin `descripcion`).

**Tabla 6. Acoplamiento entre contratos: reutilizar vs. desacoplar**

| | Reutilizar `CategoriaResponse` en `ProductoResponse` | Introducir `CategoriaResumen` (real, LP2 S3) |
|---|---|---|
| Acoplamiento | El contrato de `/productos` cambia de forma cada vez que `CategoriaResponse` cambia, aunque el cambio no afecte a lo que un producto necesita mostrar. | El contrato de `/productos` solo cambia si `CategoriaResumen` cambia — independiente de `CategoriaResponse`. |
| Costo hoy | Ninguno — `descripcion` no es un dato sensible ni voluminoso. | Una clase más, un método de mapeo más (`CategoriaMapper.toResumen`). |
| Beneficio | Menos código. | El contrato de `/productos` no depende de decisiones futuras de `/categorias`. |

**Figura 10. Acoplamiento entre contratos, acotado a este caso**

```mermaid
flowchart LR
    PR["ProductoResponse"]
    CRes["CategoriaResumen (actual)"]
    CResp["CategoriaResponse (alternativa: reutilizar)"]
    PR -->|"actual"| CRes
    PR -.->|"alternativa, mas acoplada"| CResp
```

Igual que el caso anterior, no hay una respuesta única "correcta": con los hechos de hoy (nada sensible en `descripcion`, sin motivo de ocultamiento), reutilizar `CategoriaResponse` habría sido igual de válido — la decisión real de introducir `CategoriaResumen` es explícitamente anticipatoria. Vale la pena nombrarla como tal en el hallazgo (3.6), no presentarla como la única opción correcta.

### 3.5 Evaluar modularidad y abstracción

**Producto del paso:** confirmación de que el límite de módulo (`catalogo`) se respeta, y que las abstracciones (DTO, interfaces) cumplen su función.

- **Modularidad:** `catalogo` sigue siendo el único módulo con código real; `ModularityTests` (LP2) verifica que ningún otro módulo (todavía inexistente: `ventas` llega en S4) acceda a sus repositorios o entidades directamente. Nada que evaluar todavía más allá de eso — la prueba real es automática, no una opinión de diseño.

**Figura 11. Modularidad, acotada al límite `catalogo`-`ventas`**

```mermaid
flowchart LR
    Ventas["Modulo ventas (S4, futuro)"]
    CatSvc["categoria.CategoriaService (publico)"]
    CatRepo["categoria.CategoriaRepository (interno)"]
    Ventas -->|"permitido"| CatSvc
    Ventas -.->|"bloqueado por ModularityTests"| CatRepo
```

- **Abstracción — DTO:** `ProductoResponse` embebe `CategoriaResumen`, no la entidad `Categoria` completa — la abstracción entidad-DTO evita que la API pública dependa de cada campo interno de `Categoria`. El acoplamiento **entre** `CategoriaResumen` y `CategoriaResponse` (dos DTO, no una entidad y un DTO) ya se evaluó con más detalle en 3.4.

**Figura 12. Abstracción, acotada a DTO vs. entidad**

```mermaid
flowchart LR
    Cliente["Cliente HTTP"]
    DTO["ProductoResponse"]
    Entidad["Producto (entidad)"]
    Cliente --> DTO
    DTO -.->|"oculta"| Entidad
```

- **Abstracción — interfaces de servicio:** ya evaluado en 3.2/3.3; se repite aquí como confirmación de que la abstracción no es solo una interfaz vacía — realmente oculta la implementación (MapStruct, Spring Data JPA) del resto del sistema.

### 3.6 Documentar el hallazgo real

**Producto del paso:** al menos un hallazgo real, con su justificación o su corrección propuesta.

Ejemplo de hallazgo real de esta sesión (documentado en 3.4): *"`ProductoServiceImpl` depende de `CategoriaRepository` en vez de `CategoriaService`. No es una violación de `ModularityTests` (mismo módulo), pero si `producto` empieza a necesitar más operaciones sobre `categoria` a medida que el sistema crece, migrar a `CategoriaService` reduciría el acoplamiento entre ambos paquetes sin costo arquitectónico."* Tu evidencia individual (4.3.1) debe incluir un hallazgo equivalente, real, sobre el módulo de tu propio proyecto — no una copia de este.

### 3.7 Relacionar con LP2 y BD2

**Producto del paso:** matriz de integración de la sesión.

**Tabla 7. Matriz de integración ADS-LP2-BD2 (S3)**

| Criterio evaluado | Evidencia real en LP2 | Relación con BD2 |
|---|---|---|
| Responsabilidad única, inversión de dependencias | `ProductoServiceImpl`/`CategoriaServiceImpl` (LP2 S3) | — |
| Acoplamiento `producto` → `categoria` | `ProductoServiceImpl.buscarCategoriaOFallar()` (LP2 S3) | `FK_PRODUCTO_CATEGORIA` sobre `ID_CATEGORIA` (BD2 S1) — el acoplamiento a nivel de código refleja una relación real ya existente a nivel de esquema. |
| Modularidad | `ModularityTests` (LP2, desde S1) | — |
| Abstracción (DTO) | `CategoriaResumen`, `ProductoResponse` (LP2 S3) | — |

Sesión equivalente en los otros dos cursos, misma semana: [LP2 - S3 Objetos Relacionados Categoria-Producto](../../lp2/sesiones/S03_Objetos_Relacionados_Categoria_Producto.md) y [BD2 - S3 Manejo de Excepciones y Robustez](../../bd2/sesiones/S03_Excepciones_Robustez.md).

**Evidencia de aprendizaje:**

- Tabla de responsabilidad única (S) para `categoria` y `producto`.
- Análisis de abierto/cerrado, Liskov e interfaces (O, L, I) sobre `ProductoService`/`CategoriaService`.
- Verificación de inversión de dependencias (D) con código real.
- Evaluación justificada de cohesión/acoplamiento entre `categoria` y `producto`.
- Al menos un hallazgo real documentado.
- Matriz de integración con LP2 y BD2.

## 4. Crea: actividad autónoma

Tiempo: 2h fuera del aula.

### 4.1 Actividad

Evaluación autónoma de los principios SOLID, cohesión, acoplamiento, modularidad y abstracción sobre el módulo del proyecto propio del equipo, documentada en evidencia individual.

Completa y evidencia estas tareas:

1. Elaborar la tabla de responsabilidad única (S) de las clases reales de tu módulo.
2. Analizar abierto/cerrado, Liskov e interfaces (O, L, I) sobre tu(s) interfaz(ces) de servicio.
3. Verificar inversión de dependencias (D) con tu propio código (constructor injection, sin `new` de implementaciones concretas).
4. Evaluar cohesión y acoplamiento entre dos paquetes relacionados de tu módulo (o justificar por qué no aplica todavía).
5. Evaluar modularidad (si tu proyecto ya tiene más de un módulo) y abstracción (DTO vs. entidad).
6. Documentar al menos un hallazgo real, con su justificación o corrección propuesta.

### 4.2 Propósito

Que cada estudiante demuestre, de forma individual y fuera del aula, que puede aplicar los criterios de evaluación SOLID construidos en clase sin el acompañamiento del docente.

Esta actividad autónoma se desarrolla sobre el proyecto de fin de curso del equipo. El producto de la unidad se construye por acumulación de los avances de cada sesión; por eso, la evidencia de esta sesión debe incorporarse a la documentación del proyecto y quedar trazable en GitHub.

### 4.3 Indicaciones

Entrega un PDF con el siguiente nombre:

```text
S03_ADS_Equipo##_ApellidoNombre.pdf
```

Cada captura de pantalla del informe debe mostrar, sin recortar, el reloj del sistema (fecha y hora) y tu usuario o foto de perfil (Windows, VS Code o navegador) visibles en pantalla — es lo que permite verificar que la evidencia es tuya y que corresponde al momento real de tu trabajo.

#### 4.3.1 Estructura del informe

**Datos del estudiante**

- Nombre:
- Equipo:
- Sesión: S03 - Diseño Estructural y Principios SOLID
- Rol o aporte realizado:
- Link de GitHub:

**Evidencia técnica**

Incluye capturas o extractos con una breve explicación debajo de cada uno, organizados en los mismos 5 bloques de la rúbrica (4.6):

1. *Responsabilidad única (S)*
    - Tabla de responsabilidad única de tus clases reales.
2. *Abierto/cerrado, Liskov e interfaces (O, L, I)*
    - Análisis de extensibilidad de tu(s) interfaz(ces) de servicio.
3. *Inversión de dependencias (D)*
    - Código real mostrando inyección por interfaz, sin `new` de implementaciones.
4. *Cohesión, acoplamiento, modularidad y abstracción*
    - Evaluación justificada entre dos paquetes relacionados de tu módulo.
5. *Hallazgo real*
    - Hallazgo documentado, con justificación o corrección propuesta.

**Error o hallazgo**

Describe al menos un hallazgo real de diseño (no necesariamente un error — puede ser una tensión de diseño justificada, como el de 3.4).

**Reflexión técnica breve**

Responde en 5 a 8 líneas:

```text
¿Por qué el acoplamiento entre dos paquetes del mismo módulo no es
automáticamente una violación de diseño, y qué lo diferencia del
acoplamiento entre dos módulos distintos?
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
- Presenta la tabla de responsabilidad única (S) sobre clases reales de su módulo.
- Analiza abierto/cerrado, Liskov e interfaces (O, L, I) con argumentos concretos, no genéricos.
- Verifica inversión de dependencias (D) con código real (constructor injection por interfaz).
- Evalúa cohesión y acoplamiento entre al menos dos paquetes relacionados, con justificación.
- Incluye al menos un hallazgo real de diseño, justificado o con corrección propuesta.
- Cada captura de la evidencia técnica muestra el reloj del sistema y el usuario/perfil visible, sin recortar.
- Las fechas y horas de las capturas son coherentes con el historial de commits de su repositorio en GitHub.
- Incluye la reflexión técnica breve solicitada.
- Incluye el Anexo de feedback de la sesión respondido, como última página del PDF.

### 4.5 Preguntas de defensa

1. ¿Qué diferencia hay entre una violación real de SOLID y una tensión de diseño aceptable?
2. ¿Por qué `ProductoController` depende de la interfaz `ProductoService` y no de `ProductoServiceImpl` directamente?
3. ¿Qué significa que `ProductoService` esté "abierto a extensión, cerrado a modificación"?
4. ¿Por qué el acoplamiento entre `producto` y `categoria` no es una violación de `ModularityTests`?
5. ¿Qué diferencia hay entre exponer una entidad JPA directamente y exponer un DTO?

### 4.6 Rúbrica de evaluación

**Tabla 8. Rúbrica de evaluación**

| Criterio | Peso (%) | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Nivel obtenido |
|---|---:|---|---|---|---|---:|
| 1. Responsabilidad única (S)* | 20 | Tabla completa y correcta sobre clases reales, con matices bien argumentados. | Tabla correcta, con algún matiz superficial. | Tabla incompleta o con responsabilidades mal identificadas. | No presenta la tabla. | |
| 2. Abierto/cerrado, Liskov e interfaces (O, L, I)* | 20 | Análisis concreto y correcto de los tres principios sobre código real. | Análisis correcto pero genérico en algún principio. | Análisis superficial o con confusión entre principios. | No presenta el análisis. | |
| 3. Inversión de dependencias (D)* | 20 | Verificación clara con código real, identificando cada dependencia inyectada. | Verificación correcta, con algún detalle faltante. | Verificación parcial o poco clara. | No verifica D. | |
| 4. Cohesión, acoplamiento, modularidad y abstracción* | 20 | Evaluación justificada y correcta, distinguiendo acoplamiento aceptable de violación real. | Evaluación correcta, con justificación básica. | Evaluación superficial o sin distinguir niveles de acoplamiento. | No evalúa cohesión/acoplamiento. | |
| 5. Hallazgo real* | 20 | Hallazgo real, bien justificado, con corrección propuesta o argumento sólido de por qué no se corrige. | Hallazgo real, con justificación básica. | Hallazgo genérico o poco fundamentado. | No presenta hallazgo. | |

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

**Resumen breve:** hoy se evaluó el código real de LP2 (`catalogo/categoria`, `catalogo/producto`) contra los cinco principios SOLID, cohesión, acoplamiento, modularidad y abstracción — cerrando el vacío que S2 dejó marcado explícitamente (la relación `Producto`-`Categoria`), y documentando una tensión de diseño real (el acoplamiento de `producto` hacia `CategoriaRepository`) sin forzarla a una corrección automática.

**Dinámica participativa:** en una ronda rápida, cada estudiante comparte en una frase el hallazgo real que encontró en el módulo de su propio proyecto.

**Metacognición:** cada estudiante responde el Anexo de feedback de la sesión, incluido en su evidencia individual (ver 4.3.1). El docente analiza esas respuestas con IA para identificar temas recurrentes o dudas comunes del equipo, y con esos indicadores construye el cierre real de la sesión — que se entrega al inicio de S4, no al final de esta clase.

**Proyección:** la evaluación estructural de hoy es la base de S4, donde se comparan estilos arquitectónicos completos (monolito modular, hexagonal, microservicios) y se retoma la vista de despliegue — un módulo con buena cohesión y bajo acoplamiento es más fácil de migrar entre estilos que uno que ya arrastra violaciones de SOLID.

## Bibliografía

1. Martin, R. C. (2003). *Agile Software Development: Principles, Patterns, and Practices*. Prentice Hall.
2. Martin, R. C. (2017). *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall.
3. Baeldung. (2024). *SOLID Principles*. https://www.baeldung.com/solid-principles
4. Fowler, M. (2024). *CouplingAndCohesion*. martinfowler.com. https://martinfowler.com/ieeeSoftware/coupling.pdf
5. Fowler, M. (2019). *TechnicalDebtQuadrant*. martinfowler.com. https://martinfowler.com/bliki/TechnicalDebtQuadrant.html
6. Broadcom Inc. (2025). *Spring Framework reference documentation — Dependency Injection*. VMware Tanzu. https://docs.spring.io/spring-framework/reference/core/beans/dependencies.html

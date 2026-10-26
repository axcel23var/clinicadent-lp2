# S9 - Diagramas Dinámicos UML

## 1. Introducción

Tiempo: 20 min.

### 1.1 Presentación de la sesión

S8 cerró el diagrama de clases de diseño del módulo `ventas` por capas (Figura 6 de esa guía, explícitamente ubicada como "diagrama de código, nivel 4 de C4" — el zoom máximo del modelo C4, *Context, Container, Component, Code*, Brown, 2024, dentro de **un solo** componente del nivel 3): qué clases existen, de qué capa es cada una y quién depende de quién. Ese diagrama es **estructural** — una fotografía fija de las piezas y sus conexiones posibles —, y para la mayoría de las operaciones de `ventas` esa fotografía ya basta: `buscar` y `obtener` son una llamada directa, sin ramas que se combinen ni colaboraciones repetidas, y la ficha de clase de S8 (Tabla 13) ya dice todo lo que hace falta saber sobre ellas. `crear`, en cambio, no se deja explicar así: ¿en qué orden exacto colaboran las clases cuando una venta se registra de verdad?, ¿qué pasa si el producto no existe, y qué pasa si no hay stock, y qué pasa si eso ocurre a mitad de un bucle sobre varias líneas? Una misma flecha de dependencia (`VentaServiceImpl --> ProductoService`) puede significar una llamada, cien llamadas en un bucle, o ninguna si una condición falla antes — el diagrama de clases no distingue esos casos, y por eso una operación como `crear` sí necesita una vista adicional.

Esta sesión trabaja principalmente en el **mismo nivel 4 (Código)** que fijó S8, agregando el **comportamiento en el tiempo** de esas mismas clases con diagramas de secuencia, de actividades, el ciclo de vida de `Venta` y una vista alternativa de la misma colaboración (comunicación). El modelo C4 nombra este complemento el *Dynamic diagram* — un diagrama suplementario, no un quinto nivel, que muestra cómo colaboran en tiempo de ejecución los elementos de **cualquiera** de los cuatro niveles estáticos, y que el propio C4 model define de forma explícita a partir de un diagrama de comunicación UML (*Unified Modeling Language*, Brown, 2024). Esa flexibilidad de nivel no es solo una nota al pie: 2.6 la usa de verdad, mostrando la misma colaboración de `crear` una vez más, ahora a nivel Componente (C3) en vez de Código — la misma historia, contada con módulos completos en vez de clases.

**Regla general, no solo de esta sesión.** Ningún diagrama dinámico de hoy introduce una clase que no esté ya en la Figura 6 de S8: `VentaController`, `VentaServiceImpl`, `ProductoService`, `VentaMapper` y `VentaRepository` son exactamente los mismos participantes, vistos ahora en movimiento en vez de en reposo. Un diagrama de secuencia, de actividades o de comunicación no es un modelo aparte con sus propios elementos — es **el mismo** diagrama de clases, leído desde otro ángulo. El criterio para decidir si hace falta un diagrama dinámico más (hoy, en cualquier otro módulo de BomERP, o en cualquier proyecto futuro) es siempre el mismo: se agrega uno nuevo cuando el diagrama de clases, por sí solo, deja de alcanzar para entender cómo fluye un caso de negocio concreto —su orden, sus decisiones combinadas, su concurrencia—, nunca para describir clases o relaciones que el diagrama de clases todavía no tiene. Si un escenario necesitara una clase que no existe en la Figura 6, la tarea no es dibujarla dentro del diagrama de secuencia: es volver a S8 y extender el diagrama de clases primero. 2.2 convierte esta regla en un criterio verificable, con ejemplos reales de `ventas` a ambos lados.

El porqué de modelar explícitamente el orden en el tiempo y la continuidad del estado de un objeto —no solo las dependencias que ya existen— se desarrolla en 1.6, a partir de un caso real reciente.

### 1.2 Índice

1. Cuándo hace falta (y cuándo no) un diagrama dinámico adicional.
2. Diagrama de secuencia: colaboración ordenada en el tiempo.
3. Diagrama de actividades: flujo de decisión del proceso de negocio.
4. Ciclo de vida de objetos (diagrama de estados, opcional).
5. Interacción entre componentes: la misma colaboración, a otro nivel de zoom.
6. Diagrama de comunicación: la misma colaboración, otra vista.

### 1.3 Propósito de aprendizaje

Al concluir la clase, estarás en condiciones de:

- **Decidir, con un criterio verificable**, qué operaciones de tu propio diseño necesitan un diagrama dinámico y cuáles no, **modelar** la que sí lo necesita con secuencia, actividades, ciclo de vida, interacción entre componentes y comunicación —reutilizando siempre las mismas clases del diagrama de S8—, y **verificar** que responsabilidades, mensajes y componentes sean consistentes entre todos los diagramas dinámicos y el diagrama de clases.

### 1.4 Producto de sesión

Catálogo de diagramas dinámicos del escenario "registrar una venta" de BomERP: la decisión documentada de qué operaciones de `VentaController` sí necesitan un diagrama dinámico y cuáles no, con su justificación; diagrama de secuencia de la operación elegida (`crear`) con los casos alternativos de error (producto inexistente, stock insuficiente); diagrama de actividades equivalente organizado por carriles (*swimlanes*); diagrama de ciclo de vida de `Venta` (con sus estados diseñados desde S7, y la brecha real frente a S8/LP2 identificada); diagrama de interacción entre los componentes `ventas` y `catalogo` para el mismo escenario, a nivel C3; diagrama de comunicación de la misma colaboración del diagrama de secuencia; y una matriz de consistencia que cruza cada mensaje contra las fichas de clase de S8.

### 1.5 Metodología

**Tabla 1. Metodología de la sesión**

| Actividades a Realizar en el Periodo | Orientaciones generales (Orientaciones Metodológicas) | Material de estudio recomendado |
|---|---|---|
| Revisión previa individual | Releer el diagrama de clases por capas del módulo propio (S8, Figura 6) y las fichas de clase (S8, Tablas 12-15): qué recibe y devuelve cada operación. Trabajo individual, antes de clase. | S8 (Figura 6, Tablas 10-15). |
| Clase presencial | Decisión guiada de qué operaciones de `ventas` necesitan diagrama dinámico, y construcción de los diagramas de secuencia, actividades, ciclo de vida, interacción entre componentes y comunicación de la operación elegida, con la matriz de consistencia final. Trabajo individual, siguiendo al docente paso a paso; consulta inmediata ante dudas sobre fragmentos alternativos o concurrencia. | Pasos 3.1 a 3.7 de esta guía. |
| Evaluación formativa | Revisión en clase del criterio de decisión aplicado, del diagrama de secuencia y de la matriz de consistencia. La evidencia se completa y sustenta de forma individual, fuera del aula, según los criterios mínimos de la sección 4.4. | Indicaciones de entrega (4.3), rúbrica de evaluación (4.6). |

### 1.6 Motivación de la sesión

#### 1.6.1 Caso: los seis segundos que el auto autónomo de Uber perdió reclasificando a una persona

El 18 de marzo de 2018, en Tempe (Arizona), un vehículo autónomo de pruebas de Uber atropelló y mató a Elaine Herzberg mientras cruzaba una calle fuera de un paso peatonal señalizado. El sistema la detectó **6 segundos antes del impacto** — tiempo de sobra para frenar —, pero durante esos 6 segundos el software de percepción no logró sostener una identidad estable para ese objeto: lo clasificó primero como "objeto desconocido", después como "vehículo" y después como "bicicleta", y **cada vez que cambiaba de clasificación, descartaba el historial de trayectoria** que ya había acumulado, porque el sistema no tenía una categoría de predicción de ruta para un peatón que cruza fuera de un cruce señalizado.

Fuente: National Transportation Safety Board. (2019). *Collision Between Vehicle Controlled by Developmental Automated Driving System and Pedestrian* (Highway Accident Report NTSB/HAR-19/03). https://www.ntsb.gov/investigations/accidentreports/reports/har1903.pdf

Recién 1.3 segundos antes del impacto el sistema determinó que hacía falta frenar de emergencia — pero el frenado automático de emergencia estaba deshabilitado a propósito durante las pruebas (para evitar maniobras "erráticas"), y la conductora de respaldo, que podía haber frenado a mano, no estaba atenta a la vía en ese momento.

El problema de fondo no fue "clasificar mal" — fue tratar cada reclasificación como si fuera un objeto **nuevo**, sin un ciclo de vida propio, en vez de la misma entidad que cambia de estado conservando su historia. Ningún diagrama de clases hubiera mostrado esto: las clases "objeto desconocido", "vehículo" y "bicicleta" podían estar perfectamente diseñadas por separado, cada una con sus propios atributos correctos. Lo que faltaba era exactamente lo que esta sesión construye: un diagrama de estados que dejara explícito qué información se conserva (y qué no debería perderse nunca) cuando un mismo objeto transita de un estado a otro, y un diagrama de secuencia con los tiempos reales, que hubiera hecho visible la ventana de 6 segundos desperdiciada antes de que el sistema decidiera actuar.

**Preguntas de análisis**

**Activación de conocimientos previos**

1. Antes de leer la causa técnica, ¿por qué "detectar algo a tiempo" no sirve de nada si el sistema no conserva lo que ya sabía sobre ese mismo objeto?

**Comprensión de los diagramas dinámicos**

1. Según el caso, ¿qué tendría que mostrar un diagrama de estados del objeto detectado para que el equipo notara que cada reclasificación borraba el historial de trayectoria acumulado?
2. En tu propio proyecto, ¿hay algún objeto (como `Venta`) cuyo comportamiento dependa de en qué estado se encuentra, y no solo de qué datos tiene?

### 1.7 Ubicación en el curso

- Producto del curso: Diseño Técnico Profesional Documentado.
- Producto de unidad: Catálogo UML con patrones de diseño e integración aplicados.
- Avance del producto en esta sesión: diagramas dinámicos (secuencia, actividades, ciclo de vida, interacción entre componentes, comunicación) de un escenario crítico de un módulo, verificados contra las clases y fichas de S8.

**Figura 1. Roadmap del producto de la unidad**

```mermaid
flowchart TB
    S6["`**S6:** Descubrimiento y modelado del dominio`"]
    S7["`**S7:** Diseño de clases del dominio`"]
    S8["`**S8:** Diseño de clases avanzado y transformación OR`"]
    S9["`**S9:** Diagramas dinámicos UML`"]
    S10["`**S10:** Patrones de diseño (GoF y GRASP)`"]
    S11["`**S11:** Integración y sistemas empresariales`"]
    S12["`**S12:** Producto U2`"]

    S6 --> S7 --> S8 --> S9 --> S10 --> S11 --> S12

    classDef today fill:#ffe08a,stroke:#9a6b00,stroke-width:2px,color:#111;
    class S9 today;
```

## 2. Explica

Tiempo: 40 min.

### 2.1 Arquitectura de la sesión

**Figura 2. Del diagrama de clases por capas a los diagramas dinámicos**

```mermaid
flowchart TB
    A[Diagrama de clases por capas<br/>S8, Figura 6] --> Dec{"Necesita diagrama<br/>dinamico? (2.2)"}
    Dec -- No --> Ficha[La ficha de clase de S8<br/>ya explica todo]
    Dec -- Si --> B[Diagrama de secuencia:<br/>mensajes en el tiempo]
    B --> C[Diagrama de actividades:<br/>flujo de decision por carriles]
    A --> D[Ciclo de vida de objetos:<br/>estados y transiciones]
    B --> G[Interaccion entre componentes:<br/>mismo escenario, nivel C3]
    B --> E[Diagrama de comunicacion:<br/>misma colaboracion, otra vista]
    C --> F[Matriz de consistencia:<br/>responsabilidades, mensajes, componentes]
    D --> F
    G --> F
    E --> F
```

Lectura del diagrama: no toda operación de la Figura 6 de S8 llega al mismo punto. Primero se decide (2.2) si la operación necesita un diagrama dinámico; si no lo necesita, la ficha de clase de S8 queda como la documentación completa, sin diagramas adicionales. Si sí lo necesita, los cinco diagramas de hoy convergen en una sola verificación — la matriz de consistencia — que confirma que ninguno contradice al diagrama de clases ni a los otros. Cuatro de los cinco viven dentro del **mismo nivel 4 (Código) de C4** que ya fijó la Figura 6 de S8 — ninguno dibuja un componente nuevo ni cruza a nivel 3 (salvo la llamada puntual a `ProductoService`, que ya era una dependencia entre componentes documentada en S8) —; el quinto, la interacción entre componentes (2.6), sube deliberadamente a nivel 3, mostrando la misma colaboración sin bajar a clases. Todos corresponden, en conjunto, a lo que el propio modelo C4 llama un *Dynamic diagram* (1.1): la vista suplementaria que muestra cómo colaboran en tiempo de ejecución los elementos que un diagrama estático ya fijó, en el nivel que haga falta. Cada apartado siguiente desarrolla uno de los bloques, en el mismo orden del Índice (1.2).

### 2.2 Cuándo hace falta (y cuándo no) un diagrama dinámico adicional

Dibujar un diagrama dinámico para cada operación del sistema —incluidas las triviales— no agrega información: solo repite, en otro formato, lo que la ficha de clase de S8 ya dice en una fila. El criterio para decidir si una operación concreta lo necesita no es "¿esta operación es importante?", sino una pregunta más precisa: **¿el diagrama de clases y la ficha de S8 ya explican por completo cómo fluye esta operación, o hay algo —orden, ramas combinadas, repetición, coordinación entre componentes— que solo se ve dibujando el tiempo?**

**Tabla 2. Señales de que una operación necesita un diagrama dinámico**

| Señal | ¿Aporta un diagrama dinámico? | Ejemplo real en `VentaController` (S8) |
|---|---|---|
| Una sola llamada, sin bucles ni ramas que se combinen | No — la ficha de clase ya lo dice completo | `obtener(id)` (S8, Tabla 13): una búsqueda, un `404` si no existe. Una fila de tabla alcanza. |
| Varios filtros opcionales, pero sin colaboración entre distintos objetos | No — sigue siendo una sola llamada al repositorio | `buscar(...)`, `resumen(...)` (S8, Tabla 13): arman un `Sort` y delegan; no hay nada que una secuencia muestre mejor que la firma de la operación. |
| Bucle sobre una colección, con una colaboración por elemento | Sí — un bucle no se ve en una fila de tabla | `crear(request)`: una iteración por cada línea de `detalles`, con dos llamadas a `ProductoService` en cada vuelta. |
| Más de una condición que se combina o se anida (no solo "si falla, error X") | Sí — las condiciones compuestas se pierden en una tabla plana | `crear`: ¿existe el producto? Y **solo si existe**, ¿hay stock suficiente? |
| Colaboración entre dos o más componentes/módulos distintos | Sí — una sola flecha de dependencia no distingue una llamada de muchas, ni en qué orden | `crear` llama al `ProductoService` del módulo `catalogo`, dentro del bucle; `buscar`/`obtener` no colaboran con ningún otro módulo. |
| Comportamiento "todo o nada" (transaccional) que depende del orden de varias llamadas | Sí — el efecto conjunto no cabe en una ficha de una sola operación | `crear` (RN6, S8): si cualquier línea falla, toda la venta y todo el stock descontado se deshacen. |

Aplicando esta tabla a las cuatro operaciones reales de `VentaController` (S8, Figura 6): `buscar`, `obtener` y `resumen` no activan ninguna señal — son una llamada directa, sin bucles, sin ramas combinadas, sin colaboración entre módulos. `crear` activa las cuatro señales de la columna "Sí" a la vez. Por eso esta sesión construye los diagramas dinámicos únicamente sobre `crear` (3.1), y no sobre las otras tres operaciones del mismo controlador — no por ser la más importante, sino por ser la única donde el diagrama de clases deja de alcanzar.

### 2.3 Diagrama de secuencia: colaboración ordenada en el tiempo

Un **diagrama de secuencia** muestra los objetos que participan en un escenario como líneas verticales (*lifelines*) y los mensajes entre ellos como flechas horizontales, leídas de arriba hacia abajo en el orden en que ocurren (OMG, 2017). Es el diagrama dinámico más directo para responder la pregunta que el diagrama de clases no puede: en un escenario concreto, ¿quién llama a quién, en qué orden, y qué pasa si algo falla a mitad de camino? Larman (2004) lo ubica, junto con las tarjetas CRC (*Class-Responsibility-Collaboration*), como una de las herramientas más usadas en la práctica para explorar cómo se reparten las responsabilidades entre objetos durante el diseño — no solo para documentar después de programar, sino para decidir antes el reparto de la Figura 6 de S8.

**Tabla 3. Elementos de un diagrama de secuencia en Mermaid**

| Elemento | Sintaxis Mermaid | Qué representa |
|---|---|---|
| Participante | `participant VentaController` | Un objeto o actor del escenario — siempre una clase que ya existe en el diagrama de S8 (1.1). |
| Mensaje síncrono | `A->>B: mensaje(parametros)` | Una llamada a operación; quien llama espera la respuesta. |
| Mensaje de retorno | `B-->>A: resultado` | El valor devuelto por la llamada. |
| Activación | `activate B` / `deactivate B` | El objeto `B` está ejecutando la operación recibida. |
| Fragmento alternativo | `alt condición ... else ... end` | Caminos que se excluyen entre sí — por ejemplo, producto existe o no existe. |
| Fragmento de repetición | `loop por cada detalle ... end` | Un bloque de mensajes que se repite — por ejemplo, una línea por cada producto de la venta. |
| Fragmento paralelo | `par rama A ... and rama B ... end` | Dos o más bloques de mensajes que ocurren **al mismo tiempo**, no uno después del otro. |

Dos detalles no son decorativos: la flecha `->>` (síncrona, continua) es distinta de `-->>`(el retorno, discontinua) — confundirlas hace que el diagrama diga que el emisor no espera respuesta cuando sí la espera, y por eso el diagrama "miente" sobre el orden real. Y un fragmento `alt` sin su `else` oculta exactamente el tipo de camino alternativo (un error, una condición de borde) que conviene dejar explícito, no implícito en la cabeza de quien lo dibujó.

**`par`, para cuando sí hace falta.** Ninguno de los fragmentos anteriores (`alt`, `loop`) sirve para mostrar dos cosas ocurriendo **a la vez** — para eso existe `par`, que declara ramas concurrentes en vez de secuenciales. Es útil, por ejemplo, cuando un sistema dispara dos llamadas independientes al mismo tiempo (notificar a dos servicios externos, dos procesos que no dependen entre sí) y el orden relativo entre ellas importa para detectar un riesgo real: sin `par`, un diagrama de secuencia da a entender, por omisión, que todo ocurre uno después de otro, aunque en producción corra en paralelo — y dos flujos que comparten un dato sin ningún mecanismo que los sincronice son, justamente, el tipo de riesgo que un diagrama puramente secuencial no puede exponer. El escenario de `crear` que esta sesión modela (3.2) es, a propósito, **secuencial de punta a punta** — no lanza nada en paralelo, y por eso no usa `par` —; cualquier escenario futuro de BomERP que sí ejecute dos llamadas a la vez debe usar este fragmento para que ese riesgo quede visible antes de programarlo, no después de un incidente.

**Un primer ejemplo, deliberadamente simple.** Antes de enfrentar un escenario con bucles y condiciones anidadas (3.2), conviene fijar la notación de la Tabla 3 sobre algo corto. `obtener(id)` (S8, Tabla 13) sirve para eso: una sola búsqueda, un único fragmento `alt`, sin repetición ni colaboración entre módulos.

**Figura 3. Diagrama de secuencia simple: consultar una venta por id**

```mermaid
sequenceDiagram
    actor Vendedor
    participant VC as VentaController
    participant VS as VentaServiceImpl
    participant VR as VentaRepository
    participant VM as VentaMapper

    Vendedor->>VC: GET /api/v1/ventas/{id}
    activate VC
    VC->>VS: obtener(id)
    activate VS
    VS->>VR: findById(id)
    activate VR
    alt venta no existe
        VR-->>VS: ResourceNotFoundException (404)
    else venta existe
        VR-->>VS: Venta
        deactivate VR
        VS->>VM: toResponse(venta)
        VM-->>VS: VentaResponse
    end
    deactivate VS
    VS-->>VC: VentaResponse
    VC-->>Vendedor: 200 OK
    deactivate VC
```

Cinco elementos de la Tabla 3 aparecen aquí y en ningún otro orden: una llamada síncrona (`->>`), su retorno (`-->>`), activación/desactivación, y un único `alt` con su `else`. No hay `loop` ni `par` porque no hacen falta — esas piezas se introducen recién en 3.2, cuando el escenario las exige de verdad.

Este primer ejemplo no contradice la decisión de 2.2: `obtener` sigue sin necesitar un diagrama dinámico en el catálogo real de esta sesión — una sola fila de la Tabla 13 de S8 ya la explica por completo (3.1 la deja fuera a propósito). Se dibuja aquí únicamente como ejercicio de lectura de la notación, antes de aplicarla al escenario que sí la necesita.

### 2.4 Diagrama de actividades: flujo de decisión del proceso de negocio

Un **diagrama de actividades** modela el flujo de un proceso como una serie de pasos conectados por transiciones, con nodos de decisión (rombos) donde el flujo se bifurca según una condición (OMG, 2017). A diferencia del diagrama de secuencia —centrado en qué objeto envía cada mensaje—, el diagrama de actividades se centra en la **lógica del proceso**: qué pasa primero, qué decisión se toma, y qué ocurre en cada rama. Mermaid no tiene un tipo de diagrama llamado "actividad"; se construye con `flowchart`, el mismo tipo ya usado en S2 y S8 para otros propósitos — la diferencia es de contenido (un proceso de negocio con decisiones), no de sintaxis.

**Carriles (*swimlanes*).** Cuando un proceso cruza varias capas o roles — como ocurre siempre que el escenario llega hasta el backend —, conviene agrupar los pasos por quién los ejecuta. Mermaid no tiene *swimlanes* nativos; esta guía usa `subgraph` (un rectángulo con título) para representar cada carril, mismo recurso que ADS ya usa para otros propósitos.

**Tabla 4. Diagrama de secuencia frente a diagrama de actividades**

| | Diagrama de secuencia | Diagrama de actividades |
|---|---|---|
| Pregunta que responde | ¿Quién envía qué mensaje a quién, y en qué orden? | ¿Qué decisión se toma, y qué camino sigue el proceso en cada caso? |
| Eje principal | Los objetos (una columna por clase) | Los pasos del proceso (uno por nodo) |
| Mejor para | Verificar contratos entre clases concretas | Explicar el proceso de negocio a alguien que no programa |
| Nodo de decisión | Fragmento `alt` | Rombo con una condición |

### 2.5 Ciclo de vida de objetos (diagrama de estados, opcional)

Un **diagrama de estados** (o de ciclo de vida de objetos) muestra los estados posibles de un objeto y las transiciones válidas entre ellos — y, por omisión, cualquier transición *no* dibujada queda prohibida (OMG, 2017). No todo objeto necesita uno: el mismo criterio de 2.2 aplica aquí, con una pregunta más específica — tiene sentido solo cuando el **comportamiento** de un objeto depende de en qué estado se encuentra, no solo de sus datos. El propio sílabo marca este diagrama como "opcional" dentro del catálogo dinámico, precisamente porque esa condición no siempre se cumple.

El caso de 1.6 es, en el fondo, un problema de ciclo de vida mal modelado: el sistema trataba cada reclasificación del mismo objeto como si fuera una entidad nueva, descartando la historia de trayectoria que ya había acumulado, en vez de una transición de estado que conserva lo aprendido hasta ese momento. Un diagrama de estados completo, con sus transiciones explícitas y lo que cada una conserva o descarta, es una de las formas más directas de detectar ese tipo de hueco antes de escribir el código.

**Cuándo sí vale la pena este diagrama:**

- El objeto tiene al menos dos estados distintos con comportamiento o permisos diferentes (por ejemplo, una venta `REGISTRADA` se puede anular; una `ANULADA` no se puede volver a anular).
- Existen reglas que prohíben ciertas transiciones (una venta `ANULADA` nunca vuelve a `REGISTRADA`).

**Cuándo no aporta:** un objeto con un único estado posible, o cuyo único "estado" es en realidad un atributo que no cambia el comportamiento del resto del sistema — documentarlo igual sería dibujar un diagrama con un solo nodo y ninguna transición, que no añade información sobre la estructura de clases que S8 no tenga ya.

### 2.6 Interacción entre componentes: la misma colaboración, a otro nivel de zoom

Los diagramas de 2.3 a 2.5 se quedan dentro de un solo componente: muestran las clases de `ventas` y, cuando corresponde, una llamada puntual a `ProductoService` de `catalogo` — siempre a nivel Código (C4, nivel 4). Pero el *Dynamic diagram* de C4 (1.1) no está atado a ese nivel: la misma pregunta —¿quién colabora con quién, y en qué orden?— se puede hacer también a nivel Componente (C3), reemplazando cada clase por el módulo completo al que pertenece.

Un diagrama de **interacción entre componentes** muestra esa misma colaboración, pero con cajas de componente en vez de clases: en vez de `VentaServiceImpl --> ProductoService`, se dibuja `ventas --> catalogo`. No es un diagrama con reglas distintas — es el mismo diagrama de secuencia o de comunicación de siempre, construido sobre el diagrama de componentes de S2 en vez de sobre el diagrama de clases de S8.

**Tabla 5. Mismo escenario, dos niveles de zoom**

| | Código (C4, Figura 4) | Componente (C3, Figura 7) |
|---|---|---|
| Participantes | Clases concretas: `VentaController`, `VentaServiceImpl`, `ProductoService`, `VentaMapper`, `VentaRepository` | Módulos completos: `ventas`, `catalogo` |
| Qué se ve | Cada capa, cada clase, cada responsabilidad interna | Solo qué módulo le pide qué a cuál, y cuántas veces |
| Para quién | Quien programa dentro de `ventas`: necesita saber qué hace cada clase | Quien revisa la arquitectura o el diagrama de componentes de S2: no necesita (ni debe) ver clases internas de `catalogo` |
| Corresponde a | S8, Figura 6 (diagrama de código) | S2 (diagrama de componentes, C3) |

Esta vista es la que corresponde directamente al diagrama de componentes estático de S2 (C3): si ese diagrama ya dibujaba una flecha `ventas → catalogo`, esta sección muestra, para el escenario `crear`, cuántas veces se usa esa flecha y en qué orden — la misma relación entre lo estático y lo dinámico que el resto de la sesión aplica a nivel de clases, ahora aplicada a nivel de componentes.

### 2.7 Diagrama de comunicación: la misma colaboración, otra vista

El **diagrama de comunicación** (antes llamado de colaboración) representa la misma información que un diagrama de secuencia —qué objetos colaboran y qué mensajes se envían— pero organizada **espacialmente**, alrededor de las relaciones entre objetos, con los mensajes numerados en vez de ordenados verticalmente (OMG, 2017). Mermaid no tiene un tipo de diagrama de comunicación nativo; esta guía lo representa con `flowchart`, etiquetando cada arista con el número de secuencia del mensaje (`1`, `1.1`, `1.2`, ...) — la misma convención de numeración que usa el estándar UML para este diagrama.

Esta no es una elección libre de notación: el modelo C4 define su propio *Dynamic diagram* (1.1, 2.1) explícitamente "basado en un diagrama de comunicación UML" (Brown, 2024) — es la vista que el propio C4 model recomienda para mostrar colaboración en tiempo de ejecución, precisamente con mensajes numerados en vez de líneas de vida verticales. El diagrama de esta sección, aplicado hoy al nivel 4 (Código) del módulo `ventas`, es ese *Dynamic diagram* en su forma más literal.

**Tabla 6. Diagrama de secuencia frente a diagrama de comunicación**

| | Diagrama de secuencia | Diagrama de comunicación |
|---|---|---|
| Qué enfatiza | El orden temporal (eje vertical) | Las relaciones estructurales entre objetos (como un grafo) |
| Mejor para | Escenarios largos, con muchos fragmentos alternativos | Ver de un vistazo cuántos objetos distintos colabora uno solo |
| Orden de los mensajes | Posición vertical | Numeración explícita en cada mensaje (`1`, `1.1`, `2`, ...) |
| Contenido | El mismo escenario, la misma información | El mismo escenario, la misma información |

Ambos diagramas son, en rigor, dos vistas de la **misma** información — por eso la matriz de consistencia de 3.7 verifica que los mensajes numerados del diagrama de comunicación sean exactamente los mismos del diagrama de secuencia, ni uno más ni uno menos.

## 3. Aplica: actividad práctica guiada

Tiempo: 100 min.

**Actividad:** decisión y modelado guiado de los diagramas dinámicos del escenario "registrar una venta" de BomERP, a partir de las clases y fichas del módulo `ventas` que S8 ya dejó diseñadas y verificadas contra el código real de LP2.

**Propósito de la actividad:** aplicar un criterio verificable para decidir cuándo un diagrama dinámico aporta información nueva, y llevar el diseño estático de S8 a una vista de comportamiento verificable en el caso que sí lo necesita — detectando, como en el caso de 1.6, qué información de orden, continuidad de estado o tiempo no aparecía todavía en ningún diagrama anterior.

**Orientaciones metodológicas:** en el laboratorio, el docente aplica el criterio de decisión y modela el escenario de `ventas` paso a paso frente a la clase; los estudiantes completan los mismos pasos para un escenario crítico de su propio proyecto (ver sección 4).

**Actividades para realizar:**

- **3.1** Decidir qué operaciones de `ventas` necesitan un diagrama dinámico.
- **3.2** Diagramar la secuencia de "registrar una venta".
- **3.3** Diagramar las actividades del mismo proceso, por carriles.
- **3.4** Evaluar y diagramar el ciclo de vida de `Venta`.
- **3.5** Diagramar la interacción entre `ventas` y `catalogo`, a nivel de componente.
- **3.6** Diagramar la comunicación de la misma colaboración.
- **3.7** Armar la matriz de consistencia.

### 3.1 Decidir qué operaciones de `ventas` necesitan un diagrama dinámico

**Producto del paso:** una decisión explícita y documentada, aplicando la Tabla 2 (2.2) a las cuatro operaciones reales de `VentaController` (S8, Figura 6).

**Tabla 7. Decisión aplicada a `VentaController`**

| Operación | Bucle | Condiciones combinadas | Colaboración entre módulos | Transaccional | ¿Diagrama dinámico? |
|---|---|---|---|---|---|
| `buscar(...)` | No | No | No | No | **No** — la Tabla 13 de S8 ya es la documentación completa. |
| `resumen(...)` | No | No | No | No | **No** — mismo caso que `buscar`. |
| `obtener(id)` | No | No (un solo `404`) | No | No | **No** — una búsqueda y un error posible caben en una fila. |
| `crear(request)` | Sí (una vuelta por línea) | Sí (¿existe? → ¿hay stock?) | Sí (`ProductoService`, módulo `catalogo`) | Sí (RN6, S8) | **Sí** — las cuatro señales de la Tabla 2 se activan a la vez. |

El resto de esta sesión (3.2 a 3.6) modela únicamente `crear` — no porque las otras tres operaciones sean menos importantes para el negocio, sino porque son las únicas donde el diagrama de clases de S8, con su ficha de operación, deja de ser suficiente para entender el flujo.

### 3.2 Diagramar la secuencia de "registrar una venta"

**Producto del paso:** diagrama de secuencia del escenario `POST /api/v1/ventas` (la operación seleccionada en 3.1), con los dos caminos de error ya identificados en S8 (Tabla 10: RN1, RN2).

El escenario se construye directamente sobre el código real de `VentaServiceImpl.crear` (no sobre una versión idealizada): un bucle por cada línea de la venta, que consulta el producto, descuenta su stock, y solo si ambas operaciones tienen éxito arma el detalle y lo agrega a la venta.

**Figura 4. Diagrama de secuencia: registrar una venta**

```mermaid
sequenceDiagram
    actor Vendedor
    participant VC as VentaController
    participant VS as VentaServiceImpl
    participant PS as ProductoService
    participant VM as VentaMapper
    participant VR as VentaRepository

    Vendedor->>VC: POST /api/v1/ventas (VentaRequest)
    activate VC
    VC->>VS: crear(request)
    activate VS

    loop por cada DetalleVentaRequest
        VS->>PS: obtener(productoId)
        activate PS
        alt producto no existe
            PS-->>VS: ResourceNotFoundException (404)
        else producto existe
            PS-->>VS: ProductoResponse
            deactivate PS
            VS->>PS: descontarStock(productoId, cantidad)
            activate PS
            alt stock insuficiente
                PS-->>VS: StockInsuficienteException (409)
            else stock suficiente
                PS-->>VS: stock actualizado
                deactivate PS
                VS->>VM: toDetalle(detalleRequest, producto)
                VM-->>VS: DetalleVenta
            end
        end
    end

    VS->>VR: save(venta)
    activate VR
    VR-->>VS: Venta guardada
    deactivate VR
    VS->>VM: toResponse(venta)
    VM-->>VS: VentaResponse
    deactivate VS
    VS-->>VC: VentaResponse
    VC-->>Vendedor: 201 Created
    deactivate VC
```

Tres lecturas que el diagrama de clases de S8 no podía mostrar — exactamente las señales que 3.1 marcó para `crear`:

- **El `loop` es nuevo.** La flecha `VentaServiceImpl --> ProductoService` de la Figura 6 de S8 solo decía que existe esa dependencia — no decía que, con una venta de cinco líneas, `ProductoService.obtener` y `descontarStock` se llaman cinco veces cada uno, una vez por línea.
- **Dos `alt` distintos, con dos errores distintos, anidados.** RN1 (producto inexistente) y RN2 (stock insuficiente) ya estaban en la Tabla 10 de S8 como filas de una tabla; acá se ven como decisiones que ocurren **dentro del mismo bucle**, en un orden específico: primero se verifica que el producto exista, y solo después se intenta descontar su stock.
- **La transacción no es un mensaje.** `@Transactional` (S8, 2.3) envuelve todo el método `crear`, pero no aparece como una flecha — es una propiedad del método completo. Si cualquier `alt` de error se dispara, toda la transacción se deshace (RN6, S8); el diagrama lo da por entendido, no lo dibuja mensaje por mensaje.

Nota sobre concurrencia: este escenario no usa `par` (2.3) porque no lo necesita — `crear` es secuencial de punta a punta, sin ninguna llamada que ocurra a la vez que otra. Eso es, en sí mismo, una propiedad deseable del diseño: ninguna parte de esta operación corre en paralelo con otra, así que no hay ningún estado compartido que dos flujos puedan pisarse.

**Error frecuente**: dibujar la llamada a `descontarStock` **antes** de confirmar que el producto existe (invertir el orden del `loop`). Si `descontarStock` se llamara primero sobre un `productoId` inexistente, `ProductoService` fallaría con un error distinto al esperado (ni 404 de "producto no encontrado" ni 409 de "stock insuficiente", sino una falla al buscar un producto para descontar). El orden de las llamadas en el diagrama de secuencia *es* una decisión de diseño, no un detalle de implementación — por eso se verifica en la matriz de 3.7.

### 3.3 Diagramar las actividades del mismo proceso, por carriles

**Producto del paso:** diagrama de actividades del mismo escenario de 3.2, organizado por quién ejecuta cada paso — útil para explicar el proceso a alguien que no necesita ver clases ni mensajes.

**Figura 5. Diagrama de actividades: registrar una venta, por carriles**

```mermaid
flowchart TB
    subgraph Vendedor["Carril: Vendedor"]
        Inicio([Inicio]) --> Enviar[Enviar venta con sus líneas]
    end

    subgraph Controlador["Carril: VentaController"]
        Validar{"¿Petición<br/>bien formada?"}
        Validar -- No --> Error400[Responder 400]
    end

    subgraph Servicio["Carril: VentaServiceImpl"]
        Iniciar[Iniciar transaccion]
        SiguienteLinea{"¿Quedan<br/>lineas por procesar?"}
        ArmarDetalle[Armar detalle y sumar al total]
        Guardar[Guardar venta con sus detalles]
    end

    subgraph Catalogo["Carril: ProductoService (modulo catalogo)"]
        Existe{"¿Producto<br/>existe?"}
        Stock{"¿Stock<br/>suficiente?"}
        Descontar[Descontar stock]
    end

    Enviar --> Validar
    Validar -- Si --> Iniciar
    Iniciar --> SiguienteLinea
    SiguienteLinea -- Si --> Existe
    Existe -- No --> Error404[Deshacer transaccion: 404]
    Existe -- Si --> Stock
    Stock -- No --> Error409[Deshacer transaccion: 409]
    Stock -- Si --> Descontar
    Descontar --> ArmarDetalle
    ArmarDetalle --> SiguienteLinea
    SiguienteLinea -- No --> Guardar
    Guardar --> Fin([201 Created])
```

Lectura cruzada con la Figura 4: cada rombo del diagrama de actividades (`¿Producto existe?`, `¿Stock suficiente?`) corresponde exactamente a un fragmento `alt` del diagrama de secuencia — son la misma decisión, en dos notaciones distintas. Si un rombo de este diagrama no tuviera su `alt` equivalente en la Figura 4 (o viceversa), sería una inconsistencia real que la matriz de 3.7 existe para detectar.

### 3.4 Evaluar y diagramar el ciclo de vida de `Venta`

**Producto del paso:** una decisión explícita sobre si `Venta` necesita un diagrama de estados (2.5), y el diagrama correspondiente.

El código real de `EstadoVenta` (LP2) declara hoy un único valor:

```java
public enum EstadoVenta {
    REGISTRADA
}
```

Con un solo estado posible, `Venta` no tiene todavía un ciclo de vida *implementado* que dibujar — pero sí tiene uno **diseñado**, desde antes de esta sesión: S7 ya modeló `anular()` como operación real de `Venta`, con la restricción `{estado = 'REGISTRADA'}` como precondición (S7, Tabla 5 y Figura de clases del dominio). El criterio de 2.5 (¿el comportamiento depende del estado?) ya estaba resuelto en el dominio — lo que faltaba era la vista que lo hiciera explícito como ciclo de vida, no la decisión en sí.

Al revisar la Figura 4 de S8 (clases de **diseño**, derivada del dominio de S7) aparece una brecha real: `anular()` no quedó entre las operaciones de `Venta` cuando S8 refinó el dominio a clases de diseño, y por eso tampoco está en `EstadoVenta` de LP2 (hoy, un único valor: `REGISTRADA`). El diagrama de esta sección documenta el ciclo de vida tal como S7 lo diseñó, y dejar constancia de esa brecha —no inventar la transición de la nada— es exactamente lo que la matriz de consistencia de 3.7 existe para capturar.

**Figura 6. Ciclo de vida de `Venta` (diseñado en S7; brecha: S8 y LP2 todavía no lo llevan al diseño de clases ni al código)**

```mermaid
stateDiagram-v2
    [*] --> REGISTRADA : crear() exitoso
    REGISTRADA --> ANULADA : anular() [S7]
    ANULADA --> [*]
    REGISTRADA --> [*] : consulta (sin cambio de estado)

    note right of ANULADA
        Diseñado en S7, sin bajar a S8 ni a LP2:
        restaura el stock descontado de cada
        DetalleVenta (operacion inversa de
        descontarStock, S8 2.5)
    end note
```

**Tabla 8. Transiciones del ciclo de vida de `Venta`**

| Transición | Disparada por | Precondición | Efecto |
|---|---|---|---|
| `[*] → REGISTRADA` | `crear()` (ya implementado, S8 3.3) | RN1, RN2 cumplidas para cada línea | Venta guardada, stock descontado |
| `REGISTRADA → ANULADA` | `anular()` (diseñada en S7; brecha: sin refinar en S8, sin implementar en LP2) | La venta está en `REGISTRADA`; ningún rol distinto de `SUPERVISOR`/`ADMIN` puede anular (permiso pendiente de definir, mismo criterio que RN8 de S8) | El stock de cada `DetalleVenta` se restaura; la venta no se elimina, queda marcada |
| `ANULADA → *` | Ninguna | — | No existe ninguna transición válida desde `ANULADA`: es un estado final para el ciclo de negocio |

**Error frecuente**: dibujar una transición `ANULADA → REGISTRADA` "por si acaso se necesita revertir". Un diagrama de estados que permite volver a un estado anterior sin una regla de negocio real que lo justifique no documenta un camino más flexible — documenta una regla que no existe y que, si se programa tal cual, dejaría reactivar una venta anulada sin ningún control. Si tu propio dominio sí necesita revertir un estado, la transición debe nombrar una operación concreta y sus condiciones, no quedar implícita.

### 3.5 Diagramar la interacción entre `ventas` y `catalogo`, a nivel de componente

**Producto del paso:** la misma colaboración de 3.2, redibujada a nivel Componente (C3, 2.6) — sin ninguna clase, solo los dos módulos que participan.

**Figura 7. Interacción entre componentes: registrar una venta (nivel C3)**

```mermaid
sequenceDiagram
    actor Vendedor
    participant Ventas as ventas (componente)
    participant Catalogo as catalogo (componente)

    Vendedor->>Ventas: POST /api/v1/ventas (VentaRequest)
    activate Ventas

    loop por cada DetalleVentaRequest
        Ventas->>Catalogo: obtener(productoId)
        activate Catalogo
        Catalogo-->>Ventas: ProductoResponse (o 404)
        deactivate Catalogo
        Ventas->>Catalogo: descontarStock(productoId, cantidad)
        activate Catalogo
        Catalogo-->>Ventas: stock actualizado (o 409)
        deactivate Catalogo
    end

    Note over Ventas: Arma y guarda la venta<br/>internamente (detalle en Figura 4)

    Ventas-->>Vendedor: 201 Created
    deactivate Ventas
```

Lectura cruzada con la Figura 4: los cinco participantes de nivel Código (`VentaController`, `VentaServiceImpl`, `ProductoService`, `VentaMapper`, `VentaRepository`) se colapsan en dos cajas de componente (`ventas`, `catalogo`). Las dos llamadas por línea (`obtener`, `descontarStock`) siguen apareciendo — porque cruzan el límite del componente, que es justamente lo que esta vista quiere mostrar —, pero `toDetalle` y `save` desaparecen: son colaboración **interna** de `ventas`, invisible a este nivel de zoom, y por eso el diagrama las resume en una sola nota ("Arma y guarda la venta internamente"). Si este diagrama mostrara una clase, o una llamada interna de `ventas` que nunca cruza a `catalogo`, dejaría de ser un diagrama de componentes y volvería a ser el de la Figura 4 con otro nombre.

**Error frecuente**: dibujar aquí una flecha directa `ventas --> BOM_CATALOGO.PRODUCTOS` (la tabla) en vez de `ventas --> catalogo` (el componente). A nivel de interacción entre componentes, el destino de cada mensaje es siempre **otro componente completo**, nunca una clase, una tabla o un detalle de implementación interno — ese nivel de detalle es, precisamente, lo que esta vista decide no mostrar.

### 3.6 Diagramar la comunicación de la misma colaboración

**Producto del paso:** diagrama de comunicación del mismo escenario de 3.2, con los mensajes numerados (2.7).

**Figura 8. Diagrama de comunicación: registrar una venta**

```mermaid
flowchart LR
    Vendedor((Vendedor))
    VC[VentaController]
    VS[VentaServiceImpl]
    PS[ProductoService]
    VM[VentaMapper]
    VR[VentaRepository]

    Vendedor -->|"1: crear(request)"| VC
    VC -->|"1.1: crear(request)"| VS
    VS -->|"1.1.1: obtener(productoId)"| PS
    VS -->|"1.1.2: descontarStock(productoId, cantidad)"| PS
    VS -->|"1.1.3: toDetalle(detalleRequest, producto)"| VM
    VS -->|"1.2: save(venta)"| VR
    VS -->|"1.3: toResponse(venta)"| VM
```

Lectura del diagrama: la numeración `1.1.1`, `1.1.2`, `1.1.3` dentro de `1.1` representa el mismo `loop` de la Figura 4 — todos esos mensajes ocurren una vez por cada línea de la venta, anidados dentro de la llamada `crear(request)`. A diferencia de la Figura 4, acá se ve de un vistazo que `VentaServiceImpl` es el único objeto que colabora con los otros cuatro — ningún otro objeto del escenario llama directamente a `ProductoService`, `VentaMapper` o `VentaRepository`.

### 3.7 Armar la matriz de consistencia

**Producto del paso:** verificación cruzada de que las Figuras 4 a 8 sean consistentes entre sí y con las fichas de clase de S8 — exactamente lo que pide la columna "Evidencia" del sílabo para esta sesión ("revisar responsabilidades, mensajes y consistencia entre componentes").

**Tabla 9. Matriz de consistencia de los diagramas dinámicos de `ventas`**

| Mensaje o decisión | Secuencia (Fig. 4) | Actividades (Fig. 5) | Interacción de componentes (Fig. 7) | Comunicación (Fig. 8) | Ficha de clase (S8) |
|---|---|---|---|---|---|
| `crear(request)` | Sí | "Enviar venta" → "Validar" → "Iniciar transacción" | Sí (`Vendedor → ventas`) | `1`, `1.1` | S8, Tabla 13 (`VentaService.crear`) |
| `obtener(productoId)` | Sí, dentro del `loop` | Rombo "¿Producto existe?" | Sí (`ventas → catalogo`) | `1.1.1` | S8, Tabla 13 (RN1) |
| `descontarStock(productoId, cantidad)` | Sí, dentro del `loop` | Rombo "¿Stock suficiente?" | Sí (`ventas → catalogo`) | `1.1.2` | S8, Tabla 13 (RN2) |
| `toDetalle(detalleRequest, producto)` | Sí | "Armar detalle" | No aparece (colaboración interna de `ventas`, nota en Fig. 7) | `1.1.3` | S8, Tabla 15 (`VentaMapper.toDetalle`) |
| `save(venta)` | Sí | "Guardar venta" | No aparece (colaboración interna de `ventas`, nota en Fig. 7) | `1.2` | S8, Tabla 14 (`VentaRepository`) |
| Transición `REGISTRADA → ANULADA` | No aplica (otro escenario) | No aplica | No aplica | No aplica | S7 (diseñada); brecha en S8/LP2 (Figura 6) |
| `buscar`, `resumen`, `obtener` | Sin diagrama dinámico (3.1) | Sin diagrama dinámico (3.1) | Sin diagrama dinámico (3.1) | Sin diagrama dinámico (3.1) | S8, Tabla 13 — documentación completa por sí sola |

Ninguna fila quedó vacía ni contradictoria: los cuatro diagramas describen exactamente el mismo escenario (`crear`), cada mensaje corresponde a una operación ya documentada en las fichas de clase de S8 —no a un mensaje inventado para la sesión de hoy—, las dos ausencias de la columna de interacción entre componentes (`toDetalle`, `save`) están explicadas y no son un olvido, y las tres operaciones descartadas en 3.1 quedan registradas explícitamente como "sin diagrama", con su propia justificación.

**Evidencia de aprendizaje:**

- Decisión documentada de qué operaciones de `ventas` necesitan diagrama dinámico, aplicando un criterio verificable.
- Diagrama de secuencia de "registrar una venta", con los fragmentos `alt` de RN1 y RN2.
- Diagrama de actividades equivalente, organizado por carriles.
- Evaluación explícita de si `Venta` necesita un diagrama de estados, con el ciclo de vida diseñado en S7 documentado y su brecha frente a S8/LP2 identificada.
- Diagrama de interacción entre `ventas` y `catalogo` a nivel de componente (C3), consistente con el de nivel Código.
- Diagrama de comunicación de la misma colaboración, con mensajes numerados.
- Matriz de consistencia entre los cuatro diagramas dinámicos, las fichas de clase de S8, y las operaciones descartadas en 3.1.

## 4. Crea: actividad autónoma

Tiempo: 2h fuera del aula.

### 4.1 Actividad

Modelado dinámico autónomo de un escenario crítico del módulo del proyecto propio del equipo ya diseñado en S8, documentado en evidencia individual.

Completa y evidencia estas tareas:

1. Aplicar la Tabla 2 (2.2) a **todas** las operaciones de tu módulo de S8, en una tabla propia como la Tabla 7 (3.1): marca cuáles tienen bucle, condiciones combinadas, colaboración entre módulos o comportamiento transaccional, y cuáles no. Elige para diagramar la que active más señales — no necesariamente la que te parezca más importante de negocio.
2. Diagramar la secuencia completa del escenario elegido, con al menos un fragmento `alt` para cada regla que pueda rechazar la operación. Usa exactamente las clases de tu diagrama de S8 (1.1) — si el escenario necesitara una clase que todavía no existe ahí, vuelve primero a S8 y agrégala al diagrama de clases, no la inventes dentro de la secuencia. Si tu escenario tiene algo genuinamente concurrente, usa `par` (2.3); si no, documenta por qué no hace falta.
3. Diagramar las actividades del mismo escenario, organizadas por carriles (al menos dos: quien origina la petición y el backend).
4. Evaluar si algún objeto de tu módulo necesita un diagrama de ciclo de vida (2.5) — si la respuesta es no, documentar por qué con el mismo criterio de esta guía; si es sí, diagramarlo con sus transiciones y al menos un estado final.
5. Redibujar la misma colaboración del punto 2 a nivel de componente (2.6): reemplaza cada clase por el módulo al que pertenece, y conserva solo los mensajes que cruzan el límite entre componentes.
6. Diagramar la comunicación de la misma colaboración del punto 2, con los mensajes numerados.
7. Armar tu propia matriz de consistencia, cruzando cada mensaje contra tu ficha de clase de S8, incluyendo las operaciones del punto 1 que quedaron sin diagrama, y marcando cualquier inconsistencia encontrada.

### 4.2 Propósito

Que cada estudiante demuestre, de forma individual y fuera del aula, que puede decidir con un criterio verificable cuándo un diagrama dinámico aporta información nueva, y llevar el diseño estático de su propio módulo a una vista de comportamiento verificable en el escenario que sí lo necesita —en más de un nivel de zoom—, detectando inconsistencias entre diagramas antes de que lleguen al código.

### 4.3 Indicaciones

Entrega un PDF con el siguiente nombre:

```text
S09_ADS_Equipo##_ApellidoNombre.pdf
```

Cada captura de pantalla del informe debe mostrar, sin recortar, el reloj del sistema (fecha y hora) y tu usuario o foto de perfil (Windows, VS Code o navegador) visibles en pantalla — es lo que permite verificar que la evidencia es tuya y que corresponde al momento real de tu trabajo.

#### 4.3.1 Estructura del informe

**Datos del estudiante**

- Nombre:
- Equipo:
- Sesión: S09 - Diagramas Dinámicos UML
- Rol o aporte realizado:
- Link de GitHub:

**Evidencia técnica**

Incluye capturas o extractos con una breve explicación debajo de cada uno, organizados en los mismos 4 bloques de la rúbrica (4.6):

1. *Decisión y diagrama de secuencia*
    - Tabla de decisión aplicada a todas las operaciones de tu módulo, y diagrama de secuencia del escenario elegido, con al menos un fragmento `alt` por cada regla de negocio que pueda fallar.
2. *Diagrama de actividades*
    - Mismo escenario, organizado por carriles, con los mismos puntos de decisión que el diagrama de secuencia.
3. *Ciclo de vida, interacción entre componentes y comunicación*
    - Evaluación explícita de si tu objeto necesita diagrama de estados (con el diagrama si corresponde), diagrama de interacción entre componentes a nivel C3, y diagrama de comunicación con mensajes numerados.
4. *Matriz de consistencia*
    - Matriz completa, sin filas vacías ni contradictorias, incluidas las operaciones sin diagrama, con cualquier inconsistencia encontrada y cómo se resolvió.

**Error o hallazgo**

Describe un error real: una operación que creíste que necesitaba diagrama dinámico y, al aplicar la Tabla 2, resultó no necesitarlo (o viceversa), un orden de mensajes que corregiste en el diagrama de secuencia porque invertía una validación, un rombo del diagrama de actividades que no tenía su `alt` equivalente, una clase que se te coló en el diagrama de interacción entre componentes en vez del módulo completo, o una transición de estado que quitaste por no tener ninguna regla de negocio real detrás.

**Reflexión técnica breve**

Responde en 5 a 8 líneas:

```text
¿Qué decisión de orden o de continuidad de estado de tu escenario no se
veía en tu diagrama de clases de S8, y solo apareció al dibujar la
secuencia, las actividades o el ciclo de vida? Relaciona tu respuesta
con el caso del vehículo autónomo de Uber (1.6).
```

### 4.4 Criterios mínimos de aceptación

- El archivo respeta el nombre solicitado.
- Existe una tabla de decisión (como la Tabla 7) aplicada a todas las operaciones del módulo propio, con al menos una marcada "sin diagrama" y justificada.
- El diagrama de secuencia cubre el escenario con más señales de la tabla de decisión, con un fragmento `alt` por cada regla de negocio relevante, orden de mensajes correcto, y usa exclusivamente clases que ya existen en el diagrama de clases de S8.
- El diagrama de actividades usa carriles y sus puntos de decisión coinciden con los fragmentos `alt` del diagrama de secuencia.
- Existe una evaluación explícita (sí o no, con justificación) sobre si el objeto elegido necesita un diagrama de ciclo de vida.
- El diagrama de interacción entre componentes usa solo módulos completos (nunca clases ni tablas) y conserva únicamente los mensajes que cruzan el límite entre componentes.
- El diagrama de comunicación representa la misma colaboración del diagrama de secuencia, con mensajes numerados.
- La matriz de consistencia cruza cada mensaje contra la ficha de clase correspondiente de S8, incluye las operaciones sin diagrama, y no tiene filas vacías.
- Cada captura de la evidencia técnica muestra el reloj del sistema y el usuario/perfil visible, sin recortar.
- Las fechas y horas de las capturas son coherentes con el historial de commits de su repositorio en GitHub.
- Incluye un error o hallazgo técnico diagnosticado.
- Incluye la reflexión técnica breve solicitada.

### 4.5 Preguntas de defensa

1. De todas las operaciones de tu módulo, ¿cuál tiene más señales de la Tabla 2, y por eso fue la que diagramaste?
2. En tu diagrama de secuencia, ¿qué pasaría si el fragmento `alt` de tu regla de negocio más crítica no existiera?
3. ¿Por qué el diagrama de actividades usa carriles y el diagrama de secuencia no?
4. ¿Qué criterio usaste para decidir si tu objeto necesitaba (o no) un diagrama de ciclo de vida?
5. ¿Qué mensajes de tu diagrama de secuencia desaparecieron al redibujarlo a nivel de componente, y por qué esos y no otros?
6. ¿Qué diferencia de fondo hay entre un diagrama de secuencia y uno de comunicación, si ambos representan la misma colaboración?
7. ¿Tu escenario tiene algo genuinamente concurrente? Si no, ¿por qué no hacía falta usar `par`; si sí, ¿por qué no lo usaste (o cómo lo mostrarías)?
8. Relaciona el caso del vehículo autónomo de Uber (1.6) con algo que tu matriz de consistencia (o la falta de ella) podría haber dejado pasar.

### 4.6 Rúbrica de evaluación

**Tabla 10. Rúbrica de evaluación**

| Criterio | Peso (%) | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Nivel obtenido |
|---|---:|---|---|---|---|---:|
| 1. Decisión y diagrama de secuencia* | 25 | Tabla de decisión completa sobre todas las operaciones del módulo; secuencia del escenario correcto, con fragmentos `alt` para cada regla y solo clases ya existentes en S8. | Secuencia completa, con la tabla de decisión o algún fragmento alternativo incompleto. | Secuencia sin fragmentos alternativos, con orden incorrecto, con alguna clase inventada, o sin tabla de decisión. | No presenta decisión ni diagrama de secuencia verificable. | |
| 2. Diagrama de actividades* | 25 | Carriles claros, puntos de decisión coherentes uno a uno con los fragmentos `alt` del diagrama de secuencia. | Actividades completas, con algún carril o decisión no alineado con la secuencia. | Actividades sin carriles, o con decisiones que no corresponden a la secuencia. | No presenta diagrama de actividades verificable. | |
| 3. Ciclo de vida, interacción entre componentes y comunicación* | 25 | Los tres presentes y correctos: ciclo de vida evaluado y justificado, interacción entre componentes con solo módulos completos, comunicación consistente con la secuencia. | Los tres presentes, con alguno incompleto (numeración, justificación, o una clase colada en el diagrama de componentes). | Solo dos de los tres presentados correctamente. | Presenta uno o ninguno de los tres. | |
| 4. Matriz de consistencia* | 25 | Matriz completa, sin filas vacías, incluidas las operaciones sin diagrama, con cualquier inconsistencia detectada y resuelta explícitamente. | Matriz completa, con alguna fila superficial. | Matriz incompleta o sin verificar contra las fichas de S8. | No presenta matriz de consistencia. | |

\* Agregado manual.

Nota final = suma de (`Peso` / 100 × `Puntos del nivel obtenido`) = ____ / 20.

Para usar la rúbrica con IA (inteligencia artificial), solicita:

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

**Resumen breve:** hoy no todas las operaciones de `ventas` recibieron el mismo tratamiento, y eso fue intencional: un criterio verificable (bucles, condiciones combinadas, colaboración entre módulos, comportamiento transaccional) distinguió `crear` —que sí necesitaba una vista nueva— de `buscar`, `obtener` y `resumen` —cuya ficha de clase de S8 ya las explica por completo—. Sobre `crear`, las clases de S8 ganaron una vista de comportamiento en dos niveles de zoom: a nivel Código (C4) un diagrama de secuencia con sus caminos de error, uno de actividades equivalente por carriles, el ciclo de vida de `Venta` que S7 ya había diseñado (con la brecha real de S8/LP2 identificada) y un diagrama de comunicación de la misma colaboración; y a nivel Componente (C3) la misma historia contada con módulos completos en vez de clases — ambos, el mismo *Dynamic diagram* que el propio modelo C4 define, aplicado en dos niveles distintos. Una matriz final verificó que todos ellos, las fichas de clase de S8, y las operaciones descartadas dicen exactamente lo mismo, sin contradicciones.

**Dinámica participativa:** en una ronda rápida, cada estudiante comparte en una frase qué operación de su propio módulo descartó de diagramar, y por qué.

**Metacognición:** ¿qué te costó más entender hoy: el criterio para decidir si una operación necesita un diagrama dinámico, o qué mensajes desaparecen al subir del nivel Código al nivel Componente?

**Proyección:** S10 explica **por qué** las clases de `ventas` están organizadas así —con los patrones GoF (*Gang of Four*) y GRASP (*General Responsibility Assignment Software Patterns*) que respaldan cada responsabilidad de la Figura 6 de S8 y cada decisión que hoy se vio en acción en la Figura 4.

## Bibliografía

1. National Transportation Safety Board. (2019). *Collision Between Vehicle Controlled by Developmental Automated Driving System and Pedestrian* (Highway Accident Report NTSB/HAR-19/03). https://www.ntsb.gov/investigations/accidentreports/reports/har1903.pdf
2. Object Management Group [OMG]. (2017). *OMG Unified Modeling Language (OMG UML), Version 2.5.1*. https://www.omg.org/spec/UML/2.5.1/
3. Larman, C. (2004). *Applying UML and Patterns: An Introduction to Object-Oriented Analysis and Design and Iterative Development* (3rd ed.). Prentice Hall.
4. Brown, S. (2024). *C4 model - Dynamic diagram*. https://c4model.com/diagrams/dynamic

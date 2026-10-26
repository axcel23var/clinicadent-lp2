# S1 - PL/SQL Aplicado al Negocio

## 1. Introducción

Tiempo: 20 min.

### 1.1 Presentación de la sesión

Esta sesión abre la Unidad 1: crea el esquema y las tablas base del caso empresarial en Oracle, e inicia el motor transaccional con procedimientos y funciones PL/SQL. El porqué de resolver reglas de negocio también del lado de la base de datos, y no solo en el backend, se desarrolla en 1.6, a partir del caso del cálculo de inventario y precio.

### 1.2 Índice

1. Creación del esquema y las tablas base del caso empresarial.
2. Procedimientos PL/SQL.
3. Funciones PL/SQL.
4. Parámetros `IN`, `OUT` e `IN OUT`.

### 1.3 Propósito de aprendizaje

Al concluir la clase, estarás en condiciones de:

- **Crear y entregar** el esquema y las tablas base de un caso empresarial en Oracle, con procedimientos y funciones PL/SQL que validan reglas básicas del negocio usando parámetros `IN`, `OUT` e `IN OUT`.

### 1.4 Producto de sesión

Esquema `BOM_CATALOGO` con sus tablas base, más procedimientos y funciones iniciales para registrar operaciones transaccionales del ERP.

### 1.5 Metodología

**Tabla 1. Metodología de la sesión**

| Actividades a Realizar en el Periodo | Orientaciones generales (Orientaciones Metodológicas) | Material de estudio recomendado |
|---|---|---|
| Revisión previa individual | Instalar/verificar acceso a Oracle XE y revisar el caso BomERP (ver 1.6). Trabajo individual, antes de clase. | Acceso a Oracle, sílabo BD2 U1. |
| Clase presencial | Creación guiada del esquema y tablas del catálogo, seguida de la función y los procedimientos PL/SQL. Trabajo individual en la propia laptop, siguiendo al docente paso a paso; consulta inmediata ante errores de compilación. | Scripts `S01_01_esquemas.sql`, `S01_02_tablas.sql`, `S01_03_plsql.sql`. |
| Evaluación formativa | Verificación en clase de la ejecución de la función y los procedimientos con datos válidos. La evidencia se completa y sustenta de forma individual, fuera del aula, según los criterios mínimos de la sección 4.4. | Indicaciones de entrega (4.3), rúbrica de evaluación (4.6). |

### 1.6 Motivación de la sesión

#### 1.6.1 Caso: catálogo de BomERP (`Categoria`–`Producto`)

¿Dónde debería vivir la regla que calcula el valor de inventario de un producto, o la que ajusta su precio: en el backend de la aplicación, o en la propia base de datos? El backend puede validar datos, pero hay cálculos y reglas del negocio que conviene resolver también del lado de Oracle — centralizados en un procedimiento o función PL/SQL, disponibles sin importar qué cliente los invoque.

**Preguntas de análisis**

**Activación de conocimientos previos**

1. ¿Qué diferencia hay entre calcular `precio * stock` en el backend Java y calcularlo en una función PL/SQL?
2. ¿Cómo se evidencia que los productos de esta sesión no son ejercicios aislados, sino el motor transaccional detrás del esquema `BOM_CATALOGO`?

**Comprensión de PL/SQL**

1. ¿Qué procedimiento podría invocar (o replicar) un backend REST como el de LP2?
2. ¿Qué función permite centralizar un cálculo del negocio sin repetirlo en cada consulta?
3. ¿Por qué un parámetro `IN OUT` es distinto de tener un `IN` y un `OUT` por separado?

### 1.7 Ubicación en el curso

- Unidad: U1 - Programación y optimización (Oracle XE).
- Producto del curso: base de datos empresarial Oracle operativa, administrada, optimizada, auditada y resiliente.
- Producto de unidad: motor transaccional Oracle optimizado.
- Avance del producto en esta sesión: procedimientos, funciones y reglas iniciales del motor transaccional.

Roadmap del producto de la unidad:

**Figura 1. Roadmap del producto de la unidad**

```mermaid
flowchart TB
    S1["`**S1:** PL/SQL aplicado al negocio`"]
    S2["`**S2:** Triggers DML y auditoría`"]
    S3["`**S3:** Excepciones y robustez`"]
    S4["`**S4:** Optimización de consultas`"]
    S5["`**S5:** Índices`"]
    S6["`**S6:** Producto U1`"]

    S1 --> S2 --> S3 --> S4 --> S5 --> S6

    classDef today fill:#ffe08a,stroke:#9a6b00,stroke-width:2px,color:#111;
    class S1 today;
```

## 2. Explica

Tiempo: 25 min.

### 2.1 Arquitectura de la sesión

**Figura 2. Arquitectura del esquema `BOM_CATALOGO` y su motor transaccional PL/SQL**

```mermaid
flowchart TB
    subgraph ORA["Motor transaccional Oracle - esquema BOM_CATALOGO"]
        SP1[SP_REGISTRAR_PRODUCTO]
        SP2[SP_APLICAR_DESCUENTO_PRODUCTO]
        FN[FN_VALOR_INVENTARIO_PRODUCTO]
        T[(producto)]

        SP1 --> T
        SP2 --> T
        FN -.->|lee valores para calcular| T
    end

    APP["`Aplicación cliente (cualquiera: backend, otro script, SQL directo)`"]
    APP --> SP1
    APP --> FN
    APP --> SP2
```

Lectura del diagrama:

- El procedimiento de registro (`SP_REGISTRAR_PRODUCTO`) y el de ajuste de precio (`SP_APLICAR_DESCUENTO_PRODUCTO`) escriben sobre `PRODUCTOS`; la función (`FN_VALOR_INVENTARIO_PRODUCTO`) solo lee y calcula, no modifica nada.
- Esta lógica vive en Oracle y protege la regla **sin importar quién la invoque**: un backend Java, otro cliente, o alguien ejecutando SQL directo — nadie puede saltarse la validación entrando por otro camino. **Error frecuente**: dejar todo del lado del backend sin definir qué reglas son realmente críticas — esas reglas se mueven a PL/SQL o a restricciones de la tabla.
- Integración (referencia, no requisito para esta sesión): LP2 (S2) ya implementa una regla equivalente en `ProductoServiceImpl` vía JPA/Hibernate — este procedimiento es la versión Oracle de esa misma regla, útil incluso para quien no esté llevando LP2 en paralelo. ADS documenta esta separación como decisión arquitectónica (atributo "Integridad"). **Error frecuente**: trabajar PL/SQL como ejercicio aislado, sin relacionarlo con la entidad transaccional real del proyecto.

Este diagrama es el mapa que guía el resto de la explicación: cada apartado siguiente desarrolla uno de sus componentes, en el mismo orden del Índice (1.2).

### 2.2 Creación del esquema y las tablas base del caso empresarial

PL/SQL permite ubicar lógica transaccional cerca de los datos. En un sistema empresarial, esto ayuda a proteger reglas críticas, mejorar consistencia y preparar operaciones que pueden ser consumidas por servicios backend — pero primero necesita un esquema propio y tablas base sobre las cuales operar.

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

Este esquema es la base que LP2 conecta desde su propio S1 — primero solo `PRODUCTOS` (S1-S2, sin relación); `FK_PRODUCTO_CATEGORIA` ya existe en Oracle desde hoy, pero LP2 no la traduce a `@ManyToOne` hasta su S3 ([LP2 S3](../../lp2/sesiones/S03_Objetos_Relacionados_Categoria_Producto.md), Figura 3) — la base de datos conoce la relación antes de que el código Java la use.

**Error frecuente**: nombrar objetos de forma genérica en vez de usar nombres empresariales que reflejen el proceso real (esquema, tablas, procedimientos y funciones).

Alcance metodológico de S1:

```text
En S1 se construye la primera lógica transaccional.
Todavía no se trabaja auditoría avanzada, excepciones completas,
optimización ni administración Oracle.

Esos aspectos se desarrollan progresivamente durante la unidad.
```

### 2.3 Procedimientos PL/SQL

Un **procedimiento almacenado** ejecuta una acción (registrar, actualizar) y puede declarar **variables locales** para procesar datos antes de escribirlos, aplicando **validación de reglas** del negocio antes de cualquier `INSERT` o `UPDATE`.

**Error frecuente**: no devolver el identificador generado con un parámetro `OUT` — sin él, el backend u otro cliente no puede continuar su flujo (por ejemplo, no sabe qué producto acaba de registrar).

### 2.4 Funciones PL/SQL

Una **función** PL/SQL calcula y retorna un valor (por ejemplo, el valor de inventario de un producto) sin modificar datos — a diferencia de un procedimiento, siempre se ejecuta dentro de una expresión y siempre retorna algo.

**Error frecuente**: asumir que el cálculo funciona sin probarlo — toda función se ejecuta con casos válidos e inválidos antes de darla por buena.

### 2.5 Parámetros `IN`, `OUT` e `IN OUT`

Un parámetro `IN` recibe un valor sin modificarlo, un `OUT` devuelve un resultado sin necesitar un valor de entrada, y un `IN OUT` hace ambas cosas sobre la misma variable (por ejemplo, ajustar un precio recibido y devolver el precio ya ajustado en el mismo parámetro).

**Error frecuente**: insertar datos con valores fijos en vez de recibirlos como parámetros — sin `IN`, `OUT` e `IN OUT`, el procedimiento no es reutilizable ni conectable desde otro cliente.

## 3. Aplica: actividad práctica guiada

Tiempo: 2h.

**Actividad:** creación guiada del esquema `BOM_CATALOGO` y su primer motor transaccional con procedimientos y funciones PL/SQL (Producto de la sesión en 1.4).

**Propósito de la actividad:** construir el esquema `BOM_CATALOGO` con sus tablas base y el primer motor transaccional del negocio — una función y dos procedimientos PL/SQL que validan reglas del negocio con parámetros `IN`, `OUT` e `IN OUT` — verificando cada incremento antes de continuar al siguiente.

**Orientaciones metodológicas:** en el laboratorio, el docente guía la creación del esquema y las tablas del catálogo, seguida de la función y los procedimientos PL/SQL, paso a paso frente a la clase; los estudiantes replican cada paso en su propio equipo, verificando la compilación y la ejecución antes de avanzar al siguiente.

**Actividades para realizar:**

- **3.1** Definir operación transaccional.
- **3.2** Crear el esquema y las tablas del catálogo.
- **3.3** Crear la función de negocio (parámetros `IN`).
- **3.4** Crear el procedimiento de registro (parámetros `IN` y `OUT`).
- **3.5** Crear el procedimiento de ajuste de precio (parámetro `IN OUT`).
- **3.6** Probar función y procedimientos.
- **3.7** Relacionar con ADS y LP2.

**Scripts completos, listos para ejecutar en este orden** (los pasos siguientes explican cada uno):

1. [`S01_01_esquemas.sql`](../../proyecto-integrador/u1/oracle/S01_01_esquemas.sql) — usuarios `BOM_CATALOGO` y `BOMERP_APP`.
2. [`S01_02_tablas.sql`](../../proyecto-integrador/u1/oracle/S01_02_tablas.sql) — tablas `CATEGORIAS` y `PRODUCTOS`.
3. [`S01_03_plsql.sql`](../../proyecto-integrador/u1/oracle/S01_03_plsql.sql) — la función y los dos procedimientos de esta sesión.

### 3.1 Definir operación transaccional

**Producto del paso:** operación principal del motor Oracle.

**Tabla 2. Operación transaccional del motor Oracle**

| Elemento | Respuesta |
|---|---|
| Proceso principal | Registro y valorización del catálogo de productos |
| Entidad transaccional | `BOM_CATALOGO.PRODUCTOS` |
| Acción PL/SQL inicial | Función de cálculo + procedimiento de registro + procedimiento de ajuste de precio |
| Regla crítica | El precio y el stock nunca deben quedar negativos |

### 3.2 Crear el esquema y las tablas del catálogo

**Producto del paso:** esquema `BOM_CATALOGO` y las tablas `CATEGORIAS`/`PRODUCTOS` operativas en Oracle — la base sobre la que esta sesión construye PL/SQL y sobre la que LP2 conecta su backend (S1-S2).

**Nota sobre versión y edición:** esta unidad (U1) trabaja contra el mismo
contenedor Oracle que levanta LP2 en su S1 (`gvenzl/oracle-free:23-slim` —
Oracle Database 23ai **Free**, no Oracle 19c). Alcanza para lo que pide el
sílabo en U1 (Oracle XE). U2-U3 exige Oracle 19c **Enterprise Edition** +
Oracle Linux para temas exclusivos de EE (AWR, particionamiento) que este
contenedor no soporta — ese ambiente todavía no está operacionalizado en
el repo (ver `bd2/README.md`).

Con una cuenta DBA, crea el usuario propietario del catálogo (`BOM_CATALOGO`) y el usuario técnico que usará el backend de LP2 (`BOMERP_APP`). Las contraseñas se piden en tiempo de ejecución; no se versionan:

```sql
ACCEPT pwd_catalogo CHAR PROMPT 'Password BOM_CATALOGO: ' HIDE
ACCEPT pwd_app CHAR PROMPT 'Password BOMERP_APP: ' HIDE

CREATE USER BOM_CATALOGO IDENTIFIED BY "&pwd_catalogo" QUOTA UNLIMITED ON USERS;
CREATE USER BOMERP_APP IDENTIFIED BY "&pwd_app";

GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE PROCEDURE, CREATE TRIGGER TO BOM_CATALOGO;
GRANT CREATE SESSION TO BOMERP_APP;

UNDEFINE pwd_catalogo
UNDEFINE pwd_app
```

`ACCEPT ... HIDE` es útil cuando cada quien pone su propia contraseña. Pero en el ambiente **DEV** de este proyecto (laptop de desarrollo, sin secretos que proteger) conviene que todos usen exactamente la misma credencial, para que nadie se quede sin poder conectar el backend por una contraseña distinta a la de `application-dev.yml`. Por eso, en el script real (`S01_01_esquemas.sql`) se usa contraseña fija en texto plano — mismo criterio que `application-dev.yml`/`compose-dev.yml` (ver `lp2/CLAUDE.md`, sección "Ambientes"):

```sql
CREATE USER BOM_CATALOGO IDENTIFIED BY "123456" QUOTA UNLIMITED ON USERS;
CREATE USER BOMERP_APP IDENTIFIED BY "123456";

GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE PROCEDURE, CREATE TRIGGER TO BOM_CATALOGO;
GRANT CREATE SESSION TO BOMERP_APP;
```

La contraseña de `BOMERP_APP` debe coincidir exactamente con la de `application-dev.yml`.

#### Ver el esquema `BOM_CATALOGO` desde el cliente gráfico

Con la conexión de `system` (ver LP2 S1, 3.2.2) normalmente solo ves los objetos de `SYSTEM`, no los de `BOM_CATALOGO`, aunque ya exista. Agrega una segunda conexión, igual que ya hiciste con `BOMERP_APP`:

**Tabla 3. Conexión al esquema `BOM_CATALOGO`**

| Campo | Valor |
|---|---|
| Host | `127.0.0.1` |
| Port | `1521` |
| Username | `BOM_CATALOGO` |
| Password | `123456` |
| Database | `FREEPDB1` |

Con esa conexión entras directo como ese usuario y ves su propio árbol (`CATEGORIAS`, `PRODUCTOS` una vez creadas), sin depender de que el cliente muestre o no otros esquemas.

#### Si necesitas borrar y volver a crear el esquema

Si algo quedó a medio configurar (por ejemplo, corriste `S01_01_esquemas.sql` dos veces y salió `ORA-01920`, usuario ya existente), lo más simple en DEV es borrar y recrear desde cero — conectado como `system`:

```sql
DROP USER BOM_CATALOGO CASCADE;
DROP USER BOMERP_APP CASCADE;
```

`CASCADE` borra también cualquier objeto que ese usuario ya tuviera (tablas, funciones, procedimientos), para no arrastrar nada a medio crear. Después, vuelve a ejecutar `S01_01_esquemas.sql` completo.

#### Verificar desde terminal, sin depender del cliente gráfico

Si el cliente gráfico no muestra un esquema que debería existir (o al revés, quieres confirmar un `ORA-01920` antes de asumir que ya existe), verifica directo con `sqlplus` dentro del contenedor — te da la respuesta real de la base, sin caché de ningún cliente de por medio:

```powershell
docker exec -it bomerp-oracle sqlplus system/123456@localhost:1521/FREEPDB1
```

Eso deja un prompt `SQL>` dentro del contenedor. Ahí escribes la consulta, terminando con `;`, y Enter — esta consulta muestra el **usuario en sí**, no sus tablas (que todavía no existen en este punto de la sesión):

```sql
SELECT username, account_status, default_tablespace, temporary_tablespace, created
FROM dba_users
WHERE username = 'BOM_CATALOGO';
```

Salida esperada (confirma que el usuario existe, con estado `OPEN` y tablespace por defecto `USERS`):

```text
USERNAME                       ACCOUNT_STATUS   DEFAULT_TABLESPACE   TEMPORARY_TABLESPACE   CREATED
------------------------------ ---------------- -------------------- ---------------------- -----------
BOM_CATALOGO                   OPEN             USERS                TEMP                   02-AUG-26
```

Para salir de la sesión: `exit;`

Luego las tablas del catálogo, propiedad de `BOM_CATALOGO`:

```sql
CREATE TABLE BOM_CATALOGO.CATEGORIAS (
    ID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NOMBRE VARCHAR2(80) NOT NULL UNIQUE,
    DESCRIPCION VARCHAR2(200)
);

CREATE TABLE BOM_CATALOGO.PRODUCTOS (
    ID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ID_CATEGORIA NUMBER,
    NOMBRE VARCHAR2(120) NOT NULL,
    PRECIO NUMBER(10,2) NOT NULL,
    STOCK NUMBER(10) NOT NULL,
    CONSTRAINT FK_PRODUCTO_CATEGORIA FOREIGN KEY (ID_CATEGORIA)
        REFERENCES BOM_CATALOGO.CATEGORIAS(ID),
    CONSTRAINT CK_PRODUCTO_PRECIO CHECK (PRECIO >= 0),
    CONSTRAINT CK_PRODUCTO_STOCK CHECK (STOCK >= 0)
);

-- BOMERP_APP ejecuta el backend de LP2, pero no es propietario de los objetos.
GRANT SELECT, INSERT, UPDATE, DELETE ON BOM_CATALOGO.CATEGORIAS TO BOMERP_APP;
GRANT SELECT, INSERT, UPDATE, DELETE ON BOM_CATALOGO.PRODUCTOS TO BOMERP_APP;
```

Tablas en plural y `ID` genérico como columna primaria — mismo estándar que usa Distribuidas (`categorias`, `productos`, `id`, aunque Postgres sí distingue mayúsculas/minúsculas por defecto; Oracle no: guarda todo identificador sin comillas en mayúsculas sin importar cómo se escriba, así que aquí se escribe ya en mayúsculas para que el código coincida con lo que Oracle realmente almacena) en su migración Flyway (S1 de DIST, 3.5.1). La columna que sí conserva el prefijo es la llave foránea (`ID_CATEGORIA` en `PRODUCTOS`), porque ahí el prefijo identifica a qué tabla apunta.

**`ID_CATEGORIA` queda nullable a propósito, a diferencia de Distribuidas** (donde `Producto` lleva la relación con `Categoria` completa desde su propia S1): en LP2 esa relación se construye recién en S3 ("Gestión de objetos relacionados mediante REST y ORM"). Hasta entonces, `Producto` en LP2 (entidad, DTO y CRUD de S1-S2) no conoce `Categoria` en absoluto — si la columna fuera `NOT NULL`, cualquier `INSERT` sin categoría (incluido el `POST` que S2 construye) fallaría contra Oracle. En S3, cuando la entidad `Producto` ya mapee la relación y el DTO la exija, ahí se vuelve a evaluar si conviene forzar `NOT NULL` a nivel de base de datos — no antes.

Verifica:

```sql
DESC BOM_CATALOGO.PRODUCTOS;

SELECT ID, NOMBRE, PRECIO, STOCK FROM BOM_CATALOGO.PRODUCTOS;
```

#### Ver la lista de tablas creadas

Si quieres confirmar qué tablas existen (no una en particular, sino la lista completa), la consulta depende de con quién estés conectado:

Conectado como `system` (ve tablas de cualquier usuario, filtrando por dueño):

```sql
SELECT table_name FROM dba_tables WHERE owner = 'BOM_CATALOGO';
```

Conectado como `BOM_CATALOGO` (ve solo lo suyo, sin filtrar):

```sql
SELECT table_name FROM user_tables;
```

Deberían aparecer `CATEGORIAS` y `PRODUCTOS`. Si prefieres verlas en el **árbol** del cliente gráfico: usa la conexión con `Username: BOM_CATALOGO` (ver más arriba), refresca esa conexión (botón de reconectar, no solo refresh) y expande el nodo "Tables" — ahí deberían listarse directamente, sin necesitar SQL.

**Nota de alcance:** esta sesión crea únicamente el esquema `BOM_CATALOGO` y sus dos tablas — lo que LP2 S1-S2 necesitan. Los esquemas `BOM_VENTAS` y `BOM_SEGURIDAD` (y sus tablas) se crean recién en las sesiones de BD2 que los necesitan por primera vez (cuando LP2 llegue a `Venta` en S4, y a seguridad en S10) — mismo criterio de "no crear por si acaso" que ya aplica la arquitectura de LP2 a sus módulos (ver [ADR-002](../../lp2/adr/ADR-002-spring-modulith.md)).

### 3.3 Crear la función de negocio (parámetros `IN`)

**Producto del paso:** función PL/SQL reutilizable, con dos parámetros `IN`.

```sql
CREATE OR REPLACE FUNCTION BOM_CATALOGO.FN_VALOR_INVENTARIO_PRODUCTO(
    p_precio IN NUMBER,
    p_stock IN NUMBER
) RETURN NUMBER IS
BEGIN
    RETURN p_precio * p_stock;
END;
/
```

### 3.4 Crear el procedimiento de registro (parámetros `IN` y `OUT`)

**Producto del paso:** procedimiento con parámetros `IN` y `OUT`.

```sql
CREATE OR REPLACE PROCEDURE BOM_CATALOGO.SP_REGISTRAR_PRODUCTO(
    p_id_categoria IN NUMBER,
    p_nombre IN VARCHAR2,
    p_precio IN NUMBER,
    p_stock IN NUMBER,
    p_id_producto OUT NUMBER
) IS
BEGIN
    INSERT INTO BOM_CATALOGO.PRODUCTOS (ID_CATEGORIA, NOMBRE, PRECIO, STOCK)
    VALUES (p_id_categoria, p_nombre, p_precio, p_stock)
    RETURNING ID INTO p_id_producto;
END;
/
```

### 3.5 Crear el procedimiento de ajuste de precio (parámetro `IN OUT`)

**Producto del paso:** procedimiento con un parámetro `IN OUT`, distinto de tener un `IN` y un `OUT` separados: el mismo parámetro entra con el precio actual y sale con el precio ya ajustado.

```sql
CREATE OR REPLACE PROCEDURE BOM_CATALOGO.SP_APLICAR_DESCUENTO_PRODUCTO(
    p_precio IN OUT NUMBER,
    p_porcentaje_descuento IN NUMBER
) IS
BEGIN
    p_precio := p_precio - (p_precio * p_porcentaje_descuento / 100);
END;
/
```

### 3.6 Probar función y procedimientos

**Producto del paso:** evidencia de ejecución.

`SP_REGISTRAR_PRODUCTO` exige una categoría existente (`FK_PRODUCTO_CATEGORIA`). Si `CATEGORIAS` está vacía todavía (por ejemplo, si solo ejecutaste 3.2-3.5 sin datos de prueba), la prueba siguiente falla con `ORA-02291: integrity constraint ... violated - parent key not found`. Antes de probar, registra al menos una categoría:

```sql
INSERT INTO BOM_CATALOGO.CATEGORIAS (NOMBRE, DESCRIPCION) VALUES ('Perifericos', 'Perifericos de computo');
COMMIT;
```

```sql
DECLARE
    v_id_producto  NUMBER;
    v_precio       NUMBER(10,2) := 180.50;
    v_valor_inv    NUMBER;
BEGIN
    -- IN + OUT: registrar un producto nuevo
    BOM_CATALOGO.SP_REGISTRAR_PRODUCTO(1, 'Teclado mecánico', 180.50, 25, v_id_producto);
    DBMS_OUTPUT.PUT_LINE('Producto registrado: ' || v_id_producto);

    -- IN, IN: calcular el valor de inventario
    v_valor_inv := BOM_CATALOGO.FN_VALOR_INVENTARIO_PRODUCTO(180.50, 25);
    DBMS_OUTPUT.PUT_LINE('Valor de inventario: ' || v_valor_inv);

    -- IN OUT: ajustar el precio en la misma variable
    BOM_CATALOGO.SP_APLICAR_DESCUENTO_PRODUCTO(v_precio, 10);
    DBMS_OUTPUT.PUT_LINE('Precio con 10% de descuento: ' || v_precio);
END;
/
```

Resultado esperado: `Producto registrado: <id>`, `Valor de inventario: 4512.5`, `Precio con 10% de descuento: 162.45`.

### 3.7 Relacionar con ADS y LP2

**Producto del paso:** matriz de integración.

**Tabla 4. Matriz de integración BD2-ADS-LP2**

| Objeto BD2 | Decisión ADS | Endpoint o servicio LP2 |
|---|---|---|
| `SP_REGISTRAR_PRODUCTO` | Integridad: reglas transaccionales en servicio y Oracle | `POST /api/v1/productos` (S2), regla equivalente |
| `FN_VALOR_INVENTARIO_PRODUCTO` | Centralizar regla de cálculo del negocio | Cálculo que también podría exponerse en un reporte (S4-S5) |
| `SP_APLICAR_DESCUENTO_PRODUCTO` | Mantenibilidad: lógica reutilizable, no repetida | `PUT /api/v1/productos/{id}` (S2), regla equivalente |

Sesión equivalente en los otros dos cursos, misma semana: [ADS - S1 Fundamentos de Arquitectura de Software](../../ads/sesiones/S01_Fundamentos_Arquitectura_Software.md) y [LP2 - S1 Arquitectura Backend REST Profesional](../../lp2/sesiones/S01_Arquitectura_Backend_REST_Profesional.md).

**Evidencia de aprendizaje:**

- Esquema y tablas del catálogo (`CATEGORIAS`, `PRODUCTOS`) creados en Oracle.
- Función y procedimientos PL/SQL (`IN`, `IN`/`OUT`, `IN OUT`) probados con resultados correctos.
- Matriz de integración con ADS y LP2.

## 4. Crea: actividad autónoma

Tiempo: 2h fuera del aula.

### 4.1 Actividad

Creación autónoma del esquema y el motor transaccional PL/SQL para la entidad transaccional del proyecto propio del equipo, documentada en evidencia individual.

Completa y evidencia estas tareas:

1. Definir la operación transaccional del proyecto.
2. Crear el esquema (usuario propietario) y las tablas mínimas de prueba.
3. Crear una función PL/SQL de cálculo o validación.
4. Crear un procedimiento con parámetros `IN` y `OUT`.
5. Ejecutar al menos un caso válido.
6. Registrar una mejora que se implementará en S2 o S3.

### 4.2 Propósito

Que cada estudiante demuestre, de forma individual y fuera del aula, que puede reproducir el patrón construido en clase sin el acompañamiento del docente.

Cada estudiante adapta el ejemplo a la entidad transaccional de su equipo.

### 4.3 Indicaciones

Entrega un PDF con el siguiente nombre:

```text
S01_BD2_Equipo##_ApellidoNombre.pdf
```

Cada captura de pantalla del informe debe mostrar, sin recortar, el reloj del sistema (fecha y hora) y tu usuario o foto de perfil (Windows, VS Code o navegador) visibles en pantalla — es lo que permite verificar que la evidencia es tuya y que corresponde al momento real de tu trabajo.

#### 4.3.1 Estructura del informe

**Datos del estudiante**

- Nombre:
- Equipo:
- Sesión: S01 - PL/SQL Aplicado al Negocio
- Rol o aporte realizado:
- Link de GitHub:

**Evidencia técnica**

Incluye capturas o salidas con una breve explicación debajo de cada una, organizadas en los mismos 5 bloques de la rúbrica (4.6) — así queda claro qué evidencia corresponde a cada criterio evaluado:

1. *Esquema y tablas del proyecto propio*
    - Script SQL de creación del esquema y las tablas base.
2. *Operación transaccional*
    - Breve explicación de la operación transaccional definida para el proyecto.
3. *Función PL/SQL*
    - Script de la función PL/SQL.
4. *Procedimiento PL/SQL*
    - Script del procedimiento PL/SQL, con breve explicación.
5. *Evidencia de ejecución*
    - Captura o salida de ejecución.
    - Resultado de consulta a la tabla.

**Error o hallazgo**

Describe un error técnico encontrado: compilación, tipos, parámetros, claves, reglas o pruebas.

**Reflexión técnica breve**

Responde en 5 a 8 líneas:

```text
¿Qué regla del negocio conviene proteger en Oracle y por qué no debería quedar solo en el backend?
```

### 4.4 Criterios mínimos de aceptación

La evidencia individual se considera completa si:

- El archivo respeta el nombre solicitado.
- Crea el esquema/usuario propietario y las tablas base antes de la lógica PL/SQL.
- Presenta función y procedimiento compilables.
- Usa parámetros `IN` y `OUT`.
- Ejecuta un caso válido.
- Incluye evidencia de ejecución.
- Cada captura de la evidencia técnica muestra el reloj del sistema y el usuario/perfil visible, sin recortar.
- Las fechas y horas de las capturas son coherentes con el historial de commits de su repositorio en GitHub.
- Incluye un error o hallazgo técnico diagnosticado.
- Incluye la reflexión técnica breve solicitada.

### 4.5 Preguntas de defensa

1. ¿Qué operación transaccional implementaste?
2. ¿Por qué creaste una función y no solo un procedimiento?
3. ¿Qué parámetro `OUT` devuelve tu procedimiento?
4. ¿Qué validación falta mejorar en S2 o S3?

### 4.6 Rúbrica de evaluación

**Tabla 5. Rúbrica de evaluación**

| Criterio | Peso (%) | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Nivel obtenido |
|---|---:|---|---|---|---|---:|
| 1. Esquema y tablas del proyecto propio* | 20 | Esquema y tablas propias creados correctamente, con permisos mínimos definidos para el usuario de aplicación. | Esquema y tablas funcionales, con detalles menores. | Esquema o tablas incompletos. | No crea esquema ni tablas. | |
| 2. Operación transaccional* | 20 | Define una operación transaccional clara y alineada al proyecto propio del equipo. | Define una operación comprensible. | Operación parcial o poco conectada. | No define operación. | |
| 3. Función PL/SQL* | 20 | Función correcta, reutilizable y probada. | Función funcional con detalles menores. | Función incompleta o poco probada. | No presenta función. | |
| 4. Procedimiento PL/SQL* | 20 | Procedimiento correcto con parámetros `IN`/`OUT` y registro exitoso. | Procedimiento funcional con detalles menores. | Procedimiento parcial o con errores. | No presenta procedimiento. | |
| 5. Evidencia de ejecución* | 20 | Presenta ejecución y consulta verificable del caso de prueba. | Presenta evidencia suficiente. | Evidencia incompleta. | No evidencia ejecución. | |

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

**Resumen breve:** hoy el esquema `BOM_CATALOGO` pasó de no existir a tener tablas base y su primer motor transaccional: una función y un procedimiento PL/SQL que validan reglas del negocio con parámetros `IN` y `OUT` — la base sobre la que LP2 conecta su backend.

**Dinámica participativa:** en una ronda rápida (o con una herramienta digital tipo formulario o encuesta en vivo), cada estudiante comparte en una frase qué validación implementó en su función o procedimiento.

**Metacognición:** cada estudiante responde en voz alta o por escrito: ¿qué parte de la sesión te costó más entender, y cómo la resolviste?

**Proyección:** el motor transaccional de hoy se amplía en S2 con triggers y auditoría, y el hábito de proteger reglas de negocio en la base de datos aplica a cualquier sistema profesional donde varios clientes (no solo un backend) necesiten confiar en los mismos datos.

## Bibliografía

1. Oracle Corporation. (2024). *Oracle Database Free 23ai documentation*. https://docs.oracle.com/en/database/oracle/oracle-database/23/
2. Oracle Corporation. (2024). *Database PL/SQL language reference*. https://docs.oracle.com/en/database/oracle/oracle-database/23/lnpls/

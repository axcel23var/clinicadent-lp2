# Sílabo BD2 2026-2

# Administración de Base de Datos II

## I. Información General de Asignatura

<table>
  <tr><td>01</td><td>Facultad/EGP</td><td>Facultad de Ingeniería y Arquitectura</td><td>09</td><td>Año de plan de estudio</td><td>2024-1</td></tr>
  <tr><td></td><td></td><td></td><td>10</td><td>Ciclo de estudio</td><td>4</td></tr>
  <tr><td>02</td><td>Programa de estudio</td><td>EP Ingeniería de Sistemas</td><td>11</td><td>Código de asignatura</td><td></td></tr>
  <tr><td>03</td><td>Tipo de estudio</td><td>Especialidad</td><td>12</td><td>Número de créditos</td><td>3</td></tr>
  <tr><td>04</td><td>Nombre de asignatura</td><td>Administración de Base de Datos II</td><td>13</td><td>Nota mínima probatoria</td><td>13</td></tr>
  <tr><td>05</td><td>Duración</td><td></td><td>14</td><td>Año y semestre académico</td><td>2026-2</td></tr>
  <tr><td>06</td><td>Horas de la asignatura</td><td colspan="4">H. Te. Pract 2 H. Prc. Pres 2 H. Te. On. Sin</td></tr>
  <tr><td>07</td><td>Docente</td><td>Gutierrez Quispe Eder</td><td></td><td></td><td></td></tr>
  <tr><td>08</td><td>Pre-requisito</td><td>Administración de Base de Datos I</td><td></td><td></td><td></td></tr>
</table>

## II. Sumilla

Asignatura de Administración de base de datos II pertenece al área de ingeniería de software es de carácter teórico práctico, de tipo obligatorio. Aporta al perfil de egreso en la competencia específica de ingeniería de software, cuyo objetivo es que el estudiante sea capaz de brindar el soporte computacional para la administración de la Base de Datos de las organizaciones. El curso contempla los siguientes aspectos: PL/SQL, gestión de espacios y objetos de base de datos, gestión de usuarios, privilegios, seguridad, respaldos y afinamiento..

## III. Competencias del Perfil de Egreso

### Competencia general

**PENSAMIENTO SUPERIOR**  
N°1.1 FIRMEZA DE PROPÓSITO, EJECUCIÓN, DOMINIO PROPIO, MANTENER ESFUERZO

### Competencia específica

**INGENIERÍA DE SOFTWARE**  
Gestiona y desarrolla software aplicando estándares internacionales y buenas prácticas, para resolver problemas de las organizaciones.

N°1.1 INGENIERÍA DE LA INFORMACIÓN, PROGRAMACIÓN

## IV. Resultado de aprendizaje de la asignatura

<table>
  <tr><td>Resultado de aprendizaje</td><td>Producto Académico</td></tr>
  <tr>
    <td>Administra bases de datos empresariales Oracle mediante programación PL/SQL, administración de la instancia, almacenamiento, seguridad, auditoría, optimización, respaldo, recuperación y monitoreo, garantizando operación, rendimiento, disponibilidad, trazabilidad y resiliencia de los datos.</td>
    <td>Nombre: Base de datos empresarial Oracle operativa, administrada, optimizada, auditada y resiliente.</td>
  </tr>
  <tr>
    <td>Criterios de evaluación del producto</td>
    <td>Descripción: Incluye programación PL/SQL aplicada al negocio, triggers, manejo de excepciones, optimización SQL, índices, administración de instancia Oracle, usuarios, roles, privilegios, tablespaces, datafiles, redo logs, undo, archivelog, auditoría, particionamiento, RMAN, Data Pump, recuperación, monitoreo, diagnóstico y evidencias técnicas de operación empresarial.</td>
  </tr>
</table>

## V. Unidades de aprendizaje

### Unidad 1: Programación y optimización (Oracle XE)

<table>
  <tr><td colspan="4">Resultado de aprendizaje</td><td colspan="3">Producto</td></tr>
  <tr><td colspan="4">Implementa lógica transaccional del lado del servidor y optimiza consultas SQL utilizando PL/SQL, triggers, manejo de excepciones, estadísticas e índices en Oracle XE.</td><td colspan="3">Nombre: Motor transaccional Oracle optimizado.</td></tr>
  <tr><td colspan="4">Criterios de evaluación del producto</td><td colspan="3">Descripción del producto</td></tr>
  <tr><td colspan="4">1 Implementa procedimientos y funciones PL/SQL alineados al negocio.<br>2 Automatiza reglas mediante triggers DML.<br>3 Controla errores mediante manejo de excepciones.<br>4 Analiza y mejora consultas mediante Explain Plan, CBO y DBMS_STATS.<br>5 Aplica estrategias de indexación según selectividad y necesidades de consulta.</td><td colspan="3">Motor transaccional implementado en Oracle XE con PL/SQL, triggers, excepciones, optimización de consultas e índices documentados y sustentados.</td></tr>
  <tr><td colspan="7">Sesiones de aprendizaje</td></tr>
  <tr><td>N°</td><td>Fecha</td><td>Contenido</td><td>HT</td><td>HP</td><td>Actividad práctica</td><td>Actividad autónoma</td></tr>
  <tr><td>1</td><td>10/08/2026  15/08/2026</td><td><strong>PL/SQL aplicado al negocio:</strong><br>Creación del esquema y las tablas base del caso empresarial, procedimientos, funciones, parámetros IN / OUT / IN OUT y casos empresariales.</td><td>2</td><td>2</td><td>Crear el esquema y las tablas base del caso de negocio, e implementar procedimientos y funciones PL/SQL para sus operaciones.</td><td>Completar scripts PL/SQL del proyecto y documentar parámetros, entradas, salidas y reglas aplicadas.</td></tr>
  <tr><td>2</td><td>16/08/2026  22/08/2026</td><td><strong>Triggers DML:</strong><br>:OLD, :NEW, reglas automáticas de negocio y auditoría básica.</td><td>2</td><td>2</td><td>Implementar triggers para automatizar reglas y registrar eventos relevantes.</td><td>Agregar triggers al proyecto y probar escenarios con inserción, actualización y eliminación.</td></tr>
  <tr><td>3</td><td>23/08/2026  29/08/2026</td><td><strong>Manejo de excepciones y robustez:</strong><br>Excepciones predefinidas, personalizadas, registro de errores y tolerancia a fallos.</td><td>2</td><td>2</td><td>Incorporar manejo de excepciones en procedimientos, funciones y bloques PL/SQL.</td><td>Documentar errores controlados, mensajes y escenarios de prueba del motor transaccional.</td></tr>
  <tr><td>4</td><td>30/08/2026  05/09/2026</td><td><strong>Optimización de consultas SQL:</strong><br>Cost Based Optimizer (CBO), Explain Plan, DBMS_STATS y buenas prácticas SQL.</td><td>2</td><td>2</td><td>Analizar planes de ejecución y mejorar consultas representativas del proyecto.</td><td>Registrar evidencias antes/después de optimización y actualizar estadísticas con DBMS_STATS.</td></tr>
  <tr><td>5</td><td>06/09/2026  12/09/2026</td><td><strong>Índices para optimización:</strong><br>B-Tree, Bitmap, Function-Based Index, selectividad y estrategias de indexación.</td><td>2</td><td>2</td><td>Diseñar e implementar índices pertinentes según consultas frecuentes y selectividad.</td><td>Justificar índices creados, impacto en consultas y criterios de uso en el proyecto.</td></tr>
  <tr><td>6</td><td>13/09/2026  19/09/2026</td><td><strong>Integración del motor transaccional Oracle:</strong><br>Programación PL/SQL, transacciones, estructuras de almacenamiento, índices, planes de ejecución y optimización SQL.</td><td>2</td><td>2</td><td><strong>Evaluación de la Unidad I:</strong><br>1. Resolver la evaluación teórico-práctica de los temas de la Unidad I.<br>2. Presentar y sustentar el Motor transaccional Oracle optimizado.</td><td>Incorporar observaciones y preparar el producto para administración del motor Oracle.</td></tr>
</table>

### Unidad 2: Administración, almacenamiento, seguridad y optimización

<table>
  <tr><td colspan="4">Resultado de aprendizaje</td><td colspan="3">Producto</td></tr>
  <tr><td colspan="4">Administra la instancia Oracle, usuarios, privilegios, almacenamiento, seguridad, auditoría, rendimiento y escalabilidad sobre Oracle Database 19c EE y Oracle Linux.</td><td colspan="3">Nombre: Base de datos empresarial administrada, optimizada y asegurada.</td></tr>
  <tr><td colspan="4">Criterios de evaluación del producto</td><td colspan="3">Descripción del producto</td></tr>
  <tr><td colspan="4">1 Reconoce la arquitectura Oracle y administra la instancia desde Linux.<br>2 Configura usuarios, roles y privilegios bajo mínimo privilegio.<br>3 Administra almacenamiento, seguridad y auditoría.<br>4 Optimiza rendimiento mediante estadísticas, planes, AWR e índices.<br>5 Aplica particionamiento para escalabilidad.</td><td colspan="3">Base de datos empresarial que continúa el producto U1 e incorpora administración del motor Oracle, almacenamiento, seguridad, auditoría, optimización y particionamiento.</td></tr>
  <tr><td colspan="7">Sesiones de aprendizaje</td></tr>
  <tr><td>N°</td><td>Fecha</td><td>Contenido</td><td>HT</td><td>HP</td><td>Actividad práctica</td><td>Actividad autónoma</td></tr>
  <tr><td>7</td><td>20/09/2026  26/09/2026</td><td><strong>Arquitectura Oracle e instancia:</strong><br>SGA, PGA, procesos background, ORACLE_HOME, ORACLE_SID, administración desde Oracle Linux y SYSDBA.</td><td>2</td><td>2</td><td>Explorar arquitectura Oracle, variables de entorno e inicio de administración como SYSDBA.</td><td>Documentar configuración de entorno, componentes de instancia y comandos básicos de administración.</td></tr>
  <tr><td>8</td><td>27/09/2026  03/10/2026</td><td><strong>Gestión de usuarios, roles y privilegios:</strong><br>Privilegios de sistema, objetos y principio de mínimo privilegio.</td><td>2</td><td>2</td><td>Crear usuarios, roles y privilegios adecuados para el caso empresarial.</td><td>Aplicar política de mínimo privilegio y documentar matriz de accesos del proyecto.</td></tr>
  <tr><td>9</td><td>04/10/2026  10/10/2026</td><td><strong>Administración del almacenamiento y seguridad:</strong><br>Tablespaces, segmentos, extents, datafiles, redo logs, undo, archivelog, auditoría de sentencias, auditoría de acceso y Enterprise Manager Express.</td><td>2</td><td>2</td><td>Configurar almacenamiento, revisar componentes físicos y activar evidencias básicas de auditoría.</td><td>Documentar estructura de almacenamiento, configuración de auditoría y capturas de Enterprise Manager Express.</td></tr>
  <tr><td>10</td><td>11/10/2026  17/10/2026</td><td><strong>Optimización del rendimiento:</strong><br>Explain Plan, Cost Based Optimizer, DBMS_STATS, Automatic Workload Repository (AWR) e índices.</td><td>2</td><td>2</td><td>Analizar rendimiento con planes, estadísticas, AWR e índices sobre consultas críticas.</td><td>Elaborar informe de optimización con evidencias, métricas y decisiones de ajuste.</td></tr>
  <tr><td>11</td><td>18/10/2026  24/10/2026</td><td><strong>Particionamiento y escalabilidad:</strong><br>Range, Hash, List, Composite Partition, estrategias para grandes volúmenes e impacto en el rendimiento.</td><td>2</td><td>2</td><td>Diseñar una estrategia de particionamiento para tablas de alto volumen.</td><td>Implementar o documentar particionamiento aplicable al proyecto y justificar impacto esperado.</td></tr>
  <tr><td>12</td><td>25/10/2026  31/10/2026</td><td><strong>Integración de la administración de Oracle:</strong><br>Almacenamiento, seguridad, auditoría, optimización, automatización y operación del motor.</td><td>2</td><td>2</td><td><strong>Evaluación de la Unidad II:</strong><br>1. Resolver la evaluación teórico-práctica de los temas de la Unidad II.<br>2. Presentar y sustentar la Base de datos empresarial administrada, optimizada y asegurada.</td><td>Corregir observaciones y preparar evidencias de continuidad del negocio.</td></tr>
</table>

### Unidad 3: Continuidad del negocio y operación empresarial

<table>
  <tr><td colspan="4">Resultado de aprendizaje</td><td colspan="3">Producto</td></tr>
  <tr><td colspan="4">Garantiza continuidad del negocio y operación empresarial mediante respaldo, recuperación, monitoreo, diagnóstico y sustentación integral de la base de datos Oracle.</td><td colspan="3">Nombre: Base de datos empresarial Oracle operativa, administrada, optimizada, auditada y resiliente.</td></tr>
  <tr><td colspan="4">Criterios de evaluación del producto</td><td colspan="3">Descripción del producto</td></tr>
  <tr><td colspan="4">1 Implementa estrategias de backup y recovery con RMAN.<br>2 Usa Data Pump y escenarios de recuperación como PITR.<br>3 Monitorea sesiones, bloqueos y rendimiento con vistas administrativas y herramientas Oracle.<br>4 Sustenta evidencias técnicas de programación, administración, seguridad, auditoría, optimización y recuperación.<br>5 Presenta el producto final operativo, auditado y resiliente.</td><td colspan="3">Producto final del curso que continúa U2 e incorpora backup, recovery, monitoreo, diagnóstico y operación empresarial documentada.</td></tr>
  <tr><td colspan="7">Sesiones de aprendizaje</td></tr>
  <tr><td>N°</td><td>Fecha</td><td>Contenido</td><td>HT</td><td>HP</td><td>Actividad práctica</td><td>Actividad autónoma</td></tr>
  <tr><td>13</td><td>01/11/2026  07/11/2026</td><td><strong>Backup y Recovery con RMAN:</strong><br>Respaldo completo e incremental (L0/L1), Data Pump, Point-In-Time Recovery (PITR) y escenarios reales de recuperación.</td><td>2</td><td>2</td><td>Configurar y ejecutar respaldos, exportaciones y un escenario controlado de recuperación.</td><td>Documentar estrategia de respaldo, evidencias de ejecución y procedimiento de recuperación.</td></tr>
  <tr><td>14</td><td>08/11/2026  14/11/2026</td><td><strong>Monitoreo y diagnóstico:</strong><br>Vistas V$, DBA_, sesiones, bloqueos, rendimiento, Enterprise Manager Express y Oracle Cloud Control.</td><td>2</td><td>2</td><td>Monitorear sesiones, bloqueos, objetos y rendimiento usando vistas administrativas y herramientas Oracle.</td><td>Elaborar el reporte de monitoreo y preparar el guion de demostración, las evidencias, la documentación y la explicación técnica del producto.</td></tr>
  <tr><td>15</td><td>15/11/2026  21/11/2026</td><td><strong>Integración de la operación empresarial en Oracle:</strong><br>Programación PL/SQL, administración, almacenamiento, seguridad, auditoría, optimización, respaldo, continuidad y monitoreo.</td><td>2</td><td>2</td><td><strong>Evaluación de la Unidad III:</strong><br>1. Resolver la evaluación teórico-práctica de los temas de la Unidad III.<br>2. Presentar y sustentar la Base de datos empresarial Oracle operativa, administrada, optimizada, auditada y resiliente.</td><td>Registrar las observaciones del docente y actualizar las evidencias y la documentación final.</td></tr>
  <tr><td>16</td><td>22/11/2026  28/11/2026</td><td><strong>Integración de administración y continuidad de bases de datos:</strong><br>Programación, seguridad, auditoría, optimización, respaldo, continuidad, monitoreo y diagnóstico en Oracle.</td><td>2</td><td>2</td><td><strong>Continuación de la evaluación de la Unidad III:</strong><br>Realizar, según corresponda, la evaluación final individual o las presentaciones y sustentaciones pendientes mediante demostración, preguntas técnicas y revisión de evidencias.</td><td>Entregar la versión final del producto, las evidencias y la documentación técnica.</td></tr>
</table>

## VI. Estrategias metodológicas

<table>
  <tr><td>N°</td><td>Estrategias de enseñanza y de aprendizaje que se aplicarán en la asignatura</td></tr>
  <tr><td>1.1</td><td>Aprendizaje Basado en Problemas: centra el aprendizaje en problemas reales de administración, seguridad, rendimiento y recuperación de bases de datos empresariales.</td></tr>
  <tr><td>1.2</td><td>Estudios de caso: permite aplicar PL/SQL, administración Oracle, auditoría, optimización y recuperación a un caso empresarial progresivo.</td></tr>
  <tr><td>1.3</td><td>Proyectos: integra las sesiones en un producto único desarrollado por etapas durante el semestre.</td></tr>
  <tr><td>1.4</td><td>Simulación: permite ensayar escenarios de falla, respaldo, recuperación, monitoreo y diagnóstico en un entorno controlado.</td></tr>
</table>

## VII. Recursos, medios y materiales

<table>
  <tr><td>N°</td><td>Recursos medios y materiales</td></tr>
  <tr><td>1</td><td>Guías y/o tutoriales</td></tr>
  <tr><td>2</td><td>Laboratorios</td></tr>
  <tr><td>3</td><td>Oracle XE</td></tr>
  <tr><td>4</td><td>Oracle Database 19c EE</td></tr>
  <tr><td>5</td><td>Oracle Linux</td></tr>
  <tr><td>6</td><td>Enterprise Manager Express</td></tr>
  <tr><td>7</td><td>Internet - Wifi</td></tr>
  <tr><td>8</td><td>Proyector y/o TV Smart</td></tr>
</table>

## VIII. Evaluación

<table>
  <tr><td>Fecha</td><td>Unidad</td><td>Producto</td><td>Evaluación de proceso y de resultado</td><td>Pesos</td></tr>
  <tr><td>18/09/2026</td><td>Unidad 1: Programación y optimización (Oracle XE)</td><td>Motor transaccional Oracle optimizado.</td><td>Evaluación del producto</td><td>20%</td></tr>
  <tr><td></td><td></td><td></td><td>Evaluación de sesiones</td><td>5%</td></tr>
  <tr><td>30/10/2026</td><td>Unidad 2: Administración, almacenamiento, seguridad y optimización</td><td>Base de datos empresarial administrada, optimizada y asegurada.</td><td>Evaluación del producto</td><td>20%</td></tr>
  <tr><td></td><td></td><td></td><td>Evaluación de sesiones</td><td>5%</td></tr>
  <tr><td>20/11/2026</td><td>Unidad 3: Continuidad del negocio y operación empresarial</td><td>Base de datos empresarial Oracle operativa, administrada, optimizada, auditada y resiliente.</td><td>Evaluación del producto</td><td>30%</td></tr>
  <tr><td></td><td></td><td></td><td>Evaluación de sesiones</td><td>10%</td></tr>
  <tr><td>20/11/2026</td><td>Competencia General</td><td>Servicio y misión.</td><td>Competencia General</td><td>10%</td></tr>
</table>

## IX. Referencias

### Básica (Fuentes primarias)

1. Malcher, M. & Kuhn, D. (2024). *Pro Oracle Database 23ai Administration: Manage and Safeguard Your Organization's Data*. Apress. ISBN 979-8-8688-1037-4.
2. Oracle. *Oracle Database 19c Administrator's Guide*. Oracle Documentation.
3. Oracle. *Oracle Database 19c Backup and Recovery User's Guide*. Oracle Documentation.
4. Oracle. *Oracle Database 19c Performance Tuning Guide*. Oracle Documentation.
5. Oracle. *Oracle Database PL/SQL Language Reference*. Oracle Documentation.
6. Oracle. *Oracle Database Security Guide*. Oracle Documentation.
7. Oracle. *Oracle Database 2 Day DBA*. Oracle Documentation.
8. Silberschatz, A., Korth, H. F. y Sudarshan, S. (1999). *Fundamentos de bases de datos*. McGraw-Hill Interamericana.
9. Álvarez Caules, C. (2024). *Arquitectura Java sólida y patrones*. Arquitectura Java. [PDF](https://www.arquitecturajava.com/wp-content/uploads/LibroArquitecturaJavaSolidayPatronesV2.pdf).
10. Reina Valera. (1960). *La Santa Biblia*. Sociedades Bíblicas Unidas.

### Complementaria (Fuentes secundarias)

1. Ault, M. (2008). *Oracle Database 11g New Features*. Rampant TechPress.
2. Roblero Pérez, M. E. & Cruz Antón, F. M. (2015). *Desarrollo de datamart para mejorar la toma de decisiones en los análisis académicos de la Universidad Adventista de Bolivia*. Revista de Investigación Ciencia, Tecnología y Desarrollo, 1(2), 27-35.
3. Palomino O., Y. & Andrango H., R. (2015). *Implementación de una solución de inteligencia de negocios para la generación de indicadores y control de desempeño en la empresa OTECEL S.A. utilizando la metodología HEFESTO V2.0*.
4. Vargas Marino, J. (2020). *Implementación de una solución de Inteligencia de Negocios para el análisis de indicadores de ventas en la empresa Hakansson*. Universidad Peruana Unión.
5. Sánchez G., J. *Rumbo al nuevo enfoque del Business Intelligence*.
6. Oracle. *Oracle Database 12c Administrator Certified Associate*.
7. Oracle. *Oracle Database Online Documentation 11g Release 2 (11.2)*.
8. Cornejo, A. *Administración de Oracle 10g*.
9. *Fundamentos de Oracle* (autor no identificado).
10. Oracle. *Administrator's Guide* (edición previa a 19c).

### Enlaces de internet

1. <https://bsoftgroup.com/educate/>
2. <https://codearti.com/>

## Evolución del producto

<table>
  <tr><td>Unidad</td><td>Producto</td><td>Evolución</td></tr>
  <tr><td>U1</td><td>Motor transaccional Oracle optimizado</td><td>Desarrollo con PL/SQL, triggers, excepciones, optimización e índices.</td></tr>
  <tr><td>U2</td><td>Base de datos empresarial administrada, optimizada y asegurada</td><td>U1 + arquitectura Oracle, administración, almacenamiento, seguridad, auditoría y particionamiento.</td></tr>
  <tr><td>U3</td><td>Base de datos empresarial Oracle operativa, administrada, optimizada, auditada y resiliente</td><td>U2 + backup, recovery, monitoreo y operación empresarial. Este es el producto final del curso.</td></tr>
</table>

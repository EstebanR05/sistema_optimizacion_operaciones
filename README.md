# Sistema de Optimización de Operaciones — Comparativa Java vs Python

Proyecto académico que implementa y resuelve seis problemas clásicos de optimización (programación lineal y entera mixta) en dos entornos: Java y Python. El objetivo es comparar facilidad de desarrollo, resultados y desempeño entre ambas implementaciones.

Universidad: Unicomfacauca

Autores:
- Ariana Marcela Andrade Bello
- Emanuel Esteban Restrepo Patarroyo

Fecha: 2026

## Contenido

Este repositorio contiene:

- `latex/` — Documento principal del trabajo (investigacion_operaciones.tex) con la formulación matemática, discusión y comparativa de resultados.
- `java/` — Implementación en Java. Contiene código fuente en `java/src/main/java/com/investigacion_operaciones/java/` y un `pom.xml` para compilar con Maven. La clase principal con un menú interactivo es `Java.java`.
- `python/` — Implementación en Python. El archivo principal es `python/problemas_solucionados_general.py` (se trabajó originalmente en Colab).

Además hay salidas/artefactos generados en `target/` (clases compiladas de Java) y archivos auxiliares `output java.txt` y `output python.txt`.

## Problemas implementados

Se implementaron seis ejercicios con diferentes niveles de complejidad:

1. Producción de muebles (PL entera)
2. Asignación de aplicaciones en servidores (PL continua)
3. Apertura de bodegas y transporte (PLEM)
4. Planificación de producción con apertura de plantas (PLEM)
5. Programación de bombas en red hidráulica (modelo discreto horario)
6. Planificación multi-período de producción, distribución e inventarios (PLEM multi-período)

La formulación matemática y el análisis comparativo están en `latex/investigacion_operaciones.tex`.

## Requisitos

- Java JDK 11+ y Maven (para compilar y ejecutar la versión en Java).
- Python 3.8+ y pip (para ejecutar la versión en Python).
- Biblioteca Python: `pulp` (resolver problemas de programación lineal/entera).

Recomendado: crear un entorno virtual para Python antes de instalar dependencias.

## Ejecutar la implementación en Java

1. Desde la raíz del repositorio compilar con Maven (archivo `pom.xml` en la carpeta `java/`):

```bash
mvn -f java/pom.xml package
```

2. Ejecutar la clase principal (si compilaste localmente las clases se ubican en `java/target/classes`):

```bash
java -cp java/target/classes com.investigacion_operaciones.java.Java
```

Alternativa (si tienes el plugin exec en el `pom.xml`):

```bash
mvn -f java/pom.xml exec:java -Dexec.mainClass="com.investigacion_operaciones.java.Java"
```

Notas:
- El menú de la clase `Java` permite ejecutar cada ejercicio por separado desde la interfaz de consola.
- Si ya existen clases compiladas en `java/target/classes` no es necesario recompilar.

## Ejecutar la implementación en Python

1. Crear y activar un entorno virtual (opcional pero recomendado):

```bash
python3 -m venv .venv
source .venv/bin/activate
```

2. Instalar la dependencia requerida:

```bash
pip install pulp
```

3. Ejecutar el script principal:

```bash
python3 python/problemas_solucionados_general.py
```

Importante: el archivo `python/problemas_solucionados_general.py` se generó desde Google Colab y contiene una línea tipo `!pip install pulp -q` propia de notebooks. Antes de ejecutar el script localmente es recomendable eliminar o comentar dicha línea si está presente.

## Resultados y comparativa

El informe en `latex/investigacion_operaciones.tex` resume la formulación matemática de cada ejercicio, las soluciones obtenidas y una discusión comparativa entre Java y Python en términos de:

- Precisión y consistencia de las soluciones
- Facilidad de implementación y mantenimiento
- Dependencias y ecosistema (librerías de optimización)
- Tiempo de desarrollo y reproducibilidad

Consulta el PDF generado del LaTeX para ver tablas de resultados y conclusiones.

## Estructura de carpetas (resumen)

- `java/` — código Java + pom.xml
- `python/` — script(s) Python
- `latex/` — documento LaTeX del trabajo
- `target/` — artefactos de compilación Java (no versionar en proyectos nuevos)

## Buenas prácticas y próximos pasos

- Añadir un `requirements.txt` para la parte Python (por ejemplo: `pulp`).
- Añadir scripts de ejecución o un Makefile para estandarizar los comandos de compilación/ejecución.
- Incluir tests unitarios o ejemplos reproducibles para cada ejercicio.

## Créditos

Trabajo realizado por Ariana Marcela Andrade Bello y Emanuel Esteban Restrepo Patarroyo para Unicomfacauca.

## Licencia

Este repositorio no especifica una licencia. Si deseas añadir una (por ejemplo MIT), dime y la agrego.

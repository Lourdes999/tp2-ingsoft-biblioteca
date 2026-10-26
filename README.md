# Sistema de Gestión de Biblioteca

Trabajo Práctico Nº 2 – Ingeniería de Software – Prof. Ariel Reynoso
**Integrantes:** Lourdes Melo y Camila Estevez

Aplicación de consola en Java para gestionar los socios, libros y préstamos de una biblioteca.
Usa arquitectura en capas y guarda la información en PostgreSQL.

## Requisitos

- Java 17 o superior
- Maven (incluido en IntelliJ IDEA)
- PostgreSQL 14 o superior (con pgAdmin o psql)

## Configuración de la base de datos

Los scripts están en la carpeta `sql/` y se ejecutan en este orden:

| Script | Dónde se ejecuta | Qué hace |
| --- | --- | --- |
| `01_crear_base.sql` | Conectado a la base `postgres` | Crea la base `biblioteca` |
| `02_crear_tablas.sql` | Conectado a la base `biblioteca` | Crea las tablas `socio`, `libro` y `prestamo` |
| `03_datos_prueba.sql` | Conectado a la base `biblioteca` | Carga socios, libros y préstamos ficticios |

**Con pgAdmin:** clic derecho en la base → *Query Tool* → abrir el script → ejecutar (F5).

**Con psql:**

```
psql -U postgres -f sql/01_crear_base.sql
psql -U postgres -d biblioteca -f sql/02_crear_tablas.sql
psql -U postgres -d biblioteca -f sql/03_datos_prueba.sql
```

Después, revisar `src/main/resources/config.properties` y poner el usuario y la contraseña de PostgreSQL de la instalación local:

```
db.url=jdbc:postgresql://localhost:5432/biblioteca
db.usuario=postgres
db.password=postgres
```

Para volver a los datos iniciales, ejecutar de nuevo `02_crear_tablas.sql` y `03_datos_prueba.sql`.

## Ejecución

**Desde IntelliJ:** abrir el proyecto (la carpeta que contiene `pom.xml`), esperar a que Maven descargue las dependencias y ejecutar `src/main/java/biblioteca/presentacion/Main.java`.

**Desde la terminal:**

```
mvn compile exec:java
```

**Pruebas unitarias:** `mvn test` o, en IntelliJ, clic derecho en `src/test/java` → *Run All Tests*.

## Perfiles y operaciones

Al iniciar, se elige el perfil desde el menú principal.

| Perfil | Cómo ingresa | Operaciones |
| --- | --- | --- |
| Socio | Indicando un número de socio existente | Buscar libros por título o autor (con disponibilidad) · Consultar sus propios préstamos |
| Bibliotecario | Directamente | Registrar, modificar, consultar y dar de baja socios · Buscar libros · Registrar préstamos y devoluciones (con comprobante) · Historial de un socio · Listar préstamos vigentes y vencidos |
| Administrador | Directamente | Registrar, modificar, consultar y dar de baja libros · Buscar libros · Listar socios · Historial de un socio · Listar préstamos vigentes y vencidos |

## Datos de prueba

| Socio N° | Nombre | Situación |
| --- | --- | --- |
| 1001 | Juan Pérez | 1 préstamo vigente |
| 1002 | Ana Gómez | 1 préstamo vencido: no puede retirar libros (RN-06) |
| 1003 | Lucía Fernández | 3 préstamos sin devolver: alcanzó el límite (RN-03) |
| 1004 | Pedro Ruiz | Inactivo: no puede retirar libros (RN-01) |
| 1005 | Martina López | Sin préstamos pendientes, con historial |
| 1006 | Carlos Díaz | Sin préstamos: puede retirar libros |

Libros: 12 ejemplares (IDs 1 a 12). Hay dos ejemplares de *Ficciones* (IDs 1 y 2) y dos de *Cien años de soledad* (IDs 9 y 10). El ID 12 está dado de baja.
Las fechas de los préstamos se calculan a partir de la fecha del día, así que siempre hay préstamos vigentes y vencidos.

## Reglas de negocio implementadas

| Regla | Dónde se controla |
| --- | --- |
| RN-01 Préstamos solo a socios activos | `PrestamoService.validarSocio` |
| RN-02 Solo libros activos y disponibles | `PrestamoService.registrarPrestamo` e índice único parcial en `prestamo` |
| RN-03 Máximo 3 préstamos sin devolver | `PrestamoService.validarSocio` |
| RN-04 Cada préstamo, un socio y un ejemplar | Tabla `prestamo` y clase `Prestamo` |
| RN-05 Plazo de 7 días corridos | `Prestamo.PLAZO_DIAS` |
| RN-06 Con préstamos vencidos no hay nuevos préstamos | `PrestamoService.validarSocio` |
| RN-07 Definición de préstamo vencido | `Prestamo.estaVencido` y `PrestamoDAO.listarVencidos` |
| RN-08 Devolución una sola vez | `PrestamoService.registrarDevolucion` y `PrestamoDAO.registrarDevolucion` |
| RN-09 Sin bajas con préstamos sin devolver | `SocioService.darDeBaja` y `LibroService.darDeBaja` |
| RN-10 Bajas lógicas | Campo `activo` en `socio` y `libro` |
| RN-11 DNI y número de socio únicos | `SocioService.registrar` y restricciones `UNIQUE` |
| RN-12 Fecha actual del sistema | `PrestamoService.hoy()` |

## Estructura del proyecto

```
sql/                               Scripts de PostgreSQL
src/main/java/biblioteca/
├── presentacion/                  Menús por perfil y lectura de datos (Main, MenuPrincipal, MenuSocio,
│                                  MenuBibliotecario, MenuAdministrador, Consola, Vistas)
├── negocio/                       Reglas de negocio (SocioService, LibroService, PrestamoService,
│                                  Comprobante, NegocioException)
├── datos/                         Acceso a PostgreSQL con JDBC (ConexionBD, SocioDAO, LibroDAO, PrestamoDAO)
└── modelo/                        Entidades (Socio, Libro, Prestamo)
src/main/resources/config.properties   Datos de conexión
src/test/java/                     Pruebas unitarias del modelo
```

-- 02_crear_tablas.sql
-- Crea las tablas. Ejecutar conectado a la base "biblioteca".
DROP TABLE IF EXISTS prestamo;
DROP TABLE IF EXISTS libro;
DROP TABLE IF EXISTS socio;

CREATE TABLE socio (
    id            SERIAL PRIMARY KEY,
    numero_socio  INTEGER     NOT NULL UNIQUE,          -- RN-11
    dni           VARCHAR(10) NOT NULL UNIQUE,          -- RN-11
    nombre        VARCHAR(50) NOT NULL,
    apellido      VARCHAR(50) NOT NULL,
    activo        BOOLEAN     NOT NULL DEFAULT TRUE,    -- RN-10 (baja lógica)
    CONSTRAINT ck_socio_numero CHECK (numero_socio > 0),
    CONSTRAINT ck_socio_dni CHECK (dni ~ '^[0-9]{7,8}$')
);

-- Cada registro de libro representa un ejemplar físico.
CREATE TABLE libro (
    id                SERIAL PRIMARY KEY,
    titulo            VARCHAR(150) NOT NULL,
    autor             VARCHAR(100) NOT NULL,
    editorial         VARCHAR(100),
    anio_publicacion  INTEGER,
    activo            BOOLEAN NOT NULL DEFAULT TRUE     -- RN-10 (baja lógica)
);

CREATE TABLE prestamo (
    id                 SERIAL PRIMARY KEY,
    socio_id           INTEGER NOT NULL REFERENCES socio(id),
    libro_id           INTEGER NOT NULL REFERENCES libro(id),   -- RN-04: un único ejemplar
    fecha_prestamo     DATE    NOT NULL,
    fecha_vencimiento  DATE    NOT NULL,
    fecha_devolucion   DATE,                                    -- NULL = sin devolver
    CONSTRAINT ck_prestamo_fechas CHECK (fecha_vencimiento >= fecha_prestamo)
);

-- Un ejemplar no puede tener dos préstamos sin devolver a la vez (RN-02).
CREATE UNIQUE INDEX ux_prestamo_libro_abierto ON prestamo(libro_id) WHERE fecha_devolucion IS NULL;
CREATE INDEX ix_prestamo_socio ON prestamo(socio_id);

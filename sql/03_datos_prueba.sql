-- 03_datos_prueba.sql
-- Carga datos ficticios. Ejecutar conectado a la base "biblioteca", después de 02_crear_tablas.sql.
-- Las fechas se calculan a partir de la fecha actual, para que siempre haya préstamos vigentes y vencidos.

INSERT INTO socio (numero_socio, dni, nombre, apellido, activo) VALUES
(1001, '30123456', 'Juan',    'Pérez',     TRUE),   -- id 1: tiene 1 préstamo vigente
(1002, '31555444', 'Ana',     'Gómez',     TRUE),   -- id 2: tiene 1 préstamo vencido
(1003, '32777888', 'Lucía',   'Fernández', TRUE),   -- id 3: tiene 3 préstamos sin devolver (límite)
(1004, '33999000', 'Pedro',   'Ruiz',      FALSE),  -- id 4: socio inactivo
(1005, '34111222', 'Martina', 'López',     TRUE),   -- id 5: sin préstamos pendientes, con historial
(1006, '35333444', 'Carlos',  'Díaz',      TRUE);   -- id 6: sin préstamos

INSERT INTO libro (titulo, autor, editorial, anio_publicacion, activo) VALUES
('Ficciones',                 'Jorge Luis Borges',      'Emecé',        1944, TRUE),   -- id 1
('Ficciones',                 'Jorge Luis Borges',      'Emecé',        1944, TRUE),   -- id 2 (segundo ejemplar)
('El Aleph',                  'Jorge Luis Borges',      'Emecé',        1949, TRUE),   -- id 3
('Rayuela',                   'Julio Cortázar',         'Sudamericana', 1963, TRUE),   -- id 4
('Bestiario',                 'Julio Cortázar',         'Sudamericana', 1951, TRUE),   -- id 5
('Martín Fierro',             'José Hernández',         'Kapelusz',     1872, TRUE),   -- id 6
('El túnel',                  'Ernesto Sabato',         'Seix Barral',  1948, TRUE),   -- id 7
('Sobre héroes y tumbas',     'Ernesto Sabato',         'Seix Barral',  1961, TRUE),   -- id 8
('Cien años de soledad',      'Gabriel García Márquez', 'Sudamericana', 1967, TRUE),   -- id 9
('Cien años de soledad',      'Gabriel García Márquez', 'Sudamericana', 1967, TRUE),   -- id 10 (segundo ejemplar)
('La invención de Morel',     'Adolfo Bioy Casares',    'Losada',       1940, TRUE),   -- id 11
('Don Segundo Sombra',        'Ricardo Güiraldes',      'Losada',       1926, FALSE);  -- id 12: dado de baja

INSERT INTO prestamo (socio_id, libro_id, fecha_prestamo, fecha_vencimiento, fecha_devolucion) VALUES
-- Historial (devueltos)
(5, 4, CURRENT_DATE - 40, CURRENT_DATE - 33, CURRENT_DATE - 35),
(5, 7, CURRENT_DATE - 30, CURRENT_DATE - 23, CURRENT_DATE - 20),   -- devuelto con atraso
(1, 9, CURRENT_DATE - 25, CURRENT_DATE - 18, CURRENT_DATE - 19),
(1, 12, CURRENT_DATE - 60, CURRENT_DATE - 53, CURRENT_DATE - 55),  -- historial del libro dado de baja
-- Vigentes
(1, 1, CURRENT_DATE - 2,  CURRENT_DATE + 5,  NULL),
(3, 3, CURRENT_DATE - 1,  CURRENT_DATE + 6,  NULL),
(3, 5, CURRENT_DATE - 1,  CURRENT_DATE + 6,  NULL),
(3, 8, CURRENT_DATE,      CURRENT_DATE + 7,  NULL),
-- Vencido
(2, 6, CURRENT_DATE - 12, CURRENT_DATE - 5,  NULL);

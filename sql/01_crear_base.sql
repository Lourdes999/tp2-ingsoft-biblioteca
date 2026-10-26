-- 01_crear_base.sql
-- Crea la base de datos del sistema.
-- Ejecutar conectado a la base "postgres" (por ejemplo, desde el Query Tool de pgAdmin o con psql).
-- Si la base "biblioteca" ya existe y se quiere recrear, primero ejecutar SOLO esta línea:
--     DROP DATABASE IF EXISTS biblioteca;
-- (pgAdmin no permite ejecutar DROP DATABASE y CREATE DATABASE juntos).
CREATE DATABASE biblioteca ENCODING 'UTF8';
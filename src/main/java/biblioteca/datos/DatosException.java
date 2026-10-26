package biblioteca.datos;

/**
 * Error al acceder a la base de datos. Envuelve la SQLException para que
 * las capas superiores no dependan de JDBC.
 */
public class DatosException extends RuntimeException {
    public DatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

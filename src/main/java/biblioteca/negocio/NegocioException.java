package biblioteca.negocio;

/**
 * Error de validación o de regla de negocio. La capa de presentación
 * muestra su mensaje en consola sin finalizar el programa.
 */
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) {
        super(mensaje);
    }
}

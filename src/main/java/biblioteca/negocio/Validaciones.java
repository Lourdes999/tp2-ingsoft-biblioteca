package biblioteca.negocio;

/**
 * Validaciones de datos obligatorios y de formato usadas por los servicios.
 */
final class Validaciones {

    private Validaciones() {
    }

    static String obligatorio(String valor, String campo, int largoMaximo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new NegocioException("El campo " + campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > largoMaximo) {
            throw new NegocioException("El campo " + campo + " admite como máximo " + largoMaximo + " caracteres");
        }
        return limpio;
    }

    static String dni(String valor) {
        String dni = obligatorio(valor, "DNI", 10);
        if (!dni.matches("\\d{7,8}")) {
            throw new NegocioException("El DNI debe tener 7 u 8 dígitos numéricos");
        }
        return dni;
    }
}

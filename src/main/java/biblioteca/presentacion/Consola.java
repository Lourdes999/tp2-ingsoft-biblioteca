package biblioteca.presentacion;

import java.util.Scanner;

/**
 * Lectura de datos por consola. Repite la pregunta ante entradas inválidas
 * para que el programa nunca finalice inesperadamente.
 */
public class Consola {
    private final Scanner scanner;

    public Consola(Scanner scanner) {
        this.scanner = scanner;
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje + ": ");
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    /** Devuelve null si el usuario deja el campo vacío (para conservar el valor actual). */
    public String leerTextoOpcional(String mensaje) {
        String texto = leerTexto(mensaje + " (Enter para no modificar)");
        return texto.isEmpty() ? null : texto;
    }

    public int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (texto.isEmpty() && !scanner.hasNextLine()) {
                return 0; // fin de la entrada: se interpreta como "Salir/Volver"
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("  Debe ingresar un número entero.");
            }
        }
    }

    public Integer leerEnteroOpcional(String mensaje) {
        while (true) {
            String texto = leerTextoOpcional(mensaje);
            if (texto == null) {
                return null;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("  Debe ingresar un número entero.");
            }
        }
    }

    public boolean confirmar(String mensaje) {
        String r = leerTexto(mensaje + " (S/N)");
        return r.equalsIgnoreCase("s") || r.equalsIgnoreCase("si") || r.equalsIgnoreCase("sí");
    }

    public void titulo(String texto) {
        System.out.println();
        System.out.println("---------- " + texto + " ----------");
    }

    public void exito(String texto) {
        System.out.println("OK: " + texto);
    }

    public void error(String texto) {
        System.out.println("ERROR: " + texto);
    }
}

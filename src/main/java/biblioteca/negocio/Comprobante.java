package biblioteca.negocio;

import biblioteca.modelo.Prestamo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Caso de uso incluido "Emitir comprobante": arma el texto de los comprobantes
 * de préstamo y devolución que la presentación muestra en consola.
 */
public final class Comprobante {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String LINEA = "==================================================";

    private Comprobante() {
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FORMATO);
    }

    public static String dePrestamo(Prestamo p) {
        return LINEA + "\n"
                + "             COMPROBANTE DE PRÉSTAMO\n"
                + LINEA + "\n"
                + fila("Préstamo N°", String.valueOf(p.getId()))
                + fila("Socio", "N° " + p.getSocio().getNumeroSocio() + " - " + p.getSocio().getNombreCompleto())
                + fila("Libro", "ID " + p.getLibro().getId() + " - " + p.getLibro().getTitulo())
                + fila("Autor", p.getLibro().getAutor())
                + fila("Fecha de préstamo", fecha(p.getFechaPrestamo()))
                + fila("Fecha de vencimiento", fecha(p.getFechaVencimiento()) + " (" + Prestamo.PLAZO_DIAS + " días)")
                + LINEA;
    }

    public static String deDevolucion(Prestamo p) {
        int atraso = p.diasDeAtraso(p.getFechaDevolucion());
        return LINEA + "\n"
                + "            COMPROBANTE DE DEVOLUCIÓN\n"
                + LINEA + "\n"
                + fila("Préstamo N°", String.valueOf(p.getId()))
                + fila("Socio", "N° " + p.getSocio().getNumeroSocio() + " - " + p.getSocio().getNombreCompleto())
                + fila("Libro", "ID " + p.getLibro().getId() + " - " + p.getLibro().getTitulo())
                + fila("Fecha de préstamo", fecha(p.getFechaPrestamo()))
                + fila("Fecha de vencimiento", fecha(p.getFechaVencimiento()))
                + fila("Fecha de devolución", fecha(p.getFechaDevolucion()))
                + fila("Atraso", atraso == 0 ? "sin atraso" : atraso + " día(s)")
                + " El ejemplar queda disponible nuevamente.\n"
                + LINEA;
    }

    private static String fila(String etiqueta, String valor) {
        return String.format(" %-22s%s%n", etiqueta + ":", valor);
    }
}

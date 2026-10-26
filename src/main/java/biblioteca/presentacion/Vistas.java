package biblioteca.presentacion;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.negocio.Comprobante;
import biblioteca.negocio.LibroService;

import java.time.LocalDate;
import java.util.List;

/**
 * Formatos de listados en consola, compartidos por los menús de cada perfil.
 */
final class Vistas {

    private Vistas() {
    }

    static void libros(List<Libro> libros, LibroService libroService) {
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros.");
            return;
        }
        System.out.printf("%-5s %-28s %-24s %-14s %-6s %s%n", "ID", "Título", "Autor", "Editorial", "Año", "Disponibilidad");
        for (Libro l : libros) {
            System.out.printf("%-5d %-28s %-24s %-14s %-6d %s%n", l.getId(), corto(l.getTitulo(), 28),
                    corto(l.getAutor(), 24), corto(l.getEditorial(), 14), l.getAnioPublicacion(),
                    libroService.estaDisponible(l) ? "Disponible" : "Prestado");
        }
    }

    static void libro(Libro l, LibroService libroService) {
        System.out.println(l + " | " + (l.estaActivo() ? "Activo" : "Dado de baja")
                + " | " + (libroService.estaDisponible(l) ? "Disponible" : "No disponible"));
    }

    static void prestamos(List<Prestamo> prestamos, LocalDate hoy, boolean mostrarSocio) {
        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos para mostrar.");
            return;
        }
        if (mostrarSocio) {
            System.out.printf("%-6s %-24s %-26s %-11s %-11s %-11s %s%n",
                    "N°", "Socio", "Libro", "Préstamo", "Vence", "Devolución", "Estado");
        } else {
            System.out.printf("%-6s %-26s %-11s %-11s %-11s %s%n",
                    "N°", "Libro", "Préstamo", "Vence", "Devolución", "Estado");
        }
        for (Prestamo p : prestamos) {
            String estado = p.estadoTexto(hoy);
            if (p.estaVencido(hoy)) {
                estado += " (" + p.diasDeAtraso(hoy) + " días)";
            }
            String libro = corto("ID " + p.getLibro().getId() + " " + p.getLibro().getTitulo(), 26);
            if (mostrarSocio) {
                String socio = corto(p.getSocio().getNumeroSocio() + " " + p.getSocio().getNombreCompleto(), 24);
                System.out.printf("%-6d %-24s %-26s %-11s %-11s %-11s %s%n", p.getId(), socio, libro,
                        Comprobante.fecha(p.getFechaPrestamo()), Comprobante.fecha(p.getFechaVencimiento()),
                        Comprobante.fecha(p.getFechaDevolucion()), estado);
            } else {
                System.out.printf("%-6d %-26s %-11s %-11s %-11s %s%n", p.getId(), libro,
                        Comprobante.fecha(p.getFechaPrestamo()), Comprobante.fecha(p.getFechaVencimiento()),
                        Comprobante.fecha(p.getFechaDevolucion()), estado);
            }
        }
    }

    static void socios(List<Socio> socios) {
        if (socios.isEmpty()) {
            System.out.println("No hay socios registrados.");
            return;
        }
        for (Socio s : socios) {
            System.out.println(s);
        }
    }

    private static String corto(String texto, int max) {
        if (texto == null) return "";
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + "…";
    }
}

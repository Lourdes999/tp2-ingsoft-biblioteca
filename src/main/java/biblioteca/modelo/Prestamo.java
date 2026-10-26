package biblioteca.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Préstamo de un único ejemplar a un único socio (RN-04).
 */
public class Prestamo {
    /** RN-05: el plazo de préstamo es de 7 días corridos. */
    public static final int PLAZO_DIAS = 7;

    private int id;
    private Socio socio;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion;

    /** Crea un préstamo nuevo: el vencimiento se calcula a 7 días de la fecha de préstamo. */
    public Prestamo(Socio socio, Libro libro, LocalDate fechaPrestamo) {
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaPrestamo.plusDays(PLAZO_DIAS);
    }

    /** Reconstruye un préstamo leído de la base de datos. */
    public Prestamo(int id, Socio socio, Libro libro, LocalDate fechaPrestamo,
                    LocalDate fechaVencimiento, LocalDate fechaDevolucion) {
        this.id = id;
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean estaDevuelto() {
        return fechaDevolucion != null;
    }

    /** RN-07: vencido = superó la fecha de vencimiento y no tiene devolución registrada. */
    public boolean estaVencido(LocalDate hoy) {
        return !estaDevuelto() && hoy.isAfter(fechaVencimiento);
    }

    public void registrarDevolucion(LocalDate fecha) {
        if (estaDevuelto()) {
            throw new IllegalStateException("La devolución ya fue registrada");
        }
        this.fechaDevolucion = fecha;
    }

    /** Días de atraso respecto del vencimiento (0 si no hay atraso). */
    public int diasDeAtraso(LocalDate hoy) {
        LocalDate referencia = estaDevuelto() ? fechaDevolucion : hoy;
        long dias = ChronoUnit.DAYS.between(fechaVencimiento, referencia);
        return (int) Math.max(0, dias);
    }

    public String estadoTexto(LocalDate hoy) {
        if (estaDevuelto()) return "Devuelto";
        return estaVencido(hoy) ? "Vencido" : "Vigente";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Socio getSocio() { return socio; }
    public Libro getLibro() { return libro; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
}

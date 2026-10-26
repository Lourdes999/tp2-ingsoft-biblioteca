package biblioteca.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PrestamoTest {
    private Prestamo prestamo;
    private final LocalDate inicio = LocalDate.of(2026, 10, 20);

    @BeforeEach
    void setUp() {
        Socio socio = new Socio(1, 1001, "30123456", "Juan", "Pérez", true);
        Libro libro = new Libro(1, "Ficciones", "Jorge Luis Borges", "Emecé", 1944, true);
        prestamo = new Prestamo(socio, libro, inicio);
    }

    @Test
    void elVencimientoEsASieteDias() {                       // RN-05
        assertEquals(LocalDate.of(2026, 10, 27), prestamo.getFechaVencimiento());
    }

    @Test
    void noEstaVencidoElDiaDelVencimiento() {                // caso límite RN-07
        assertFalse(prestamo.estaVencido(LocalDate.of(2026, 10, 27)));
    }

    @Test
    void estaVencidoAlDiaSiguienteDelVencimiento() {         // RN-07
        assertTrue(prestamo.estaVencido(LocalDate.of(2026, 10, 28)));
        assertEquals(1, prestamo.diasDeAtraso(LocalDate.of(2026, 10, 28)));
    }

    @Test
    void unPrestamoDevueltoNoEstaVencido() {                 // RN-07
        prestamo.registrarDevolucion(LocalDate.of(2026, 10, 30));
        assertFalse(prestamo.estaVencido(LocalDate.of(2026, 11, 15)));
        assertEquals(3, prestamo.diasDeAtraso(LocalDate.of(2026, 11, 15)));
    }

    @Test
    void laDevolucionSeRegistraUnaSolaVez() {                // RN-08
        prestamo.registrarDevolucion(LocalDate.of(2026, 10, 25));
        assertThrows(IllegalStateException.class, () -> prestamo.registrarDevolucion(LocalDate.of(2026, 10, 26)));
    }
}

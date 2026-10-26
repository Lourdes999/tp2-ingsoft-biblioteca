package biblioteca.negocio;

import biblioteca.datos.LibroDAO;
import biblioteca.datos.PrestamoDAO;
import biblioteca.datos.SocioDAO;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Reglas de negocio de préstamos y devoluciones.
 */
public class PrestamoService {
    public static final int MAXIMO_SIN_DEVOLVER = 3;

    private final SocioDAO socioDAO;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final Clock reloj;

    public PrestamoService(SocioDAO socioDAO, LibroDAO libroDAO, PrestamoDAO prestamoDAO, Clock reloj) {
        this.socioDAO = socioDAO;
        this.libroDAO = libroDAO;
        this.prestamoDAO = prestamoDAO;
        this.reloj = reloj;
    }

    /** RN-12: la fecha de las operaciones es la fecha actual del sistema. */
    public LocalDate hoy() {
        return LocalDate.now(reloj);
    }

    /**
     * Caso de uso incluido "Validar condición del socio": RN-01, RN-06 y RN-03.
     * Si el socio tiene préstamos vencidos, el mensaje los informa (relación extend).
     */
    public Socio validarSocio(int numeroSocio) {
        Socio socio = socioDAO.buscarPorNumero(numeroSocio);
        if (socio == null) {
            throw new NegocioException("Socio inexistente: no hay un socio con el número " + numeroSocio);
        }
        if (!socio.estaActivo()) {
            throw new NegocioException("El socio " + socio.getNombreCompleto() + " está inactivo");   // RN-01
        }
        List<Prestamo> vencidos = prestamoDAO.listarVencidosDeSocio(socio.getId(), hoy());
        if (!vencidos.isEmpty()) {                                                                  // RN-06
            StringBuilder msg = new StringBuilder("El socio tiene préstamos vencidos y no puede retirar libros "
                    + "hasta regularizar su situación:");
            for (Prestamo p : vencidos) {
                msg.append("\n   - Préstamo N° ").append(p.getId()).append(": ").append(p.getLibro().getTitulo())
                   .append(" (venció el ").append(Comprobante.fecha(p.getFechaVencimiento())).append(")");
            }
            throw new NegocioException(msg.toString());
        }
        int sinDevolver = prestamoDAO.contarSinDevolver(socio.getId());
        if (sinDevolver >= MAXIMO_SIN_DEVOLVER) {                                                   // RN-03
            throw new NegocioException("El socio alcanzó el límite de " + MAXIMO_SIN_DEVOLVER
                    + " préstamos sin devolver");
        }
        return socio;
    }

    /** CU-10: registra un préstamo con fecha actual y vencimiento a 7 días (RN-05, RN-12). */
    public Prestamo registrarPrestamo(int numeroSocio, int libroId) {
        Socio socio = validarSocio(numeroSocio);
        Libro libro = libroDAO.buscarPorId(libroId);
        if (libro == null || !libro.estaActivo()) {                                                 // RN-02
            throw new NegocioException("Libro inexistente o dado de baja (ID " + libroId + ")");
        }
        if (!libroDAO.estaDisponible(libroId)) {                                                    // RN-02
            throw new NegocioException("El libro \"" + libro.getTitulo() + "\" (ID " + libroId
                    + ") no está disponible: tiene un préstamo sin devolver");
        }
        Prestamo prestamo = new Prestamo(socio, libro, hoy());
        prestamoDAO.insertar(prestamo);
        return prestamo;
    }

    /** CU-11: registra la devolución una sola vez (RN-08) con la fecha actual (RN-12). */
    public Prestamo registrarDevolucion(int prestamoId) {
        Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);
        if (prestamo == null) {
            throw new NegocioException("Préstamo inexistente: no hay un préstamo con el número " + prestamoId);
        }
        if (prestamo.estaDevuelto()) {
            throw new NegocioException("La devolución de este préstamo ya fue registrada el "
                    + Comprobante.fecha(prestamo.getFechaDevolucion()));
        }
        LocalDate fecha = hoy();
        if (!prestamoDAO.registrarDevolucion(prestamoId, fecha)) {
            throw new NegocioException("La devolución de este préstamo ya fue registrada");
        }
        prestamo.registrarDevolucion(fecha);
        return prestamo;
    }

    /** RF-08: préstamos del socio que ingresó con su perfil. */
    public List<Prestamo> prestamosDeSocio(int numeroSocio) {
        Socio socio = socioDAO.buscarPorNumero(numeroSocio);
        if (socio == null) {
            throw new NegocioException("Socio inexistente: no hay un socio con el número " + numeroSocio);
        }
        return prestamoDAO.listarPorSocio(socio.getId());
    }

    /** RF-07 */
    public List<Prestamo> listarVigentes() {
        return prestamoDAO.listarVigentes(hoy());
    }

    /** RF-07 */
    public List<Prestamo> listarVencidos() {
        return prestamoDAO.listarVencidos(hoy());
    }
}

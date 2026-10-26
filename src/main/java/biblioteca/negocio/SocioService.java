package biblioteca.negocio;

import biblioteca.datos.PrestamoDAO;
import biblioteca.datos.SocioDAO;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;

import java.util.List;

/**
 * Reglas de negocio de socios.
 */
public class SocioService {
    private final SocioDAO socioDAO;
    private final PrestamoDAO prestamoDAO;

    public SocioService(SocioDAO socioDAO, PrestamoDAO prestamoDAO) {
        this.socioDAO = socioDAO;
        this.prestamoDAO = prestamoDAO;
    }

    /** RF-01: registra un socio validando los datos obligatorios y evitando duplicados (RN-11). */
    public Socio registrar(int numeroSocio, String dni, String nombre, String apellido) {
        if (numeroSocio <= 0) {
            throw new NegocioException("El número de socio debe ser mayor que cero");
        }
        dni = Validaciones.dni(dni);
        nombre = Validaciones.obligatorio(nombre, "nombre", 50);
        apellido = Validaciones.obligatorio(apellido, "apellido", 50);
        if (socioDAO.buscarPorNumero(numeroSocio) != null) {
            throw new NegocioException("Ya existe un socio con el número " + numeroSocio);
        }
        if (socioDAO.buscarPorDni(dni) != null) {
            throw new NegocioException("Ya existe un socio con el DNI " + dni);
        }
        Socio socio = new Socio(numeroSocio, dni, nombre, apellido);
        socioDAO.insertar(socio);
        return socio;
    }

    /** RF-02: modifica DNI, nombre o apellido. Un valor vacío (null) conserva el dato actual. */
    public Socio modificar(int numeroSocio, String nuevoDni, String nuevoNombre, String nuevoApellido) {
        Socio socio = consultar(numeroSocio);
        if (nuevoDni != null) {
            String dni = Validaciones.dni(nuevoDni);
            Socio otro = socioDAO.buscarPorDni(dni);
            if (otro != null && otro.getId() != socio.getId()) {
                throw new NegocioException("Ya existe otro socio con el DNI " + dni);
            }
            socio.setDni(dni);
        }
        if (nuevoNombre != null) {
            socio.setNombre(Validaciones.obligatorio(nuevoNombre, "nombre", 50));
        }
        if (nuevoApellido != null) {
            socio.setApellido(Validaciones.obligatorio(nuevoApellido, "apellido", 50));
        }
        socioDAO.actualizar(socio);
        return socio;
    }

    public Socio consultar(int numeroSocio) {
        Socio socio = socioDAO.buscarPorNumero(numeroSocio);
        if (socio == null) {
            throw new NegocioException("Socio inexistente: no hay un socio con el número " + numeroSocio);
        }
        return socio;
    }

    /** Baja lógica (RN-10). No se permite si el socio tiene préstamos sin devolver (RN-09). */
    public Socio darDeBaja(int numeroSocio) {
        Socio socio = consultar(numeroSocio);
        if (!socio.estaActivo()) {
            throw new NegocioException("El socio ya está dado de baja");
        }
        int pendientes = prestamoDAO.contarSinDevolver(socio.getId());
        if (pendientes > 0) {
            throw new NegocioException("No se puede dar de baja: el socio tiene " + pendientes
                    + " préstamo(s) sin devolver");
        }
        socio.darDeBaja();
        socioDAO.actualizar(socio);
        return socio;
    }

    public List<Socio> listar() {
        return socioDAO.listarTodos();
    }

    /** RF-06: historial de préstamos del socio. */
    public List<Prestamo> historial(int numeroSocio) {
        Socio socio = consultar(numeroSocio);
        return prestamoDAO.listarPorSocio(socio.getId());
    }
}

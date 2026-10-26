package biblioteca.negocio;

import biblioteca.datos.LibroDAO;
import biblioteca.datos.PrestamoDAO;
import biblioteca.modelo.Libro;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Reglas de negocio de libros (ejemplares).
 */
public class LibroService {
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final Clock reloj;

    public LibroService(LibroDAO libroDAO, PrestamoDAO prestamoDAO, Clock reloj) {
        this.libroDAO = libroDAO;
        this.prestamoDAO = prestamoDAO;
        this.reloj = reloj;
    }

    /** RF-03: registra un nuevo ejemplar. */
    public Libro registrar(String titulo, String autor, String editorial, int anio) {
        titulo = Validaciones.obligatorio(titulo, "título", 150);
        autor = Validaciones.obligatorio(autor, "autor", 100);
        editorial = editorial == null || editorial.trim().isEmpty() ? "-" : editorial.trim();
        validarAnio(anio);
        Libro libro = new Libro(titulo, autor, editorial, anio);
        libroDAO.insertar(libro);
        return libro;
    }

    /** RF-03: modifica los datos de un ejemplar. Un valor null conserva el dato actual. */
    public Libro modificar(int id, String titulo, String autor, String editorial, Integer anio) {
        Libro libro = consultar(id);
        if (titulo != null) libro.setTitulo(Validaciones.obligatorio(titulo, "título", 150));
        if (autor != null) libro.setAutor(Validaciones.obligatorio(autor, "autor", 100));
        if (editorial != null) libro.setEditorial(Validaciones.obligatorio(editorial, "editorial", 100));
        if (anio != null) {
            validarAnio(anio);
            libro.setAnioPublicacion(anio);
        }
        libroDAO.actualizar(libro);
        return libro;
    }

    public Libro consultar(int id) {
        Libro libro = libroDAO.buscarPorId(id);
        if (libro == null) {
            throw new NegocioException("Libro inexistente: no hay un ejemplar con el ID " + id);
        }
        return libro;
    }

    /** Baja lógica (RN-10). No se permite si el ejemplar tiene un préstamo sin devolver (RN-09). */
    public Libro darDeBaja(int id) {
        Libro libro = consultar(id);
        if (!libro.estaActivo()) {
            throw new NegocioException("El libro ya está dado de baja");
        }
        if (prestamoDAO.contarSinDevolverPorLibro(id) > 0) {
            throw new NegocioException("No se puede dar de baja: el libro tiene un préstamo sin devolver");
        }
        libro.darDeBaja();
        libroDAO.actualizar(libro);
        return libro;
    }

    /** RF-04: búsqueda por título. */
    public List<Libro> buscarPorTitulo(String texto) {
        return libroDAO.buscarPorTitulo(Validaciones.obligatorio(texto, "texto a buscar", 150));
    }

    /** RF-04: búsqueda por autor. */
    public List<Libro> buscarPorAutor(String texto) {
        return libroDAO.buscarPorAutor(Validaciones.obligatorio(texto, "texto a buscar", 100));
    }

    /** Un ejemplar activo está disponible cuando no tiene un préstamo sin devolver. */
    public boolean estaDisponible(Libro libro) {
        return libro.estaActivo() && libroDAO.estaDisponible(libro.getId());
    }

    private void validarAnio(int anio) {
        int actual = LocalDate.now(reloj).getYear();
        if (anio < 1 || anio > actual) {
            throw new NegocioException("Año de publicación inválido: debe estar entre 1 y " + actual);
        }
    }
}

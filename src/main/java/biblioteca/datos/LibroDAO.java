package biblioteca.datos;

import biblioteca.modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla libro.
 */
public class LibroDAO {

    public void insertar(Libro l) {
        String sql = "INSERT INTO libro (titulo, autor, editorial, anio_publicacion, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getEditorial());
            ps.setInt(4, l.getAnioPublicacion());
            ps.setBoolean(5, l.estaActivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    l.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al guardar el libro", e);
        }
    }

    public void actualizar(Libro l) {
        String sql = "UPDATE libro SET titulo = ?, autor = ?, editorial = ?, anio_publicacion = ?, activo = ? WHERE id = ?";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getEditorial());
            ps.setInt(4, l.getAnioPublicacion());
            ps.setBoolean(5, l.estaActivo());
            ps.setInt(6, l.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el libro", e);
        }
    }

    public Libro buscarPorId(int id) {
        String sql = "SELECT * FROM libro WHERE id = ?";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar el libro", e);
        }
    }

    /** Busca ejemplares activos cuyo título contiene el texto (sin distinguir mayúsculas). */
    public List<Libro> buscarPorTitulo(String texto) {
        return buscarActivos("titulo", texto);
    }

    /** Busca ejemplares activos cuyo autor contiene el texto (sin distinguir mayúsculas). */
    public List<Libro> buscarPorAutor(String texto) {
        return buscarActivos("autor", texto);
    }

    /** Un ejemplar está disponible si no tiene un préstamo sin devolver. */
    public boolean estaDisponible(int libroId) {
        String sql = "SELECT COUNT(*) FROM prestamo WHERE libro_id = ? AND fecha_devolucion IS NULL";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al consultar la disponibilidad del libro", e);
        }
    }

    private List<Libro> buscarActivos(String columna, String texto) {
        List<Libro> lista = new ArrayList<>();
        // La columna es fija (titulo o autor), el texto se pasa como parámetro.
        String sql = "SELECT * FROM libro WHERE activo = TRUE AND " + columna + " ILIKE ? ORDER BY titulo, id";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar libros", e);
        }
        return lista;
    }

    static Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"),
                rs.getString("editorial"), rs.getInt("anio_publicacion"), rs.getBoolean("activo"));
    }
}

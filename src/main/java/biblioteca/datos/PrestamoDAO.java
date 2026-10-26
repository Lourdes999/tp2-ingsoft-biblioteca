package biblioteca.datos;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla prestamo.
 */
public class PrestamoDAO {

    private static final String SELECT_BASE =
            "SELECT p.id, p.fecha_prestamo, p.fecha_vencimiento, p.fecha_devolucion, "
            + "s.id AS s_id, s.numero_socio, s.dni, s.nombre, s.apellido, s.activo AS s_activo, "
            + "l.id AS l_id, l.titulo, l.autor, l.editorial, l.anio_publicacion, l.activo AS l_activo "
            + "FROM prestamo p JOIN socio s ON s.id = p.socio_id JOIN libro l ON l.id = p.libro_id ";

    public void insertar(Prestamo p) {
        String sql = "INSERT INTO prestamo (socio_id, libro_id, fecha_prestamo, fecha_vencimiento) VALUES (?, ?, ?, ?)";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getSocio().getId());
            ps.setInt(2, p.getLibro().getId());
            ps.setDate(3, Date.valueOf(p.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(p.getFechaVencimiento()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al guardar el préstamo", e);
        }
    }

    /**
     * Registra la devolución solo si el préstamo todavía no tiene una (RN-08).
     * Devuelve false si otro proceso ya la había registrado.
     */
    public boolean registrarDevolucion(int prestamoId, LocalDate fecha) {
        String sql = "UPDATE prestamo SET fecha_devolucion = ? WHERE id = ? AND fecha_devolucion IS NULL";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setInt(2, prestamoId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DatosException("Error al registrar la devolución", e);
        }
    }

    public Prestamo buscarPorId(int id) {
        List<Prestamo> lista = consultar(SELECT_BASE + "WHERE p.id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Historial completo de préstamos de un socio, del más reciente al más antiguo. */
    public List<Prestamo> listarPorSocio(int socioId) {
        return consultar(SELECT_BASE + "WHERE p.socio_id = ? ORDER BY p.fecha_prestamo DESC, p.id DESC", socioId);
    }

    public int contarSinDevolver(int socioId) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE socio_id = ? AND fecha_devolucion IS NULL", socioId, null);
    }

    public int contarVencidos(int socioId, LocalDate hoy) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE socio_id = ? AND fecha_devolucion IS NULL AND fecha_vencimiento < ?",
                socioId, hoy);
    }

    public int contarSinDevolverPorLibro(int libroId) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE libro_id = ? AND fecha_devolucion IS NULL", libroId, null);
    }

    /** Préstamos sin devolver cuyo vencimiento no pasó. */
    public List<Prestamo> listarVigentes(LocalDate hoy) {
        return consultarPorFecha(SELECT_BASE + "WHERE p.fecha_devolucion IS NULL AND p.fecha_vencimiento >= ? "
                + "ORDER BY p.fecha_vencimiento", hoy);
    }

    /** RN-07: préstamos sin devolver cuyo vencimiento ya pasó. */
    public List<Prestamo> listarVencidos(LocalDate hoy) {
        return consultarPorFecha(SELECT_BASE + "WHERE p.fecha_devolucion IS NULL AND p.fecha_vencimiento < ? "
                + "ORDER BY p.fecha_vencimiento", hoy);
    }

    public List<Prestamo> listarVencidosDeSocio(int socioId, LocalDate hoy) {
        List<Prestamo> lista = new ArrayList<>();
        for (Prestamo p : listarVencidos(hoy)) {
            if (p.getSocio().getId() == socioId) {
                lista.add(p);
            }
        }
        return lista;
    }

    private int contar(String sql, int id, LocalDate fecha) {
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (fecha != null) {
                ps.setDate(2, Date.valueOf(fecha));
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatosException("Error al consultar los préstamos", e);
        }
    }

    private List<Prestamo> consultar(String sql, int valor) {
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, valor);
            return leer(ps);
        } catch (SQLException e) {
            throw new DatosException("Error al consultar los préstamos", e);
        }
    }

    private List<Prestamo> consultarPorFecha(String sql, LocalDate fecha) {
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            return leer(ps);
        } catch (SQLException e) {
            throw new DatosException("Error al consultar los préstamos", e);
        }
    }

    private List<Prestamo> leer(PreparedStatement ps) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Socio s = new Socio(rs.getInt("s_id"), rs.getInt("numero_socio"), rs.getString("dni"),
                        rs.getString("nombre"), rs.getString("apellido"), rs.getBoolean("s_activo"));
                Libro l = new Libro(rs.getInt("l_id"), rs.getString("titulo"), rs.getString("autor"),
                        rs.getString("editorial"), rs.getInt("anio_publicacion"), rs.getBoolean("l_activo"));
                Date dev = rs.getDate("fecha_devolucion");
                lista.add(new Prestamo(rs.getInt("id"), s, l,
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_vencimiento").toLocalDate(),
                        dev == null ? null : dev.toLocalDate()));
            }
        }
        return lista;
    }
}

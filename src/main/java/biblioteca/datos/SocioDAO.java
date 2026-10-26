package biblioteca.datos;

import biblioteca.modelo.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla socio.
 */
public class SocioDAO {

    public void insertar(Socio s) {
        String sql = "INSERT INTO socio (numero_socio, dni, nombre, apellido, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getNumeroSocio());
            ps.setString(2, s.getDni());
            ps.setString(3, s.getNombre());
            ps.setString(4, s.getApellido());
            ps.setBoolean(5, s.estaActivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al guardar el socio", e);
        }
    }

    public void actualizar(Socio s) {
        String sql = "UPDATE socio SET dni = ?, nombre = ?, apellido = ?, activo = ? WHERE id = ?";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getDni());
            ps.setString(2, s.getNombre());
            ps.setString(3, s.getApellido());
            ps.setBoolean(4, s.estaActivo());
            ps.setInt(5, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el socio", e);
        }
    }

    public Socio buscarPorNumero(int numeroSocio) {
        return buscarUno("SELECT * FROM socio WHERE numero_socio = ?", numeroSocio);
    }

    public Socio buscarPorId(int id) {
        return buscarUno("SELECT * FROM socio WHERE id = ?", id);
    }

    public Socio buscarPorDni(String dni) {
        String sql = "SELECT * FROM socio WHERE dni = ?";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar el socio", e);
        }
    }

    public List<Socio> listarTodos() {
        List<Socio> lista = new ArrayList<>();
        String sql = "SELECT * FROM socio ORDER BY numero_socio";
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DatosException("Error al listar los socios", e);
        }
        return lista;
    }

    private Socio buscarUno(String sql, int valor) {
        try (Connection c = ConexionBD.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar el socio", e);
        }
    }

    static Socio mapear(ResultSet rs) throws SQLException {
        return new Socio(rs.getInt("id"), rs.getInt("numero_socio"), rs.getString("dni"),
                rs.getString("nombre"), rs.getString("apellido"), rs.getBoolean("activo"));
    }
}

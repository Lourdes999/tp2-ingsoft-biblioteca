package biblioteca.datos;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Obtiene conexiones a PostgreSQL con los datos de src/main/resources/config.properties.
 */
public class ConexionBD {
    private static final String url;
    private static final String usuario;
    private static final String password;

    static {
        Properties p = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            throw new DatosException("No se pudo leer config.properties", e);
        }
        url = p.getProperty("db.url", "jdbc:postgresql://localhost:5432/biblioteca");
        usuario = p.getProperty("db.usuario", "postgres");
        password = p.getProperty("db.password", "postgres");
    }

    private ConexionBD() {
    }

    public static Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            throw new DatosException("No se pudo conectar a la base de datos (" + url + "). "
                    + "Verifique que PostgreSQL esté iniciado y los datos de config.properties.", e);
        }
    }
}

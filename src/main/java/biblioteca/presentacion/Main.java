package biblioteca.presentacion;

import biblioteca.datos.ConexionBD;
import biblioteca.datos.LibroDAO;
import biblioteca.datos.PrestamoDAO;
import biblioteca.datos.SocioDAO;
import biblioteca.negocio.LibroService;
import biblioteca.negocio.PrestamoService;
import biblioteca.negocio.SocioService;

import java.sql.Connection;
import java.time.Clock;
import java.util.Scanner;

/**
 * Punto de entrada. Crea las capas y las conecta:
 * presentación -> lógica de negocio -> acceso a datos -> PostgreSQL.
 */
public class Main {
    public static void main(String[] args) {
        // Se verifica la conexión antes de mostrar los menús.
        try (Connection c = ConexionBD.obtenerConexion()) {
            // conexión correcta
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Detalle: " + e.getCause().getMessage());
            }
            System.out.println("Revise el README para configurar la base de datos.");
            return;
        }

        Clock reloj = Clock.systemDefaultZone();   // RN-12: fecha actual del sistema
        SocioDAO socioDAO = new SocioDAO();
        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();

        SocioService socioService = new SocioService(socioDAO, prestamoDAO);
        LibroService libroService = new LibroService(libroDAO, prestamoDAO, reloj);
        PrestamoService prestamoService = new PrestamoService(socioDAO, libroDAO, prestamoDAO, reloj);

        Consola consola = new Consola(new Scanner(System.in));
        MenuPrincipal menu = new MenuPrincipal(consola,
                new MenuSocio(consola, socioService, libroService, prestamoService),
                new MenuBibliotecario(consola, socioService, libroService, prestamoService),
                new MenuAdministrador(consola, socioService, libroService, prestamoService));
        menu.mostrar();
    }
}

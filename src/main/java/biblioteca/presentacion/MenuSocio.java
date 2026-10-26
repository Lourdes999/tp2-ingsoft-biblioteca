package biblioteca.presentacion;

import biblioteca.modelo.Socio;
import biblioteca.negocio.LibroService;
import biblioteca.negocio.PrestamoService;
import biblioteca.negocio.SocioService;

/**
 * Opciones del perfil Socio: consultar libros y sus propios préstamos (RF-08).
 */
public class MenuSocio {
    private final Consola consola;
    private final SocioService socioService;
    private final LibroService libroService;
    private final PrestamoService prestamoService;

    public MenuSocio(Consola consola, SocioService socioService, LibroService libroService,
                     PrestamoService prestamoService) {
        this.consola = consola;
        this.socioService = socioService;
        this.libroService = libroService;
        this.prestamoService = prestamoService;
    }

    public void mostrar() {
        // Simplificación del acceso: el socio se identifica con un número de socio existente.
        int numero = consola.leerEntero("Ingrese su número de socio");
        Socio socio;
        try {
            socio = socioService.consultar(numero);
        } catch (RuntimeException e) {
            consola.error(e.getMessage());
            return;
        }
        int opcion;
        do {
            consola.titulo("MENÚ SOCIO - " + socio.getNombreCompleto());
            System.out.println("1. Buscar libros por título");
            System.out.println("2. Buscar libros por autor");
            System.out.println("3. Consultar mis préstamos");
            System.out.println("0. Volver al menú principal");
            opcion = consola.leerEntero("Opción");
            try {
                switch (opcion) {
                    case 1 -> Vistas.libros(libroService.buscarPorTitulo(consola.leerTexto("Título a buscar")), libroService);
                    case 2 -> Vistas.libros(libroService.buscarPorAutor(consola.leerTexto("Autor a buscar")), libroService);
                    case 3 -> Vistas.prestamos(prestamoService.prestamosDeSocio(socio.getNumeroSocio()),
                            prestamoService.hoy(), false);
                    case 0 -> { }
                    default -> consola.error("Opción inválida");
                }
            } catch (RuntimeException e) {
                consola.error(e.getMessage());
            }
        } while (opcion != 0);
    }
}

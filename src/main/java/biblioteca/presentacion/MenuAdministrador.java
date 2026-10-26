package biblioteca.presentacion;

import biblioteca.modelo.Libro;
import biblioteca.negocio.LibroService;
import biblioteca.negocio.PrestamoService;
import biblioteca.negocio.SocioService;

/**
 * Opciones del perfil Administrador: gestión de libros y consulta de la
 * información general de socios y préstamos.
 */
public class MenuAdministrador {
    private final Consola consola;
    private final SocioService socioService;
    private final LibroService libroService;
    private final PrestamoService prestamoService;

    public MenuAdministrador(Consola consola, SocioService socioService, LibroService libroService,
                             PrestamoService prestamoService) {
        this.consola = consola;
        this.socioService = socioService;
        this.libroService = libroService;
        this.prestamoService = prestamoService;
    }

    public void mostrar() {
        int opcion;
        do {
            consola.titulo("MENÚ ADMINISTRADOR");
            System.out.println("1. Registrar libro");
            System.out.println("2. Modificar libro");
            System.out.println("3. Consultar libro por ID");
            System.out.println("4. Dar de baja libro");
            System.out.println("5. Buscar libros por título");
            System.out.println("6. Buscar libros por autor");
            System.out.println("7. Listar socios");
            System.out.println("8. Historial de préstamos de un socio");
            System.out.println("9. Listar préstamos vigentes");
            System.out.println("10. Listar préstamos vencidos");
            System.out.println("0. Volver al menú principal");
            opcion = consola.leerEntero("Opción");
            try {
                switch (opcion) {
                    case 1 -> registrarLibro();
                    case 2 -> modificarLibro();
                    case 3 -> Vistas.libro(libroService.consultar(consola.leerEntero("ID del ejemplar")), libroService);
                    case 4 -> darDeBajaLibro();
                    case 5 -> Vistas.libros(libroService.buscarPorTitulo(consola.leerTexto("Título a buscar")), libroService);
                    case 6 -> Vistas.libros(libroService.buscarPorAutor(consola.leerTexto("Autor a buscar")), libroService);
                    case 7 -> Vistas.socios(socioService.listar());
                    case 8 -> Vistas.prestamos(socioService.historial(consola.leerEntero("Número de socio")),
                            prestamoService.hoy(), false);
                    case 9 -> Vistas.prestamos(prestamoService.listarVigentes(), prestamoService.hoy(), true);
                    case 10 -> Vistas.prestamos(prestamoService.listarVencidos(), prestamoService.hoy(), true);
                    case 0 -> { }
                    default -> consola.error("Opción inválida");
                }
            } catch (RuntimeException e) {
                consola.error(e.getMessage());
            }
        } while (opcion != 0);
    }

    private void registrarLibro() {
        String titulo = consola.leerTexto("Título");
        String autor = consola.leerTexto("Autor");
        String editorial = consola.leerTexto("Editorial");
        int anio = consola.leerEntero("Año de publicación");
        Libro libro = libroService.registrar(titulo, autor, editorial, anio);
        consola.exito("Libro registrado. " + libro);
    }

    private void modificarLibro() {
        int id = consola.leerEntero("ID del ejemplar a modificar");
        System.out.println("Datos actuales: " + libroService.consultar(id));
        String titulo = consola.leerTextoOpcional("Nuevo título");
        String autor = consola.leerTextoOpcional("Nuevo autor");
        String editorial = consola.leerTextoOpcional("Nueva editorial");
        Integer anio = consola.leerEnteroOpcional("Nuevo año de publicación");
        Libro libro = libroService.modificar(id, titulo, autor, editorial, anio);
        consola.exito("Libro modificado. " + libro);
    }

    private void darDeBajaLibro() {
        int id = consola.leerEntero("ID del ejemplar a dar de baja");
        System.out.println(libroService.consultar(id));
        if (consola.confirmar("¿Confirma la baja?")) {
            libroService.darDeBaja(id);
            consola.exito("Libro dado de baja. Su historial se conserva.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
}

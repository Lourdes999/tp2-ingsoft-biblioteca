package biblioteca.presentacion;

import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.negocio.Comprobante;
import biblioteca.negocio.LibroService;
import biblioteca.negocio.PrestamoService;
import biblioteca.negocio.SocioService;

/**
 * Opciones del perfil Bibliotecario: gestión de socios, consulta de libros,
 * préstamos, devoluciones y listados.
 */
public class MenuBibliotecario {
    private final Consola consola;
    private final SocioService socioService;
    private final LibroService libroService;
    private final PrestamoService prestamoService;

    public MenuBibliotecario(Consola consola, SocioService socioService, LibroService libroService,
                             PrestamoService prestamoService) {
        this.consola = consola;
        this.socioService = socioService;
        this.libroService = libroService;
        this.prestamoService = prestamoService;
    }

    public void mostrar() {
        int opcion;
        do {
            consola.titulo("MENÚ BIBLIOTECARIO");
            System.out.println("1. Registrar socio");
            System.out.println("2. Modificar socio");
            System.out.println("3. Consultar socio");
            System.out.println("4. Dar de baja socio");
            System.out.println("5. Buscar libros por título");
            System.out.println("6. Buscar libros por autor");
            System.out.println("7. Registrar préstamo");
            System.out.println("8. Registrar devolución");
            System.out.println("9. Historial de préstamos de un socio");
            System.out.println("10. Listar préstamos vigentes");
            System.out.println("11. Listar préstamos vencidos");
            System.out.println("0. Volver al menú principal");
            opcion = consola.leerEntero("Opción");
            try {
                switch (opcion) {
                    case 1 -> registrarSocio();
                    case 2 -> modificarSocio();
                    case 3 -> consultarSocio();
                    case 4 -> darDeBajaSocio();
                    case 5 -> Vistas.libros(libroService.buscarPorTitulo(consola.leerTexto("Título a buscar")), libroService);
                    case 6 -> Vistas.libros(libroService.buscarPorAutor(consola.leerTexto("Autor a buscar")), libroService);
                    case 7 -> registrarPrestamo();
                    case 8 -> registrarDevolucion();
                    case 9 -> Vistas.prestamos(socioService.historial(consola.leerEntero("Número de socio")),
                            prestamoService.hoy(), false);
                    case 10 -> Vistas.prestamos(prestamoService.listarVigentes(), prestamoService.hoy(), true);
                    case 11 -> Vistas.prestamos(prestamoService.listarVencidos(), prestamoService.hoy(), true);
                    case 0 -> { }
                    default -> consola.error("Opción inválida");
                }
            } catch (RuntimeException e) {
                consola.error(e.getMessage());
            }
        } while (opcion != 0);
    }

    private void registrarSocio() {
        int numero = consola.leerEntero("Número de socio");
        String dni = consola.leerTexto("DNI");
        String nombre = consola.leerTexto("Nombre");
        String apellido = consola.leerTexto("Apellido");
        Socio socio = socioService.registrar(numero, dni, nombre, apellido);
        consola.exito("Socio registrado. " + socio);
    }

    private void modificarSocio() {
        int numero = consola.leerEntero("Número de socio a modificar");
        System.out.println("Datos actuales: " + socioService.consultar(numero));
        String dni = consola.leerTextoOpcional("Nuevo DNI");
        String nombre = consola.leerTextoOpcional("Nuevo nombre");
        String apellido = consola.leerTextoOpcional("Nuevo apellido");
        Socio socio = socioService.modificar(numero, dni, nombre, apellido);
        consola.exito("Socio modificado. " + socio);
    }

    private void consultarSocio() {
        Socio socio = socioService.consultar(consola.leerEntero("Número de socio"));
        System.out.println(socio);
    }

    private void darDeBajaSocio() {
        int numero = consola.leerEntero("Número de socio a dar de baja");
        System.out.println(socioService.consultar(numero));
        if (consola.confirmar("¿Confirma la baja?")) {
            socioService.darDeBaja(numero);
            consola.exito("Socio dado de baja. Su historial se conserva.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void registrarPrestamo() {
        int numero = consola.leerEntero("Número de socio");
        Socio socio = prestamoService.validarSocio(numero);
        System.out.println("Socio habilitado: " + socio.getNombreCompleto());
        int libroId = consola.leerEntero("ID del ejemplar");
        Prestamo prestamo = prestamoService.registrarPrestamo(numero, libroId);
        consola.exito("Préstamo registrado.");
        System.out.println(Comprobante.dePrestamo(prestamo));
    }

    private void registrarDevolucion() {
        int prestamoId = consola.leerEntero("Número de préstamo");
        Prestamo prestamo = prestamoService.registrarDevolucion(prestamoId);
        consola.exito("Devolución registrada.");
        System.out.println(Comprobante.deDevolucion(prestamo));
    }
}

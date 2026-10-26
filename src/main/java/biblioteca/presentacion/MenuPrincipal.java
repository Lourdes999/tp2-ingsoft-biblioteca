package biblioteca.presentacion;

/**
 * Menú inicial: permite seleccionar el perfil (simplificación del acceso).
 */
public class MenuPrincipal {
    private final Consola consola;
    private final MenuSocio menuSocio;
    private final MenuBibliotecario menuBibliotecario;
    private final MenuAdministrador menuAdministrador;

    public MenuPrincipal(Consola consola, MenuSocio menuSocio, MenuBibliotecario menuBibliotecario,
                         MenuAdministrador menuAdministrador) {
        this.consola = consola;
        this.menuSocio = menuSocio;
        this.menuBibliotecario = menuBibliotecario;
        this.menuAdministrador = menuAdministrador;
    }

    public void mostrar() {
        int opcion;
        do {
            consola.titulo("SISTEMA DE GESTIÓN DE BIBLIOTECA");
            System.out.println("Seleccione su perfil:");
            System.out.println("1. Socio");
            System.out.println("2. Bibliotecario");
            System.out.println("3. Administrador");
            System.out.println("0. Salir");
            opcion = consola.leerEntero("Opción");
            switch (opcion) {
                case 1 -> menuSocio.mostrar();
                case 2 -> menuBibliotecario.mostrar();
                case 3 -> menuAdministrador.mostrar();
                case 0 -> System.out.println("Fin del programa.");
                default -> consola.error("Opción inválida");
            }
        } while (opcion != 0);
    }
}

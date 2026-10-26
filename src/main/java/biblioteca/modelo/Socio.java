package biblioteca.modelo;

/**
 * Socio de la biblioteca.
 */
public class Socio {
    private int id;
    private int numeroSocio;
    private String dni;
    private String nombre;
    private String apellido;
    private boolean activo;

    public Socio(int numeroSocio, String dni, String nombre, String apellido) {
        this.numeroSocio = numeroSocio;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = true;
    }

    public Socio(int id, int numeroSocio, String dni, String nombre, String apellido, boolean activo) {
        this(numeroSocio, dni, nombre, apellido);
        this.id = id;
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean estaActivo() {
        return activo;
    }

    /** Baja lógica (RN-10): el socio queda inactivo y conserva su historial. */
    public void darDeBaja() {
        this.activo = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNumeroSocio() { return numeroSocio; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    @Override
    public String toString() {
        return "Socio N° " + numeroSocio + " | DNI " + dni + " | " + getNombreCompleto()
                + " | " + (activo ? "Activo" : "Inactivo");
    }
}

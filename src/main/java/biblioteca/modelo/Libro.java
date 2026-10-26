package biblioteca.modelo;

/**
 * Libro de la biblioteca. Cada registro representa un ejemplar físico:
 * dos ejemplares del mismo título tienen identificadores distintos.
 */
public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private String editorial;
    private int anioPublicacion;
    private boolean activo;

    public Libro(String titulo, String autor, String editorial, int anioPublicacion) {
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.activo = true;
    }

    public Libro(int id, String titulo, String autor, String editorial, int anioPublicacion, boolean activo) {
        this(titulo, autor, editorial, anioPublicacion);
        this.id = id;
        this.activo = activo;
    }

    public boolean estaActivo() {
        return activo;
    }

    /** Baja lógica (RN-10): el ejemplar queda inactivo y conserva su historial. */
    public void darDeBaja() {
        this.activo = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    @Override
    public String toString() {
        return "Libro ID " + id + " | " + titulo + " - " + autor + " | " + editorial + " (" + anioPublicacion + ")";
    }
}

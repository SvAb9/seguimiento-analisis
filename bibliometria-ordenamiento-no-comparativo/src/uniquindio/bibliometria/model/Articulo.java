package uniquindio.bibliometria.model;

import java.util.Collections;
import java.util.List;

/**
 * Uno de los 20 articulos del corpus: identificador, titulo, autores y abstract.
 * El ranking de terminos usa solo el abstract; titulo y autores se conservan para
 * los requerimientos que cruzan informacion.
 */
public final class Articulo {

    private final int id;
    private final String titulo;
    private final List<String> autores;
    private final String resumen;

    public Articulo(int id, String titulo, List<String> autores, String resumen) {
        this.id = id;
        this.titulo = titulo;
        this.autores = Collections.unmodifiableList(autores);
        this.resumen = resumen;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<String> getAutores() {
        return autores;
    }

    public String getResumen() {
        return resumen;
    }
}

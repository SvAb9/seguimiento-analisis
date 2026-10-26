package uniquindio.bibliometria.model;

/**
 * Articulo del corpus: los metadatos (titulo y autores) y el abstract extraidos
 * de uno de los 20 PDF entregados por el docente. El ranking de terminos solo
 * usa el abstract; titulo y autores quedan para los requerimientos que cruzan
 * informacion.
 */
public final class Article {

    private final int id;
    private final String file;
    private final String title;
    private final String authors;
    private final String abstractText;

    public Article(int id, String file, String title, String authors, String abstractText) {
        this.id = id;
        this.file = file;
        this.title = title;
        this.authors = authors;
        this.abstractText = abstractText;
    }

    public int getId() {
        return id;
    }

    public String getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getAbstractText() {
        return abstractText;
    }

    @Override
    public String toString() {
        return id + ". " + title;
    }
}

package uniquindio.bibliometria.model;

/**
 * Par (termino, frecuencia) que representa una palabra del corpus y el numero
 * de veces que aparece en los abstracts de los 20 articulos.
 *
 * Esta clase es el CONTRATO COMUN entre los tres integrantes: Zuleta la produce
 * (extraccion + preprocesamiento) y los tres algoritmos (Counting, Radix y
 * Bucket Sort) la consumen.
 */
public final class TermFrequency {

    private final String term;
    private final int frequency;

    public TermFrequency(String term, int frequency) {
        this.term = term;
        this.frequency = frequency;
    }

    public String getTerm() {
        return term;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return term + "=" + frequency;
    }
}

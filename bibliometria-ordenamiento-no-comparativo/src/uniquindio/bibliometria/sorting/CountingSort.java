package uniquindio.bibliometria.sorting;

import uniquindio.bibliometria.model.TermFrequency;

/**
 * Algoritmo 1 - Counting Sort. RESPONSABLE: Zuleta.
 *
 * PENDIENTE: esta clase es solo el esqueleto para que el proyecto compile.
 * Debe implementar FrequencySorter respetando las reglas del contrato
 * (descendente, estable, sin modificar la entrada).
 */
public class CountingSort implements FrequencySorter {

    @Override
    public String getName() {
        return "Counting Sort";
    }

    @Override
    public TermFrequency[] sortDescending(TermFrequency[] input) {
        // TODO (Zuleta): implementar Counting Sort sobre las frecuencias
        throw new UnsupportedOperationException("Counting Sort pendiente (Zuleta)");
    }
}

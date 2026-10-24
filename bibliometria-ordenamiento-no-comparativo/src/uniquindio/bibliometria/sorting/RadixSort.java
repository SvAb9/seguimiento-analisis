package uniquindio.bibliometria.sorting;

import uniquindio.bibliometria.model.TermFrequency;

/**
 * Algoritmo 2 - Radix Sort. RESPONSABLE: Natalia.
 *
 * PENDIENTE: esta clase es solo el esqueleto para que el proyecto compile.
 * Debe implementar FrequencySorter respetando las reglas del contrato
 * (descendente, estable, sin modificar la entrada).
 */
public class RadixSort implements FrequencySorter {

    @Override
    public String getName() {
        return "Radix Sort";
    }

    @Override
    public TermFrequency[] sortDescending(TermFrequency[] input) {
        // TODO (Natalia): implementar Radix Sort (LSD) sobre las frecuencias
        throw new UnsupportedOperationException("Radix Sort pendiente (Natalia)");
    }
}

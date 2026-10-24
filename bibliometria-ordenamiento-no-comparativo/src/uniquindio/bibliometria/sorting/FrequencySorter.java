package uniquindio.bibliometria.sorting;

import uniquindio.bibliometria.model.TermFrequency;

/**
 * Contrato que deben cumplir los tres algoritmos no comparativos
 * (CountingSort, RadixSort y BucketSort).
 *
 * Reglas acordadas para que los tres produzcan EXACTAMENTE el mismo ranking:
 *  1. Orden DESCENDENTE por frecuencia (el termino mas frecuente queda en la posicion 0).
 *  2. El algoritmo debe ser ESTABLE: ante frecuencias iguales conserva el orden de la
 *     entrada. Si la entrada llega ordenada alfabeticamente, los empates quedan
 *     alfabeticos en los tres algoritmos y el top-15 coincide.
 *  3. NO modifica el arreglo recibido: devuelve un arreglo nuevo.
 *  4. Dentro del algoritmo no se usan comparaciones entre elementos como mecanismo
 *     principal de ordenamiento (esa es la gracia del requerimiento). Unica excepcion
 *     documentada: BucketSort ordena cada cubeta con insercion.
 */
public interface FrequencySorter {

    /** Nombre corto para tablas y graficos (por ejemplo "Bucket Sort"). */
    String getName();

    /** Devuelve una copia de la entrada ordenada de mayor a menor frecuencia. */
    TermFrequency[] sortDescending(TermFrequency[] input);
}

package uniquindio.bibliometria.sorting;

import uniquindio.bibliometria.model.TermFrequency;

/**
 * Algoritmo 1 - Counting Sort (responsable: Zuleta).
 *
 * Ordena de mayor a menor frecuencia una lista de terminos.
 *
 * Idea (esquema de la seccion 3.3 del informe adaptado a TermFrequency[]):
 *  - Las frecuencias estan en [min, max]; rango k = max - min + 1.
 *  - Se cuenta cuantas veces aparece cada valor v en c[v - min] (no hay comparaciones).
 *  - Se acumula DE MAYOR A MENOR: c[v] pasa a ser el numero de elementos con valor >= v,
 *    es decir, la posicion final (exclusiva) del ultimo elemento con ese valor.
 *  - Se recorre la entrada de derecha a izquierda colocando cada elemento en --c[v],
 *    lo que conserva el orden de llegada de los empates (estable).
 *
 * Complejidad: T(n, k) = O(n + k) tiempo y O(n + k) espacio adicional.
 */
public class CountingSort implements FrequencySorter {

    @Override
    public String getName() {
        return "Counting Sort";
    }

    @Override
    public TermFrequency[] sortDescending(TermFrequency[] entrada) {
        if (entrada == null) {
            throw new IllegalArgumentException("La entrada no puede ser null");
        }
        int n = entrada.length;
        TermFrequency[] salida = new TermFrequency[n];
        if (n == 0) {
            return salida;
        }

        // Paso 1: frecuencia minima y maxima
        int min = entrada[0].getFrequency();
        int max = min;
        for (int i = 1; i < n; i++) {
            int f = entrada[i].getFrequency();
            if (f < min) {
                min = f;
            }
            if (f > max) {
                max = f;
            }
        }
        long rango = (long) max - min + 1;
        if (rango > Integer.MAX_VALUE - 8) {
            throw new IllegalArgumentException("Rango k = " + rango + " demasiado grande para Counting Sort");
        }
        int k = (int) rango;

        // Paso 2: contar cada frecuencia
        int[] c = new int[k];
        for (int i = 0; i < n; i++) {
            c[entrada[i].getFrequency() - min]++;
        }

        // Paso 3: acumular de mayor a menor (c[v] = elementos con valor >= v)
        for (int v = k - 2; v >= 0; v--) {
            c[v] += c[v + 1];
        }

        // Paso 4: colocar de derecha a izquierda (estable)
        for (int i = n - 1; i >= 0; i--) {
            int v = entrada[i].getFrequency() - min;
            c[v]--;
            salida[c[v]] = entrada[i];
        }
        return salida;
    }
}

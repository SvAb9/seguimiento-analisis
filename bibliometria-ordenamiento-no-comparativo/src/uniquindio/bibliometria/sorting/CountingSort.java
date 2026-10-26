package uniquindio.bibliometria.sorting;

import uniquindio.bibliometria.model.TermFrequency;

/**
 * Algoritmo 1 - Counting Sort (responsable: Zuleta).
 *
 * Ordena de mayor a menor frecuencia una lista de terminos.
 *
 * Idea:
 *  - Las frecuencias estan en el intervalo [min, max]; rango k = max - min + 1.
 *  - Se trabaja con la clave x = f - min, que esta en [0, k - 1] (admite negativos).
 *  - Se cuenta cuantas veces aparece cada clave en un arreglo de k contadores.
 *  - Los contadores se acumulan de la clave mayor a la menor: c[x] queda como el
 *    numero de elementos con clave >= x, que es donde termina el bloque de x
 *    en el orden descendente.
 *  - Se coloca cada elemento recorriendo la entrada de derecha a izquierda,
 *    lo que conserva el orden de llegada de los empates (estable).
 *
 * Complejidad (ver seccion 3.3 del informe): O(n + k) en tiempo y espacio.
 * No hay comparaciones entre elementos: la frecuencia se usa como indice.
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
        if (n < 2) {
            if (n == 1) {
                salida[0] = entrada[0];
            }
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

        // Paso 2: rango k y arreglo de contadores (uno por cada valor posible)
        long rango = (long) max - min + 1;
        if (rango > Integer.MAX_VALUE - 8) {
            throw new IllegalArgumentException("Rango de frecuencias demasiado grande para Counting Sort: " + rango);
        }
        int k = (int) rango;
        int[] c = new int[k];

        // Paso 3: contar cuantas veces aparece cada clave x = f - min
        for (int i = 0; i < n; i++) {
            c[entrada[i].getFrequency() - min]++;
        }

        // Paso 4: acumular de la clave mayor a la menor (orden descendente)
        for (int x = k - 2; x >= 0; x--) {
            c[x] += c[x + 1];
        }

        // Paso 5: colocar de derecha a izquierda (estable)
        for (int i = n - 1; i >= 0; i--) {
            int x = entrada[i].getFrequency() - min;
            c[x]--;
            salida[c[x]] = entrada[i];
        }
        return salida;
    }
}

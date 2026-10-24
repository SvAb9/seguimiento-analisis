package uniquindio.bibliometria.sorting;

import uniquindio.bibliometria.model.TermFrequency;

/**
 * Algoritmo 3 - Bucket Sort (responsable: Sara).
 *
 * Ordena de mayor a menor frecuencia una lista de terminos.
 *
 * Idea:
 *  - Las frecuencias estan en el intervalo [min, max]; rango k = max - min + 1.
 *  - Se crean m = min(n, k) cubetas, cada una cubre un subintervalo de ancho k/m.
 *  - Cada elemento se envia a su cubeta en O(1) con una formula aritmetica
 *    (no se compara con otros elementos).
 *  - Cada cubeta se ordena con insercion estable (las cubetas son pequenas).
 *  - Se concatenan las cubetas de la ultima a la primera (orden descendente).
 *
 * Complejidad (ver docs/analisis_bucket_sort.md y el informe):
 *  - T(n) = 56n + 19m - 15e + 31 + 11S  (conteo de operaciones elementales,
 *    e = cubetas no vacias, S = desplazamientos totales de la insercion).
 *  - Si k <= n  ->  m = k, cada cubeta guarda UNA sola frecuencia, S = 0  ->  O(n + k).
 *  - Si k > n y la distribucion es uniforme -> O(n) esperado.
 *  - Peor caso (todo en una cubeta y en orden inverso) -> O(n^2).
 */
public class BucketSort implements FrequencySorter {

    @Override
    public String getName() {
        return "Bucket Sort";
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

        // Paso 2: rango k y numero de cubetas m = min(n, k)
        long rango = (long) max - min + 1;
        int m = (int) Math.min(n, rango);

        // Paso 3: cubeta de cada elemento y tamano de cada cubeta
        int[] tamanos = new int[m];
        int[] indice = new int[n];
        for (int i = 0; i < n; i++) {
            int c = (int) ((((long) entrada[i].getFrequency() - min) * m) / rango);
            indice[i] = c;
            tamanos[c]++;
        }

        // Paso 4: crear las cubetas con su tamano exacto
        TermFrequency[][] cubetas = new TermFrequency[m][];
        for (int c = 0; c < m; c++) {
            cubetas[c] = new TermFrequency[tamanos[c]];
        }

        // Paso 5: distribuir los elementos (conserva el orden de llegada -> estable)
        int[] llenos = new int[m];
        for (int i = 0; i < n; i++) {
            int c = indice[i];
            cubetas[c][llenos[c]] = entrada[i];
            llenos[c]++;
        }

        // Paso 6: ordenar cada cubeta (insercion estable, descendente)
        for (int c = 0; c < m; c++) {
            insercionDescendente(cubetas[c]);
        }

        // Paso 7: concatenar de la ultima cubeta (frecuencias altas) a la primera
        int pos = 0;
        for (int c = m - 1; c >= 0; c--) {
            for (int j = 0; j < cubetas[c].length; j++) {
                salida[pos] = cubetas[c][j];
                pos++;
            }
        }
        return salida;
    }

    /** Insercion estable en orden descendente (solo se usa dentro de una cubeta). */
    private void insercionDescendente(TermFrequency[] a) {
        for (int i = 1; i < a.length; i++) {
            TermFrequency clave = a[i];
            int j = i - 1;
            while (j >= 0 && a[j].getFrequency() < clave.getFrequency()) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = clave;
        }
    }
}

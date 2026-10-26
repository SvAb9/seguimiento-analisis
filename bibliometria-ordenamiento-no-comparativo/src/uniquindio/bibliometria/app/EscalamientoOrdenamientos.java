package uniquindio.bibliometria.app;

import java.util.Arrays;
import java.util.Locale;
import uniquindio.bibliometria.model.TermFrequency;
import uniquindio.bibliometria.sorting.BucketSort;
import uniquindio.bibliometria.sorting.CountingSort;
import uniquindio.bibliometria.sorting.FrequencySorter;
import uniquindio.bibliometria.sorting.RadixSort;

/**
 * Comprobacion empirica del crecimiento de los tres algoritmos al duplicar n
 * (complementa la medicion sobre el corpus, que tiene un unico tamano n = 984).
 * Usa los mismos generadores sinteticos de EscalamientoBucketSort:
 *   ZIPF     : frecuencias tipo lenguaje natural (k <= n)
 *   UNIFORME : frecuencias uniformes en [1, 10n] (k > n: crece el arreglo de Counting y los digitos de Radix)
 * Salida CSV: escenario,algoritmo,n,k,d_radix,ms_mediana (mediana de 21 ejecuciones; el calentamiento
 * es de max(10, 4.000.000 / n) ejecuciones para que el JIT compile el codigo tambien con n pequeno)
 *
 * Uso: java -cp out uniquindio.bibliometria.app.EscalamientoOrdenamientos
 */
public class EscalamientoOrdenamientos {

    private static final int REPETICIONES = 21;

    public static void main(String[] args) {
        int[] tamanos = {1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 256000};
        FrequencySorter[] algoritmos = {new CountingSort(), new RadixSort(), new BucketSort()};
        System.out.println("escenario,algoritmo,n,k,d_radix,ms_mediana");
        for (int n : tamanos) {
            TermFrequency[] zipf = EscalamientoBucketSort.crearZipf(n);
            TermFrequency[] uniforme = EscalamientoBucketSort.crearUniforme(n);
            for (FrequencySorter s : algoritmos) {
                medir("ZIPF", s, zipf);
                medir("UNIFORME", s, uniforme);
            }
        }
    }

    private static void medir(String escenario, FrequencySorter s, TermFrequency[] datos) {
        int calentamiento = Math.max(10, 4_000_000 / datos.length);
        for (int i = 0; i < calentamiento; i++) {          // calentamiento de la JVM
            s.sortDescending(datos);
        }
        double[] tiempos = new double[REPETICIONES];
        for (int r = 0; r < REPETICIONES; r++) {
            long ini = System.nanoTime();
            s.sortDescending(datos);
            tiempos[r] = (System.nanoTime() - ini) / 1_000_000.0;
        }
        Arrays.sort(tiempos);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (TermFrequency t : datos) {
            min = Math.min(min, t.getFrequency());
            max = Math.max(max, t.getFrequency());
        }
        long k = (long) max - min + 1;
        int d = 0;
        for (long exp = 1; (k - 1) / exp > 0; exp *= RadixSort.BASE_POR_DEFECTO) {
            d++;
        }
        System.out.printf(Locale.US, "%s,%s,%d,%d,%d,%.4f%n", escenario, s.getName(), datos.length, k, d, tiempos[REPETICIONES / 2]);
    }
}

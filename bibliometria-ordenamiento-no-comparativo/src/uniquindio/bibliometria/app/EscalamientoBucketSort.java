package uniquindio.bibliometria.app;

import java.util.Arrays;
import java.util.Random;
import uniquindio.bibliometria.model.TermFrequency;
import uniquindio.bibliometria.sorting.BucketSort;

/**
 * Comprobacion empirica de la complejidad de Bucket Sort (parte de Sara).
 * Mide el tiempo (ms) al duplicar n en tres escenarios:
 *   ZIPF         : frecuencias tipo lenguaje natural (k <= n  ->  O(n + k))
 *   UNIFORME     : frecuencias uniformes en [1, 10n]  (k > n, caso esperado O(n))
 *   ADVERSARIO   : 1..n-1 ascendente + un valor enorme (todo en una cubeta -> O(n^2))
 * Salida en formato CSV: escenario,n,ms_mediana
 *
 * Uso: java -cp out uniquindio.bibliometria.app.EscalamientoBucketSort
 */
public class EscalamientoBucketSort {

    private static final int REPETICIONES = 7;

    public static void main(String[] args) {
        int[] tamanos = {1000, 2000, 4000, 8000, 16000, 32000, 64000};
        System.out.println("escenario,n,k,ms_mediana");
        for (int n : tamanos) {
            medir("ZIPF", crearZipf(n));
            medir("UNIFORME", crearUniforme(n));
        }
        int[] tamAdv = {1000, 2000, 4000, 8000, 16000, 32000};
        for (int n : tamAdv) {
            medir("ADVERSARIO", crearAdversario(n));
        }
    }

    private static void medir(String escenario, TermFrequency[] datos) {
        BucketSort bs = new BucketSort();
        for (int i = 0; i < 3; i++) {          // calentamiento de la JVM
            bs.sortDescending(datos);
        }
        double[] tiempos = new double[REPETICIONES];
        for (int r = 0; r < REPETICIONES; r++) {
            long ini = System.nanoTime();
            bs.sortDescending(datos);
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
        System.out.printf(java.util.Locale.US, "%s,%d,%d,%.3f%n", escenario, datos.length, k, tiempos[REPETICIONES / 2]);
    }

    static TermFrequency[] crearZipf(int n) {
        Random rnd = new Random(7);
        TermFrequency[] d = new TermFrequency[n];
        double c = 0.2 * n;                     // frecuencia del termino mas comun ~ proporcional a n
        for (int r = 1; r <= n; r++) {
            int f = Math.max(1, (int) (c / Math.pow(r, 0.9) * (0.85 + 0.3 * rnd.nextDouble())));
            d[r - 1] = new TermFrequency(String.format("t%07d", r), f);
        }
        return d;
    }

    static TermFrequency[] crearUniforme(int n) {
        Random rnd = new Random(11);
        TermFrequency[] d = new TermFrequency[n];
        for (int i = 0; i < n; i++) {
            d[i] = new TermFrequency(String.format("t%07d", i), 1 + rnd.nextInt(10 * n));
        }
        return d;
    }

    private static TermFrequency[] crearAdversario(int n) {
        TermFrequency[] d = new TermFrequency[n];
        for (int i = 0; i < n - 1; i++) {
            d[i] = new TermFrequency(String.format("t%07d", i), i + 1);
        }
        d[n - 1] = new TermFrequency("outlier", n * n);
        return d;
    }
}

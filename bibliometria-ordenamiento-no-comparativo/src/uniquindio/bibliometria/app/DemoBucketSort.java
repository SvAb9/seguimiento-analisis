package uniquindio.bibliometria.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import uniquindio.bibliometria.io.FrequencyCsvReader;
import uniquindio.bibliometria.model.TermFrequency;
import uniquindio.bibliometria.sorting.BucketSort;
import uniquindio.bibliometria.sorting.FrequencySorter;
import uniquindio.bibliometria.sorting.Ranking;

/**
 * Ejecuta Bucket Sort sobre un CSV "termino,frecuencia" y muestra el top-15.
 *
 * Uso:  java -cp out uniquindio.bibliometria.app.DemoBucketSort [ruta.csv]
 * Sin argumentos usa data/frecuencias_ejemplo.csv (datos sinteticos).
 */
public class DemoBucketSort {

    public static void main(String[] args) throws Exception {
        Path ruta = Paths.get(args.length > 0 ? args[0] : "data/frecuencias_ejemplo.csv");
        TermFrequency[] datos = FrequencyCsvReader.read(ruta);
        System.out.println("Terminos distintos (n): " + datos.length);

        FrequencySorter sorter = new BucketSort();
        long inicio = System.nanoTime();
        TermFrequency[] ordenado = sorter.sortDescending(datos);
        double ms = (System.nanoTime() - inicio) / 1_000_000.0;

        Ranking.imprimir("Top 15 con " + sorter.getName(), Ranking.top(ordenado, 15));
        System.out.printf("Tiempo de una ejecucion (sin calentamiento): %.3f ms%n", ms);
        System.out.println("Orden descendente correcto: " + Ranking.esDescendente(ordenado));
    }
}

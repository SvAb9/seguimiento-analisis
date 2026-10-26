package uniquindio.bibliometria.app;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import uniquindio.bibliometria.io.FrequencyCsvReader;
import uniquindio.bibliometria.model.TermFrequency;
import uniquindio.bibliometria.sorting.BucketSort;
import uniquindio.bibliometria.sorting.CountingSort;
import uniquindio.bibliometria.sorting.FrequencySorter;
import uniquindio.bibliometria.sorting.RadixSort;
import uniquindio.bibliometria.sorting.Ranking;

/**
 * Medicion empirica y validacion del requerimiento (responsable: Natalia).
 *
 * Sobre la lista de frecuencias real del corpus (data/frecuencias_corpus.csv):
 *  1. Ejecuta Counting, Radix y Bucket Sort y comprueba que las tres salidas son identicas
 *     (mismo orden completo, no solo el top-15) y descendentes.
 *  2. Mide el tiempo de ejecucion en milisegundos:
 *       - primera ejecucion de cada algoritmo, en frio (JVM sin calentar);
 *       - CALENTAMIENTO ejecuciones descartadas por algoritmo (el compilador JIT optimiza el codigo);
 *       - RONDAS mediciones por algoritmo, alternando el orden de los tres en cada ronda
 *         para que ninguno se beneficie siempre de la cache. Se reporta minimo, mediana,
 *         media, desviacion estandar y maximo.
 *  3. Imprime y guarda el top-15 de cada algoritmo.
 * Archivos generados: docs/tiempos_corpus.csv y docs/top15_corpus.csv
 *
 * Uso: java -cp out uniquindio.bibliometria.app.MedicionCorpus [frecuencias.csv]
 */
public class MedicionCorpus {

    private static final int CALENTAMIENTO = 2000;
    private static final int RONDAS = 501;
    private static final int TOP = 15;

    public static void main(String[] args) throws Exception {
        Path ruta = Paths.get(args.length > 0 ? args[0] : "data/frecuencias_corpus.csv");
        TermFrequency[] datos = FrequencyCsvReader.read(ruta);
        FrequencySorter[] algoritmos = {new CountingSort(), new RadixSort(), new BucketSort()};
        int a = algoritmos.length;

        System.out.println("Archivo: " + ruta + "  |  terminos distintos n = " + datos.length);
        System.out.println("Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vm.name")
            + ")  |  " + System.getProperty("os.name") + " " + System.getProperty("os.arch")
            + "  |  nucleos: " + Runtime.getRuntime().availableProcessors());

        // 1. Ejecucion en frio + resultados
        TermFrequency[][] resultado = new TermFrequency[a][];
        double[] frio = new double[a];
        for (int i = 0; i < a; i++) {
            long ini = System.nanoTime();
            resultado[i] = algoritmos[i].sortDescending(datos);
            frio[i] = (System.nanoTime() - ini) / 1_000_000.0;
        }
        boolean identicos = true;
        boolean descendentes = true;
        for (int i = 0; i < a; i++) {
            descendentes = descendentes && Ranking.esDescendente(resultado[i]);
            identicos = identicos && Ranking.mismoTop(resultado[0], resultado[i], datos.length);
        }

        // 2. Calentamiento y medicion
        for (int r = 0; r < CALENTAMIENTO; r++) {
            for (FrequencySorter s : algoritmos) {
                s.sortDescending(datos);
            }
        }
        double[][] tiempos = new double[a][RONDAS];
        for (int r = 0; r < RONDAS; r++) {
            for (int j = 0; j < a; j++) {
                int i = (r + j) % a;                       // rota el orden de ejecucion
                long ini = System.nanoTime();
                algoritmos[i].sortDescending(datos);
                tiempos[i][r] = (System.nanoTime() - ini) / 1_000_000.0;
            }
        }

        // 3. Reporte de tiempos
        Path dirDocs = Paths.get("docs");
        Files.createDirectories(dirDocs);
        System.out.printf("%nTiempo de ejecucion (ms) - %d rondas tras %d ejecuciones de calentamiento%n", RONDAS, CALENTAMIENTO);
        System.out.printf("%-14s %10s %10s %10s %10s %10s %10s%n", "Algoritmo", "frio", "minimo", "mediana", "media", "desv.est", "maximo");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(dirDocs.resolve("tiempos_corpus.csv"), StandardCharsets.UTF_8))) {
            pw.println("algoritmo,n,ms_frio,ms_minimo,ms_mediana,ms_media,ms_desv_est,ms_maximo,rondas,calentamiento");
            for (int i = 0; i < a; i++) {
                double[] t = tiempos[i].clone();
                Arrays.sort(t);
                double media = 0;
                for (double x : t) {
                    media += x;
                }
                media /= t.length;
                double var = 0;
                for (double x : t) {
                    var += (x - media) * (x - media);
                }
                double desv = Math.sqrt(var / (t.length - 1));
                String nombre = algoritmos[i].getName();
                System.out.printf("%-14s %10.4f %10.4f %10.4f %10.4f %10.4f %10.4f%n",
                    nombre, frio[i], t[0], t[t.length / 2], media, desv, t[t.length - 1]);
                pw.printf(Locale.US, "%s,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%d,%d%n",
                    nombre, datos.length, frio[i], t[0], t[t.length / 2], media, desv, t[t.length - 1], RONDAS, CALENTAMIENTO);
            }
        }

        // 4. Top-15 por algoritmo
        System.out.printf("%nTop %d por algoritmo%n", TOP);
        System.out.printf("%-4s", "Pos");
        for (FrequencySorter s : algoritmos) {
            System.out.printf(" %-26s", s.getName());
        }
        System.out.println();
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(dirDocs.resolve("top15_corpus.csv"), StandardCharsets.UTF_8))) {
            pw.println("posicion,counting_termino,counting_frecuencia,radix_termino,radix_frecuencia,bucket_termino,bucket_frecuencia");
            for (int p = 0; p < Math.min(TOP, datos.length); p++) {
                System.out.printf("%-4d", p + 1);
                pw.print(p + 1);
                for (int i = 0; i < a; i++) {
                    TermFrequency t = resultado[i][p];
                    System.out.printf(" %-26s", t.getTerm() + " (" + t.getFrequency() + ")");
                    pw.print("," + t.getTerm() + "," + t.getFrequency());
                }
                System.out.println();
                pw.println();
            }
        }
        System.out.println();
        System.out.println("Salidas descendentes:                  " + descendentes);
        System.out.println("Los tres algoritmos dan el mismo orden: " + identicos);
        System.out.println("Archivos: docs/tiempos_corpus.csv, docs/top15_corpus.csv");
    }
}

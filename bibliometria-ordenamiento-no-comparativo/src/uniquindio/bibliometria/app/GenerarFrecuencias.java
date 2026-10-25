package uniquindio.bibliometria.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import uniquindio.bibliometria.io.CorpusReader;
import uniquindio.bibliometria.io.FrequencyCsvWriter;
import uniquindio.bibliometria.model.Articulo;
import uniquindio.bibliometria.model.TermFrequency;
import uniquindio.bibliometria.preprocessing.Preprocesador;

/**
 * Lee los abstracts del corpus, los preprocesa y escribe data/frecuencias_corpus.csv
 * (termino,frecuencia en orden alfabetico). Imprime los datos del corpus que usa el informe:
 * n (terminos distintos), total de palabras, frecuencia minima, maxima y k = max - min + 1.
 *
 * Uso: java -cp out uniquindio.bibliometria.app.GenerarFrecuencias [abstracts.txt] [salida.csv]
 */
public class GenerarFrecuencias {

    public static void main(String[] args) throws Exception {
        Path corpus = Paths.get(args.length > 0 ? args[0] : "data/corpus/abstracts.txt");
        Path salida = Paths.get(args.length > 1 ? args[1] : "data/frecuencias_corpus.csv");

        List<Articulo> articulos = CorpusReader.read(corpus);
        Preprocesador pre = new Preprocesador(Preprocesador.leerStopwords(Paths.get("data/stopwords_en.txt")));
        TermFrequency[] datos = pre.frecuencias(articulos);

        long total = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int unaVez = 0;
        for (TermFrequency t : datos) {
            total += t.getFrequency();
            min = Math.min(min, t.getFrequency());
            max = Math.max(max, t.getFrequency());
            if (t.getFrequency() == 1) {
                unaVez++;
            }
        }
        FrequencyCsvWriter.write(salida, datos,
            "Frecuencias de terminos en los abstracts de los 20 articulos (generado por GenerarFrecuencias)");

        System.out.println("Articulos leidos:            " + articulos.size());
        System.out.println("Terminos tras preprocesar:   " + total);
        System.out.println("Terminos distintos (n):      " + datos.length);
        System.out.println("Frecuencia minima:           " + min);
        System.out.println("Frecuencia maxima:           " + max);
        System.out.println("Rango k = max - min + 1:     " + ((long) max - min + 1));
        System.out.println("Terminos con frecuencia 1:   " + unaVez);
        System.out.println("CSV escrito en " + salida);
    }
}

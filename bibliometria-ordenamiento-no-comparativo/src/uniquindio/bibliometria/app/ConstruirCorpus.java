package uniquindio.bibliometria.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import uniquindio.bibliometria.extraction.ArticleExtractor;
import uniquindio.bibliometria.io.FrequencyCsvWriter;
import uniquindio.bibliometria.model.Article;
import uniquindio.bibliometria.model.TermFrequency;
import uniquindio.bibliometria.preprocessing.TextPreprocessor;
import uniquindio.bibliometria.sorting.CountingSort;
import uniquindio.bibliometria.sorting.Ranking;

/**
 * Construye la lista de frecuencias del corpus a partir de los 20 PDF (parte de Zuleta).
 *
 * Entradas:  corpus/pdf/*.pdf  y  data/stopwords_en.txt
 * Salidas:
 *   corpus/abstracts/NN.txt      abstract extraido de cada articulo (para revisarlo a mano)
 *   data/articulos.csv           id, archivo, titulo, autores, palabras del abstract
 *   data/frecuencias_corpus.csv  termino,frecuencia en ORDEN ALFABETICO (entrada comun de los 3 algoritmos)
 * Imprime n, frecuencia minima y maxima, k y el top-15 (con Counting Sort).
 *
 * Uso: java -cp "out:lib/*" uniquindio.bibliometria.app.ConstruirCorpus   (en Windows "out;lib/*")
 */
public class ConstruirCorpus {

    /** Referencia fuerte: si el logger se recolecta, PDFBox vuelve a imprimir advertencias de fuentes. */
    private static final Logger LOG_PDFBOX = Logger.getLogger("org.apache.pdfbox");

    public static void main(String[] args) throws Exception {
        LOG_PDFBOX.setLevel(Level.SEVERE);
        Path carpetaPdf = Paths.get("corpus/pdf");
        Path carpetaAbstracts = Paths.get("corpus/abstracts");
        Files.createDirectories(carpetaAbstracts);

        File[] pdfs = carpetaPdf.toFile().listFiles((d, nombre) -> nombre.toLowerCase().endsWith(".pdf"));
        if (pdfs == null || pdfs.length == 0) {
            throw new IOException("No hay PDF en " + carpetaPdf.toAbsolutePath());
        }
        Arrays.sort(pdfs, (a, b) -> Integer.compare(numero(a.getName()), numero(b.getName())));

        Set<String> stopwords = TextPreprocessor.leerStopwords(Paths.get("data/stopwords_en.txt"));
        TextPreprocessor pre = new TextPreprocessor(stopwords);

        List<Article> articulos = new ArrayList<>();
        Map<String, Integer> conteo = new TreeMap<>();      // TreeMap: los terminos quedan en orden alfabetico
        int totalTerminos = 0;
        for (File pdf : pdfs) {
            Article a = ArticleExtractor.extract(numero(pdf.getName()), pdf);
            articulos.add(a);
            Files.writeString(carpetaAbstracts.resolve(String.format("%02d.txt", a.getId())),
                a.getTitle() + System.lineSeparator() + System.lineSeparator() + a.getAbstractText()
                    + System.lineSeparator(), StandardCharsets.UTF_8);
            for (String termino : pre.terminos(a.getAbstractText())) {
                conteo.merge(termino, 1, Integer::sum);
                totalTerminos++;
            }
        }

        TermFrequency[] lista = new TermFrequency[conteo.size()];
        int i = 0;
        for (Map.Entry<String, Integer> e : conteo.entrySet()) {
            lista[i++] = new TermFrequency(e.getKey(), e.getValue());
        }
        FrequencyCsvWriter.write(Paths.get("data/frecuencias_corpus.csv"), lista);
        escribirArticulos(Paths.get("data/articulos.csv"), articulos);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (TermFrequency t : lista) {
            min = Math.min(min, t.getFrequency());
            max = Math.max(max, t.getFrequency());
        }
        System.out.println("Articulos procesados: " + articulos.size());
        System.out.println("Terminos tras el preprocesamiento (con repeticion): " + totalTerminos);
        System.out.println("Terminos distintos (n): " + lista.length);
        System.out.println("Frecuencia minima: " + min + "  maxima: " + max + "  k = max - min + 1 = " + (max - min + 1));
        Ranking.imprimir("Top 15 (Counting Sort)", Ranking.top(new CountingSort().sortDescending(lista), 15));
    }

    /** Numero inicial del nombre del archivo ("7. Human-centred ..." -> 7). */
    private static int numero(String nombre) {
        return Integer.parseInt(nombre.substring(0, nombre.indexOf('.')).trim());
    }

    private static void escribirArticulos(Path archivo, List<Article> articulos) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            bw.write("id,archivo,titulo,autores,palabras_abstract");
            bw.newLine();
            for (Article a : articulos) {
                bw.write(a.getId() + "," + csv(a.getFile()) + "," + csv(a.getTitle()) + "," + csv(a.getAuthors())
                    + "," + a.getAbstractText().split("\\s+").length);
                bw.newLine();
            }
        }
    }

    private static String csv(String valor) {
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}

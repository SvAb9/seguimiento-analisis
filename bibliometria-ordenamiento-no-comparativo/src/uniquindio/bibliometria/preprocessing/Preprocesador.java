package uniquindio.bibliometria.preprocessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import uniquindio.bibliometria.model.Articulo;
import uniquindio.bibliometria.model.TermFrequency;

/**
 * Preprocesamiento de los abstracts y calculo de frecuencias de terminos.
 *
 * Pasos aplicados a cada abstract:
 *  1. Normalizacion Unicode NFKC (ligaduras como "fi" pasan a dos letras).
 *  2. Minusculas.
 *  3. Tokenizacion: todo caracter que no sea letra (puntuacion, digitos, guiones,
 *     apostrofos, parentesis, barras) separa palabras. "AI-driven" -> "ai", "driven";
 *     "learners'" -> "learners"; los numeros desaparecen.
 *  4. Se descartan los tokens de una sola letra y las stopwords (data/stopwords_en.txt).
 * No se aplica stemming ni lematizacion: "learning" y "learners" son terminos distintos.
 *
 * La salida queda en ORDEN ALFABETICO para que los empates salgan iguales en los tres
 * algoritmos de ordenamiento (todos son estables).
 */
public class Preprocesador {

    private final Set<String> stopwords;

    public Preprocesador(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    /** Carga las stopwords de un archivo de texto (una por linea, '#' para comentarios). */
    public static Set<String> leerStopwords(Path archivo) throws IOException {
        Set<String> conjunto = new HashSet<>();
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            linea = linea.trim().toLowerCase(Locale.ROOT);
            if (!linea.isEmpty() && !linea.startsWith("#")) {
                conjunto.add(linea);
            }
        }
        return conjunto;
    }

    /** Terminos de un texto despues de normalizar, tokenizar y quitar stopwords. */
    public List<String> tokenizar(String texto) {
        String normal = Normalizer.normalize(texto, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        List<String> terminos = new ArrayList<>();
        for (String token : normal.split("[^\\p{L}]+")) {
            if (token.length() > 1 && !stopwords.contains(token)) {
                terminos.add(token);
            }
        }
        return terminos;
    }

    /** Frecuencia de cada termino en los abstracts de todos los articulos, en orden alfabetico. */
    public TermFrequency[] frecuencias(List<Articulo> articulos) {
        Map<String, Integer> conteo = new TreeMap<>();
        for (Articulo a : articulos) {
            for (String termino : tokenizar(a.getResumen())) {
                conteo.merge(termino, 1, Integer::sum);
            }
        }
        TermFrequency[] resultado = new TermFrequency[conteo.size()];
        int i = 0;
        for (Map.Entry<String, Integer> e : conteo.entrySet()) {
            resultado[i] = new TermFrequency(e.getKey(), e.getValue());
            i++;
        }
        return resultado;
    }
}

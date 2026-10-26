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
import java.util.Set;

/**
 * Preprocesamiento de los abstracts (responsable: Zuleta).
 *
 * Pasos, en este orden:
 *  1. Normalizacion Unicode NFKD y eliminacion de tildes y ligaduras ("ﬁ" -> "fi").
 *  2. Minusculas.
 *  3. Tokenizacion: un termino es una secuencia de letras a-z. Todo lo demas
 *     (signos de puntuacion, digitos, guiones, apostrofos) separa terminos, asi
 *     "AI-driven" da "ai" y "driven", y "ChatGPT's" da "chatgpt" y "s".
 *  4. Se descartan los terminos de una sola letra ("s", "e" de "e.g.", ...).
 *  5. Se descartan las stopwords del ingles (lista de data/stopwords_en.txt).
 * No se aplica lematizacion ni stemming: "study" y "studies" son terminos distintos.
 */
public final class TextPreprocessor {

    private static final int LARGO_MINIMO = 2;

    private final Set<String> stopwords;

    public TextPreprocessor(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    /** Lee una stopword por linea; ignora lineas vacias y comentarios "#". */
    public static Set<String> leerStopwords(Path archivo) throws IOException {
        Set<String> conjunto = new HashSet<>();
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            String s = linea.trim().toLowerCase(Locale.ROOT);
            if (!s.isEmpty() && !s.startsWith("#")) {
                conjunto.add(s);
            }
        }
        return conjunto;
    }

    /** Devuelve los terminos del texto, en orden de aparicion, ya filtrados. */
    public List<String> terminos(String texto) {
        String limpio = Normalizer.normalize(texto, Normalizer.Form.NFKD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT);
        List<String> resultado = new ArrayList<>();
        for (String token : limpio.split("[^a-z]+")) {
            if (token.length() >= LARGO_MINIMO && !stopwords.contains(token)) {
                resultado.add(token);
            }
        }
        return resultado;
    }
}

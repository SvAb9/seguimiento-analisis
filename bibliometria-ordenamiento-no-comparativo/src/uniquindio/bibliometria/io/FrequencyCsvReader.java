package uniquindio.bibliometria.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import uniquindio.bibliometria.model.TermFrequency;

/**
 * Lee un CSV "termino,frecuencia" (UTF-8, una pareja por linea, encabezado opcional)
 * y lo convierte en TermFrequency[]. Es el punto de integracion con la parte de Zuleta:
 * basta con que su modulo de preprocesamiento exporte este formato (ordenado
 * alfabeticamente para que los empates salgan iguales en los tres algoritmos).
 */
public final class FrequencyCsvReader {

    private FrequencyCsvReader() {
    }

    public static TermFrequency[] read(Path archivo) throws IOException {
        List<TermFrequency> lista = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                int coma = linea.lastIndexOf(',');
                if (coma < 0) {
                    continue;
                }
                String termino = linea.substring(0, coma).trim();
                try {
                    int frecuencia = Integer.parseInt(linea.substring(coma + 1).trim());
                    lista.add(new TermFrequency(termino, frecuencia));
                } catch (NumberFormatException e) {
                    // encabezado ("termino,frecuencia") u otra linea no numerica: se ignora
                }
            }
        }
        return lista.toArray(new TermFrequency[0]);
    }
}

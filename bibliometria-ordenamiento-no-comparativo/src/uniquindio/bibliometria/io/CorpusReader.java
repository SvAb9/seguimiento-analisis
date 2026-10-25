package uniquindio.bibliometria.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import uniquindio.bibliometria.model.Articulo;

/**
 * Lee data/corpus/abstracts.txt (generado por scripts/extraer_abstracts.py a partir de los 20 PDF).
 * Formato: bloques de cuatro lineas "@id", "@titulo", "@autores" (separados por ';') y "@abstract".
 */
public final class CorpusReader {

    private CorpusReader() {
    }

    public static List<Articulo> read(Path archivo) throws IOException {
        List<Articulo> articulos = new ArrayList<>();
        int id = -1;
        String titulo = null;
        List<String> autores = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("@id ")) {
                    id = Integer.parseInt(valor(linea));
                } else if (linea.startsWith("@titulo ")) {
                    titulo = valor(linea);
                } else if (linea.startsWith("@autores ")) {
                    autores = new ArrayList<>();
                    for (String autor : valor(linea).split(";")) {
                        autores.add(autor.trim());
                    }
                } else if (linea.startsWith("@abstract ")) {
                    articulos.add(new Articulo(id, titulo, autores, valor(linea)));
                }
            }
        }
        return articulos;
    }

    private static String valor(String linea) {
        return linea.substring(linea.indexOf(' ') + 1).trim();
    }
}

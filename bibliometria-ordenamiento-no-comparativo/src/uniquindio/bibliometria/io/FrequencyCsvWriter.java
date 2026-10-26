package uniquindio.bibliometria.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import uniquindio.bibliometria.model.TermFrequency;

/**
 * Escribe un TermFrequency[] como CSV "termino,frecuencia" (UTF-8 sin BOM, con
 * encabezado), en el mismo orden del arreglo. Es el formato que lee FrequencyCsvReader.
 */
public final class FrequencyCsvWriter {

    private FrequencyCsvWriter() {
    }

    public static void write(Path archivo, TermFrequency[] lista) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            bw.write("termino,frecuencia");
            bw.newLine();
            for (TermFrequency t : lista) {
                bw.write(t.getTerm() + "," + t.getFrequency());
                bw.newLine();
            }
        }
    }
}

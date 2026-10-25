package uniquindio.bibliometria.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import uniquindio.bibliometria.model.TermFrequency;

/** Escribe un CSV "termino,frecuencia" (UTF-8) que luego lee FrequencyCsvReader. */
public final class FrequencyCsvWriter {

    private FrequencyCsvWriter() {
    }

    public static void write(Path archivo, TermFrequency[] datos, String comentario) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            if (comentario != null) {
                bw.write("# " + comentario);
                bw.newLine();
            }
            bw.write("termino,frecuencia");
            bw.newLine();
            for (TermFrequency t : datos) {
                bw.write(t.getTerm() + "," + t.getFrequency());
                bw.newLine();
            }
        }
    }
}

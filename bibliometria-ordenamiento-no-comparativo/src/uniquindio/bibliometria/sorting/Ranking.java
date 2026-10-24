package uniquindio.bibliometria.sorting;

import uniquindio.bibliometria.model.TermFrequency;

/** Utilidades para presentar y validar el ranking (top-N) producido por cualquier algoritmo. */
public final class Ranking {

    private Ranking() {
    }

    /** Primeros k elementos de una lista ya ordenada (o todos si hay menos de k). */
    public static TermFrequency[] top(TermFrequency[] ordenado, int k) {
        int tam = Math.min(k, ordenado.length);
        TermFrequency[] resultado = new TermFrequency[tam];
        for (int i = 0; i < tam; i++) {
            resultado[i] = ordenado[i];
        }
        return resultado;
    }

    /** true si ambos rankings tienen los mismos terminos y frecuencias en las primeras k posiciones. */
    public static boolean mismoTop(TermFrequency[] a, TermFrequency[] b, int k) {
        TermFrequency[] ta = top(a, k);
        TermFrequency[] tb = top(b, k);
        if (ta.length != tb.length) {
            return false;
        }
        for (int i = 0; i < ta.length; i++) {
            if (!ta[i].getTerm().equals(tb[i].getTerm()) || ta[i].getFrequency() != tb[i].getFrequency()) {
                return false;
            }
        }
        return true;
    }

    /** Verifica que la lista este en orden descendente por frecuencia. */
    public static boolean esDescendente(TermFrequency[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1].getFrequency() < a[i].getFrequency()) {
                return false;
            }
        }
        return true;
    }

    /** Imprime una tabla "posicion | termino | frecuencia" en consola. */
    public static void imprimir(String titulo, TermFrequency[] lista) {
        System.out.println(titulo);
        for (int i = 0; i < lista.length; i++) {
            System.out.printf("%3d  %-24s %d%n", i + 1, lista[i].getTerm(), lista[i].getFrequency());
        }
    }
}

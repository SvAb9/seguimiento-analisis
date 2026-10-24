package uniquindio.bibliometria;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import uniquindio.bibliometria.model.TermFrequency;
import uniquindio.bibliometria.sorting.BucketSort;
import uniquindio.bibliometria.sorting.Ranking;

/**
 * Pruebas de BucketSort sin dependencias externas (no requiere JUnit).
 * Ejecutar: java -cp out uniquindio.bibliometria.BucketSortTest  (termina con codigo 1 si algo falla)
 */
public class BucketSortTest {

    private static int pruebas = 0;
    private static int fallos = 0;

    public static void main(String[] args) {
        casoVacio();
        casoUnElemento();
        casoTodosIguales();
        casoEjemploPequeno();
        casoEstabilidadEmpates();
        casoNoModificaEntrada();
        casoExtremosEnteros();
        casoNull();
        casoAleatoriosContraReferencia();
        casoAdversario();
        System.out.println(pruebas + " verificaciones, " + fallos + " fallos");
        System.exit(fallos == 0 ? 0 : 1);
    }

    private static TermFrequency[] ref(TermFrequency[] in) {
        TermFrequency[] copia = in.clone();
        Arrays.sort(copia, Comparator.comparingInt(TermFrequency::getFrequency).reversed()); // TimSort estable
        return copia;
    }

    private static boolean igual(TermFrequency[] a, TermFrequency[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {          // misma referencia: comprueba tambien la estabilidad
                return false;
            }
        }
        return true;
    }

    private static void check(String nombre, boolean ok) {
        pruebas++;
        if (!ok) {
            fallos++;
            System.out.println("FALLA: " + nombre);
        }
    }

    private static void casoVacio() {
        check("vacio", new BucketSort().sortDescending(new TermFrequency[0]).length == 0);
    }

    private static void casoUnElemento() {
        TermFrequency[] e = {new TermFrequency("a", 5)};
        check("un elemento", igual(new BucketSort().sortDescending(e), e));
    }

    private static void casoTodosIguales() {
        TermFrequency[] e = new TermFrequency[50];
        for (int i = 0; i < e.length; i++) {
            e[i] = new TermFrequency("t" + i, 3);
        }
        check("todos iguales (k=1)", igual(new BucketSort().sortDescending(e), e));
    }

    private static void casoEjemploPequeno() {
        int[] f = {51, 27, 8, 10, 16, 28};
        TermFrequency[] e = new TermFrequency[f.length];
        for (int i = 0; i < f.length; i++) {
            e[i] = new TermFrequency("w" + i, f[i]);
        }
        TermFrequency[] s = new BucketSort().sortDescending(e);
        int[] esperado = {51, 28, 27, 16, 10, 8};
        boolean ok = true;
        for (int i = 0; i < esperado.length; i++) {
            ok = ok && s[i].getFrequency() == esperado[i];
        }
        check("ejemplo del libro 51 27 8 10 16 28", ok);
    }

    private static void casoEstabilidadEmpates() {
        TermFrequency[] e = {
            new TermFrequency("a", 2), new TermFrequency("b", 9), new TermFrequency("c", 2),
            new TermFrequency("d", 9), new TermFrequency("e", 5), new TermFrequency("f", 2)};
        TermFrequency[] s = new BucketSort().sortDescending(e);
        String orden = "";
        for (TermFrequency t : s) {
            orden += t.getTerm();
        }
        check("empates conservan orden de entrada (bdeacf)", orden.equals("bdeacf"));
    }

    private static void casoNoModificaEntrada() {
        TermFrequency[] e = {new TermFrequency("x", 1), new TermFrequency("y", 8), new TermFrequency("z", 4)};
        TermFrequency[] copia = e.clone();
        new BucketSort().sortDescending(e);
        check("no modifica la entrada", igual(e, copia));
    }

    private static void casoExtremosEnteros() {
        TermFrequency[] e = {new TermFrequency("min", Integer.MIN_VALUE), new TermFrequency("max", Integer.MAX_VALUE),
            new TermFrequency("cero", 0), new TermFrequency("neg", -5)};
        TermFrequency[] s = new BucketSort().sortDescending(e);
        check("extremos enteros sin desbordamiento", igual(s, ref(e)));
    }

    private static void casoNull() {
        boolean lanzo = false;
        try {
            new BucketSort().sortDescending(null);
        } catch (IllegalArgumentException ex) {
            lanzo = true;
        }
        check("null lanza IllegalArgumentException", lanzo);
    }

    private static void casoAleatoriosContraReferencia() {
        Random rnd = new Random(42);
        boolean ok = true;
        for (int prueba = 0; prueba < 300; prueba++) {
            int n = rnd.nextInt(400);
            int tope = 1 + rnd.nextInt(prueba % 2 == 0 ? 30 : 100000);
            TermFrequency[] e = new TermFrequency[n];
            for (int i = 0; i < n; i++) {
                e[i] = new TermFrequency("t" + i, rnd.nextInt(tope));
            }
            TermFrequency[] s = new BucketSort().sortDescending(e);
            ok = ok && igual(s, ref(e)) && Ranking.esDescendente(s);
        }
        check("300 entradas aleatorias iguales a la referencia estable", ok);
    }

    private static void casoAdversario() {
        int n = 2000;
        TermFrequency[] e = new TermFrequency[n];
        for (int i = 0; i < n - 1; i++) {
            e[i] = new TermFrequency("t" + i, i + 1);
        }
        e[n - 1] = new TermFrequency("outlier", n * n);
        check("caso adversario correcto (aunque O(n^2))", igual(new BucketSort().sortDescending(e), ref(e)));
    }
}

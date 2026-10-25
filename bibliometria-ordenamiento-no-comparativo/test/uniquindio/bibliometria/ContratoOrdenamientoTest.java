package uniquindio.bibliometria;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import uniquindio.bibliometria.model.TermFrequency;
import uniquindio.bibliometria.sorting.BucketSort;
import uniquindio.bibliometria.sorting.CountingSort;
import uniquindio.bibliometria.sorting.FrequencySorter;
import uniquindio.bibliometria.sorting.RadixSort;
import uniquindio.bibliometria.sorting.Ranking;

/**
 * Pruebas del contrato comun para Counting Sort y Radix Sort (y casos propios de Radix),
 * en el mismo estilo de BucketSortTest: sin JUnit, termina con codigo 1 si algo falla.
 * Ejecutar: java -cp out uniquindio.bibliometria.ContratoOrdenamientoTest
 */
public class ContratoOrdenamientoTest {

    private static int pruebas = 0;
    private static int fallos = 0;

    public static void main(String[] args) {
        FrequencySorter[] sorters = {new CountingSort(), new RadixSort(), new RadixSort(2), new RadixSort(16), new RadixSort(256)};
        for (FrequencySorter s : sorters) {
            String nombre = s.getName() + (s instanceof RadixSort ? " b=" + ((RadixSort) s).getBase() : "");
            casoVacio(nombre, s);
            casoUnElemento(nombre, s);
            casoTodosIguales(nombre, s);
            casoEjemploPequeno(nombre, s);
            casoEstabilidadEmpates(nombre, s);
            casoNoModificaEntrada(nombre, s);
            casoNull(nombre, s);
            casoAleatoriosContraReferencia(nombre, s);
            casoNegativos(nombre, s);
        }
        casoExtremosEnterosRadix();
        casoRangoExcesivoCounting();
        casoEjemploRadixLibro();
        casoBaseInvalida();
        casoTresAlgoritmosIguales();
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

    private static TermFrequency[] crear(int... f) {
        TermFrequency[] e = new TermFrequency[f.length];
        for (int i = 0; i < f.length; i++) {
            e[i] = new TermFrequency("w" + i, f[i]);
        }
        return e;
    }

    private static void casoVacio(String nombre, FrequencySorter s) {
        check(nombre + ": vacio", s.sortDescending(new TermFrequency[0]).length == 0);
    }

    private static void casoUnElemento(String nombre, FrequencySorter s) {
        TermFrequency[] e = crear(5);
        check(nombre + ": un elemento", igual(s.sortDescending(e), e));
    }

    private static void casoTodosIguales(String nombre, FrequencySorter s) {
        TermFrequency[] e = new TermFrequency[50];
        for (int i = 0; i < e.length; i++) {
            e[i] = new TermFrequency("t" + i, 3);
        }
        check(nombre + ": todos iguales (k=1)", igual(s.sortDescending(e), e));
    }

    private static void casoEjemploPequeno(String nombre, FrequencySorter s) {
        TermFrequency[] e = crear(51, 27, 8, 10, 16, 28);
        check(nombre + ": ejemplo 51 27 8 10 16 28", igual(s.sortDescending(e), ref(e)));
    }

    private static void casoEstabilidadEmpates(String nombre, FrequencySorter s) {
        TermFrequency[] e = {
            new TermFrequency("a", 2), new TermFrequency("b", 9), new TermFrequency("c", 2),
            new TermFrequency("d", 9), new TermFrequency("e", 5), new TermFrequency("f", 2)};
        StringBuilder orden = new StringBuilder();
        for (TermFrequency t : s.sortDescending(e)) {
            orden.append(t.getTerm());
        }
        check(nombre + ": empates conservan orden de entrada (bdeacf)", orden.toString().equals("bdeacf"));
    }

    private static void casoNoModificaEntrada(String nombre, FrequencySorter s) {
        TermFrequency[] e = crear(1, 8, 4, 120, 7);
        TermFrequency[] copia = e.clone();
        TermFrequency[] salida = s.sortDescending(e);
        check(nombre + ": no modifica la entrada y devuelve arreglo nuevo", igual(e, copia) && salida != e);
    }

    private static void casoNull(String nombre, FrequencySorter s) {
        boolean lanzo = false;
        try {
            s.sortDescending(null);
        } catch (IllegalArgumentException ex) {
            lanzo = true;
        }
        check(nombre + ": null lanza IllegalArgumentException", lanzo);
    }

    private static void casoAleatoriosContraReferencia(String nombre, FrequencySorter s) {
        Random rnd = new Random(42);
        boolean ok = true;
        for (int prueba = 0; prueba < 300; prueba++) {
            int n = rnd.nextInt(400);
            int tope = 1 + rnd.nextInt(prueba % 2 == 0 ? 30 : 100000);
            TermFrequency[] e = new TermFrequency[n];
            for (int i = 0; i < n; i++) {
                e[i] = new TermFrequency("t" + i, rnd.nextInt(tope));
            }
            TermFrequency[] r = s.sortDescending(e);
            ok = ok && igual(r, ref(e)) && Ranking.esDescendente(r);
        }
        check(nombre + ": 300 entradas aleatorias iguales a la referencia estable", ok);
    }

    private static void casoNegativos(String nombre, FrequencySorter s) {
        TermFrequency[] e = crear(-3, 0, 12, -40, 7, 0, -3, 999);
        check(nombre + ": valores negativos y cero", igual(s.sortDescending(e), ref(e)));
    }

    private static void casoExtremosEnterosRadix() {
        TermFrequency[] e = {new TermFrequency("min", Integer.MIN_VALUE), new TermFrequency("max", Integer.MAX_VALUE),
            new TermFrequency("cero", 0), new TermFrequency("neg", -5), new TermFrequency("max2", Integer.MAX_VALUE)};
        boolean ok = true;
        for (int b : new int[] {2, 10, 256, 65536}) {
            ok = ok && igual(new RadixSort(b).sortDescending(e), ref(e));
        }
        check("Radix: extremos enteros sin desbordamiento (bases 2, 10, 256, 65536)", ok);
    }

    private static void casoRangoExcesivoCounting() {
        TermFrequency[] e = {new TermFrequency("min", Integer.MIN_VALUE), new TermFrequency("max", Integer.MAX_VALUE)};
        boolean lanzo = false;
        try {
            new CountingSort().sortDescending(e);
        } catch (IllegalArgumentException ex) {
            lanzo = true;
        }
        check("Counting: rango k > 2^31 se rechaza con IllegalArgumentException", lanzo);
    }

    /** Ejemplo de la tabla de Radix Sort del libro de clase (base 10, tres digitos). */
    private static void casoEjemploRadixLibro() {
        TermFrequency[] e = crear(984, 547, 551, 125, 762, 34, 330, 5, 711, 98);
        TermFrequency[] s = new RadixSort().sortDescending(e);
        int[] esperado = {984, 762, 711, 551, 547, 330, 125, 98, 34, 5};
        boolean ok = true;
        for (int i = 0; i < esperado.length; i++) {
            ok = ok && s[i].getFrequency() == esperado[i];
        }
        check("Radix: ejemplo de varios digitos (984 547 551 ...)", ok);
    }

    private static void casoBaseInvalida() {
        int lanzadas = 0;
        for (int b : new int[] {-1, 0, 1, 65537}) {
            try {
                new RadixSort(b);
            } catch (IllegalArgumentException ex) {
                lanzadas++;
            }
        }
        check("Radix: bases fuera de [2, 65536] se rechazan", lanzadas == 4);
    }

    /** Con entrada alfabetica, los tres algoritmos deben dar exactamente el mismo ranking completo. */
    private static void casoTresAlgoritmosIguales() {
        Random rnd = new Random(2026);
        int n = 3000;
        TermFrequency[] e = new TermFrequency[n];
        for (int i = 0; i < n; i++) {
            int f = Math.max(1, (int) (600 / Math.pow(i + 1, 0.9) * (0.5 + rnd.nextDouble())));
            e[i] = new TermFrequency(String.format("t%05d", i), f);
        }
        TermFrequency[] c = new CountingSort().sortDescending(e);
        TermFrequency[] r = new RadixSort().sortDescending(e);
        TermFrequency[] b = new BucketSort().sortDescending(e);
        check("Counting, Radix y Bucket producen el mismo orden completo", igual(c, r) && igual(r, b));
    }
}

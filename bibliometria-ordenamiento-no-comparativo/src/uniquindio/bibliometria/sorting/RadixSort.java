package uniquindio.bibliometria.sorting;

import uniquindio.bibliometria.model.TermFrequency;

/**
 * Algoritmo 2 - Radix Sort LSD (responsable: Natalia).
 *
 * Ordena de mayor a menor frecuencia una lista de terminos.
 *
 * Idea:
 *  - Clave de cada termino: x = f - min (entero >= 0, cabe en long aunque f sea negativo).
 *  - Se ordena por digitos en base b, del MENOS al MAS significativo (LSD).
 *  - Cada pasada es un Counting Sort estable sobre un solo digito (valores 0..b-1),
 *    con acumulacion de mayor a menor para que la salida quede descendente.
 *  - La estabilidad de cada pasada garantiza la correccion: al ordenar por el digito j,
 *    los elementos con el mismo digito j conservan el orden logrado por los digitos 0..j-1.
 *  - Numero de pasadas d = cantidad de digitos de (max - min) en base b (0 pasadas si k = 1).
 *
 * Complejidad (ver informe, seccion 3.4):
 *  - T(n) = d(22n + 6b + 10) + 6n + 5 en el esquema analizado  ->  O(d(n + b)).
 *  - b es una constante (10 por defecto) y d = floor(log_b(k - 1)) + 1, asi que
 *    con k acotado el algoritmo es O(n). Espacio adicional O(n + b).
 *  - Las frecuencias del corpus ocupan pocos digitos, por eso basta con base 10.
 */
public class RadixSort implements FrequencySorter {

    public static final int BASE_POR_DEFECTO = 10;

    private final int base;

    public RadixSort() {
        this(BASE_POR_DEFECTO);
    }

    /** @param base base numerica de los digitos, entre 2 y 65536 */
    public RadixSort(int base) {
        if (base < 2 || base > 65536) {
            throw new IllegalArgumentException("La base debe estar entre 2 y 65536");
        }
        this.base = base;
    }

    public int getBase() {
        return base;
    }

    @Override
    public String getName() {
        return "Radix Sort";
    }

    @Override
    public TermFrequency[] sortDescending(TermFrequency[] entrada) {
        if (entrada == null) {
            throw new IllegalArgumentException("La entrada no puede ser null");
        }
        int n = entrada.length;
        TermFrequency[] origen = entrada.clone();   // se trabaja sobre una copia: la entrada no cambia
        if (n < 2) {
            return origen;
        }

        // Paso 1: frecuencia minima y maxima -> mayor clave x = max - min
        int min = entrada[0].getFrequency();
        int max = min;
        for (int i = 1; i < n; i++) {
            int f = entrada[i].getFrequency();
            if (f < min) {
                min = f;
            }
            if (f > max) {
                max = f;
            }
        }
        long mayorClave = (long) max - min;

        // Paso 2: una pasada de Counting Sort estable por cada digito (LSD)
        TermFrequency[] destino = new TermFrequency[n];
        int[] c = new int[base];
        for (long exp = 1; mayorClave / exp > 0; exp *= base) {
            // 2a) contar el digito de cada clave
            for (int d = 0; d < base; d++) {
                c[d] = 0;
            }
            for (int i = 0; i < n; i++) {
                c[digito(origen[i], min, exp)]++;
            }
            // 2b) acumular de mayor a menor digito (orden descendente)
            for (int d = base - 2; d >= 0; d--) {
                c[d] += c[d + 1];
            }
            // 2c) colocar de derecha a izquierda (estable)
            for (int i = n - 1; i >= 0; i--) {
                int d = digito(origen[i], min, exp);
                c[d]--;
                destino[c[d]] = origen[i];
            }
            // 2d) la salida de esta pasada es la entrada de la siguiente
            TermFrequency[] tmp = origen;
            origen = destino;
            destino = tmp;
        }
        return origen;
    }

    /** Digito de la clave (f - min) en la posicion exp = base^j. */
    private int digito(TermFrequency t, int min, long exp) {
        return (int) ((((long) t.getFrequency() - min) / exp) % base);
    }
}

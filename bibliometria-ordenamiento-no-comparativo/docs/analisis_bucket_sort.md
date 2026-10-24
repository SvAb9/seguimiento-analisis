# Análisis de tiempo de ejecución – Bucket Sort

Método de clase: se cuentan las operaciones elementales (asignaciones, aritmética, comparaciones, accesos `[]`,
`++`/`--`, `new`, parámetros, `return`); no se cuentan declaraciones ni conversiones de tipo. `getFrequency()` cuenta 1.
Para los `if` se toma el peor caso. Notación: `n` términos, `k = max − min + 1`, `m = min(n, k)` cubetas,
`e` cubetas no vacías, `S` desplazamientos totales de la inserción.

| Bloque de `sortDescending` | Operaciones |
|---|---|
| Parámetro, validaciones, `n`, `salida`, `if (n < 2)` | 7 |
| Paso 1: mínimo y máximo | 9n − 3 |
| Paso 2: `rango`, `m` | 6 |
| Paso 3: índice y tamaño de cubetas | 12n + 6 |
| Paso 4: crear cubetas | 6m + 4 |
| Paso 5: distribuir | 11n + 4 |
| Paso 6: ordenar cubetas (sin contar la inserción) | 4m + 2 |
| Paso 7: concatenar | 9n + 6m + 4 |
| `return` | 1 |

Inserción en una cubeta de tamaño `b ≥ 1` con `s` desplazamientos: `15b − 12 + 11s` (cubeta vacía: 3).
Sumando todas las cubetas: `15n + 3m − 15e + 11S`.

**T(n) = 56n + 19m − 15e + 31 + 11S**

* `k ≤ n` → `m = k`, cada cubeta guarda una única frecuencia, `S = 0` → `T ≤ 56n + 19k + 31` → **O(n + k)**.
* `k > n`, frecuencias uniformes → `E[S] = n(n−1)/(2m)`; con `m = n` es `(n−1)/2` → **O(n)** esperado.
* Peor caso: `S = (n−1)(n−2)/2` → `T ≈ 5,5n²` → **O(n²)**.
* Espacio adicional: **O(n + m)**.

La fórmula se verificó simulando el conteo sobre entradas aleatorias (coincide exactamente).

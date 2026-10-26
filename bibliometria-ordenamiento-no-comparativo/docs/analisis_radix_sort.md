# Análisis de tiempo de ejecución – Radix Sort

Mismas convenciones que `analisis_bucket_sort.md`: se cuentan asignaciones, aritmética, comparaciones, accesos `[]`,
`++`/`--`, `new`, parámetros y `return`; `getFrequency()` cuenta 1; las conversiones de tipo y las declaraciones no se
cuentan. La llamada `digito(t, min, exp)` cuesta 8 (3 parámetros, `getFrequency`, resta, división, módulo y `return`).
`entrada.clone()` se cuenta como `n + 1`. Notación: `n` términos, `b` base, `d` pasadas = dígitos de `max − min` en base `b`.

| Bloque de `sortDescending` (n ≥ 2) | Operaciones |
|---|---|
| Parámetro y validación | 2 |
| `n`, copia de la entrada, `if (n < 2)` | n + 4 |
| Paso 1: mínimo y máximo | 9n − 3 |
| `mayorClave`, `destino`, `c` | 6 |
| Inicio de `exp` y última condición del `for` | 3 |
| Por pasada: condición y `exp *= base` | 4 |
| Por pasada: reiniciar `c` | 4b + 2 |
| Por pasada: contar dígitos | 13n + 2 |
| Por pasada: acumular de mayor a menor | 7b − 4 |
| Por pasada: colocar de derecha a izquierda | 18n + 3 |
| Por pasada: intercambiar `origen` y `destino` | 3 |
| `return` | 1 |

**T(n) = d(31n + 11b + 10) + 10n + 13** → **O(d·(n + b))**; con `b` constante y rango acotado, **O(n)**.
Espacio adicional **O(n + b)**.

* Corpus: n = 984, k = 86, b = 10 → d = 2 → T = 71.101 operaciones.
* La fórmula se verificó simulando el conteo sobre 8 entradas aleatorias (n entre 12 y 492, bases 2, 10, 16 y 256,
  de 1 a 7 pasadas): coincide exactamente.

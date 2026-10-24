# Ranking de términos con ordenamiento no comparativo – Grupo 6

Seguimiento 2026-2 · *Análisis de algoritmos en el contexto de la bibliometría* · Universidad del Quindío

Este proyecto contiene el **esqueleto común** del requerimiento "Ranking de términos mediante algoritmos de
ordenamiento" y la parte de **Sara** terminada: **Bucket Sort**, su análisis de complejidad y las pruebas.
Las partes de Zuleta y Natalia están marcadas con `TODO` para que puedan continuar sin tocar lo demás.

## Estructura

```
bibliometria-ordenamiento-no-comparativo/
├── src/uniquindio/bibliometria/
│   ├── model/TermFrequency.java        # (término, frecuencia) – contrato común
│   ├── sorting/FrequencySorter.java    # interfaz que implementan los 3 algoritmos
│   ├── sorting/BucketSort.java         # ✅ Sara
│   ├── sorting/CountingSort.java       # ⏳ Zuleta (TODO)
│   ├── sorting/RadixSort.java          # ⏳ Natalia (TODO)
│   ├── sorting/Ranking.java            # top-N, comparar rankings, imprimir
│   ├── io/FrequencyCsvReader.java      # lee "termino,frecuencia"
│   └── app/DemoBucketSort.java         # ejecuta Bucket Sort y muestra el top-15
│   └── app/EscalamientoBucketSort.java # comprobación empírica de la complejidad
├── test/uniquindio/bibliometria/BucketSortTest.java
├── data/frecuencias_ejemplo.csv        # DATOS SINTÉTICOS (no son del corpus real)
├── docs/analisis_bucket_sort.md        # conteo de operaciones T(n) de Bucket Sort
├── docs/resultados_escalamiento.csv
├── informe/                            # informe en la plantilla (secciones de otros en azul)
└── scripts/compilar_y_probar.sh|.bat
```

## Compilar y probar

Requiere JDK 11 o superior (se probó con 21). Sin librerías externas.

```bash
./scripts/compilar_y_probar.sh        # Linux / macOS
scripts\compilar_y_probar.bat         # Windows
```

Hace `javac`, corre `BucketSortTest` (debe decir `0 fallos`) y la demo. Para la comprobación de escalamiento:
`java -cp out uniquindio.bibliometria.app.EscalamientoBucketSort`.

## Contrato común (importante para que los 3 algoritmos coincidan)

Todo algoritmo implementa `FrequencySorter.sortDescending(TermFrequency[])` y debe:

1. Ordenar de **mayor a menor** frecuencia (posición 0 = término más frecuente).
2. Ser **estable**: ante empates conserva el orden de la entrada.
3. **No modificar** el arreglo recibido (devuelve uno nuevo).
4. No usar comparaciones entre elementos como mecanismo principal de ordenamiento.

Como los tres son estables, **si la lista de entrada está en orden alfabético los empates salen alfabéticos en los
tres** y el top-15 de cada algoritmo es idéntico. `Ranking.mismoTop(a, b, 15)` lo verifica.

## Para Zuleta

- Exportar el resultado del preprocesamiento como CSV UTF-8 `termino,frecuencia` **ordenado alfabéticamente**
  (o construir directamente un `TermFrequency[]`). `FrequencyCsvReader.read(path)` ya lo carga.
- Implementar `CountingSort` (hoy lanza `UnsupportedOperationException`). Puede ser el esquema de `docs/analisis_bucket_sort.md`
  (secciones 3.3 del informe) adaptado a `TermFrequency[]`; recuerde recorrer la entrada de derecha a izquierda para ser estable.
- Entregar para el informe: número de términos distintos `n`, frecuencia mínima y máxima del corpus (para `k`).

## Para Natalia

- Implementar `RadixSort` (LSD, estable). Sugerencia: base 10 y una pasada de Counting Sort por dígito.
- Medir en milisegundos los **tres** algoritmos con la lista de frecuencias real (varias repeticiones + calentamiento de la JVM;
  `EscalamientoBucketSort.medir` es un ejemplo de cómo hacerlo con mediana de 7 ejecuciones).
- Generar la tabla/gráfico del top-15 por algoritmo y pasar los tiempos a Sara para el análisis comparativo.

## Notas sobre Bucket Sort

- Usa `m = min(n, k)` cubetas, con `k = max − min + 1`. Si `k ≤ n` cada cubeta contiene una sola frecuencia → **O(n + k)** sin importar la distribución.
- Si `k > n` y las frecuencias son parejas → O(n) esperado; el peor caso (todo en una cubeta, en orden inverso) es O(n²).
- La inserción dentro de cada cubeta es la única comparación del algoritmo y solo actúa sobre cubetas pequeñas.
- Los datos de `data/frecuencias_ejemplo.csv` y los de `EscalamientoBucketSort` son **sintéticos**.

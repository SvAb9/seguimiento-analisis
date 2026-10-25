# Ranking de términos con ordenamiento no comparativo – Grupo 6

Seguimiento 2026-2 · *Análisis de algoritmos en el contexto de la bibliometría* · Universidad del Quindío

Implementación completa del requerimiento "Ranking de términos mediante algoritmos de ordenamiento": extracción de
los abstracts de los 20 artículos, preprocesamiento, **Counting Sort**, **Radix Sort** y **Bucket Sort**, medición de
tiempos sobre el corpus real y validación del top-15.

## Resultados sobre el corpus (20 abstracts)

- 2.500 palabras tras el preprocesamiento, **n = 984** términos distintos, frecuencias entre 1 y 86 → **k = 86** (k ≤ n).
- Los tres algoritmos producen **el mismo orden completo** (984 posiciones). Top-15: ai (86), education (57),
  research (46), learning (40), aied (38), review (36), educational (33), assessment (32), intelligence (23),
  artificial (22), studies (21), higher (16), literature (15), study (15), systematic (15).
- Mediana del tiempo (i7-1255U, OpenJDK 25): Counting ≈ 0,003 ms, Bucket ≈ 0,0075 ms, Radix ≈ 0,018 ms
  (`docs/tiempos_corpus.csv`; 5 ejecuciones independientes en `docs/repeticiones_corpus.csv`). Al volver a ejecutar
  `MedicionCorpus` estos archivos se sobrescriben y los valores cambian un poco.

## Estructura

```
bibliometria-ordenamiento-no-comparativo/
├── src/uniquindio/bibliometria/
│   ├── model/TermFrequency.java        # (término, frecuencia) – contrato común
│   ├── sorting/FrequencySorter.java    # interfaz que implementan los 3 algoritmos
│   ├── model/Articulo.java             # id, título, autores y abstract de un artículo
│   ├── sorting/BucketSort.java         # Sara
│   ├── sorting/CountingSort.java       # Counting Sort estable, descendente
│   ├── sorting/RadixSort.java          # Natalia: Radix Sort LSD, base configurable (10 por defecto)
│   ├── sorting/Ranking.java            # top-N, comparar rankings, imprimir
│   ├── preprocessing/Preprocesador.java# tokenización, stopwords y frecuencias (orden alfabético)
│   ├── io/CorpusReader.java            # lee data/corpus/abstracts.txt
│   ├── io/FrequencyCsvReader.java      # lee "termino,frecuencia"
│   ├── io/FrequencyCsvWriter.java      # escribe "termino,frecuencia"
│   ├── app/GenerarFrecuencias.java     # abstracts -> data/frecuencias_corpus.csv (+ n, mín, máx, k)
│   ├── app/MedicionCorpus.java         # tiempos (ms) de los 3 algoritmos + top-15 sobre el corpus
│   ├── app/EscalamientoOrdenamientos.java # crecimiento de los 3 algoritmos al duplicar n (sintético)
│   ├── app/DemoBucketSort.java         # ejecuta Bucket Sort y muestra el top-15
│   └── app/EscalamientoBucketSort.java # comprobación empírica de la complejidad de Bucket Sort
├── test/uniquindio/bibliometria/
│   ├── BucketSortTest.java
│   └── ContratoOrdenamientoTest.java   # contrato común para Counting y Radix (+ casos de Radix)
├── data/corpus/abstracts.txt           # título, autores y abstract de los 20 artículos
├── data/stopwords_en.txt               # stopwords en inglés (NLTK + adicionales)
├── data/frecuencias_corpus.csv         # frecuencias reales del corpus (orden alfabético)
├── data/frecuencias_ejemplo.csv        # DATOS SINTÉTICOS (no son del corpus real)
├── docs/analisis_bucket_sort.md        # conteo de operaciones T(n) de Bucket Sort
├── docs/analisis_radix_sort.md         # conteo de operaciones T(n) de Radix Sort
├── docs/tiempos_corpus.csv, docs/top15_corpus.csv, docs/repeticiones_corpus.csv, docs/salida_medicion_corpus.txt
├── docs/escalamiento_ordenamientos.csv, docs/resultados_escalamiento.csv
├── docs/fig_*.png                      # figuras del informe (scripts/graficos.py)
├── informe/                            # informe en la plantilla
└── scripts/
    ├── compilar_y_probar.sh|.bat       # compila, prueba, genera frecuencias y mide
    ├── extraer_abstracts.py            # PDF -> data/corpus/abstracts.txt (requiere pdftotext)
    └── graficos.py                     # CSV de docs/ -> figuras PNG (requiere matplotlib)
```

## Compilar y probar

Requiere JDK 11 o superior (se probó con 21). Sin librerías externas.

```bash
./scripts/compilar_y_probar.sh        # Linux / macOS
scripts\compilar_y_probar.bat         # Windows
```

Hace `javac`, corre `BucketSortTest` y `ContratoOrdenamientoTest` (deben decir `0 fallos`), genera
`data/frecuencias_corpus.csv` a partir de los abstracts y ejecuta `MedicionCorpus`, que mide los tres algoritmos y
escribe `docs/tiempos_corpus.csv` y `docs/top15_corpus.csv`. Otros programas:

```bash
java -cp out uniquindio.bibliometria.app.EscalamientoOrdenamientos > docs/escalamiento_ordenamientos.csv
java -cp out uniquindio.bibliometria.app.EscalamientoBucketSort
python3 scripts/graficos.py                     # figuras (requiere matplotlib)
python3 scripts/extraer_abstracts.py "<carpeta de los PDF>" data/corpus/abstracts.txt   # regenerar el corpus
```

## Contrato común (importante para que los 3 algoritmos coincidan)

Todo algoritmo implementa `FrequencySorter.sortDescending(TermFrequency[])` y debe:

1. Ordenar de **mayor a menor** frecuencia (posición 0 = término más frecuente).
2. Ser **estable**: ante empates conserva el orden de la entrada.
3. **No modificar** el arreglo recibido (devuelve uno nuevo).
4. No usar comparaciones entre elementos como mecanismo principal de ordenamiento.

Como los tres son estables, **si la lista de entrada está en orden alfabético los empates salen alfabéticos en los
tres** y el top-15 de cada algoritmo es idéntico. `Ranking.mismoTop(a, b, 15)` lo verifica.

## Pipeline

1. **Extracción** (`scripts/extraer_abstracts.py`): `pdftotext` de las 2 primeras páginas, normalización NFKC, recorte
   del abstract entre una frase inicial y una final fijadas por artículo (cada revista maqueta distinto) y revisión
   manual. Título y autores transcritos de la primera página.
2. **Preprocesamiento** (`Preprocesador`): minúsculas, todo lo que no es letra separa palabras, se quitan tokens de una
   letra y stopwords; sin stemming. Frecuencias sobre los 20 abstracts, en orden alfabético.
3. **Ordenamiento**: los tres algoritmos sobre el mismo `TermFrequency[]`; como son estables y la entrada es alfabética,
   los empates salen iguales (hay 4 términos con frecuencia 15 y "technologies" queda 16.º en los tres).
4. **Medición** (`MedicionCorpus`): 1 ejecución en frío, 2.000 de calentamiento y 501 rondas alternando el orden de los
   algoritmos; mínimo, mediana, media, desviación y máximo en ms.

## Notas sobre Radix Sort

- LSD con una pasada de Counting Sort estable por dígito; la acumulación va de mayor a menor dígito para que la salida
  quede descendente sin invertirla.
- Clave `x = f − min` (en `long`): el número de pasadas depende del rango k y no del valor absoluto, y admite negativos.
- `T(n) = d(31n + 11b + 10) + 10n + 13` → O(d(n + b)); con el corpus (k = 86, b = 10) son d = 2 pasadas.
  Detalle en `docs/analisis_radix_sort.md`.

## Notas sobre Bucket Sort

- Usa `m = min(n, k)` cubetas, con `k = max − min + 1`. Si `k ≤ n` cada cubeta contiene una sola frecuencia → **O(n + k)** sin importar la distribución.
- Si `k > n` y las frecuencias son parejas → O(n) esperado; el peor caso (todo en una cubeta, en orden inverso) es O(n²).
- La inserción dentro de cada cubeta es la única comparación del algoritmo y solo actúa sobre cubetas pequeñas.
- Los datos de `data/frecuencias_ejemplo.csv` y los de `EscalamientoBucketSort` son **sintéticos**.

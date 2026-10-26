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
│   ├── sorting/CountingSort.java       # ✅ Zuleta
│   ├── sorting/RadixSort.java          # ⏳ Natalia (TODO)
│   ├── sorting/Ranking.java            # top-N, comparar rankings, imprimir
│   ├── io/FrequencyCsvReader.java      # lee "termino,frecuencia"
│   ├── io/FrequencyCsvWriter.java      # escribe "termino,frecuencia"
│   ├── model/Article.java              # título, autores y abstract de un artículo
│   ├── extraction/ArticleExtractor.java    # ✅ Zuleta – lee los PDF con PDFBox
│   ├── preprocessing/TextPreprocessor.java # ✅ Zuleta – minúsculas, tokens, stopwords
│   ├── app/ConstruirCorpus.java        # PDF -> data/frecuencias_corpus.csv
│   ├── app/DemoBucketSort.java         # ejecuta Bucket Sort y muestra el top-15
│   └── app/EscalamientoBucketSort.java # comprobación empírica de la complejidad
├── test/uniquindio/bibliometria/BucketSortTest.java
├── test/uniquindio/bibliometria/CountingSortTest.java
├── lib/pdfbox-app-3.0.3.jar            # Apache PDFBox (única librería externa)
├── corpus/pdf/                         # los 20 PDF del docente (no se suben al repo)
├── corpus/abstracts/                   # abstract extraído de cada artículo (NN.txt)
├── data/frecuencias_corpus.csv         # frecuencias REALES del corpus, orden alfabético
├── data/articulos.csv                  # id, archivo, título, autores de cada artículo
├── data/stopwords_en.txt               # stopwords del inglés (NLTK)
├── data/frecuencias_ejemplo.csv        # DATOS SINTÉTICOS (no son del corpus real)
├── docs/analisis_bucket_sort.md        # conteo de operaciones T(n) de Bucket Sort
├── docs/resultados_escalamiento.csv
├── informe/                            # informe en la plantilla (secciones de otros en azul)
└── scripts/compilar_y_probar.sh|.bat
```

## Compilar y probar

Requiere JDK 11 o superior (se probó con 21). La única librería externa es Apache PDFBox, incluida en `lib/`.

```bash
./scripts/compilar_y_probar.sh        # Linux / macOS
scripts\compilar_y_probar.bat         # Windows
```

Hace `javac`, corre `BucketSortTest` y `CountingSortTest` (deben decir `0 fallos`) y la demo. Para la comprobación
de escalamiento: `java -cp out uniquindio.bibliometria.app.EscalamientoBucketSort`.

## Construir la lista de frecuencias del corpus

Copie los 20 PDF del docente en `corpus/pdf/` (conservando el número inicial del nombre, `1. ...pdf` a `20. ...pdf`) y,
después de compilar:

```bash
java -cp "out:lib/*" uniquindio.bibliometria.app.ConstruirCorpus     # Linux / macOS
java -cp "out;lib/*" uniquindio.bibliometria.app.ConstruirCorpus     # Windows
```

Genera `data/frecuencias_corpus.csv`, `data/articulos.csv` y `corpus/abstracts/NN.txt`, e imprime `n`, la frecuencia
mínima y máxima, `k` y el top-15. Resultado actual: 20 artículos, 2.528 términos tras el preprocesamiento,
**n = 992** términos distintos, frecuencias entre 1 y 86 (**k = 86**).

## Contrato común (importante para que los 3 algoritmos coincidan)

Todo algoritmo implementa `FrequencySorter.sortDescending(TermFrequency[])` y debe:

1. Ordenar de **mayor a menor** frecuencia (posición 0 = término más frecuente).
2. Ser **estable**: ante empates conserva el orden de la entrada.
3. **No modificar** el arreglo recibido (devuelve uno nuevo).
4. No usar comparaciones entre elementos como mecanismo principal de ordenamiento.

Como los tres son estables, **si la lista de entrada está en orden alfabético los empates salen alfabéticos en los
tres** y el top-15 de cada algoritmo es idéntico. `Ranking.mismoTop(a, b, 15)` lo verifica.

## Parte de Zuleta (terminada)

- `CountingSort`: clave `x = f − mín`, contadores acumulados de la clave mayor a la menor (así sale descendente sin
  invertir) y colocación de derecha a izquierda (estable). O(n + k).
- Extracción: el abstract empieza en "A B S T R A C T" / "Abstract" y termina en "Keywords", en la introducción o, en
  Elsevier, al cerrar el párrafo. Se unen las palabras partidas en sílabas al final de línea. Los 20 abstracts se
  revisaron a mano en `corpus/abstracts/`.
- Preprocesamiento: NFKD sin tildes, minúsculas, términos = secuencias de letras `a-z`, se descartan los de una letra y
  las stopwords de `data/stopwords_en.txt`. Sin lematización.
- Los autores salen de los metadatos del PDF y en varios artículos están incompletos (no se usan en el ranking).

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

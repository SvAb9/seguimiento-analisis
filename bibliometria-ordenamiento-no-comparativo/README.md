# Ranking de términos con ordenamiento no comparativo – Grupo 6

Seguimiento 2026-2 · *Análisis de algoritmos en el contexto de la bibliometría* · Universidad del Quindío

Implementación completa del requerimiento "Ranking de términos mediante algoritmos de ordenamiento": extracción de
los abstracts de los 20 artículos, preprocesamiento, **Counting Sort**, **Radix Sort** y **Bucket Sort**, medición de
tiempos sobre el corpus real y validación del top-15.

| Parte | Responsable |
|---|---|
| Extracción de los PDF, preprocesamiento, lista de frecuencias y Counting Sort | Zuleta |
| Radix Sort, medición de tiempos, top-15 y figuras | Natalia |
| Bucket Sort, análisis de complejidad y análisis comparativo | Sara |

## Resultados sobre el corpus (20 abstracts)

- 2.493 términos tras el preprocesamiento, **n = 981** términos distintos, frecuencias entre 1 y 86 → **k = 86** (k ≤ n).
- Top-15: ai (86), education (57), research (46), learning (40), aied (38), review (36), educational (33),
  assessment (32), intelligence (23), artificial (22), studies (21), higher (16), literature (15), study (15),
  systematic (15). Hay 4 términos con frecuencia 15 y «technologies» queda 16.º en los tres algoritmos.
- Los archivos de `docs/` con tiempos (`tiempos_corpus.csv`, `repeticiones_corpus.csv`, `salida_medicion_corpus.txt`
  y las figuras) se generaron con la lista anterior (n = 984) y deben regenerarse con `MedicionCorpus`.

## Estructura

```
bibliometria-ordenamiento-no-comparativo/
├── src/uniquindio/bibliometria/
│   ├── model/TermFrequency.java            # (término, frecuencia) – contrato común
│   ├── model/Article.java                  # título, autores y abstract de un artículo
│   ├── extraction/ArticleExtractor.java    # Zuleta: PDF -> título, autores, abstract (PDFBox)
│   ├── preprocessing/TextPreprocessor.java # Zuleta: minúsculas, tokens, stopwords
│   ├── sorting/FrequencySorter.java        # interfaz que implementan los 3 algoritmos
│   ├── sorting/CountingSort.java           # Zuleta: Counting Sort estable, descendente
│   ├── sorting/RadixSort.java              # Natalia: Radix Sort LSD, base configurable (10 por defecto)
│   ├── sorting/BucketSort.java             # Sara
│   ├── sorting/Ranking.java                # top-N, comparar rankings, imprimir
│   ├── io/FrequencyCsvReader.java          # lee "termino,frecuencia"
│   ├── io/FrequencyCsvWriter.java          # escribe "termino,frecuencia"
│   ├── app/ConstruirCorpus.java            # PDF -> data/frecuencias_corpus.csv (+ n, mín, máx, k)
│   ├── app/MedicionCorpus.java             # tiempos (ms) de los 3 algoritmos + top-15 sobre el corpus
│   ├── app/EscalamientoOrdenamientos.java  # crecimiento de los 3 algoritmos al duplicar n (sintético)
│   ├── app/DemoBucketSort.java             # ejecuta Bucket Sort y muestra el top-15
│   └── app/EscalamientoBucketSort.java     # comprobación empírica de la complejidad de Bucket Sort
├── test/uniquindio/bibliometria/
│   ├── BucketSortTest.java
│   ├── CountingSortTest.java
│   └── ContratoOrdenamientoTest.java       # contrato común para Counting y Radix (+ casos de Radix)
├── lib/pdfbox-app-3.0.3.jar                # Apache PDFBox (única librería externa)
├── corpus/pdf/                             # los 20 PDF del docente (no se suben al repo)
├── corpus/abstracts/                       # abstract extraído de cada artículo (NN.txt)
├── data/metadatos_articulos.tsv            # título y autores verificados a mano contra cada PDF
├── data/articulos.csv                      # id, archivo, título, autores, palabras del abstract
├── data/stopwords_en.txt                   # stopwords en inglés (NLTK + adicionales)
├── data/frecuencias_corpus.csv             # frecuencias reales del corpus (orden alfabético)
├── data/frecuencias_ejemplo.csv            # DATOS SINTÉTICOS (no son del corpus real)
├── docs/analisis_bucket_sort.md            # conteo de operaciones T(n) de Bucket Sort
├── docs/analisis_radix_sort.md             # conteo de operaciones T(n) de Radix Sort
├── docs/tiempos_corpus.csv, docs/top15_corpus.csv, docs/repeticiones_corpus.csv, docs/salida_medicion_corpus.txt
├── docs/escalamiento_ordenamientos.csv, docs/resultados_escalamiento.csv
├── docs/fig_*.png                          # figuras del informe (scripts/graficos.py)
├── informe/                                # informe en la plantilla
└── scripts/
    ├── compilar_y_probar.sh|.bat           # compila, corre las 3 pruebas y la demo
    └── graficos.py                         # CSV de docs/ -> figuras PNG (requiere matplotlib)
```

## Compilar y probar

Requiere JDK 11 o superior (se probó con 21). La única librería externa es Apache PDFBox, incluida en `lib/`.

```bash
./scripts/compilar_y_probar.sh        # Linux / macOS
scripts\compilar_y_probar.bat         # Windows
```

Hace `javac`, corre `BucketSortTest`, `CountingSortTest` y `ContratoOrdenamientoTest` (deben decir `0 fallos`) y la
demo. El script no regenera el corpus ni mide tiempos, para no sobrescribir los resultados del informe en cada prueba.

## Construir la lista de frecuencias del corpus

Copie los 20 PDF del docente en `corpus/pdf/` (conservando el número inicial del nombre, `1. ...pdf` a `20. ...pdf`) y,
después de compilar:

```bash
java -cp "out:lib/*" uniquindio.bibliometria.app.ConstruirCorpus     # Linux / macOS
java -cp "out;lib/*" uniquindio.bibliometria.app.ConstruirCorpus     # Windows
```

Genera `data/frecuencias_corpus.csv`, `data/articulos.csv` y `corpus/abstracts/NN.txt`, e imprime `n`, la frecuencia
mínima y máxima, `k` y el top-15.

## Medir los tiempos y generar las figuras

```bash
java -cp out uniquindio.bibliometria.app.MedicionCorpus                 # docs/tiempos_corpus.csv y docs/top15_corpus.csv
java -cp out uniquindio.bibliometria.app.EscalamientoOrdenamientos > docs/escalamiento_ordenamientos.csv
java -cp out uniquindio.bibliometria.app.EscalamientoBucketSort
python3 scripts/graficos.py                                              # figuras (requiere matplotlib)
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

1. **Extracción** (`ArticleExtractor`): PDFBox convierte las 2 primeras páginas a texto. El abstract empieza en
   "A B S T R A C T" / "Abstract" y termina en "Keywords", en la introducción o, en Elsevier, al cerrar el párrafo; se
   salta el bloque de palabras clave que Elsevier pone antes del abstract, se quitan los rótulos del abstract
   estructurado del artículo 13 ("Purpose –", "Findings –", ...) y se unen las palabras partidas en sílabas al final de
   línea. Los 20 abstracts se revisaron a mano en `corpus/abstracts/`. Título y autores salen de
   `data/metadatos_articulos.tsv` (transcritos de la primera página), porque los metadatos internos de los PDF están
   incompletos.
2. **Preprocesamiento** (`TextPreprocessor`): NFKD sin tildes, minúsculas, términos = secuencias de letras `a-z`, se
   descartan los de una letra y las stopwords de `data/stopwords_en.txt`; sin stemming. `ConstruirCorpus` cuenta las
   frecuencias de los 20 abstracts en orden alfabético.
3. **Ordenamiento**: los tres algoritmos sobre el mismo `TermFrequency[]`; como son estables y la entrada es alfabética,
   los empates salen iguales.
4. **Medición** (`MedicionCorpus`): 1 ejecución en frío, 2.000 de calentamiento y 501 rondas alternando el orden de los
   algoritmos; mínimo, mediana, media, desviación y máximo en ms.

## Notas sobre Counting Sort

- Clave `x = f − mín`; los contadores se acumulan de la clave mayor a la menor (sale descendente sin invertir) y la
  colocación recorre la entrada de derecha a izquierda (estable).
- `T(n, k) = 28n + 7k + 14` → O(n + k), verificado con un conteo simulado.

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

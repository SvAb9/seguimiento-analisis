#!/bin/bash
# Compila todo el proyecto y ejecuta las pruebas + demo. Uso: ./scripts/compilar_y_probar.sh
# La lista del corpus (ConstruirCorpus) y las mediciones (MedicionCorpus) se ejecutan aparte: ver README.md.
set -e
cd "$(dirname "$0")/.."
rm -rf out && mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out $(find src test -name "*.java")
java -cp out uniquindio.bibliometria.BucketSortTest
java -cp out uniquindio.bibliometria.CountingSortTest
java -cp out uniquindio.bibliometria.ContratoOrdenamientoTest
java -cp out uniquindio.bibliometria.app.DemoBucketSort

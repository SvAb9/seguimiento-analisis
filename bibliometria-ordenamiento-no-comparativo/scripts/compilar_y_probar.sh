#!/bin/bash
# Compila todo el proyecto y ejecuta las pruebas + demo. Uso: ./scripts/compilar_y_probar.sh
set -e
cd "$(dirname "$0")/.."
rm -rf out && mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out $(find src test -name "*.java")
java -cp out uniquindio.bibliometria.BucketSortTest
java -cp out uniquindio.bibliometria.CountingSortTest
java -cp out uniquindio.bibliometria.app.DemoBucketSort

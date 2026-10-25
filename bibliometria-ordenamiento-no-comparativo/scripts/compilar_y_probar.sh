#!/bin/bash
# Compila todo el proyecto, ejecuta las pruebas, genera las frecuencias del corpus y mide los tres algoritmos.
# Uso: ./scripts/compilar_y_probar.sh
set -e
cd "$(dirname "$0")/.."
rm -rf out && mkdir out
javac -encoding UTF-8 -d out $(find src test -name "*.java")
java -cp out uniquindio.bibliometria.BucketSortTest
java -cp out uniquindio.bibliometria.ContratoOrdenamientoTest
java -cp out uniquindio.bibliometria.app.GenerarFrecuencias
java -cp out uniquindio.bibliometria.app.MedicionCorpus

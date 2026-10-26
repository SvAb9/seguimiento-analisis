@echo off
REM Compila todo el proyecto y ejecuta las pruebas + demo (Windows)
REM La lista del corpus (ConstruirCorpus) y las mediciones (MedicionCorpus) se ejecutan aparte: ver README.md.
cd /d "%~dp0\.."
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java test\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\*" -d out @sources.txt
del sources.txt
java -cp out uniquindio.bibliometria.BucketSortTest
java -cp out uniquindio.bibliometria.CountingSortTest
java -cp out uniquindio.bibliometria.ContratoOrdenamientoTest
java -cp out uniquindio.bibliometria.app.DemoBucketSort

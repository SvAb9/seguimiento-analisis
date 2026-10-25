@echo off
REM Compila todo el proyecto, ejecuta las pruebas, genera las frecuencias del corpus y mide los tres algoritmos (Windows)
cd /d "%~dp0\.."
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java test\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
del sources.txt
java -cp out uniquindio.bibliometria.BucketSortTest
java -cp out uniquindio.bibliometria.ContratoOrdenamientoTest
java -cp out uniquindio.bibliometria.app.GenerarFrecuencias
java -cp out uniquindio.bibliometria.app.MedicionCorpus

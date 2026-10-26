"""Genera las figuras del informe a partir de los CSV de docs/ (requiere matplotlib).

Uso (desde la carpeta del proyecto, despues de MedicionCorpus y EscalamientoOrdenamientos):
    python3 scripts/graficos.py
Salida: docs/fig_tiempos_corpus.png, docs/fig_top15_corpus.png, docs/fig_escalamiento.png
"""
import csv

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.ticker

COLOR = {"Counting Sort": "#2a78d6", "Radix Sort": "#eb6834", "Bucket Sort": "#1baf7a"}
TINTA, TINTA2, REJILLA = "#0b0b0b", "#52514e", "#e4e3df"
plt.rcParams.update({"font.family": "DejaVu Sans", "font.size": 10, "axes.edgecolor": TINTA2,
                     "axes.labelcolor": TINTA, "xtick.color": TINTA2, "ytick.color": TINTA2,
                     "axes.spines.top": False, "axes.spines.right": False})


def coma(x, dec):
    return f"{x:.{dec}f}".replace(".", ",")


def tiempos():
    filas = list(csv.DictReader(open("docs/tiempos_corpus.csv", encoding="utf-8")))
    nombres = [f["algoritmo"] for f in filas]
    med = [float(f["ms_mediana"]) for f in filas]
    fig, ax = plt.subplots(figsize=(6.2, 2.6), dpi=200)
    barras = ax.barh(nombres, med, color=[COLOR[n] for n in nombres], height=0.5)
    for b, v in zip(barras, med):
        ax.text(b.get_width() + max(med) * 0.01, b.get_y() + b.get_height() / 2, coma(v, 4) + " ms",
                va="center", color=TINTA, fontsize=9)
    ax.invert_yaxis()
    ax.set_xlabel(f"Mediana del tiempo de ejecución (ms), n = {filas[0]['n']} términos")
    ax.set_xlim(0, max(med) * 1.25)
    ax.xaxis.set_major_formatter(matplotlib.ticker.FuncFormatter(lambda x, _: coma(x, 3)))
    ax.xaxis.grid(True, color=REJILLA, linewidth=0.8)
    ax.set_axisbelow(True)
    fig.tight_layout()
    fig.savefig("docs/fig_tiempos_corpus.png")


def top15():
    filas = list(csv.DictReader(open("docs/top15_corpus.csv", encoding="utf-8")))
    terminos = [f["counting_termino"] for f in filas]
    frec = [int(f["counting_frecuencia"]) for f in filas]
    fig, ax = plt.subplots(figsize=(6.2, 4.2), dpi=200)
    barras = ax.barh(terminos, frec, color="#2a78d6", height=0.65)
    for b, v in zip(barras, frec):
        ax.text(b.get_width() + 0.8, b.get_y() + b.get_height() / 2, str(v), va="center", color=TINTA, fontsize=8.5)
    ax.invert_yaxis()
    ax.set_xlabel("Frecuencia en los 20 abstracts (igual con Counting, Radix y Bucket Sort)")
    ax.xaxis.grid(True, color=REJILLA, linewidth=0.8)
    ax.set_axisbelow(True)
    fig.tight_layout()
    fig.savefig("docs/fig_top15_corpus.png")


def escalamiento():
    filas = [f for f in csv.DictReader(open("docs/escalamiento_ordenamientos.csv", encoding="utf-8"))
             if f["escenario"] == "ZIPF"]
    fig, ax = plt.subplots(figsize=(6.2, 3.6), dpi=200)
    for nombre in COLOR:
        pts = [(int(f["n"]), float(f["ms_mediana"])) for f in filas if f["algoritmo"] == nombre]
        xs, ys = zip(*pts)
        ax.plot(xs, ys, color=COLOR[nombre], linewidth=2, marker="o", markersize=4, label=nombre)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (términos, datos sintéticos tipo Zipf)")
    ax.set_ylabel("Mediana (ms)")
    ax.grid(True, which="major", color=REJILLA, linewidth=0.8)
    ax.legend(frameon=False, loc="upper left")
    fig.tight_layout()
    fig.savefig("docs/fig_escalamiento.png")


if __name__ == "__main__":
    tiempos()
    top15()
    escalamiento()
    print("Figuras escritas en docs/")

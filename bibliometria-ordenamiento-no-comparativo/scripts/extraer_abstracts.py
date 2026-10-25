"""Extraccion semiautomatica de titulo, autores y abstract de los 20 articulos.

Uso (desde la carpeta del proyecto):
    python3 scripts/extraer_abstracts.py "<carpeta con los 20 PDF>" data/corpus/abstracts.txt

Requiere pdftotext (poppler-utils). Para cada articulo:
  1. Se convierten las 2 primeras paginas del PDF a texto con pdftotext.
  2. Se normaliza el texto (NFKC: ligaduras como "fi"; guiones blandos de fin de linea; espacios).
  3. El abstract se recorta entre una frase de inicio y una de fin propias de cada articulo
     (las revistas tienen maquetaciones distintas, asi que las anclas se fijaron revisando cada PDF).
  4. Titulo y autores se transcribieron a mano desde la primera pagina (metadatos para cruces de informacion).
El archivo generado se reviso manualmente contra los PDF.
"""
import re
import subprocess
import sys
import unicodedata
from pathlib import Path

# id: (titulo, autores, frase inicial del abstract, frase final del abstract)
ARTICULOS = {
    1: ("Artificial intelligence literacy in higher and adult education: A scoping literature review",
        "Matthias Carl Laupichler; Alexandra Aster; Jana Schirch; Tobias Raupach",
        "Since artificial intelligence (AI) is finding", "are derived from the results."),
    2: ("Systematic literature review on opportunities, challenges, and future research recommendations of artificial intelligence in education",
        "Thomas K.F. Chiu; Qi Xia; Xinyan Zhou; Ching Sing Chai; Miaoting Cheng",
        "Applications of artificial intelligence in education (AIEd) are emerging", "directions of AIEd research."),
    3: ("Myths, mis- and preconceptions of artificial intelligence: A review of the literature",
        "Arne Bewersdorff; Xiaoming Zhai; Jessica Roberts; Claudia Nerdel",
        "Artificial Intelligence (AI) is prevalent in nearly", "outreach strategies in AI education."),
    4: ("Opportunities of artificial intelligence for supporting complex problem-solving: Findings from a scoping review",
        "Srecko Joksimovic; Dirk Ifenthaler; Rebecca Marrone; Maarten De Laat; George Siemens",
        "The research objective of this paper", "research and practice are being discussed."),
    5: ("Application and theory gaps during the rise of Artificial Intelligence in Education",
        "Xieling Chen; Haoran Xie; Di Zou; Gwo-Jen Hwang",
        "Considering the increasing importance", "application of AI technologies with educational theories."),
    6: ("A multi-perspective study on Artificial Intelligence in Education: grants, conferences, journals, software tools, institutions, and researchers",
        "Xieling Chen; Haoran Xie; Gwo-Jen Hwang",
        "With the rapid development of", "Computers & Education: Artificial Intelligence."),
    7: ("Human-centred learning analytics and AI in education: A systematic literature review",
        "Riordan Alfredo; Vanessa Echeverria; Yueqiao Jin; Lixiang Yan; Zachari Swiecki; Dragan Gašević; Roberto Martinez-Maldonado",
        "The rapid expansion of Learning Analytics", "future human-centred LA/AIED systems."),
    8: ("A systematic review of artificial intelligence techniques for collaborative learning over the past two decades",
        "Seng Chee Tan; Alwyn Vwen Yen Lee; Min Lee",
        "This systematic review focuses on publications", "suggest future areas of study."),
    9: ("AI technologies for education: Recent research & future directions",
        "Ke Zhang; Ayse Begum Aslan",
        "From unique educational perspectives", "development efforts."),
    10: ("Educational applications of artificial intelligence in simulation-based learning: A systematic mapping review",
         "Chih-Pu Dai; Fengfeng Ke",
         "The field of education has experienced a transformation", "in AI in education using simulation-based learning."),
    11: ("A scoping review on how generative artificial intelligence transforms assessment in higher education",
         "Qi Xia; Xiaojing Weng; Fan Ouyang; Tzung Jin Lin; Thomas K.F. Chiu",
         "Generative artificial intelligence provides both", "inter-disciplinary programs and teaching."),
    12: ("A meta systematic review of artificial intelligence in higher education: a call for increased ethics, collaboration, and rigour",
         "Melissa Bond; Hassan Khosravi; Maarten De Laat; Nina Bergdahl; Violeta Negrea; Emily Oxley; Phuong Pham; Sin Wang Chong; George Siemens",
         "Although the field of Artificial Intelligence in Education (AIEd) has", "primary and secondary research."),
    13: ("Artificial intelligence for education and its emphasis on assessment and adversity quotient: a review",
         "Jyoti Mudkanna Gavhane; Reena Pagare",
         "Purpose – The purpose", "the evaluation of AQ."),
    14: ("A comprehensive exploration of artificial intelligence competence frameworks for educators: A critical review",
         "Tamar Mikeladze; Paulien C. Meijer; Roald P. Verhoeff",
         "Recent literature underscores", "in their teaching practices."),
    15: ("A Systematic Review of Generative AI for Teaching and Learning Practice",
         "Bayode Ogunleye; Kudirat Ibilola Zakariyyah; Oluwaseun Ajao; Olakunle Olayinka; Hemlata Sharma",
         "The use of generative artificial intelligence (GenAI) in academia", "policies for GenAI usage."),
    16: ("Generative Artificial Intelligence in Tertiary Education: Assessment Redesign Principles and Considerations",
         "Che Yee Lye; Lyndon Lim",
         "The emergence of generative artificial intelligence (AI) such as ChatGPT", "fundamental issues in assessments."),
    17: ("A Systematic Review of AI-Driven Educational Assessment in STEM Education",
         "Fan Ouyang; Tuan Anh Dinh; Weiqi Xu",
         "Artificial intelligence (AI), as an emerging technology", "educational assessment in STEM education."),
    18: ("Transforming Education: A Comprehensive Review of Generative Artificial Intelligence in Educational Settings through Bibliometric and Content Analysis",
         "Zied Bahroun; Chiraz Anane; Vian Ahmed; Andrew Zacca",
         "In the ever-evolving era", "intersection of GAI and education."),
    19: ("What Is the Impact of ChatGPT on Education? A Rapid Review of the Literature",
         "Chung Kwan Lo",
         "An artificial intelligence-based chatbot, ChatGPT, was launched", "on the educational environment."),
    20: ("Systematic review of research on artificial intelligence applications in higher education – where are the educators?",
         "Olaf Zawacki-Richter; Victoria I. Marín; Melissa Bond; Franziska Gouverneur",
         "According to various international reports", "application of AIEd in higher education."),
}

# El articulo 14 tiene el abstract en una columna lateral: se lee con -layout y se toma esa columna.
COLUMNA_DERECHA = {14}

# pdftotext elimina el guion al unir palabras partidas al final de la linea; estos casos eran
# palabras compuestas reales y se restituye el guion (revisado contra el PDF).
CORRECCIONES = {
    "AIdriven": "AI-driven",
    "instancebased": "instance-based",
}


def pdf_a_texto(pdf, layout=False):
    cmd = ["pdftotext", "-l", "2"] + (["-layout"] if layout else []) + [str(pdf), "-"]
    return subprocess.run(cmd, capture_output=True, check=True, text=True).stdout


def columna_derecha(texto):
    lineas = texto.splitlines()
    inicio = next(i for i, l in enumerate(lineas) if re.search(r"\bAbstract\b", l))
    col = lineas[inicio].index("Abstract")
    partes = []
    for linea in lineas[inicio + 1:]:
        trozo = linea[col:].strip() if len(linea) > col else ""
        if trozo == "KEYWORDS":
            break
        if trozo:
            partes.append(trozo)
    return "\n".join(partes)


def normalizar(texto):
    texto = unicodedata.normalize("NFKC", texto)
    texto = re.sub("­\\s*", "", texto)          # guion blando (posible corte de linea)
    texto = re.sub(r"(\w)-\n(\w)", r"\1\2", texto)   # corte de palabra con guion normal (modo layout)
    return re.sub(r"\s+", " ", texto).strip()


def recortar(texto, desde, hasta):
    i = texto.index(desde)
    j = texto.index(hasta, i) + len(hasta)
    return texto[i:j]


def main(carpeta, salida):
    pdfs = {int(p.name.split(".")[0]): p for p in Path(carpeta).glob("*.pdf")}
    bloques = []
    for num, (titulo, autores, desde, hasta) in ARTICULOS.items():
        bruto = pdf_a_texto(pdfs[num], layout=num in COLUMNA_DERECHA)
        if num in COLUMNA_DERECHA:
            bruto = columna_derecha(bruto)
        abstract = recortar(normalizar(bruto), desde, hasta)
        for malo, bueno in CORRECCIONES.items():
            abstract = abstract.replace(malo, bueno)
        bloques.append(f"@id {num}\n@titulo {titulo}\n@autores {autores}\n@abstract {abstract}\n")
    cabecera = ("# Corpus: titulo, autores y abstract de los 20 articulos del proyecto.\n"
                "# Generado con scripts/extraer_abstracts.py y revisado manualmente contra los PDF.\n\n")
    Path(salida).write_text(cabecera + "\n".join(bloques), encoding="utf-8")
    print(f"{len(bloques)} articulos escritos en {salida}")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])

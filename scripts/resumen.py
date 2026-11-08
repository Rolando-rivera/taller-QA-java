import glob
import os
from pathlib import Path
import xml.etree.ElementTree as ET
with open(os.environ["GITHUB_STEP_SUMMARY"], "a",
          encoding="utf-8") as salida:
    salida.write("## Resultados funcionales\n\n")
    archivos = sorted(glob.glob(
        "target/surefire-reports/TEST-*.xml"
    ))
    if archivos:
        salida.write("| Suite | Tests | Fallos | Errores | Omitidos |\n")
        salida.write("|---|---:|---:|---:|---:|\n")
        for archivo in archivos:
            datos = ET.parse(archivo).getroot().attrib
            salida.write(
                "| {name} | {tests} | {failures} | {errors} | {skipped} |\n"
                .format(**datos)
            )
    else:
        salida.write("No se generaron reportes funcionales.\n")
    salida.write("\n## Rendimiento\n\n")
    reporte = Path("target/k6.txt")
    if reporte.exists():
        salida.write("```text\n")
        for linea in reporte.read_text().splitlines():
            if any(m in linea for m in (
                "http_reqs", "http_req_duration", "p(95)",
                "http_req_failed", "checks", "dropped_iterations"
            )):
                salida.write(linea + "\n")
        salida.write("```\n")
    else:
        salida.write("No hay resultados de k6.\n")

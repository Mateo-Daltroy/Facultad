"""Aplica al flow desplegado los umbrales nuevos de la autonomía (18/11/2026, tras la primera prueba).

- Autonomía: toma el código actual de build_autonomia.py.
- Aceleracion: su freno de 50 cm no aplica mientras flow "maniobra" sea true (arco de esquive).

Uso: python3 patch_umbrales.py entrada_v2.json salida_v2.json
"""
import json
import sys

import build_autonomia as b

FRENO_VIEJO = "const frenar = boton || (obstaculo && pedal >= 0);"
FRENO_NUEVO = (
    "// Durante el arco de esquive (flow \"maniobra\") Autonomía frena sola a 20 cm\n"
    "const maniobra = flow.get(\"maniobra\") || false;\n"
    "const frenar = boton || (obstaculo && pedal >= 0 && !maniobra);"
)

data = json.load(open(sys.argv[1]))
by_id = {n["id"]: n for n in data["flows"]}
by_id[b.AUTONOMIA]["func"] = b.AUTONOMIA_FUNC
acel = by_id[b.ACELERACION]
if FRENO_VIEJO not in acel["func"]:
    raise SystemExit("Aceleracion cambió: no encuentro la línea del freno")
acel["func"] = acel["func"].replace(FRENO_VIEJO, FRENO_NUEVO)
json.dump(data, open(sys.argv[2], "w"), ensure_ascii=False, indent=1)
print("rev", data["rev"])

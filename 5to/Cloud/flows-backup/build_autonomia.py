"""Agrega la autonomía (esquive de obstáculos) y el dashboard al flow de Node-RED del robot.

Uso: python3 build_autonomia.py entrada.json salida.json
`entrada.json` es la respuesta de GET /flows con la cabecera `Node-RED-API-Version: v2`
({"rev": ..., "flows": [...]}). La salida conserva el `rev` para que el deploy falle
si alguien modificó el flow mientras tanto.
"""
import json
import sys

TAB = "b0d25a1cf450683d"

# Nodos existentes (ids del flow desplegado al 18/11/2026)
MQTT_IN = "4a52b344098d1068"
DIVIDIR = "5a116c1a32b357b5"
ULTRASONIDO = "c713c30206c85e91"
OBSTACULO = "9d660a212ee71cc2"
RBE_PEDAL = "bd0729e1096c8a28"
RBE_DIR = "f86932a49f54f859"
ACELERACION = "837ff0481221fb76"
DIRECCION = "0e41e380269df966"
IN1, IN2, ENA, SERVO = "d5889b47af29aa30", "dd66e0ecf244d5d4", "638cfbfc54837839", "ecf0ce9c6cdf3406"
BOTON_X = "bz0000000000x001"
BUZZER = "bz0000000000x002"

# Nodos nuevos
G_MANUAL, G_SENS, G_AUTO, G_ACT, G_DASH = (f"a0a0000000000g0{i}" for i in range(1, 6))
C_MANUAL, C_SENS, C_AUTO, C_ACT, C_DASH = (f"a0a0000000000c0{i}" for i in range(1, 6))
BOTON_MODO = "a0a0000000000n01"
NORMALIZAR = "a0a0000000000n02"
TICK = "a0a0000000000n03"
AUTONOMIA = "a0a0000000000n04"
MODO = "a0a0000000000n05"
REGISTRO = "a0a0000000000n06"
LOG_FILE = "a0a0000000000n07"
COMANDO = "a0a0000000000n08"
DIST_RATE = "a0a0000000000n09"
UI_SWITCH = "a0a0000000000u01"
UI_DIST = "a0a0000000000u02"
UI_ESTADO = "a0a0000000000u03"
UI_ULTIMO = "a0a0000000000u04"
UI_BASE, UI_THEME, UI_PAGE, UI_GROUP = "a0a0000000000b01", "a0a0000000000b02", "a0a0000000000b03", "a0a0000000000b04"

TONO_SIN_SALIDA = "/home/pi/tonos/sin_salida.txt"
LOG_PATH = "/home/pi/autonomia_log.csv"

DIVIDIR_FUNC = r"""// Traduce el gamepad (MQTT control/gamepad) a pedal, freno y dirección.
// En modo "auto" solo deja pasar el freno (cuadrado): pedal y dirección los manda Autonomía.
const UMBRAL = 0.1;       // evita que un roce del gatillo mueva el motor
const ZONA_MUERTA = 0.1;  // ignora el ruido del stick

const { axes, buttons } = msg.payload;
const boton = { topic: "boton", payload: buttons[3] };  // cuadrado

if ((flow.get("modo") || "manual") === "auto") return [[boton], null];

const l2 = (axes[2] + 1) / 2;
const r2 = (axes[5] + 1) / 2;

const pedal = r2 >= l2 ? r2 : -l2;
const acel = Math.abs(pedal) < UMBRAL ? 0 : Math.round(pedal * 20) / 20;

const x = Math.abs(axes[0]) < ZONA_MUERTA ? 0 : Math.round(axes[0] * 10) / 10;

return [
    [
        { topic: "pedal", payload: acel },
        boton,
    ],
    { payload: x },
];
"""

BOTON_MODO_FUNC = r"""// Options (botón 9) alterna entre modo manual y automático.
// Solo cuenta el flanco de subida, no mientras se mantiene apretado.
const apretado = msg.payload.buttons[9] === 1;
const antes = context.get("apretado") || false;
context.set("apretado", apretado);
if (!apretado || antes) return null;
return { topic: "toggle", payload: "toggle" };
"""

NORMALIZAR_FUNC = r"""// Normaliza la lectura del ultrasónico.
// Descarta 0, NaN y valores fuera de rango (el driver manda "0.0" si falla una lectura).
// Guarda la última lectura válida con su hora en flow "dist" para Autonomía.
const MIN_CM = 2, MAX_CM = 400;
const cm = Number(msg.payload);
if (!Number.isFinite(cm) || cm <= MIN_CM || cm >= MAX_CM) return null;

flow.set("dist", { cm, t: Date.now() });
return { topic: "distancia", payload: cm };
"""

OBSTACULO_FUNC = r"""// Freno de seguridad: marca obstáculo bajo FRENO_CM y lo libera sobre LIBRE_CM.
// Aceleracion bloquea el avance (no la marcha atrás) mientras haya obstáculo.
const FRENO_CM = 50;
const LIBRE_CM = 55;  // margen para que no alterne al rondar los 50 cm
const SIN_LECTURA_MS = 2000;  // si el sensor deja de responder, no dejar el auto trabado

const cm = Number(msg.payload);
let obstaculo = context.get("obstaculo") || false;

if (cm < FRENO_CM) obstaculo = true;
else if (cm > LIBRE_CM) obstaculo = false;

context.set("obstaculo", obstaculo);
context.set("cm", cm);
node.status({ fill: obstaculo ? "red" : "green", shape: "dot", text: `${cm} cm` });
// El driver del sensor solo informa cuando la distancia cambia y no avisa
// si deja de responder: sin lecturas nuevas, el obstáculo se libera solo
clearTimeout(context.get("vigia"));
if (obstaculo) {
    context.set("vigia", setTimeout(() => {
        context.set("obstaculo", false);
        node.status({ fill: "yellow", shape: "ring", text: `sin lecturas desde ${cm} cm, libero` });
        node.send({ topic: "obstaculo", payload: false });
    }, SIN_LECTURA_MS));
}

return { topic: "obstaculo", payload: obstaculo };
"""

AUTONOMIA_FUNC = r"""// Máquina de estados de esquive. La dispara "Tick" cada 100 ms.
// Lee la distancia de flow "dist" (la guarda "Normalizar distancia").
// Salida 1: comando {vel, steer, buzzer} -> Actuadores (vel y steer de -1 a 1, steer -1 = izquierda).
// Salida 2: una transición por mensaje -> registro y dashboard.
const D = 50;               // cm: obstáculo / camino libre (igual al freno de "Obstaculo")
const D_RETRO = 80;         // cm: la marcha atrás sigue hasta ganar esta distancia...
const T_BACK_MAX = 2000;    // ms: ...o hasta este tope (marcha atrás ciega)
const D_CRITICA = 20;       // cm: durante el arco solo frena si algo queda más cerca que esto
const V = 0.3;              // velocidad de avance (subir de a poco tras probar)
const V_BACK = 0.45;        // velocidad de marcha atrás (0.2 no movía el auto en el piso)
const V_ARC = 0.45;         // velocidad de arcos y de deshacerlos
const T_WAIT = 300;         // ms quieto para que el sensor se asiente
const T_SERVO = 300;        // ms para que el servo gire antes de mover el motor
const T_ARC = 1200;         // ms: duración de cada arco
const T_SIN_DATO = 1000;    // ms sin lecturas válidas -> detenido

// Durante el arco el sensor todavía ve el obstáculo (< 50 cm): flow "maniobra" le avisa a
// Aceleracion que no aplique su freno de 50 cm. Autonomía frena sola bajo D_CRITICA.
const maniobra = (activa) => flow.set("maniobra", activa);

if ((flow.get("modo") || "manual") !== "auto") {
    context.set("s", null);  // al volver a auto arranca de cero
    maniobra(false);
    return null;
}

const ahora = Date.now();
const lectura = flow.get("dist");
const d = lectura && ahora - lectura.t <= T_SIN_DATO ? lectura.cm : null;

const transiciones = [];
let s = context.get("s");
const go = (st, extra = {}) => {
    transiciones.push({ payload: { ts: new Date(ahora).toISOString(), de: s.st, a: st, dist: d, lado: s.side, ...extra } });
    s.st = st;
    s.t0 = ahora;
};
if (!s) {
    s = { st: "INICIO", t0: ahora, side: -1, arcMs: 0, tDeteccion: null };
    go("AVANZAR");
}

if (d === null && s.st !== "SIN_SENSOR" && s.st !== "BUZZER") go("SIN_SENSOR");

const el = ahora - s.t0;
const PARADO = { vel: 0, steer: 0, buzzer: false };
let cmd = PARADO;

switch (s.st) {
    case "AVANZAR":
        cmd = { vel: V, steer: 0, buzzer: false };
        if (d < D) {
            s.side = -1;
            s.tDeteccion = ahora;
            cmd = PARADO;
            go("FRENAR");
        }
        break;
    case "FRENAR":  // quieto, deja asentar el sensor
        if (el > T_WAIT) go(d > D + 5 ? "AVANZAR" : "RETRO");
        break;
    case "RETRO":  // marcha atrás recta para ganar espacio para el arco
        cmd = { vel: -V_BACK, steer: 0, buzzer: false };
        if (d >= D_RETRO || el > T_BACK_MAX) {
            cmd = PARADO;
            go("ARCO");
        }
        break;
    case "ARCO":  // gira el servo y avanza hacia s.side, usando el espacio ganado
        if (el < T_SERVO) {
            cmd = { vel: 0, steer: s.side, buzzer: false };
            break;
        }
        cmd = { vel: V_ARC, steer: s.side, buzzer: false };
        if (el - T_SERVO > T_ARC || d < D_CRITICA) {
            s.arcMs = el - T_SERVO;
            cmd = { vel: 0, steer: s.side, buzzer: false };
            go("MEDIR", { arcoMs: s.arcMs });
        }
        break;
    case "MEDIR":  // quieto, mide hacia donde quedó apuntando
        cmd = { vel: 0, steer: s.side, buzzer: false };
        if (el > T_WAIT) {
            if (d >= D) go("AVANZAR", { esquiveMs: ahora - s.tDeteccion });
            else go("VOLVER");
        }
        break;
    case "VOLVER":  // deshace el arco: misma dirección, marcha atrás
        cmd = { vel: -V_ARC, steer: s.side, buzzer: false };
        if (el > s.arcMs) {
            cmd = { vel: 0, steer: s.side, buzzer: false };
            if (s.side === -1) {
                s.side = 1;
                go("ARCO");
            } else {
                go("BUZZER");
            }
        }
        break;
    case "BUZZER":  // sin salida: detenido con alarma hasta pasar a manual
        cmd = { vel: 0, steer: 0, buzzer: true };
        break;
    case "SIN_SENSOR":  // sin lecturas: detenido hasta que vuelvan
        if (d !== null) go(d >= D ? "AVANZAR" : "FRENAR");
        break;
    default:
        throw new Error(`Estado desconocido: ${s.st}`);
}

// Solo el arco hacia adelante esquiva el freno de 50 cm; cualquier otro estado lo vuelve a activar
maniobra(s.st === "ARCO" && cmd.vel > 0);
context.set("s", s);
node.status({ fill: s.st === "AVANZAR" ? "green" : "yellow", shape: "dot", text: `${s.st} · ${d ?? "-"} cm` });
return [{ payload: cmd }, transiciones];
"""

MODO_FUNC = r"""// Cambia flow "modo" entre "manual" y "auto".
// Entradas: el switch del dashboard ("manual"/"auto") o Options del gamepad (topic "toggle").
// Salida 1: al pasar a manual, comando de detención para Actuadores.
// Salida 2: estado para sincronizar el switch.  Salida 3: registro.
const actual = flow.get("modo") || "manual";
const nuevo = msg.topic === "toggle" ? (actual === "auto" ? "manual" : "auto") : msg.payload;
if (nuevo !== "manual" && nuevo !== "auto") throw new Error(`Modo inválido: ${nuevo}`);
if (nuevo === actual) return null;

flow.set("modo", nuevo);
node.status({ fill: nuevo === "auto" ? "green" : "grey", shape: "dot", text: nuevo });

const parar = nuevo === "manual" ? { payload: { vel: 0, steer: 0, buzzer: false } } : null;
const registro = { payload: { ts: new Date().toISOString(), de: `modo ${actual}`, a: `modo ${nuevo}`, dist: null, lado: null } };
return [parar, { payload: nuevo }, registro];
"""

REGISTRO_FUNC = r"""// Arma una línea CSV por transición para las métricas del informe:
// ts, de, a, dist_cm, lado, arco_ms, esquive_ms
const { ts, de, a, dist, lado, arcoMs, esquiveMs } = msg.payload;
const v = (x) => (x === null || x === undefined ? "" : x);
return { payload: [ts, de, a, v(dist), v(lado), v(arcoMs), v(esquiveMs)].join(",") };
"""

COMANDO_FUNC = r"""// Traduce {vel, steer, buzzer} al mismo camino que usa el control manual:
// salida 1 -> pedal (-1..1) hacia Aceleracion, salida 2 -> dirección (-1..1) hacia Direccion,
// salida 3 -> buzzer.js (arranca la alarma al activarse y la corta al desactivarse).
// Direccion ya convierte -1..1 al rango del servo (5 izquierda, 7 centro, 10 derecha).
const TONO = "__TONO__";
const { vel, steer, buzzer } = msg.payload;
if (typeof vel !== "number" || typeof steer !== "number" || typeof buzzer !== "boolean") {
    throw new Error(`Comando inválido: ${JSON.stringify(msg.payload)}`);
}
const limitar = (x) => Math.max(-1, Math.min(1, x));

const pedal = { topic: "pedal", payload: Math.round(limitar(vel) * 20) / 20 };
const direccion = { payload: Math.round(limitar(steer) * 10) / 10 };

let alarma = null;
const sonando = context.get("buzzer") || false;
if (buzzer && !sonando) alarma = { payload: TONO };
else if (!buzzer && sonando) alarma = { kill: "SIGTERM", payload: "" };
context.set("buzzer", buzzer);

return [pedal, direccion, alarma];
""".replace("__TONO__", TONO_SIN_SALIDA)


def fn(id_, name, func, outputs, x, y, g, wires, finalize=""):
    return {"id": id_, "type": "function", "z": TAB, "g": g, "name": name, "func": func, "outputs": outputs,
            "timeout": 0, "noerr": 0, "initialize": "", "finalize": finalize, "libs": [], "x": x, "y": y, "wires": wires}


def comment(id_, name, info, x, y, g):
    return {"id": id_, "type": "comment", "z": TAB, "g": g, "name": name, "info": info, "x": x, "y": y, "wires": []}


def ui_text(id_, label, fmt, order, x, y):
    return {"id": id_, "type": "ui-text", "z": TAB, "g": G_DASH, "group": UI_GROUP, "order": order, "width": 0, "height": 0,
            "name": label, "label": label, "format": fmt, "layout": "row-spread", "style": False, "font": "", "fontSize": 16,
            "color": "#717171", "wrapText": False, "className": "", "value": "payload", "valueType": "msg", "x": x, "y": y, "wires": []}


def main(src, dst):
    data = json.load(open(src))
    flows = data["flows"]
    by_id = {n["id"]: n for n in flows}
    nuevos = {n for n in by_id if n.startswith("a0a0000000000")}
    if nuevos:
        raise SystemExit(f"El flow ya tiene nodos de la autonomía: {sorted(nuevos)}")
    for req in (MQTT_IN, DIVIDIR, ULTRASONIDO, OBSTACULO, RBE_PEDAL, RBE_DIR, ACELERACION, DIRECCION, BOTON_X, BUZZER):
        if req not in by_id:
            raise SystemExit(f"Falta el nodo esperado {req}; el flow cambió, revisar antes de desplegar")

    # --- Cambios en nodos existentes ---
    by_id[MQTT_IN]["wires"] = [[DIVIDIR, BOTON_X, BOTON_MODO]]
    by_id[DIVIDIR]["func"] = DIVIDIR_FUNC
    by_id[ULTRASONIDO]["wires"] = [[NORMALIZAR]]
    by_id[OBSTACULO]["func"] = OBSTACULO_FUNC

    layout = {
        MQTT_IN: (G_MANUAL, 150, 100), DIVIDIR: (G_MANUAL, 390, 100),
        ULTRASONIDO: (G_SENS, 150, 300), OBSTACULO: (G_SENS, 650, 340),
        RBE_PEDAL: (G_ACT, 640, 640), RBE_DIR: (G_ACT, 640, 700),
        ACELERACION: (G_ACT, 850, 640), DIRECCION: (G_ACT, 850, 700),
        IN2: (G_ACT, 1080, 600), IN1: (G_ACT, 1080, 640), ENA: (G_ACT, 1080, 680), SERVO: (G_ACT, 1080, 720),
        BOTON_X: (G_ACT, 640, 780), BUZZER: (G_ACT, 850, 780),
    }
    for nid, (g, x, y) in layout.items():
        by_id[nid].update({"g": g, "x": x, "y": y})

    # Los nodos de prueba desactivados quedan fuera de los grupos, abajo
    y = 1020
    for n in flows:
        if n.get("z") == TAB and n.get("d") and n["id"] not in layout:
            n.pop("g", None)
            n.update({"x": 150 + 220 * ((y - 1020) // 40), "y": 1020})
            y += 40

    # --- Nodos nuevos ---
    new = [
        comment(C_MANUAL, "Control manual por MQTT",
                "Recibe el gamepad por MQTT (`control/gamepad`, broker local de la Pi).\n\n"
                "- R2 avanza, L2 retrocede, stick izquierdo dirige, cuadrado frena.\n"
                "- Options alterna manual/auto.\n- En modo auto, pedal y dirección los ignora (solo pasa el freno).",
                150, 60, G_MANUAL),
        fn(BOTON_MODO, "Options → modo", BOTON_MODO_FUNC, 1, 390, 160, G_MANUAL, [[MODO]]),

        comment(C_SENS, "Sensores",
                "Ultrasónico frontal (pines físicos 16 y 18).\n\n"
                "- `Normalizar distancia` descarta 0, NaN y fuera de rango y guarda flow `dist` = {cm, t}.\n"
                "- `Obstaculo` es el freno de seguridad (50 cm) que usa Aceleracion en ambos modos.\n"
                "- El driver solo emite cuando la distancia cambia y no emite sin eco (> 4 m).",
                150, 260, G_SENS),
        fn(NORMALIZAR, "Normalizar distancia", NORMALIZAR_FUNC, 1, 390, 300, G_SENS, [[OBSTACULO, DIST_RATE]]),

        comment(C_AUTO, "Autonomía: esquive de obstáculos",
                "Máquina de estados: AVANZAR → FRENAR → RETRO → ARCO (izq) → MEDIR → AVANZAR, o VOLVER → ARCO (der) → MEDIR → AVANZAR / BUZZER.\n\n"
                "- Solo corre con flow `modo` = `auto`.\n- Sin lecturas por 1 s pasa a SIN_SENSOR (detenido).\n"
                f"- Cada transición se agrega a `{LOG_PATH}`.",
                150, 420, G_AUTO),
        {"id": TICK, "type": "inject", "z": TAB, "g": G_AUTO, "name": "Tick 100 ms", "props": [{"p": "payload"}],
         "repeat": "0.1", "crontab": "", "once": True, "onceDelay": "1", "topic": "", "payload": "", "payloadType": "date",
         "x": 160, "y": 460, "wires": [[AUTONOMIA]]},
        fn(AUTONOMIA, "Autonomía", AUTONOMIA_FUNC, 2, 390, 460, G_AUTO, [[COMANDO], [REGISTRO, UI_ESTADO, UI_ULTIMO]]),
        fn(MODO, "Modo manual/auto", MODO_FUNC, 3, 390, 520, G_AUTO, [[COMANDO], [UI_SWITCH], [REGISTRO]]),
        fn(REGISTRO, "Registro CSV", REGISTRO_FUNC, 1, 640, 500, G_AUTO, [[LOG_FILE]]),
        {"id": LOG_FILE, "type": "file", "z": TAB, "g": G_AUTO, "name": "autonomia_log.csv", "filename": LOG_PATH,
         "filenameType": "str", "appendNewline": True, "createDir": False, "overwriteFile": "false", "encoding": "none",
         "x": 870, "y": 500, "wires": [[]]},

        comment(C_ACT, "Actuadores",
                "`Comando a actuadores` recibe {vel, steer, buzzer} (-1..1) de Autonomía y Modo y los manda por el mismo camino que el manual:\n\n"
                "- pedal → filtro → Aceleracion (rampa, IN1/IN2/ENA).\n- dirección → filtro → Direccion (servo 5/7/10).\n"
                f"- buzzer → buzzer.js con `{TONO_SIN_SALIDA}`.",
                150, 600, G_ACT),
        fn(COMANDO, "Comando a actuadores", COMANDO_FUNC, 3, 390, 680, G_ACT, [[RBE_PEDAL], [RBE_DIR], [BUZZER]]),

        comment(C_DASH, "Dashboard",
                "Dashboard 2.0 en `/dashboard/robot`: switch manual/auto, distancia (máx. 4 por segundo), estado y última transición.",
                150, 880, G_DASH),
        {"id": UI_SWITCH, "type": "ui-switch", "z": TAB, "g": G_DASH, "name": "Modo automático", "label": "Modo automático",
         "group": UI_GROUP, "order": 1, "width": 0, "height": 0, "passthru": False, "decouple": False, "topic": "modo",
         "topicType": "str", "style": "", "className": "", "layout": "row-spread", "clickableArea": "switch",
         "onvalue": "auto", "onvalueType": "str", "onicon": "", "oncolor": "", "offvalue": "manual", "offvalueType": "str",
         "officon": "", "offcolor": "", "x": 170, "y": 920, "wires": [[MODO]]},
        {"id": DIST_RATE, "type": "delay", "z": TAB, "g": G_DASH, "name": "4 por segundo", "pauseType": "rate",
         "timeout": "5", "timeoutUnits": "seconds", "rate": "4", "nbRateUnits": "1", "rateUnits": "second",
         "randomFirst": "1", "randomLast": "5", "randomUnits": "seconds", "drop": True, "allowrate": False, "outputs": 1,
         "x": 390, "y": 960, "wires": [[UI_DIST]]},
        ui_text(UI_DIST, "Distancia", "{{msg.payload}} cm", 2, 620, 960),
        ui_text(UI_ESTADO, "Estado", "{{msg.payload.a}}", 3, 620, 920),
        ui_text(UI_ULTIMO, "Última transición", "{{msg.payload.de}} → {{msg.payload.a}} ({{msg.payload.ts}})", 4, 860, 920),

        {"id": UI_BASE, "type": "ui-base", "name": "Robot", "path": "/dashboard", "appIcon": "", "includeClientData": True,
         "acceptsClientConfig": ["ui-notification", "ui-control"], "showPathInSidebar": False, "headerContent": "page",
         "navigationStyle": "default", "titleBarStyle": "default", "showReconnectNotification": True,
         "notificationDisplayTime": 1, "showDisconnectNotification": True, "allowInstall": False},
        {"id": UI_THEME, "type": "ui-theme", "name": "Robot",
         "colors": {"surface": "#fcfcfd", "primary": "#0d74ce", "bgPage": "#f9f9fb", "groupBg": "#ffffff", "groupOutline": "#d9d9e0"},
         "sizes": {"density": "default", "pagePadding": "12px", "groupGap": "12px", "groupBorderRadius": "4px", "widgetGap": "12px"}},
        {"id": UI_PAGE, "type": "ui-page", "name": "Robot", "ui": UI_BASE, "path": "/robot", "icon": "robot", "layout": "grid",
         "theme": UI_THEME, "breakpoints": [{"name": "Default", "px": 0, "cols": 3}, {"name": "Tablet", "px": 576, "cols": 6},
                                            {"name": "Small Desktop", "px": 768, "cols": 9}, {"name": "Desktop", "px": 1024, "cols": 12}],
         "order": 1, "className": "", "visible": "true", "disabled": "false"},
        {"id": UI_GROUP, "type": "ui-group", "name": "Autonomía", "page": UI_PAGE, "width": 6, "height": 1, "order": 1,
         "showTitle": True, "className": "", "visible": "true", "disabled": "false", "groupType": "default"},
    ]
    flows.extend(new)

    # --- Grupos: el contenedor abarca a sus nodos ---
    names = {G_MANUAL: ("Control manual", "#e3f3d3"), G_SENS: ("Sensores", "#dbcbe7"), G_AUTO: ("Autonomía", "#ffefbf"),
             G_ACT: ("Actuadores", "#ffdf7f"), G_DASH: ("Dashboard", "#bfdbef")}
    for gid, (name, fill) in names.items():
        members = [n for n in flows if n.get("g") == gid]
        xs = [n["x"] for n in members]
        ys = [n["y"] for n in members]
        x0, y0 = min(xs) - 110, min(ys) - 40
        flows.append({"id": gid, "type": "group", "z": TAB, "name": name,
                      "style": {"label": True, "fill": fill, "fill-opacity": "0.4"},
                      "nodes": [n["id"] for n in members], "x": x0, "y": y0,
                      "w": max(xs) + 110 - x0, "h": max(ys) + 40 - y0})

    json.dump({"rev": data["rev"], "flows": flows}, open(dst, "w"), ensure_ascii=False, indent=1)
    print(f"{len(flows)} nodos, rev {data['rev']}")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])

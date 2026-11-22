# Brief para Claude Code: autonomía del auto en la Raspberry Pi

Nov 18, 2026 · @Mateo

## Contexto y objetivo

Es la Actividad Final de Cloud Robotics 2026: un auto robot que ya se controla por MQTT, al que se le suma una Raspberry Pi con Node-RED y algo de autonomía. La entrega es el 5 de diciembre de 2026.

Este brief cubre la primera pieza: el esquive autónomo de obstáculos, orquestado en Node-RED. Los sensores extra del kit y la parte cloud vienen después.

Lo que pide el enunciado:

- Raspberry Pi con Node-RED como núcleo de orquestación y control.
- Al menos tres módulos de sensores o actuadores de tipos distintos.
- Dashboard o control local para ver y controlar el robot.
- Algún nivel de autonomía, como evitar obstáculos.
- Comunicación robot–nube, con flujos exportados locales y cloud.

Se evalúa: hardware 25%, inteligencia local 25%, calidad de los flujos Node-RED (claridad, modularidad, documentación) 20%, informe 10%, creatividad 10%, resultados y video 10%.

## Hardware y restricciones

- **Dirección por servo:** el auto gira con un servo conectado a las ruedas delanteras. No puede girar sobre su eje: para reorientarse tiene que avanzar o retroceder con la dirección girada.
- **Ultrasónico frontal fijo:** mide solo hacia donde apunta el auto. Para "mirar" a un lado hay que mover el auto.
- **Buzzer:** ya instalado.
- **Frenado existente:** el auto ya frena solo cerca de un obstáculo; no romper ese comportamiento.
- **Sin sensor trasero:** la marcha atrás es ciega, así que va lenta y corta.
- **Control por MQTT:** ya funciona. El broker, los tópicos y el formato de los mensajes hay que descubrirlos (ver Arquitectura).
- **La Pi no tiene entradas analógicas:** cualquier sensor analógico del kit tiene que pasar por el Arduino.

## Arquitectura

Por ahora todo va junto, en una sola pestaña, con un grupo por bloque. Al terminar se divide en pestañas. Los bloques:

1. **Sensores:** leer y normalizar la distancia. Filtrar lecturas 0 o NaN, que el ultrasónico devuelve cuando no recibe eco.
2. **Autonomía:** la máquina de estados de la sección siguiente.
3. **Actuadores:** traducir `{vel, steer, buzzer}` a lo que el auto entiende hoy por MQTT.
4. **Dashboard:** switch de modo manual/auto, distancia y estado actual.
5. **Cloud:** más adelante; no tocar ahora.

Cada bloque lleva su grupo, nodos `comment` que expliquen qué hace y nombres descriptivos en los nodos.

**Interfaz interna entre bloques** (nombres propuestos):

- Distancia: `msg.payload` en cm.
- Comando: `{vel, steer, buzzer}`, con `vel` de -1 a 1, `steer` de -1 (izquierda) a 1 (derecha) y `buzzer` booleano.
- Modo: `flow.get('modo')`, con valores `manual` o `auto`. La autonomía solo corre en `auto`.

**Descubrir antes de escribir código** (hay acceso por SSH a la Pi):

- Dónde corre el broker MQTT y qué tópicos y formato de payload mueven hoy el auto.
- Qué hardware ejecuta motores y servo (Arduino, ESP o GPIO de la Pi) y cómo llega la lectura de distancia.
- Versión de Node-RED y nodos instalados, en especial el dashboard.
- Qué flujos existen ya: reusarlos y no romper el control manual por MQTT.

Si algo de esto no se puede determinar, preguntar al usuario en vez de suponer.

## Comportamiento de esquive

&#91;embedded content: máquina de estados · 7 estados, 1 salida con buzzer\]

Hace un solo intento: prueba un arco a la izquierda y, si no hay salida, otro a la derecha, y vuelve sobre su camino en marcha atrás con la misma dirección. Si tampoco hay salida a la derecha, pasa a BUZZER.

## Código de referencia

Function node del bloque Autonomía, con 1 entrada y 2 salidas. Es un punto de partida: hay que ajustarlo a lo que se descubra del auto y calibrar tiempos y umbrales en el piso.

```javascript
// Función "Autonomía". Entrada: distancia en cm (~10 Hz). Dos salidas:
// 1) comando {vel, steer, buzzer}   2) estado para dashboard y logs
if (flow.get('modo') !== 'auto') { context.set('s', null); return null; }  // al volver a auto arranca de cero

const D = 50;                                  // cm: umbral de obstáculo y de camino libre
const V = 0.5, V_SLOW = 0.35;                   // velocidades (-1..1)
const T_BACK = 600, T_ARC = 900, T_WAIT = 300;  // ms (calibrar en el piso)

let s = context.get('s') || { st: 'AVANZAR', t0: Date.now(), side: -1 };
const d = Number(msg.payload);
const el = Date.now() - s.t0;
const go = (st) => { s.st = st; s.t0 = Date.now(); };
let cmd = { vel: 0, steer: 0, buzzer: false };  // steer: -1 izq ... +1 der

switch (s.st) {
  case 'AVANZAR':
    cmd = { vel: V, steer: 0, buzzer: false };
    if (d < D) { s.side = -1; go('FRENAR'); }
    break;
  case 'FRENAR':                                // frena y deja asentar el sensor
    if (el > T_WAIT) go('RETRO');
    break;
  case 'RETRO':                                 // retrocede recto para ganar espacio
    cmd = { vel: -V_SLOW, steer: 0, buzzer: false };
    if (el > T_BACK) go('ARCO');
    break;
  case 'ARCO':                                  // avanza girando hacia s.side
    cmd = { vel: V_SLOW, steer: s.side, buzzer: false };
    if (el > T_ARC || d < D) { s.arcMs = el; go('MEDIR'); }
    break;
  case 'MEDIR':                                 // quieto, lee hacia donde quedó apuntando
    if (el > T_WAIT) go(d >= D ? 'AVANZAR' : 'VOLVER');
    break;
  case 'VOLVER':                                // deshace el arco: mismo steer, marcha atrás
    cmd = { vel: -V_SLOW, steer: s.side, buzzer: false };
    if (el > s.arcMs) {
      if (s.side === -1) { s.side = 1; go('ARCO'); }   // probar el otro lado
      else go('BUZZER');                                 // ya probó los dos lados: sin salida
    }
    break;
  case 'BUZZER':                                // sin salida: detenido y buzzer intermitente
    cmd = { vel: 0, steer: 0, buzzer: Math.floor(el / 300) % 2 === 0 };
    break;
}
context.set('s', s);
return [{ payload: cmd }, { payload: { estado: s.st, dist: d, lado: s.side } }];
```

Notas para ajustarlo:

- Deshacer el arco funciona porque retroceder con el mismo ángulo de dirección recorre el mismo camino al revés. Es aproximado: el patinaje del piso lo desvía.
- `T_ARC` y `T_BACK` son tiempos. Si más adelante hay sensor de velocidad (Hall o Light blocking), conviene reemplazarlos por pulsos.
- En `BUZZER` el auto queda detenido con el buzzer intermitente hasta que se cambie a modo manual y se vuelva a `auto`. Si el usuario prefiere otra salida, es un cambio en ese `case`.

## Tareas y criterios de aceptación

Tareas, en este orden:

- [ ] Inspeccionar la Pi por SSH (broker, tópicos, flujos existentes, versión de Node-RED y nodos instalados) y resumirle al usuario lo que se encontró antes de modificar nada.
- [ ] Exportar a JSON los flujos actuales como respaldo.
- [ ] Armar todo en una sola pestaña, con un grupo por bloque (Sensores, Autonomía, Actuadores y Dashboard) y nodos `comment`.
- [ ] Implementar la máquina de estados del código de referencia, adaptando entradas y salidas a lo descubierto.
- [ ] Traducir `{vel, steer, buzzer}` a los mensajes MQTT que el auto ya entiende; mapear `steer` a ángulos del servo respetando sus límites mecánicos.
- [ ] Agregar al dashboard el switch manual/auto y mostrar distancia y estado. Al pasar a manual, mandar `vel: 0`.
- [ ] Registrar cada cambio de estado con su hora, para las métricas del informe.
- [ ] Probar primero con las ruedas en el aire y después en el piso.
- [ ] Cuando todo funcione, dividir el flujo en pestañas junto con el usuario y exportar los flujos finales a JSON.

Criterios de aceptación:

- En modo `auto`, al detectar un obstáculo a menos de 50 cm el auto se detiene, prueba izquierda y derecha, y retoma por el primer lado con 50 cm o más libres.
- Si ninguno de los dos lados tiene salida, queda detenido con el buzzer intermitente.
- En modo `manual` la autonomía no emite comandos y el control por MQTT funciona como antes.
- Sin lecturas de distancia, el auto se detiene.
- Los flujos exportados se importan sin errores en otra instancia de Node-RED.

## Seguridad, pruebas y métricas

- La primera prueba va siempre con las ruedas en el aire; después, en un espacio abierto y despejado, con alguien listo para cortar la alimentación.
- En las primeras pruebas usar `V = 0.3` y `V_SLOW = 0.2`, y subirlos de a poco.
- La marcha atrás es ciega: mantener `T_BACK` corto y tener el modo manual a mano.
- No cambiar el firmware del auto ni la configuración del broker sin avisarle antes al usuario.

Métricas para el informe, registradas con hora en un archivo o en el historial de Node-RED:

- Distancia de frenado a distintas velocidades.
- Tasa de éxito esquivando N obstáculos, anotando cuántas veces se llegó al estado `BUZZER`.
- Tiempo desde la detección del obstáculo hasta retomar la marcha.
- Log de las transiciones de estado.

import json, time
import pygame
import paho.mqtt.client as mqtt

pygame.init()
pygame.joystick.init()
js = pygame.joystick.Joystick(0)

c = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
c.connect("163.10.53.49", 1883)  # IP de la Pi en la red WiFi
c.loop_start()

last = None
while True:
    pygame.event.pump()
    state = {
        "axes": [round(js.get_axis(i), 2) for i in range(js.get_numaxes())],
        "buttons": [js.get_button(i) for i in range(js.get_numbuttons())],
    }
    if state != last:  # solo publica cuando cambia algo
        c.publish("control/gamepad", json.dumps(state))
        last = state
    time.sleep(0.02)

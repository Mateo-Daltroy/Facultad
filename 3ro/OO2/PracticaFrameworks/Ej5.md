
# Contexto Relevante

- Procesar eventos
- Pub/Sub style

# Respuestas

1. El diseño corresponde a un framework, esto se debe a que provee una solucion completa donde el usuario deja el control para que el framework de StreamBeacons se encargue de ejecutar el codigo y hacer los llamados necesarios una vez configurado.

2. Existe inversion de control, se presenta dentro de las interfaces de BeaconEvent, BeaconDispatcher y BeaconListener. Al permitir que el usuario pueda definir implementaciones de las partes que el framework utiliza y asignarlas a traves de los metodos registerListener, dispatch y setDispatcher, observamos que el framework le otorga le control al usuario sobre ciertas partes al usuario final mientras que mantiene el flujo necesario para asegurar el funcionamiento del conjunto de partes.

// Me mate pensando al pedo, es streamBeacons.registerListener(new ConsoleLoggerListener());
3. Para registrar en la consola cada evento despachado seria necesario crear una nueva clase que implemente BeaconDispatcher, en este caso la llamo LoggerDispatcher y su implementacion es la siguiente:

Public class LoggerDispatcher extends DefaultDispatcher {
  public void dispatch(BeaconEvent event) {
    System.out.println(event.getName(), event.getTimestamp()); // El formato es lo de menos
    super.dispatch(event);
  }
}

4. 
  a. La solucion dependera sobre si la restriccion de que no se puede modificar el codigo provisto se mantiene vigente en esta tarea o no. En el caso de que no se mantenga vigente, la solucion es sencilla, se le debe agrega
  a. Para asegurarnos de que los listeners reciban unicamente aquiellos eventos que sean de su interes tenemos que crear un nuevo dispatcher, LazyDispatcher, que solo envie mensajes a quienes desean recibirlos. Esto lo hacemos a traves de un Map<String, List<BeaconListener>> donde el String representa el nombre del evento. Su implementacion se veria asi:

Public class LazyDispatcher extends DefaultDispatcher {
  private Map<String, List<BeaconListener>> data = new HashMap<>();
  private List<BeaconListener> allNotifs = new ArrayList<>();

  public void dispatch(BeaconEvent event) {
    this.data.get(event.getName()).stream().forEach(listener -> listener.onEvent(event));
    this.allNotifs.stream().forEach(listener -> listener.onEvent(event));
  }

  public void register(BeaconListener listener) {
    this.allNotifs.add(listener);
  }

  public void register(BeaconListener listener, List<String> relevantEvents) {
    relevantEvents.stream().forEach(event -> this.registerConnection(event, listener));
  }

  public void registerConnection(String eventName, BeaconListener listener) {
    List currentEvents = this.data.get(eventName);
    if (currentEvents == null) {
      currentEvents = new ArrayList<BeaconListener>();
    }
    currentEvents.add(listener)
    this.data.put(eventName, currentEvents);
    // Once you have at least one explicit event, remove from the allNotifs list
    this.allNotifs.remove(listener);
  } 
}

  b. El listener determina los elementos que le interesan enviandole a su LazyDispatcher un mensaje ya sea con la coleccion de eventos que le interesan o con un solo evento que le interesa. Para mantener la retrocompatibilidad, todos los listeners que se agreguen sin una lista automaticamente escuchan todos los eventos por defecto.

  c. // Gemini dame una pista con este, no estoy completamente seguro de la diferencia entre extension e instanciacion


5.
  a. Falso, el template method consiste en dividir un algoritmo en distintos pasos, proporcionando defaults y hooks para que las clases que implementan los pasos de dicho algoritmo. Como BeaconDispatcher no es un Template, su metodo dispatch tampoco lo es.

  b. Falso, register no es un metodo hook debido a que BeaconDispatcher no es un Template.

  c. Verdadero, onEvent es un hotspot debido a que es una de las partes del sistema hechas para ser implementadas por el usuario consumidor del framework

  d. Falso, un frozenspot es considerado eso debido a que los detalles de su implementacion no pueden ser cambiados por el consumidor del framework. De esta manera, cambiar la instancia del BeaconDispatcher no lo vuelve un hotspot dado que no cambia la manera en la que StreamBeacons lo utiliza

  e. Falso, el diseño permite extender el comportamiento de BeaconDispatcher, como se hace en los ejercicios anteriores. Igualmente, el framework si es de caja blanca por esto mismo.

  f. Falso, el uso de interfaces no es suficiente para identificar si un framework es de caja negra o caja blanca. Si hay una interfaz accessible al cliente para extender (como es el caso en este ejemplo), sabemos que estamos tratando con un framework de caja blanca

  g. Falso, no es suficiente para determinar el tipo de diseño del framework. Este ejemplo es de caja blanca (como ya se explico anteriormente).

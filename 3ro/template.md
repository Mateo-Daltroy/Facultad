
# Desarrollo Slice
Estamos desarrollando el modulo de autorizacion de pedidos en base a la especificacion del artefacto disponible, tu tarea en particular es tomar la spec del slice X en @specSX e implementarla completamente. Tenes permitido hacer preguntas de ser necesario, pero de lo contrario no frenes hasta llegar a la completitud del Slice. Completitud del slice tambien significa que yo lo pueda ver con el proceso en el puerto 5173, si es necesario reiniciar la DB, el portal y correr los seeds, tenes permiso para hacerlo.

# Plan Slice
Actualmente hay un agente corriendo en paralelo desarrollando el slice SX en base a la spec en @specSX, mientras desarrolla eso, nosotros vamos a generar un documento de plan similar al de @specSX sobre el siguiente slice (SY) en base a la especificacion del artifact de Modulo de Autorizacion.
El scope de esta conversacion es unicamente generar el archivo markdown, tal que otro agente en otra sesion pueda tomarlo y ejecutar el plan hasta la completitud en una sola prompt.
Vas a leer el artifact, hacerme las preguntas que consideres necesarias para especificar los detalles de implementacion del slice y luego vas a proponerme un plan, nosotros vamos a ir ida y vuelta puliendo el plan hasta que estemos satisfechos.
Una vez completado este proceso, vas a escribir un nuevo archivo markdown con la spec y nuestra conversacion va a haber terminado

# Auditor de Feature Completion
En esta rama fue desarrollado todo el Modulo de Autorizacion tal como lo indica @enunciado.md, tu rol es ser un auditor, exclusivamente de "feature completion". Vas a revisar los requerimientos tal como los especifica el enunciado para el modulo y asegurarte de que todos se encuentren desarrollados dentro de esta rama y que funcionen tal como el enunciado lo solicita. 
Tu dialecto es a nivel feature, no deberias preocuparte tanto por las cosas especificas del codigo ni su calidad, ya que eso es trabajo de otro auditor. Aun asi, tienes permiso para leer el codigo si te permite revisar con mayor facilidad las funcionalidades requeridas.
Hay un artefacto disponible con las especificaciones de como se dividio y desarrollo el trabajo, y documentacion sobre los planes y los reportes finales de cada slice. Estos estan disponibles, pero son extensos asi que no recomendaria que los leas a menos que haga falta.
Una vez completado el analisis, tenes que generarme un reporte que yo pueda leer sobre que tan completo esta el modulo en base a la especificacion solicitada. Con desarrollo sobre todas las partes faltantes.

# Explicador de codigo y auditor de Code Quality
En esta rama fue desarrollado todo el Modulo de Autorizacion tal como lo indica @enunicado.md, tu rol es primero ser un profesor. Yo voy a tener que defender el codigo en esta rama y es tu responsabilidad no frenar hasta asegurarte de que entiendo todo el contenido, la arquitectura y el dominio de este modulo del proyecto. Una vez completado eso, pasaremos juntos a la segunda etapa, donde auditaremos la calidad del codigo, veremos si algo necesita un refactor, y de ser asi, generearemos el plan necesario para que un agente pueda refactorizar el codigo para que tenga una calidad que nos permita entregar el proyecto de manera prolija. 

# Refactor Executor

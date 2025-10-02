# Respuestas Trabajo Practico 1

##  6. ArrayList vs LinkedList
a. 
    ArrayList ofrece un mejor rendimiento que LinkedList siempre que se quiere acceder a una posicion de memoria en especifico, dado que la estructura de datos interna es un Array, permitiendo un tiempo de acceso con orden O(1)
b. 
    Una LinkedList es mas eficiente al momento de agregar multiples elementos, ya que cada uno se añade de manera individual (a diferencia del arraylist que cuando la cantidad de elementos supera su dimension fisica, la lista entera tiene que volver a copiarse en alguna otra posicion que permita que entre el arreglo). A la vez, es importante saber que la ArrayList no le saca ventaja a la LinkedList cuando el elemento al que se desea acceder es el ultimo o el primero, ambos lo hacen en orden de O(1)
c. 
    Las dos principales diferencias que se encuentran en el manejo en memoria entre las ArrayList y las LinkedList son en la manera interna en la que se representan y en la cantidad de espacio que ocupan. Las ArrayLists se manejan internamente como un arreglo (es decir, como un conjunto de direcciones de memoria continuas), eso permite acceso directo pero demanda que se encuentre un espacio de memoria continuo que pueda contener todo el contenido del arreglo. Las LinkedList por el otro lado se manejan como una estructura de lista convencional, con punteros al nodo anterior y nodo siguiente, esto proporciona la ventaja de que no es necesario realocar informacion cuando queres añadir un nuevo nodo, pero a cambio se ven limitados a un acceso secuencial de los datos y a la vez ocupan mas memoria al tener que guardar los dos punteros de direccion.
d.
    ArrayList: una ArrayList es preferible en las situaciones en las que uno no esta constantemente agregando y removiendo elementos de una lista y donde uno puede desear acceder a alguna posicion en especifico
    LinkedList: una LinkedList es preferible cuando uno va agregando datos (especialmente si se agregan y eliminan por la mitad de la lista, siendo que la ArrayList tiene que reordenar todos los otros elementos del arreglo para mantener la continuidad) a lo largo de la ejecucion del programa y quiere tratar con todos los elementos en conjunto en lugar de buscar alguna posicion en particular.

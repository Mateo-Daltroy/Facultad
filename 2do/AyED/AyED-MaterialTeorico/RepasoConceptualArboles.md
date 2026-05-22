# HEAP BINARIA

## Agregar Dato
Cuando se agrega un dato, este solo puede entrar dentro de la posicion que le corresponde, es decir, al final del arreglo. Luego para mantener la propiedad de orden, el dato va subiendo (percolate up) hasta que llega a la posicion donde es mayor/menor (min/max heap) que el dato padre.

## Eliminar Dato
Cuando se elimina un dato de una heap, pasa lo opuesto, siempre se elimina el dato en la raiz dado que este es el de maxima prioridad en todo momento. Esto geenra una estructura rota en el arbol que si se quiere mantener se lleva a la raiz el dato al final del vector y luego cae hasta la posicion que le corresponde en base a su valor y la politica de min/max heap.

## BuildHeap
> Dado un vector con I datos
El algoritmo de buildheap consiste en comenzar en la mitad del vector (la parte entera, siendo que necesitamos al nodo padre del ultimo dato) y percolar hacia abajo hasta la posicion que le corresponda. Luego repetir el proceso para todos los datos menores a nuestra base, construyendo asi multiples heaps y fusionandolas de maneras que se mantengan ordenadas

## HeapSort
El algoritmo de heapsort ordena una heap en una lista a traves de
    1. construir una MaxHeap para orden ascendente - MinHeap para orden descendente
    2. "eliminar logicamente" (poner al final del vector) el mayor elemento, traer el ultimo elemento del vector a la raiz, percolarlo hacia abajo y reducir el tamaño del vector
    3. repetir el paso 2 hasta que no quede MaxHeap

# ARBOLES DE EXPRESION

## Expresion In-Fija
Para transformar una expresion infija en un arbol de expresion es simple, dado que la expresion esta compuesta de manera natural para que vos la veas y puedas reconstruirla en base a los parentesis y propiedades de orden matematico

## Expresion Post-Fija
Para transformar una expresion postfija en un arbol de expresion es necesario utilizar una pila donde si el nodo es un operando se apila directamente y si el nodo es un operador, desapila dos datos para establecerlos como hijos izquierdo y derecho antes de apilarse a si mismo como raiz de un subarbol

## Expresion Pre-Fija
En una expresion prefija siempre se encuentra primero un operando seguido por su hijo izquierdo y su hijo derecho, esta propiedad se aplica de manera recursiva hasta fomrar el arbol completo (siendo una expresion prefija, sabemos que el primer dato es la raiz) 

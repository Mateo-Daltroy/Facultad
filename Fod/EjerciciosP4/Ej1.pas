program ej1;
const
    M = ...;
type
    alumno = record 
	nombre: string[10];
	apellido: string[15];
	dni: integer;
	legajo: integer;
	ingreso: integer;
    end;

    TNodo = record
	hijos = array [1..M] of integer;
	datos = array [1..M-1] of alumno;
	cantDatos = integer;
    end;
    
    arbolB = file of TNodo;

//B. Asumiendo que un alumno ocupa 64bytes, un integer 4, y el nodo arbolB tiene un tamaño de 512:
    // M = 7 -> 7*64 + 6*4 + 4 => 476
    // M = 8 => 544
// El mayor valor de M que entra es 7
// 512 = (M-1)*64 + M*4 + 4
// 512 = 64M + 4M - 60
// 452 / 68 = M
// 6.66 = M
// El mayor valor de M que entra es 6 segun la ecuacion provista
//
// C. Organizar el archivo del arbol con la informacion completa de los alumnos en vez de utilizar un indice hace que los registros ocupen mucho mas espacio, limitando nuestro orden M. Si utilizamos un indice al NRR de otro archivo que contiene todos los datos, nos encontramos con que cada dato ocupa 4byes en vez de 64, permitiendonos guardar casi 8 veces la cantidad de datos (casi, porque el tamaño del regsitro con punteros a los hijos tambien tendria que crecer)
//
// D. Hay 2 opciones principales, DNI y Legajo, dado que son identificadores univocos de cada uno de los elementos, que nos permite garantizar la falta de duplicados. Yo utilizaria el DNI, porque siempre es conocido por el alumno y no cambia de formato
//
// E. Buscar un alumno por DNI es sencillo, si contamos comparaciones consecutivas de datos para encontrar el hijo adecuado como un unico acceso a un nodo (simplificacion necesaria para poder estimar la cantidad de accesos), nos encontramos con que en el peor de los casos, siendo este que el dato se encuentre en una hoja, encontrariamos el dato en Log(N), siendo N la cantidad de elementos y con una cantidad de lecturas a nodos igual a la altura del arbol. En el mejor caso, siendo que el elemento sea encontrado en el primer dato del nodo raiz, lo encontrariamos en un solo acceso a un nodo.
//
// F. En el peor de los casos, tendrias que leer absolutamente todos los nodos en el arbol para encontrar el alumno
//

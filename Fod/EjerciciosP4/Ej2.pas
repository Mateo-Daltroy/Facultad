program Ej2;
const
    M = 6;
type
    alumno = record
	nombre: string[10];
	apellido: string[15];
	dni: integer;
	legajo: integer;
	ingreso: integer;
    end;

    TNodo = record
	cantClaves: integer;
	claves: array [1..M-1] of integer; //dni, utilizado para ordenar
	enlaces: array [1..M-1] of integer; //NRR del alumno al que le corresponde el dni
	hijos: array [1..M] of integer; //NRR de los hijos
    end;

    ArchAlu = file of alumno;
    arbolB = file of TNodo;

//B. 
//N = (M-1) * A + M * B + C
// 512 = (M-1) * 4 + M * 4 + 4 + (M-1)*4
// 512 = 4M - 4 + 4M + 4 + 4M - 4
// 516 = 12M
// 43 = M
//
// C. El uso de un orden mayor nos permite guardar mas datos por nodo, minimizando asi la altura, cantidad de nodos y por consecuencia cantidad de accesos por busqueda. Volviendo toda la estructura mas eficiente.
//
// D. La busqueda del DNI 12345678 varia dependiendo de la estructura del arbol y la cantidad de datos que se hayan ingresado. Aun asi lo que hace es ingresar primero por la raiz del arbol, buscarse dentro, y si no se encuentra, se busca de nuevo dentro del hijo que corresponde en base a las claves (el hijo que se encuentre luego del numero mas grande que todavia sea menor que 12345678). Eso se repite de manera recursiva hasta que se encuentra el dato o se llega a ver que no se encuentra en la hoja correspondiente, por lo cual se puede determinar que no existe el dato en la estructura.
//
// E. Para poder buscar por legajo, uno tendria que buscar de manera ineficiente, recorriendo todo el arbol y perdiendo las ventajas de la estructura. O tendria que crear una nueva estructura en base a ArchAlu que se encuentre ordenada por legajo.
//
// F. El principal problema de querer buscar multiples datos en base a un arbol indizado es que no minimizan los accesos a memoria dado que no puede garantizar que los datos se encuentren cerca unos de otros dentro del ArchAlu, porque no sabemos si esta ordenado, o en base a que esta ordenado en el caso de estarlo. Ademas de todo esto, uno tiene que subir y bajar multiples niveles del arbol dentro de su recorrido para poder encontrar todos los datos que necesita?

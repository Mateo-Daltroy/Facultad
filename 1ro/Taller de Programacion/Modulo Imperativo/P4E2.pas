program P4E2;
type
    prestamoCod = record
        isbn: integer;
        socio: integer;
        fecha: string;
        cant: integer;
    end;
	
<<<<<<< Updated upstream
=======
	prestamoISBN = record
		isbn: integer;
		l: lista;
	end;
	
>>>>>>> Stashed changes
    prestamoList = record
        socio: integer;
        fecha: string;
        cant: integer;
    end;

    arbol1 = ^nodo1;
    nodo1 = record
        d: prestamoCod;
        HI: arbol1;
        HD: arbol1;
    end;

    lista = ^nodo2;
    nodo2 = record
        d: prestamoList;
        sig: lista;
    end;
<<<<<<< Updated upstream
	
	prestamoISBN = record
		isbn: integer;
		l: lista;
	end;

    arbol2 = ^nodo3;
=======

    arbol2: ^nodo3;
>>>>>>> Stashed changes
    nodo3 = record
        d: prestamoISBN;
        HI: arbol2;
        HD: arbol2;
    end;

<<<<<<< Updated upstream
	datoF = record
		isbn: integer;
		cont: integer;
	end;

	listaF = ^nodoF;
	nodoF = record
		d: datoF;
		sig: listaF;
	end;

=======
>>>>>>> Stashed changes

// Inicio Modulos


<<<<<<< Updated upstream
procedure leerPrestamo(var cod: prestamoCod; var list: prestamoList; var aux: integer);
=======
procedure leerPrestamo(var cod: prestamoCod; var list: prestamoList; var aux; integer);
>>>>>>> Stashed changes
begin
    write('Introducir codigo ISBN: ');
    readln(cod.isbn);
    if (cod.isbn <> -1) then
    begin
        aux:= cod.isbn;
        write('Introducir numero de socio: ');
        readln(cod.socio);
        list.socio:= cod.socio;
        write('Introducir fecha de prestamo: ');
        readln(cod.fecha);
        list.fecha:= cod.fecha;
        write('Introducir duracion del prestamo: ');
        readln(cod.cant);
        list.cant:= cod.cant;
    end;
<<<<<<< Updated upstream
    writeln;
=======
>>>>>>> Stashed changes
end;

procedure generarArbol1(var a: arbol1; p: prestamoCod);
begin
	if (a = nil) then
	begin
		new(a);
		a^.d:= p;
		a^.HI:= nil;
		a^.HD:= nil;
	end
	else if (a^.d.isbn >= p.isbn) then
		generarArbol1(a^.HI, p)
	else
		generarArbol1(a^.HD, p);
end;

procedure agregarAdelante(var l: lista; p: prestamoList);
var
	nuevo: lista;
begin
	new(nuevo);
	nuevo^.d:= p;
	nuevo^.sig:= l;
	l:= nuevo;
end;

procedure generarArbol2 (var a: arbol2; p:prestamoList; aux: integer);
begin
	if (a = nil) then
	begin
		new(a);
		a^.d.l:= nil;
		a^.d.isbn:= aux;
		a^.HI:= nil;
		a^.HD:= nil;
		agregarAdelante(a^.d.l, p);
	end
	else if (a^.d.isbn = aux) then
		agregarAdelante(a^.d.l, p)
<<<<<<< Updated upstream
	else if (a^.d.isbn > aux) then
=======
	else if (a^.d.isbn >= aux) then
>>>>>>> Stashed changes
		generarArbol2(a^.HI, p, aux)
	else
		generarArbol2(a^.HD, p, aux);
end;

procedure leerDatos (var a1: arbol1; var a2: arbol2);
var
    aux: integer;
    pres1: prestamoCod;
    pres2: prestamoList;
begin
    a1:= nil;
    a2:= nil;
    leerPrestamo(pres1, pres2, aux);
    while (pres1.isbn <> -1) do
    begin
        generarArbol1(a1, pres1);
        generarArbol2(a2, pres2, aux);
        leerPrestamo(pres1, pres2, aux);
    end;
end;

function isbnMayor (a: arbol1): integer;
begin
	if (a^.HD <> nil) then
		isbnMayor:= isbnMayor(a^.HD)
	else
		isbnMayor:= a^.d.isbn;
end;

function isbnMenor (a: arbol2): integer;
begin
	if (a^.HI <> nil) then
		isbnMenor:= isbnMenor(a^.HI)
	else
		isbnMenor:= a^.d.isbn;
end;

procedure cantPrestA1(a: arbol1; var cont: integer; busq: integer);
// Ini cont en 0 desde afuera
begin
	if (a <> nil) then
	begin
<<<<<<< Updated upstream
		cantPrestA1(a^.HI, cont, busq);
		if (a^.d.socio = busq) then
			cont:= cont + 1;
		cantPrestA1(a^.HD, cont, busq);
=======
		cantPrestA1(a^.HI, cont);
		if (a^.d.socio = busq) then
			cont:= cont + 1;
		cantPrestA1(a^.HD, cont);
>>>>>>> Stashed changes
	end;
end;

function contarEnLista(l: lista; busq: integer): integer;
var
	cont: integer;
begin
	cont:= 0;
	while (l <> nil) do
	begin
		if (l^.d.socio = busq) then
			cont:= cont + 1;
		l:= l^.sig;
	end;
	contarEnLista:= cont;
end;

procedure cantPrestA2(a: arbol2; var cont: integer; busq: integer);
// Ini cont en 0 desde afuera
begin
	if (a <> nil) then
	begin
<<<<<<< Updated upstream
		cantPrestA2(a^.HI, cont, busq);
		cont:= cont + contarEnLista(a^.d.l, busq);
		cantPrestA2(a^.HD, cont, busq);
	end;
end;

procedure insertarOrdenado(p: prestamoCod; var l: listaF);
var
	actual, ant, nuevo: listaF;
begin
	new(nuevo);
	nuevo^.d.cont:= 1;
	nuevo^.d.isbn:= p.isbn;
	nuevo^.sig:= nil;
	if (l = nil) then
		l:= nuevo
	else
// Esta parte del insertarOrdenado da error, no se porque
	begin
		actual:= l;
		ant:= l;
		while (actual <> nil) and (actual^.d.isbn < nuevo^.d.isbn) do
		begin
			ant:= actual;
			actual:= actual^.sig;
		end;
		if (actual = l) then
		begin
			nuevo^.sig:= l;
			l:= nuevo;
		end
		else
		begin
			if (actual^.d.isbn = nuevo^.d.isbn) then
				actual^.d.cont:= actual^.d.cont + 1
			else
			begin
				ant^.sig:= nuevo;
				nuevo^.sig:= actual;
			end;
		end;
	end;
end;

procedure generarListaF(a: arbol1; var l: listaF);
// Iniciar lista en nil desde afuera
begin
	if (a <> nil) then
	begin
		generarListaF(a^.HI, l);
		insertarOrdenado(a^.d, l);
		generarListaF(a^.HI, l);
	end;
end;

procedure mostrarLista(l: listaF);
begin
	while (l <> nil) do
	begin
		write('El libro bajo el codigo ');
		write(l^.d.isbn);
		write(' fue prestado ');
		write(l^.d.cont);
		writeln(' veces.');
		l:= l^.sig;
	end;
end;

procedure prestamosEntre1(a: arbol1; var cont: integer; max, min: integer);
begin
	if (a <> nil) then
	begin
		if (a^.d.isbn > max) then
			prestamosEntre1(a^.HI, cont, max, min)
		else if (a^.d.isbn < min) then
			prestamosEntre1(a^.HD, cont, max, min)
		else
		begin
			cont:= cont + 1;
			prestamosEntre1(a^.HI, cont, max, min);
			prestamosEntre1(a^.HD, cont, max, min)
		end;
	end;
end;

function contarNodos(l: lista): integer;
var
	cont: integer;
begin
	cont:= 0;
	while (l <> nil) do
	begin
		cont:= cont + 1;
		l:= l^.sig;
	end;
	contarNodos:= cont;
end;

procedure prestamosEntre2(a: arbol2; var cont: integer; max, min: integer);
begin
	if (a <> nil) then
	begin
		if (a^.d.isbn > max) then
			prestamosEntre2(a^.HI, cont, max, min)
		else if (a^.d.isbn < min) then
			prestamosEntre2(a^.HD, cont, max, min)
		else
		begin
			cont:= cont + contarNodos(a^.d.l);
			prestamosEntre2(a^.HI, cont, max, min);
			prestamosEntre2(a^.HD, cont, max, min)
		end;
	end;
end;
=======
		cantPrestA1(a^.HI, cont);
		cont:= cont + contarEnLista(a^.d.l, busq);
		cantPrestA1(a^.HD, cont);
	end;
end;

// Punto F
>>>>>>> Stashed changes

// Inicio Programa Principal

var
<<<<<<< Updated upstream
	a1: arbol1;
	a2: arbol2;
	cont, busq, max, min: integer;
	l: listaF;
begin
	leerDatos(a1, a2);
	write('El mayor ISBN es: ');
	writeln(isbnMayor(a1));
	write('El menor ISBN es: ');
	writeln(isbnMenor(a2));
	write('Introducir numero de socio a buscar: ');
	readln(busq);
	cont:= 0;
	cantPrestA1(a1, cont, busq);
	write('Ese socio recibio ');
	write(cont);
	writeln(' prestamos');
	write('Introducir numero de socio a buscar: ');
	readln(busq);
	cont:= 0;
	cantPrestA2(a2, cont, busq);
	write('Ese socio recibio ');
	write(cont);
	writeln(' prestamos');
{
	l:= nil;
	generarListaF(a1, l);
	mostrarLista(l);
}
	write('Introducir valor minimo: ');
	readln(min);
	write('Introducir valor maximo: ');
	readln(max);
	cont:= 0;
	prestamosEntre1(a1, cont, max, min);
	writeln(cont);
	cont:= 0;
	prestamosEntre2(a2, cont, max, min);
	writeln(cont);
end.
=======
begin

end;
>>>>>>> Stashed changes

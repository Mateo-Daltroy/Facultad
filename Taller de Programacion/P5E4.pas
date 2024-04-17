//P4E4.pas
program P5E4;
type
	reclamo = record
		ano: integer;
		cod: integer;
		tipo: integer;
	end;
	
	lista = ^nodo2;
	nodo2 = record
		d: reclamo;
		sig: lista;
	end;
	
	persona = record
		dni: integer;
		l: lista;
		tot: integer;
	end;
	
	arbol = ^nodo;
	nodo = record
		d: persona;
		HI: arbol;
		HD: arbol;
	end;
	
	lista2 = ^nodo3;
	nodo3 = record
		d: integer;
		sig: lista2;
	end;

// Inicio Modulos


procedure leerReclamo (var r: reclamo; var dni: integer);
begin
	write('Ingresar codigo de reclamo: ');
	readln(r.cod);
	if (r.cod <> -1) then
	begin
		write('Ingresar anio del reclamo: ');
		readln(r.ano);
		write('Ingresa tipo de reclamo: ');
		readln(r.tipo);
		write('Ingresar dni de la persona: ');
		readln(dni);
	end;
	writeln;
end;

procedure agregarAdelante (var l: lista; r: reclamo);
var
	nuevo: lista;
begin
	new(nuevo);
	nuevo^.d:= r;
	nuevo^.sig:= l;
	l:= nuevo;
end;

procedure armarArbol (var a: arbol; r: reclamo; dni: integer);
begin
	if (a = nil) then
	begin
		new(a);
		a^.HI:= nil;
		a^.HD:= nil;
		new(a^.d.l);
		a^.d.l^.d:= r;
		a^.d.l^.sig:= nil;
		a^.d.dni:= dni;
		a^.d.tot:= 1;
	end
	else if (a^.d.dni = dni) then
	begin
		agregarAdelante(a^.d.l, r);
		a^.d.tot:= a^.d.tot + 1;
	end
	else if (a^.d.dni < dni) then
		armarArbol(a^.HD, r, dni)
	else
		armarArbol(a^.HI, r, dni);
end;

procedure leerDatos (var a: arbol);
var
	r: reclamo;
	dni: integer;
begin
	a:= nil;
	leerReclamo(r, dni);
	while (r.cod <> -1) do
	begin
		armarArbol(a, r, dni);
		leerReclamo(r, dni);
	end;
end;

function reclamosDNI (a: arbol; dni: integer): integer;
begin
	if (a = nil) then
		reclamosDNI:= -1 //Dni no encontrado
	else if (a^.d.dni = dni) then
		reclamosDNI:= a^.d.tot
	else if (a^.d.dni > dni) then
		reclamosDNI:= reclamosDNI(a^.HI, dni)
	else
		reclamosDNI:= reclamosDNI(a^.HD, dni);
end;

procedure reclamosEntre (a: arbol; min, max: integer; var cont: integer);
// Ini cont en 0 desde afuera
begin
	if (a <> nil) then
	begin
		if (a^.d.dni < min) then
			reclamosEntre(a^.HD, min, max, cont)
		else if (a^.d.dni > max) then
			reclamosEntre(a^.HI, min, max, cont)
		else
		begin
			cont:= cont + a^.d.tot;
			reclamosEntre(a^.HI, min, max, cont);
			reclamosEntre(a^.HD, min, max, cont);
		end;
	end;
end;

procedure agregarCoinciden (l: lista; var l2: lista2; ano: integer);
var
	nuevo: lista2;
begin
	while (l <> nil) do
	begin
		if (l^.d.ano = ano) then
		begin
			new(nuevo);
			nuevo^.d:= l^.d.cod;
			nuevo^.sig:= l2;
			l2:= nuevo;
		end;
		l:= l^.sig;
	end;
end;

procedure reclamosAno (a: arbol; ano: integer; var l: lista2);
// Ini l2 en nil desde afuera
begin
	if (a <> nil) then
	begin
		reclamosAno(a^.HI, ano, l);
		agregarCoinciden(a^.d.l, l, ano);
		reclamosAno(a^.HD, ano, l);
	end;
end;

// Modulo debugging
procedure imprimirLista (l: lista2);
begin
	while (l <> nil) do
	begin
		write(l^.d);
		write(', ');
		l:= l^.sig;
	end;
	writeln;
end;
// Modulo debugging


// Inicio Programa Principal

var
	a: arbol;
	dni, min, max, aux, ano: integer;
	l2: lista2;
begin
	leerDatos(a);
	write('Introducir dni a buscar: ');
	readln(dni);
	writeln(reclamosDNI(a, dni));
	write('Introducir dni minimo a buscar: ');
	readln(min);
	write('Introducir dni maximo a buscar: ');
	readln(max);
	aux:= 0;
	reclamosEntre(a, min, max, aux);
	writeln(aux);
	l2:= nil;
	write('Introducir anio a buscar: ');
	readln(ano);
	reclamosAno(a, ano, l2);
	imprimirLista(l2);
end.

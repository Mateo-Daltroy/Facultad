program P4E4;
const
	maxMaterias = 30;
type
	notasPos = -1..10; // -1 Ini
	cantMaterias = 1..maxMaterias;
	
	vectorAlu = array [cantMaterias] of notasPos;
	
	alumno = record
		cod: integer;
		vect: vectorAlu;
	end;
	
	arbol = ^nodo1;
	nodo1 = record
		d: alumno;
		HI: arbol;
		HD: arbol;
	end;
	
	fin = record
		cod: integer;
		nota: notasPos;
	end;
	
	lista = ^nodo2;
	nodo2 = record
		d: fin;
		sig: lista;
	end;
	
	vectorMat = array [cantMaterias] of lista;
	
	promedio = record
		cod: integer;
		prom: real;
	end;
	
	lista2 = ^nodo3;
	nodo3 = record
		d: promedio;
		sig: lista2;
	end;

// Inicio Modulos


procedure iniVectMat (var v: vectorMat);
var
	i: cantMaterias;
begin
	for i:= 1 to maxMaterias do
	begin
		v[i]:= nil;
	end;
end;

procedure leerFinal (var f: fin; pos: cantMaterias);
begin
	write('Introducir nota final: ');
	readln(f.nota);
	if (f.nota <> -1) then
	begin
		write('Introducir codigo de materia: ');
		readln(pos);
		write('Introducir codigo de alumno: ');
		readln(f.cod);
	end;
	writeln;
end;

procedure agregarAdelante (var l: lista; f: fin);
var
	nuevo: lista;
begin
	new(nuevo);
	nuevo^.d:= f;
	nuevo^.sig:= l;
	l:= nuevo;
end;

procedure iniVectAlu (var v: vectorAlu);
var
	i: integer;
begin
	for i:= 1 to maxMaterias do
	begin
		v[i]:= -1;
	end;
end;

procedure armarArbol (var a: arbol; f: fin; pos: cantMaterias);
begin
	if (a = nil) then
	begin
		new(a);
		a^.HI:= nil;
		a^.HD:= nil;
		iniVectAlu(a^.d.vect);
		a^.d.cod:= f.cod;
		a^.d.vect[pos]:= f.nota;
	end
	else if (a^.d.cod = f.cod) then
		a^.d.vect[pos]:= f.nota
	else if (a^.d.cod >= f.cod) then
		armarArbol(a^.HI, f, pos)
	else
		armarArbol(a^.HD, f, pos);
end;

procedure leerDatos (var a: arbol; var v: vectorMat);
var
	pos: cantMaterias;
	f: fin;
begin
	a:= nil;
	iniVectMat(v);
	leerFinal(f, pos);
	while (f.nota <> -1) do
	begin
		if (f.nota >= 4) then
			armarArbol(a, f, pos); // El dato en el vector sale de aca insertado
		agregarAdelante(v[pos], f);
		leerFinal(f, pos);
	end;
end;

procedure agregarAdelante2 (var l: lista2; prom: real; cod: integer);
var
	nuevo: lista2;
begin
	new(nuevo);
	nuevo^.sig:= l;
	nuevo^.d.prom:= prom;
	nuevo^.d.cod:= cod;
	l:= nuevo;
end;

function promedioNotas(v: vectorAlu): real;
var
	i, cont, tot: integer;
	aux: real;
begin
	cont:= 0;
	tot:= 0;
	for i:= 1 to maxMaterias do
	begin
		if (v[i] <> -1) then 
		begin
			cont:= cont + 1;
			tot:= tot + v[i];
		end;
	end;
	aux:= tot / cont;
	promedioNotas:= aux;
end;

procedure listaPromMayor (a: arbol; var l: lista2; minimo: integer);
// Ini lista2 en nil desde afuera
var
	aux: real;
begin
	if (a <> nil) then
	begin
		if (a^.d.cod < minimo) then
			listaPromMayor(a^.HD, l, minimo)
		else
		begin
			aux:= promedioNotas(a^.d.vect);
			agregarAdelante2(l, aux, a^.d.cod);
			listaPromMayor(a^.HI, l, minimo);
			listaPromMayor(a^.HD, l, minimo);
		end;
	end;
end;

function cumpleCant(v: vectorAlu; comp: cantMaterias): boolean;
var
	i, aux: integer;
begin
	aux:= 0;
	for i:= 1 to maxMaterias do
	begin
		if (v[i] <> -1) then
			aux:= aux + 1;
	end;
	if (aux = comp) then
		cumpleCant:= true
	else
		cumpleCant:= false;
end;

procedure cantAlumnosAp (a: arbol; min, max: integer; var cant: integer; comp: cantMaterias);
// Ini cant en 0 desde afuera
begin
	if (a <> nil) then
	begin
		if (a^.d.cod < min) then
			cantAlumnosAp(a^.HD, min, max, cant, comp)
		else if (a^.d.cod > max) then
			cantAlumnosAp(a^.HI, min, max, cant, comp)
		else
		begin
			cantAlumnosAp(a^.HI, min, max, cant, comp);
			cantAlumnosAp(a^.HD, min, max, cant, comp);
			if (cumpleCant(a^.d.vect, comp)) then
				cant:= cant + 1;
		end;
	end;
end;

// Inicio Modulos debugging

function finalesAp(v: vectorAlu): integer;
var
	cont, i: integer;
begin
	cont:= 0;
	for i:= 1 to maxMaterias do
	begin
		if (v[i] <> -1) then
		begin
			write('final aprobado');
			cont:= cont + 1;
		end;
		write(i); write(', ');
		writeln(v[i]);
	end;
	finalesAp:= cont;
end;

procedure mostrarArbol(a: arbol);
begin
	if (a <> nil) then
	begin
		mostrarArbol(a^.HI);
		write('codigo '); write(a^.d.cod);
		write(' aprobo '); write(finalesAp(a^.d.vect));
		writeln(' finales.');
		mostrarArbol(a^.HD);
	end;
end;

procedure mostrarLista(l: lista2);
begin
	while (l <> nil) do
	begin
		writeln(l^.d.cod);
	end;
end;

// Fin Modulos debugging


// Inicio Programa Principal

var
	a: arbol;
	v: vectorMat;
	l2: lista2;
	max, min, cant: integer;
	comp: cantMaterias;
begin
	leerDatos(a, v);
	mostrarArbol(a);
	l2:= nil;
	write('Introducir codigo minimo B: ');
	readln(min);
	listaPromMayor(a, l2, min);
	mostrarLista(l2);
	write('Introducir codigo minimo C: ');
	readln(min);
	write('Introducir codigo maximo C: ');
	readln(max);
	write('Introducir cuantos finales debe aprobar: ');
	readln(comp);
	cant:= 0;
	cantAlumnosAp(a, min, max, cant, comp);
	writeln(cant);
end.

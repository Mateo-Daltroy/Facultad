program P1E3;
const
	maxGeneros = 8;
type

	cantGeneros = 1..maxGeneros;
	
	pelicula = record
		cod: integer;
		gen: cantGeneros;
		pun: real;
	end;
	
	peliB = record
		cod: integer;
		pun: real;
	end;
	
	lista = ^nodo;
	nodo = record
		d: pelicula;
		sig: lista;
	end;
	
	vector = array [cantGeneros] of peliB;

// Inicio Modulos

procedure leerPelicula (var p: pelicula);
begin
	write('Introducir codigo de la pelicula: ');
	readln(p.cod);
	if (p.cod <> -1) then
	begin
		write('Introducir genero de la pelicula: ');
		readln(p.gen);
		write('Introducir puntaje de la pelicula: ');
		readln(p.pun);
	end;
	writeln;
end;

procedure agregarFinal (var l: lista; nuevo: lista);
var
	aux: lista;
begin
	if (l = nil) then
		l:= nuevo
	else
	begin
		aux:= l;
		while (aux^.sig <> nil) do
		begin
			aux:= aux^.sig;
		end;
		aux^.sig:= nuevo;
	end;
end;

procedure leerDatos (var l: lista);
var
	aux: lista;
begin
	new(aux);
	leerPelicula(aux^.d);
	while (aux^.d.cod <> -1) do
	begin
		agregarFinal(l, aux);
		leerPelicula(aux^.d);
	end;
end;

procedure iniVect (var v: vector);
var
	i: integer;
begin
	for i:= 1 to maxGeneros do
	begin
		v[i].cod:= -1;
		v[i].pun:= -1;
	end;
end;

procedure generarVector(l: lista; var v: vector);
begin
	iniVect(v);
	while (l <> nil) do
	begin
		if (l^.d.pun > v[l^.d.cod].pun) then
		begin
			v[l^.d.cod].pun:= l^.d.pun;
			v[l^.d.cod].cod:= l^.d.cod;
		end;
		l:= l^.sig;
	end;
end;

procedure seleccion (var v:vector);
var
	i, j, pos, dimL: cantGeneros;
	aux: peliB;
begin
	dimL:= maxGeneros;
	for i:= 1 to (dimL - 1) do
	begin
		pos:= i;
		for j:= (i+1) to dimL do
		begin
			if (v[j].pun > v[pos].pun) then
				pos:= j;
		end;
		aux:= v[i];
		v[i]:= v[pos];
		v[pos]:= aux;
	end;
end;

procedure mostrarMayMen (v: vector);
begin
	write('La pelicula con mayor puntaje es: ');
	writeln(v[1].cod);
	write('Con un puntaje de: ');
	writeln(v[1].pun);
	writeln;
	write('La pelicula con menor puntaje es: ');
	writeln(v[maxGeneros].cod);
	write('Con un puntaje de: ');
	writeln(v[maxGeneros].pun);
end;


// Inicio Programa Principal

var
	v: vector;
	l: lista;
begin
	leerDatos(l);
	generarVector(l, v);
	seleccion(v);
	mostrarMayMen(v);
end.

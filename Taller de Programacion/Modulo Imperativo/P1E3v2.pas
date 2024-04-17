program P1E3v2;
const
	maxGeneros = 8;
type
	cantGeneros = -1..maxGeneros;
	
	pelicula = record
		cod: integer;
		pun: real;
	end;
	
	lista = ^nodo;
	nodo = record
		d: pelicula;
		sig: lista;
	end;
	
	vector = array [cantGeneros] of lista;
	
	vector2 = array [cantGeneros] of pelicula;


// Inicio Modulos


procedure leerPelicula (var p: pelicula; var g: cantGeneros);
begin
	write('Introducir codigo del genero: ');
	readln(g);
	if (g <> -1) then
	begin
		write('Introducir codigo de la pelicula: ');
		readln(p.cod);
		write('Introducir puntaje de la pelicula: ');
		readln(p.pun);
	end;
end;

procedure insertarOrdenado (var pI:lista; valor:pelicula);
var
	actual, anterior, nuevo: lista;
begin
	new(nuevo);
	nuevo^.d:= valor;
	nuevo^.sig:= nil;
	if (pI = nil) then
		pI:= nuevo
	else
	begin
		actual:= pI;
		anterior:= pI;
		while (actual <> nil) and (actual^.d.pun > nuevo^.d.pun) do
		begin
			anterior:= actual;
			actual:= actual^.sig;
		end;
		if (actual = pI) then
		begin
			nuevo^.sig:= pI;
			pI:= nuevo;
		end
		else
		begin
			anterior^.sig:= nuevo;
			nuevo^.sig:= actual;
		end;
	end;
end;

procedure leerDatos (var v: vector);
var
	p: pelicula;
	g: cantGeneros;
begin
	leerPelicula(p, g);
	while (g <> -1) do
	begin
		insertarOrdenado(v[g], p);
		leerPelicula(p, g);
	end;
end;

procedure generarVect2 (v: vector; var v2: vector2);
var
	i: cantGeneros;
begin
	for i:= 1 to maxGeneros do
	begin
		v2[i]:= v[i]^.d;
	end;
end;

procedure seleccion (var v: vector2; dimL: cantGeneros);
var
	i, j, pos: cantGeneros;
	aux: pelicula;
begin
	dimL:= maxGeneros;
	for i:= 1 to (dimL - 1) do
	begin
		pos:= i;
		for j:= (i-1) to dimL do
		begin
			if (v[j].pun > v[pos].pun) then
				pos:= j;
		end;
		aux:= v[pos];
		v[pos]:= v[i];
		v[i]:= aux;
	end;
end;

procedure mostrarMayMen(v: vector2);
begin
	write('El codigo de la pelicula con mayor puntaje es: ');
	writeln(v[1].cod);
	write('El codigo de la pelicula con menor puntaje es: ');
	writeln(v[maxGeneros].cod);
end;


// Inicio Programa Principal

var
begin
end;

program P2E1;
const
	maxChar = 10;
type
	cantChar = 1..maxChar;
	
	vector = array [cantChar] of char;
	
	lista = ^nodo;
	nodo = record
		d: char;
		sig: lista;
	end;

// Inicio Modulos

procedure lecturaA (var v: vector; var dimL: cantChar);
// dimL se tiene que inicializar en 1 desde afuera
begin
	write('Insertar caracter: ');
	readln(v[dimL]);
	if (v[dimL] <> '.') and (dimL < maxChar) then
	begin
		dimL:= dimL + 1;
		lecturaA(v, dimL);
	end;
end;

procedure imprimirVect (v: vector; dimL: cantChar);
var
	i: cantChar;
begin
	for i:= 1 to (dimL - 1) do
	begin
		write(v[i]);
	end;
	writeln(v[dimL]);
end;

procedure imprimirVectRec (v: vector; dimL: cantChar; var cont: cantChar);
// Inicializar cont afuera en 1
begin
	write(v[cont]);
	if (cont < dimL) then
	begin
		cont:= cont + 1;
		imprimirVectRec(v, dimL, cont);
	end
	else
		writeln;
end;

procedure lecturaD (var cont: integer);
// Inicializar cont afuera en 1
var
	aux: char;
begin
	write('Introducir un caracter: ');
	readln(aux);
	if (aux <> '.') then
	begin
		cont:= cont + 1;
		lecturaD(cont);
	end
	else
	begin
		write('Se leyeron ');
		write(cont);
		writeln(' caracteres.');
	end;
end;


procedure lecturaE (var l: lista);
var
	aux: lista;
begin
	new(aux);
	write('Introducir un caracter: ');
	readln(aux^.d);
	aux^.sig:= nil;
	l:= aux;
	if (aux^.d <> '.') then
		lecturaE(l^.sig);
end;

procedure imprimirListaOrd (l: lista);
begin
	if (l <> nil) then
	begin
		write(l^.d);
		imprimirListaOrd(l^.sig)
	end;
end;

procedure imprimirListaReves (l: lista);
begin
	if (l <> nil) then
	begin
		imprimirListaReves(l^.sig);
		write(l^.d);
	end;
end;


// Inicio Programa Principal

var
	dimL, contC: cantChar;
	contD: integer;
	l: lista;
	v: vector;
begin
	dimL:= 1;
	contC:= 1;
	lecturaA(v, dimL);
	imprimirVect(v, dimL);
	imprimirVectRec(v, dimL, contC);
	contD:= 1;
	lecturaD(contD);
	lecturaE(l);
	imprimirListaOrd(l);
	imprimirListaReves(l);
end.

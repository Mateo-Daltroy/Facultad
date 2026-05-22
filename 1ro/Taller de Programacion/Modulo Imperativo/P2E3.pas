program P2E3;
const
	maxRandom = 101;
type
	lista = ^nodo;
	nodo = record
		d: integer;
		sig: lista;
	end;

// Inicio Modulos


procedure generarLista (var l: lista);
var
	nuevo: lista;
begin
	new(nuevo);
	nuevo^.d:= Random(maxRandom);
	nuevo^.sig:= nil;
	while (nuevo^.d <> 0) do
	begin
		nuevo^.sig:= l;
		l:= nuevo;
		new(nuevo);
		nuevo^.d:= Random(maxRandom);
		nuevo^.sig:= nil;
	end;
end;

procedure mostrarLista (l:lista);
{Modulo no pedido, pero sirve de referencia
para saber si los otros funcionan bien}
begin
	if (l <> nil) then
	begin
		write(l^.d);
		write(', ');
		mostrarLista(l^.sig);
	end;
end;

procedure mostrarMinimo (l: lista; var min: integer);
//Iniciar minimio fuera en 101
begin
	if (l <> nil) then
	begin
		mostrarMinimo(l^.sig, min);
		if (l^.d < min) then
			min:= l^.d;
	end;
end;

procedure mostrarMaximo(l: lista; var max: integer);
//Iniciar maximo fuera en -1
begin
	if (l <> nil) then
	begin
		mostrarMaximo(l^.sig, max);
		if (l^.d > max) then
			max:= l^.d;
	end;
end;


function buscarValor(l: lista; v: integer): boolean;
begin
	if (l <> nil) then
	begin
		if (l^.d = v) then
			buscarValor:= true
		else
			buscarValor:= buscarValor(l^.sig, v);
	end
	else
		buscarValor:= false;
end;


// Inicio Programa Principal

var
	l: lista;
	max, min, busq: integer;
begin
	Randomize();
	min:= 101;
	max:= -1;
	generarLista(l);
	mostrarLista(l);
	writeln;
	mostrarMinimo(l, min);
	write('El valor minimo encontrado es: ');
	writeln(min);
	mostrarMaximo(l, max);
	write('El valor maximo encontrado es: ');
	writeln(max);
	write('Introducir valor a buscar: ');
	readln(busq);
	if (buscarValor(l, busq) = true) then
		writeln('El valor se encuentra dentro de la lista.')
	else
		writeln('El valor NO se encuentra dentro de la lista.');
end.

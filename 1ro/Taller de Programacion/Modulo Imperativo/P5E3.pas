program P5E3;
const
	maxRubros = 10;
type
	cantRubros = 1..maxRubros;
	
	producto = record
		cod: integer;
		sto: integer;
		pre: real;
	end;
	
	productoC = record
		cod: integer;
		sto: integer;
	end;
	
	arbol = ^nodo;
	nodo = record
		d: producto;
		HI: arbol;
		HD: arbol;
	end;
	
	vector = array [cantRubros] of arbol;
	vectorC = array [cantRubros] of productoC;
	vectorD = array [cantRubros] of integer;


// Inicio Modulos


procedure leerProducto (var p: producto; var aux: cantRubros);
begin
	write('Introducir codigo del producto: ');
	readln(p.cod);
	if (p.cod <> -1) then
	begin
		write('Introducir rubro del producto: ');
		readln(aux);
		write('Introducir stock del producto: ');
		readln(p.sto);
		write('Introducir precio del producto: ');
		readln(p.pre);
	end;
	writeln;
end;

procedure iniVect (var v: vector);
var
	i: integer;
begin
	for i:= 1 to maxRubros do
	begin
		v[i]:= nil;
	end;
end;

procedure armarArbol(var a: arbol; p: producto);
begin
	if (a = nil) then
	begin
		new(a);
		a^.d:= p;
		a^.HI:= nil;
		a^.HD:= nil;
	end
	else if (a^.d.cod >= p.cod) then
		armarArbol(a^.HI, p)
	else
		armarArbol(a^.HD, p);
end;

procedure leerDatos (var v: vector);
var
	p: producto;
	aux: cantRubros;
begin
	iniVect(v);
	leerProducto(p, aux);
	while (p.cod <> -1) do
	begin
		armarArbol(v[aux], p);
		leerProducto(p, aux)
	end;
	writeln;
end;

function codExiste (a: arbol; cod: integer): boolean;
begin
	if (a = nil) then
		codExiste:= false
	else if (a^.d.cod = cod) then
		codExiste:= true
	else if (a^.d.cod > cod) then
		codExiste:= codExiste(a^.HI, cod)
	else
		codExiste:= codExiste(a^.HD, cod);
end;

function mayorNodo (a: arbol): producto;
begin
	if (a^.HD = nil) then
		mayorNodo:= a^.d
	else
		mayorNodo:= mayorNodo(a^.HD);
end;

procedure vectMayor (v: vector; var v2: vectorC);
var
	i: cantRubros;
	aux: producto;
begin
	for i:= 1 to maxRubros do
	begin
		aux:= mayorNodo(v[i]);
		v2[i].cod:= aux.cod;
		v2[i].sto:= aux.sto;
	end;
end;

procedure informarVD(v: vectorD);
var
	i: cantRubros;
begin
	for i:= 1 to maxRubros do
	begin
		write('El rubro '); write(i);
		write(' hay '); write(v[i]);
		writeln(' codigos que cumplen con los requisitos.');
	end;
	writeln;
end;

procedure informarVC(v: vectorC);
var
	i: integer;
begin
	for i:= 1 to maxRubros do
	begin
		write('El rubro '); write(i);
		write(' tiene el producto con el codigo ');
		writeln(v[i].cod);
	end;
	writeln;
end;

procedure codigosEntre(a: arbol; var v: integer; min, max: integer);
begin
	if (a <> nil) then
	begin
		if (a^.d.cod < min) then
			codigosEntre(a^.HI, v, min, max)
		else if (a^.d.cod > max) then
			codigosEntre(a^.HD, v, min, max)
		else
		begin
			v:= v + 1;
			codigosEntre(a^.HD, v, min, max);
			codigosEntre(a^.HI, v, min, max);
		end;
	end;
end;

procedure codigosEntreVect(v: vector; var v3: vectorD; min, max: integer);
var
	i: integer;
begin
	for i:= 1 to maxRubros do
	begin
		v3[i]:= 0;
		codigosEntre(v[i], v3[i], min, max);
	end;
end;


// Inicio Programa Principal


var
	v: vector;
	v2: vectorC;
	v3: vectorD;
	rub: cantRubros;
	min, max, cod: integer;
begin
	leerDatos(v);
	write('Introducir codigo a buscar: ');
	readln(cod);
	write('Introducir rubro en el que buscar: ');
	readln(rub);
	if (codExiste(v[rub], cod)) then
		writeln('El codigo fue encontrado.')
	else
		writeln('El codigo no fue encontrado.');
	vectMayor(v, v2);
	informarVC(v2);
	write('Introducir codigo minimo a buscar: ');
	readln(min);
	write('Introducir codigo maximo a buscar: ');
	readln(max);
	codigosEntreVect(v, v3, min, max);
	informarVD(v3);
end.

program P1E4;
const
	maxRubros = 8;
	maxElementos = 30;
type
	cantRubros = 1..maxRubros;
	cantElementos = 1..maxElementos;
	
	producto = record
		prod: integer;
		pre: real;
	end;
	
	lista = ^nodo;
	nodo = record
		d: producto;
		sig: lista;
	end;
	
	vector = array [cantRubros] of lista;
	vectorC = array [cantElementos] of producto;

// Inicio Modulos


procedure iniVect (var v: vector);
var
	i: cantRubros;
begin
	for i:= 1 to maxRubros do
	begin
		new(v[i]);
		v[i]:= nil;
		
		//Consultar por este for
		
	end;
end;

procedure leerProducto (var p: producto; var rub: cantRubros);
var
begin
	write('Introducir precio del producto: ');
	readln (p.pre);
	if (p.pre <> 0) then
	begin
		write('Introducir codigo del producto: ');
		readln(pre.prod);
		write('Introducir rubro del producto: ');
		readln(rub);
		writeln;
	end
	else
	begin
		writeln;
		write('Fin de lectura de datos.');
		writeln;
		writeln;
	end;
end;

procedure insertarOrdenado (var pI:lista; valor:producto);
var
	actual, ant, nuevo: lista;
begin
	new(nuevo);
	nuevo^.d:= valor;
	nuevo^.sig:= nil;
	if (pI = nil) then
		pI = nuevo;
	else
	begin
		actual:= pI;
		ant:= pI;
		while (actual <> nil) and (actual^.d.prod < nuevo^.d.prod) do
		begin
			anterior:= actual;
			actual:= actual^.sig;
		end;
		if (actual = pI) then
		begin
			nuevo^.sig:= pI;
			pI:= nuevo;
		end;
		else
		begin
			anterior^.sig:= nuevo;
			nuevo^.sig:= actual;
		end;
	end;
end;

procedure leerDatos (var v: vector);
var
	rub: cantRubros;
	prod: producto;
begin
	iniVect(v);
	leerProducto(prod, rub);
	while (prod.pre <> 0) do
	begin
		insertarOrdenado(v[rub], prod);
		leerProducto(prod, rub);
	end;
end;

procedure mostrarCodigos (v: vector);
var
	i: cantRubros;
begin
	for i:= 1 to maxRubros do
	begin
		write('En el rubro numero ');
		write(i);
		writeln(' se encuentran los productos bajo los codigos: ')
		while (v[i] <> nil) do
		begin
			writeln(v[i]^.d.prod);
			v[i]:= v[i]^.sig;
		end;
		writeln;
		writeln;
	end;
end;

procedure iniVectC (var v: vectorC; var dimL: cantElementos);
var
	i: cantElementos;
begin
	dimL:= 0;
	for i:= 1 to maxElementos do
	begin
		v[i].prod:= -1;
		v[i].pre:= -1;
	end;
end;

procedure generarVector (l: lista; var v: vectorC; var dimL: cantElementos);
var
begin
	iniVectC(v, dimL);
	while (l <> nil) and (dimL < 30) do
	begin
		dimL:= dimL + 1;
		v[dimL]:= l^.d;
		l:= l^.sig;
	end;
end;

procedure seleccion (var v: vectorC; dimL: cantElementos);
var
	i, j, pos: cantElementos;
	aux: producto;
begin
	for i:= 1 to (dimL - 1) do
	begin
		pos:= i;
		for j:= (i+1) to dimL do
		begin
			if (v[j].pre < v[pos].pre) then
				pos:= j;
		end;
		aux:= v[i];
		v[i]:= v[pos];
		v[pos]:= aux;
	end;
end;

procedure mostrarPrecios (v: vectorC; dimL: cantElementos);
var
	i: cantElementos;
begin
	for i:= 1 to dimL do
	begin
		write('Precio numero ');
		write(i);
		write(', total: ');
		writeln(v[i].pre);
	end;
end;

function calcularPromedio (v: vectorC; dimL: cantElementos): real;
var
	i: cantElementos;
	aux: real;
begin
	for i:= 1 to dimL do
	begin
		aux:= aux + v[i].pre;
	end;
	calcularPromedio:= aux / dimL;
end;

// Inicio Programa Principal

var
begin
end.

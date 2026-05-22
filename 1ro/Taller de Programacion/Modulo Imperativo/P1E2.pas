program P1E2;
const
	maxOfi = 100;
type
	cantOficinas = 1..maxOfi;
	
	oficina = record
		cod: integer;
		dni: integer;
		valor: real;
	end;
	
	vector = array [cantOficinas] of oficina;

// Inicio Modulos

procedure leerOficina (var o: oficina);
begin
	write('Introducir codigo: ')
	readln(o.cod);
	if (o.cod <> -1) then
	begin
		write('Introducir dni: ');
		readln(o.dni);
		write('Introducir valor: ');
		readln(o.valor);
	end;
	writeln();
end;

procedure leerDatos (var v: vector; var dimL: cantOficinas);
var
	o: oficina;
begin
	leerOficina(o);
	dimL:= 0;
	while (o.cod <> -1) and (dimL < maxOfi) do
	begin
		dimL:= dimL + 1;
		v[dimL]:= o;
		leerOficina(o);
	end;
end;

procedure seleccion (var v: vector; dimL: cantOficinas);
var
	pos, i, j: cantOficinas;
	aux: oficina;
begin
	for i:= 1 to (dimL - 1) do
	begin
		pos:= i;
		for j:= (i-1) to dimL do
		begin
			if (v[j].cod < v[pos].cod) then
				pos:= j;
		end;
		aux:= v[pos];
		v[pos]:= v[i];
		v[i]:= aux;
	end;
end;

procedure insercion (var v:vector; dimL:cantOficinas);
var
	i, j: cantOficinas;
	actual: oficina;
begin
	for i:= 2 to dimL do
	begin
		actual:= v[i];
		j:= (i - 1);
		while (j > 0) and (v[j] > actual) do
		begin
			v[j + 1]:= v[j];
			j:= (j - 1);
		end;
		v[j + 1]:= actual;
	end;
end; 

// Inicio PP

var
begin
end.

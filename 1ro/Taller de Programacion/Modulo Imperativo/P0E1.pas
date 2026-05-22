program P0E1;
const
	maxMaterias = 36;
type

cantMaterias = 1..maxMaterias;

vector = array [cantMaterias] of integer;

alumno = record;
	ap: string;
	cod: integer;
	ano: string;
	mat: vector;
end;

lista = ^nodo;
nodo = record;
	d: alumno;
	sig: lista;
end;

puntoB = record;
	cod: integer;
	prom: real;
end;

lista2 = ^nodo2;
nodo2 = record;
	d: puntoB;
	sig: lista2;
end;

// Inicio modulos

procedure iniVector(v: vector);
var
	i: integer;
begin
	for i:= 1 to maxMaterias do
	begin
		v[i]:= -1;
	end;
end;

procedure leerAlumno (var a: alumno);
var
	i, pos: integer;
	cont: cantMaterias;
begin
	readln(a.ap);
	readln(a.cod);
	readln(a.ano);
	iniVector(a.mat);
	readln(cont);
	for i:= 1 to cont do
	begin
		readln(pos);
		readln(a.mat[pos]);
	end;
end;

procedure leerLista (var l: lista);
var
	nuevo: lista;
begin
	repeat
		new(nuevo);
		leerAlumno(nuevo^.d);
		nuevo^.sig:= l;
		l:= nuevo;
	until (nuevo.d.cod = 11111);
end;

function sacaPromedio (v: vector): real;
var
	i, cant, tot;
begin
	cant:= 0;
	tot:= 0;
	for i:= 1 to cantMaterias do
	begin
		if (v[i] <> -1) then
		begin
			cant:= cant + 1;
			tot:= tot + v[i];
		end;
	end;
	if (cant <> 0) do
		sacaPromedio:= -1
	else
		sacaPromedio:= (tot / cant);
end;

procedure listaB (l: lista; var l2: lista2);
var
	nod: lista2;
begin
	while (l <> nil) do
	begin
		new(nod);
		nod^.d.prom:= sacaPromedio(l^.d.mat);
		nod^.d.cod:= l^.d.cod;
		nod^.sig:= l2;
		l2:= nod;
	end;
end;

// Falta punto C

// Fin modulos / Inicio PP

var

begin

end.

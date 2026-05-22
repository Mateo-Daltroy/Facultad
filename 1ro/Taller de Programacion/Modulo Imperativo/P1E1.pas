program P1E1;
const
	maxVentas = 50;
type
	cantVentas = 0..maxVentas; // 0 indica no ini
	codigosPos = 0..15; //0 indica no ini
	unidadesPos = 1..99;
	
	venta = record
		cod: codigosPos;
		fecha: string;
		cant: unidadesPos;
	end;

	vector = array [cantVentas] of venta;

	venta2 = record
		cod: codigosPos;
		cant: unidadesPos;
	end;
	
	vector2 = array [cantVentas] of venta2; 

// Inicio Modulos

procedure leerVenta (var ven: venta);
begin
	write('Introducir fecha de la venta: ');
	readln(ven.fecha);
	if (ven.fecha <> '0') then
	begin
		write('Introducir cantidad de unidades vendidas: ');
		readln(ven.cant);
		ven.cod:= (Random(15) + 1);
	end;
end;

procedure iniVect (var v: vector);
var
	i: integer;
begin
	for i:= 1 to maxVentas do
	begin
		v[i].cod:= 0;
	end;
end;

procedure iniVectG (var v: vector2);
var
	i: integer;
begin
	for i:= 1 to maxVentas do
	begin
		v[i].cod:= 0;
	end;
end;

procedure leerDatos (var v: vector; var dimL: cantVentas);
var
	ven: venta;
begin
	iniVect(v);
	leerVenta(ven);
	while (ven.fecha <> '0') and (dimL < maxVentas) do
	begin
		dimL:= dimL + 1;
		v[dimL]:= ven;
		leerVenta(ven);
	end;
end;

procedure mostrarVect (v: vector; dimL: cantVentas);
var
	i: cantVentas;
begin
	for i:= 1 to dimL do
	begin
		write('Venta numero: '); writeln(i);
		write('Con el codigo: '); writeln(v[i].cod);
		write('Vendio '); write(v[i].cant);
		write(' unidades el '); writeln(v[i].fecha);
		writeln;
	end;
end;

procedure seleccion (var v: vector; dimL: cantVentas);
var
	i, j, pos: cantVentas;
	aux: venta;
begin
	for i:= 1 to (dimL - 1) do
	begin
		pos:= i;
		for j:= (i+1) to dimL do
		begin
			if (v[j].cod < v[pos].cod) then
				pos:= j;
		end;
		aux:= v[i];
		v[i]:= v[pos];
		v[pos]:= aux;
	end;
end;

procedure borrarEntre (var v: vector; var dimL: cantVentas; cod1: codigosPos; cod2: codigosPos);
var
	dist, cont: cantVentas;
begin
	dist:= 0;
	cont:= 1;
	while (v[cont].cod < cod1) do
	begin
		cont:= cont + 1;
	end;
	while (v[cont].cod < cod2) do
	begin
		dist:= dist + 1;
		cont:= cont + 1;
	end;
	while (cont <= dimL) do
	begin
		v[cont - dimL]:= v[dimL];
	end;
	dimL:= dimL - dist;
end;

procedure vectorG (v: vector; var v2: vector2; dimL: cantVentas; var dimL2: cantVentas; var tot: integer);
var
	i: integer;
begin
	iniVectG(v2);
	dimL2:= 0;
	tot:= 0;
	for i:= 1 to dimL do
	begin
		if ((v[i].cod MOD 2) = 0) then
		begin
			dimL2:= dimL2 + 1;
			v2[dimL2].cod:= v[i].cod;
			v2[dimL2].cant:= v[i].cant;
		end;
		tot:= tot + v[i].cant;
	end;
end;

procedure mostrarVectG (v: vector2; dimL: cantVentas; tot: integer);
var
	i: integer;
begin
	for i:= 1 to dimL do
	begin
		write('Codigo: '); writeln(v[i].cod);
		writeln('Ventas: '); writeln(v[i].cant);
		writeln;
	end;
	write('Ventas totales: ');
	writeln(tot);
end;


// Inicio PP

var
	v: vector;
	v2: vector2;
	cod1, cod2: codigosPos;
	dimL, dimL2: cantVentas;
	tot: integer;
begin
	Randomize();
	dimL:= 0;
	leerDatos(v, dimL);
	mostrarVect(v, dimL);
	seleccion(v, dimL);
	mostrarVect(v, dimL);
	write('Introducir primer codigo: ');
	readln(cod1);
	write('Introducir segundo codigo: ');
	readln(cod2);
	borrarEntre(v, dimL, cod1, cod2);
	mostrarVect(v, dimL);
	vectorG(v, v2, dimL, dimL2, tot);
	mostrarVectG(v2, dimL2, tot);
end.

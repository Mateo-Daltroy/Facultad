program P3E1;
type
	socio = record
		cod: integer;
		nom: string;
		edad: integer;
	end;
	
	arbol = ^nodo;
	nodo = record
		d: socio;
		HI: arbol;
		HD: arbol;
	end;


// Inicio Modulos


procedure generarArbol (var a: arbol; n: arbol);
begin
	if (a = nil) then
	begin
		a:= n;
		a^.HI:= nil;
		a^.HD:= nil;
	end
	else if (n^.d.cod <= a^.d.cod) then
		generarArbol(a^.HI, n)
	else
		generarArbol(a^.HD, n);
end;

procedure leerDatos (var a: arbol);
var
	nuevo: arbol;
begin
	new(nuevo);
	a:= nil;
	write('Introducir numero de socio: ');
	readln(nuevo^.d.cod);
	while (nuevo^.d.cod <> 0) do
	begin
		write('Introducir nombre del socio: ');
		readln(nuevo^.d.nom);
		write('Introducir edad del socio: ');
		readln(nuevo^.d.edad);
		generarArbol(a, nuevo);
		writeln;
		new(nuevo);
		write('Introducir numero de socio: ');
		readln(nuevo^.d.cod);
	end;
end;

function socioMay(a: arbol): integer;
begin
	if (a^.HD = nil) then
		socioMay:= a^.d.cod
	else
		socioMay:= socioMay(a^.HD);
end;

function socioMen(a: arbol): arbol;
begin
	if (a^.HI = nil) then
		socioMen:= a
	else
		socioMen:= socioMen(a^.HI);
end;

procedure socioViejo(a: arbol; var viejo: arbol);
begin
	if (a <> nil) then
	begin
		socioViejo(a^.HI, viejo);
		if (a^.d.edad > viejo^.d.edad) then
			viejo:= a;
		socioViejo(a^.HD, viejo);
	end;
end;

procedure sumarEdad(a: arbol);
begin
	if (a <> nil) then
	begin
		sumarEdad(a^.HI);
		a^.d.edad:= a^.d.edad + 1;
		sumarEdad(a^.HD);
	end;
end;

function busqSocio(a: arbol; busq: integer): boolean;
begin
	if (a = nil) then
		busqSocio:= false
	else if (a^.d.cod = busq) then
		busqSocio:= true
	else if (busq > a^.d.cod) then
		busqSocio:= busqSocio(a^.HD, busq)
	else
		busqSocio:= busqSocio(a^.HI, busq);
end;

procedure busqNombre(a: arbol; busq: string; var enc: boolean);
// Inicializar enc en false desde afuera
begin
	if (a <> nil) and (enc = false) then
	begin
		if (a^.d.nom = busq) then
			enc:= true
		else
		begin
			busqNombre(a^.HI, busq, enc);	
			busqNombre(a^.HD, busq, enc);
		end;
	end;
end;

procedure cantSocios (a: arbol; var c: integer);
begin
	if (a <> nil) then
	begin
		cantSocios(a^.HI, c);
		c:= c + 1;
		cantSocios(a^.HD, c);
	end;
end;

procedure edadTot(a: arbol; var tot: integer);
begin
	if (a <> nil) then
	begin
		edadTot(a^.HI, tot);
		tot:= tot + a^.d.edad;
		edadTot(a^.HD, tot);
	end;
end;


{Preguntar por este modulo, la division
* de la ultima linea da un resultado erroneo
* por mas de que en los writeln anteriores
* muestran que los datos son correctos}
function edadProm (a: arbol): real;
var
	c, tot: integer;
begin
	c:= 0;
	tot:= 0;
	cantSocios(a, c);
	edadTot(a, tot);
// writeln de debugging
{
	writeln(tot);
	writeln(c);
}
	edadProm:= tot / c;
end;

procedure impEnOrden(a: arbol);
begin
	if (a <> nil) then
	begin
		impEnOrden(a^.HI);
		writeln(a^.d.cod);
		impEnOrden(a^.HD);
	end;
end;

procedure impDecreciente(a: arbol);
begin
	if (a <> nil) then
	begin
		impDecreciente(a^.HD);
		if ((a^.d.cod MOD 2) = 0) then
			writeln(a^.d.cod);
		impDecreciente(a^.HI);
	end;
end;

procedure informarDatos (s: socio);
begin
	write('El socio numero ');
	write(s.cod);
	write(' se llama ');
	write(s.nom);
	write(' y tiene ');
	write(s.edad);
	writeln(' años.')
end;

procedure mostrarLista (a: arbol);
begin
	if (a <> nil) then
	begin
		mostrarLista(a^.HI);
		informarDatos(a^.d);
		mostrarLista(a^.HD);
	end;
end;


// Inicio Programa Principal


var
	a, a2: arbol;
	c: integer;
	nom: string;
	busq: boolean;
begin
	leerDatos(a);
	mostrarLista(a);
	writeln;
	write('El numero de socio de mayor codigo es: ');
	writeln(socioMay(a));
	writeln('El socio de menor codigo es: ');
	a2:= socioMen(a);
	informarDatos(a2^.d);
	writeln('El socio de mayor edad es: ');
	socioViejo(a, a2);
	informarDatos(a2^.d);
	sumarEdad(a);
	mostrarLista(a);
	writeln;
	write('Introducir codigo a buscar: ');
	readln(c);
	busq:= busqSocio(a, c);
	if (busq = true) then
		writeln('El codigo se encuentra dentro del arbol.')
	else
		writeln('El codigo no se encuentra dentro del arbol.');
	writeln;
	write('Introducir nombre a buscar: ');
	readln(nom);
	busq:= false;
	busqNombre(a, nom, busq);
	if (busq = true) then
		writeln('El nombre se encuentra dentro del arbol.')
	else
		writeln('El nombre no se encuentra dentro del arbol.');
	c:= 0;
	cantSocios(a, c);
	write('El arbol contiene ');
	write(c);
	write(' socios, con una edad promedio de ');
	writeln(edadProm(a));
	impEnOrden(a);
	impDecreciente(a);
end.

program P2E4;
const
	maxRandom = 99;
	maxNum = 20;
type
	cantNum = 1..maxNum;
	
	vector = array [cantNum] of integer;

// Inicio MOdulos


procedure generarDatos (var v: vector);
var
	i: cantNum;
begin
	for i:= 1 to maxNum do
	begin
		v[i]:= Random(maxRandom) + 1;
	end;
end;

procedure mostrarVect (v: vector);
//Modulo no requerido pero sirve para ver si los otros funcionan
var
	i: cantNum;
begin
	for i:= 1 to maxNum do
	begin
		write(v[i]);
		write(', ');
	end;
	writeln;
end;

procedure encontrarMayor (v: vector; var max: integer; pos: cantNum);
// Ini en -1 y pos en 1 afuera
begin
	if (v[pos] > max) then
		max:= v[pos];
	if (pos < maxNum) then
		encontrarMayor(v, max, pos + 1);
end;

procedure sumarVector (v: vector; var tot: integer; pos: cantNum);
begin
	if (pos < maxNum) then
		sumarVector(v, tot, (pos + 1));
	tot:= tot + v[pos];
end;

// Porque no funciona esto?
{
function sumarVector (v: vector; var tot: integer; pos: cantNum): integer;
// tot en 0 y pos en 1 afuera
begin
	if (pos < maxNum) then
		sumarVector:= sumarVector(v, tot, pos + 1);
	tot:= tot + v[pos];
	sumarVector:= tot + v[pos];
end;
}


// Inicio Programa Principal

var
	v: vector;
	tot, max: integer;
	pos: cantNum;
begin
	Randomize();
	max:= -1;
	pos:= 1;
	tot:= 0;
	generarDatos(v);
	mostrarVect(v);
	encontrarMayor(v, max, pos);
	write('El mayor valor es: ');
	writeln(max);
	write('El total del vector sumado es: ');
	sumarVector(v, tot, pos);
	writeln(tot);
end.

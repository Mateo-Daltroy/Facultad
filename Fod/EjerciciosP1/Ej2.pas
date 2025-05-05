program Ej2;
type
	archivoInt= File of integer;


procedure numerosMenores(var cant: integer; var prom: real; var arch: archivoInt);
var
	tot, cantNum, num: integer;
begin
	tot:= 0;
	cantNum:= 0;
	cant:= 0;
	while (not EOF(arch)) do
	begin
		cantNum:= cantNum + 1;
		read(arch, num);
		writeln(num);
		if (num < 1500) then
			cant:= cant + 1;
		tot:= tot + num;
	end;
	prom:= tot / cantNum;
end;


var
	arch: archivoInt;
	nombre: string;
	prom: real;
	cant: integer;
begin
	writeln('Introducir nombre del archivo a leer: ');
	readln(nombre);
	assign(arch, nombre);
	reset(arch);
	numerosMenores(cant, prom, arch);
	write('Hay '); write(cant); writeln(' numeros menores a 1500.');
	write('El promedio de todos los numeros introducidos es: '); writeln(prom);
	close(arch);
end.

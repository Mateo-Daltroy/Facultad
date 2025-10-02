program Ej1;
type
	archivoInt = File of integer;
 
var
	arch: archivoInt;
	nombre: string;
	num: integer;
begin
	write('Introducir el nombre del archivo: ');
	readln(nombre);
	writeln('1');
	assign(arch, nombre);
	writeln('2');
	rewrite(arch);
	reset(arch);
	readln(num);
	while (num <> 30000) do
	begin
		write(arch, num);
		readln(num);
	end;
	close(arch);
end.

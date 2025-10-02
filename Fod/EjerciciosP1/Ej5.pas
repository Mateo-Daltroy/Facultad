program Ej5;
uses SysUtils;
const
	textFile = 'celulares.txt';
	sinStock = 'SinStock.txt';
type
	celular = record
		cod: integer;
		nom: string;
		desc: string;
		marca: string;
		precio: real;
		stkMin: integer;
		stkAct: integer;
	end;
	
	ArchBin = File of celular;


// Inicio Modulos


// Generales
procedure darOpciones (var op: integer);
begin
	writeln('Seleccione una opcion para continuar:');
	write('1 para actualizar los datos para ser utilizados, 2 para listar los celulares con stocks por debajo del minimo,');
	write(' 3 para buscar los celulares que coincidan con una descripcion, 4 para exportar el archivo a uno legible (.txt),');
	writeln(' 5 para agregar celulares, 6 para modificar el stock de un celular, 7 para actualizr SinStock.txt y 0 para Salir');
	readln(op);
	while (op < 0) or (op > 7) do
	begin
		if ((op < 0) or (op > 7)) then
			writeln('Numero invalido');
		readln(op);
	end;
	writeln;
end;

procedure listarEnPantalla (cel: celular);
begin
	writeln('{' + cel.nom + ', stMin: ' + IntToStr(cel.stkMin) + ', stAct: ' + IntToStr(cel.stkAct) + '}');
	writeln('Descripcion: ' + cel.desc);
	writeln;
end;	

// Op 1
procedure leerCelular(var txtF: Text; var cel: celular);
begin
	read(txtF, cel.cod); read(txtF, cel.precio); readln(txtF, cel.marca);
	read(txtF, cel.stkAct); read(txtF, cel.stkMin); readln(txtF, cel.desc);
	readln(txtF, cel.nom);
end;

procedure crearArchivoBin(var arch: ArchBin);
var
	nomArch: string;
	celulares: Text;
	cel: celular;
begin write('Ingresar nombre del archivo a actualizar: ');
	readln(nomArch);
	assign(arch, nomArch); assign(celulares, textFile);
	rewrite(arch); reset(celulares);
		while (not EOF(celulares)) do
	begin
		leerCelular(celulares, cel);
		write(arch, cel);
	end;
	writeln('Archivo actualizado.');
	writeln;
	close(celulares);
end;

//Op 2
function stockBajo(cel: celular): boolean;
begin
	stockBajo:= (cel.stkAct < cel.stkMin);
end;

procedure listarMenorStock(var arch: ArchBin);
var
	cel: celular;
begin
	reset(arch);
	while (not EOF(arch)) do
	begin
		read(arch, cel);
		if (stockBajo(cel)) then	
			listarEnPantalla(cel);
	end;
	writeln;
end;

//Op 3
function descCoincide(busq: string; fuente: string): boolean;
var
	aux: integer;
begin
	aux:= Pos(busq, fuente);
	descCoincide:= (aux <> 0);
end;

procedure buscarDescripcion(var arch: ArchBin);
var
	cel: celular;
	busq: string;
	cont: integer;
begin
	reset(arch);
	write('Introducir descripcion a buscar: ');
	readln(busq);
	cont:= 0;
	while (not EOF(arch)) do
	begin
		read(arch, cel);
		if (descCoincide(busq, cel.Desc)) then	
		begin
			cont:= cont + 1;
			listarEnPantalla(cel);
		end;
	end;
	if (cont = 0) then
		writeln('Ningun producto coincide con la busqueda');
	writeln;
end;

//Op 4
procedure escribirEnTxt(var txtF: Text; cel: celular);
begin
	writeln(txtF, cel.cod, ' ', cel.precio, ' ', cel.marca);
	writeln(txtF, cel.stkAct, ' ', cel.stkMin, ' ', cel.desc);
	writeln(txtF, cel.nom);
	//write(txtF, cel.cod); write(txtF, cel.precio); writeln(txtF, cel.marca);
	//write(txtF, cel.stkAct); write(txtF, cel.stkMin); writeln(txtF, cel.desc);
	//writeln(txtF, cel.nom);
end;

procedure exportarDatos(var arch: ArchBin);
var
	txtF: Text;
	cel: celular;
begin
	reset(arch);
	assign(txtF, textFile); rewrite(txtF);
	while (not EOF(arch)) do
	begin
		read(arch, cel);
		escribirEnTxt(txtF, cel);
	end;
	close(txtF);
	writeln('Datos exportados con exito.');
end;

//Op 5
procedure leerCelularInput(var cel: celular);
begin
	write('Cod: '); readln(cel.cod);
	if (cel.cod <> -1) then
	begin
		write('Desc: '); readln(cel.desc);
		write('Nom: '); readln(cel.nom);
		write('Precio: '); readln(cel.precio);
		write('StkAct: '); readln(cel.stkAct);
		write('StkMin: '); readln(cel.stkMin);
		write('Marca: '); readln(cel.marca);
	end;
end;

procedure concatCelu(var arch: ArchBin);
var
	cel: celular;
begin
	reset(arch);
	seek(arch, fileSize(arch));
	leerCelularInput(cel);
	while (cel.cod <> -1) do
	begin
		write(arch, cel);
		leerCelularInput(cel);
	end;
	close(arch);
end;

//Op 6
procedure actualizarStock(var cel: celular);
begin
	write('El stock actual del celular es: ' + IntToStr(cel.stkAct) + ', ingresar el nuevo stock: ');
	readln(cel.stkAct);
end;

procedure modificarStock(var arch: ArchBin);
var
	str: string;
	encontrado: boolean;
	cel: celular;
begin
	reset(arch);
	encontrado:= false;
	write('Ingresar nombre del celular: ');
	readln(str);
	while ((not encontrado) and (not EOF(arch))) do
	begin
		read(arch, cel);
		if (cel.nom = str) then
		begin
			encontrado:= true;
			actualizarStock(cel);
			seek(arch, (filePos(arch) - 1));
			write(arch, cel);
		end;
	end;
	if (not encontrado) then
		writeln('No existe un celular bajo ese nombre.');
	close(arch);
end;

//Op 7
procedure actualizarSinStk(var arch: ArchBin);
var
	txtF: Text;
	cel: celular;
begin
	reset(arch);
	assign(txtF, sinStock);
	rewrite(txtF);
	while (not EOF(arch)) do
	begin
		read(arch, cel);
		if (cel.stkAct = 0) then
			escribirEnTxt(txtF, cel);
	end; 
	close(txtF);
end;

// Fin Modulos, Inicio PP


var
	op: integer;
	arch: ArchBin;
begin
	op:= -1;
	while (op <> 0) do
	begin
		darOpciones(op);
		case op of
			1: crearArchivoBin(arch);
			2: listarMenorStock(arch);
			3: buscarDescripcion(arch);
			4: exportarDatos(arch); 
			5: concatCelu(arch);
			6: modificarStock(arch);
			7: actualizarSinStk(arch);
		end;
	end;
	close(arch);
end.

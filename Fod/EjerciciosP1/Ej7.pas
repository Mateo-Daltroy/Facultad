Program Ej7;
uses SysUtils;
type
    novela = record
	cod: integer;
	nom: string;
	genero: string;
	precio: real;
    end;

    archNov = File of novela;

// Inicio Procedures

// Generales
procedure leerNovelaText(var texto: text; var aux: novela);
begin
    read(texto, aux.cod);
    read(texto, aux.precio);
    readln(texto, aux.genero);
    readln(texto, aux.nom);
end;

procedure imprimirNov(nov: Novela);
begin
    writeln('cod: ' + IntToStr(nov.cod) + ' nombre: ' + nov.nom);
    write('precio: ');
    write(nov.precio);
    writeln(' genero: ' + nov.genero);
end;

procedure leerNovelaCom(var n: novela);
begin
    write('cod: '); readln(n.cod);
    if (n.cod <> -1) then begin
	write('precio: '); readln(n.precio);
	write('genero: '); readln(n.genero);
	write('nombre: '); readln(n.nom);
    end;
end;

// PunA
procedure crearBinario(var texto: text; var arch: archNov);
var
    aux: novela;
begin
    rewrite(arch);
    reset(texto);
    while (not EOF(texto)) do
    begin leerNovelaText(texto, aux);
	write(arch, aux);
    end;
end;

// PunB
procedure agregarBin(var arch: archNov);
var
    nov: novela;
begin
    seek(arch, fileSize(arch));
    leerNovelaCom(nov);
    while (nov.cod <> -1) do begin
	write(arch, nov);
	leerNovelaCom(nov);
    end;
end;

procedure actualizarDatos(var nov: novela);
begin
    writeln('datos actuales: ');
    imprimirNov(nov);
    writeln('leyendo nuevos datos: ');
    leerNovelaCom(nov);
end;

procedure actualizarBin(var arch: archNov);
var
    cod: integer;
    act: novela;
    encontrado: boolean;
begin
    encontrado:= false;
    write('Codigo a buscar: '); readln(cod);
    while(not EOF(arch)) do begin
	read(arch, act);
	if (act.cod = cod) then begin
	    actualizarDatos(act);
	    seek(arch, filePos(arch) - 1);
	    write(arch, act);
	    encontrado:= true;
	end;
    end;
    if (encontrado) then
	writeln('libro actualizado con exito')
    else
	writeln('codigo no encontrado');
end;

procedure checkDatos(var arch: archNov);
var
    nov: novela;
begin
    reset(arch);
    while (not EOF(arch)) do begin
	read(arch, nov);
	imprimirNov(nov);
    end;
end;

procedure mostrarOpciones(var arch: archNov);
var
    opt: integer;
begin
    opt:= 100;
    while (opt <> 0) do begin
	writeln('Ingresar 1 para actualizar un binario, 2 para agregar una novela');
	readln(opt);
	reset(arch);
	case opt of
	    1: actualizarBin(arch);
	    2: agregarBin(arch);
	else
	    writeln('numero invalido');
	end;
    end;
    close(arch);
end;



// Inicio Programa Principal
var
    arch: archNov;
    fuente: Text;
begin
    assign(fuente, 'novelas.txt');
    assign(arch, 'novelas.bin');
    crearBinario(fuente, arch);
    mostrarOpciones(arch);
    checkDatos(arch); //Modulo de Testing
end.

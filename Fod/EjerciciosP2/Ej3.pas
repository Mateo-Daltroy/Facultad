program Ej3;
uses SysUtils;
const
    corte = 'ZZZ';
type
    datCenso = record
	nom: string;
	cod: integer;
	alfa: integer;
	encu: integer;
    end;

    archCenso = file of datCenso;

    datBase = record
	nom: string;
	alfa: integer;
	encu: integer;
    end;

    archBase = file of datBase;

// Inicio Modulos


procedure leerDatoBase(var dato: datBase);
begin
    write('nom: '); readln(dato.nom);
    if (dato.nom <> corte) then
    begin
	write('alfabetizados: '); readln(dato.alfa);
	write('encuestados: '); readln(dato.encu);
    end;
end;

procedure leerDatoCenso(var dato: datCenso);
begin
    write('nom: '); readln(dato.nom);
    if (dato.nom <> corte) then
    begin
	write('alfabetizados: '); readln(dato.alfa);
	write('encuestados: '); readln(dato.encu);
	write('codigo: '); readln(dato.cod);
    end;
end;

procedure testGenerarMaestro(var mastr: archBase);
var
    dato: datBase;
    op: integer;
begin
    writeln('ingresar 1 para generar un nuevo maestro, otro numero para usar el anterior');
    readln(op);
    if (op = 1) then
    begin
	rewrite(mastr);
	leerDatoBase(dato);
	while (dato.nom <> corte) do 
	begin
	    write(mastr, dato);
	    leerDatoBase(dato);
	end;
    end;
    writeln('fin de ingreso de maestro');
end;

procedure testGenerarDetalle(var det: archCenso);
var
    dato: datCenso;
    op: integer;
begin
    writeln('ingresar 1 para generar un nuevo detalle, otro numero para usar el anterior');
    readln(op);
    if (op = 1) then
    begin
	rewrite(det);
	leerDatoCenso(dato);
	while (dato.nom <> corte) do 
	begin
	    write(det, dato);
	    leerDatoCenso(dato);
	end;
    end;
    writeln('fin de ingreso de detalle');
end;

procedure imprimir(var mastr: archBase);
var
    dato: datBase;
begin
    reset(mastr);
    while (not EOF(mastr)) do
    begin
	read(mastr, dato);
	writeln('{nom: ' + dato.nom + ', alfabetizados: ' + IntToStr(dato.alfa) + ', encuestados: ' + IntToStr(dato.encu) + '}');
    end;
end;

procedure minimo(var det1, det2: archCenso; var ret: datCenso);
var
    dato1, dato2: datCenso;
begin
    if (EOF(det1)) then
	read(det2, ret)
    else if (EOF(det2)) then
	read(det1, ret)
    else begin
	read(det1, dato1);
	read(det2, dato2);
	if (dato1.nom <= dato2.nom) then
	begin
	    ret:= dato1;
	    seek(det2, filePos(det2) - 1);
	end
	else begin
	    ret:= dato2;
	    seek(det1, filePos(det1) - 1);
	end;
    end;
end;

//Suma mal el ultimo parece, no se porque todavia
procedure actualizarMaestro(var mastr: archBase; var det1, det2: archCenso);
var
    datoDetalle: datCenso;
    datoMastr: datBase;
begin
    reset(mastr); reset(det1); reset(det2);
    read(mastr, datoMastr);
    while (not ((EOF(det1)) and (EOF(det2)))) do
    begin
	minimo(det1, det2, datoDetalle);
	while ((not EOF(mastr)) and (datoMastr.nom <> datoDetalle.nom)) do
	begin
	    read(mastr, datoMastr);
	end;
	datoMastr.alfa:= datoMastr.alfa + datoDetalle.alfa;
	datoMastr.encu:= datoMastr.encu + datoDetalle.encu;
	seek(mastr, filePos(mastr) - 1);
	write(mastr, datoMastr);
    end;
end;


// Inicio Programa Principal
var
    mastr: archBase;
    det1, det2: archCenso;
begin
    assign(mastr, 'baseCensal');
    assign(det1, 'censo1');
    assign(det2, 'censo2');
    testGenerarMaestro(mastr);
    testGenerarDetalle(det1);
    testGenerarDetalle(det2);
    writeln('Datos ingresados: ');
    imprimir(mastr);
    actualizarMaestro(mastr, det1, det2);
    writeln('Maestro actualizado: ');
    imprimir(mastr);
    close(mastr);
    close(det1);
    close(det2);
end.

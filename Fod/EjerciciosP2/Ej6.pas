//    Se desea modelar la información necesaria para un sistema de recuentos de casos de covid
//para el ministerio de salud de la provincia de buenos aires.Diariamente se reciben archivos provenientes de los distintos municipios, la información
//contenida en los mismos es la siguiente: código de localidad, código cepa, cantidad de
//casos activos, cantidad de casos nuevos, cantidad de casos recuperados, cantidad de casos
//fallecidos.
//    El ministerio cuenta con un archivo maestro con la siguiente información: código localidad,
//nombre localidad, código cepa, nombre cepa, cantidad de casos activos, cantidad de casos
//nuevos, cantidad de recuperados y cantidad de fallecidos.
//    Se debe realizar el procedimiento que permita actualizar el maestro con los detalles
//recibidos, se reciben 10 detalles. Todos los archivos están ordenados por código de
//localidad y código de cepa.
//
//Para la actualización se debe proceder de la siguiente manera:
//    1. Al número de fallecidos se le suman el valor de fallecidos recibido del detalle.
//    2. Idem anterior para los recuperados.
//    3. Los casos activos se actualizan con el valor recibido en el detalle.
//    4. Idem anterior para los casos nuevos hallados.
//
//    Realice las declaraciones necesarias, el programa principal y los procedimientos que
//requiera para la actualización solicitada e informe cantidad de localidades con más de 50
//casos activos (las localidades pueden o no haber sido actualizadas).

program Ej6;
uses SysUtils;
const
    valorAlto = 9999;
    dimF = 10;
type
    rangoArch = 1..dimF;

    datoMastr = record
	codLoc: integer;
	nomLoc: string;
	codCep: integer;
	nomCep: string;
	cAct: integer;
	cNue: integer;
	cRec: integer;
	cFall: integer;
    end;

    archMae = File of datoMastr;

    datoDet = record
	codLoc: integer;
	codCep: integer;
	cAct: integer;
	cNue: integer;
	cRec: integer;
	cFall: integer;
    end;

    archDet = File of datoDet;
    
    vector = array [rangoArch] of archDet;
    vectorReg = array [rangoArch] of datoDet;

    lista = ^nodo;
    nodo = record
	d: datoDet;
	sig: lista;
    end;

// Inicio Modulos


procedure iniArchivos(var mae: archMae; var v: vector);
var
    i: integer;
    nomDet: string;
begin
    assign(mae, '6maestro');
    for i:= 1 to dimF do
    begin
	nomDet:= '6det' + IntToStr(i);
	assign(v[i], nomDet);
	rewrite(v[i]);
    end;
end;

procedure generarDatoDetalle(var d: datoDet; i: integer);
// recibe i para poder asegurarse de que los codLoc generados
// no coincidan en distintos municipios
begin
    d.codLoc:= Random(20) + ((i - 1) * 20) + 1;
    d.codCep:= Random(5) + 1;
    d.cAct:= Random(50) + 100;
    d.cNue:= Random(20);
    d.cRec:= Random(20);
    d.cFall:= Random(20);
end;

procedure escribirLista(var det: archDet; var l: lista);
begin
    while (l <> nil) do
    begin
	write(det, l^.d);
	l:= l^.sig;
    end;
end;

procedure insertarOrdenado (var pI:lista; valor:datoDet);
var
    actual, ant, nuevo: lista;
begin
    new(nuevo);
    nuevo^.d:= valor;
    nuevo^.sig:= nil;
    if (pI = nil) then
	pI:= nuevo
    else
    begin
	actual:= pI;
	ant:= pI;
	while (actual <> nil) and (actual^.d.codLoc < nuevo^.d.codLoc) do
	begin
	    ant:= actual;
	    actual:= actual^.sig;
	end;
	if (actual = pI) then
	begin
	    nuevo^.sig:= pI;
	    pI:= nuevo;
	end
	else
	begin
	    ant^.sig:= nuevo;
	    nuevo^.sig:= actual;
	end;
    end;
end;

procedure generarDatosVector(var v: vector);
var
    i, j, cant: integer;
    dato: datoDet;
    l: lista;
begin
    l:= nil;
    for i:= 1 to dimF do
    begin
	cant:= Random(7) + 1;
	for j:= 1 to cant do
	begin
	    generarDatoDetalle(dato, i);
	    insertarOrdenado(l, dato);
	end;
	escribirLista(v[i], l);
    end;
end;

procedure resetVect(var v: vector);
var
    i: integer;
begin
    for i:= 1 to dimF do
    begin
	reset(v[i]);
    end;
end;

procedure checkDatosVector(var v: vector);
var
    i: integer;
    dato: datoDet;
begin
    resetVect(v);
    writeln('Inicio datos generados vector');
    for i:= 1 to dimF do
    begin
	writeln('Datos Det' + IntToStr(i) + ': ');
	while (not EOF(v[i])) do
	begin
	    read(v[i], dato);
	    writeln('{codLoc: ' + IntToStr(dato.codLoc) + ', fallecidos: ' + IntToStr(dato.cFall) + ', activos: ' + IntToStr(dato.cAct) + '}');
	end;
	writeln();
    end;
end;

procedure imprimir(var mae: archMae);
var
    dato: datoMastr;
begin
    while (not EOF(mae)) do
    begin
	read(mae, dato);
	writeln('{codLoc: ' + IntToStr(dato.codLoc) + ', fallecidos: ' + IntToStr(dato.cFall) + ', activos: ' + IntToStr(dato.cAct) + '}');
    end;
end;

procedure cerrarArchivos(var mae: archMae; var v: vector);
var
    i: integer;
begin
    close(mae);
    for i:= 1 to dimF do
    begin
	close(v[i]);
    end;
end;

procedure leerDatos(var v: vector; var datos: vectorReg);
var
    i: integer;
begin
    for i:= 1 to dimF do
    begin
	read(v[i], datos[i]);
    end;
end;

function menorPos(datos: vectorReg): rangoArch;
var
    aux: rangoArch;
    minAct, i, minCep: integer;
begin
    minAct:= valorAlto;
    minCep:= valorAlto;
    for i:= 1 to dimF do
    begin
	if ((datos[i].codLoc < minAct) or ((datos[i].codLoc = minAct) and (datos[i].codCep < minCep))) then
	begin
	    aux:= i;
	    minAct:= datos[i].codLoc;
	    minCep:= datos[i].codCep;
	end;
    end;
    menorPos:= aux;
end;

procedure leer(var arch: archDet; var dato: datoDet);
begin
    if (EOF(arch)) then
	dato.codLoc:= valorAlto
    else
	read(arch, dato);
end;

procedure minimo(var v:vector; var datos: vectorReg; var min: datoDet);
var
    pos: rangoArch;
begin
    pos:= menorPos(datos);
    min:= datos[pos];
    leer(v[pos], datos[pos]);
end;

procedure actualizarMaestro(var mae: archMae; var v: vector);
var
    datos: vectorReg;
    min: datoDet;
    act: datoMastr;
begin
    resetVect(v);
    reset(mae);
    leerDatos(v, datos);
    minimo(v, datos, min);
    read(mae, act);
    while (min.codLoc <> valorAlto) do
    begin
	while (act.codLoc <> min.codLoc) do
	begin
	    read(mae, act);
	end;
	act.cFall:= act.cFall + min.cFall;
	act.cRec:= act.cRec + min.cRec;
	act.cAct:= min.cAct;
	act.cNue:= min.cNue;
	seek(mae, filePos(mae) -1);
	write(mae, act);
	minimo(v, datos, min);
    end;
end;


// Inicio Programa Principal
var
    mae: archMae;
    vectDet: vector;
begin
    iniArchivos(mae, vectDet);
    generarDatosVector(vectDet);
    checkDatosVector(vectDet);
    actualizarMaestro(mae, vectDet);
    imprimir(mae);
    cerrarArchivos(mae, vectDet);
end.

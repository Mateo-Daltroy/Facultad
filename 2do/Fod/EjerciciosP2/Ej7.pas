program Ej7;
uses SysUtils;
const
    valorAlto = 9999;
type
    regMae = record
	cod: integer;
	ap: string;
	nom: string;
	cantCur: integer;
	cantFin: integer;
    end;

    archMae = File of regMae;

    regCurs = record
	cod: integer;
	codMat: integer;
	ano: string;
	paso: boolean;
    end;

    archCurs = File of regCurs;

    regFin = record
	cod: integer;
	codMat: integer;
	fecha: string;
	nota: integer;
    end;

    archFin = File of regFin;

// Inicio Modulos Testing


procedure imprimirDetalleCursadas(var detCur: archCurs);
var
    temp: regCurs;
begin
    reset(detCur);
    writeln('Detalle de Cursadas:');
    while (not EOF(detCur)) do
    begin
	read(detCur, temp);
	writeln('{cod: ', temp.cod, ', codMat: ', temp.codMat, ', ano: ', temp.ano, ', paso: ', BoolToStr(temp.paso, True), '}');
    end;
    writeln();
end;

procedure imprimirDetalleFinales(var detFin: archFin);
var
    temp: regFin;
begin
    reset(detFin);
    writeln('Detalle de Finales:');
    while (not EOF(detFin)) do
    begin
	read(detFin, temp);
	writeln('{cod: ', temp.cod, ', codMat: ', temp.codMat, ', fecha: ', temp.fecha, ', nota: ', temp.nota, '}');
    end;
    writeln();
end;

procedure generarDatos(var mae: archMae; var detCur: archCurs; var detFin: archFin);
var
    rMae: regMae;
    rCur: regCurs;
    rFin: regFin;
begin
    // Maestro
    rewrite(mae);
    rMae.cod := 1; rMae.ap := 'Lopez'; rMae.nom := 'Ana'; rMae.cantCur := 0; rMae.cantFin := 0;
    write(mae, rMae);

    rMae.cod := 2; rMae.ap := 'Gomez'; rMae.nom := 'Luis'; rMae.cantCur := 1; rMae.cantFin := 1;
    write(mae, rMae);

    rMae.cod := 3; rMae.ap := 'Perez'; rMae.nom := 'Maria'; rMae.cantCur := 0; rMae.cantFin := 0;
    write(mae, rMae);

    // Detalle de Cursadas
    rewrite(detCur);
    rCur.cod := 1; rCur.codMat := 101; rCur.ano := '2022'; rCur.paso := true;
    write(detCur, rCur);
    rCur.codMat := 102; rCur.ano := '2023'; rCur.paso := true;
    write(detCur, rCur);

    rCur.cod := 2; rCur.codMat := 103; rCur.ano := '2023'; rCur.paso := false;
    write(detCur, rCur);

    rCur.cod := 3; rCur.codMat := 104; rCur.ano := '2023'; rCur.paso := true;
    write(detCur, rCur);

    // Detalle de Finales
    rewrite(detFin);
    rFin.cod := 1; rFin.codMat := 101; rFin.fecha := '01/07/2023'; rFin.nota := 4;
    write(detFin, rFin);
    rFin.codMat := 102; rFin.fecha := '15/07/2023'; rFin.nota := 2;
    write(detFin, rFin);
    rFin.cod := 2; rFin.codMat := 105; rFin.fecha := '20/07/2023'; rFin.nota := 7;
    write(detFin, rFin);

    // Mostrar los detalles generados
    imprimirDetalleCursadas(detCur);
    imprimirDetalleFinales(detFin);
end;

// Inicio Modulos

procedure imprimir (var mae: archMae);
var
    alu: regMae;
begin
    reset(mae);
    while (not EOF(mae)) do
    begin
	read(mae, alu);
	writeln('{cod: ' + IntToStr(alu.cod) + ', cursadas: ' + IntToStr(alu.cantCur) + ', finales: ' + IntToStr(alu.cantFin) + '}');
    end;
    writeln();
end;

procedure leerCur(var arch: archCurs; var dato: regCurs);
begin
    if (not EOF(arch)) then
	read(arch, dato)
    else
	dato.cod:= valorAlto;
end;

procedure leerFin(var arch: archFin; var dato: regFin);
begin
    if (not EOF(arch)) then
	read(arch, dato)
    else
	dato.cod:= valorAlto;
end;

procedure leerAlu(var arch: archMae; var dato: regMae);
begin
    if (not EOF(arch)) then
	read(arch, dato)
    else
	dato.cod:= valorAlto;
end;

function finAmbos(fin: regFin; cur: regCurs): boolean;
begin
    finAmbos:= ((fin.cod = valorAlto) and (cur.cod = valorAlto));
end;

function codMenor(fin: regFin; cur: regCurs): integer;
// 0 = trabajar con final || 1 = trabajar con cursada
// No es un booleano por escalabilidad
begin
    if (fin.cod <= cur.cod) then
	codMenor:= 0
    else
	codMenor:= 1;
end;

function contarFinales(var det: archFin; var dato: regFin): integer;
var
    codAct, aux: integer;
begin
    aux:= 0;
    codAct:= dato.cod;
    while (codAct = dato.cod) do
    begin
	if (dato.nota >= 4) then
	    aux:= aux + 1;
	leerFin(det, dato);
    end;
    contarFinales:= aux;
end;

function contarCursadas(var det: archCurs; var dato: regCurs): integer;
var
    codAct, aux: integer;
begin
    aux:= 0;
    codAct:= dato.cod;
    while (codAct = dato.cod) do
    begin
	if (dato.paso) then
	    aux:= aux + 1;
	leerCur(det, dato);
    end;
    contarCursadas:= aux;
end;

procedure actualizarMaestro(var mae: archMae; var detCur: archCurs; var detFin: archFin);
var
    menor, cantFin, cantCurs: integer;
    curAct: regCurs;
    finAct: regFin;
    aluAct: regMae;
begin
    reset(mae); reset(detCur); reset(detFin);
    leerCur(detCur, curAct);
    leerFin(detFin, finAct);
    leerAlu(mae, aluAct);
    while (not finAmbos(finAct, curAct)) do
    begin
	menor:= codMenor(finAct, curAct);
	if (menor = 0) then
	begin
	    while (aluAct.cod <> finAct.cod) do //no pregunta mae.cod = valorAlto porque se asume que el alumno existe
	    begin
		leerAlu(mae, aluAct);
	    end;
	    cantFin:= contarFinales(detFin, finAct);
	    aluAct.cantFin:= aluAct.cantFin + cantFin;
	    seek(mae, filepos(mae) - 1);
	    write(mae, aluAct);
	end
	else
	begin
	    while (aluAct.cod <> curAct.cod) do
	    begin
		leerAlu(mae, aluAct);
	    end;
	    cantCurs:= contarCursadas(detCur, curAct);
	    aluAct.cantCur:= aluAct.cantCur + cantCurs;
	    seek(mae, filepos(mae) - 1);
	    write(mae, aluAct);
	end;
    end;
end;


// Inicio Programa Principal
var
    mae: archMae;
    detCur: archCurs;
    detFin: archFin;
begin
    assign(mae, 'mae7');
    assign(detCur, 'detCur7');
    assign(detFin, 'detFin7');
    generarDatos(mae, detCur, detFin);
    actualizarMaestro(mae, detCur, detFin);
    imprimir(mae);
    close(mae); close(detCur); close(detFin);
end.

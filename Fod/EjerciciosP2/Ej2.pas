program Ej2;
uses SysUtils; //Maybe delete?
type
    producto = record
	cod: integer;
	nom: string;
	precio: real;
	stkAct: integer;
	stkMin: integer;
    end;

    archProd = File of producto;

    venta = record
	cod: integer;
	cant: integer;
    end;

    archVent = File of venta;

// Inicio Modulos


procedure leerProd(var prod: producto);
begin
    write('cod: '); readln(prod.cod);
    if (prod.cod <> 0) then
    begin
	//write('nom: '); readln(prod.nom);
	//write('precio: '); readln(prod.precio);
	prod.nom:= 'aux';
	prod.precio:= 1;
	write('stkAct: '); readln(prod.stkAct);
	write('stkMin: '); readln(prod.stkMin);
    end;
end;

procedure leerVenta(var vent: venta);
begin
    write('cod: '); readln(vent.cod);
    if (vent.cod <> 0) then
    begin
	write('cant: '); readln(vent.cant);
    end;
end;

procedure testCargarMaster(var mastr: archProd);
var
    prod: producto;
    op: integer;
begin
    write('ingresar 1 para crear el maestro, otro numero para usar el anterior: ');
    readln(op);
    if (op = 1) then
    begin
	rewrite(mastr);
	leerProd(prod);
	while(prod.cod <> 0) do
	begin
	    write(mastr, prod);
	    leerProd(prod);
	end;
	writeln('fin de carga');
    end;
end;


procedure testCargarDetalle(var det: archVent);
var
    vent: venta;
    op: integer;
begin
    write('ingresar 1 para crear el detalle, otro numero para usar el anterior: ');
    readln(op);
    if (op = 1) then
    begin
	rewrite(det);
	leerVenta(vent);
	while(vent.cod <> 0) do
	begin
	    write(det, vent);
	    leerVenta(vent);
	end;
	writeln('fin de carga');
    end;
end;

procedure imprimirProd(prod: producto);
begin
    writeln('{cod: ' + IntToStr(prod.cod) + ', stkAct: ' + IntToStr(prod.stkAct) + ', stkMin: ' + IntToStr(prod.stkMin) + '}');
end;

procedure imprimir(var mastr: archProd);
var
    prod: producto;
begin
    reset(mastr);
    while(not EOF(mastr)) do
    begin
	read(mastr, prod);
	imprimirProd(prod);
    end;
end;

procedure actStkMin(var mastr: archProd);
var
    export: Text;
    prod: producto;
begin
    assign(export, 'stock_minimo.txt');
    rewrite(export);
    reset(mastr);
    writeln(export, 'Codigo, stock actual y stock minimo de todos los productos actualmente por debajo de su minimo:');
    while (not EOF(mastr)) do
    begin
	read(mastr, prod);
	if (prod.stkAct < prod.stkMin) then
	begin
	    writeln(export, '{cod: ' + IntToStr(prod.cod) + ', stkAct: ' + IntToStr(prod.stkAct) + ', stkMin: ' + IntToStr(prod.stkMin) + '}');
	end;
    end;
    close(export);
end;

procedure actualizarMaster(var mastr: archProd; var det: archVent);
var
    prod: producto;
    vent: venta;
begin
    reset(mastr);
    reset(det);
    read(mastr, prod);
    while (not EOF(det)) do
    begin
	read(det, vent);
	while (prod.cod < vent.cod) do
	begin
	    read(mastr, prod);
	end;
	prod.stkAct:= prod.stkAct - vent.cant;
	seek(mastr, filePos(mastr) - 1);
	write(mastr, prod);
    end;
end;


// Inicio Programa Principal
var
    mastr: archProd;
    det: archVent;
begin
    assign(mastr, 'prods2');
    assign(det, 'aux');
    testCargarMaster(mastr);
    testCargarDetalle(det);
    writeln('pre actualizacion:');
    imprimir(mastr);
    actualizarMaster(mastr, det);
    writeln('post actualizacion:');
    imprimir(mastr);
    actStkMin(mastr);
    close(mastr);
    close(det);
end.

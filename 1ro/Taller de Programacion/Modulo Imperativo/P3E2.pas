program P3E2;
type
    venta = record
        cod: integer;
        fec: string;
        cant: integer;
    end;

    venta2 = record
        cod: integer;
        cant: integer;
    end;

    arbol = ^nodo;
    nodo = record
        d: venta;
        HI: arbol;
        HD: arbol;
    end;

    arbol2 = ^nodo2;
    nodo2 = record
        d: venta2;
        HI: arbol2;
        HD: arbol2;
    end;

// Inicio Modulos


procedure leerVenta(var v: venta; var v2: venta2);
begin
    write('Introducir codigo de venta: ');
    readln(v.cod);
    if (v.cod <> 0) then
    begin
        v2.cod:= v.cod;
        write('Introducir fecha de venta: ');
        readln(v.fec);
        write('Introducir cantidad de unidades vendidas: ');
        readln(v.cant);
        v2.cant:= v.cant;
    end;
    writeln;
end;

procedure generarArbol1(var a: arbol; v: venta);
begin
    if (a = nil) then
    begin
        new(a);
        a^.d:= v;
        a^.HI:= nil;
        a^.HD:= nil;
    end
    else if (v.cod <= a^.d.cod) then
        generarArbol1(a^.HI, v)
    else
        generarArbol1(a^.HD, v);
end;

procedure generarArbol2(var a: arbol2; v:venta2);
begin
    if (a = nil) then
    begin
        new(a);
        a^.d:= v;
        a^.HI:= nil;
        a^.HD:= nil;
    end
    else if (v.cod < a^.d.cod) then
        generarArbol2(a^.HI, v)
    else if (v.cod = a^.d.cod) then
		a^.d.cant:= a^.d.cant + v.cant
	else
        generarArbol2(a^.HD, v);
end;

procedure leerDatos(var a1: arbol; var a2: arbol2);
var
    v1: venta;
    v2: venta2;
begin
    a1:= nil;
    a2:= nil;
    leerVenta(v1, v2);
    while (v1.cod <> 0) do
    begin
        generarArbol1(a1, v1);
        generarArbol2(a2, v2);
        leerVenta(v1, v2);
    end;
end;

function busqCant1(a: arbol; busq: integer):integer;
var
	aux: integer;
begin
    if (a = nil) then
        busqCant1:= 0 //Valor no encontrado
    else if (a^.d.cod = busq) then
    begin
        aux:= a^.d.cant;
        busqCant1:= busqCant1(a^.HI, busq) + aux;
    end
    else if (busq > a^.d.cod) then
        busqCant1:= busqCant1(a^.HD, busq)
    else
        busqCant1:= busqCant1(a^.HI, busq);
end;

function busqCant2(a: arbol2; busq: integer):integer;
begin
    if (a = nil) then
        busqCant2:= -1 //Valor no encontrado
    else if (a^.d.cod = busq) then
        busqCant2:= a^.d.cant
    else if (busq > a^.d.cod) then
        busqCant2:= busqCant2(a^.HD, busq)
    else
        busqCant2:= busqCant2(a^.HI, busq);
end;


// Inicio Programa Principal

var
    a: arbol;
    a2: arbol2;
    cod, tot: integer;
begin
    leerDatos(a, a2);
    write('Introducir codigo a buscar: ');
    readln(cod);
    tot:= busqCant1(a, cod);
    write('El codigo '); write(cod);
    write(' vendio '); write(tot);
    writeln(' unidades.');
    write('Introducir codigo a buscar: ');
    readln(cod);
    tot:= busqCant2(a2, cod);
    write('El codigo '); write(cod);
    write(' vendio '); write(tot);
    writeln(' unidades.');
end.

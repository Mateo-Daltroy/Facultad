program P4E1;
type
    venta = record
        cod: integer;
        uni: integer;
        monto: real;
    end;

    arbol = ^nodo;
    nodo = record
        d: venta;
        HI: arbol;
        HD: arbol;
    end;

// Inicio Modulos


procedure leerVenta(var v: venta);
begin
    write('Introducir codigo de la venta: ');
    readln(v.cod);
    if (v.cod <> -1) then
    begin
        write('Introducir unidades vendidas: ');
        readln(v.uni);
        write('Introducir monto total: ');
        readln(v.monto);
    end;
end;

procedure generarArbol(var a: arbol; v: venta);
begin
    if (a = nil) then
    begin
        new(a);
        a^.d:= v;
        a^.HI:= nil;
        a^.HD:= nil;
    end
    else if (v.cod <= a^.d.cod) then
        generarArbol(a^.HI, v)
    else
        generarArbol(a^.HD, v);
end;

procedure leerDatos(var a: arbol);
var
    v: venta;
begin
    a:= nil;
    leerVenta(v);
    while (v.cod <> -1) do
    begin
        generarArbol(a, v);
        leerVenta(v);
    end;
    writeln;
end;

procedure impEnOrden(a: arbol);
begin
    if (a <> nil) then
    begin
        impEnOrden(a^.HI);
        write('Venta: ');
        write(a^.d.cod);
        write('. Unidades: ');
        write(a^.d.uni);
        write('. Monto total: ');
        writeln(a^.d.monto);
        impEnOrden(a^.HD);
    end;
end;

procedure maxUni(a: arbol; var cod: integer; var max: integer);
// Ini max en -1 desde afuera
begin
    if (a <> nil) then
    begin
        maxUni(a^.HI, cod, max);
        if (a^.d.uni > max) then
        begin
            cod:= a^.d.cod;
            max:= a^.d.uni;
        end;
        maxUni(a^.HD, cod, max);
    end;
end;

procedure cantCodMen(a: arbol; min: integer; var cont: integer);
begin
    if (a <> nil) then
    begin
        if (a^.d.cod > min) then
            cantCodMen(a^.HI, min, cont);
        else
        begin
            cont:= cont + 1;
            cantCodMen(a^.HI, min, cont);
            cantCodMen(a^.HD, min, cont);
        end;
    end;
end;


// Consultar por este modulo
procedure codigosEntre(a: arbol; min, max: integer; var tot: real);
// Ini tot en 0 desde afuera
begin
    if (a <> nil) then
    begin
        if (a^.d.cod > min) then
        begin
            codigosEntre(a^.HI, min, max, tot);
            if (a^.d.cod < max) then
                tot:= tot + a^.d.monto;
        end;
        if (a^.d.cod < max) then
            codigosEntre(a^.HD, min, max, tot);
    end;
end;


// Inicio Programa Principal

var
    a: arbol;
    cod, max, min: integer;
    tot: real;
begin
    leerDatos(a);
    max:= -1;
    maxUni(a, cod, max);
    write('El codigo con mayor cantidad de unidades es: ');
    writeln(cod);
    write('Introducir codigo minimo a sumar: ');
    readln(min);
    write('Introducir codigo maximo a sumar: ');
    readln(max);
    tot:= 0;
    codigosEntre(a, min, max, tot);
    write('El monto total de todos los codigos que califican es: ');
    writeln(tot);
end.

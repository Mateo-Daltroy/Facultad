program P3E3;
type
    final = record
        cod: integer;
        nota: integer;
    end;

    lista = ^nodo;
    nodo = record
        d: final;
        sig: lista;
    end;

    alumno = record
        leg: integer;
        dni: integer;
        ing: integer;
        fin: lista;
    end;

	arbol = ^nodoArb;
    nodoArb = record
        d: alumno;
        HI: arbol;
        HD: arbol;
    end;

    regsitroB = record
        dni: integer;
        ing: integer;
    end;

    listaB = ^nodoB;
    nodoB = record
        d: regsitroB;
        sig: listaB;
    end;

    registroF = record
        prom: real;
        leg: integer;
    end;

    listaF = ^nodoF;
    nodoF = record
        d: registroF;
        sig: listaF;
    end;

// Inicio Modulos


procedure generarLista(var l: lista);
var
    aux: lista;
begin
    l:= nil;
    new(aux);
    write('Introducir codigo de la materia: ');
    readln(aux^.d.cod);
    while (aux^.d.cod <> -1) do
    begin
        write('introducir nota del final: ');
        readln(aux^.d.nota);
        aux^.sig:= l;
        l:= aux;
        new(aux);
        write('Introducir codigo de la materia: ');
        readln(aux^.d.cod);
    end;
end;

procedure leerAlumno(var a: alumno);
begin
    write('Ingresar legajo del alumno: ');
    readln(a.leg);
    if (a.leg <> 0) then
    begin
        write('Ingresar dni del alumno: ');
        readln(a.dni);
        write('Ingresar anio de ingreso: ');
        readln(a.ing);
        generarLista(a.fin);
        writeln;
    end;
end;

procedure armarArbol(var a: arbol; n: alumno);
begin
    if (a = nil) then
    begin
        new(a);
        a^.d:= n;
        a^.HI:= nil;
        a^.HD:= nil;
    end
    else if (n.leg <= a^.d.leg) then
        armarArbol(a^.HI, n)
    else
        armarArbol(a^.HD, n);
end;

procedure leerDatos (var a: arbol);
var
    nuevo: alumno;
begin
    a:= nil;
    leerAlumno(nuevo);
    while (nuevo.leg <> 0) do
    begin
        armarArbol(a, nuevo);
        leerAlumno(nuevo);
    end;
    writeln;
end;


// No se como hacer este modulo 
{
procedure puntoB(a: arbol; var l: listaB; max: integer);
// Inicializar l en nil desde afuera
var
    aux: listaB;
begin
    if (a <> nil) then
    begin
        if (a^.d.leg < max) then
        begin
            generarListaB()
        end;
    end;
end;
}

function legajoMayor(a: arbol): integer;
begin
    if (a^.HD <> nil) then
        legajoMayor:= legajoMayor(a^.HD)
    else
        legajoMayor:= a^.d.leg;
end;

procedure dniMayor(a: arbol; var max: integer);
// Ini max en -1 desde afuera
begin
    if (a <> nil) then
    begin
        dniMayor(a^.HI, max);
        if (a^.d.dni > max) then
            max:= a^.d.dni;
        dniMayor(a^.HD, max);
    end;
end;

procedure cantLegImpar(a: arbol; var cont: integer);
// Ini cont en 0 desde afuera
begin
    if (a <> nil) then
    begin
        cantLegImpar(a^.HI, cont);
        if ((a^.d.leg MOD 2) <> 0) then
            cont:= cont + 1;
        cantLegImpar(a^.HD, cont);
    end;
end;

function calcularProm(l: lista): real;
var
    tot, cont: integer;
begin
    cont:= 0;
    tot:= 0;
    while (l <> nil) do
    begin
        tot:= tot + l^.d.nota;
        cont:= cont + 1;
        l:= l^.sig;
    end;
    calcularProm:= (tot / cont);
end;

procedure mayPromedio(a: arbol; var prom: real; var leg: integer);
// Ini prom en -1 desde afuera
var
    nprom: real;
begin
    if (a <> nil) then
    begin
        mayPromedio(a^.HI, prom, leg);
        nprom:= calcularProm(a^.d.fin);
        if (nprom > prom) then
        begin
            prom:= nprom;
            leg:= a^.d.leg;
        end;
        mayPromedio(a^.HD, prom, leg);
    end;
end;

procedure listaPromSupera(a: arbol; var l: listaF; min: real);
// Inicializar l en nil desde afuera
var
    aux: listaF;
    prom: real;
begin
    if (a <> nil) then
    begin
        listaPromSupera(a^.HI, l, min);
        prom:= calcularProm(a^.d.fin);
        if (prom > min) then
        begin
            new(aux);
            aux^.d.prom:= prom;
            aux^.d.leg:= a^.d.leg;
            aux^.sig:= l;
            l:= aux;
        end;
        listaPromSupera(a^.HD, l, min);
    end;
end;

procedure imprimirListaB(l: listaB);
begin
    while (l <> nil) do
    begin
        write(l^.d.dni); write(', ');
        write(l^.d.ing);
        write(' --- ');
        l:= l^.sig;
    end;
    writeln;
end;

procedure imprimirListaF(l: listaF);
begin
    while (l <> nil) do
    begin
        write(l^.d.leg); write(', ');
        write(l^.d.prom);
        write(' --- ');
        l:= l^.sig;
    end;
    writeln;
end;


// Inicio Programa Principal

var
    a: arbol;
    lb: listaB;
    lf: listaF;
    max, cont, leg: integer;
    prom: real;
begin
    leerDatos(a);
{    lb:= nil;
    write('Introducir anio maximo de listaB: ');
    readln(max);
    puntoB(a, lb, max);
    imprimirListaB(lb);
}   write('El legajo mayor es: ');
    writeln(legajoMayor(a));
    write('El dni mayor es: ');
    max:= -1;
    dniMayor(a, max);
    writeln(max);
    cont:= 0;
    cantLegImpar(a, cont);
    write('La cantidad de legajos impares es: ');
    writeln(cont);
    prom:= -1;
    mayPromedio(a, prom, leg);
    write('El alumno con el mayor promedio tiene el legajo ');
    write(leg);
    write(' y su promedio es: ');
    writeln(prom);
    writeln;
    write('Introducir promedio minimo: ');
    readln(prom);
    lf:= nil;
    listaPromSupera(a, lf, prom);
    imprimirListaF(lf);
end.

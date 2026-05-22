program P5E1;
const
    maxOfis = 300;
type
    cantOfis = 1..maxOfis;

    oficina = record
        cod: integer;
        dni: integer;
        valor: real;
    end;

    vector = array [cantOfis] of oficina;

// Inicio Modulos


procedure leerOficina(var o: oficina);
begin
    write('Introducir codigo de la oficina: ');
    readln(o.cod);
    if (o.cod <> -1) then
    begin
        write('Introducir dni del propietario: ');
        readln(o.dni);
        write('Introducir valor de expensas: ');
        readln(o.valor);
    end;
end;

procedure leerDatos(var v: vector; dimL: cantOfis);
var
    ofi: oficina;
begin
    dimL:= 0;
    leerOficina(ofi);
    while (ofi.cod <> -1) and (dimL < maxOfis) do
    begin
        dimL:= dimL + 1;
        v[dimL]:= ofi;
        leerOficina(ofi);
    end;
    writeln;
end;

procedure ordenarVector(var v: vector; dimL: integer);
var
    i, j, pos: cantOfis;
    aux: oficina;
begin
    for i:= 1 to (dimL - 1) do
    begin
        pos:= i;
        for j:= (i+1) to dimL do
        begin
            if (v[j].cod < v[pos].cod) then
                pos:= j;
        end;
        aux:= v[pos];
        v[pos]:= v[i];
        v[i]:= aux;
    end;
end;

function busquedaDicotomica(v: vector; dimL: cantOfis; valor: integer): integer;
var
    ini, fin, medio: cantOfis;
begin
    ini:= 1;
    fin:= dimL;
    while (ini <= fin) do
    begin
        medio:= (fin + ini) DIV 2;
        if (v[medio].cod = valor) then
            busquedaDicotomica:= medio
        else if (v[medio].cod < valor) then
            ini:= medio + 1
        else
            fin:= medio - 1;
    end;
    busquedaDicotomica:= 0;
end;

{
procedure expensasTot(v: vector; dimL: integer; var tot: real);
// Ini tot en 0 desde afuera;
begin
    if (dimL > 0) then
    begin
        tot:= tot + v[dimL].valor;
        expensasTot(v, (dimL-1), tot);
    end;
end;
}

function expensasTot(v: vector; dimL: integer): real;
// Ini tot en 0 desde afuera;
begin
    if (dimL > 0) then
        expensasTot:= v[dimL].valor + expensasTot(v, (dimL-1), tot)
    else
        expensasTot:= 0;
end;


// Inicio Programa Principal

var
begin
    
end.
program P2E5;
const

type

// Inicio Modulos


procedure busquedaDicotomica(v: vector; ini, fin: integer; dato: integer; var pos: integer);
var 
    aux: integer;
begin
    pos:= -1;
    while (ini <> fin) and (pos = -1) do
    begin
        aux:= fin / 2;
        if (v[aux] = dato) then
            pos:= aux
        else if (v[aux] < dato) then
            ini:= aux
        else
            fin:= aux;
    end;
end;


// Inicio Programa Principal

var
begin
    
end.
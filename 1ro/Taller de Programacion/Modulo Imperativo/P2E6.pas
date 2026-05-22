program ej6practica2;
type 
  lista = ^nodo;
  
  binario = 0..1;
  
  
  nodo = record
    dato: binario;
    sig: lista;
  end;

procedure agregar_adelante (var l: lista; dig: binario);
var
  nuevo: lista;
begin
  new(nuevo);
  nuevo^.dato:= dig;
  nuevo^.sig:= l;
  l:= nuevo;
end;

procedure calcular_binario (num: integer; var l: lista);
var
  d: binario;
begin
  if (num <>0) then begin
    d:= num MOD 2;
    agregar_adelante (l,d);
    calcular_binario (num DIV 2, l);
  end;
end;

procedure imprimir (l: lista);
begin
  while(l<>nil) do begin
    write(l^.dato);
    l:= l^.sig;
  end;
end;
procedure leer (var l: lista);
var
  num: integer;
begin
  writeln('ingrese un numero decimal: ');
  readln(num);
  while (num <>0) do begin
    calcular_binario (num, l);
    imprimir(l);
    l:= nil;
    writeln('ingrese un numero decimal: ');
    readln(num);
  end;
end;

  
var
  l: lista;
begin
  l:= nil;
  leer(l);
  imprimir(l);
end.
  
  
  
  

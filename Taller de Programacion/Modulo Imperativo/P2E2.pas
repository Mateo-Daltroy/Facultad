program P2E2;


// Inicio Modulos

function cantDigitos (num: integer): integer;
var
	cont: integer;
begin
	cont:= 1;
	while ((num DIV 10) <> 0) do
	begin
		cont:= cont + 1;
		num:= num DIV 10;
	end;
	cantDigitos:= cont;
end;

function primerDigito (num: integer): integer;
begin
	while ((num DIV 10) <> 0) do
	begin
		num:= num DIV 10;
	end;
	primerDigito:= num;
end;

function borrador (var aux2: integer): integer;
var
	i, aux: integer;
begin
	aux:= 1;
	for i:= 1 to (aux2 - 1) do
	begin
		aux:= aux * 10;
	end;
	aux2:= aux2 - 1;
	borrador:= aux;
end;

procedure imprimirNum(num: integer);
var
	i, aux, aux2: integer;
begin
	aux:= cantDigitos(num);
	aux2:= aux;
	for i:= 1 to aux do
	begin
		write(primerDigito(num));
		write(' ');
		num:= num MOD borrador(aux2);
	end;
end;


// Inicio Programa Principal


var
	num: integer;
begin
	write('Introducir un numero: ');
	readln(num);
	while (num <> 0) do
	begin
		imprimirNum(num);
		readln(num);
	end;
end.

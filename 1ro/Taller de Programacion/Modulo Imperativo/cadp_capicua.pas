program capicua;
const
	maxCli = 500;
type
	cliente = record
		dni: integer;
		cod: integer;
		cuota: real;
	end;
	
	cantCli = 1..maxCli;
	vector = array[cantCli] of cliente;
	
	lista = ^nodo;
	nodo = record
		d: integer;
		sig: lista;
	end;

// Inicio modulos


procedure generarLista(dni: integer; var l: lista);
var
	aux: lista;
begin
	l:= nil;					// 1
	while (dni > 0) do			// 7N + 1
	begin
		new(aux);
		aux^.d:= (dni MOD 10);	// 2
		aux^.sig:= l;			// 1
		l:= aux;				// 1
		dni:= dni DIV 10;		// 2
	end;
end;							// 2 + 7N

function checkCapicua(dni: integer; l: lista): boolean;
var
	ok: boolean;
begin
	ok:= true;							// 1
	while ((l <> nil) and (ok)) do 		// 8N + 3
	begin
		if (l^.d <> (dni MOD 10)) then	// 5
			ok:= false
		else
		begin
			l:= l^.sig;					
			dni:= dni DIV 10;			
		end;
	end;
	checkCapicua:= ok;					// 1
end;									// 5 + 8N

function capicuas (v: vector; dimL: integer): integer;
var
	l: lista;
	i, cont: integer;
begin
	cont:= 0;								// 1
	for i:= 1 to dimL do					// 2 + 15N^2 + 12N
	begin
		generarLista(v[i].dni, l);			// 2 + 7N
		if (checkCapicua(v[i].dni, l)) then	// 7 + 8N
			cont:= cont + 1;
	end;
	capicuas:= cont;						// 1
end;										// 2 + 12N + 15N^2


// Inicio Programa Principal
var
	v: vector;
	dimL, cant: integer;
begin
	leerDatos(v, dimL) // se dispone
	cant:= capicuas(v, dimL);
	writeln(cant);
end.	// Total 3 + 12N + 15N^2

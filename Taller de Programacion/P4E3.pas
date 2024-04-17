program P4E3;
const
    maxMaterias = 30;

type
    cantMaterias  = 1..maxMaterias;
<<<<<<< Updated upstream
    notasPosibles = -1..10; // -1 corte
=======
    notasPosibles = 0..10; 
>>>>>>> Stashed changes

    vectorIndiv = array [cantMaterias] of notasPosibles;

    alumno = record
        cod: integer; //cod alumno
        vect: vectorIndiv;
    end;

    arbol = ^nodo;
    nodo = record
        d: alumno; 
        HI: arbol;
        HD: arbol;
    end;

    final = record
        cod: integer; //cod alumno
        nota: notasPosibles; // nota final
    end;

    lista = ^nodo2;
    nodo2 = record
        d: final;
        sig: lista;
    end;

    vectorGen = array [cantMaterias] of lista;
<<<<<<< Updated upstream
    
	recordB = record
		cod: integer;
		prom: real;
	end;
	
    listaB = ^nodoB;
    nodoB = record
		d: recordB;
		sig: listaB;
	end;
=======
>>>>>>> Stashed changes


// Inicio Modulos


procedure leerFinal(var f: final; var aux: cantMaterias);
begin
    write('Introducir nota del final: ');
    readln(f.nota);
    if (f.nota <> -1) then
    begin
        write('Introducir codigo de la materia: ');
        readln(aux);
        write('Introducir codigo del alumno: ');
        readln(f.cod);
    end;
end;

procedure iniVectInd(var v: vectorIndiv);
var
    i: integer;
begin
    for i:= 1 to maxMaterias do
    begin
<<<<<<< Updated upstream
        v[i]:= -1;
=======
        v[i]:= 0;
>>>>>>> Stashed changes
    end;
end;

procedure generarArbol(var a: arbol; f: final; aux: cantMaterias);
begin
    if (a = nil) then
    begin
        new(a);
        a^.HI:= nil;
        a^.HD:= nil;
        a^.d.cod:= f.cod;
        iniVectInd(a^.d.vect);
        a^.d.vect[aux]:= f.nota;
    end
    else if (a^.d.cod = f.cod) then
        a^.d.vect[aux]:= f.nota
    else if (a^.d.cod > f.cod) then
        generarArbol(a^.HI, f, aux)
    else
        generarArbol(a^.HD, f, aux);
end;

procedure agregarAdelante(var l: lista; f: final);
var
    nuevo: lista;
begin
    new(nuevo);
    nuevo^.d:= f;
    nuevo^.sig:= l;
    l:= nuevo;
end;

procedure iniVectGen(var v: vectorGen);
var
    i: integer;
begin
    for i:= 1 to maxMaterias do
    begin
        v[i]:= nil;
    end;
end;

procedure leerDatos(var a: arbol; var v: vectorGen);
var
    f: final;
    aux: cantMaterias;
begin
    a:= nil;
    leerFinal(f, aux);
    iniVectGen(v);
    while (f.nota <> -1) do
    begin
        if (f.nota >= 4) then
            generarArbol(a, f, aux);
        agregarAdelante(v[aux], f);
        leerFinal(f, aux);
    end;
end;

<<<<<<< Updated upstream
function calcularProm(v: vectorIndiv): real;
var
	i, tot, cont: integer;
begin
	tot:= 0;
	cont:= 0;
	for i:= 1 to maxMaterias do
	begin
		if (v[i] <> -1) then
		begin
			tot:= tot + v[i];
			cont:= cont + 1;
		end;
	end;
	calcularProm:= tot / cont;
end;

procedure agregarAdelanteB(var l: listaB; cod: integer; prom: real);
var
	nuevo: listaB;
begin
	new(nuevo);
	nuevo^.d.cod:= cod;
	nuevo^.d.prom:= prom;
	nuevo^.sig:= l;
	l:= nuevo;
end;

procedure generarListaB(a: arbol; cod: integer; var l: listaB);
// listaB ini en nil afuera
=======
procedure generarListaB(a: arbol; cod: integer; var l: listaB);
// listaB ini en nil afuera
// listaB no existe
>>>>>>> Stashed changes
begin
    if (a <> nil) then
    begin
        if (a^.d.cod > cod) then
        begin
<<<<<<< Updated upstream
            agregarAdelanteB(l, a^.d.cod, calcularProm(a^.d.vect));
=======
            agregarAdelanteB(l, a^.d);
>>>>>>> Stashed changes
            generarListaB(a^.HI, cod, l);
        end;
        generarListaB(a^.HD, cod, l);
    end;
end;

<<<<<<< Updated upstream
procedure mostrarLista(l: listaB);
begin
	while (l <> nil) do
	begin
		write('El alumno de codigo ');
		write(l^.d.cod);
		write(' tiene un promedio de ');
		writeln(l^.d.prom);
	end;
	writeln;
end;

=======
>>>>>>> Stashed changes
function cumpleNotas(v: vectorIndiv; n: notasPosibles):boolean;
var
    i: cantMaterias; 
    cont: notasPosibles;
begin
    cont:= 0;
    for i:= 1 to maxMaterias do
    begin
        if (v[i] >= 4) then
            cont:= cont + 1;
    end;
    if (cont = n) then
<<<<<<< Updated upstream
        cumpleNotas:= true
=======
        cumpleNotas:= true;
>>>>>>> Stashed changes
    else
        cumpleNotas:= false;
end;

<<<<<<< Updated upstream
procedure puntoC(a: arbol; var cont: integer; min, max: integer; nota: integer);
// ini cont en 0;
begin
    if (a <> nil) then;
=======
procedure puntoC(a: arbol; var cont: integer; min, max: integer; nota: notasPosibles);
// ini cont en 0;
begin
    if (a <> nil);
>>>>>>> Stashed changes
    begin
        if (a^.d.cod < min) then
            puntoC(a^.HD, cont, min, max, nota)
        else if (a^.d.cod > max) then
            puntoC(a^.HI, cont, min, max, nota)
        else
        begin
            if (cumpleNotas(a^.d.vect, nota)) then
                cont:= cont + 1;
            puntoC(a^.HI, cont, min, max, nota);
            puntoC(a^.HD, cont, min, max, nota);
        end;
    end;
end;


// Inicio Programa principal

var
<<<<<<< Updated upstream
	a: arbol;
	v: vectorGen;
	cod, min, max, nota: integer;
	l: listaB;
begin
    leerDatos(a, v);
    write('Introducir codigo minimo: ');
    readln(cod);
    l:= nil;
    generarListaB(a, cod, l);
    mostrarLista(l);
    write('Introducir codigo minimo: ');
    readln(min);
    write('Introducir codigo maximo: ');
    readln(max);
    write('Introducir finales a aprobar: ');
    readln(nota);
    cod:= 0;
	puntoC(a, cod, min, max, nota);
	write(cod);
	writeln(' alumnnos cumplen con las condiciones.');
end.
=======
begin
    
end.
>>>>>>> Stashed changes

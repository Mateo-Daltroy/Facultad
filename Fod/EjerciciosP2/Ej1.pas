program Ej1;
uses SysUtils;
type
    comision = record
	codEmp: integer;
	nom: string;
	monto: real;
    end;

    archCom = File of comision;

// Inicio Modulos


procedure testCrearLargo(var arch: archCom);
var
    com: comision;
    op: integer;
begin
    write('1 para reescribir el archivo Largo, cualquier otro numero para usar el anterior: ');
    readln(op);
    if (op = 1) then
    begin
	rewrite(arch);
	write('ing codEmp: '); readln(com.codEmp);
	while (com.codEmp <> 0) do
	begin
	    write('ing nom: '); readln(com.nom);
	    write('ing monto: '); readln(com.monto);
	    write(arch, com);
	    write('ing codEmp: '); readln(com.codEmp);
	end;
    end;
end;

procedure imprimirComision(com: comision);
begin
    write('{' + IntToStr(com.codEmp) + ', monto: ');
    write(com.monto);
    writeln('}');
end;

procedure testImprimir(var arch: archCom);
var
    com: comision;
begin
    reset(arch);
    while (not EOF(arch)) do
    begin
	read(arch, com);
	imprimirComision(com);
    end;
    writeln();
end;

procedure escribirComision(var arch: archCom; cod: integer; monto: real);
var
    c: comision;
begin
    c.codEmp:= cod;
    c.monto:= monto;
    c.nom:= 'simplificado'; 
    // Deberian ser dos tipos de archivos distintos con distintos tipos de registros
    // pero es mas facil testear si son iguales
    write(arch, c);
end;

procedure comprimir(var archLarg: archCom; var archComp: archCom);
var
    codAct: integer;
    montoAct: real;
    com: comision;
begin
    rewrite(archComp);
    reset(archLarg);
    codAct:= 0;
    while (not EOF(archLarg)) do
    begin
	read(archLarg, com);
	if (com.codEmp <> codAct) then
	begin
	    if (codAct <> 0) then
		escribirComision(archComp, codAct, montoAct);
	    codAct:= com.codEmp;
	    montoAct:= com.monto;
	end
	else begin
	    montoAct:= montoAct + com.monto;
	end;
    end;
    escribirComision(archComp, codAct, montoAct);
end;


// Inicio Programa Principal

var
    archLarg, archComp: archCom;
begin
    assign(archLarg, 'largo');
    assign(archComp, 'comprimido');
    testCrearLargo(archLarg);
    writeln('Archivo Largo');
    testImprimir(archLarg);
    comprimir(archLarg, archComp);
    writeln('Archivo Comprimido');
    testImprimir(archComp);
    close(archLarg); close(archComp);
end.

program L8;
const
    valorAlto = 9999;
    maxDet = 16;
type
    cantDet = 1..maxDet;

    regMae = record
	cod: integer;
	nom: string;
	hab: integer;
	cons: integer;
    end;

    archMae = File of regMae;

    regDet = record
	cod: integer;
	cons: integer;
    end;

    archDet = File of regDet;

    vector = array [cantDet] of archDet;
    vectorDatos = array [cantDet] of regDet;

// Inicio Modulos

procedure leerMae(var mae: archMae; var dato: regMae);
begin
    if(EOF(mae)) then
	dato.cod:= valorAlto
    else
	read(mae, dato);
end;

procedure leerDet(var det: archDet; var dato: regDet);
begin
    if(EOF(det)) then
	dato.cod:= valorAlto
    else
	read(det, dato);
end;

procedure leerUnoVect(var v: vector; var vDat: vectorDatos);
var
    i: integer;
begin
    for i:= 1 to maxDet do
    begin
	leerDet(v[i], vDat[i]);
    end;
end;

procedure minimoVect(var v: vector; var vDat: vectorDatos; var provAct: regDet);
var
    i, codMin, posMin: integer;
begin
    posMin:= valorAlto;
    codMin:= valorAlto;
    for i:= 1 to maxDet do
    begin
	if (vDat[i].cod < codMin) then
	begin
	    codMin:= vDat[i].cod;
	    posMin:= i;
	end;
    end;
    provAct:= vDat[posMin];
    leerDet(v[posMin], vDat[posMin]);
end;

procedure sumarProvincia(var v: vector; var vDat: vectorDatos; var provAct, provProx: regDet);
var
    provRet: regDet;
begin
    provRet.cod:= provAct.cod;
    provRet.cons:= 0;
    while (provAct.cod = provRet.cod) do
    begin
	provRet.cons:= provRet.cons + provAct.cons;
	minimoVect(v, vDat, provAct);
    end;
    provProx:= provAct;
    provAct:= provRet;
end;

procedure resetVect(var v: vector);
var
    i: integer;
begin
    for i:= 1 to maxDet do
    begin
	reset(v[i]);
    end;
end;

procedure actualizarMaestro(var mae: archMae; var v: vector);
var
    provAct, provProx: regDet;
    provMae: regMae;
    vDat: vectorDatos;
begin
    reset(mae);
    resetVect(v);
    leerMae(mae, provMae);
    leerUnoVect(v, vDat);
    minimoVect(v, vDat, provAct);
    while (provAct.cod <> valorAlto) do
    begin
	while (provMae.cod <> provAct.cod) do
	begin
	    if (provMae.cons > 10000) then
	    begin
		writeln('La provincia ', provMae.nom, ' consumio mas de 10.000 kilos historicamente, con un promedio de ', 
		    (provMae.cons / provMae.hab), ' por habitante');
	    end;
	    leerMae(mae, provMae);
	end;
	sumarProvincia(v, vDat, provAct, provProx);
	provMae.cons:= provMae.cons + provAct.cons;
	seek(mae, filePos(mae) - 1);
	write(mae, provMae);
	provAct:= provProx;
    end;
end;

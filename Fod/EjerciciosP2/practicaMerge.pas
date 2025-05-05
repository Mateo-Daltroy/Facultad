program practMerge;
const
    valorAlto = 9999;
type
    venta = record
	cod: integer;
	precio: real;
	cant: integer;
	nombre: string;
	local: string;
    end;

    archVenta = File of venta;

// Inicio Modulos


procedure leer(var arch: archVenta; var dato: venta);
begin
    if (not EOF(arch)) then
	read(arch, dato)
    else
	dato.cod:= valorAlto;
end;

procedure minimo(var det1, det2, det3: archVenta; var reg1, reg2, reg3, min: venta);
begin
    if ((reg1.cod <= reg2.cod) and (reg1.cod <= reg3.cod)) then
    begin
	min:= reg1;
	leer(det1, reg1);
    end
    else if ((reg2.cod <= reg1.cod) and (reg2.cod <= reg3.cod)) then
    begin
	min:= reg2;
	leer(det2, reg2);
    end
    else
    begin
	min:= reg3;
	leer(det3, reg3);
    end;
end;

procedure procesarDatos(var mae, det1, det2, det3: archVenta; var reg1, reg2, reg3: venta);
var
    min: venta;
begin
    rewrite(mae); //Para el ejemplo simplificado, sino usaria reset(mae); para recorrerlo y actualizar
    minimo(det1, det2, det3, reg1, reg2, reg3, min);
    while (min.cod <> valorAlto) do
    begin
	write(mae, min); //O cualquier operacion que se desee hacer en base a los prerequisitos
	minimo(det1, det2, det3, reg1, reg2, reg3, min);
    end;
end;


// Inicio Programa Principal
var
    mae, det1, det2, det3: archVenta;
    reg1, reg2, reg3: venta;
begin
    assign(mae, 'unarchivo'); assign(det3, 'unarchivo'); assign(det2, 'unarchivo'); assign(det1, 'unarchivo'); 
    leer(det1, reg1);
    leer(det2, reg2);
    leer(det3, reg3);
    procesarDatos(mae, det1, det2, det3, reg1, reg2, reg3);
    close(mae);
    close(det1);
    close(det2);
    close(det3);
end.

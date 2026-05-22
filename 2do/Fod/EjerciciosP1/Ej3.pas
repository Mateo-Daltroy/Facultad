program Ej3;
uses SysUtils;
type
	empleado = record
		cod: integer;
		nom: string;
		ap: string;
		dni: string;
		edad:integer;
	end;
	
	archivoEmp = File of empleado;

// Inicio modulos


procedure listarEmpleado(emp: empleado);
begin
	write('{Empleado: '); write(emp.nom); write(' '); write(emp.ap); write(', edad: ');
	write(emp.edad); write(', codigo: '); write(emp.cod); write(', dni: '); write(emp.dni); writeln('}');
end;

procedure leerEmpleado(var emp: empleado);
begin
	write('introducir apellido: ');
	readln(emp.ap);
	if (emp.ap <> 'fin') then
	begin  
		write('introducir codigo: ');
		readln(emp.cod);
		write('introducir edad: ');
		readln(emp.edad);
		write('introducir dni: ');
		readln(emp.dni);
		write('introducir nombre: ');
		readln(emp.nom);
	end;
	writeln;
end;

procedure nuevoArchivo(var arch: archivoEmp);
var
	emp: empleado;
begin
	rewrite(arch);
	leerEmpleado(emp);
	while (emp.ap <> 'fin') do
	begin
		write(arch, emp);
		leerEmpleado(emp);
	end;
end;

procedure agregarArch(var arch: archivoEmp);
var	
	empIns, empAct: empleado;
	presente: boolean;
begin
	leerEmpleado(empIns);
	while (empIns.ap <> 'fin') do
	begin
		reset(arch);
		presente:= false;
		while ((not EOF(arch)) and (not presente)) do
		begin
			read(arch, empAct);
			if (empIns.cod = empAct.cod) then
				presente:= true;
		end;
		if (EOF(arch)) then
		begin
			seek(arch, (fileSize(arch) - 1));
			write(arch, empIns);
		end
		else
			writeln('El empleado ya se encuentra registrado.');
		leerEmpleado(empIns);
	end;
end;

procedure cumplirAno(var arch: archivoEmp);
var
	codAux: integer;
	buscando: boolean;
	emp: empleado;
begin
	write('Introducir codigo del empleado que cumplio años: ');
	readln(codAux);
	buscando:= true;
	while (buscando and (not EOF(arch))) do
	begin
		read(arch, emp);
		if (emp.cod = codAux) then
			buscando:= false;
	end;
	if (buscando) then
		writeln('no se encontro un usuario bajo ese codigo')
	else
	begin
		seek(arch, (filePos(arch) - 1));
		emp.edad:= emp.edad + 1;
		write(arch, emp);
	end;
end;

// Procedimiento de seleccion en carga
procedure cargarEmpleados(var arch: archivoEmp);
var
	opcion: integer;
begin
	writeln('Desea agregar/modificar un empleado o sobreescribir el archivo?');
	writeln('1 para agregar un empleado, 2 para cumplir años y 3 para sobreescribir');
	readln(opcion);
	if (opcion = 1) then
		agregarArch(arch)
	else if (opcion = 2) then
		cumplirAno(arch)
	else if (opcion = 3) then
		nuevoArchivo(arch);
	close(arch);
end;

procedure buscarNomAp(var arch: archivoEmp);
var
	busq: string;
	emp: empleado;
begin
	write('Introducir nombre o apellido a buscar: ');
	readln(busq);
	while (not EOF(arch)) do
	begin
		read(arch, emp);
		if ((emp.nom = busq) or (emp.ap = busq)) then
			listarEmpleado(emp);
	end;
end;

procedure verTodosEmp(var arch: archivoEmp);
var
	emp: empleado;
begin
	while (not EOF(arch)) do
	begin
		read(arch, emp);
		listarEmpleado(emp);
	end;
end;

procedure proximosJubilados(var arch: archivoEmp);
var
	emp: empleado;
begin
	while (not EOF(arch)) do
	begin
		read(arch, emp);
		if (emp.edad > 70) then
			listarEmpleado(emp);
	end;
end;

// Procedimiento de seleccion en lectura
procedure leerDeArchivo(var arch: archivoEmp);
var
	opcion: integer;
begin
	writeln('Desea buscar por nombre/apellido, todos los empleados o los proximos a jubilarse?');
	writeln('1 para buscar por nombre/apellido, 2 para ver todos los empleados y 3 para los proximos jubilados');
	readln(opcion);
	if (opcion = 1) then
		buscarNomAp(arch)
	else if (opcion = 2) then
		verTodosEmp(arch)
	else if (opcion = 3) then
		proximosJubilados(arch);
end;

procedure ingresarEmpleadoTexto(var txt: Text; emp: empleado);
var
	str: string;
begin
	str:= '{Empleado: ' + emp.nom + ' ' + emp.ap + ', edad: ' + IntToStr(emp.edad) 
		+ ', codigo: ' + IntToStr(emp.cod) + ', dni: ' + emp.dni + '}';
	write(txt, str);
	writeln(txt);
end;

procedure actualizarEmpleados(var arch: archivoEmp);
var
	registro: Text;	
	emp: empleado;
begin
	assign(registro, 'todos_empleados.txt');
	rewrite(registro);
	while (not EOF(arch)) do
	begin
		read(arch, emp);
		ingresarEmpleadoTexto(registro, emp);
	end;
	writeln('Informacion actualizada en el archivo "todos_empleados.txt"');
	writeln;
	close(registro);
end;

procedure actualizarNoDNI(var arch: archivoEmp);
var
	registro: Text;
	emp: empleado;
begin
	assign(registro, 'faltaDNIEmpleado.txt');
	rewrite(registro);
	while (not EOF(arch)) do
	begin
		read(arch, emp);
		if (emp.dni = '00') then
			ingresarEmpleadoTexto(registro, emp);
	end;
	writeln('Informacion actualizada en el archivo "faltaDNIEmpleado.txt"');
	writeln;
	close(registro);
end;

// Procedimiento de seleccion en exportacion
procedure exportarDatos(var arch: archivoEmp);
var
	opcion: integer;
begin
	writeln('Desea actualizar el archivo de todos los empleados o el de los empleados sin DNI?');
	writeln('1 para actualizar el archivo de todos los empleados y 2 para los empleados sin DNI');
	readln(opcion);
	if (opcion = 1) then
		actualizarEmpleados(arch)
	else if (opcion = 2) then
		actualizarNoDNI(arch);
end;


// Fin modulos, inicio Programa Principal

var
	arch: archivoEmp;
	opcion: integer;
	nombreArch: string;
begin
	opcion:= -1;
	while (opcion <> 0) do
	begin
		writeln('bienvenido al sistema de carga de empleados, desea cargar o buscar empleados?');
		writeln('marque 0 para cerrar la aplicacion, 1 para cargar/modificar empleados, 2 para buscar y 3 para exportar datos');
		readln(opcion);
		if (opcion <> 0) then
		begin
			writeln;
			write('Introducir el nombre del archivo sobre el que trabajar: ');
			readln(nombreArch);
			writeln;
			assign(arch, nombreArch);
			reset(arch);
			if (opcion = 1) then
				cargarEmpleados(arch)
			else if (opcion = 2) then
				leerDeArchivo(arch)
			else if (opcion = 3) then
				exportarDatos(arch);
		end;
	end;
end.

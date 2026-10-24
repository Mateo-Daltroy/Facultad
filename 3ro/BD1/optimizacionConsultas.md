
# Optimizacion de Consultas SQL

## Seleccion

### Orden de pasos
1. Cache
  > Se busca en la cache, si se tiene el dato, fin
2. Parsing
3. Planning
4. Execution
  > Esta es la unica etapa que accede directamente a la DB

### Tipos de Selects
Las propriedades que tenga un select (la forma de su consulta) condiciona las operaciones que debe hacer el motor para optimizar la respuesta. Para simplificar, un select se divide en las siguientes categorias:
  - Simple: No hay uniones ni subconsultas
  - Primary: Select mas externo (donde existen subconsultas)
  - Union
  - Dependent Union: Depende del resultado de otra consulta externa
  - Union Result
  - Subquery
  - Dependent Subquery: Depende de una consulta externa en una subconsulta
  - Derived: Subconsulta en FROM

### Tipos de Joins

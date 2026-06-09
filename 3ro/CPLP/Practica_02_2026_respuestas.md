## Practica 2
1.
  l

2. 
  La sintaxis en un lenguaje es utilizada para definir una estructura que permita al compilador analizar una sentencia a traves de parsing, de esta manera una sintaxis define que sentencias estan escritas de manera correcta y cuales no. 
  Una sintaxis esta compuesta por:
  - Identificadores: Aliases definidos que establecen ligaduras con la memoria
  - Operadores: Conjunto de simbolos que permiten trabajar con los identificadores
  - Palabras Clave/Reservadas: Representan abstracciones y herramientas base del programa que no pueden ser utilizadas para establecer ningun identificador
  - Comentarios: Secciones de codigo que el compilador ignora, existen solo para que el programador pueda leerlas y entender mejor el codigo

3.
  Las reglas lexicograficas y sintacticas son las dos tipos de reglas que existen dentro de la declaracion de la sintaxis de un lenguaje.
  Una regla lexicografica establece como se escribe de manera apropiada una "word", es decir, el componente mas elemental de una sintaxis. En este caso una regla lexicografica determinaria como se escribe el simbolo de distinto, si "!=" o "<>".
  Por el otro lado una regla sintactica establece reglas en cuanto a las sentencias que se conforman por words, de esta manera una regla sintactica puede establecer que al final de cada linea va un ";" para marcar su final

4.
  Una palabra reservada es una word que conlleva un significado/abstraccion sobre la implementacion base del lenguaje, por la cual no pueden ser utilizadas como identificadores en el resto del programa por mas de que las reglas lexicograficas lo permitan. Un ejemplo puede ser la palabra "begin" en Pascal, que se encuentra reservada ya que cumple el rol de demarcar las diferentes estructuras de control (de la misma manera que el "{" en los lenguajes de estilo C).
  Las palabras reservadas son equivalentes a los simbolos terminales dentro de la definicion de una gramatica.

5.
  a. <D-,> indicar que representa cada punto en G = (N, T, S, P)
  b. la gramatica es ambigua porque existen dos definiciones de <numero_entero> recursivas, una por izquierda y una por derecha. Para corregirla bastaria con eliminar a cualquiera de las dos.

6.
  G = (N, T, S, P)
  N = {<char> <palabra>}
  T = {a, b, ..., z}
  S = <palabra>
  P = {
    <char> ::= a | b | ... | z
    <palabra> ::= <char> | <char><palabra>
  }

7.
  G = (n, t, s, p)
  N = {<digito>, <real>}
  T = {0, 1, 2, ..., 9}
  S = <real>
  P = {
    <digito> ::= 1 | 2 | ... | 9
    <real> ::= {<digito>}+\["."{<digito>}*]
  }

  BNF precisa multiples definiciones para evitar una recursion ambigua->
  G = (n, t, s, p)
  N = {<digito>, <real>, <numero>, <decimal>}
  T = {0, 1, 2, ..., 9}
  S = <real>
  P = {
    <digito> ::= 1 | 2 | ... | 9
    <numero> ::= <digito> | <digito><numero>
    <decimal> ::= <digito> | <digito><decimal>
    <real> ::= <numero> | <numero>.<decimal>
  }

  
10.
  a.
G = (n, t, s, p)
N = {<digito>, <operador>, <char>, <variable>, <expresion>}
T = {0, 1, 2, ..., 9, a, b, ..., Z, -, +, /, \*}
S = <expresion>
P = {
  <digito> ::= 1 | 2 | ... | 9
  <char> ::= a | b | ... | Z
  <operador> ::=  / | - | + | \*
  <numero> ::= <digito> | <digito><numero>
  <variable> ::= <char>{<char>|<digito>}\*
  <expresion> ::= \[<variable>|<numero>]{<operador><expresion>}\*
}

  b.
G = (n, t, s, p)
N = {<digito>, <numero>, <operador>, <char>, <variable>, <expresion>}
T = {0, 1, 2, ..., 9, a, b, ..., Z, -, +, /, \*}
S = <expresion>
P = {
  <digito> ::= 1 | 2 | ... | 9
  <char> ::= a | b | ... | Z
  <numero> ::= <digito> | <digito><numero>
  <variable> ::= <char>{<char>|<digito>}\*
  <operadorPrio> ::=  / | \*
  <operadorNoPrio> ::= - | +
  <expresion> ::= <termino>{<operadorNoPrio><termino>}*
  <termino> ::=  <factor>{<operadorPrio><factor>}*
  <factor> ::= <variable>|<numero>
}

  c.
  La manera de agregar prioridad dentro de la expresion es a traves de dividir las expresiones prioritarias de las no prioritarias para poder anidar las prioritarias dentro de las no prioritarias. Esto fuerza que para llegar a resolver una expresion no prioritaria, las expresiones prioritarias internas a ella deben ser resueltas previamente.

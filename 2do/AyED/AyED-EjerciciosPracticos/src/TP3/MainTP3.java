package TP3;

import Testers.*;
import java.util.ArrayList;

public class MainTP3 {
    public static void main(String[] args) {
    }
}

/* Main Ej3
        GeneralTree<Integer> arbol = GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerInteger());
        System.out.println(arbol.nivel(6));
        System.out.println(arbol.altura());
        System.out.println(arbol.ancho());
 */

/* Main Ej4
        GeneralTree<AreaEmpresa> arbol = GeneradorArbGen.generarDesordenado(AreaEmpresa.leerArea());
        System.out.println(AnalizadorArbol.devolverMaximoPromedio(arbol));
 */

/* Main Ej5
        GeneralTree<Integer> arbol = GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerInteger());
        System.out.println(arbol.esAncestro(2, 6));
        System.out.println(arbol.esAncestro(6, 2));
 */

/* Main Ej6
        GeneralTree<Character> arbol = GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerCharacter());
        RedDeAguaPotable red = new RedDeAguaPotable(arbol);
        System.out.println(red.minimoCaudalRecursivo(1000));
 */

/* Main Ej7
        GeneralTree<Integer> arbol = GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerInteger());
        Caminos cam = new Caminos(arbol);
        ArrayList<Integer> lista = (ArrayList<Integer>) cam.caminoAHojaMasLejana();
        for (Integer i : lista) {
            System.out.print(i + ", ");
        }
        System.out.println();
 */

/* Main Ej8
        GeneralTree<Integer> arbol = GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerInteger());
        Navidad nav = new Navidad(arbol);
        System.out.println(nav.esAbetoNavidenio());
 */

/* Main Ej9
        System.out.println(Ej9Parcial.esDeSeleccion(GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerInteger())));
 */

/* Main Ej10
        ArrayList<Integer> res = (ArrayList) Ej10Parcial.resolver(GeneradorArbGen.generarDesordenado(LectorPrimitivo.leerInteger()));
        System.out.println(res.size());
 */
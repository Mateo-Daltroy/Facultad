/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tema3;

/**
 *
 * @author mateo
 */
import PaqueteLectura.GeneradorAleatorio;
import PaqueteLectura.Lector;

public class Ej03 {
    
    public static void main(String [] args){
        Estante e = new Estante();
        Libro l;
        Autor a;
        String nom; String titulo; String editorial; String  isbn; int cont = 0;
        GeneradorAleatorio.iniciar();
        do {
            System.out.print("Ingresar titulo del libro: ");
            titulo = Lector.leerString();
            System.out.print("Ingresar nombre del autor: ");
            nom = Lector.leerString();
            a = new Autor(nom);
            System.out.print("Ingresar editorial: ");
            editorial = Lector.leerString();
            System.out.print("Ingresar isbn: ");
            isbn = Lector.leerString();
            l = new Libro(titulo, editorial, a, isbn);
            e.agregarLibro(l);
            cont++;
        } while (!(titulo.equals("Mujercitas")) && (cont < 20));
        l = e.devolverLibro("Mujercitas");
        System.out.println(l.getPrimerAutor().getNom());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repaso5;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;
import PaqueteLectura.GeneradorAleatorio;

public class Implementacion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Director D;
        Encargado Enc;
        Empresa Emp;
        int dimF; String nom; String direccion;
        String nom2; int dni; double sueldoBasico;
        int ingreso; double viaticos; int empCarg;
        GeneradorAleatorio.iniciar();
        
        System.out.print("Introducir nombre del director: ");
        nom2 = Lector.leerString();
        System.out.print("Introducir dni del director: ");
        dni = Lector.leerInt();
        System.out.print("Introducir sueldo basico del director: ");
        sueldoBasico = Lector.leerDouble();
        System.out.print("Introducir ingreso del director: ");
        ingreso = Lector.leerInt();
        System.out.print("Introducir viaticos del director: ");
        viaticos = Lector.leerDouble();
        D = new Director(viaticos, dni, ingreso, sueldoBasico, nom2);
        
        System.out.print("Ingresar nombre de la empresa: ");
        nom = Lector.leerString();
        System.out.print("Introducir direccion de la empresa: ");
        direccion = Lector.leerString();
        System.out.print("Introducir encargados de la empresa: ");
        dimF = Lector.leerInt();
        Emp = new Empresa(nom, direccion, D, dimF);
        
        for (int i = 0; i < 2; i++){
            //int cantEmp, int dni, int ingreso, double sueldoB, String nom
            System.out.print("Introducir cantidad de empleados del Encargado: ");
            empCarg = Lector.leerInt();
            System.out.print("Introducir dni del Encargado: ");
            dni = Lector.leerInt();
            System.out.print("Introducir ingreso del Encargado: ");
            ingreso = Lector.leerInt();
            System.out.print("Introducir sueldo basico del Encargado: ");
            sueldoBasico = Lector.leerDouble();
            System.out.print("Introducir nombre del Encargado: ");
            nom = Lector.leerString();
            Enc = new Encargado(empCarg , dni, ingreso, sueldoBasico, nom);
            Emp.asignarEncargado(Enc, (i+2));
        }
        
        System.out.print(Emp.toString());
    }
    
}

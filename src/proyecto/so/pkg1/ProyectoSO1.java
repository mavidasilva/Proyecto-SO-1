/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto.so.pkg1;

import EDD.Lista;
import EDD.Nodo;
import Funciones.JSONHandler;
import Interfaces.VistaSimulacion;

/**
 *
 * @author mariavictoriadasilvanunez
 */
public class ProyectoSO1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      
        String filepath = "procesos.json";
        Lista listos = JSONHandler.readProcesosFromJson(filepath);
        if (listos == null) listos = new Lista();

        String filepath1 = "numbers.json";
        int[] preferencias = JSONHandler.readFromJson(filepath1);
        int tiempo   = (preferencias != null && preferencias.length > 0) ? preferencias[0] : 1000; // ms
        int politica = (preferencias != null && preferencias.length > 1) ? preferencias[1] : 0;    // FIFO

        Lista todos = new Lista();
        Nodo pw = listos.getpFirst();
        while (pw != null) {
            todos.InsertarFinal(pw.getDato());
            pw = pw.getPnext();
        }
        new VistaSimulacion(tiempo, politica, listos, todos);

    
    }
    
}

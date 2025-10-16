/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author mariavictoriadasilvanunez
 */
public class Lista {
     private Nodo pFirst;
    private int size;
    
    //Constructor de la clase Lista
    public Lista() {
        this.pFirst = null;
        this.size = 0; 
    }

    public Nodo getpFirst() {
        return pFirst;
    }

    public void setpFirst(Nodo pFirst) {
        this.pFirst = pFirst;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    
    public boolean EsVacio(){
        return this.pFirst == null;
    }
}

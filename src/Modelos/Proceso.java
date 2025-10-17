/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

/**
 *
 * @author mariavictoriadasilvanunez
 */
public class Proceso {
    private int id;
    private String nombre;
    private String tipo;
    private int instrucciones;
    private int ciclosParaExcepcion;
    private int ciclosParaSatisfacerExcepcion;
    private int prioridad;
    private String estado;
    private int pc;
    private int mar;
    private int tiempoEspera;

    public Proceso() {
        this.estado = "Listo";
        this.pc = 1;
        this.mar = 0;
        this.tiempoEspera = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(int instrucciones) {
        this.instrucciones = instrucciones;
    }

    public int getCiclosParaExcepcion() {
        return ciclosParaExcepcion;
    }

    public void setCiclosParaExcepcion(int ciclosParaExcepcion) {
        this.ciclosParaExcepcion = ciclosParaExcepcion;
    }

    public int getCiclosParaSatisfacerExcepcion() {
        return ciclosParaSatisfacerExcepcion;
    }

    public void setCiclosParaSatisfacerExcepcion(int ciclosParaSatisfacerExcepcion) {
        this.ciclosParaSatisfacerExcepcion = ciclosParaSatisfacerExcepcion;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getMar() {
        return mar;
    }

    public void setMar(int mar) {
        this.mar = mar;
    }

    public int getTiempoEspera() {
        return tiempoEspera;
    }

    public void setTiempoEspera(int tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }
}

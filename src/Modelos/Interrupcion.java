/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import EDD.Lista;
import Funciones.ControladorSimulacion;
import static java.lang.Thread.sleep;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Mafe
 */
public class Interrupcion extends Thread {

    private int originCPU;
    private int exceptionCycle;
    private ControladorSimulacion controlador;
    private int processId;
    private Lista interruptionList; // EDD.Lista
    private Semaphore mutex;

    public Interrupcion(int originCPU, int exceptionCycle, ControladorSimulacion controlador, int processId, Lista interruptionList, Semaphore mutex) {
        this.controlador = controlador;
        this.originCPU = originCPU;
        this.exceptionCycle = exceptionCycle;
        this.processId = processId;
        this.interruptionList = interruptionList;
        this.mutex = mutex;
    }

    public int getProcessId() {
        return processId;
    }

    @Override
    public void run() {
        for (int i = 0; i <= this.exceptionCycle; i++) {
            try {
                sleep(controlador.getTiempo());
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(Interrupcion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        try {
            mutex.acquire();
        } catch (InterruptedException ex) {
            System.out.println("ERROR");
        }
        this.interruptionList.InsertarFinal(this);
        mutex.release();
    }
}

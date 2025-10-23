/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import Funciones.ControladorSimulacion;
import static java.lang.Thread.sleep;
import java.util.concurrent.Semaphore;

/**
 *
 * @author mariavictoriadasilvanunez
 */
public class Reloj extends Thread {

    private Semaphore mutex;
    private ControladorSimulacion controlador;
    private Planificador planificador;
    private int ciclo;
    private volatile boolean running = true;

    public Reloj(Semaphore mutex, Planificador dispatcher, ControladorSimulacion controlador) {
        this.mutex = mutex;
        this.ciclo = 0;
        this.planificador = dispatcher;
        this.controlador = controlador;
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                sleep(controlador.getTiempo());
                if (!running) {
                    break;
                }
                mutex.acquire();
            } catch (InterruptedException ex) {
                if (!running) {
                    break;
                }
            }
            this.planificador.updateWaitingTime();

            // LÍMITES de "memoria" simulada
            int MAX_READY = 5;
            int MAX_BLOCKED = 3;  

            // 1) Sobrecarga => suspende exceso de Ready y de Blocked
            this.planificador.verificarSobrecargaMemoria(MAX_READY, MAX_BLOCKED);

            // 2) Si hay espacio libre en Ready => trae procesos desde Suspendido-Listo
            this.planificador.promoverSuspendidosSiHayEspacio(MAX_READY);

            mutex.release();
            ciclo++;
            controlador.actulizarCiclo(ciclo);
        }
    }
}

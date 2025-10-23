/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import EDD.Lista;
import Funciones.ControladorSimulacion;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mariavictoriadasilvanunez
 */
public class CPU extends Thread {

    private int quantum;
    private int memoryAddressRegister;
    private int programCounter;
    private Lista interruptionsList;
    private Planificador planificador;
    private Proceso currentProcess;
    private int id;
    private Semaphore mutexInterruciones;
    private Semaphore mutexCPUs;
    private ControladorSimulacion controlador;
    private volatile boolean running = true;

    public CPU(ControladorSimulacion controlador, Planificador planificador, int id, Semaphore mutexCPUs) {
        this.controlador = controlador;
        this.planificador = planificador;
        this.id = id;
        this.mutexCPUs = mutexCPUs;
        this.mutexInterruciones = new Semaphore(1);
        this.interruptionsList = new Lista();
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }

    private boolean sleepQuiet(long ms) {
        try {
            Thread.sleep(ms);
            return true;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    @Override
    public void run() {
        if (!running) {
            return;
        }

        // Intenta obtener el primer proceso; si nos detienen, salimos
        this.obtenerProceso();
        if (!running || currentProcess == null) {
            return;
        }

        while (running) {

            // 1) Atender interrupciones pendientes
            if (!this.interruptionsList.isEmpty()) {
                Interrupcion exception = (Interrupcion) interruptionsList.getpFirst().getDato();
                interruptionsList.eliminarInicio();
                this.interruptHandler(exception);
                continue;
            }

            // 2) RR: quantum agotado y hay listos -> replanificar
            if (planificador.getSelectedAlgorithm() == 1
                    && this.quantum <= 0
                    && !planificador.getReadyList().isEmpty()) {

                this.usarPlanificador("Listo");
                this.obtenerProceso();
                if (!running || currentProcess == null) {
                    break;
                }
                continue;
            }

            // 3) SRT: preempción
            if (planificador.getSelectedAlgorithm() == 3
                    && this.checkSRT()
                    && !planificador.getReadyList().isEmpty()) {

                this.controlador.setCPUText(id, "Planificador");
                for (int i = 0; i < 4 && running; i++) {
                    if (!sleepQuiet(controlador.getTiempo())) {
                        return; // salir limpio si nos interrumpen
                    }
                }
                this.actulizarCPUvista();
                continue;
            }

            // 4) ¿Terminó el proceso actual?
            if (this.currentProcess.getInstrucciones() <= this.memoryAddressRegister) {
                this.usarPlanificador("Salida");
                this.obtenerProceso();
                if (!running || currentProcess == null) {
                    break;
                }
                continue;
            }

            // 5) Ejecutar 1 “tick”
            if (!sleepQuiet(controlador.getTiempo())) {
                return; // salir silencioso
            }
            this.actulizarCPUvista();
            quantum--;

            // 6) ¿Interrupción de E/S?
            if ("I/O Bound".equals(this.currentProcess.getTipo())
                    && this.isInterruption(memoryAddressRegister)) {

                this.usarPlanificador("Bloqueado");
                this.obtenerProceso();
                if (!running || currentProcess == null) {
                    break;
                }

            } else {
                programCounter++;
                this.memoryAddressRegister++;
                this.actulizarCPUvista();
            }
        }
    }

    public boolean isInterruption(int mar) {
        int cpe = currentProcess.getCiclosParaExcepcion();
        if (cpe > 0 && mar % cpe == 0) {
            Interrupcion exception = new Interrupcion(id, currentProcess.getCiclosParaSatisfacerExcepcion(), this.controlador, this.currentProcess.getId(), this.interruptionsList, this.mutexInterruciones);
            exception.start();
            return true;
        }
        return false;
    }

    private void interruptHandler(Interrupcion exception) {
        try {
            mutexCPUs.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Interrupcion.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.planificador.onIOComplete(exception.getProcessId());

        mutexCPUs.release();
    }

    private void usarPlanificador(String state) {
        try {
            mutexCPUs.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (quantum != 5) {
            this.planificador.updatePCB(currentProcess, programCounter, memoryAddressRegister, state);
        } else {
            this.planificador.updatePCB(currentProcess, state);
        }
        mutexCPUs.release();
    }

    private boolean checkSRT() {
        try {
            mutexCPUs.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Interrupcion.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean output = this.planificador.ifSRT(currentProcess);
        if (output) {
            if (quantum != 5) {
                this.planificador.updatePCB(currentProcess, programCounter, memoryAddressRegister, "Listo");
            } else {
                this.planificador.updatePCB(currentProcess, "Listo");
            }
            this.currentProcess = this.planificador.getProcess();
            quantum = 5;
            programCounter = currentProcess.getPc() + 1;
            memoryAddressRegister = currentProcess.getPc();
        }
        mutexCPUs.release();
        return output;
    }

    private void actulizarCPUvista() {
        String display = "Id: " + currentProcess.getId()
                + "\nNombre: " + currentProcess.getNombre()
                + "\nEstado: " + currentProcess.getEstado()
                + "\nPC: " + programCounter
                + "\nMAR: " + this.memoryAddressRegister;
        this.controlador.setCPUText(id, display);
    }

    private void obtenerProceso() {
        currentProcess = null;

        while (running && currentProcess == null) {
            // Simula tiempo de planificación
            this.controlador.setCPUText(id, "Planificador");
            for (int i = 0; i < 4 && running; i++) {
                if (!sleepQuiet(controlador.getTiempo())) {
                    return; // salir sin log
                }
            }

            // Intentar tomar proceso del planificador
            try {
                mutexCPUs.acquire();
            } catch (InterruptedException ie) {
                if (!running) {
                    return;
                }
            }
            this.currentProcess = this.planificador.getProcess();
            mutexCPUs.release();

            if (currentProcess != null || !running) {
                break;
            }

            // Tiempo ocioso del SO
            this.controlador.setCPUText(id, "System 32");
            if (!sleepQuiet(controlador.getTiempo())) {
                return;
            }

            // Atender interrupciones en cola, si las hay
            if (!this.interruptionsList.isEmpty()) {
                Interrupcion exception = (Interrupcion) interruptionsList.getpFirst().getDato();
                interruptionsList.eliminarInicio();
                this.interruptHandler(exception);
            }
        }

        if (!running || currentProcess == null) {
            return;
        }

        // Preparar registros para ejecutar
        quantum = 5;
        programCounter = currentProcess.getPc() + 1;
        memoryAddressRegister = currentProcess.getPc();
        this.actulizarCPUvista();
    }

}

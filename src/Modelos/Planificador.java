/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import EDD.Lista;
import EDD.Nodo;
import Funciones.ControladorSimulacion;

/**
 *
 * @author mariavictoriadasilvanunez
 */
public class Planificador {

    private Lista readyList;
    private Lista blockedList;
    private Lista exitList;
    private Lista allProcessList;
    private ControladorSimulacion controlador;
    public int selectedAlgorithm;

    public Planificador(Lista readyList, Lista blockedList, Lista exitList, Lista allProcess, ControladorSimulacion controlador) {
        this.controlador = controlador;
        this.readyList = readyList;
        this.blockedList = blockedList;
        this.exitList = exitList;
        this.allProcessList = allProcess;
    }

    public Lista getBlockedList() {
        return blockedList;
    }

    public void setBlockedList(Lista blockedList) {
        this.blockedList = blockedList;
    }

    public Lista getExitList() {
        return exitList;
    }

    public void setExitList(Lista exitList) {
        this.exitList = exitList;
    }

    public Lista getAllProcessList() {
        return allProcessList;
    }

    public void setAllProcessList(Lista allProcessList) {
        this.allProcessList = allProcessList;
    }

    public ControladorSimulacion getControlador() {
        return controlador;
    }

    public void setControlador(ControladorSimulacion controlador) {
        this.controlador = controlador;
    }

    public int getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public Lista getReadyList() {
        return readyList;
    }

    public void setSelectedAlgorithm(int selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
    }

    /**
     * Devuelve el siguiente proceso listo y lo remueve de readyList
     */
    public Proceso getProcess() {
        Proceso output = null;

        if (!this.readyList.isEmpty()) {
            if (selectedAlgorithm != controlador.getPolitica()) {
                selectedAlgorithm = controlador.getPolitica();
                sortReadyQueue(selectedAlgorithm);
            }

            // Tomar cabeza y eliminarla
            Nodo head = this.readyList.getpFirst();
            output = (Proceso) head.getDato();
            this.readyList.eliminarInicio();

            // actualizar estado
            output.setEstado("running");
        }

        // actualizar interfaz
        this.updateProcessList();

        if (output == null) {
            System.out.println("process null");
        }
        return output;
    }

    /**
     * Ordenar la cola de procesos antes de la selección *
     */
    private void sortReadyQueue(int schedulingAlgorithm) {
        switch (schedulingAlgorithm) {
            case 0: // FIFO (priorizamos mayor tiempo de espera para mantener fairness visual)
            case 1: // Round Robin (mantener orden/espera)
                readyList = sortByWaitingTime(readyList);
                break;
            case 2: // SPN - menor duración
                readyList = sortByDuration(readyList);
                break;
        }
    }

    private Lista sortByWaitingTime(Lista list) {
        return bubbleSort(list, (p1, p2) -> Integer.compare(((Proceso) p2).getTiempoEspera(), ((Proceso) p1).getTiempoEspera()));
    }

    public boolean ifSRT(Proceso process) {
        if (controlador.getPolitica() == 3) {
            Nodo current = this.readyList.getpFirst();
            while (current != null) {
                Proceso other = (Proceso) current.getDato();
                int remOther = other.getInstrucciones() - other.getMar();
                int remProc = process.getInstrucciones() - process.getMar();
                if (remOther < remProc) {
                    return true;
                }
                current = current.getPnext();
            }
        }
        return false;
    }

    /**
     * Métodos de Ordenamiento (burbuja, intercambiando dato en Nodo) *
     */
    private Lista sortByDuration(Lista list) {
        return bubbleSort(list, (p1, p2) -> Integer.compare(((Proceso) p1).getInstrucciones(), ((Proceso) p2).getInstrucciones()));
    }

    private interface Cmp {

        int compare(Object a, Object b);
    }

    private Lista bubbleSort(Lista list, Cmp cmp) {
        if (list.getSize() <= 1) {
            return list;
        }

        boolean swapped;
        do {
            swapped = false;
            Nodo current = list.getpFirst();
            while (current != null && current.getPnext() != null) {
                Object a = current.getDato();
                Object b = current.getPnext().getDato();
                if (cmp.compare(a, b) > 0) {
                    // swap datos
                    current.setDato(b);
                    current.getPnext().setDato(a);
                    swapped = true;
                }
                current = current.getPnext();
            }
        } while (swapped);

        return list;
    }

    public void updatePCB(Proceso process, int programCounter, int memoryAddressRegister, String state) {
        process.setEstado(state);
        process.setPc(programCounter);
        process.setMar(memoryAddressRegister);
        process.setTiempoEspera(0);

        if ("Bloqueado".equals(state)) {
            this.blockedList.InsertarFinal(process);
        } else if ("Listo".equals(state)) {
            this.readyList.InsertarFinal(process);
        } else {
            this.exitList.InsertarFinal(process);
        }
        this.updateexitList();
        this.updateProcessList();
    }

    public void updatePCB(Proceso process, String state) {
        process.setEstado(state);
        process.setTiempoEspera(0);
        if ("Bloqueado".equals(state)) {
            this.blockedList.InsertarFinal(process);
        } else if ("Listo".equals(state) || "ready".equalsIgnoreCase(state)) {
            this.readyList.InsertarFinal(process);
        } else {
            this.exitList.InsertarFinal(process);
        }
        this.updateexitList();
        this.updateProcessList();
    }

    public void updateWaitingTime() {
        if (selectedAlgorithm != controlador.getPolitica()) {
            selectedAlgorithm = controlador.getPolitica();
            sortReadyQueue(selectedAlgorithm);
            
        }
        Nodo pAux = this.readyList.getpFirst();
        while (pAux != null) {
            Proceso process = (Proceso) pAux.getDato();
            process.setTiempoEspera(process.getTiempoEspera() + 1);
            pAux = pAux.getPnext();
        }
        this.updateProcessList();
    }

    public void updateProcessList() {
        Nodo pAux = allProcessList.getpFirst();
        StringBuilder display = new StringBuilder();
        while (pAux != null) {
            Proceso process = (Proceso) pAux.getDato();
            display.append(this.stringInterfaz(process));
            pAux = pAux.getPnext();
        }
        controlador.setPcbs(display.toString());
    }

    public void updateexitList() {
        Nodo pAux = exitList.getpFirst();
        StringBuilder display = new StringBuilder();
        while (pAux != null) {
            Proceso process = (Proceso) pAux.getDato();
            display.append("\n ----------------------------------\n ")
                    .append("Id: ").append(process.getId())
                    .append("\n Nombre: ").append(process.getNombre());
            pAux = pAux.getPnext();
        }
    }

    public static String stringInterfaz(Proceso currentProcess) {
        return "\n ----------------------------------\n Id: " + currentProcess.getId()
                + "\n Estado: " + currentProcess.getEstado()
                + "\n Nombre: " + currentProcess.getNombre()
                + "\n PC: " + currentProcess.getPc()
                + "\n MAR: " + currentProcess.getMar()
                + "\n Espera: " + currentProcess.getTiempoEspera();
    }
}


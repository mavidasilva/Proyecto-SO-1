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
    private Lista suspendedReadyList;     
    private Lista suspendedBlockedList;   

    private ControladorSimulacion controlador;
    public int selectedAlgorithm;

    public Planificador(
            Lista readyList,
            Lista blockedList,
            Lista exitList,
            Lista allProcess,
            Lista suspendedReadyList,
            Lista suspendedBlockedList,
            ControladorSimulacion controlador
    ) {
        this.controlador = controlador;
        this.readyList = readyList;
        this.blockedList = blockedList;
        this.exitList = exitList;
        this.allProcessList = allProcess;
        this.suspendedReadyList = suspendedReadyList;
        this.suspendedBlockedList = suspendedBlockedList;
    }

    public Lista getReadyList() {
        return readyList;
    }

    public Lista getBlockedList() {
        return blockedList;
    }

    public Lista getExitList() {
        return exitList;
    }

    public Lista getAllProcessList() {
        return allProcessList;
    }

    public int getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public ControladorSimulacion getControlador() {
        return controlador;
    }

    public void setBlockedList(Lista blockedList) {
        this.blockedList = blockedList;
    }

    public void setExitList(Lista exitList) {
        this.exitList = exitList;
    }

    public void setAllProcessList(Lista allProcessList) {
        this.allProcessList = allProcessList;
    }

    public void setControlador(ControladorSimulacion c) {
        this.controlador = c;
    }

    public void setSelectedAlgorithm(int selected) {
        this.selectedAlgorithm = selected;
    }

    /**
     * Devuelve el siguiente proceso listo y lo remueve de readyList.
     */
    public Proceso getProcess() {
        Proceso output = null;

        if (!this.readyList.isEmpty()) {
            // Si cambió la política en la UI, reordenamos antes de elegir
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

        // actualizar interfaz (todas las listas y PCBs)
        this.updateAllQueuesOnUI();

        if (output == null) {
            System.out.println("process null");
        }
        return output;
    }

    private void sortReadyQueue(int schedulingAlgorithm) {
        switch (schedulingAlgorithm) {
            case 0: // FIFO
            case 1: // Round Robin (mantener orden/espera)
                readyList = sortByWaitingTime(readyList);
                break;
            case 2: // SPN - menor duración
                readyList = sortByDuration(readyList);
                break;
            case 3: // SRT - menor tiempo restante
                readyList = sortByRemainingTime(readyList);
                break;
            case 4: // HRRN - mayor Response Ratio
                readyList = sortByHRR(readyList);
                break;
        }
    }

    private Lista sortByWaitingTime(Lista list) {
        return bubbleSort(list, (p1, p2)
                -> Integer.compare(((Proceso) p2).getTiempoEspera(), ((Proceso) p1).getTiempoEspera()));
    }

    private Lista sortByDuration(Lista list) {
        return bubbleSort(list, (p1, p2)
                -> Integer.compare(((Proceso) p1).getInstrucciones(), ((Proceso) p2).getInstrucciones()));
    }

    private Lista sortByRemainingTime(Lista list) {
        return bubbleSort(list, (p1, p2)
                -> Integer.compare(
                        ((Proceso) p1).getInstrucciones() - ((Proceso) p1).getPc(),
                        ((Proceso) p2).getInstrucciones() - ((Proceso) p2).getPc()
                )
        );
    }

    private Lista sortByHRR(Lista list) {
        return bubbleSort(list, (p1, p2)
                -> Double.compare(getHRR((Proceso) p2), getHRR((Proceso) p1)));
    }

    private double getHRR(Proceso p) {
        return (p.getTiempoEspera() + p.getInstrucciones()) / (double) p.getInstrucciones();
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
                    // swap datos (no nodos)
                    current.setDato(b);
                    current.getPnext().setDato(a);
                    swapped = true;
                }
                current = current.getPnext();
            }
        } while (swapped);

        return list;
    }

    // ===== Preempción SRT =====
    public boolean ifSRT(Proceso process) {
        if (controlador.getPolitica() == 3) { // SRT
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

    // ===== Actualización de PCB / movimiento entre colas =====
    // Con PC/MAR
    public void updatePCB(Proceso process, int programCounter, int memoryAddressRegister, String state) {
        process.setEstado(state);
        process.setPc(programCounter);
        process.setMar(memoryAddressRegister);
        process.setTiempoEspera(0);

        switch (state) {
            case "Bloqueado" ->
                blockedList.InsertarFinal(process);
            case "Listo" ->
                readyList.InsertarFinal(process);
            case "Suspendido-Listo" ->
                suspendedReadyList.InsertarFinal(process);      // NUEVO
            case "Suspendido-Bloqueado" ->
                suspendedBlockedList.InsertarFinal(process);    // NUEVO
            default ->
                exitList.InsertarFinal(process);
        }
        this.updateAllQueuesOnUI();
    }

    // Sin PC/MAR
    public void updatePCB(Proceso process, String state) {
        process.setEstado(state);
        process.setTiempoEspera(0);

        switch (state) {
            case "Bloqueado" ->
                blockedList.InsertarFinal(process);
            case "Listo", "ready" ->
                readyList.InsertarFinal(process);
            case "Suspendido-Listo" ->
                suspendedReadyList.InsertarFinal(process);      // NUEVO
            case "Suspendido-Bloqueado" ->
                suspendedBlockedList.InsertarFinal(process);    // NUEVO
            default ->
                exitList.InsertarFinal(process);
        }
        this.updateAllQueuesOnUI();
    }

    // ===== Tick de reloj: aumenta espera en Ready y reordena si cambió política =====
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
        this.updateAllQueuesOnUI();
    }

    // ===== Utilidades de movimiento =====
    public void moveReadyToSuspendedReady(int pid) {
        Nodo n = readyList.getpFirst();
        while (n != null) {
            Proceso p = (Proceso) n.getDato();
            if (p.getId() == pid) {
                readyList.eliminarPorReferencia(p);
                p.setEstado("Suspendido-Listo");
                suspendedReadyList.InsertarFinal(p);
                updateAllQueuesOnUI();
                return;
            }
            n = n.getPnext();
        }
    }

    public void moveBlockedToSuspendedBlocked(int pid) {
        Nodo n = blockedList.getpFirst();
        while (n != null) {
            Proceso p = (Proceso) n.getDato();
            if (p.getId() == pid) {
                blockedList.eliminarPorReferencia(p);
                p.setEstado("Suspendido-Bloqueado");
                suspendedBlockedList.InsertarFinal(p);
                updateAllQueuesOnUI();
                return;
            }
            n = n.getPnext();
        }
    }

    public void activateFromSuspendedReady(int pid) {
        Nodo n = suspendedReadyList.getpFirst();
        while (n != null) {
            Proceso p = (Proceso) n.getDato();
            if (p.getId() == pid) {
                suspendedReadyList.eliminarPorReferencia(p);
                p.setEstado("Listo");
                readyList.InsertarFinal(p);
                updateAllQueuesOnUI();
                return;
            }
            n = n.getPnext();
        }
    }

    public void activateFromSuspendedBlocked(int pid) {
        Nodo n = suspendedBlockedList.getpFirst();
        while (n != null) {
            Proceso p = (Proceso) n.getDato();
            if (p.getId() == pid) {
                suspendedBlockedList.eliminarPorReferencia(p);
                p.setEstado("Bloqueado");
                blockedList.InsertarFinal(p);
                updateAllQueuesOnUI();
                return;
            }
            n = n.getPnext();
        }
    }

    // ===== Render de todas las colas + PCBs =====
    private void updateAllQueuesOnUI() {
        this.updateReadyList();
        this.updateBlockedList();
        this.updateExitList();
        this.updateSuspendedReadyList();
        this.updateSuspendedBlockedList();
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

    public void updateReadyList() {
        Nodo pAux = readyList.getpFirst();
        StringBuilder display = new StringBuilder();
        while (pAux != null) {
            Proceso process = (Proceso) pAux.getDato();
            display.append("\n ----------------------------------\n ")
                    .append("ID: ").append(process.getId())
                    .append("\n Nombre: ").append(process.getNombre());
            pAux = pAux.getPnext();
        }
        controlador.setListosText(display.toString());
    }

    public void updateBlockedList() {
        Nodo pAux = blockedList.getpFirst();
        StringBuilder display = new StringBuilder();
        while (pAux != null) {
            Proceso process = (Proceso) pAux.getDato();
            display.append("\n ----------------------------------\n ")
                    .append("ID: ").append(process.getId())
                    .append("\n Nombre: ").append(process.getNombre());
            pAux = pAux.getPnext();
        }
        controlador.setBloqueadosText(display.toString());
    }

    public void updateExitList() {
        Nodo pAux = exitList.getpFirst();
        StringBuilder display = new StringBuilder();
        while (pAux != null) {
            Proceso process = (Proceso) pAux.getDato();
            display.append("\n ----------------------------------\n ")
                    .append("Id: ").append(process.getId())
                    .append("\n Nombre: ").append(process.getNombre());
            pAux = pAux.getPnext();
        }
        controlador.setSalidaText(display.toString());
    }

    public void updateSuspendedReadyList() {
        Nodo pAux = suspendedReadyList.getpFirst();
        StringBuilder display = new StringBuilder();
        while (pAux != null) {
            Proceso process = (Proceso) pAux.getDato();
            display.append("\n ----------------------------------\n ")
                    .append("ID: ").append(process.getId())
                    .append("\n Nombre: ").append(process.getNombre());
            pAux = pAux.getPnext();
        }
        controlador.setSuspendidosListosText(display.toString());
    }

    public void updateSuspendedBlockedList() {
        Nodo pAux = suspendedBlockedList.getpFirst();
        StringBuilder display = new StringBuilder();
        while (pAux != null) {
            Proceso process = (Proceso) pAux.getDato();
            display.append("\n ----------------------------------\n ")
                    .append("ID: ").append(process.getId())
                    .append("\n Nombre: ").append(process.getNombre());
            pAux = pAux.getPnext();
        }
        controlador.setSuspendidosBloqueadosText(display.toString());
    }

    // ===== Util para pintar un PCB =====
    public static String stringInterfaz(Proceso currentProcess) {
        return "\n ----------------------------------\n Id: " + currentProcess.getId()
                + "\n Estado: " + currentProcess.getEstado()
                + "\n Nombre: " + currentProcess.getNombre()
                + "\n PC: " + currentProcess.getPc()
                + "\n MAR: " + currentProcess.getMar()
                + "\n Espera: " + currentProcess.getTiempoEspera();
    }
}

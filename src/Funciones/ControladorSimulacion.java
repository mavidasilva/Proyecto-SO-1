/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import Interfaces.VistaSimulacion;
import javax.swing.SwingUtilities;

/**
 *
 * @author Moises Liota
 */
public class ControladorSimulacion {

    private VistaSimulacion vista;

    public ControladorSimulacion(VistaSimulacion vista) {
        this.vista = vista;
    }

    public void setCPUText(int id, String text) {
        vista.setCPU1(text);
    }

    public VistaSimulacion getVista() {
        return vista;
    }

    public void setVista(VistaSimulacion vista) {
        this.vista = vista;
    }

    public void setRelojGlobal(int i) {
        SwingUtilities.invokeLater(() -> vista.setReloj(Integer.toString(i)));
    }

    public int getPolitica() {
        return vista.getPolitica();
    }

    public int getTiempo() {
        return vista.getTiempoInstrucion();
    }

    // (mantén el nombre si otras clases ya lo llaman)
    public void actulizarCiclo(int i) {
        SwingUtilities.invokeLater(() -> vista.setReloj(Integer.toString(i)));
    }

    public void setPcbs(String text) {
        SwingUtilities.invokeLater(() -> vista.setPcbs(text));
    }

    public void setSuspendidosListosText(String text) {
        vista.setSuspendidosListos(text);
    }

    public void setSuspendidosBloqueadosText(String text) {
        vista.setSuspendidosBloqueados(text);
    }
    public void setListosText(String text) {
        vista.setListos(text);
    }
    
     public void setBloqueadosText(String text) {
        vista.setBloqueados(text);
    }

    public void setSalidaText(String text) {
        vista.setSalida(text);
    }

}

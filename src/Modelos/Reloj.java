/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import static java.lang.Thread.sleep;
import java.util.concurrent.Semaphore;

/**
 *
 * @author mariavictoriadasilvanunez
 */
public class Reloj extends Thread{
    private Semaphore mutex;
    private int ciclo;
    private volatile boolean running = true;

    //Debo agregar el Planificador
    public Reloj(Semaphore mutex) {
        this.mutex = mutex;
        this.ciclo = 0;
    }
    
    public void shutdown() {                
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {                   
            try {
                if (!running) break;
                mutex.acquire();
            } catch (InterruptedException ex) {
                if (!running) break;         
            }
            mutex.release();
            ciclo++;
        }
    }
}


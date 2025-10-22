/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaces;

import java.awt.BorderLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.SwingUtilities;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import EDD.*;
import Funciones.ControladorSimulacion;
import Funciones.JSONHandler;
import Modelos.CPU;
import Modelos.Planificador;
import Modelos.Proceso;
import Modelos.Reloj;
import java.util.concurrent.Semaphore;
import javax.swing.JOptionPane;

/**
 *
 * @author tomas
 */
public class VistaSimulacion extends javax.swing.JFrame {

    DefaultPieDataset dataset1;
    DefaultPieDataset dataset2;
    DefaultPieDataset dataset3;
    DefaultPieDataset dataset4;
    CPU[] cpus;
    Reloj reloj;
    Lista listolista;
    Lista todos;

    private Planificador planificador;
    private Lista blockedList = new Lista();
    private Lista exitList = new Lista();

    private Semaphore mutexCPUs;
    private Semaphore mutexReloj;

    /**
     * Creates new form VistaSimulacion
     */
    public VistaSimulacion(int tiempo, int politica, Lista listo, Lista todos) {
        initComponents();
        this.unidadescpu.setText("1");
        dataset1 = new DefaultPieDataset();
        dataset2 = new DefaultPieDataset();
        dataset3 = new DefaultPieDataset();
        dataset4 = new DefaultPieDataset();
        jPanel5.setLayout(new BorderLayout());
        jPanel5.add(createPieChart(dataset2, "CPU 1"), BorderLayout.CENTER);
        this.politica.setSelectedIndex(politica);
        this.tiempoinstruccion.setValue(tiempo);
        this.jLabel16.setText(tiempo + " ms");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.listolista = listo;
        this.todos = todos;
        this.uPcbs();

        this.unidadescpu.setText("1");
        this.unidadescpu.setEnabled(false);

        suspendidosListos = new javax.swing.JTextArea();
        suspendidosListos.setEditable(false);
        javax.swing.JScrollPane spSL = new javax.swing.JScrollPane(suspendidosListos);
        jPanel4.add(spSL, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 50, 120, 140));
        javax.swing.JLabel lblSL = new javax.swing.JLabel("Suspendidos-Listo");
        jPanel4.add(lblSL, new org.netbeans.lib.awtextra.AbsoluteConstraints(675, 30, -1, -1));

        suspendidosBloqueados = new javax.swing.JTextArea();
        suspendidosBloqueados.setEditable(false);
        javax.swing.JScrollPane spSB = new javax.swing.JScrollPane(suspendidosBloqueados);
        jPanel4.add(spSB, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 220, 120, 120));
        javax.swing.JLabel lblSB = new javax.swing.JLabel("Suspendidos-Bloqueado");
        jPanel4.add(lblSB, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 200, -1, -1));

    }

    public void setSuspendidosListos(String t) {
        this.suspendidosListos.setText(t);
    }

    public void setSuspendidosBloqueados(String t) {
        this.suspendidosBloqueados.setText(t);
    }

    public CPU[] getCpus() {
        return cpus;
    }

    public void setCpus(CPU[] cpus) {
        this.cpus = cpus;
    }

    public Reloj getReloj() {
        return reloj;
    }

    public void setReloj(Reloj reloj) {
        this.reloj = reloj;
    }

    public VistaSimulacion() {
        initComponents();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    private boolean validateInputs() {
        try {
            // Check if text fields are empty
            if (nombre.getText().isEmpty() || duracion.getText().isEmpty() || cicloexcep.getText().isEmpty() || duracionexcep.getText().isEmpty()) {
                return false;
            }
            // Check if the values are numbers
            Integer.parseInt(duracion.getText()); // duracion
            if (this.tipoproceso.getSelectedIndex() == 1) {
                Integer.parseInt(cicloexcep.getText()); // cicloexcep
                Integer.parseInt(duracionexcep.getText()); // duracionexcep
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public void setCPU1(String t) {
        this.cpu1.setText(t);
    }

    public void uPcbs() {
        String d = "";
        Nodo p = todos.getpFirst();
        while (p != null) {
            d += Planificador.stringInterfaz((Proceso) p.getDato());
            p = p.getPnext();
        }
        this.setPcbs(d);
    }

    public void setReloj(String t) {
        this.relojglobal.setText(t);
    }

    public void setListos(String t) {
        this.listos.setText(t);
    }

    public void setBloqueados(String t) {
        this.bloqueados.setText(t);
    }

    public void setSalida(String t) {
        this.salida.setText(t);
    }

    public void setPcbs(String t) {
        this.pcbs.setText(t);
    }

    public void setTiempoInstruccion(String i) {
        this.relojglobal.setText(i);
    }

    public int getTiempoInstrucion() {
        return this.tiempoinstruccion.getValue();
    }

    public int getPolitica() {
        return this.politica.getSelectedIndex();
    }

    private ChartPanel createPieChart(DefaultPieDataset dataset, String title) {

        dataset.setValue("Usuario", 0);
        dataset.setValue("Sistema Operativo", 0);

        JFreeChart pieChart = ChartFactory.createPieChart(
                title,
                dataset,
                true, true, false);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Usuario", Color.BLUE);
        plot.setSectionPaint("Sistema Operativo", Color.RED);

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(670, 380));
        return chartPanel;
    }

    public void updateDataset(int chartNumber, String category, int value) {
        SwingUtilities.invokeLater(() -> {
            DefaultPieDataset dataset;
            switch (chartNumber) {
                case 1 ->
                    dataset = dataset1;
                case 2 ->
                    dataset = dataset2;
                case 3 ->
                    dataset = dataset3;
                case 4 ->
                    dataset = dataset4;
                default ->
                    throw new IllegalArgumentException("Invalid chart number: " + chartNumber);
            }

            Number existingValue = dataset.getValue(category);
            int newValue = existingValue.intValue() + value;
            dataset.setValue(category, newValue);

            switch (chartNumber) {
                case 1 ->
                    ((ChartPanel) jPanel5.getComponent(0)).repaint();
            }
        });
    }

    private void resetUIForNewRun() {
        // TextAreas
        cpu1.setText("");
        listos.setText("");
        bloqueados.setText("");
        salida.setText("");
        pcbs.setText("");
        relojglobal.setText("0");

        // Gráficas a cero
        dataset1.setValue("Usuario", 0);
        dataset1.setValue("Sistema Operativo", 0);
        dataset2.setValue("Usuario", 0);
        dataset2.setValue("Sistema Operativo", 0);
        dataset3.setValue("Usuario", 0);
        dataset3.setValue("Sistema Operativo", 0);
        dataset4.setValue("Usuario", 0);
        dataset4.setValue("Sistema Operativo", 0);

        ((ChartPanel) jPanel5.getComponent(0)).repaint();
    }

    private void buildQueuesFromTodos() {
        listolista.vaciar();
        blockedList.vaciar();
        exitList.vaciar();

        Nodo n = todos.getpFirst();
        while (n != null) {
            Proceso p = (Proceso) n.getDato();
            p.setEstado("Listo");
            p.setPc(0);
            p.setMar(0);
            p.setTiempoEspera(0);
            listolista.InsertarFinal(p);
            n = n.getPnext();
        }
        uPcbs(); // refresca el panel de PCBs
    }

    private void startNewThreads() {
        // Semáforos nuevos
        mutexCPUs = new Semaphore(1);
        mutexReloj = new Semaphore(1);

        // Un único Controlador y un único Planificador
        ControladorSimulacion ctrl = new ControladorSimulacion(this);

        // Agrega ahora las DOS nuevas colas al planificador (ver sección 2)
        planificador = new Planificador(
                listolista,
                blockedList,
                exitList,
                todos,
                /* suspendedReady */ new Lista(),
                /* suspendedBlocked */ new Lista(),
                ctrl
        );

        // Solo 1 CPU
        cpus = new CPU[1];
        cpus[0] = new CPU(ctrl, planificador, /*id*/ 1, mutexCPUs);

        // Reloj
        //reloj = new Reloj(mutexReloj, planificador, ctrl);

        // Arrancar
        cpus[0].start();
        reloj.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        guardarproceso = new javax.swing.JButton();
        nombre = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        duracion = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cicloexcep = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        duracionexcep = new javax.swing.JTextField();
        tipoproceso = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        cpu1 = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        salida = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        pcbs = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        bloqueados = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        listos = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        relojglobal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnDetener = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        suspendidosListos = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        suspendidosBloqueados = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        tiempoinstruccion = new javax.swing.JSlider();
        unidadescpu = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        politica = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        guardarproceso.setText("Añadir");
        guardarproceso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarprocesoActionPerformed(evt);
            }
        });
        jPanel2.add(guardarproceso, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 320, 140, 30));

        nombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombreActionPerformed(evt);
            }
        });
        jPanel2.add(nombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, 280, -1));

        jLabel9.setText("Nombre:");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 30, -1, -1));

        jLabel10.setText("Duración: ");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 70, -1, -1));

        duracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duracionActionPerformed(evt);
            }
        });
        jPanel2.add(duracion, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, 280, -1));

        jLabel11.setText("Cada excepción:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, -1, -1));

        cicloexcep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cicloexcepActionPerformed(evt);
            }
        });
        jPanel2.add(cicloexcep, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 190, 280, -1));

        jLabel12.setText("Duración excepción:");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, -1, -1));

        duracionexcep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duracionexcepActionPerformed(evt);
            }
        });
        jPanel2.add(duracionexcep, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 230, 280, -1));

        tipoproceso.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CPU Bound", "I/O Bound" }));
        tipoproceso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoprocesoActionPerformed(evt);
            }
        });
        jPanel2.add(tipoproceso, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, 280, -1));

        jLabel13.setText("Tipo:");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, 30, 20));

        jTabbedPane1.addTab("Añadir", jPanel2);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 668, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("CPU 1", jPanel5);

        jPanel1.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 670, 380));

        jTabbedPane1.addTab("Estadisticas", jPanel1);

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        cpu1.setEditable(false);
        cpu1.setColumns(20);
        cpu1.setRows(5);
        jScrollPane4.setViewportView(cpu1);

        jPanel4.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 150, 360));

        salida.setEditable(false);
        salida.setColumns(20);
        salida.setRows(5);
        jScrollPane1.setViewportView(salida);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 220, 110, 120));

        pcbs.setEditable(false);
        pcbs.setColumns(20);
        pcbs.setRows(5);
        pcbs.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                pcbsCaretUpdate(evt);
            }
        });
        jScrollPane2.setViewportView(pcbs);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 50, 110, 290));

        bloqueados.setEditable(false);
        bloqueados.setColumns(20);
        bloqueados.setRows(5);
        jScrollPane7.setViewportView(bloqueados);

        jPanel4.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 50, 110, 130));

        listos.setEditable(false);
        listos.setColumns(20);
        listos.setRows(5);
        jScrollPane6.setViewportView(listos);

        jPanel4.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 50, 110, 290));

        jLabel1.setText("PCB");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, -1, -1));

        jLabel2.setText("Salida");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 200, -1, -1));

        jLabel3.setText("Suspendido Bloq.");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, -1, -1));

        jLabel4.setText("Listos");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, -1, -1));

        jLabel6.setText("Reloj Global");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, -1, -1));

        relojglobal.setEditable(false);
        relojglobal.setText("0");
        jPanel4.add(relojglobal, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 70, -1));

        jLabel8.setText("CPU 1");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        btnDetener.setText("DETENER");
        btnDetener.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetenerActionPerformed(evt);
            }
        });
        jPanel4.add(btnDetener, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 390, 340, -1));

        suspendidosListos.setEditable(false);
        suspendidosListos.setColumns(20);
        suspendidosListos.setRows(5);
        jScrollPane8.setViewportView(suspendidosListos);

        jPanel4.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 220, 110, 130));

        jLabel18.setText("Bloqueados");
        jPanel4.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 30, -1, -1));

        jLabel19.setText("Suspendido listo");
        jPanel4.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 200, -1, -1));

        suspendidosBloqueados.setEditable(false);
        suspendidosBloqueados.setColumns(20);
        suspendidosBloqueados.setRows(5);
        jScrollPane9.setViewportView(suspendidosBloqueados);

        jPanel4.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 50, 110, 130));

        jTabbedPane1.addTab("Simulación", jPanel4);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tiempoinstruccion.setMaximum(5000);
        tiempoinstruccion.setMinimum(1);
        tiempoinstruccion.setValue(5000);
        tiempoinstruccion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tiempoinstruccionStateChanged(evt);
            }
        });
        jPanel3.add(tiempoinstruccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 100, 220, -1));

        unidadescpu.setText("1");
        unidadescpu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unidadescpuActionPerformed(evt);
            }
        });
        jPanel3.add(unidadescpu, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 200, 210, -1));

        jLabel14.setText("CPUs:");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 200, -1, -1));

        jLabel15.setText("Tiempo instrucción: ");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, -1, -1));

        jLabel16.setText("5000 ms");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 120, 110, -1));

        politica.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FIFO", "RR", "SPN", "SRT", "HRRN" }));
        politica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                politicaActionPerformed(evt);
            }
        });
        jPanel3.add(politica, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 150, 210, -1));

        jLabel17.setText("Politica de Planificación: ");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 150, -1, -1));

        jButton2.setText("Iniciar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 330, 150, 30));

        jTabbedPane1.addTab("Configuración", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombreActionPerformed

    private void duracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duracionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_duracionActionPerformed

    private void cicloexcepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cicloexcepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cicloexcepActionPerformed

    private void duracionexcepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duracionexcepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_duracionexcepActionPerformed

    private void unidadescpuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unidadescpuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_unidadescpuActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // 1) Resetear UI
            resetUIForNewRun();
            // 2) Reconstruir colas desde 'todos'
            buildQueuesFromTodos();
            // 3) Crear planificador + 1 CPU + Reloj NUEVOS y arrancar
            startNewThreads();
            // 4) Persistir prefs
            JSONHandler.saveToJson(new int[]{this.tiempoinstruccion.getValue(), this.politica.getSelectedIndex()}, "numbers.json");
            JSONHandler.writeProcesosToJson(todos, "procesos.json");
            // Botones
            this.jButton2.setEnabled(false);
            this.guardarproceso.setEnabled(false);
            this.btnDetener.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al iniciar: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void guardarprocesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarprocesoActionPerformed
        // TODO add your handling code here:
        int ciclo = 1; // cicloexcep
        int duracionciclp = 1; // duracionexcep
        if (this.validateInputs()) {
            int duracionnt = Integer.parseInt(duracion.getText()); // duracion
            if (this.tipoproceso.getSelectedIndex() == 1) {
                ciclo = Integer.parseInt(cicloexcep.getText()); // cicloexcep
                duracionciclp = Integer.parseInt(duracionexcep.getText()); // duracionexcep
            }
            Proceso p = new Proceso(listolista.getSize(), nombre.getText(), (String) this.tipoproceso.getSelectedItem(), duracionnt, ciclo, duracionciclp, 0);
            listolista.InsertarFinal(p);
            todos.InsertarFinal(p);
            this.uPcbs();

        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "error en los atributos del proceso");
        }
    }//GEN-LAST:event_guardarprocesoActionPerformed

    private void tipoprocesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoprocesoActionPerformed
        // TODO add your handling code here:
        if (tipoproceso.getSelectedIndex() == 1) {
            this.duracionexcep.setEnabled(true);
            this.cicloexcep.setEnabled(true);
        } else {
            this.duracionexcep.setEnabled(false);
            this.cicloexcep.setEnabled(false);
        }
    }//GEN-LAST:event_tipoprocesoActionPerformed

    private void tiempoinstruccionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tiempoinstruccionStateChanged
        // TODO add your handling code here:
        this.jLabel16.setText(this.tiempoinstruccion.getValue() + " ms");
        int[] h = {this.tiempoinstruccion.getValue(), this.politica.getSelectedIndex()};
        JSONHandler.saveToJson(h, "numbers.json");
    }//GEN-LAST:event_tiempoinstruccionStateChanged

    private void politicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_politicaActionPerformed
        // TODO add your handling code here:
        int[] h = {this.tiempoinstruccion.getValue(), this.politica.getSelectedIndex()};
        JSONHandler.saveToJson(h, "numbers.json");
    }//GEN-LAST:event_politicaActionPerformed

    private void pcbsCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_pcbsCaretUpdate
        // TODO add your handling code here:
    }//GEN-LAST:event_pcbsCaretUpdate

    private void btnDetenerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetenerActionPerformed
        try {
            // Parar CPUs
            if (cpus != null) {
                for (CPU c : cpus) {
                    if (c != null) {
                        c.shutdown();
                    }
                }
            }
            // Parar reloj
            if (reloj != null) {
                reloj.shutdown();
            }

            // Rehabilitar “Iniciar” y “Añadir” si quieres relanzar
            this.jButton2.setEnabled(true);
            this.guardarproceso.setEnabled(true);

            // (Opcional) deshabilitar “Detener”
            this.btnDetener.setEnabled(false);

        } catch (Exception ex) {
            // log opcional
        }
    }//GEN-LAST:event_btnDetenerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VistaSimulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VistaSimulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VistaSimulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VistaSimulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VistaSimulacion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea bloqueados;
    private javax.swing.JButton btnDetener;
    private javax.swing.JTextField cicloexcep;
    private javax.swing.JTextArea cpu1;
    private javax.swing.JTextField duracion;
    private javax.swing.JTextField duracionexcep;
    private javax.swing.JButton guardarproceso;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea listos;
    private javax.swing.JTextField nombre;
    private javax.swing.JTextArea pcbs;
    private javax.swing.JComboBox<String> politica;
    private javax.swing.JTextField relojglobal;
    private javax.swing.JTextArea salida;
    private javax.swing.JTextArea suspendidosBloqueados;
    private javax.swing.JTextArea suspendidosListos;
    private javax.swing.JSlider tiempoinstruccion;
    private javax.swing.JComboBox<String> tipoproceso;
    private javax.swing.JTextField unidadescpu;
    // End of variables declaration//GEN-END:variables
}

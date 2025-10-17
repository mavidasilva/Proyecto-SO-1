/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import EDD.Lista;
import Modelos.Proceso;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author mariavictoriadasilvanunez
 */
public class JSONHandler {
    // ObjectMapper único, con pretty-print activado

    private static final ObjectMapper MAPPER
            = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Escribe una Lista (EDD.Lista) de Proceso a un archivo JSON.Convierte tu
     * lista a arreglo antes de serializar.
     *
     * @param procesos
     * @param filePath
     * @throws java.io.IOException
     */
    public static void writeProcesosToJson(Lista procesos, String filePath) throws IOException {
        if (procesos == null) {
            throw new IllegalArgumentException("procesos no puede ser null");
        }
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("filePath inválido");
        }

        Proceso[] arr = new Proceso[procesos.getSize()];
        for (int i = 0; i < procesos.getSize(); i++) {
            Object value = procesos.getValor(i);
            arr[i] = (Proceso) value;
        }

        ensureParentDirectory(Path.of(filePath));
        MAPPER.writeValue(new File(filePath), arr);
    }

    /**
     * Lee un archivo JSON (array de Proceso) y lo vuelca en una Lista.Si
     * falla, devuelve una lista vacía.
     *
     * @param filePath
     * @return
     */
    public static Lista readProcesosFromJson(String filePath) {
        Lista procesos = new Lista();
        try {
            Proceso[] arr = MAPPER.readValue(new File(filePath), Proceso[].class);
            for (Proceso p : arr) {
                procesos.InsertarFinal(p);
            }
        } catch (IOException e) {
            System.out.println("No se pudieron cargar procesos: " + e.getMessage());
        }
        return procesos;
    }

    /**
     * Guarda un arreglo de enteros como JSON.
     *
     * @param numberArray
     * @param filePath
     */
    public static void saveToJson(int[] numberArray, String filePath) {
        if (numberArray == null) {
            throw new IllegalArgumentException("numberArray no puede ser null");
        }
        try {
            ensureParentDirectory(Path.of(filePath));
            MAPPER.writeValue(new File(filePath), numberArray);
        } catch (IOException e) {
            System.err.println("Error guardando arreglo: " + e.getMessage());
        }
    }

    /**
     * Lee un arreglo de enteros desde un archivo JSON.
     *
     * @param filePath
     * @return
     */
    public static int[] readFromJson(String filePath) {
        try {
            return MAPPER.readValue(new File(filePath), int[].class);
        } catch (IOException e) {
            System.err.println("Error leyendo arreglo: " + e.getMessage());
            return null;
        }
    }

    private static void ensureParentDirectory(Path path) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}

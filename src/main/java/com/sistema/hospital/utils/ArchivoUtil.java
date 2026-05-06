package com.sistema.hospital.utils;

import com.sistema.hospital.models.Paciente;
import com.sistema.hospital.structures.ListaTriage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoUtil {
    private static final String RUTA_BIN = System.getProperty("user.home") + File.separator + "triage_datos.dat";

    public static void guardar(ListaTriage lista) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_BIN))) {
            oos.writeObject(lista);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static ListaTriage leer() {
        File file = new File(RUTA_BIN);
        if (!file.exists()) return new ListaTriage();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (ListaTriage) ois.readObject();
        } catch (Exception e) { return new ListaTriage(); }
    }

    public static List<Paciente> importarDesdeTXT(String ruta) {
        List<Paciente> lista = new ArrayList<>();
        File archivo = new File(ruta);
        
        if (!archivo.exists()) {
            System.out.println("error: no existe el archivo en la ruta: " + ruta);
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {
            String linea;
            int contadorLineas = 0;
            while ((linea = br.readLine()) != null) {
                contadorLineas++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] d = linea.split(",");
                if (d.length == 4) {
                    try {
                        Paciente p = new Paciente(d[0].trim(), d[1].trim(), d[2].trim(), Integer.parseInt(d[3].trim()));
                        lista.add(p);
                    } catch (Exception e) {
                        System.out.println("error en linea " + contadorLineas + ": " + linea);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("fallo critico de lectura: " + e.getMessage());
        }
        return lista;
    }

    public static void exportarATXT(ListaTriage hospital, String ruta) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(ruta));
            // este es el cambio vital: jala los pacientes de ambas listas dobles (espera y salas)
            List<Paciente> todosLosPacientes = hospital.obtenerTodosLosPacientes();
            
            for (Paciente p : todosLosPacientes) {
                writer.println(p.getNombre() + "," + p.getApellido() + "," + p.getCedula() + "," + p.getGravedad());
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("error al exportar txt: " + e.getMessage());
        }
    }
}
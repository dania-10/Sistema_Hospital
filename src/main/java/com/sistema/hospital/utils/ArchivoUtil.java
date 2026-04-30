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
        
        // RASTREO 1: ¿Existe el archivo?
        if (!archivo.exists()) {
            System.out.println("❌ ERROR: No existe el archivo en la ruta: " + ruta);
            return lista;
        }
        System.out.println("✅ ARCHIVO ENCONTRADO. Iniciando lectura...");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {
            String linea;
            int contadorLíneas = 0;
            while ((linea = br.readLine()) != null) {
                contadorLíneas++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] d = linea.split(",");
                if (d.length == 4) {
                    try {
                        Paciente p = new Paciente(d[0].trim(), d[1].trim(), d[2].trim(), Integer.parseInt(d[3].trim()));
                        lista.add(p);
                        System.out.println("👉 Paciente leído: " + d[0]);
                    } catch (Exception e) {
                        System.out.println("❌ Error en línea " + contadorLíneas + ": " + linea);
                    }
                } else {
                    System.out.println("⚠️ Línea " + contadorLíneas + " ignorada (no tiene 4 datos)");
                }
            }
            System.out.println("🏁 FIN: Se cargaron " + lista.size() + " pacientes de la lista.");
        } catch (Exception e) {
            System.out.println("❌ Fallo crítico de lectura: " + e.getMessage());
        }
        return lista;
    }
}
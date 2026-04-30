package com.sistema.hospital.structures;

import com.sistema.hospital.models.Paciente;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListaTriage implements Serializable {
    private Nodo cabezaTriage;
    private Nodo colaTriage;
    private List<Paciente> historialPacientes = new ArrayList<>();

    private int uciMax = 20;         private int uciOcupadas = 0;
    private int urgenciasMax = 15;   private int urgenciasOcupadas = 0;
    private int observacionMax = 10; private int observacionOcupadas = 0;

    public int getUciDisp() { return uciMax - uciOcupadas; }
    public int getUrgenciasDisp() { return urgenciasMax - urgenciasOcupadas; }
    public int getObservacionDisp() { return observacionMax - observacionOcupadas; }

    public String insertarPaciente(Paciente nuevoPaciente) {
        // Validación de duplicados
        if (!nuevoPaciente.getCedula().startsWith("NN")) {
            Nodo temp = cabezaTriage;
            while(temp != null) {
                if(temp.paciente.getCedula().equals(nuevoPaciente.getCedula())) return "ERROR: ❌ Cédula duplicada en Triage.";
                temp = temp.siguiente;
            }
            for (Paciente p : historialPacientes) {
                if(p.getCedula().equals(nuevoPaciente.getCedula())) return "ERROR: ❌ El paciente ya está en " + p.getSalaAsignada();
            }
        }

        Nodo nuevoNodo = new Nodo(nuevoPaciente);
        if (cabezaTriage == null) {
            cabezaTriage = colaTriage = nuevoNodo;
            return "Paciente ingresado a Triage.";
        }
        if (nuevoPaciente.getGravedad() > cabezaTriage.paciente.getGravedad()) {
            nuevoNodo.siguiente = cabezaTriage;
            cabezaTriage.anterior = nuevoNodo;
            cabezaTriage = nuevoNodo;
            return "Paciente registrado. Prioridad Máxima.";
        }
        Nodo actual = cabezaTriage;
        while (actual != null && actual.paciente.getGravedad() >= nuevoPaciente.getGravedad()) {
            actual = actual.siguiente;
        }
        if (actual == null) {
            colaTriage.siguiente = nuevoNodo;
            nuevoNodo.anterior = colaTriage;
            colaTriage = nuevoNodo;
        } else {
            nuevoNodo.siguiente = actual;
            nuevoNodo.anterior = actual.anterior;
            actual.anterior.siguiente = nuevoNodo;
            actual.anterior = nuevoNodo;
        }
        return "Paciente registrado en Triage.";
    }

    // --- MÉTODO ACTUALIZADO: Busca en Triage y en Historial ---
    public void editarPaciente(String cedulaVieja, String nNombre, String nApellido, String nCedula) {
        // 1. Busca en la fila de espera (Triage)
        Nodo actual = cabezaTriage;
        while (actual != null) {
            if (actual.paciente.getCedula().equals(cedulaVieja)) {
                actual.paciente.setNombre(nNombre);
                actual.paciente.setApellido(nApellido);
                actual.paciente.setCedula(nCedula);
                return; // Si lo encuentra aquí, actualiza y termina
            }
            actual = actual.siguiente;
        }
        
        // 2. Si no lo encontró en espera, busca en las salas activas (Historial)
        for (Paciente p : historialPacientes) {
            if (p.getCedula().equals(cedulaVieja)) {
                p.setNombre(nNombre);
                p.setApellido(nApellido);
                p.setCedula(nCedula);
                return; // Si lo encuentra aquí, actualiza y termina
            }
        }
    }

    public void actualizarEstadoPaciente(String cedula, String nuevoEstado) {
        for (Paciente p : historialPacientes) {
            if (p.getCedula().equals(cedula)) {
                p.setEstadoClinico(nuevoEstado);
                return;
            }
        }
    }

    public String despacharASala(String cedula, String salaElegida, String estadoInicial) {
        if (salaElegida.equals("UCI") && uciOcupadas >= uciMax) return "ERROR: ? UCI llena.";
        if (salaElegida.equals("URGENCIAS") && urgenciasOcupadas >= urgenciasMax) return "ERROR: 🚨 Urgencias llena.";
        if (salaElegida.equals("OBSERVACION") && observacionOcupadas >= observacionMax) return "ERROR: 🚨 Observación llena.";

        Nodo actual = cabezaTriage;
        while (actual != null && !actual.paciente.getCedula().equals(cedula)) actual = actual.siguiente;
        if (actual == null) return "No encontrado.";

        String numeroCama = "";
        if (salaElegida.equals("UCI")) { uciOcupadas++; numeroCama = "Cama " + (uciOcupadas + 300); }
        else if (salaElegida.equals("URGENCIAS")) { urgenciasOcupadas++; numeroCama = "Box " + urgenciasOcupadas; }
        else if (salaElegida.equals("OBSERVACION")) { observacionOcupadas++; numeroCama = "Camilla " + observacionOcupadas; }

        Paciente p = actual.paciente;
        p.setSalaAsignada(salaElegida + " - " + numeroCama); 
        p.setEstadoClinico(estadoInicial);
        historialPacientes.add(p); 

        if (cabezaTriage == colaTriage) cabezaTriage = colaTriage = null;
        else if (actual == cabezaTriage) { cabezaTriage = cabezaTriage.siguiente; cabezaTriage.anterior = null; }
        else if (actual == colaTriage) { colaTriage = colaTriage.anterior; colaTriage.siguiente = null; }
        else { actual.anterior.siguiente = actual.siguiente; actual.siguiente.anterior = actual.anterior; }

        return "✅ Remitido a " + p.getSalaAsignada();
    }

    // --- REGLA DE NEGOCIO: VALIDACIÓN ANTES DE DAR DE ALTA ---
    public String darAltaPaciente(String cedula) {
        Paciente pacienteAlta = null;
        int indice = -1;

        // 1. Buscamos al paciente primero sin borrarlo
        for(int i=0; i<historialPacientes.size(); i++) {
            if(historialPacientes.get(i).getCedula().equals(cedula)) {
                pacienteAlta = historialPacientes.get(i);
                indice = i;
                break;
            }
        }
        
        if(pacienteAlta == null) return "ERROR: Paciente no encontrado.";

        // 2. Verificamos el estado clínico
        String estado = pacienteAlta.getEstadoClinico();
        if (estado == null || (!estado.equals("Estable") && !estado.equals("Mejorando"))) {
            return "ERROR: 🚫 Alta denegada. El paciente debe estar 'Estable' o 'Mejorando'. (Estado actual: " + estado + ").";
        }

        // 3. Si pasó la prueba, lo sacamos de la lista
        historialPacientes.remove(indice);
        
        String sala = pacienteAlta.getSalaAsignada();
        if(sala.startsWith("UCI")) uciOcupadas--;
        else if(sala.startsWith("URGENCIAS")) urgenciasOcupadas--;
        else if(sala.startsWith("OBSERVACION")) observacionOcupadas--;
        
        return "👋 Alta dada a " + pacienteAlta.getNombreCompleto();
    }

    public List<Paciente> listarTriage() {
        List<Paciente> lista = new ArrayList<>();
        Nodo actual = cabezaTriage;
        while (actual != null) { lista.add(actual.paciente); actual = actual.siguiente; }
        return lista;
    }

    public List<Paciente> getHistorial() { return historialPacientes; }
}
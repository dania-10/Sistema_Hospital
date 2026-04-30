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
                if(p.getCedula().equals(nuevoPaciente.getCedula())) return "ERROR: ❌ El paciente ya está en " + p.getSala();
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

    public void editarPaciente(String cedulaVieja, String nNombre, String nApellido, String nCedula) {
        Nodo actual = cabezaTriage;
        while (actual != null) {
            if (actual.paciente.getCedula().equals(cedulaVieja)) {
                actual.paciente.setNombre(nNombre);
                actual.paciente.setApellido(nApellido);
                actual.paciente.setCedula(nCedula);
                return;
            }
            actual = actual.siguiente;
        }
    }

    // --- NUEVO MÉTODO: EDITAR ESTADO ---
    public void actualizarEstadoPaciente(String cedula, String nuevoEstado) {
        for (Paciente p : historialPacientes) {
            if (p.getCedula().equals(cedula)) {
                p.setEstadoActual(nuevoEstado);
                return;
            }
        }
    }

    public String despacharASala(String cedula, String salaElegida, String estadoInicial) {
        if (salaElegida.equals("UCI") && uciOcupadas >= uciMax) return "ERROR: 🚨 UCI llena.";
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
        p.setSala(salaElegida + " - " + numeroCama); 
        p.setEstadoActual(estadoInicial);
        historialPacientes.add(p); 

        if (cabezaTriage == colaTriage) cabezaTriage = colaTriage = null;
        else if (actual == cabezaTriage) { cabezaTriage = cabezaTriage.siguiente; cabezaTriage.anterior = null; }
        else if (actual == colaTriage) { colaTriage = colaTriage.anterior; colaTriage.siguiente = null; }
        else { actual.anterior.siguiente = actual.siguiente; actual.siguiente.anterior = actual.anterior; }

        return "✅ Remitido a " + p.getSala();
    }

    public String darAltaPaciente(String cedula) {
        Paciente pacienteAlta = null;
        for(int i=0; i<historialPacientes.size(); i++) {
            if(historialPacientes.get(i).getCedula().equals(cedula)) {
                pacienteAlta = historialPacientes.remove(i);
                break;
            }
        }
        if(pacienteAlta == null) return "ERROR: No encontrado.";
        String sala = pacienteAlta.getSala();
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
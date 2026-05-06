package com.sistema.hospital.structures;

import com.sistema.hospital.models.Paciente;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListaTriage implements Serializable {
    // primera lista doble: sala de espera (orden descendente: graves primero)
    private Nodo cabezaTriage;
    private Nodo colaTriage;
    
    // segunda lista doble: historial de salas (orden ascendente: listos para salida primero)
    private Nodo cabezaHistorial;
    private Nodo colaHistorial;

    private int uciMax = 20;         private int uciOcupadas = 0;
    private int urgenciasMax = 15;   private int urgenciasOcupadas = 0;
    private int observacionMax = 10; private int observacionOcupadas = 0;

    public int getUciDisp() { return uciMax - uciOcupadas; }
    public int getUrgenciasDisp() { return urgenciasMax - urgenciasOcupadas; }
    public int getObservacionDisp() { return observacionMax - observacionOcupadas; }

    public String insertarPaciente(Paciente nuevoPaciente) {
        if (!nuevoPaciente.getCedula().toLowerCase().startsWith("nn")) {
            Nodo temp = cabezaTriage;
            while(temp != null) {
                if(temp.paciente.getCedula().equals(nuevoPaciente.getCedula())) return "error: cedula duplicada en el sistema";
                temp = temp.siguiente;
            }
            Nodo tempH = cabezaHistorial;
            while(tempH != null) {
                if(tempH.paciente.getCedula().equals(nuevoPaciente.getCedula())) return "error: el paciente ya esta en " + tempH.paciente.getSalaAsignada().toLowerCase();
                tempH = tempH.siguiente;
            }
        }

        Nodo nuevoNodo = new Nodo(nuevoPaciente);
        if (cabezaTriage == null) {
            cabezaTriage = colaTriage = nuevoNodo;
            return "paciente ingresado a la sala de espera";
        }
        if (nuevoPaciente.getGravedad() > cabezaTriage.paciente.getGravedad()) {
            nuevoNodo.siguiente = cabezaTriage;
            cabezaTriage.anterior = nuevoNodo;
            cabezaTriage = nuevoNodo;
            return "paciente registrado con prioridad maxima";
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
        return "paciente registrado correctamente";
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
        
        Nodo actualH = cabezaHistorial;
        while (actualH != null) {
            if (actualH.paciente.getCedula().equals(cedulaVieja)) {
                actualH.paciente.setNombre(nNombre);
                actualH.paciente.setApellido(nApellido);
                actualH.paciente.setCedula(nCedula);
                return;
            }
            actualH = actualH.siguiente;
        }
    }

    public void actualizarEstadoPaciente(String cedula, String nuevoEstado) {
        Nodo actualH = cabezaHistorial;
        while (actualH != null) {
            if (actualH.paciente.getCedula().equals(cedula)) {
                actualH.paciente.setEstadoClinico(nuevoEstado);
                return;
            }
            actualH = actualH.siguiente;
        }
    }

    public String despacharASala(String cedula, String salaElegida, String estadoInicial) {
        if (salaElegida.equalsIgnoreCase("uci") && uciOcupadas >= uciMax) return "error: uci llena";
        if (salaElegida.equalsIgnoreCase("urgencias") && urgenciasOcupadas >= urgenciasMax) return "error: urgencias llena";
        if (salaElegida.equalsIgnoreCase("observacion") && observacionOcupadas >= observacionMax) return "error: observacion llena";

        Nodo actual = cabezaTriage;
        while (actual != null && !actual.paciente.getCedula().equals(cedula)) actual = actual.siguiente;
        if (actual == null) return "error: paciente no encontrado en espera";

        String numeroCama = "";
        if (salaElegida.equalsIgnoreCase("uci")) { uciOcupadas++; numeroCama = "cama " + (uciOcupadas + 300); }
        else if (salaElegida.equalsIgnoreCase("urgencias")) { urgenciasOcupadas++; numeroCama = "box " + urgenciasOcupadas; }
        else if (salaElegida.equalsIgnoreCase("observacion")) { observacionOcupadas++; numeroCama = "camilla " + observacionOcupadas; }

        Paciente p = actual.paciente;
        p.setSalaAsignada(salaElegida + " - " + numeroCama); 
        p.setEstadoClinico(estadoInicial);

        // lo sacamos de la primera lista doble (espera)
        if (cabezaTriage == colaTriage) cabezaTriage = colaTriage = null;
        else if (actual == cabezaTriage) { cabezaTriage = cabezaTriage.siguiente; cabezaTriage.anterior = null; }
        else if (actual == colaTriage) { colaTriage = colaTriage.anterior; colaTriage.siguiente = null; }
        else { actual.anterior.siguiente = actual.siguiente; actual.siguiente.anterior = actual.anterior; }

        // lo metemos en la segunda lista doble (historial) en orden ascendente
        Nodo nodoHistorial = new Nodo(p);
        if (cabezaHistorial == null) {
            cabezaHistorial = colaHistorial = nodoHistorial;
        } else if (p.getGravedad() <= cabezaHistorial.paciente.getGravedad()) {
            nodoHistorial.siguiente = cabezaHistorial;
            cabezaHistorial.anterior = nodoHistorial;
            cabezaHistorial = nodoHistorial;
        } else {
            Nodo temp = cabezaHistorial;
            while (temp != null && temp.paciente.getGravedad() < p.getGravedad()) {
                temp = temp.siguiente;
            }
            if (temp == null) {
                colaHistorial.siguiente = nodoHistorial;
                nodoHistorial.anterior = colaHistorial;
                colaHistorial = nodoHistorial;
            } else {
                nodoHistorial.siguiente = temp;
                nodoHistorial.anterior = temp.anterior;
                temp.anterior.siguiente = nodoHistorial;
                temp.anterior = nodoHistorial;
            }
        }

        return "remitido correctamente a " + p.getSalaAsignada().toLowerCase();
    }

    public String darAltaPaciente(String cedula) {
        Nodo actual = cabezaHistorial;
        while (actual != null) {
            if (actual.paciente.getCedula().equals(cedula)) {
                String estado = actual.paciente.getEstadoClinico();
                if (estado == null || (!estado.equalsIgnoreCase("estable") && !estado.equalsIgnoreCase("mejorando"))) {
                    return "error: alta denegada, el paciente debe estar estable o mejorando";
                }

                String sala = actual.paciente.getSalaAsignada().toLowerCase();
                if(sala.startsWith("uci")) uciOcupadas--;
                else if(sala.startsWith("urgencias")) urgenciasOcupadas--;
                else if(sala.startsWith("observacion")) observacionOcupadas--;

                // desvincular de la segunda lista doble
                if (actual == cabezaHistorial && actual == colaHistorial) {
                    cabezaHistorial = colaHistorial = null;
                } else if (actual == cabezaHistorial) {
                    cabezaHistorial = actual.siguiente;
                    cabezaHistorial.anterior = null;
                } else if (actual == colaHistorial) {
                    colaHistorial = actual.anterior;
                    colaHistorial.siguiente = null;
                } else {
                    actual.anterior.siguiente = actual.siguiente;
                    actual.siguiente.anterior = actual.anterior;
                }
                return "alta medica dada a " + actual.paciente.getNombreCompleto().toLowerCase();
            }
            actual = actual.siguiente;
        }
        return "error: paciente no encontrado en las salas";
    }

    public String eliminarPaciente(String cedula) {
        boolean encontrado = false;

        Nodo actual = cabezaTriage;
        while (actual != null) {
            if (actual.paciente.getCedula().equals(cedula)) {
                if (actual == cabezaTriage && actual == colaTriage) {
                    cabezaTriage = colaTriage = null;
                } else if (actual == cabezaTriage) {
                    cabezaTriage = actual.siguiente;
                    cabezaTriage.anterior = null;
                } else if (actual == colaTriage) {
                    colaTriage = actual.anterior;
                    colaTriage.siguiente = null;
                } else {
                    actual.anterior.siguiente = actual.siguiente;
                    actual.siguiente.anterior = actual.anterior;
                }
                encontrado = true;
                break;
            }
            actual = actual.siguiente;
        }

        if (!encontrado) {
            Nodo actualH = cabezaHistorial;
            while (actualH != null) {
                if (actualH.paciente.getCedula().equals(cedula)) {
                    String sala = actualH.paciente.getSalaAsignada().toLowerCase();
                    if(sala.startsWith("uci")) uciOcupadas--;
                    else if(sala.startsWith("urgencias")) urgenciasOcupadas--;
                    else if(sala.startsWith("observacion")) observacionOcupadas--;

                    if (actualH == cabezaHistorial && actualH == colaHistorial) {
                        cabezaHistorial = colaHistorial = null;
                    } else if (actualH == cabezaHistorial) {
                        cabezaHistorial = actualH.siguiente;
                        cabezaHistorial.anterior = null;
                    } else if (actualH == colaHistorial) {
                        colaHistorial = actualH.anterior;
                        colaHistorial.siguiente = null;
                    } else {
                        actualH.anterior.siguiente = actualH.siguiente;
                        actualH.siguiente.anterior = actualH.anterior;
                    }
                    encontrado = true;
                    break;
                }
                actualH = actualH.siguiente;
            }
        }

        if (encontrado) return "paciente eliminado correctamente";
        return "error: no se pudo eliminar, paciente no encontrado";
    }

    public List<Paciente> listarTriage() {
        List<Paciente> lista = new ArrayList<>();
        Nodo actual = cabezaTriage;
        while (actual != null) { lista.add(actual.paciente); actual = actual.siguiente; }
        return lista;
    }

    public List<Paciente> getHistorial() { 
        List<Paciente> lista = new ArrayList<>();
        Nodo actualH = cabezaHistorial;
        while (actualH != null) { lista.add(actualH.paciente); actualH = actualH.siguiente; }
        return lista;
    }
    
    public List<Paciente> obtenerTodosLosPacientes() {
        List<Paciente> todos = new ArrayList<>();
        todos.addAll(listarTriage());
        todos.addAll(getHistorial());
        return todos;
    }
}
package com.sistema.hospital.structures;

import com.sistema.hospital.models.Paciente;
import java.io.Serializable;

public class Nodo implements Serializable {
    public Paciente paciente;
    public Nodo anterior;
    public Nodo siguiente;

    public Nodo(Paciente paciente) {
        this.paciente = paciente;
        this.anterior = null;
        this.siguiente = null;
    }
}

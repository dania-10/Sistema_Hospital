package com.sistema.hospital.models;

import java.io.Serializable;

public class Paciente implements Serializable {
    private String nombre;
    private String apellido;
    private String cedula;
    private int gravedad;
    private String sala;         // Nueva característica: Sala física
    private String estadoActual; // Nueva característica: Estable, Crítico, etc.

    public Paciente(String nombre, String apellido, String cedula, int gravedad) {
        this.nombre = (nombre == null || nombre.trim().isEmpty()) ? "NN" : nombre;
        this.apellido = (apellido == null || apellido.trim().isEmpty()) ? "NN" : apellido;
        this.cedula = (cedula == null || cedula.trim().isEmpty()) ? "NN" : cedula;
        this.gravedad = gravedad;
        this.estadoActual = "En Triage"; // Cuando llega, su estado por defecto es este
        this.sala = "Sala de Espera";    // Cuando llega, su sala por defecto es esta
    }

    // --- GETTERS Y SETTERS ---
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getNombreCompleto() { return nombre + " " + apellido; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public int getGravedad() { return gravedad; }
    public void setGravedad(int gravedad) { this.gravedad = gravedad; }
    
    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }
    
    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }
}
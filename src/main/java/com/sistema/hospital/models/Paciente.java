package com.sistema.hospital.models;

import java.io.Serializable;

public class Paciente implements Serializable {
    private String nombre;
    private String apellido;
    private String cedula;
    private int gravedad;
    
    // Nombres corregidos para que coincidan con historial.jsp
    private String salaAsignada;  
    private String estadoClinico; 

    public Paciente(String nombre, String apellido, String cedula, int gravedad) {
        this.nombre = (nombre == null || nombre.trim().isEmpty()) ? "NN" : nombre;
        this.apellido = (apellido == null || apellido.trim().isEmpty()) ? "NN" : apellido;
        this.cedula = (cedula == null || cedula.trim().isEmpty()) ? "NN" : cedula;
        this.gravedad = gravedad;
        this.estadoClinico = "En Triage"; 
        this.salaAsignada = "Sala de Espera";
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
    
    // Métodos actualizados para el JSP
    public String getSalaAsignada() { return salaAsignada; }
    public void setSalaAsignada(String salaAsignada) { this.salaAsignada = salaAsignada; }
    
    public String getEstadoClinico() { return estadoClinico; }
    public void setEstadoClinico(String estadoClinico) { this.estadoClinico = estadoClinico; }
}
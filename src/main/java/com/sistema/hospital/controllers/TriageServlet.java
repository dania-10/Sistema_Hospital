package com.sistema.hospital.controllers;

import com.sistema.hospital.models.Paciente;
import com.sistema.hospital.structures.ListaTriage;
import com.sistema.hospital.utils.ArchivoUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "TriageServlet", urlPatterns = {"/TriageServlet"})
public class TriageServlet extends HttpServlet {
    private ListaTriage hospital;

    @Override
    public void init() { 
        hospital = ArchivoUtil.leer(); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarDatosYResponder(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String mensaje = "";

        if ("registrar".equals(accion)) {
            try {
                int gravedad = Integer.parseInt(request.getParameter("gravedad"));
                mensaje = hospital.insertarPaciente(new Paciente(request.getParameter("nombre"), request.getParameter("apellido"), request.getParameter("cedula"), gravedad));
                
                if(mensaje.startsWith("ERROR")) {
                    request.setAttribute("errorCapacidad", mensaje);
                } else {
                    request.setAttribute("mensaje", mensaje);
                    if (gravedad >= 9) {
                        request.setAttribute("alertaCritica", "⚠️ PACIENTE EN ESTADO CRÍTICO EN LISTA DE ESPERA");
                    }
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorCapacidad", "ERROR: La gravedad debe ser un número del 1 al 10.");
            }
        } 
        // --- SECCIÓN EDITADA: EDITAR NOMBRE, CÉDULA Y ESTADO ---
        else if ("editar".equals(accion)) {
            String cedulaOriginal = request.getParameter("cedulaOriginal");
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String cedulaNueva = request.getParameter("cedula");
            String nuevoEstado = request.getParameter("estado");

            // 1. Actualizamos datos básicos (Nombre y Cédula)
            hospital.editarPaciente(cedulaOriginal, nombre, apellido, cedulaNueva);
            
            // 2. Si se envió un nuevo estado (desde el historial), lo actualizamos
            if (nuevoEstado != null && !nuevoEstado.isEmpty()) {
                hospital.actualizarEstadoPaciente(cedulaNueva, nuevoEstado);
            }
            
            request.setAttribute("mensaje", "✅ Datos y estado del paciente actualizados.");
        }
        else if ("despachar".equals(accion)) {
            mensaje = hospital.despacharASala(request.getParameter("cedula"), request.getParameter("sala"), request.getParameter("estado"));
            if(mensaje.startsWith("ERROR")) {
                request.setAttribute("errorCapacidad", mensaje);
            } else {
                request.setAttribute("mensaje", mensaje);
            }
        }
        // --- SECCIÓN ACTUALIZADA: REGLA DE ALTA MÉDICA ---
        else if ("alta".equals(accion)) {
            mensaje = hospital.darAltaPaciente(request.getParameter("cedula"));
            // Separamos si es un error (rojo) o un éxito (verde)
            if (mensaje.startsWith("ERROR")) {
                request.setAttribute("errorCapacidad", mensaje);
            } else {
                request.setAttribute("mensaje", mensaje);
            }
        }
        else if ("actualizarEstado".equals(accion)) {
            hospital.actualizarEstadoPaciente(request.getParameter("cedula"), request.getParameter("nuevoEstado"));
            request.setAttribute("mensaje", "✅ Estado clínico actualizado correctamente.");
        }
        else if ("cargarTXT".equals(accion)) {
            System.out.println("DEBUG: Iniciando carga masiva...");
            String ruta = "C:\\hospital\\pacientes.txt"; 
            List<Paciente> importados = ArchivoUtil.importarDesdeTXT(ruta);
            
            if (importados.isEmpty()) {
                request.setAttribute("errorCapacidad", "ERROR: El archivo está vacío o no se encontró en C:\\hospital\\pacientes.txt");
            } else {
                int contador = 0;
                boolean hayCritico = false;
                
                for (Paciente p : importados) {
                    String res = hospital.insertarPaciente(p);
                    if (!res.startsWith("ERROR")) {
                        contador++;
                        if (p.getGravedad() >= 9) hayCritico = true;
                    }
                }
                
                if (hayCritico) {
                    request.setAttribute("alertaCritica", "⚠️ EMERGENCIA: SE DETECTARON PACIENTES CRÍTICOS EN LA CARGA MASIVA");
                }
                request.setAttribute("mensaje", "✅ Carga finalizada: " + contador + " pacientes nuevos agregados.");
            }
        }

        ArchivoUtil.guardar(hospital);
        cargarDatosYResponder(request, response);
    }

    private void cargarDatosYResponder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("hospitalDatos", hospital); 
        request.setAttribute("pacientes", hospital.listarTriage());
        request.setAttribute("historial", hospital.getHistorial());
        
        String vista = request.getParameter("vista");
        if ("espera".equals(vista)) {
            request.getRequestDispatcher("espera.jsp").forward(request, response);
        } else if ("historial".equals(vista)) {
            request.getRequestDispatcher("historial.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
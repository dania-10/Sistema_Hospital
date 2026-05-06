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
                
                if (gravedad < 1 || gravedad > 10) {
                    request.setAttribute("errorCapacidad", "error: el nivel de gravedad debe ser un numero entre 1 y 10");
                } else {
                    mensaje = hospital.insertarPaciente(new Paciente(request.getParameter("nombre"), request.getParameter("apellido"), request.getParameter("cedula"), gravedad));
                    
                    if(mensaje.startsWith("error")) {
                        request.setAttribute("errorCapacidad", mensaje);
                    } else {
                        request.setAttribute("mensaje", mensaje);
                        if (gravedad >= 9) {
                            request.setAttribute("alertaCritica", "alerta: paciente en estado critico en lista de espera");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorCapacidad", "error: la gravedad debe ser un numero valido del 1 al 10");
            }
        } 
        else if ("editar".equals(accion)) {
            String cedulaOriginal = request.getParameter("cedulaOriginal");
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String cedulaNueva = request.getParameter("cedula");
            String nuevoEstado = request.getParameter("estado");

            hospital.editarPaciente(cedulaOriginal, nombre, apellido, cedulaNueva);
            
            if (nuevoEstado != null && !nuevoEstado.isEmpty()) {
                hospital.actualizarEstadoPaciente(cedulaNueva, nuevoEstado);
            }
            
            request.setAttribute("mensaje", "datos y estado del paciente actualizados");
        }
        else if ("despachar".equals(accion)) {
            mensaje = hospital.despacharASala(request.getParameter("cedula"), request.getParameter("sala"), request.getParameter("estado"));
            if(mensaje.startsWith("error")) {
                request.setAttribute("errorCapacidad", mensaje);
            } else {
                request.setAttribute("mensaje", mensaje);
            }
        }
        else if ("alta".equals(accion)) {
            mensaje = hospital.darAltaPaciente(request.getParameter("cedula"));
            if (mensaje.startsWith("error")) {
                request.setAttribute("errorCapacidad", mensaje);
            } else {
                request.setAttribute("mensaje", mensaje);
            }
        }
        else if ("actualizarEstado".equals(accion)) {
            hospital.actualizarEstadoPaciente(request.getParameter("cedula"), request.getParameter("nuevoEstado"));
            request.setAttribute("mensaje", "estado clinico actualizado correctamente");
        }
        else if ("eliminar".equals(accion)) {
            String cedula = request.getParameter("cedula");
            mensaje = hospital.eliminarPaciente(cedula);
            if (mensaje.startsWith("error")) {
                request.setAttribute("errorCapacidad", mensaje);
            } else {
                request.setAttribute("mensaje", mensaje);
            }
        }
        else if ("cargarTXT".equals(accion)) {
            String ruta = "C:\\hospital\\pacientes.txt"; 
            List<Paciente> importados = ArchivoUtil.importarDesdeTXT(ruta);
            
            if (importados.isEmpty()) {
                hospital = new ListaTriage();
                request.setAttribute("mensaje", "el archivo txt esta vacio. todos los pacientes fueron eliminados del sistema.");
            } else {
                // 1. sincronizacion inteligente: elimina de la pagina a los que borraste del txt
                List<Paciente> actuales = hospital.obtenerTodosLosPacientes();
                for (Paciente pActual : actuales) {
                    boolean existeEnTxt = false;
                    for (Paciente pTxt : importados) {
                        if (pActual.getCedula().equals(pTxt.getCedula())) {
                            existeEnTxt = true;
                            break;
                        }
                    }
                    if (!existeEnTxt) {
                        hospital.eliminarPaciente(pActual.getCedula());
                    }
                }

                // 2. agrega a los nuevos que hayas escrito sin afectar a los que ya tienen cama
                int pacientesNuevos = 0;
                boolean hayCritico = false;
                
                for (Paciente p : importados) {
                    String res = hospital.insertarPaciente(p);
                    if (!res.startsWith("error")) { 
                        pacientesNuevos++;
                        if (p.getGravedad() >= 9) hayCritico = true;
                    }
                }
                
                if (pacientesNuevos == 0) {
                    request.setAttribute("mensaje", "sincronizacion completada: se eliminaron los borrados del txt. no hay nuevos.");
                } else {
                    if (hayCritico) {
                        request.setAttribute("alertaCritica", "alerta: se detectaron pacientes criticos en la carga masiva");
                    }
                    request.setAttribute("mensaje", "sincronizacion completada: actualizados correctamente y " + pacientesNuevos + " nuevos");
                }
            }
        }

        ArchivoUtil.exportarATXT(hospital, "C:\\hospital\\pacientes.txt");
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
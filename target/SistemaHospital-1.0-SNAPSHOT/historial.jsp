<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List, com.sistema.hospital.models.Paciente"%>
<!DOCTYPE html>
<html>
<head>
    <title>Gestión de Salas Activas</title>
    <style>
        :root {
            --teal-claro: #5ab4ac;
            --teal-oscuro: #337a75;
            --azul-medio: #2e5c9a;
            --azul-oscuro: #183765;
            --morado-profundo: #381545;
        }
        
        body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #0b2421 0%, #0a1838 50%, #200a2b 100%); background-attachment: fixed; min-height: 100vh; padding: 40px 20px; color: #2c3e50; margin: 0; display: flex; justify-content: center; align-items: flex-start; }
        .container { background: white; width: 100%; max-width: 1050px; padding: 35px; border-radius: 15px; box-shadow: 0 20px 50px rgba(0,0,0,0.5); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 4px solid var(--morado-profundo); padding-bottom: 15px; margin-bottom: 30px; }
        h1 { color: var(--azul-oscuro); margin: 0; font-weight: 800; letter-spacing: -0.5px; }
        .btn-volver { padding: 12px 24px; background: var(--azul-oscuro); color: white; text-decoration: none; border-radius: 8px; font-weight: bold; transition: all 0.3s ease; text-transform: uppercase; letter-spacing: 1px; font-size: 14px; display: inline-block; }
        .btn-volver:hover { background: var(--teal-oscuro); transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0,0,0,0.3); }
        
        table { width: 100%; border-collapse: collapse; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 5px 15px rgba(24, 55, 101, 0.08); margin-bottom: 20px;}
        th { background: var(--morado-profundo); color: white; padding: 20px 15px; text-align: left; font-size: 16px; letter-spacing: 1px; text-transform: uppercase; }
        td { padding: 18px 15px; border-bottom: 1px solid #e2e8f0; vertical-align: middle; font-weight: 500; }
        tr:hover { background-color: #f8fafc; }
        
        .badge-sala { padding: 8px 14px; border-radius: 20px; color: white; font-weight: bold; background: var(--azul-medio); font-size: 13px; letter-spacing: 0.5px; }
        
        .acciones-container { display: flex; gap: 8px; align-items: center; }
        .btn-alta { background: var(--teal-claro); color: var(--azul-oscuro); border: none; padding: 10px 18px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; text-transform: uppercase; font-size: 12px; letter-spacing: 0.5px; }
        .btn-alta:hover { background: var(--teal-oscuro); color: white; transform: scale(1.05); }
        
        .btn-edit { background: #e2e8f0; color: var(--azul-oscuro); border: none; padding: 10px 18px; border-radius: 6px; cursor: pointer; font-weight: bold; transition: 0.2s; text-transform: uppercase; font-size: 12px; letter-spacing: 0.5px; }
        .btn-edit:hover { background: #cbd5e1; transform: scale(1.05); }
        
        .alert-success { background: #d4edda; color: #155724; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #c3e6cb; font-weight: bold; text-align: center; }
        /* NUEVO ESTILO PARA EL ERROR */
        .alert-error { background: #f8d7da; color: #721c24; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #f5c6cb; font-weight: bold; text-align: center; }

        /* MODAL */
        .modal { display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 35px; border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.7); z-index: 1000; width: 400px; border-top: 8px solid var(--azul-medio); }
        .modal h3 { color: var(--azul-oscuro); margin-top: 0; font-weight: 800; text-align: center; margin-bottom: 25px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; font-weight: bold; color: var(--azul-oscuro); margin-bottom: 5px; font-size: 14px; }
        input, select { width: 100%; padding: 12px; border: 2px solid #cbd5e1; border-radius: 8px; box-sizing: border-box; background: #f8fafc; font-weight: 600; color: var(--azul-oscuro); transition: 0.3s; }
        input:focus, select:focus { outline: none; border-color: var(--teal-claro); background: white; }
    </style>
    <script>
        function mostrarEditar(c, n, a, est) {
            document.getElementById('editModal').style.display = 'block';
            document.getElementById('cOrig').value = c;
            document.getElementById('nNom').value = n;
            document.getElementById('nApe').value = a;
            document.getElementById('nCed').value = c;
            
            // Esto selecciona el estado actual en el menú desplegable automáticamente
            if(est && est !== "null") {
                document.getElementById('nEstado').value = est;
            }
        }
        function cerrarModal(id) {
            document.getElementById(id).style.display = 'none';
        }
    </script>
</head>
<body>

    <div class="container">
        <div class="header">
            <h1>🏢 Gestión de Salas Activas</h1>
            <a href="index.jsp" class="btn-volver">⬅ Volver al Menú</a>
        </div>
        
        <!-- ALERTAS DE ÉXITO O ERROR -->
        <% if (request.getAttribute("errorCapacidad") != null) { %>
            <div class="alert-error">🚫 <%= request.getAttribute("errorCapacidad") %></div>
        <% } %>

        <% if (request.getAttribute("mensaje") != null) { %>
            <div class="alert-success">✅ <%= request.getAttribute("mensaje") %></div>
        <% } %>

        <table>
            <tr>
                <th>Documento</th>
                <th>Paciente</th>
                <th>Sala Asignada</th>
                <th>Estado Clínico</th>
                <th>Acciones</th>
            </tr>
            <% 
                List<Paciente> historial = (List<Paciente>) request.getAttribute("historial");
                if (historial != null && !historial.isEmpty()) {
                    for (Paciente p : historial) {
            %>
            <tr>
                <td><%= p.getCedula() %></td>
                <td><strong><%= p.getNombreCompleto() %></strong></td>
                <td><span class="badge-sala"><%= p.getSalaAsignada() != null ? p.getSalaAsignada() : "SIN ASIGNAR" %></span></td>
                <td style="color: <%= "Crítico (Inestable)".equals(p.getEstadoClinico()) ? "#e74c3c" : "var(--azul-oscuro)" %>; font-weight: bold;">
                    <%= p.getEstadoClinico() != null ? p.getEstadoClinico() : "No definido" %>
                </td>
                <td>
                    <div class="acciones-container">
                        <!-- AQUÍ ENVIAMOS EL ESTADO CLÍNICO A LA FUNCIÓN JS -->
                        <button class="btn-edit" onclick="mostrarEditar('<%= p.getCedula() %>','<%= p.getNombre() %>','<%= p.getApellido() %>','<%= p.getEstadoClinico() %>')">✏️ Editar</button>
                        <form action="TriageServlet" method="POST" style="margin: 0;">
                            <input type="hidden" name="accion" value="alta">
                            <input type="hidden" name="vista" value="historial">
                            <input type="hidden" name="cedula" value="<%= p.getCedula() %>">
                            <button type="submit" class="btn-alta">🚶‍♂️ Dar de Alta</button>
                        </form>
                    </div>
                </td>
            </tr>
            <%      }
                } else { %>
                <tr><td colspan="5" style="text-align:center; padding: 30px; color: #64748b;">No hay pacientes ingresados en salas actualmente.</td></tr>
            <% } %>
        </table>
    </div>

    <!-- MODAL EDITAR -->
    <div id="editModal" class="modal">
        <h3>✏️ Actualizar Paciente</h3>
        <form action="TriageServlet" method="POST">
            <input type="hidden" name="accion" value="editar">
            <input type="hidden" name="vista" value="historial"> 
            <input type="hidden" name="cedulaOriginal" id="cOrig">
            
            <div class="form-group">
                <label>Nombre:</label>
                <input type="text" name="nombre" id="nNom" required>
            </div>
            <div class="form-group">
                <label>Apellido:</label>
                <input type="text" name="apellido" id="nApe" required>
            </div>
            <div class="form-group">
                <label>Nueva Cédula:</label>
                <input type="text" name="cedula" id="nCed" required>
            </div>
            <!-- NUEVO CAMPO DE ESTADO -->
            <div class="form-group">
                <label>Estado Clínico:</label>
                <select name="estado" id="nEstado">
                    <option value="Crítico (Inestable)">Crítico (Inestable)</option>
                    <option value="Estable">Estable</option>
                    <option value="En Observación">En Observación</option>
                    <option value="Mejorando">Mejorando</option>
                </select>
            </div>
            
            <button type="submit" style="width:100%; margin-top: 10px; background: var(--teal-oscuro); color: white; border: none; padding: 12px; border-radius: 8px; font-weight: bold; cursor: pointer; text-transform: uppercase;">💾 Guardar Cambios</button>
            <button type="button" onclick="cerrarModal('editModal')" style="width:100%; border:none; margin-top:10px; background: #94a3b8; color: white; padding: 12px; border-radius: 8px; font-weight: bold; cursor: pointer; text-transform: uppercase;">❌ Cancelar</button>
        </form>
    </div>

</body>
</html>
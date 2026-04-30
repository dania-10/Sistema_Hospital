<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List, com.sistema.hospital.models.Paciente"%>
<!DOCTYPE html>
<html>
<head>
    <title>Gestión de Salas</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #e8f4fd; padding: 30px; }
        .container { background: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); max-width: 1100px; margin: auto; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .btn-volver { padding: 10px 20px; background: #34495e; color: white; text-decoration: none; border-radius: 8px; font-weight: bold; }
        table { width: 100%; border-collapse: collapse; }
        th { background: #8e44ad; color: white; padding: 12px; text-align: left; }
        td { padding: 12px; border-bottom: 1px solid #ddd; }
        .btn-salida { background: #e74c3c; color: white; padding: 8px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }
        select { padding: 8px; border-radius: 5px; border: 1px solid #ccc; font-weight: bold; }
        .btn-mini-edit { background: #f39c12; color: white; border: none; padding: 5px 8px; border-radius: 4px; cursor: pointer; font-size: 12px; margin-top: 5px; }
        .modal { display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 10px 40px rgba(0,0,0,0.3); z-index: 100; width: 350px; }
    </style>
    <script>
        function mostrarEditar(c, n, a) {
            document.getElementById('editModal').style.display = 'block';
            document.getElementById('cOrig').value = c;
            document.getElementById('nNom').value = n;
            document.getElementById('nApe').value = a;
            document.getElementById('nCed').value = c;
        }
    </script>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>🏢 Gestión de Pacientes en Salas</h2>
            <a href="index.jsp" class="btn-volver">⬅ Volver</a>
        </div>
        <table>
            <tr>
                <th>Ubicación</th>
                <th>Paciente</th>
                <th>Estado Clínico (Editar)</th>
                <th>Acción</th>
            </tr>
            <% 
                List<Paciente> hist = (List<Paciente>) request.getAttribute("historial");
                if (hist != null) for (Paciente p : hist) { 
            %>
            <tr>
                <td><b style="color: #2980b9;"><%= p.getSala() %></b></td>
                <td>
                    <strong><%= p.getNombreCompleto() %></strong><br>
                    <small>ID: <%= p.getCedula() %></small><br>
                    <button class="btn-mini-edit" onclick="mostrarEditar('<%= p.getCedula() %>','<%= p.getNombre() %>','<%= p.getApellido() %>')">✏️ Editar Datos</button>
                </td>
                <td>
                    <form action="TriageServlet" method="POST" style="margin:0;">
                        <input type="hidden" name="accion" value="actualizarEstado">
                        <input type="hidden" name="cedula" value="<%= p.getCedula() %>">
                        <input type="hidden" name="vista" value="historial">
                        <select name="nuevoEstado" onchange="this.form.submit()">
                            <option value="Crítico (Inestable)" <%= p.getEstadoActual().equals("Crítico (Inestable)") ? "selected" : "" %>>🔴 Crítico</option>
                            <option value="Estable" <%= p.getEstadoActual().equals("Estable") ? "selected" : "" %>>🟢 Estable</option>
                            <option value="En Observación" <%= p.getEstadoActual().equals("En Observación") ? "selected" : "" %>>🟡 Observación</option>
                        </select>
                    </form>
                </td>
                <td>
                    <form action="TriageServlet" method="POST">
                        <input type="hidden" name="accion" value="alta">
                        <input type="hidden" name="cedula" value="<%= p.getCedula() %>">
                        <input type="hidden" name="vista" value="historial">
                        <button class="btn-salida">🚪 Dar Salida</button>
                    </form>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- MODAL EDITAR DATOS (También aquí por si acaso) -->
    <div id="editModal" class="modal">
        <h3>Editar Datos del Paciente</h3>
        <form action="TriageServlet" method="POST">
            <input type="hidden" name="accion" value="editar">
            <input type="hidden" name="vista" value="historial">
            <input type="hidden" name="cedulaOriginal" id="cOrig">
            <input type="text" name="nombre" id="nNom">
            <input type="text" name="apellido" id="nApe">
            <input type="text" name="cedula" id="nCed">
            <button type="submit" style="width:100%; background:#f39c12; color:white; border:none; padding:10px; border-radius:5px; font-weight:bold;">Guardar</button>
            <button type="button" onclick="this.parentElement.parentElement.style.display='none'" style="width:100%; border:none; margin-top:5px;">Cancelar</button>
        </form>
    </div>
</body>
</html>
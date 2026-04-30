<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List, com.sistema.hospital.models.Paciente, com.sistema.hospital.structures.ListaTriage"%>
<!DOCTYPE html>
<html>
<head>
    <title>Sala de Espera - Triage</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #fff; padding: 30px; }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #eee; padding-bottom: 15px; margin-bottom: 20px; }
        .btn-volver { padding: 10px 20px; background: #34495e; color: white; text-decoration: none; border-radius: 8px; font-weight: bold; }
        table { width: 100%; border-collapse: collapse; box-shadow: 0 5px 15px rgba(0,0,0,0.05); }
        th { background: #3498db; color: white; padding: 15px; text-align: left; }
        td { padding: 15px; border-bottom: 1px solid #eee; }
        .badge { padding: 5px 12px; border-radius: 20px; color: white; font-weight: bold; }
        .btn-edit { background: #f39c12; color: white; border: none; padding: 8px 12px; border-radius: 5px; cursor: pointer; font-weight: bold; }
        .btn-rem { background: #2980b9; color: white; border: none; padding: 8px 12px; border-radius: 5px; cursor: pointer; font-weight: bold; }
        
        /* ESTILO DEL ERROR (LO QUE FALTABA) */
        .alert-error { background: #f8d7da; color: #721c24; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #f5c6cb; font-weight: bold; text-align: center; font-size: 18px; }
        .alert-success { background: #d4edda; color: #155724; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #c3e6cb; font-weight: bold; text-align: center; }

        .modal { display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: #f9f9f9; padding: 30px; border-radius: 12px; box-shadow: 0 10px 40px rgba(0,0,0,0.3); z-index: 100; width: 350px; }
        input, select { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px; box-sizing: border-box; }
    </style>
    <script>
        function mostrarEditar(c, n, a) {
            document.getElementById('editModal').style.display = 'block';
            document.getElementById('cOrig').value = c;
            document.getElementById('nNom').value = n;
            document.getElementById('nApe').value = a;
            document.getElementById('nCed').value = c;
        }
        function mostrarRemision(c) {
            document.getElementById('remModal').style.display = 'block';
            document.getElementById('cRem').value = c;
        }
    </script>
</head>
<body>

    <div class="header">
        <h1>📋 Sala de Espera Priorizada</h1>
        <a href="index.jsp" class="btn-volver">⬅ Volver al Registro</a>
    </div>

    <!-- BLOQUE DE MENSAJES AÑADIDO AQUÍ -->
    <% if (request.getAttribute("errorCapacidad") != null) { %>
        <div class="alert-error">
            🚫 <%= request.getAttribute("errorCapacidad") %>
        </div>
    <% } %>
    
    <% if (request.getAttribute("mensaje") != null) { %>
        <div class="alert-success">
            ✅ <%= request.getAttribute("mensaje") %>
        </div>
    <% } %>

    <table>
        <tr>
            <th>Prioridad</th>
            <th>Paciente</th>
            <th>Documento</th>
            <th>Acciones</th>
        </tr>
        <% 
            List<Paciente> lista = (List<Paciente>) request.getAttribute("pacientes");
            if (lista != null) for (Paciente p : lista) {
                String color = (p.getGravedad() >= 9) ? "#e74c3c" : (p.getGravedad() >= 7 ? "#e67e22" : "#27ae60");
        %>
        <tr>
            <td><span class="badge" style="background: <%= color %>;"><%= p.getGravedad() %></span></td>
            <td><strong><%= p.getNombreCompleto() %></strong></td>
            <td><%= p.getCedula() %></td>
            <td>
                <button class="btn-edit" onclick="mostrarEditar('<%= p.getCedula() %>','<%= p.getNombre() %>','<%= p.getApellido() %>')">✏️ Editar</button>
                <button class="btn-rem" onclick="mostrarRemision('<%= p.getCedula() %>')">🏥 Remitir</button>
            </td>
        </tr>
        <% } %>
    </table>

    <!-- MODAL EDITAR -->
    <div id="editModal" class="modal">
        <h3>Identificar Paciente</h3>
        <form action="TriageServlet" method="POST">
            <input type="hidden" name="accion" value="editar">
            <input type="hidden" name="vista" value="espera">
            <input type="hidden" name="cedulaOriginal" id="cOrig">
            <input type="text" name="nombre" id="nNom" placeholder="Nombre">
            <input type="text" name="apellido" id="nApe" placeholder="Apellido">
            <input type="text" name="cedula" id="nCed" placeholder="Nueva Cédula">
            <button type="submit" class="btn-edit" style="width:100%">Guardar Cambios</button>
            <button type="button" onclick="this.parentElement.parentElement.style.display='none'" style="width:100%; border:none; margin-top:5px;">Cancelar</button>
        </form>
    </div>

    <!-- MODAL REMISIÓN -->
    <div id="remModal" class="modal">
        <% ListaTriage hosp = (ListaTriage) request.getAttribute("hospitalDatos"); %>
        <h3>Remitir a Sala</h3>
        <form action="TriageServlet" method="POST">
            <input type="hidden" name="accion" value="despachar">
            <input type="hidden" name="vista" value="espera">
            <input type="hidden" name="cedula" id="cRem">
            <label>Seleccionar Destino:</label>
            <select name="sala">
                <option value="UCI">🚨 UCI (Disp: <%= hosp != null ? hosp.getUciDisp() : 0 %>)</option>
                <option value="URGENCIAS">🚑 Urgencias (Disp: <%= hosp != null ? hosp.getUrgenciasDisp() : 0 %>)</option>
                <option value="OBSERVACION">🛏️ Observación (Disp: <%= hosp != null ? hosp.getObservacionDisp() : 0 %>)</option>
            </select>
            <label>Estado Inicial:</label>
            <select name="estado">
                <option>Crítico (Inestable)</option>
                <option>Estable</option>
                <option>En Observación</option>
            </select>
            <button type="submit" class="btn-rem" style="width:100%">Confirmar Remisión</button>
            <button type="button" onclick="this.parentElement.parentElement.style.display='none'" style="width:100%; border:none; margin-top:5px;">Cancelar</button>
        </form>
    </div>

</body>
</html><
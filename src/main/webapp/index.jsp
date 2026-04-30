<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sistema.hospital.structures.ListaTriage"%>
<!DOCTYPE html>
<html>
<head>
    <title>Hospital Central - Registro</title>
    <style>
        :root { --primary: #2c3e50; --success: #27ae60; --info: #2980b9; --dark: #34495e; }
        body { font-family: 'Segoe UI', sans-serif; background: #f0f2f5; margin: 0; padding: 20px; display: flex; flex-direction: column; align-items: center; }
        .card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); width: 100%; max-width: 850px; margin-bottom: 20px; }
        .stats { display: flex; gap: 15px; margin-bottom: 25px; }
        .stat-box { flex: 1; padding: 15px; border-radius: 10px; text-align: center; color: white; font-weight: bold; }
        .btn { padding: 12px; border: none; border-radius: 8px; cursor: pointer; font-weight: bold; color: white; transition: 0.3s; width: 100%; font-size: 16px; margin-top: 10px; }
        .grid-form { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
        input { padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; }
        .alert { padding: 15px; border-radius: 8px; margin-bottom: 20px; font-weight: bold; text-align: center; border: 1px solid transparent; }
        .alert-error { background: #f8d7da; color: #721c24; border-color: #f5c6cb; }
        .alert-success { background: #d4edda; color: #155724; border-color: #c3e6cb; }
        .popup-critico { background: #c0392b; color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; text-align: center; animation: pulse 1s infinite; width: 100%; max-width: 850px; font-weight: bold; }
        @keyframes pulse { 0% { transform: scale(1); } 50% { transform: scale(1.02); } 100% { transform: scale(1); } }
        .nav-grid { display: flex; gap: 20px; width: 100%; max-width: 850px; }
        .btn-nav { flex: 1; padding: 20px; font-size: 18px; text-decoration: none; border: none; border-radius: 8px; color: white; cursor: pointer; font-weight: bold; }
    </style>
    <script>function abrir(url, nombre) { window.open(url, nombre); }</script>
</head>
<body>

    <% if (request.getAttribute("alertaCritica") != null) { %>
        <div class="popup-critico">🚨 <%= request.getAttribute("alertaCritica") %> 🚨</div>
        <audio autoplay><source src="https://assets.mixkit.co/active_storage/sfx/995/995-preview.mp3" type="audio/mpeg"></audio>
    <% } %>

    <% ListaTriage hosp = (ListaTriage) request.getAttribute("hospitalDatos"); %>

    <div class="card">
        <% if (request.getAttribute("errorCapacidad") != null) { %>
            <div class="alert alert-error">❌ <%= request.getAttribute("errorCapacidad") %></div>
        <% } else if (request.getAttribute("mensaje") != null) { %>
            <div class="alert alert-success">🔔 <%= request.getAttribute("mensaje") %></div>
        <% } %>

        <div class="stats">
            <div class="stat-box" style="background: #e74c3c;">UCI<br><span><%= hosp != null ? hosp.getUciDisp() : 20 %> Libres</span></div>
            <div class="stat-box" style="background: #e67e22;">Urgencias<br><span><%= hosp != null ? hosp.getUrgenciasDisp() : 15 %> Libres</span></div>
            <div class="stat-box" style="background: #f1c40f; color: #2c3e50;">Obs.<br><span><%= hosp != null ? hosp.getObservacionDisp() : 10 %> Libres</span></div>
        </div>

        <h2 style="text-align: center; color: var(--primary);">🏥 Registro Manual</h2>
        <form action="TriageServlet" method="POST" class="grid-form">
            <input type="hidden" name="accion" value="registrar">
            <input type="text" name="nombre" placeholder="Nombre">
            <input type="text" name="apellido" placeholder="Apellido">
            <input type="text" name="cedula" placeholder="Cédula">
            <input type="number" name="gravedad" placeholder="Gravedad (1-10)" required>
            <button type="submit" class="btn" style="background: var(--success); grid-column: span 2;">➕ Registrar Individual</button>
        </form>

        <hr style="margin: 30px 0; border: 0; border-top: 1px solid #eee;">

        <h2 style="text-align: center; color: var(--primary);">📂 Carga Masiva</h2>
        <!-- Formulario separado para evitar el error de "Rellene este campo" -->
        <form action="TriageServlet" method="POST">
            <input type="hidden" name="accion" value="cargarTXT">
            <p style="text-align: center; font-size: 14px; color: #7f8c8d;">Ruta: <i>C:/hospital/pacientes.txt</i></p>
            <button type="submit" class="btn" style="background: var(--dark);">📥 Cargar Pacientes desde TXT</button>
        </form>
    </div>

    <div class="nav-grid">
        <button onclick="abrir('TriageServlet?vista=espera', 'HospEspera')" class="btn-nav" style="background: var(--info);">📋 Sala de Espera</button>
        <button onclick="abrir('TriageServlet?vista=historial', 'HospSalas')" class="btn-nav" style="background: #8e44ad;">🏢 Gestión de Salas</button>
    </div>

</body>
</html>
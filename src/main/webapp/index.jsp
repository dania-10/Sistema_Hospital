<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sistema.hospital.structures.ListaTriage"%>
<%@page import="com.sistema.hospital.utils.ArchivoUtil"%>
<!DOCTYPE html>
<html>
<head>
    <title>Hospital Central - Registro</title>
    <style>
        :root {
            --teal-claro: #5ab4ac;
            --teal-oscuro: #337a75;
            --azul-medio: #2e5c9a;
            --azul-oscuro: #183765;
            --morado-profundo: #381545;
        }
        
        body { 
            font-family: 'Segoe UI', sans-serif; 
            background: linear-gradient(135deg, #0b2421 0%, #0a1838 50%, #200a2b 100%);
            background-attachment: fixed; 
            min-height: 100vh;
            margin: 0; 
            padding: 40px 20px; 
            display: flex; 
            flex-direction: column; 
            align-items: center; 
            color: #2c3e50; 
        }

        .card { 
            background: white; 
            padding: 35px; 
            border-radius: 15px; 
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.4); 
            width: 100%; 
            max-width: 850px; 
            margin-bottom: 25px; 
            border-top: 6px solid var(--teal-oscuro); 
        }
        
        .stats { display: flex; gap: 15px; margin-bottom: 25px; }
        .stat-box { flex: 1; padding: 15px; border-radius: 10px; text-align: center; color: white; font-weight: bold; box-shadow: 0 4px 10px rgba(0,0,0,0.15); letter-spacing: 0.5px; }
        
        h2 { color: var(--azul-oscuro); font-weight: 800; letter-spacing: -0.5px; }
        
        .btn { padding: 14px; border: none; border-radius: 8px; cursor: pointer; font-weight: bold; color: white; transition: all 0.3s ease; width: 100%; font-size: 15px; margin-top: 10px; text-transform: uppercase; letter-spacing: 1px; }
        .btn:hover { transform: translateY(-2px); box-shadow: 0 8px 15px rgba(0,0,0,0.2); }
        
        .grid-form { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
        input { padding: 14px; border: 2px solid #e2e8f0; border-radius: 8px; font-size: 14px; background: #f8fafc; transition: 0.3s; color: var(--azul-oscuro); font-weight: 600; }
        input:focus { outline: none; border-color: var(--teal-claro); background: white; box-shadow: 0 0 0 3px rgba(90, 180, 172, 0.2); }
        
        .alert { padding: 15px; border-radius: 8px; margin-bottom: 20px; font-weight: bold; text-align: center; }
        .alert-error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .popup-critico { background: #e74c3c; color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; text-align: center; animation: pulse 1s infinite; width: 100%; max-width: 850px; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; box-shadow: 0 10px 30px rgba(231, 76, 60, 0.3); }
        @keyframes pulse { 0% { transform: scale(1); } 50% { transform: scale(1.02); } 100% { transform: scale(1); } }
        
        .nav-grid { display: flex; gap: 20px; width: 100%; max-width: 850px; }
        .btn-nav { flex: 1; padding: 20px; font-size: 16px; text-decoration: none; border: none; border-radius: 12px; color: white; cursor: pointer; font-weight: bold; text-transform: uppercase; transition: all 0.3s ease; letter-spacing: 1px; box-shadow: 0 10px 25px rgba(0,0,0,0.3); }
        .btn-nav:hover { transform: translateY(-3px); box-shadow: 0 15px 30px rgba(0,0,0,0.4); }
    </style>
    <script>
        function irAServlet(vista) {
            // Siempre pasamos por el Servlet para asegurar datos frescos
            window.location.href = 'TriageServlet?vista=' + vista;
        }
    </script>
</head>
<body>

    <% if (request.getAttribute("alertaCritica") != null) { %>
        <div class="popup-critico">🚨 <%= request.getAttribute("alertaCritica") %> 🚨</div>
        <audio autoplay><source src="https://assets.mixkit.co/active_storage/sfx/995/995-preview.mp3" type="audio/mpeg"></audio>
    <% } %>

    <% 
        // TRUCO MAESTRO: Si no viene desde el Servlet, lee directamente la base de datos para mostrar la realidad
        ListaTriage hosp = (ListaTriage) request.getAttribute("hospitalDatos"); 
        if (hosp == null) {
            hosp = ArchivoUtil.leer();
        }
    %>

    <div class="card">
        <% if (request.getAttribute("errorCapacidad") != null) { %>
            <div class="alert alert-error">❌ <%= request.getAttribute("errorCapacidad") %></div>
        <% } else if (request.getAttribute("mensaje") != null) { %>
            <div class="alert alert-success">✅ <%= request.getAttribute("mensaje") %></div>
        <% } %>

        <div class="stats">
            <div class="stat-box" style="background: var(--teal-oscuro);">UCI<br><span style="font-size: 20px;"><%= hosp != null ? hosp.getUciDisp() : 20 %> Libres</span></div>
            <div class="stat-box" style="background: var(--azul-medio);">URGENCIAS<br><span style="font-size: 20px;"><%= hosp != null ? hosp.getUrgenciasDisp() : 15 %> Libres</span></div>
            <div class="stat-box" style="background: var(--morado-profundo);">OBSERVACIÓN<br><span style="font-size: 20px;"><%= hosp != null ? hosp.getObservacionDisp() : 10 %> Libres</span></div>
        </div>

        <h2 style="text-align: center;">🏥 Registro Manual</h2>
        <form action="TriageServlet" method="POST" class="grid-form">
            <input type="hidden" name="accion" value="registrar">
            <input type="text" name="nombre" placeholder="Nombre">
            <input type="text" name="apellido" placeholder="Apellido">
            <input type="text" name="cedula" placeholder="Cédula">
            <input type="number" name="gravedad" placeholder="Gravedad (1-10)" required>
            <button type="submit" class="btn" style="background: var(--teal-oscuro); grid-column: span 2;">➕ Registrar Paciente</button>
        </form>

        <hr style="margin: 35px 0; border: 0; border-top: 2px dashed #cbd5e1;">

        <h2 style="text-align: center;">📂 Importación Masiva</h2>
        <form action="TriageServlet" method="POST">
            <input type="hidden" name="accion" value="cargarTXT">
            <p style="text-align: center; font-size: 14px; color: #64748b; font-weight: 600;">Ruta de lectura: <i>C:/hospital/pacientes.txt</i></p>
            <button type="submit" class="btn" style="background: var(--azul-oscuro);">📥 Cargar Pacientes desde TXT</button>
        </form>
    </div>

    <div class="nav-grid">
        <!-- Actualizados para forzar la lectura del Servlet -->
        <button onclick="irAServlet('espera')" class="btn-nav" style="background: var(--azul-medio);">📋 Sala de Espera</button>
        <button onclick="irAServlet('historial')" class="btn-nav" style="background: var(--morado-profundo);">🏢 Gestión de Salas</button>
    </div>

</body>
</html>
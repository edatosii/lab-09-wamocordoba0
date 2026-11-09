<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="co.umariana.modelo.Estudiante" %>
<%@ page import="co.umariana.modelo.Materia" %>
<%@ page import="co.umariana.util.Html" %>
<%
    Estudiante estudiante = (Estudiante) request.getAttribute("estudiante");
    @SuppressWarnings("unchecked")
    List<Materia> materiasMatriculadas = (List<Materia>) request.getAttribute("materiasMatriculadas");
    @SuppressWarnings("unchecked")
    List<Materia> materiasDisponibles = (List<Materia>) request.getAttribute("materiasDisponibles");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Materias de <%= Html.escapar(estudiante.getNombre()) %></title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <header>
        <h1>Materias matriculadas: <%= Html.escapar(estudiante.getNombre()) %></h1>
    </header>
    <main>
        <p><a class="boton" href="estudiantes">Volver al listado de estudiantes</a></p>

        <%-- FORMULARIO PARA AGREGAR MATERIA AL ESTUDIANTE --%>
        <form action="matriculas" method="post">
            <input type="hidden" name="accion" value="matricular">
            <input type="hidden" name="origen" value="estudiante">
            <input type="hidden" name="estudianteId" value="<%= estudiante.getId() %>">
            <p>
                <label for="materiaId">Matricular en materia:</label>
                <select id="materiaId" name="materiaId" required>
                    <option value="">-- Seleccione una materia --</option>
                    <% for (Materia m : materiasDisponibles) { %>
                    <option value="<%= m.getId() %>"><%= Html.escapar(m.getNombre()) %> (<%= m.getCreditos() %> créditos)</option>
                    <% } %>
                </select>
                <button type="submit">Agregar materia</button>
            </p>
        </form>
       
        <%-- MATERIAS CURSADA POR EL ESTUDIANTE Y PERMITE ELIMINARLO DE CUALQUIERA--%>
        <table>
            <thead>
                <tr><th>Id</th><th>Materia</th><th>Créditos</th><th>Acciones</th></tr>
            </thead>
            <tbody>
                <% for (Materia m : materiasMatriculadas) { %>
                <tr>
                    <td><%= m.getId() %></td>
                    <td><%= Html.escapar(m.getNombre()) %></td>
                    <td><%= m.getCreditos() %></td>
                    <td class="acciones">
                        <form action="matriculas" method="post"
                              onsubmit="return confirm('¿Retirar al estudiante de esta materia?');">
                            <input type="hidden" name="accion" value="desmatricular">
                            <input type="hidden" name="origen" value="estudiante">
                            <input type="hidden" name="estudianteId" value="<%= estudiante.getId() %>">
                            <input type="hidden" name="materiaId" value="<%= m.getId() %>">
                            <button type="submit" class="peligro">Eliminar de materia</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                <% if (materiasMatriculadas.isEmpty()) { %>
                <tr><td colspan="4" class="vacio">Este estudiante aún no tiene materias matriculadas.</td></tr>
                <% } %>
            </tbody>
        </table>
        
    </main>
</body>
</html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="co.umariana.modelo.Estudiante" %>
<%@ page import="co.umariana.modelo.Materia" %>
<%@ page import="co.umariana.util.Html" %>
<%
    Materia materia = (Materia) request.getAttribute("materia");
    @SuppressWarnings("unchecked")
    List<Estudiante> estudiantesMatriculados = (List<Estudiante>) request.getAttribute("estudiantesMatriculados");
    @SuppressWarnings("unchecked")
    List<Estudiante> estudiantesDisponibles = (List<Estudiante>) request.getAttribute("estudiantesDisponibles");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Estudiantes en <%= Html.escapar(materia.getNombre()) %></title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <header>
        <h1>Estudiantes matriculados en: <%= Html.escapar(materia.getNombre()) %></h1>
    </header>
    <main>
        <p><a class="boton" href="matriculas?accion=materias">Volver a materias disponibles</a></p>

        <%-- FORMULARIO PARA AGREGAR ESTUDIANTE A LA MATERIA--%>
        <form action="matriculas" method="post">
            <input type="hidden" name="accion" value="matricular">
            <input type="hidden" name="origen" value="materia">
            <input type="hidden" name="materiaId" value="<%= materia.getId() %>">
            <p>
                <label for="estudianteId">Agregar estudiante a esta materia:</label>
                <select id="estudianteId" name="estudianteId" required>
                    <option value="">-- Seleccione un estudiante --</option>
                    <% for (Estudiante e : estudiantesDisponibles) { %>
                    <option value="<%= e.getId() %>"><%= Html.escapar(e.getNombre()) %> - Semestre <%= e.getSemestre() %></option>
                    <% } %>
                </select>
                <button type="submit">Agregar estudiante</button>
            </p>
        </form>
    
        <%-- LISTA ESTUDIANTES INSCRITOR EN UNA MATERIA Y PERMITE ELIMINARLOS DE ELLA--%>
        <table>
            <thead>
                <tr><th>Id</th><th>Nombre</th><th>Correo</th><th>Semestre</th><th>Acciones</th></tr>
            </thead>
            <tbody>
                <% for (Estudiante e : estudiantesMatriculados) { %>
                <tr>
                    <td><%= e.getId() %></td>
                    <td><%= Html.escapar(e.getNombre()) %></td>
                    <td><%= Html.escapar(e.getCorreo()) %></td>
                    <td><%= e.getSemestre() %></td>
                    <td class="acciones">
                        <form action="matriculas" method="post"
                              onsubmit="return confirm('¿Eliminar a este estudiante de la materia?');">
                            <input type="hidden" name="accion" value="desmatricular">
                            <input type="hidden" name="origen" value="materia">
                            <input type="hidden" name="materiaId" value="<%= materia.getId() %>">
                            <input type="hidden" name="estudianteId" value="<%= e.getId() %>">
                            <button type="submit" class="peligro">Eliminar de materia</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                <% if (estudiantesMatriculados.isEmpty()) { %>
                <tr><td colspan="5" class="vacio">Aún no hay estudiantes matriculados en esta materia.</td></tr>
                <% } %>
            </tbody>
        </table>
    </main>
</body>
</html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="co.umariana.modelo.Materia" %>
<%@ page import="co.umariana.util.Html" %>
<%
    @SuppressWarnings("unchecked")
    List<Materia> materias = (List<Materia>) request.getAttribute("materias");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Materias disponibles</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <header>
        <h1>Materias disponibles</h1>
    </header>
    <main>
        <p><a class="boton" href="estudiantes">Volver a estudiantes</a></p>

        <%--FORMULARIO PARA AGREGAR NUEVAS MATERIAS AL CATÁLOGO--%>
        <% if (error != null) { %>
        <p class="error"><%= Html.escapar(error) %></p>
        <% } %>
        <form action="matriculas" method="post">
            <input type="hidden" name="accion" value="crearMateria">
            <p>
                <label for="nombre">Nueva materia</label>
                <input type="text" id="nombre" name="nombre" placeholder="Ej. Ingeniería de Software" required>
            </p>
            <p>
                <label for="creditos">Créditos (1 a 10)</label>
                <input type="number" id="creditos" name="creditos" min="1" max="10" value="3" required>
            </p>
            <button type="submit">Agregar materia</button>
        </form>
        

        <%-- LISTA MATERIAS DISPONIBLES PARA VER ESTUDIANTES MATRICULADOS EN CADA UNA--%>
        <table>
            <thead>
                <tr><th>Id</th><th>Nombre</th><th>Créditos</th><th>Acciones</th></tr>
            </thead>
            <tbody>
                <% for (Materia m : materias) { %>
                <tr>
                    <td><%= m.getId() %></td>
                    <td><%= Html.escapar(m.getNombre()) %></td>
                    <td><%= m.getCreditos() %></td>
                    <td class="acciones">
                        <a href="matriculas?accion=porMateria&materiaId=<%= m.getId() %>">Ver estudiantes matriculados</a>
                    </td>
                </tr>
                <% } %>
                <% if (materias.isEmpty()) { %>
                <tr><td colspan="4" class="vacio">Aún no hay materias registradas.</td></tr>
                <% } %>
            </tbody>
        </table>
        
    </main>
</body>
</html>
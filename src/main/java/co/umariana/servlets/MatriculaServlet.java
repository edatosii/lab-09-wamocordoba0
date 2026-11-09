package co.umariana.servlets;

import co.umariana.dao.EstudianteDAO;
import co.umariana.dao.MatriculaDAO;
import co.umariana.modelo.Estudiante;
import co.umariana.modelo.Materia;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Control para gestionar las materias disponibles y las matrículas entre estudiantes y materias*/
@WebServlet("/matriculas")
public class MatriculaServlet extends HttpServlet {

    static final String VISTA_MATERIAS = "/WEB-INF/vistas/materias.jsp";
    static final String VISTA_MATERIAS_ESTUDIANTE = "/WEB-INF/vistas/materias_estudiante.jsp";
    static final String VISTA_ESTUDIANTES_MATERIA = "/WEB-INF/vistas/estudiantes_materia.jsp";

    private final MatriculaDAO matriculaDao = new MatriculaDAO();
    private final EstudianteDAO estudianteDao = new EstudianteDAO();

    /* PETICIONES GET (Mostrar vistas de materias y matrículas)*/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // 1. Listar todas las materias disponibles
        if (accion == null || "materias".equals(accion)) {
            request.setAttribute("materias", matriculaDao.listarMaterias());
            request.getRequestDispatcher(VISTA_MATERIAS).forward(request, response);
            return;
        }

        // 2. Listar las materias matriculadas por un estudiante
        if ("porEstudiante".equals(accion)) {
            int estudianteId;
            try {
                estudianteId = Integer.parseInt(request.getParameter("estudianteId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Estudiante estudiante = estudianteDao.buscarPorId(estudianteId);
            if (estudiante == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("estudiante", estudiante);
            request.setAttribute("materiasMatriculadas", matriculaDao.listarMateriasPorEstudiante(estudianteId));
            request.setAttribute("materiasDisponibles", matriculaDao.listarMaterias());
            request.getRequestDispatcher(VISTA_MATERIAS_ESTUDIANTE).forward(request, response);
            return;
        }

        // 3. Listar los estudiantes matriculados en una materia
        if ("porMateria".equals(accion)) {
            int materiaId;
            try {
                materiaId = Integer.parseInt(request.getParameter("materiaId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Materia materia = matriculaDao.buscarMateriaPorId(materiaId);
            if (materia == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("materia", materia);
            request.setAttribute("estudiantesMatriculados", matriculaDao.listarEstudiantesPorMateria(materiaId));
            request.setAttribute("estudiantesDisponibles", estudianteDao.listar());
            request.getRequestDispatcher(VISTA_ESTUDIANTES_MATERIA).forward(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }


    /* PETICIONES POST (Matricular, Desmatricular y Crear Materia)*/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        // Agregar un estudiante a una materia
        if ("matricular".equals(accion)) {
            int estudianteId;
            int materiaId;
            try {
                estudianteId = Integer.parseInt(request.getParameter("estudianteId"));
                materiaId = Integer.parseInt(request.getParameter("materiaId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            matriculaDao.matricular(estudianteId, materiaId);

            // Redirige de vuelta a la pantalla desde donde se hizo la operación
            String origen = request.getParameter("origen");
            if ("materia".equals(origen)) {
                response.sendRedirect("matriculas?accion=porMateria&materiaId=" + materiaId);
            } else {
                response.sendRedirect("matriculas?accion=porEstudiante&estudianteId=" + estudianteId);
            }
            return;
        }

        // Eliminar un estudiante de una materia 
        if ("desmatricular".equals(accion)) {
            int estudianteId;
            int materiaId;
            try {
                estudianteId = Integer.parseInt(request.getParameter("estudianteId"));
                materiaId = Integer.parseInt(request.getParameter("materiaId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            matriculaDao.desmatricular(estudianteId, materiaId);

            String origen = request.getParameter("origen");
            if ("materia".equals(origen)) {
                response.sendRedirect("matriculas?accion=porMateria&materiaId=" + materiaId);
            } else {
                response.sendRedirect("matriculas?accion=porEstudiante&estudianteId=" + estudianteId);
            }
            return;
        }

        // Crear una nueva materia disponible 
        if ("crearMateria".equals(accion)) {
            String nombre = request.getParameter("nombre");
            String creditosStr = request.getParameter("creditos");

            if (nombre == null || nombre.trim().isEmpty()) {
                request.setAttribute("error", "El nombre de la materia es obligatorio");
                request.setAttribute("materias", matriculaDao.listarMaterias());
                request.getRequestDispatcher(VISTA_MATERIAS).forward(request, response);
                return;
            }

            int creditos;
            try {
                creditos = Integer.parseInt(creditosStr != null ? creditosStr.trim() : "");
                if (creditos < 1 || creditos > 10) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Los créditos deben ser un número entre 1 y 10");
                request.setAttribute("materias", matriculaDao.listarMaterias());
                request.getRequestDispatcher(VISTA_MATERIAS).forward(request, response);
                return;
            }

            if (!matriculaDao.registrarMateria(new Materia(0, nombre.trim(), creditos))) {
                request.setAttribute("error", "No se pudo guardar la materia. Verifique que no esté repetida.");
                request.setAttribute("materias", matriculaDao.listarMaterias());
                request.getRequestDispatcher(VISTA_MATERIAS).forward(request, response);
                return;
            }

            response.sendRedirect("matriculas?accion=materias");
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    
}
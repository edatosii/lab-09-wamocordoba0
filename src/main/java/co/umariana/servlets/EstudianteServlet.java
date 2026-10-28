package co.umariana.servlets;

import co.umariana.dao.EstudianteDAO;
import co.umariana.modelo.Estudiante;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controlador del CRUD de estudiantes: recibe las peticiones HTTP, usa el DAO
 * y decide qué vista mostrar.
 *
 * CONTRATO
 * --------------------------------------------------------------------
 * GET /estudiantes
 *   (sin accion) o accion=listar: muestra el listado de estudiantes.
 *   accion=nuevo: muestra el formulario vacío.
 *   accion=editar&id=N: busca al estudiante y muestra el formulario.
 *   Si el ID no existe o no es un número: sendError(404).
 *   Cualquier otra acción: sendError(400).
 *
 * POST /estudiantes
 *   accion=crear: registra un estudiante.
 *   accion=actualizar: actualiza un estudiante existente.
 *   accion=eliminar: elimina un estudiante.
 *   Cualquier otra acción: sendError(400).
 *
 * El nombre y el correo se guardan sin espacios al inicio/final (trim()).
 */
@WebServlet("/estudiantes")
public class EstudianteServlet extends HttpServlet {

    static final String VISTA_LISTAR = "/WEB-INF/vistas/listar.jsp";
    static final String VISTA_FORMULARIO = "/WEB-INF/vistas/formulario.jsp";

    private final EstudianteDAO dao = new EstudianteDAO();

    /**
     * Valida los datos del formulario.
     *
     * @return el mensaje de error, o null si los datos son válidos
     */
    static String validar(String nombre, String correo, String semestre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre es obligatorio";
        }

        if (correo == null ||
            !correo.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "El correo no es válido";
        }

        try {
            int s = Integer.parseInt(semestre.trim());

            if (s < 1 || s > 10) {
                return "El semestre debe ser un número entre 1 y 10";
            }
        } catch (NumberFormatException | NullPointerException e) {
            return "El semestre debe ser un número entre 1 y 10";
        }

        return null;
    }

    // =====================================================================
    // ETAPA 6. Mostrar (GET)
    // =====================================================================

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // Si no se especifica una acción o se solicita listar,
        // muestra todos los estudiantes.
        if (accion == null || "listar".equals(accion)) {
            request.setAttribute("estudiantes", dao.listar());
            request.getRequestDispatcher(VISTA_LISTAR)
                   .forward(request, response);
            return;
        }

        // Muestra el formulario vacío para registrar un estudiante nuevo.
        if ("nuevo".equals(accion)) {
            request.getRequestDispatcher(VISTA_FORMULARIO)
                   .forward(request, response);
            return;
        }

        // Busca al estudiante por su ID para mostrar el formulario de edición.
        if ("editar".equals(accion)) {
            int id;

            // Si el ID no es un número válido, responde con error 404.
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Busca al estudiante en la base de datos.
            Estudiante estudiante = dao.buscarPorId(id);

            // Si el estudiante no existe, responde con error 404.
            if (estudiante == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Envía el estudiante a la vista para mostrar sus datos.
            request.setAttribute("estudiante", estudiante);
            request.getRequestDispatcher(VISTA_FORMULARIO)
                   .forward(request, response);
            return;
        }

        // Si la acción no está permitida, responde con error 400.
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    // =====================================================================
    // ETAPA 7. Crear, actualizar y eliminar (POST)
    // =====================================================================

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Permite recibir correctamente caracteres especiales, como tildes y ñ.
        request.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");

        // -----------------------------------------------------------------
        // CREAR: registra un estudiante nuevo.
        // -----------------------------------------------------------------
        if ("crear".equals(accion)) {
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String semestre = request.getParameter("semestre");

            // Valida los datos antes de intentar guardarlos.
            String error = validar(nombre, correo, semestre);

            if (error != null) {
                request.setAttribute("error", error);
                request.getRequestDispatcher(VISTA_FORMULARIO)
                       .forward(request, response);
                return;
            }

            // Construye el objeto con los datos validados.
            Estudiante estudiante = new Estudiante(
                0,
                nombre.trim(),
                correo.trim(),
                Integer.parseInt(semestre.trim())
            );

            // Registra el estudiante y redirige al listado si tiene éxito.
            if (dao.registrar(estudiante)) {
                response.sendRedirect("estudiantes");
                return;
            }

            // Si no se pudo guardar, muestra el mensaje de error.
            request.setAttribute("error",
                "No se pudo guardar el estudiante. Verifique que el correo no esté repetido.");
            request.getRequestDispatcher(VISTA_FORMULARIO)
                   .forward(request, response);
            return;
        }

        // -----------------------------------------------------------------
        // ACTUALIZAR: modifica los datos de un estudiante existente.
        // -----------------------------------------------------------------
        if ("actualizar".equals(accion)) {
            int id;

            // Primero verifica que el ID sea un número válido.
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Comprueba que el estudiante exista antes de validar los datos.
            Estudiante existente = dao.buscarPorId(id);

            if (existente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String semestre = request.getParameter("semestre");

            // Valida los nuevos datos del estudiante.
            String error = validar(nombre, correo, semestre);

            if (error != null) {
                request.setAttribute("error", error);

                // Conserva los datos enviados para volver a mostrar el formulario.
                request.setAttribute("estudiante", new Estudiante(
                    id,
                    nombre == null ? "" : nombre.trim(),
                    correo == null ? "" : correo.trim(),
                    semestreValido(semestre)
                ));

                request.getRequestDispatcher(VISTA_FORMULARIO)
                       .forward(request, response);
                return;
            }

            // Construye el objeto con el ID y los datos actualizados.
            Estudiante estudiante = new Estudiante(
                id,
                nombre.trim(),
                correo.trim(),
                Integer.parseInt(semestre.trim())
            );

            // Actualiza el estudiante y redirige al listado si tiene éxito.
            if (dao.actualizar(estudiante)) {
                response.sendRedirect("estudiantes");
                return;
            }

            // Si ocurre un error, muestra el formulario con los datos ingresados.
            request.setAttribute("error",
                "No se pudo actualizar el estudiante. Verifique que el correo no esté repetido.");
            request.setAttribute("estudiante", estudiante);
            request.getRequestDispatcher(VISTA_FORMULARIO)
                   .forward(request, response);
            return;
        }

        // -----------------------------------------------------------------
        // ELIMINAR: borra un estudiante por su ID.
        // -----------------------------------------------------------------
        if ("eliminar".equals(accion)) {
            int id;

            // Comprueba que el ID enviado sea un número válido.
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Si no se pudo eliminar, responde con error 404.
            if (!dao.eliminar(id)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Si se eliminó correctamente, vuelve al listado.
            response.sendRedirect("estudiantes");
            return;
        }

        // Cualquier acción desconocida o una petición sin acción responde 400.
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Convierte el semestre a un valor válido para volver a mostrar el formulario.
     *
     * @param semestre valor recibido desde el formulario
     * @return el semestre si está entre 1 y 10; en caso contrario, 1
     */
    private int semestreValido(String semestre) {
        try {
            int s = Integer.parseInt(semestre.trim());
            return (s >= 1 && s <= 10) ? s : 1;
        } catch (NumberFormatException | NullPointerException e) {
            return 1;
        }
    }
}
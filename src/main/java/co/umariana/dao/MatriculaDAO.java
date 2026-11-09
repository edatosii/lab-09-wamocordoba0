package co.umariana.dao;

import co.umariana.conexion.ConexionBD;
import co.umariana.modelo.Estudiante;
import co.umariana.modelo.Materia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* Gestiona las materias y las matriculas entre estudantes y materias */
public class MatriculaDAO {

    /* Lista materias disponibles regristradas en las tabla "materias" con ID*/
    public List<Materia> listarMaterias() {
        List<Materia> materias = new ArrayList<>();
        String sql = "SELECT id, nombre, creditos FROM materias ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Materia materia = new Materia(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getInt("creditos")
                );
                materias.add(materia);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar materias: " + e.getMessage());
        }

        return materias;
    }
    

    /* Agrega estudiante a una materia en la tabla intermedia "matriculas"*/
    public boolean matricular(int estudianteId, int materiaId) {
        String sql = "INSERT INTO matriculas (estudiante_id, materia_id) VALUES (?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estudianteId);
            ps.setInt(2, materiaId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al matricular estudiante en materia: " + e.getMessage());
            return false;
        }
    }

    /* Elimina el vinculo entre un estudiante y una materia en la tabla "matriculas"*/
    public boolean desmatricular(int estudianteId, int materiaId) {
        String sql = "DELETE FROM matriculas WHERE estudiante_id = ? AND materia_id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estudianteId);
            ps.setInt(2, materiaId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar estudiante de la materia: " + e.getMessage());
            return false;
        }
    }


    /* Lista materias matriculadas por un estudiante en especifico*/
    public List<Materia> listarMateriasPorEstudiante(int estudianteId) {
        List<Materia> materias = new ArrayList<>();
        String sql = "SELECT m.id, m.nombre, m.creditos "
                   + "FROM materias m "
                   + "INNER JOIN matriculas mat ON m.id = mat.materia_id "
                   + "WHERE mat.estudiante_id = ? "
                   + "ORDER BY m.id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estudianteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    materias.add(new Materia(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("creditos")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar materias del estudiante: " + e.getMessage());
        }

        return materias;
    }

    /* Listar estudiantes matriculados en una materia determinada ordenados por ID */
    public List<Estudiante> listarEstudiantesPorMateria(int materiaId) {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.correo, e.semestre "
                   + "FROM estudiantes e "
                   + "INNER JOIN matriculas mat ON e.id = mat.estudiante_id "
                   + "WHERE mat.materia_id = ? "
                   + "ORDER BY e.id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, materiaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    estudiantes.add(new Estudiante(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("semestre")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar estudiantes de la materia: " + e.getMessage());
        }

        return estudiantes;
    }

    /* Permite buscar un materia especifica para mostrar vista y regristrar nuevas materias en la base de datos*/
    public Materia buscarMateriaPorId(int id) {
        String sql = "SELECT id, nombre, creditos FROM materias WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Materia(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("creditos")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar materia: " + e.getMessage());
        }

        return null;
    }

    public boolean registrarMateria(Materia materia) {
        String sql = "INSERT INTO materias (nombre, creditos) VALUES (?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, materia.getNombre());
            ps.setInt(2, materia.getCreditos());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar materia: " + e.getMessage());
            return false;
        }
    }
}
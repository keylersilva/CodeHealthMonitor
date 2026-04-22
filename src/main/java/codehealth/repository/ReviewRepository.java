package codehealth.repository;

import codehealth.model.CodeReview;
import codehealth.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {

    // 1. LEER LOS DATOS PARA LLENAR LAS TARJETAS
    public List<CodeReview> findAll() {
        List<CodeReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM code_reviews ORDER BY id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reviews.add(new CodeReview(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("snippet"),
                        rs.getString("estado_salud"),
                        rs.getString("observacion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // 2. GUARDAR UN ANÁLISIS NUEVO EN LA BASE DE DATOS
    public void guardar(CodeReview review) {
        String sql = "INSERT INTO code_reviews (titulo, autor, snippet, estado_salud, observacion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, review.getTitulo());
            pstmt.setString(2, review.getAutor());
            pstmt.setString(3, review.getSnippet());
            pstmt.setString(4, review.getEstadoSalud());
            pstmt.setString(5, review.getObservacion());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. ELIMINAR REGISTRO CUANDO SE PULSA LA PAPELEGA (EL QUE TE DABA ERROR ROJO)
    public void eliminar(int id) {
        String sql = "DELETE FROM code_reviews WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate(); // Ejecuta el borrado real en SQL

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
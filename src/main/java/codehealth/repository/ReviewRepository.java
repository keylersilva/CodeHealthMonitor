package codehealth.repository;

import codehealth.model.CodeReview;
import codehealth.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {
    public List<CodeReview> findAll() {
        List<CodeReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM code_reviews ORDER BY fecha_creacion DESC";

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
    public void guardar(CodeReview review) {
        String sql = "INSERT INTO code_reviews (titulo, snippet, estado_salud, observacion) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, review.getTitulo());
            pstmt.setString(2, review.getSnippet());
            pstmt.setString(3, review.getEstadoSalud());
            pstmt.setString(4, review.getObservacion());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
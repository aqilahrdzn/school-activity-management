package dao;

import util.DBConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Poster;

public class PosterDAO {

    public void savePoster(Poster poster) {
        String query = "INSERT INTO posters (title, description, file_path) VALUES (?, ?, ?)";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, poster.getTitle());
            preparedStatement.setString(2, poster.getDescription());
            preparedStatement.setString(3, poster.getFilePath());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving poster: " + e.getMessage());
        }
    }

    public List<Poster> getAllPosters() {
        List<Poster> posters = new ArrayList<>();
        String query = "SELECT * FROM posters";

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Poster poster = new Poster(
                    resultSet.getString("title"),
                    resultSet.getString("description"),
                    resultSet.getString("file_path")
                );
                posters.add(poster);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving posters: " + e.getMessage());
        }

        return posters;
    }
}

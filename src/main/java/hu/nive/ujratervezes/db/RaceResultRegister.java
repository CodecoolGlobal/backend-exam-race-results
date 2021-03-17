package hu.nive.ujratervezes.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RaceResultRegister {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public RaceResultRegister(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            System.err.println("Database not reachable.");
        }
        return connection;
    }

    public void saveResult(int competitorId, int result) {
        String query = "INSERT INTO race_result (competitor_id, result) VALUES (?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, competitorId);
            statement.setInt(2, result);

            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getLastResult(int competitorId) {
        int result = 0;

        String query = "SELECT result FROM race_result WHERE competitor_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, competitorId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                result = resultSet.getInt("result");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    public List<Integer> getAllResults(int competitorId) {
        List<Integer> results = new ArrayList<>();

        String query = "SELECT result FROM race_result WHERE competitor_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, competitorId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                results.add(resultSet.getInt("result"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return results;
    }

    public List<String> getMissingCompetitors() {
        List<String> competitors = new ArrayList<>();

        Set<Integer> competitorIds = getAllCompetitorIds();
        Set<Integer> resultIds = getCompetitorIdsWithResults();

        for (Integer id : competitorIds) {
            if (!resultIds.contains(id)) competitors.add(getCompetitorName(id));
        }

        return competitors;
    }

    private Set<Integer> getCompetitorIdsWithResults() {
        Set<Integer> competitorIds = new HashSet<>();
        String query = "SELECT competitor_id FROM race_result";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                competitorIds.add(resultSet.getInt("competitor_id"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return competitorIds;
    }

    private String getCompetitorName(Integer competitorId) {
        String name = null;
        String query = "SELECT competitor_name FROM competitor WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, competitorId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) name = resultSet.getString("competitor_name");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return name;
    }

    private Set<Integer> getAllCompetitorIds() {
        Set<Integer> competitorIds = new HashSet<>();
        String query = "SELECT id FROM competitor";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                competitorIds.add(resultSet.getInt("id"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return competitorIds;
    }
}

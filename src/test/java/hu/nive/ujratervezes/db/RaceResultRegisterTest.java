package hu.nive.ujratervezes.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RaceResultRegisterTest {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "hamargyuri";
    private static final String DB_PASSWORD = "data";

    private RaceResultRegister raceResultRegister;

    @BeforeEach
    void init() throws SQLException {
        raceResultRegister = new RaceResultRegister(DB_URL, DB_USER, DB_PASSWORD);
        initTables();
        createDummyData();
    }

    @AfterEach
    void destruct() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String dropRaceResult = "DROP TABLE IF EXISTS race_result;";
            String dropCompetitor = "DROP TABLE IF EXISTS competitor;";
            Statement statement = connection.createStatement();
            statement.execute(dropRaceResult);
            statement = connection.createStatement();
            statement.execute(dropCompetitor);
        }
    }

    @Test
    void test_saveResult_noPreviousResult() throws SQLException {
        raceResultRegister.saveResult(4, 4);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT result FROM race_result WHERE competitor_id = 4;");
            assertTrue(resultSet.next());
            assertEquals(4, resultSet.getInt("result"));
            assertFalse(resultSet.next());
        }
    }

    @Test
    void test_saveResult_withPreviousResult() throws SQLException {
        raceResultRegister.saveResult(1, 5);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT result FROM race_result WHERE competitor_id = 1;");
            assertTrue(resultSet.next());
            assertEquals(3, resultSet.getInt("result"));
            assertTrue(resultSet.next());
            assertEquals(5, resultSet.getInt("result"));
            assertFalse(resultSet.next());
        }
    }

    @Test
    void test_getLastResult_singleResult() {
        assertEquals(3, raceResultRegister.getLastResult(1));
    }

    @Test
    void test_getLastResult_multipleResults() {
        assertEquals(5, raceResultRegister.getLastResult(3));
    }

    @Test
    void test_getLastResult_noResults() {
        assertEquals(0, raceResultRegister.getLastResult(5));
    }

    @Test
    void test_getAllResults_singleResult() {
        assertEquals(List.of(3), raceResultRegister.getAllResults(1));
    }

    @Test
    void test_getAllResults_multipleResults() {
        assertEquals(List.of(3, 1, 4), raceResultRegister.getAllResults(2));
    }

    @Test
    void test_getAllResults_noResults() {
        assertEquals(List.of(), raceResultRegister.getAllResults(4));
    }

    @Test
    void test_getMissingCompetitors_withMissingCompetitors() {
        List<String> expectedMissingCompetitors = List.of("No Result Competitor", "Other No Result Competitor");
        assertEquals(expectedMissingCompetitors, raceResultRegister.getMissingCompetitors());
    }

    @Test
    void test_getMissingCompetitors_noMissingCompetitors() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertRaceResult = "INSERT INTO race_result (competitor_id, result) VALUES (?, ?);";
            PreparedStatement insertRaceResultStatement = connection.prepareStatement(insertRaceResult);
            insertRaceResultStatement.setInt(1, 4);
            insertRaceResultStatement.setInt(2, 4);
            insertRaceResultStatement.executeUpdate();
            insertRaceResultStatement.setInt(1, 5);
            insertRaceResultStatement.setInt(2, 5);
            insertRaceResultStatement.executeUpdate();
        }
        List<String> expectedMissingCompetitors = List.of();
        assertEquals(expectedMissingCompetitors, raceResultRegister.getMissingCompetitors());
    }


    void initTables() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String createCompetitor = "CREATE TABLE IF NOT EXISTS competitor (" +
                    "id SERIAL PRIMARY KEY, " +
                    "competitor_name VARCHAR(255)" +
                    ");";
            String createRaceResult = "CREATE TABLE IF NOT EXISTS race_result (" +
                    "id SERIAL PRIMARY KEY, " +
                    "competitor_id INTEGER, " +
                    "result INTEGER, " +
                    "FOREIGN KEY (competitor_id) REFERENCES competitor(id)" +
                    ");";
            Statement statement = connection.createStatement();
            statement.execute(createCompetitor);
            statement = connection.createStatement();
            statement.execute(createRaceResult);
        }
    }

    void createDummyData() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertCompetitor = "INSERT INTO competitor (competitor_name) VALUES (?);";
            PreparedStatement insertCompetitorStatement = connection.prepareStatement(insertCompetitor);
            insertCompetitorStatement.setString(1, "Single Result Competitor");
            insertCompetitorStatement.executeUpdate();
            insertCompetitorStatement.setString(1, "Multiple Result Competitor");
            insertCompetitorStatement.executeUpdate();
            insertCompetitorStatement.setString(1, "Other Multiple Result Competitor");
            insertCompetitorStatement.executeUpdate();
            insertCompetitorStatement.setString(1, "No Result Competitor");
            insertCompetitorStatement.executeUpdate();
            insertCompetitorStatement.setString(1, "Other No Result Competitor");
            insertCompetitorStatement.executeUpdate();

            String insertRaceResult = "INSERT INTO race_result (competitor_id, result) VALUES (?, ?);";
            PreparedStatement insertRaceResultStatement = connection.prepareStatement(insertRaceResult);
            //Single Result Competitor
            insertRaceResultStatement.setInt(1, 1);
            insertRaceResultStatement.setInt(2, 3);
            insertRaceResultStatement.executeUpdate();
            //Multiple Result Competitor
            insertRaceResultStatement.setInt(1, 2);
            insertRaceResultStatement.setInt(2, 3);
            insertRaceResultStatement.executeUpdate();
            insertRaceResultStatement.setInt(1, 2);
            insertRaceResultStatement.setInt(2, 1);
            insertRaceResultStatement.executeUpdate();
            insertRaceResultStatement.setInt(1, 2);
            insertRaceResultStatement.setInt(2, 4);
            insertRaceResultStatement.executeUpdate();
            //Other Multiple Result Competitor
            insertRaceResultStatement.setInt(1, 3);
            insertRaceResultStatement.setInt(2, 1);
            insertRaceResultStatement.executeUpdate();
            insertRaceResultStatement.setInt(1, 3);
            insertRaceResultStatement.setInt(2, 5);
            insertRaceResultStatement.executeUpdate();
        }
    }
}

package com.sziffer.series;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager {

    private static final String URL = "jdbc:oracle:thin:@dboracle.itk.ppke.hu:1521/lab.itk.ppke.hu";

    private Connection connection;
    private PreparedStatement getData, insertSeries, removeSeries, updateSeries;
    private Main main;

    public DatabaseManager(Main main) {
        this.main = main;
    }

    void connectToOracleDb(String username, String password) {
        new Thread(() -> {
            try {
                String adatbazis = "oracle.jdbc.driver.OracleDriver";
                Class.forName(adatbazis);
                connection = DriverManager.getConnection(URL, username, password);

                System.out.println("DATABASE CONNECTION SET");

                getData = connection.prepareStatement(
                        "SELECT series_id, series_name, description, next_episode FROM SERIES"
                );
                insertSeries = connection.prepareStatement(
                        "INSERT INTO SERIES (series_name, description, next_episode) VALUES(?,?,?)"
                );
                removeSeries = connection.prepareStatement(
                        "DELETE FROM SERIES WHERE SERIES_ID=?"
                );
                updateSeries = connection.prepareStatement(
                        "UPDATE SERIES SET description=?, next_episode=? WHERE SERIES_ID=?"
                );
                getSeries();
                Platform.runLater(() -> main.connected());

            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    void removeSeries(SeriesItem seriesItem) throws SQLException {
        removeSeries.clearParameters();
        removeSeries.setInt(1, seriesItem.getId());
        if (removeSeries.executeUpdate() > 0) {
            main.seriesRemoved(seriesItem);
            System.out.println("Item removed successfully");
        }
    }

    void insertSeries(String name, String description, LocalDate nextEpisode) throws SQLException {
        insertSeries.clearParameters();
        insertSeries.setString(1, name);
        insertSeries.setString(2, description);
        insertSeries.setDate(3, Date.valueOf(nextEpisode));
        if (insertSeries.executeUpdate() > 0) {
            getSeries();
            System.out.println("Row inserted successfully");
        }
    }

    void updateSeries(String description, LocalDate nextEpisode, int id) throws SQLException {
        updateSeries.clearParameters();
        updateSeries.setString(1, description);
        updateSeries.setDate(2, Date.valueOf(nextEpisode));
        updateSeries.setInt(3, id);
        if (updateSeries.executeUpdate() > 0) {
            getSeries();
            System.out.println("Database updated successfully");
        }
    }

    public void closeConnection() {
        if (connection == null) {
            return;
        }
        try {
            if (!connection.isClosed()) {
                connection.close();
                System.out.println("The connection was closed");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void getSeries() throws SQLException {

        final ObservableList<SeriesItem> observableList = FXCollections.observableArrayList();

        final ResultSet res = getData.executeQuery();

        while (res.next()) {

            observableList.add(
                    new SeriesItem(
                            res.getInt(1),
                            res.getString(2),
                            res.getString(3),
                            res.getDate(4))
            );
        }
        System.out.println(observableList.toString());
        main.setSeries(observableList);
    }
}
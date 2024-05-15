package exercise1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PlayerDisplayTable extends JFrame {
    private JTable playerTable;

    public PlayerDisplayTable() {
        setTitle("Player Information");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a table model with columns
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Player ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Address");
        tableModel.addColumn("Province");
        tableModel.addColumn("Postal Code");
        tableModel.addColumn("Phone Number");
        tableModel.addColumn("Game Title");
        tableModel.addColumn("Score");
        tableModel.addColumn("Playing Date");

        // Create a table and set the model
        playerTable = new JTable(tableModel);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(playerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch data from the database and populate the table
        fetchAndPopulateData();

        // Set JFrame properties
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchAndPopulateData() {
        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@199.212.26.208:1521:SQLD", "COMP228_F23_sy_59", "password")) {
            String sql = "SELECT PLAYER.PLAYER_ID, PLAYER.FIRST_NAME, PLAYER.LAST_NAME, PLAYER.ADDRESS, PLAYER.PROVINCE, PLAYER.POSTAL_CODE, PLAYER.PHONE_NUMBER, GAME.GAME_TITLE, PLAYER_AND_GAME.SCORE, PLAYER_AND_GAME.PLAYING_DATE FROM PLAYER_AND_GAME " +
                    "INNER JOIN PLAYER ON PLAYER_AND_GAME.PLAYER_ID = PLAYER.PLAYER_ID " +
                    "INNER JOIN GAME ON PLAYER_AND_GAME.GAME_ID = GAME.GAME_ID";

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                // Iterate through the result set and add rows to the table model
                while (resultSet.next()) {
                    Object[] rowData = {
                            resultSet.getInt("PLAYER_ID"),
                            resultSet.getString("FIRST_NAME"),
                            resultSet.getString("LAST_NAME"),
                            resultSet.getString("ADDRESS"),
                            resultSet.getString("PROVINCE"),
                            resultSet.getString("POSTAL_CODE"),
                            resultSet.getString("PHONE_NUMBER"),
                            resultSet.getString("GAME_TITLE"),
                            resultSet.getString("SCORE"),
                            resultSet.getDate("PLAYING_DATE")
                    };
                    ((DefaultTableModel) playerTable.getModel()).addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("An error occurred: " + e.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

package exercise1;

import java.sql.*;
import javafx.scene.control.Alert;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;


public class Controller
{
    public void createPlayerAction(String firstName, String lastName, String address, String postalCode, String province, String phoneNumber, String gameTitle, String score, String playingDate) throws ClassNotFoundException, SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@199.212.26.208:1521:SQLD", "COMP228_F23_sy_59", "password")) {

            // Insert into PLAYER table
            String playerInsertSql = "INSERT INTO PLAYER (FIRST_NAME, LAST_NAME, ADDRESS, POSTAL_CODE, PROVINCE, PHONE_NUMBER) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement playerStatement = connection.prepareStatement(playerInsertSql, new String[]{"PLAYER_ID"})) {
                playerStatement.setString(1, firstName);
                playerStatement.setString(2, lastName);
                playerStatement.setString(3, address);
                playerStatement.setString(4, postalCode);
                playerStatement.setString(5, province);
                playerStatement.setString(6, phoneNumber);

                int playerRowsAffected = playerStatement.executeUpdate();

                if (playerRowsAffected > 0) {
                    // Retrieve the generated player_id
                    ResultSet playerGeneratedKeys = playerStatement.getGeneratedKeys();
                    int playerId = -1;
                    if (playerGeneratedKeys.next()) {
                        playerId = playerGeneratedKeys.getInt(1);
                    }

                    // Insert into GAME table
                    String gameInsertSql = "INSERT INTO GAME (GAME_TITLE) VALUES (?)";
                    try (PreparedStatement gameStatement = connection.prepareStatement(gameInsertSql, new String[]{"GAME_ID"})) {
                        gameStatement.setString(1, gameTitle);

                        int gameRowsAffected = gameStatement.executeUpdate();

                        if (gameRowsAffected > 0) {
                            // Retrieve the generated game_id
                            ResultSet gameGeneratedKeys = gameStatement.getGeneratedKeys();
                            int gameId = -1;
                            if (gameGeneratedKeys.next()) {
                                gameId = gameGeneratedKeys.getInt(1);
                            }

                            // Insert into PLAYER_AND_GAME table
                            String playerAndGameInsertSql = "INSERT INTO PLAYER_AND_GAME (PLAYER_ID, GAME_ID, PLAYING_DATE, SCORE) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";
                            try (PreparedStatement playerAndGameStatement = connection.prepareStatement(playerAndGameInsertSql)) {
                                playerAndGameStatement.setInt(1, playerId);
                                playerAndGameStatement.setInt(2, gameId);
                                playerAndGameStatement.setString(3, playingDate);
                                playerAndGameStatement.setString(4, score);


                                int playerAndGameRowsAffected = playerAndGameStatement.executeUpdate();

                                if (playerAndGameRowsAffected > 0) {
                                    showSuccessMessage("Player and associated game information inserted successfully.");
                                } else {
                                    showErrorMessage("Failed to insert player and associated game information.");
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("An error occurred: " + e.getMessage());
        }
    }

    public void updatePlayerAction(String playerId, String firstName, String lastName, String address, String postalCode, String province, String phoneNumber, String gameTitle, String score, String playingDate) throws ClassNotFoundException, SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@199.212.26.208:1521:SQLD", "COMP228_F23_sy_59", "password")) {
            if (playerId == null || playerId.isEmpty()) {
                showErrorMessage("Player ID is required.");
                return;
            }
            if (firstName == null && lastName == null && address == null && postalCode == null && province == null && phoneNumber == null) {
                showErrorMessage("At least one field in player information is required.");
                return;
            } else
            {
                StringBuilder updateSql = new StringBuilder("UPDATE PLAYER SET ");
                List<String> updateValues = new ArrayList<>();
                if (firstName != null && !firstName.isEmpty())
                {
                    updateValues.add("FIRST_NAME = ?");
                }
                if (lastName != null && !lastName.isEmpty())
                {
                    updateValues.add("LAST_NAME = ?");
                }
                if (address != null && !address.isEmpty())
                {
                    updateValues.add("ADDRESS = ?");
                }
                if (postalCode != null && !postalCode.isEmpty())
                {
                    updateValues.add("POSTAL_CODE = ?");
                }
                if (province != null && !province.isEmpty())
                {
                    updateValues.add("PROVINCE = ?");
                }
                if (phoneNumber != null && !phoneNumber.isEmpty())
                {
                    updateValues.add("PHONE_NUMBER = ?");
                }
                if (!updateValues.isEmpty())
                {
                    updateSql.append(String.join(", ", updateValues));
                }
                updateSql.append(" WHERE PLAYER_ID = ?");

                try (PreparedStatement statement = connection.prepareStatement(updateSql.toString()))
                {
                    int parameterIndex = 1;
                    if (firstName != null && !firstName.isEmpty())
                    {
                        statement.setString(parameterIndex++, firstName);
                    }
                    if (lastName != null && !lastName.isEmpty())
                    {
                        statement.setString(parameterIndex++, lastName);
                    }
                    if (address != null && !address.isEmpty())
                    {
                        statement.setString(parameterIndex++, address);
                    }
                    if (postalCode != null && !postalCode.isEmpty())
                    {
                        statement.setString(parameterIndex++, postalCode);
                    }
                    if (province != null && !province.isEmpty())
                    {
                        statement.setString(parameterIndex++, province);
                    }
                    if (phoneNumber != null && !phoneNumber.isEmpty())
                    {
                        statement.setString(parameterIndex++, phoneNumber);
                    }
                    // Set the parameter for the WHERE clause
                    statement.setString(parameterIndex++, playerId);

                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                    {
                        showSuccessMessage("Player updated successfully.");
                    } else
                    {
                        showErrorMessage("Failed to update player.");
                    }
                }
            }

            //if game title is not null and not empty
            if (gameTitle != null && !gameTitle.isEmpty()) {
                //check if game exists
                String gameExistsSql = "SELECT COUNT(*) FROM GAME WHERE GAME_TITLE = ?";
                try (PreparedStatement gameExistsStatement = connection.prepareStatement(gameExistsSql))
                {
                    gameExistsStatement.setString(1, gameTitle);

                    try (ResultSet resultSet = gameExistsStatement.executeQuery())
                    {
                        if (resultSet.next())
                        {
                            int gameCount = resultSet.getInt(1);
                            //if game does not exist insert into game table and update player and game table
                            if (gameCount == 0)
                            {
                                // Insert into GAME table
                                String gameInsertSql = "INSERT INTO GAME (GAME_TITLE) VALUES (?)";
                                try (PreparedStatement gameStatement = connection.prepareStatement(gameInsertSql, new String[]{"GAME_ID"}))
                                {
                                    gameStatement.setString(1, gameTitle);

                                    int gameRowsAffected = gameStatement.executeUpdate();

                                    if (gameRowsAffected > 0)
                                    {
                                        // Retrieve the generated game_id
                                        ResultSet gameGeneratedKeys = gameStatement.getGeneratedKeys();
                                        int gameId = -1;
                                        if (gameGeneratedKeys.next())
                                        {
                                            gameId = gameGeneratedKeys.getInt(1);
                                        }
                                        //Update player and game table where player id = player id only with the fields that are not null
                                        StringBuilder playerAndGameUpdateSql = new StringBuilder("UPDATE PLAYER_AND_GAME SET GAME_ID = ?,");
                                        List<String> playerAndGameUpdateValues = new ArrayList<>();
                                        if (playingDate != null && !playingDate.isEmpty())
                                        {
                                            playerAndGameUpdateValues.add("PLAYING_DATE = TO_DATE(?, 'YYYY-MM-DD')");
                                        }
                                        if (score != null && !score.isEmpty())
                                        {
                                            playerAndGameUpdateValues.add("SCORE = ?");
                                        }
                                        if (!playerAndGameUpdateValues.isEmpty())
                                        {
                                            playerAndGameUpdateSql.append(String.join(", ", playerAndGameUpdateValues));
                                        }
                                        else {
                                            playerAndGameUpdateSql.deleteCharAt(playerAndGameUpdateSql.length() - 1);
                                        }
                                        playerAndGameUpdateSql.append(" WHERE PLAYER_ID = ?");
                                        try (PreparedStatement playerAndGameStatement = connection.prepareStatement(playerAndGameUpdateSql.toString()))
                                        {
                                            int parameterIndex = 1;
                                            playerAndGameStatement.setInt(parameterIndex++, gameId);
                                            if (playingDate != null && !playingDate.isEmpty())
                                            {
                                                playerAndGameStatement.setString(parameterIndex++, playingDate);
                                            }
                                            if (score != null && !score.isEmpty())
                                            {
                                                playerAndGameStatement.setString(parameterIndex++, score);
                                            }
                                            playerAndGameStatement.setString(parameterIndex++, playerId);

                                            int playerAndGameRowsAffected = playerAndGameStatement.executeUpdate();

                                            if (playerAndGameRowsAffected > 0)
                                            {
                                                showSuccessMessage("Player and associated game information updated successfully.");
                                            } else
                                            {
                                                showErrorMessage("Failed to update player and associated game information.");
                                            }
                                        }
                                    }
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                    showErrorMessage("An error occurred: " + e.getMessage());
                                }
                            } else
                            {
                                //if game exists get the game id and update player and game table
                                String getGameIdSql = "SELECT GAME_ID FROM GAME WHERE GAME_TITLE = ?";
                                try (PreparedStatement getGameIdStatement = connection.prepareStatement(getGameIdSql))
                                {
                                    getGameIdStatement.setString(1, gameTitle);

                                    try (ResultSet resultSet1 = getGameIdStatement.executeQuery())
                                    {
                                        if (resultSet1.next())
                                        {
                                            int gameId = resultSet1.getInt(1);
                                            //Update player and game table where player id = player id only with the fields that are not null
                                            StringBuilder playerAndGameUpdateSql = new StringBuilder("UPDATE PLAYER_AND_GAME SET GAME_ID = ?,");
                                            List<String> playerAndGameUpdateValues = new ArrayList<>();
                                            if (playingDate != null && !playingDate.isEmpty())
                                            {
                                                playerAndGameUpdateValues.add("PLAYING_DATE = TO_DATE(?, 'YYYY-MM-DD')");
                                            }
                                            if (score != null && !score.isEmpty())
                                            {
                                                playerAndGameUpdateValues.add("SCORE = ?");
                                            }
                                            if (!playerAndGameUpdateValues.isEmpty())
                                            {
                                                playerAndGameUpdateSql.append(String.join(", ", playerAndGameUpdateValues));
                                            }
                                            else {
                                                playerAndGameUpdateSql.deleteCharAt(playerAndGameUpdateSql.length() - 1);
                                            }

                                            playerAndGameUpdateSql.append(" WHERE PLAYER_ID = ?");
                                            try (PreparedStatement playerAndGameStatement = connection.prepareStatement(playerAndGameUpdateSql.toString()))
                                            {
                                                int parameterIndex = 1;
                                                playerAndGameStatement.setInt(parameterIndex++, gameId);
                                                if (playingDate != null && !playingDate.isEmpty())
                                                {
                                                    playerAndGameStatement.setString(parameterIndex++, playingDate);
                                                }
                                                if (score != null && !score.isEmpty())
                                                {
                                                    playerAndGameStatement.setString(parameterIndex++, score);
                                                }
                                                playerAndGameStatement.setString(parameterIndex++, playerId);

                                                int playerAndGameRowsAffected = playerAndGameStatement.executeUpdate();

                                                if (playerAndGameRowsAffected > 0)
                                                {
                                                    showSuccessMessage("Player and associated game information updated successfully.");
                                                } else
                                                {
                                                    showErrorMessage("Failed to update player and associated game information.");
                                                }
                                            }
                                        }
                                    }
                                    catch (SQLException e)
                                    {
                                        e.printStackTrace();
                                        showErrorMessage("An error occurred: " + e.getMessage());
                                    }
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                    showErrorMessage("An error occurred: " + e.getMessage());
                                }
                            }
                        }
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                        showErrorMessage("An error occurred: " + e.getMessage());
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    showErrorMessage("An error occurred: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("An error occurred: " + e.getMessage());
        }
    }
    //display the list of all players, their player id, the game they are playing, the score and the date played using JTable
    public void displayAllPlayersAction() throws ClassNotFoundException, SQLException
    {
        SwingUtilities.invokeLater(PlayerDisplayTable::new);
    }

    private void showSuccessMessage(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", message);
    }

    private void showErrorMessage(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

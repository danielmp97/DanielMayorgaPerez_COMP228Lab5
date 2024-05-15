package exercise1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.sql.*;


public class Main extends Application {
    private Controller controller = new Controller();

    public static void main(String[] args) {
        try
        {
            launch(args);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("Player Information");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label playerLabel = new Label("Player Information");
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        grid.add(playerLabel, 0, 0, 2, 1);

        grid.add(new Label("First Name:"), 0, 1);
        TextField firstNameField = new TextField();
        grid.add(firstNameField, 1, 1);

        grid.add(new Label("Last Name:"), 0, 2);
        TextField lastNameField = new TextField();
        grid.add(lastNameField, 1, 2);

        grid.add(new Label("Address:"), 0, 3);
        TextField addressField = new TextField();
        grid.add(addressField, 1, 3);

        grid.add(new Label("Province:"), 0, 4);
        TextField provinceField = new TextField();
        grid.add(provinceField, 1, 4);

        grid.add(new Label("Postal Code:"), 0, 5);
        TextField postalCodeField = new TextField();
        grid.add(postalCodeField, 1, 5);

        grid.add(new Label("Phone Number:"), 0, 6);
        TextField phoneNumberField = new TextField();
        grid.add(phoneNumberField, 1, 6);

        Label gameLabel = new Label("Game Information");
        gameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        grid.add(gameLabel, 3, 0, 2, 1);

        grid.add(new Label("Game Title:"), 3, 1);
        TextField gameTitleField = new TextField();
        grid.add(gameTitleField, 4, 1);

        grid.add(new Label("Game Score:"), 3, 2);
        TextField gameScoreField = new TextField();
        grid.add(gameScoreField, 4, 2);

        grid.add(new Label("Date Played:"), 3, 3);
        DatePicker datePlayedPicker = new DatePicker();
        grid.add(datePlayedPicker, 4, 3);

        Label updateLabel = new Label("Update Player By ID");
        updateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        grid.add(updateLabel, 3, 4, 2, 1);

        grid.add(new Label("Player ID:"), 3, 5);
        TextField playerIdField = new TextField();
        grid.add(playerIdField, 4, 5);

        Button updateButton = new Button("Update Player");
        grid.add(updateButton, 4, 6);

        Button createPlayerButton = new Button("Create Player");
        Button displayAllPlayersButton = new Button("Display All Players");

        createPlayerButton.setOnAction(e ->
        {
            try
            {
                String datePlayed = datePlayedPicker.getValue() != null ? datePlayedPicker.getValue().toString() : null;
                controller.createPlayerAction(firstNameField.getText(), lastNameField.getText(), addressField.getText(), postalCodeField.getText(), provinceField.getText(), phoneNumberField.getText(), gameTitleField.getText(), gameScoreField.getText(), datePlayed);
                firstNameField.setText("");
                lastNameField.setText("");
                addressField.setText("");
                postalCodeField.setText("");
                provinceField.setText("");
                phoneNumberField.setText("");
                gameTitleField.setText("");
                gameScoreField.setText("");
                datePlayedPicker.setValue(null);
            }
            catch (ClassNotFoundException | SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        });
        displayAllPlayersButton.setOnAction(e ->
        {
            try
            {
                controller.displayAllPlayersAction();
            }
            catch (ClassNotFoundException ex)
            {
                throw new RuntimeException(ex);
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        });

        updateButton.setOnAction(e ->
        {
            try
            {
                String datePlayed = datePlayedPicker.getValue() != null ? datePlayedPicker.getValue().toString() : null;
                controller.updatePlayerAction(playerIdField.getText(), firstNameField.getText(), lastNameField.getText(), addressField.getText(), postalCodeField.getText(), provinceField.getText(), phoneNumberField.getText(), gameTitleField.getText(), gameScoreField.getText(), datePlayed);
                playerIdField.setText("");
                firstNameField.setText("");
                lastNameField.setText("");
                addressField.setText("");
                postalCodeField.setText("");
                provinceField.setText("");
                phoneNumberField.setText("");
                gameTitleField.setText("");
                gameScoreField.setText("");
                datePlayedPicker.setValue(null);
            }
            catch (ClassNotFoundException | SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        });

        grid.add(createPlayerButton, 0, 11);
        grid.add(displayAllPlayersButton, 1, 11);

        Scene scene = new Scene(grid, 600, 350); // Increased width for better separation
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
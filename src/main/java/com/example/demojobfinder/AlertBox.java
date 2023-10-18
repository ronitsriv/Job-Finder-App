package com.example.demojobfinder;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Font;

public class AlertBox {
    public static void display(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000;");

        // Customize font if needed
        Font font = new Font("Arial", 15);
        dialogPane.setStyle("-fx-font-family: " + font.getFamily() + "; -fx-font-size: " + font.getSize() + ";");

        alert.showAndWait();
    }
}

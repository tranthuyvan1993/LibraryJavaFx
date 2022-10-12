package manage_library.util;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class AlertCustom {

    public static ButtonType show(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        DialogPane dialogPane = alert.getDialogPane();
        for ( ButtonType bt : alert.getDialogPane().getButtonTypes() )
        {
            if (bt.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)
            {
                Button cancelButton = ( Button ) alert.getDialogPane().lookupButton(bt);
                cancelButton.getStyleClass().add("btnCancel");
            } else if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Button okButton = ( Button ) alert.getDialogPane().lookupButton(bt);
                okButton.getStyleClass().add("btnApply");
            }
        }
        dialogPane.getStylesheets().add(Objects.requireNonNull(AlertCustom.class.getResource("/manage_library/assets/css/alert.css")).toString());
        dialogPane.getStyleClass().add("dialog");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get();
    }
}

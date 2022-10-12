package manage_library.util;

import com.jfoenix.controls.JFXSnackbar;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class SnackBar {
    private static AnchorPane anchorPane = (AnchorPane) Navigator.getInstance().getStage().getScene().lookup("#snackbarAnchorPane");

    public static void show(String action, String s) {
        JFXSnackbar.SnackbarEvent snackbarEvent = new JFXSnackbar.SnackbarEvent(new Label(s), Duration.seconds(3), null);
        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
        snackbar.setPrefWidth(600);
        snackbar.getStyleClass().add("snackbar");
        switch (action) {
            case Constant.SnackBarAction.WARNING:
                snackbar.getStyleClass().add("snackbar-warning");
                break;

            case Constant.SnackBarAction.ERROR:
                snackbar.getStyleClass().add("snackbar-error");
                break;

            case Constant.SnackBarAction.SUCCESS:
                snackbar.getStyleClass().add("snackbar-success");
                break;

            default:
                break;
        }
        snackbar.enqueue(snackbarEvent);
    }
}

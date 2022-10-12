package manage_library;

import javafx.application.Application;
import javafx.stage.Stage;
import manage_library.controller.user_controller.LoggedInController;
import manage_library.database.DatabaseHandler;
import manage_library.util.Navigator;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        DatabaseHandler.getInstance().initDatabase();
        DatabaseHandler.getInstance().getAnnounceSendReader();
        Navigator.getInstance().setStage(primaryStage);
        Navigator.getInstance().getStage().setTitle("Library");
        if (new LoggedInController().checkRememberUser()) {
            Navigator.getInstance().redirectTo(Navigator.LAYOUT);
        } else {
            Navigator.getInstance().redirectTo(Navigator.LOGIN);
        }
        Navigator.getInstance().getStage().show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

package manage_library.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import manage_library.controller.borrow_controller.CreateOrUpdateBorrowController;
import manage_library.controller.reader_controller.CreateOrUpdateReaderController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Navigator {
    public static final String ROOT_SCROLL_ID = "#rootScroll";

    public static final String LAYOUT = "/manage_library/view/layout.fxml";
    public static final String LOGIN = "/manage_library/view/user_view/logIn.fxml";

    public static final String CREATE_READER = "/manage_library/view/reader_view/createOrUpdateReader.fxml";
    public static final String LIST_READER = "/manage_library/view/reader_view/listReader.fxml";

    public static final String LIST_BORROW = "/manage_library/view/borrow_view/listBorrow.fxml";
    public static final String CREATE_BORROW = "/manage_library/view/borrow_view/createOrUpdateBorrow.fxml";
    public static final String ADD_BOOK_BORROW = "/manage_library/view/borrow_view/addBookBorrowComponent.fxml";


    public static final String RESET_PASSWORD = "/manage_library/view/user_view/resetPassword.fxml";
    public static final String TWO_AUTH = "/manage_library/view/user_view/twoAuth.fxml";

    public static final String CRUD_USER = "/manage_library/view/user_view/createOrUpdateUser.fxml";
    public static final String CRUD_BOOK = "/manage_library/view/book_view/createOrUpdateBook.fxml";
    public static final String LIST_USER = "/manage_library/view/user_view/listUsers.fxml";
    public static final String LIST_BOOK = "/manage_library/view/book_view/listBooks.fxml";
    public static final String CHANGE_PASSWORD = "/manage_library/view/user_view/changePassword.fxml";

    public static final String REPORT_REVENUE = "/manage_library/view/report_view/revenueReport.fxml";

    private static Navigator navigator;
    private Stage stage;
    private FXMLLoader loader;
    private String currentScreen = "";
    private final List<String> screenSmall = Arrays.asList(LOGIN, TWO_AUTH, RESET_PASSWORD, CHANGE_PASSWORD);

    public static Navigator getInstance() {
        if (navigator == null) {
            navigator = new Navigator();
        }
        return navigator;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getCurrentScreen() {
        return currentScreen;
    }

    public Stage getStage() {
        return this.stage;
    }
    public void redirectTo(String fxml) throws IOException {
        currentScreen = fxml;
        changeRangeScreenSize(fxml);

        this.loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxml));
        Parent root = loader.load();
        if (screenSmall.contains(fxml)) {
            this.stage.setScene(new Scene(root, Constant.SizeScreen.SCREEN_SMALL_WIDTH, Constant.SizeScreen.SCREEN_SMALL_HEIGHT));
        } else {
            this.stage.setScene(new Scene(root));
        }
    }

    public void redirectTo(String fxml, ScrollPane rootAnchorPane) throws IOException {
        currentScreen = fxml;
        AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        changeRangeScreenSize(fxml);
        setSizeScreen(rootAnchorPane, anchorPane);
        rootAnchorPane.setContent(anchorPane);
    }

    public void redirectTo(String fxml, ScrollPane rootAnchorPane, int id, String action) throws IOException {
        currentScreen = fxml;

        AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        switch (fxml) {
            case CREATE_READER:
                CreateOrUpdateReaderController.setReaderId(id);
                if (action.equals(Constant.NAVIGATION_REDIRECT_ACTION.VIEW.name())) {
                    CreateOrUpdateReaderController.setRedirectAction(Constant.NAVIGATION_REDIRECT_ACTION.VIEW.name());
                } else if (action.equals(Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name())) {
                    CreateOrUpdateReaderController.setRedirectAction(Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name());
                }
                break;

            case CREATE_BORROW:
                CreateOrUpdateBorrowController.setSelectedReaderCode(id);
                if (action.equals(Constant.NAVIGATION_REDIRECT_ACTION.VIEW.name())) {
                    CreateOrUpdateBorrowController.setRedirectAction(Constant.NAVIGATION_REDIRECT_ACTION.VIEW.name());
                } else if (action.equals(Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name())) {
                    CreateOrUpdateBorrowController.setRedirectAction(Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name());
                }
                break;

            case CRUD_USER:
                if (action.equals(Constant.NAVIGATION_REDIRECT_ACTION.VIEW.name())) {
                    CreateOrUpdateReaderController.setRedirectAction(Constant.NAVIGATION_REDIRECT_ACTION.VIEW.name());
                } else if (action.equals(Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name())) {
                    CreateOrUpdateReaderController.setRedirectAction(Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name());
                }
                break;
            default:
                break;
        }
        changeRangeScreenSize(fxml);
        setSizeScreen(rootAnchorPane, anchorPane);
        rootAnchorPane.setContent(anchorPane);

    }

    private void setSizeScreen(ScrollPane rootAnchorPane, AnchorPane anchorPane) {
        ScrollPane scrollPane = (ScrollPane) this.stage.getScene().lookup("#rootScroll");
        scrollPane.setVvalue(0);

        rootAnchorPane.setPrefWidth(this.stage.getWidth() - Constant.SizeScreen.SCREEN_NAV_WIDTH);
        rootAnchorPane.setPrefHeight(this.stage.getHeight() - Constant.SizeScreen.SCREEN_HEADER_HEIGHT);
        anchorPane.setPrefWidth(rootAnchorPane.getPrefWidth() - Constant.SizeScreen.SCREEN_PADDING_WIDTH);
        anchorPane.setPrefHeight(rootAnchorPane.getPrefHeight() - Constant.SizeScreen.SCREEN_PADDING_HEIGHT);
    }

    private void changeRangeScreenSize(String fxml) {
        if (!screenSmall.contains(fxml)) {
            this.stage.setMaximized(true);
            this.stage.setMinWidth(800);
            this.stage.setMinHeight(600);
        } else  {
            this.stage.setMaximized(false);
            this.stage.setMinWidth(Constant.SizeScreen.SCREEN_SMALL_WIDTH);
            this.stage.setWidth(Constant.SizeScreen.SCREEN_SMALL_WIDTH);
            this.stage.setMinHeight(Constant.SizeScreen.SCREEN_SMALL_HEIGHT);
            this.stage.setHeight(Constant.SizeScreen.SCREEN_SMALL_HEIGHT);
        }
    }
}

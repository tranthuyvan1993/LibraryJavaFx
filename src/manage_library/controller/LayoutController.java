package manage_library.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import manage_library.controller.user_controller.CreateOrUpdateUserController;
import manage_library.data_object.admin.AdminDto;
import manage_library.util.AlertCustom;
import manage_library.util.Constant;
import manage_library.data_object.CurrentAdmin;
import manage_library.util.Navigator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class LayoutController {
    @FXML
    private AnchorPane screenContainer;

    @FXML
    private ImageView avatarBtn;

    @FXML
    private MaterialDesignIconView settingBtn;

    @FXML
    private MaterialDesignIconView notificationBtn;

    @FXML
    private HBox manageBookBtn;

    @FXML
    private HBox manageAdminBtn;

    @FXML
    private HBox manageReaderBtn;

    @FXML
    private HBox reportBtn;

    @FXML
    private HBox manageBorrowBtn;

    @FXML
    private AnchorPane toggleActionUser;

    @FXML
    private HBox userInfoBtn;

    @FXML
    private HBox changePasswordBtn;

    @FXML
    private ScrollPane rootScroll;

    @FXML
    private HBox logoutBtn;
    @FXML
    private Label showAdminInfo;

    @FXML
    private AnchorPane snackbarAnchorPane;

    private boolean avatarActionShow = false;
    private final String CLASS_SELECTED_ITEM = "selected";
    private String avatarName;
    File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/data/info.dat");

    @FXML
    void initialize() throws IOException {
        AdminDto admin = CurrentAdmin.getInstance().getCurrentAdmin();
        avatarName = System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/" + admin.getAvatar();
        try {
            InputStream inputStream = new FileInputStream(avatarName);
            Image image = new Image(inputStream);
            inputStream.close();
            avatarBtn.setImage(image);
        } catch (Exception e) {

        }
        snackbarAnchorPane.setPickOnBounds(false);
        toggleActionUser.setVisible(false);
        screenContainer.getStylesheets().add(Objects.requireNonNull(AlertCustom.class.getResource("/manage_library/assets/css/style.css")).toString());
        checkRoleRedirectTabAdmin();
        Platform.runLater(() -> {
            try {
                Navigator.getInstance().redirectTo(checkRoleScreenAdmin(), rootScroll);
                selectCurrentTabScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        manageBookBtn.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.LIST_BOOK, rootScroll);
                selectCurrentTabScreen();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        manageAdminBtn.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.LIST_USER, rootScroll);
                selectCurrentTabScreen();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        manageReaderBtn.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.LIST_READER, rootScroll);
                selectCurrentTabScreen();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        reportBtn.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.REPORT_REVENUE, rootScroll);
                selectCurrentTabScreen();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        manageBorrowBtn.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.LIST_BORROW, rootScroll);
                selectCurrentTabScreen();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        if (admin != null) {
            showAdminInfo.setText(admin.getFullname());
        }
        userInfoBtn.setOnMouseClicked(event -> {
           showUserInfo();
        });
        logoutBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleLogout());
        changePasswordBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> changePassword());

        AnchorPane anchorPane = FXMLLoader.load((Objects.requireNonNull(getClass().getResource(checkRoleScreenAdmin()))));
        rootScroll.setContent(anchorPane);

        screenContainer.setOnMouseClicked(e -> {
            if (e.getPickResult() != null && e.getPickResult().getIntersectedNode() != null) {
                if (e.getPickResult().getIntersectedNode().getId() != null && e.getPickResult().getIntersectedNode().getId().equals("avatarBtn")) {
                    avatarActionShow = !avatarActionShow;
                    toggleActionUser.getStyleClass().add("show");
                    toggleActionUser.setVisible(avatarActionShow);
                } else {
                    avatarActionShow = false;
                    toggleActionUser.setVisible(false);
                    toggleActionUser.getStyleClass().remove("show");
                }
            }
        });
    }

    private void handleLogout() {
        ButtonType buttonType = AlertCustom.show(Alert.AlertType.CONFIRMATION, "Đăng xuất", "Đăng xuất người dùng", "Bạn có muốn đăng xuất không?");
        if (buttonType == ButtonType.OK) {
            try {
                file.delete();
                Thread.sleep(1000);
                Navigator.getInstance().redirectTo(Navigator.LOGIN);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String checkRoleScreenAdmin() {

        switch (CurrentAdmin.getInstance().getCurrentAdmin().getRole()) {
            case Constant.Role.SUPER_ADMIN:
            case Constant.Role.ADMIN:
                return Navigator.LIST_BOOK;

            case Constant.Role.ADMIN_MANAGER:
                return Navigator.LIST_USER;

            case Constant.Role.READER_MANAGER:
                return Navigator.LIST_READER;

            default:
                return null;
        }
    }

    private void changePassword() {
        ButtonType buttonType = AlertCustom.show(Alert.AlertType.CONFIRMATION, "Đổi mật khẩu", null, "Bạn có muốn đổi mật khẩu không?");
        if (buttonType == ButtonType.OK) {
            try {
                Navigator.getInstance().redirectTo(Navigator.CHANGE_PASSWORD);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void checkRoleRedirectTabAdmin() {

        switch (CurrentAdmin.getInstance().getCurrentAdmin().getRole()) {
            case Constant.Role.ADMIN_MANAGER:
                manageReaderBtn.setVisible(false);
                manageReaderBtn.setManaged(false);
                reportBtn.setVisible(false);
                reportBtn.setManaged(false);
                manageBorrowBtn.setManaged(false);
                manageBorrowBtn.setVisible(false);
                manageBookBtn.setManaged(false);
                manageBookBtn.setVisible(false);
                break;
            case Constant.Role.READER_MANAGER:
                manageAdminBtn.setVisible(false);
                manageAdminBtn.setManaged(false);
                break;

            default:
                break;
        }
    }
    private void showUserInfo() {
        AdminDto adminDto = CurrentAdmin.getInstance().getCurrentAdmin();
        CreateOrUpdateUserController.setAdminId(adminDto.getId());
        if (adminDto.getRole() != null) {
            try {
                Navigator.getInstance().redirectTo(Navigator.CRUD_USER, rootScroll);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void selectCurrentTabScreen() throws IOException {
        switch (Navigator.getInstance().getCurrentScreen()) {
            case Navigator.LIST_BOOK:
                manageAdminBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageReaderBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                reportBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBorrowBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBookBtn.getStyleClass().add(CLASS_SELECTED_ITEM);
                break;

            case Navigator.LIST_USER:
                manageReaderBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                reportBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBookBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBorrowBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageAdminBtn.getStyleClass().add(CLASS_SELECTED_ITEM);
                break;

            case Navigator.LIST_READER:
                manageAdminBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBookBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBorrowBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                reportBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageReaderBtn.getStyleClass().add(CLASS_SELECTED_ITEM);
                break;

            case Navigator.LIST_BORROW:
                manageAdminBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBookBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageReaderBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                reportBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBorrowBtn.getStyleClass().add(CLASS_SELECTED_ITEM);
                break;

            case Navigator.REPORT_REVENUE:
                manageAdminBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBookBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageReaderBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                manageBorrowBtn.getStyleClass().removeIf(e -> e.equals(CLASS_SELECTED_ITEM));
                reportBtn.getStyleClass().add(CLASS_SELECTED_ITEM);
                break;

            default:
                break;
        }
    }
}

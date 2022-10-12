package manage_library.controller.user_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import manage_library.data_object.CurrentAdmin;
import manage_library.data_object.admin.Admin;
import manage_library.data_object.admin.AdminDto;
import manage_library.model.AdminModel;
import manage_library.util.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class CreateOrUpdateUserController {
    @FXML
    private Label titleCrudUser;
    @FXML
    private ImageView userAvatar;
    @FXML
    private Label labelClickNone;
    @FXML
    private ChoiceBox<String> cbRole;
    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfFullname;
    @FXML
    private TextField tfPassword;
    @FXML
    private TextField tfConfirmPassword;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhone;
    @FXML
    private JFXToggleButton tbStatus;
    @FXML
    private CheckBox userTwoAuth;
    @FXML
    private JFXButton btnAddUser;
    @FXML
    private FontAwesomeIconView backBtn;
    @FXML
    private Label userUsernameError;

    @FXML
    private Label userFullnameError;

    @FXML
    private Label userPassError;

    @FXML
    private Label userConfirmPassError;

    @FXML
    private Label userEmailError;

    @FXML
    private Label userPhoneError;
    @FXML
    private Label labelStatus;
    @FXML
    private Label labelRole;
    @FXML
    private Label labelConfirmPass;
    @FXML
    private Label labelPass;
    private static Integer adminId = null;
    private static String avatarName = "";
    Pattern patternEmail = Pattern.compile(Constant.RegexForm.REGEX_EMAIL);
    Pattern patternPhone = Pattern.compile(Constant.RegexForm.REGEX_PHONE);
    Pattern patternPass = Pattern.compile(Constant.RegexForm.REGEX_PASS);
    Pattern patternUsername = Pattern.compile(Constant.RegexForm.REGEX_USERNAME);
    Pattern patternFullname = Pattern.compile(Constant.RegexForm.REGEX_FULLNAME);
    @FXML
    private String[] roles = {"ADMIN", "READER_MANAGER", "ADMIN_MANAGER"};
    String role = null;
    private File file;
    String oldAvatar = null;

    public static void setAdminId(Integer adminId) {
        CreateOrUpdateUserController.adminId = adminId;
    }

    public static void getAdminAvatar(String avatarName) {
        CreateOrUpdateUserController.avatarName = avatarName;
    }

    @FXML
    void initialize() {
        labelClickNone.setPickOnBounds(false);
        userAvatar.setOnMouseClicked(e -> {
            try {
                file = HandleFile.handleSelectAvatar(userAvatar, labelClickNone);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        if (adminId != null) {
            String sql = "select * from admin where id = " + adminId + " and status != - 1";
            AdminDto admin = AdminModel.findAdminById(sql);
            getAdminAvatar(admin.getAvatar());
            tfFullname.setText(admin.getFullname());
            tfUsername.setText(admin.getUsername());
            tfUsername.setDisable(true);
            tfEmail.setDisable(true);
            tfEmail.setText(admin.getEmail());
            tfPhone.setText(admin.getPhone());
            cbRole.setDisable(false);
            cbRole.setVisible(false);
            tbStatus.setVisible(false);
            labelStatus.setVisible(false);
            labelConfirmPass.setVisible(false);
            labelPass.setVisible(false);
            tfPassword.setVisible(false);
            tfConfirmPassword.setVisible(false);
            labelRole.setVisible(false);
            oldAvatar = System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/" + admin.getAvatar();
            try {
                InputStream inputStream = new FileInputStream(oldAvatar);
                Image image = new Image(inputStream);
                inputStream.close();
                userAvatar.setImage(image);
                labelClickNone.setText(null);
            } catch (Exception e) {
                SnackBar.show(Constant.SnackBarAction.ERROR, "Ảnh đã bị xóa hoặc không tồn tại. Vui lòng tải ảnh mới!");
            }
            if (admin.getTwo_auth() == 1) {
                userTwoAuth.setSelected(true);
            } else {
                userTwoAuth.setSelected(false);
            }
            btnAddUser.setText("CẬP NHẬT");
            titleCrudUser.setText("Cập nhật thông tin người dùng");
            tfFullname.setOnKeyReleased(e -> {
                try {
                    validForm(null);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            tfPhone.setOnKeyReleased(e -> {
                try {
                    validForm(null);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        } else {
            btnAddUser.setDisable(true);
            cbRole.setValue(roles[0]);
            tbStatus.setSelected(true);
            tfFullname.focusedProperty().addListener((ov, oldValue, newValue) -> {
                try {
                    validForm(Constant.FormField.FULLNAME);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            tfUsername.focusedProperty().addListener((ov, oldValue, newValue) -> {
                try {
                    validForm(Constant.FormField.USER_NAME);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            tfEmail.focusedProperty().addListener((ov, oldValue, newValue) -> {
                try {
                    validForm(Constant.FormField.EMAIL_USER);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            tfPhone.setOnKeyReleased(e -> {
                try {
                    validForm(Constant.FormField.PHONE);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            tfPassword.setOnKeyReleased(e -> {
                try {
                    validForm(Constant.FormField.PASSWORD);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            tfConfirmPassword.setOnKeyReleased(e -> {
                try {
                    validForm("CONFIRMPASS");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }
        cbRole.getItems().addAll(roles);
        cbRole.setOnAction(this::getRolevalue);
        backBtn.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.LIST_USER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                adminId = null;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        btnAddUser.setOnMouseClicked(e -> {
            String username = tfUsername.getText();
            String fullname = tfFullname.getText();
            String phone = tfPhone.getText();
            String email = tfEmail.getText();
            String password = tfPassword.getText();
            role = cbRole.getValue();
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            if (adminId == null) {
                if (file != null) {
                    try {
                        String newNameFile = HandleFile.saveImage(file, tfPhone.getText(), oldAvatar);
                        Admin admin = new Admin(fullname, username, phone, hashPassword, email, role, tbStatus.isSelected() ? 1 : 0,
                                userTwoAuth.isSelected() ? 1 : 0, newNameFile);
                        AdminModel.insertAdmin(admin);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    SnackBar.show(Constant.SnackBarAction.ERROR, "Bạn cần tải ảnh đại diện!");
                }
            } else {
                String roleCurrentAdmin = CurrentAdmin.getInstance().getCurrentAdmin().getRole();
                int status, two_auth;
                if (tbStatus.isSelected()) {
                    status = 1;
                } else {
                    status = 0;
                }
                if (userTwoAuth.isSelected()) {
                    two_auth = 1;
                } else {
                    two_auth = 0;
                }
                role = cbRole.getValue();
                if (file != null || !avatarName.trim().equals("")) {
                    String newNameFile = null;
                    if (!avatarName.trim().equals("") && file == null) {
                        newNameFile = this.avatarName;
                    } else if (file != null) {
                        try {
                            newNameFile = HandleFile.saveImage(file, phone, avatarName);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    String sql = "UPDATE admin SET fullname = '" + fullname +
                            "' , phone = '" + phone +
                            "' , avatar = '" + newNameFile +
                            "' , two_auth = '" + two_auth +
                            "' WHERE id = " + adminId;
                    AdminModel.updateUser(sql, roleCurrentAdmin);
                    adminId = null;
                } else {
                    ButtonType button = AlertCustom.show(Alert.AlertType.INFORMATION, "Tải ảnh đại diện", null, "Bạn cần tải ảnh đại diện!");
                    if (button == ButtonType.OK) {
                        return;
                    }
                }
            }
        });
    }

    private void validForm(String field) throws SQLException {
        if (adminId == null) {
            switch (field) {
                case Constant.FormField.EMAIL_USER:
                    userEmailError.setText(Validation.validationField(Constant.FormField.EMAIL_USER, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN, Constant.Validator.DUPLICATE), tfEmail.getText(), patternEmail, btnAddUser));
                    break;
                case Constant.FormField.USER_NAME:
                    userUsernameError.setText(Validation.validationField(Constant.FormField.USER_NAME, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN, Constant.Validator.DUPLICATE), tfUsername.getText(), patternUsername, btnAddUser));
                    break;
                case Constant.FormField.PHONE:
                    userPhoneError.setText(Validation.validationField(Constant.FormField.PHONE, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), tfPhone.getText(), patternPhone, btnAddUser));
                    break;

                case Constant.FormField.FULLNAME:
                    userFullnameError.setText(Validation.validationField(Constant.FormField.FULLNAME, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), tfFullname.getText(), patternFullname, btnAddUser));
                    break;

                case Constant.FormField.PASSWORD:
                    userPassError.setText(Validation.validationField(Constant.FormField.PASSWORD, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), tfPassword.getText(), patternPass, btnAddUser));
                    break;
                case "CONFIRMPASS":
                    if (!tfPassword.getText().equals(tfConfirmPassword.getText())) {
                        userConfirmPassError.setText("Password không khớp!");
                    } else {
                        userConfirmPassError.setText(null);
                    }
                    break;

                default:
                    break;
            }

            if (userFullnameError.getText() == null
                    && userUsernameError.getText() == null
                    && userPassError.getText() == null
                    && userEmailError.getText() == null
                    && userPhoneError.getText() == null
                    && userConfirmPassError.getText() == null) {
                btnAddUser.setDisable(false);
            }
        } else {
            userPhoneError.setText(Validation.validationField(Constant.FormField.PHONE, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), tfPhone.getText(), patternPhone, btnAddUser));
            userFullnameError.setText(Validation.validationField(Constant.FormField.FULLNAME, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), tfFullname.getText(), patternFullname, btnAddUser));
            if (userFullnameError.getText() == null
                    && userPhoneError.getText() == null) {
                btnAddUser.setDisable(false);
            }
        }
    }

    private String getRolevalue(ActionEvent event) {
        role = cbRole.getValue();
        return role;
    }

}
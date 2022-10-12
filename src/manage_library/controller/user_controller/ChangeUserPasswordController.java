package manage_library.controller.user_controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import manage_library.data_object.CurrentAdmin;
import manage_library.data_object.CurrentAdminPass;
import manage_library.data_object.admin.Admin;
import manage_library.data_object.admin.AdminDto;
import manage_library.model.AdminModel;
import manage_library.util.Constant;
import manage_library.util.Navigator;
import manage_library.util.Validation;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ChangeUserPasswordController {

    @FXML
    private TextField currentPassword;

    @FXML
    private TextField confirmNewPass;

    @FXML
    private JFXButton btnChangePassword;

    @FXML
    private Label confirmPassError;

    @FXML
    private JFXButton btnHomeScreen;

    @FXML
    private TextField newPassword;

    @FXML
    private Label newPassCheck;

    @FXML
    private Label currentPassError;
    @FXML
    private PasswordField currenPassHide;

    @FXML
    private PasswordField newPassHide;

    @FXML
    private PasswordField confirmPassHide;

    @FXML
    private CheckBox showCurrentPass;

    @FXML
    private CheckBox showNewPass;

    @FXML
    private CheckBox showConfirmPass;
    Pattern patternPass = Pattern.compile(Constant.RegexForm.REGEX_PASS);
    @FXML
    void initialize() throws Exception{
        Admin admin = CurrentAdminPass.getInstance().getCurrentAdminPass();
        AdminDto admin1 = CurrentAdmin.getInstance().getCurrentAdmin();
        btnChangePassword.setDisable(true);
        showCurrentPass.setOnMouseClicked(event1 -> {
            this.changeVisibilityPass(null, "showCurrentPass");
        });
        btnHomeScreen.setOnMouseClicked(event -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.LAYOUT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        showNewPass.setOnMouseClicked(event1 -> {
            this.changeVisibilityPass(null, "showNewPass");
        });
        showConfirmPass.setOnMouseClicked(event1 -> {
            this.changeVisibilityPass(null, "showConfirmPass");
        });
        newPassHide.setOnKeyReleased(event -> {
            try {
                newPassCheck.setText(Validation.validationField(Constant.FormField.PASSWORD, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), newPassHide.getText(), patternPass, btnChangePassword));
                if (newPassCheck.getText()==null){
                    btnChangePassword.setDisable(false);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        newPassword.setOnKeyReleased(event -> {
            try {
                newPassCheck.setText(Validation.validationField(Constant.FormField.PASSWORD, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), newPassword.getText(), patternPass, btnChangePassword));
                if (newPassCheck.getText()==null){
                    btnChangePassword.setDisable(false);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        if (currentPassword.getText().equals("")||currenPassHide.getText().equals("")||newPassword.getText().equals("")
                ||newPassHide.getText().equals("")){
            btnChangePassword.setDisable(true);
        }else {
            btnChangePassword.setDisable(false);
        }
        btnChangePassword.setOnMouseClicked(e -> {
            String currentPasswordText = currentPassword.getText();
            String currentPassHideText = currenPassHide.getText();
            String newPasswordText = newPassword.getText();
            String newPassHideText = newPassHide.getText();
            String confirmNewPassText = confirmNewPass.getText();
            String confirmPassHideText = confirmPassHide.getText();
            if (newPasswordText.trim().equals("")){
                newPassword.setText(newPassHideText);
            }if (confirmNewPassText.trim().equals("")){
                confirmNewPass.setText(confirmPassHideText);
            }if (newPassHideText.trim().equals("")){
                newPassHide.setText(newPasswordText);
            }if (confirmPassHideText.trim().equals("")){
                confirmPassHide.setText(confirmNewPassText);
            }
                if (newPassword.getText().equals(confirmNewPass.getText()) || newPassHide.getText().equals(confirmNewPass.getText())
                        || newPassword.getText().equals(confirmPassHide.getText()) || newPassHide.getText().equals(confirmPassHide.getText())) {
                    confirmPassError.setText(null);
                    if (BCrypt.checkpw(currentPasswordText, admin.getPassword())
                            || BCrypt.checkpw(currentPassHideText, admin.getPassword())){
                        currentPassError.setText(null);
                    }else {
                        currentPassError.setText("Mật khẩu hiện tại không đúng");
                    }
                } else {
                    confirmPassError.setText("Mật khẩu không khớp!");

                }
            if (currentPassError.getText() == null
                    && confirmPassError.getText() == null
                    && newPassCheck.getText() == null) {
                AdminModel.changeAdminPassword(admin1.getUsername(), newPasswordText);
            }else return;
        });
    }
    private void changeVisibilityPass(ActionEvent event, String s){
        if (s.equals("showCurrentPass")){
            if (showCurrentPass.isSelected()){
                currentPassword.setText(currenPassHide.getText());
                currentPassword.setVisible(true);
                currenPassHide.setVisible(false);
                return;
            }else {
                currenPassHide.setText(currentPassword.getText());
                currenPassHide.setVisible(true);
                currentPassword.setVisible(false);
            }
        }
        else if (s.equals("showNewPass")){
            if (showNewPass.isSelected()){
                newPassword.setText(newPassHide.getText());
                newPassword.setVisible(true);
                newPassHide.setVisible(false);
                return;
            }else {
                newPassHide.setText(newPassword.getText());
                newPassHide.setVisible(true);
                newPassword.setVisible(false);
            }
        }
        else if (s.equals("showConfirmPass")){
            if (showConfirmPass.isSelected()){
                confirmNewPass.setText(confirmPassHide.getText());
                confirmNewPass.setVisible(true);
                confirmPassHide.setVisible(false);
                return;
            }else {
                confirmPassHide.setText(confirmNewPass.getText());
                confirmPassHide.setVisible(true);
                confirmNewPass.setVisible(false);
            }
        }

    }
}

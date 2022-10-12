package manage_library.controller.user_controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;
import manage_library.data_object.CurrentAdmin;
import manage_library.data_object.CurrentAdminPass;
import manage_library.data_object.admin.Admin;
import manage_library.data_object.admin.LoginForm;
import manage_library.model.AdminModel;
import manage_library.util.*;
import manage_library.util.email.EmailHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;


import javax.mail.MessagingException;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LoggedInController implements Initializable, Serializable {
    @FXML
    private AnchorPane screenContainer;

    @FXML
    private Button btnLogin;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField hidenPass;
    @FXML
    private TextField tfPassword;
    @FXML
    private Label loginNotice;
    @FXML
    private Button btnResetPassword;
    @FXML
    private CheckBox cbShowPass;
    @FXML
    private CheckBox cbRememberLogin;

    File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/data/info.dat");
    LoginForm loginForm = new LoginForm();
    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        tfUsername.setText(loginForm.getUsername());
//        tfPassword.setText(loginForm.getPassword());
        cbShowPass.setOnMouseClicked(event1 -> {
            this.changeVisibilityPass(null);
        });
//        cbRememberLogin.setOnMouseClicked(event1 -> {
//        });

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username = tfUsername.getText();
                String hidenPassText = hidenPass.getText();
                String password = tfPassword.getText();
                Admin admin = AdminModel.login(username);
                if (admin == null) {
                    loginNotice.setText("Tài khoản không tồn tại!");
                } else {
                    if (BCrypt.checkpw(hidenPassText, admin.getPassword()) || BCrypt.checkpw(password, admin.getPassword())) {
                        CurrentAdmin.getInstance().getAdminInfo(username);
//                        CurrentAdminPass.getInstance().getAdminPassword(username);
                        if (admin.getStatus() != Constant.Status.ACTIVE) {
                            loginNotice.setText("Tài khoản chưa được kích hoạt!");
                        } else {
                            if (cbRememberLogin.isSelected()) {
                                try {
                                    rememberUser();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (admin.getTwo_auth() == 1) {
                                try {
                                    String characters = "0123456789";
                                    String code = RandomStringUtils.random(6, characters);
                                    new EmailHandler(Constant.ActionEmail.TWO_AUTH, admin.getEmail(), code).start();
                                    VerifyEmailCode.getInstance().checkCodeEmail(code);
                                    Navigator.getInstance().redirectTo(Navigator.TWO_AUTH);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                try {
                                    Navigator.getInstance().redirectTo(Navigator.LAYOUT);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    } else {
                        loginNotice.setText("Sai mật khẩu, vui lòng kiểm tra lại!");
                    }
                }
            }
        });
        btnResetPassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.CONFIRMATION, "Reset mật khẩu", "Reset mật khẩu", "Bạn có muốn reset mật khẩu không?");
                if (buttonType == ButtonType.OK) {
                    try {
                        Navigator.getInstance().redirectTo(Navigator.RESET_PASSWORD);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    private void changeVisibilityPass(ActionEvent e) {
        if (cbShowPass.isSelected()) {
            tfPassword.setText(hidenPass.getText());
            tfPassword.setVisible(true);
            hidenPass.setVisible(false);
            return;
        }
        hidenPass.setText(tfPassword.getText());
        hidenPass.setVisible(true);
        tfPassword.setVisible(false);
    }

    private void rememberUser() throws IOException {
        if (!Validation.textNotEmpty(tfPassword.getText())) {
            tfPassword.setText(hidenPass.getText());
        } else if (!Validation.textNotEmpty(hidenPass.getText())) {
            hidenPass.setText(tfPassword.getText());
        }
        loginForm = new LoginForm(tfUsername.getText(), tfPassword.getText());
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/data/info.dat"))){
            objectOutputStream.writeObject(loginForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkRememberUser() throws IOException {
        if (file.exists()) {
            try (ObjectInputStream data = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/data/info.dat"))) {
                loginForm = (LoginForm) data.readObject();
                Admin admin = AdminModel.login(loginForm.getUsername());
                if (admin.getStatus() != 1) {
                    return false;
                }
                if (BCrypt.checkpw(loginForm.getPassword(), admin.getPassword())) {
                    CurrentAdmin.getInstance().getAdminInfo(loginForm.getUsername());
//                    CurrentAdminPass.getInstance().getAdminPassword(loginForm.getUsername());
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
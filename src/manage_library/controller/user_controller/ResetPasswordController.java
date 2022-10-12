package manage_library.controller.user_controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import manage_library.data_object.admin.Admin;
import manage_library.model.AdminModel;
import manage_library.util.AlertCustom;
import manage_library.util.Navigator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ResetPasswordController implements Initializable {
    @FXML
    private Label resetPassNotice;
    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfEmail;
    @FXML
    private Button btnResetPassword;
    @FXML
    private Button btnLoginScreen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnResetPassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username = tfUsername.getText();
                String email = tfEmail.getText();
                Admin admin = AdminModel.login(username);
                if (admin==null){
                    resetPassNotice.setText("Tài khoản không tồn tại");
                    tfUsername.setText("");
                    tfEmail.setText("");
                }else {
                    if (email.equals(admin.getEmail())){
                        AdminModel.updateAdminPasswordByEmail(username, email);
                        tfUsername.setText("");
                        tfEmail.setText("");
                    }else {
                        resetPassNotice.setText("Email không đúng!");
                        tfUsername.setText("");
                        tfEmail.setText("");
                    }
                }
            }
        });
        btnLoginScreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.CONFIRMATION, "Trang đăng nhập", "Trang đăng nhập", "Bạn có muốn chuyển đến trang đăng nhập không?");
                if(buttonType == ButtonType.OK){
                    try {
                        Navigator.getInstance().redirectTo(Navigator.LOGIN);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}

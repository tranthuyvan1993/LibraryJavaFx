package manage_library.controller.user_controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import manage_library.util.Navigator;
import manage_library.util.VerifyEmailCode;

import java.io.IOException;

public class TwoAuthController {
    @FXML
    private TextField tfcheckTwoAuth;
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnLoginScreen;
    @FXML
    private Label checkCodeNotice;
    @FXML
    public void initialize(){
        String code = VerifyEmailCode.getInstance().code();
        btnSubmit.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            String checkTwoAuth = tfcheckTwoAuth.getText();
            if (code!=null){
                if (code.equals(checkTwoAuth)){
                    try {
                        Navigator.getInstance().redirectTo(Navigator.LAYOUT);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    tfcheckTwoAuth.setText("");
                    checkCodeNotice.setText("Sai mã bảo mật!");
                }
            }else {
                checkCodeNotice.setText("Không thể gửi mã, hãy thử lại!");
                return;
            }

        });
        btnLoginScreen.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.LOGIN);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}

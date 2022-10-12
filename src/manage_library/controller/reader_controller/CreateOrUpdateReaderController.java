package manage_library.controller.reader_controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import manage_library.data_object.borrow.BorrowDetail;
import manage_library.data_object.reader.Reader;
import manage_library.data_object.reader.ReaderDetail;
import manage_library.database.DatabaseHandler;
import manage_library.model.BorrowModel;
import manage_library.model.ReaderModel;
import manage_library.util.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Pattern;

public class CreateOrUpdateReaderController {
    @FXML
    private JFXTextField readerCode;

    @FXML
    private JFXTextField readerName;

    @FXML
    private Label titleAction;

    @FXML
    private Label readerNameError;

    @FXML
    private Label readerPhoneError;

    @FXML
    private Label readerEmailError;

    @FXML
    private Label readerAvatarError;

    @FXML
    private JFXTextField readerPhone;


    @FXML
    private JFXTextField readerEmail;

    @FXML
    private JFXTextField readerAddress;

    @FXML
    private JFXToggleButton readerStatus;

    @FXML
    private JFXRadioButton  readerMale;

    @FXML
    private JFXRadioButton  readerFemale;

    @FXML
    private JFXCheckBox readerAcceptEmail;

    @FXML
    private FontAwesomeIconView backBtn;

    @FXML
    private JFXButton addUserBtn;

    @FXML
    private ImageView readerAvatar;

    @FXML
    private Label labelClickNone;

    private static Integer readerId = null;
    private static String redirectAction = "";
    AnchorPane anchorPane = (AnchorPane) Navigator.getInstance().getStage().getScene().lookup("#snackbarAnchorPane");
    private File file;
    private String newNameFile = "";
    private String oldAvatar = null;
    private String oldAvatarName = null;
    private final BorrowModel borrowModel = new BorrowModel();
    private ReaderDetail readerDetail = new ReaderDetail();

    public static void setReaderId(Integer readerId) {
        CreateOrUpdateReaderController.readerId = readerId;
    }

    public static void setRedirectAction(String redirectAction) {
        CreateOrUpdateReaderController.redirectAction = redirectAction;
    }

    private final ReaderModel readerModel = new ReaderModel();

    Pattern patternEmail = Pattern.compile(Constant.RegexForm.REGEX_EMAIL);
    Pattern patternPhone = Pattern.compile(Constant.RegexForm.REGEX_PHONE);

    @FXML
    void initialize() {
        labelClickNone.setPickOnBounds(false);

        readerAvatar.setOnMouseClicked(e -> {
            try {
                File newFile = HandleFile.handleSelectAvatar(readerAvatar, labelClickNone);
                if (newFile != null) {
                    file = newFile;
                }
                validForm(Constant.FormField.AVATAR);
            } catch (IOException | SQLException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        readerStatus.setOnMouseClicked(e -> {
            if (readerId != null) {
                try {
                    BorrowDetail borrowDetail = borrowModel.findCurrentBorrowOfUserByCode(DatabaseHandler.getInstance().getDbConnection(), readerDetail.getCode());
                    if (borrowDetail.getBorrowAddBookItems() != null && !borrowDetail.getBorrowAddBookItems().isEmpty()) {
                        readerStatus.setSelected(true);
                        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.USER_CURRENT_BORROW_BOOK_CANNOT_BLOCK);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        Platform.runLater(() -> {
            Connection connection = null;
            try {
                connection = DatabaseHandler.getInstance().getDbConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (readerId != null) {
                readerNameError.setText(null);
                readerEmailError.setText(null);
                readerPhoneError.setText(null);
                readerDetail = readerModel.findReaderById(connection, readerId);
                readerCode.setText(readerDetail.getCode());
                readerCode.setDisable(true);
                readerName.setDisable(true);
                readerEmail.setDisable(true);
                oldAvatar = System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/" + readerDetail.getAvatar();
                oldAvatarName = readerDetail.getAvatar();

                try {
                    InputStream inputStream = new FileInputStream(oldAvatar);
                    Image image = new Image(inputStream);
                    inputStream.close();
                    readerAvatar.setImage(image);
                    labelClickNone.setText(null);
                } catch (Exception e) {
                    SnackBar.show(Constant.SnackBarAction.ERROR, Constant.FormTextError.IMAGE_NOT_AVAILABLE);
                }

                readerName.setText(readerDetail.getName());
                readerPhone.setText(readerDetail.getPhone());
                if (readerDetail.getGender() != null) {
                    readerFemale.setSelected(readerDetail.getGender().equals(Constant.Gender.FEMALE.name()));
                    readerMale.setSelected(readerDetail.getGender().equals(Constant.Gender.MALE.name()));
                }
                readerStatus.setSelected(readerDetail.getStatus() == 1);
                readerEmail.setText(readerDetail.getEmail());
                readerAddress.setText(readerDetail.getAddress());
                readerAcceptEmail.setSelected(readerDetail.getAcceptEmail());
                addUserBtn.setText("Cập nhật");
                titleAction.setText("Cập nhật Người đọc");
                if (redirectAction.equals(Constant.NAVIGATION_REDIRECT_ACTION.VIEW.name())) {
                    addUserBtn.setVisible(false);
                    readerAvatar.setDisable(true);
                    readerFemale.setDisable(true);
                    readerMale.setDisable(true);
                    readerName.setDisable(true);
                    readerAcceptEmail.setDisable(true);
                    readerAddress.setDisable(true);
                    readerEmail.setDisable(true);
                    readerPhone.setDisable(true);
                    readerStatus.setDisable(true);
                    addUserBtn.setDisable(true);
                }
            } else {
                do {
                    String randomCode = RandomStringUtils.random(8, "0123456789");
                    if (Boolean.FALSE.equals(readerModel.checkExistsByCode(connection, randomCode))) {
                        readerCode.setText(randomCode);
                    }
                } while(readerCode.getText() == null);


                readerCode.setDisable(true);
                addUserBtn.setDisable(true);
            }

            backBtn.setOnMouseClicked(e -> {
                try {
                    Navigator.getInstance().redirectTo(Navigator.LIST_READER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });

            readerName.focusedProperty().addListener((ov, oldValue, newValue) -> {
                if (Boolean.FALSE.equals(newValue)) {
                    try {
                        validForm(Constant.FormField.NAME);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
            readerPhone.focusedProperty().addListener((ov, oldValue, newValue) -> {
                if (Boolean.FALSE.equals(newValue)) {
                    try {
                        validForm(Constant.FormField.PHONE);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
            readerEmail.focusedProperty().addListener((ov, oldValue, newValue) -> {
                if (Boolean.FALSE.equals(newValue)) {
                    try {
                        validForm(Constant.FormField.EMAIL);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });


            readerName.setOnKeyReleased(e -> {
                try {
                    validForm(Constant.FormField.NAME);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            readerEmail.setOnKeyReleased(e -> {
                try {
                    validForm(Constant.FormField.EMAIL);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            readerPhone.setOnKeyReleased(e -> {
                try {
                    validForm(Constant.FormField.PHONE);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            readerMale.setOnAction(this::handleSelectedGender);
            readerFemale.setOnAction(this::handleSelectedGender);

            addUserBtn.setOnMouseClicked(e -> {
                try {
                    if (file != null || oldAvatar != null) {
                        newNameFile = file != null ? HandleFile.saveImage(file, readerPhone.getText(), oldAvatar) : oldAvatarName;
                        Reader reader = new Reader(readerId, readerCode.getText(), readerName.getText(), newNameFile, readerPhone.getText(), readerEmail.getText(), readerAddress.getText(), readerStatus.isSelected() ? 1 : 0, readerAcceptEmail.isSelected(), readerMale.isSelected() ? Constant.Gender.MALE.name() : Constant.Gender.FEMALE.name(), new Timestamp(Calendar.getInstance().getTimeInMillis()));
                        if (readerModel.createOrUpdateReader(DatabaseHandler.getInstance().getDbConnection(), reader) == 1) {
                            SnackBar.show(Constant.SnackBarAction.SUCCESS, readerId == null ? Constant.SnackBarMessage.CREATE_SUCCESS : Constant.SnackBarMessage.UPDATE_SUCCESS);
                            Navigator.getInstance().redirectTo(Navigator.LIST_READER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                        } else {
                            SnackBar.show(Constant.SnackBarAction.ERROR, readerId == null ? Constant.SnackBarMessage.CREATE_ERROR : Constant.SnackBarMessage.UPDATE_ERROR);
                        }
                    } else {
                        SnackBar.show(Constant.SnackBarAction.ERROR, "Bạn phải chọn ảnh tải lên!");
                    }

                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }
            });

        });
    }

    private void validForm(String field) throws SQLException {
        switch (field) {
            case Constant.FormField.EMAIL:
                readerEmailError.setText(Validation.validationField(Constant.FormField.EMAIL, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN, Constant.Validator.DUPLICATE), readerEmail.getText(), patternEmail, addUserBtn));
                break;

            case Constant.FormField.PHONE:
                readerPhoneError.setText(Validation.validationField(Constant.FormField.PHONE, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), readerPhone.getText(), patternPhone, addUserBtn));
                break;

            case Constant.FormField.NAME:
                readerNameError.setText(Validation.validationField(Constant.FormField.NAME, Collections.singletonList(Constant.Validator.REQUIRED), readerName.getText(), null, addUserBtn));
                break;

            case Constant.FormField.AVATAR:
                if (!Validation.textNotEmpty(newNameFile)) {
                    readerAvatarError.setText(Validation.validationField(Constant.FormField.AVATAR, Collections.singletonList(Constant.Validator.REQUIRED), file != null ? "file" : "", null, addUserBtn));

                }
                break;

            default:
                break;
        }

        if ((file != null || Validation.textNotEmpty(oldAvatarName)) && readerNameError.getText() == null && readerEmailError.getText() == null && readerPhoneError.getText() == null) {
            addUserBtn.setDisable(false);
        }

    }

    private void handleSelectedGender(Event event) {
        Object gender = event.getSource();
        if (gender.equals(readerMale)) {
            readerFemale.setSelected(false);
        } else {
            readerMale.setSelected(false);
        }
    }





}

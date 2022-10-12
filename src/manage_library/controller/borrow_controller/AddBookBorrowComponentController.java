package manage_library.controller.borrow_controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;
import manage_library.data_object.borrow.BorrowAddBookItem;
import manage_library.database.DatabaseHandler;
import manage_library.model.BorrowModel;
import manage_library.util.*;
import manage_library.util.format.CurrencyFormat;
import manage_library.util.format.DateFormat;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddBookBorrowComponentController extends JFXListCell<BorrowAddBookItem> {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label deposit;

    @FXML
    private JFXTextField bookCode;

    @FXML
    private Label bookCodeError;

    @FXML
    private Label statusLabel;

    @FXML
    private JFXTextField bookName;

    @FXML
    private JFXTextField auth;

    @FXML
    private JFXTextField price;

    @FXML
    private JFXDatePicker borrowDate;

    @FXML
    private JFXDatePicker dueDate;

    @FXML
    private JFXDatePicker returnDate;

    @FXML
    private JFXTextField noteBookDetail;

    @FXML
    private JFXTextField borrowNote;

    @FXML
    private MaterialDesignIconView removeBookBtn;

    @FXML
    private JFXRadioButton yetReturnBtn;

    @FXML
    private JFXRadioButton lateReturnBtn;

    @FXML
    private JFXRadioButton hasReturnBtn;

    @FXML
    private JFXRadioButton notReturnBtn;

    private List<JFXRadioButton> allStatusBorrow = new ArrayList<JFXRadioButton>();
    private FXMLLoader fxmlLoader;
    private final BorrowModel borrowModel = new BorrowModel();
    private BorrowAddBookItem borrowAddBookItem;

    @FXML
    void initialize() throws SQLException {
        DateFormat.formatDatePicker(dueDate, borrowDate, returnDate);

        Connection connection = DatabaseHandler.getInstance().getDbConnection();
        disableTextField();

        bookCode.focusedProperty().addListener((ov, oldValue, newValue) -> {
            if (Boolean.FALSE.equals(newValue) && (bookCode.getText() == null || bookCode.getText().trim().length() == 0)) {
                bookCodeError.setText(Constant.FormTextError.REQUIRED);
            }
        });
        bookCode.textProperty().addListener((observer, oldValue, newValue) -> {
                    if (Validation.textNotEmpty(bookCode.getText())) {
                        bookCode.setText(newValue.toUpperCase());
                    }
                }
        );

        bookCode.setOnKeyReleased(e -> {
            boolean bookExists = false;
            for (int i = 0; i < CreateOrUpdateBorrowController.getBookLists().size(); i ++) {
                if (i != getIndex() && bookCode.getText().equals(CreateOrUpdateBorrowController.getBookLists().get(i).getBookCode())) {
                    bookExists = true;
                }
            }
            if (Validation.textNotEmpty(bookCode.getText()) && bookExists) {
                bookCodeError.setText(Constant.FormTextError.BOOK_CODE_IS_EXISTS);
            } else if (bookCode.getText() != null && bookCode.getText().trim().length() >= 9) {

                borrowAddBookItem = borrowModel.findBookByCode(connection, bookCode.getText());
                if (borrowAddBookItem != null) {
                    if (borrowAddBookItem.getBookDetailStatus() == 0) {
                        bookCodeError.setText(Constant.FormTextError.BOOK_NOT_AVAILABLE);
                        CreateOrUpdateBorrowController.getBookLists().get(getIndex()).setBookCode(bookCode.getText());
                    } else {
                        bookCodeError.setText(null);
                        CreateOrUpdateBorrowController.getBookLists().set(getIndex(), borrowAddBookItem);
                    }
                }
            } else {
                if (!Validation.textNotEmpty(bookCode.getText())) {
                    bookCodeError.setText(Constant.FormTextError.REQUIRED);
                } else {
                    bookCodeError.setText(null);
                }
                CreateOrUpdateBorrowController.getBookLists().set(getIndex(), new BorrowAddBookItem(bookCode.getText()));
            }
            bookCode.end();
        });

        borrowNote.setOnKeyReleased(e -> CreateOrUpdateBorrowController.getBookLists().get(getIndex()).setBorrowNote(borrowNote.getText()));

        notReturnBtn.setOnMouseClicked(e -> selectStatusBorrow(notReturnBtn));
        yetReturnBtn.setOnMouseClicked(e -> selectStatusBorrow(yetReturnBtn));
        hasReturnBtn.setOnMouseClicked(e -> selectStatusBorrow(hasReturnBtn));
        lateReturnBtn.setOnMouseClicked(e -> selectStatusBorrow(lateReturnBtn));

        dueDate.valueProperty().addListener((ov, oldValue, newValue) -> CreateOrUpdateBorrowController.getBookLists().get(getIndex()).setDueDate(newValue));

        removeBookBtn.setOnMouseClicked(e -> {
            getListView().getId();
            getListView().getItems().remove(getItem());
            SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.REMOVE_BORROW_BOOK_SUCCESS);
        });
    }

    @SneakyThrows
    @Override
    public void updateItem(BorrowAddBookItem borrowAddBookItem, boolean empty) {
        super.updateItem(borrowAddBookItem, empty);
        if (empty || borrowAddBookItem == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource(Navigator.ADD_BOOK_BORROW));
                fxmlLoader.setController(this);
                fxmlLoader.load();
            }
            if (allStatusBorrow.isEmpty()) {
                allStatusBorrow.add(hasReturnBtn);
                allStatusBorrow.add(notReturnBtn);
                allStatusBorrow.add(yetReturnBtn);
                allStatusBorrow.add(lateReturnBtn);
            }

            fillDataBook(borrowAddBookItem);

            setText(null);
            setGraphic(anchorPane);
        }
    }
    private void selectStatusBorrow(JFXRadioButton selectedBtn) {
        if (allStatusBorrow.size() == 4) {
            allStatusBorrow.forEach(e -> {
                if (e.equals(selectedBtn)) {
                    e.setSelected(true);
                    if (getIndex() >= 0) {
                        dueDate.setDisable(true);
                        switch (e.getId()) {
                            case Constant.BorrowStatus.YET_RETURN:
                                returnDate.setValue(null);
                                CreateOrUpdateBorrowController.getBookLists().get(getIndex()).setBorrowStatus(Constant.BorrowStatusInt.YET_RETURN);
                                break;

                            case Constant.BorrowStatus.HAS_RETURN:
                                returnDate.setValue(LocalDate.now());
                                CreateOrUpdateBorrowController.getBookLists().get(getIndex()).setBorrowStatus(Constant.BorrowStatusInt.HAS_RETURN);
                                break;

                            case Constant.BorrowStatus.LATE_RETURN:
                                returnDate.setValue(null);
                                if (CreateOrUpdateBorrowController.getBookLists().size() == 1) {
                                    dueDate.setDisable(false);
                                } else {
                                    disableAllStatusBorrow();
                                }
                                CreateOrUpdateBorrowController.getBookLists().get(getIndex()).setBorrowStatus(Constant.BorrowStatusInt.LATE_RETURN);
                                break;

                            case Constant.BorrowStatus.NOT_RETURN:
                                returnDate.setValue(LocalDate.now());
                                CreateOrUpdateBorrowController.getBookLists().get(getIndex()).setBorrowStatus(Constant.BorrowStatusInt.NOT_RETURN);
                                break;

                            default:
                                break;
                        }
                    }
                } else {
                    e.setSelected(false);
                }
            });

        }
    }

    private void selectStatusBorrow(int selectedBtn) {
        switch (selectedBtn) {
            case 0:
                selectStatusBorrow(yetReturnBtn);
                break;

            case 1:
                selectStatusBorrow(hasReturnBtn);
                break;

            case 2:
                selectStatusBorrow(lateReturnBtn);
                break;

            case 3:
                selectStatusBorrow(notReturnBtn);
                break;

            default:
                break;
        }
    }

    private void disableAllStatusBorrow() {
        allStatusBorrow.forEach(e -> {
            if (e != null) e.setDisable(true);
        });
    }

    private void fillDataBook(BorrowAddBookItem borrowAddBookItem) {
        if (borrowAddBookItem.getBorrowId() != null) {
            selectStatusBorrow(borrowAddBookItem.getBorrowStatus());
            bookCode.setDisable(true);
            if (borrowAddBookItem.getBorrowStatus() != 2) dueDate.setDisable(true);
            removeBookBtn.setDisable(true);
            borrowNote.setDisable(false);

            if (borrowAddBookItem.getBorrowStatus() == 1 || borrowAddBookItem.getBorrowStatus() == 3) {
                returnDate.setValue(borrowAddBookItem.getReturnDate() != null ? borrowAddBookItem.getReturnDate() : LocalDate.now());
                borrowNote.setDisable(true);
                if (borrowAddBookItem.getBorrowStatus() == 1) {
                    selectStatusBorrow(hasReturnBtn);
                } else {
                    selectStatusBorrow(notReturnBtn);
                }
                disableAllStatusBorrow();
            }
        } else {
            selectStatusBorrow(yetReturnBtn);
            borrowNote.setDisable(true);
            returnDate.setDisable(true);
            removeBookBtn.setDisable(false);
            bookCode.setDisable(false);
            dueDate.setDisable(false);
            disableAllStatusBorrow();
            if (borrowAddBookItem.getBorrowStatus() == 1 || borrowAddBookItem.getBorrowStatus() == 3) returnDate.setValue(borrowAddBookItem.getReturnDate());
        }

        bookCode.setText(borrowAddBookItem.getBookCode());
        bookName.setText(borrowAddBookItem.getBookName());
        auth.setText(borrowAddBookItem.getAuth());
        price.setText(CurrencyFormat.simpleCurrencyFormat(borrowAddBookItem.getPrice()));
        String pathImage = Validation.textNotEmpty(borrowAddBookItem.getImage()) ? borrowAddBookItem.getImage() : "book_default.jpg";
        String imageBookName = System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/bookImage/" + pathImage;
        try (FileInputStream inputStream = new FileInputStream(imageBookName)) {
            bookImage.setImage(new Image(inputStream));
            bookImage.setFitWidth(250);
            bookImage.setFitHeight(325);
        } catch (Exception e) {
            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.FormTextError.IMAGE_NOT_AVAILABLE);
        }

        borrowDate.setValue(borrowAddBookItem.getBorrowDate() != null ? borrowAddBookItem.getBorrowDate() : LocalDate.now());
        dueDate.setValue(borrowAddBookItem.getDueDate() != null ? borrowAddBookItem.getDueDate() : (borrowAddBookItem.getBorrowDate() != null ? borrowAddBookItem.getBorrowDate().plusDays(Constant.PlusDayDueDate.PLUS_DAY) : LocalDate.now().plusDays(Constant.PlusDayDueDate.PLUS_DAY)));
        noteBookDetail.setText(borrowAddBookItem.getNote());
        borrowNote.setText(borrowAddBookItem.getBorrowNote());
        deposit.setText(CurrencyFormat.ceilCurrencyFormat(borrowAddBookItem.getPrice() != null ? borrowAddBookItem.getPrice() * 1.1 : 0.0));
    }

    private void disableTextField() {
        yetReturnBtn.setDisable(true);
        returnDate.setDisable(true);
        bookName.setDisable(true);
        removeBookBtn.setDisable(true);
        auth.setDisable(true);
        price.setDisable(true);
        borrowDate.setDisable(true);
        if (dueDate == null) {
            returnDate.setDisable(true);
        }
        noteBookDetail.setDisable(true);
    }
}

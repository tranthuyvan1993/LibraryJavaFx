package manage_library.controller.borrow_controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import manage_library.data_object.borrow.BorrowAddBookItem;
import manage_library.data_object.borrow.BorrowDetail;
import manage_library.database.DatabaseHandler;
import manage_library.model.BorrowModel;
import manage_library.util.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CreateOrUpdateBorrowController {
    @FXML
    private Label titleAction;

    @FXML
    private JFXTextField readerCode;

    @FXML
    private Label readerCodeError;

    @FXML
    private JFXTextField readerName;

    @FXML
    private JFXTextField readerPhone;

    @FXML
    private AnchorPane borrowBookContainer;

    @FXML
    private MaterialDesignIconView addBookBtn;

    @FXML
    private JFXCheckBox readerAcceptEmail;

    @FXML
    public JFXButton addBorrowBtn;

    @FXML
    private FontAwesomeIconView backBtn;

    @FXML
    private JFXListView<BorrowAddBookItem> borrowBookList;

    @FXML
    private AnchorPane addABookBtn;

    private static Integer selectedReaderCode = null;
    private List<Integer> bookOfSelectedReader = new ArrayList<>();
    AnchorPane anchorPane = (AnchorPane) Navigator.getInstance().getStage().getScene().lookup("#snackbarAnchorPane");
    private static ObservableList<BorrowAddBookItem> bookLists = FXCollections.observableArrayList();

    public static ObservableList<BorrowAddBookItem> getBookLists() {
        return bookLists;
    }

    public static void setBookLists(ObservableList<BorrowAddBookItem> bookLists) {
        CreateOrUpdateBorrowController.bookLists = bookLists;
    }

    private static String redirectAction = "";

    public static void setRedirectAction(String redirectAction) {
        CreateOrUpdateBorrowController.redirectAction = redirectAction;
    }

    public static void setSelectedReaderCode(Integer selectedReaderCode) {
        CreateOrUpdateBorrowController.selectedReaderCode = selectedReaderCode;
    }

    private final BorrowModel borrowModel = new BorrowModel();
    private BorrowDetail borrowDetail;
    private List<Integer> beforeBorrowBookDetailIds = new ArrayList<>();

    @FXML
    void initialize() {
        titleAction.setFocusTraversable(true);
        readerCode.setOnKeyReleased(e -> {
            if (Validation.textNotEmpty(readerCode.getText())) {
                try {
                    borrowDetail = borrowModel.findCurrentBorrowOfUserByCode(DatabaseHandler.getInstance().getDbConnection(), readerCode.getText());
                    readerName.setText(null);
                    readerPhone.setText(null);
                    if (borrowDetail != null) {
                        if (borrowDetail.getBorrowAddBookItems() != null) borrowDetail.getBorrowAddBookItems().forEach(borrow -> {
                            if (borrow.getBookDetailId() != null) beforeBorrowBookDetailIds.add(borrow.getBookDetailId());
                        });
                        handleIfReaderHasBook();
                    } else {
                        if (!bookOfSelectedReader.isEmpty()) {
                            borrowBookList.getItems().removeIf(book -> bookOfSelectedReader.contains(book.getBookDetailId()));
                        }
                        if (!Validation.textNotEmpty(readerCode.getText())) {
                            readerCodeError.setText(Constant.FormTextError.REQUIRED);
                        } else {
                            readerCodeError.setText(null);
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            } else {
                if (!bookOfSelectedReader.isEmpty()) {
                    borrowBookList.getItems().removeIf(book -> bookOfSelectedReader.contains(book.getBookDetailId()));
                }
                if (!Validation.textNotEmpty(readerCode.getText())) {
                    readerCodeError.setText(Constant.FormTextError.REQUIRED);
                }
            }
            readerCode.end();
            validForm();
        });

        readerCode.focusedProperty().addListener((ov, oldValue, newValue) -> {
            if (Boolean.FALSE.equals(newValue) && !Validation.textNotEmpty(readerCode.getText())) {
                readerCodeError.setText(Constant.FormTextError.REQUIRED);
                validForm();
            }
        });

        borrowBookList.setItems(bookLists);
        borrowBookList.getStylesheets().add(Objects.requireNonNull(AlertCustom.class.getResource("/manage_library/assets/css/list.css")).toString());
        borrowBookList.setCellFactory(addBookBorrowComponentController -> new AddBookBorrowComponentController());

        Platform.runLater(() -> {
            Connection connection = null;

            try {
                connection = DatabaseHandler.getInstance().getDbConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            readerPhone.setDisable(true);
            readerName.setDisable(true);
            if (selectedReaderCode != null) {
                borrowDetail = borrowModel.findAllBorrowOfUserByBorrowId(connection, selectedReaderCode);
                if (borrowDetail != null) {
                    handleIfReaderHasBook();
                    addBorrowBtn.setText("Cập nhật");
                    titleAction.setText("Cập nhật Mượn trả sách");
                    addABookBtn.setManaged(false);
                    addABookBtn.setVisible(false);
                    addBorrowBtn.setVisible(!borrowDetail.getBorrowAddBookItems().stream().allMatch(book -> book.getBorrowStatus() == 1 || book.getBorrowStatus() == 3));
                    addBorrowBtn.setManaged(!borrowDetail.getBorrowAddBookItems().stream().allMatch(book -> book.getBorrowStatus() == 1 || book.getBorrowStatus() == 3));
                }

                readerAcceptEmail.setDisable(true);
                readerCode.setDisable(true);

            } else {
                addBorrowBtn.setDisable(true);
                borrowBookList.getItems().addAll(new BorrowAddBookItem());
            }




            backBtn.setOnMouseClicked(e -> {
                try {
                    Navigator.getInstance().redirectTo(Navigator.LIST_BORROW, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });

            addBorrowBtn.setOnMouseClicked(e -> {
                try {
                    handleAddBorrow();
                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }
            });

        });

        addBookBtn.setOnMouseClicked(e -> handleAddBook());
    }

    private void handleAddBorrow() throws SQLException, IOException {
        borrowBookList.getItems().removeIf(book -> book.getBookName() == null);
        List<BorrowAddBookItem> newList = borrowBookList.getItems().stream().filter(book -> book.getReturnDate() != null || (book.getBorrowStatus() != null && (book.getBorrowStatus() == 1 || book.getBorrowStatus() == 3))).collect(Collectors.toList());

        if (borrowBookList.getItems().stream().noneMatch(book -> book.getBookName() != null)) {
            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.MUST_HAS_LEAST_A_BOOK_VALID);
        } else if (borrowBookList.getItems().stream().anyMatch(book -> book.getDueDate() == null
                || book.getDueDate().compareTo(book.getBorrowDate()) < 0)) {
            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.DUE_DATE_MUST_GREATER_THAN_BORROW_DATE);
        } else if (borrowBookList.getItems().stream().anyMatch(book -> book.getReturnDate() != null
                && book.getReturnDate().compareTo(book.getBorrowDate()) < 0)) {
            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.RETURN_DATE_MUST_GREATER_THAN_BORROW_DATE);
        } else {
            borrowDetail.setBorrowAddBookItems(!newList.isEmpty() ? newList.stream().filter(book -> book.getBookDetailStatus() == 0 && !beforeBorrowBookDetailIds.contains(book.getBookDetailId()) && book.getBookName() != null).collect(Collectors.toList()) : borrowBookList.getItems().stream().filter(book -> book.getBookName() != null && !beforeBorrowBookDetailIds.contains(book.getBookDetailId())).collect(Collectors.toList()));
            borrowModel.createOrUpdateBorrow(DatabaseHandler.getInstance().getDbConnection(),  borrowDetail);
        }
    }

    private void handleAddBook() {
        if (bookLists.size() >= 3) {
            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.MAX_BORROW_BOOK_PER_READER);
        } else {
            bookLists.add(new BorrowAddBookItem());
            SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.ADD_BORROW_BOOK_SUCCESS);
        }
    }

    private void validForm() {
        if (readerCodeError.getText() == null && Validation.textNotEmpty(readerName.getText()) && !borrowBookList.getItems().isEmpty()) {
            addBorrowBtn.setDisable(false);
        }
    }

    private void handleIfReaderHasBook() {
        if (borrowDetail.getReaderStatus() == 0) {
            readerCodeError.setText(Constant.FormTextError.READER_IS_CURRENT_BLOCKED_CANNOT_BORROW_BOOK);
        } else {
            readerCodeError.setText(null);
            readerCode.setText(borrowDetail.getReaderCode());
            readerPhone.setText(borrowDetail.getReaderPhone());
            readerName.setText(borrowDetail.getReaderName());
            readerAcceptEmail.setSelected(borrowDetail.getReaderAcceptEmail());
            if (borrowDetail.getBorrowAddBookItems() != null) {

                if (!bookOfSelectedReader.isEmpty()) {
                    borrowBookList.getItems().removeIf(book -> bookOfSelectedReader.contains(book.getBookDetailId()));
                }
                bookOfSelectedReader = borrowDetail.getBorrowAddBookItems().stream().map(BorrowAddBookItem::getBookDetailId).collect(Collectors.toList());
                if (borrowDetail.getBorrowAddBookItems().size() > 3) {
                    SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.MAX_BORROW_BOOK_PER_READER);
                }
                if (borrowDetail.getBorrowAddBookItems().size() >= 3) {
                    borrowBookList.getItems().clear();
                }
                borrowBookList.getItems().addAll(0, borrowDetail.getBorrowAddBookItems());
            }
        }
    }
}

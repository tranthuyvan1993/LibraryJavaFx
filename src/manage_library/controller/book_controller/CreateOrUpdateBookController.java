package manage_library.controller.book_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import manage_library.data_object.book.Book;
import manage_library.data_object.book.BookDetail;
import manage_library.data_object.book.BookDto;
import manage_library.database.DatabaseHandler;
import manage_library.model.BookModel;
import manage_library.util.*;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CreateOrUpdateBookController {
    @FXML
    private ImageView bookImage;

    @FXML
    private Label labelClickNone;

    @FXML
    private Label titleCrudUser;

    @FXML
    private FontAwesomeIconView backBtn;

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfBookName;

    @FXML
    private ChoiceBox<String> cbCategory;

    @FXML
    private JFXButton btnAddBook;

    @FXML
    private Label bookCodeError;

    @FXML
    private Label bookNameError;

    @FXML
    private Label publisherError;

    @FXML
    private Label authorError;

    @FXML
    private Label userTwoAuthErro;

    @FXML
    private TextField tfPublisher;

    @FXML
    private TextField tfAuthor;

    @FXML
    private TextArea tfDescription;

    @FXML
    private TextArea tfNote;
    @FXML
    private TextField tfPrice;

    @FXML
    private Label priceError;
    @FXML
    private ChoiceBox<String> cbFloor;

    @FXML
    private ChoiceBox<String> cbDivision;

    @FXML
    private ChoiceBox<String> cbShelf;

    @FXML
    private ChoiceBox<String> cbShelfLayer;
    @FXML
    private AnchorPane anchorPaneCode;
    @FXML
    private ChoiceBox<String> cbNumber;
    @FXML
    private Label codeShow;
    private static Integer bookDetailId = null;
    private static String imageBook = "";
    Pattern patternBookName = Pattern.compile(Constant.RegexForm.REGEX_BOOKNAME);
    Pattern bookPrice = Pattern.compile(Constant.RegexForm.REGEX_BOOK_PRICE);
    @FXML
    private String[] categories = {"Tiểu thuyết tình cảm", "Truyện ngắn", "Khoa học viễn tưởng", "Kinh dị", "Lịch sử", "Bí ẩn"};
    private String[] floors = {"1", "2", "3", "4", "5"};
    private String[] divisions = {"A", "B", "C", "D", "E", "F"};
    private String[] shelves = {"1", "2", "3"};
    private String[] shelfLayers = {"1", "2", "3", "4"};
    private String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    String category = null;
    String floor = null;
    String division = null;
    String shelf = null;
    String shelfLayer = null;
    String number = null;
    private File file;
    private String oldImageBook = null;

    public static void setbookDetailId(Integer bookDetailId) {
        CreateOrUpdateBookController.bookDetailId = bookDetailId;
    }

    public static void getImageBook(String imageBook) {
        CreateOrUpdateBookController.imageBook = imageBook;
    }

    String sqlFindBook = "select b.name as bookName, b.price as price, b.description as description, " +
            "b.publisher as publisher, b.auth as auth, b.image as image, bd.status as status, " +
            "bd.code as code, bd.id as bookDetailId, b.id bookId,bd.note as note, c.name as categoryName " +
            "from library.book b, library.book_detail bd, library.category c " +
            "where b.id = bd.book_id and b.category_id = c.id and bd.id = " + bookDetailId;

    @FXML
    void initialize() throws Exception {
        labelClickNone.setPickOnBounds(false);

        bookImage.setOnMouseClicked(e -> {
            try {
                file = HandleFile.handleSelectAvatar(bookImage, labelClickNone);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        if (bookDetailId != null) {
            BookDto bookDto = BookModel.findBookById(sqlFindBook);
            getImageBook(bookDto.getImage());
            tfBookName.setText(bookDto.getBookName());
            tfBookName.setDisable(true);
            tfPrice.setDisable(true);
            tfPublisher.setDisable(true);
            tfAuthor.setDisable(true);
            anchorPaneCode.setVisible(false);
            codeShow.setText(bookDto.getCode());
            tfPrice.setText(String.valueOf(bookDto.getPrice()));
            tfPublisher.setText(bookDto.getPublisher());
            tfAuthor.setText(bookDto.getAuth());
            cbCategory.setValue(bookDto.getCategoryName());
            tfDescription.setText(bookDto.getDescription());
            tfNote.setText(bookDto.getNote());
            String note = tfNote.getText();
            oldImageBook = System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/bookImage/" + bookDto.getImage();
            try {
                InputStream inputStream = new FileInputStream(oldImageBook);
                Image image = new Image(inputStream);
                inputStream.close();
                bookImage.setImage(image);
                labelClickNone.setText(null);
            } catch (Exception e) {
                SnackBar.show(Constant.SnackBarAction.ERROR, "Ảnh đã bị xóa hoặc không tồn tại. Vui lòng tải ảnh mới!");
            }
            btnAddBook.setText("CẬP NHẬT");
            titleCrudUser.setText("Cập nhật thông tin sách");
        } else {
            btnAddBook.setDisable(true);
            cbCategory.setValue(categories[0]);
            tfBookName.focusedProperty().addListener((ov, oldValue, newValue) -> {
                try {
                    validForm(Constant.FormField.BOOK_NAME);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    if (Boolean.TRUE.equals(BookModel.checkExistsByBookName(DatabaseHandler.getInstance().getDbConnection(), tfBookName.getText()))) {
                        bookCodeError.setText(null);
                        priceError.setText(null);
                        authorError.setText(null);
                        publisherError.setText(null);

                        Book book = BookModel.findBookByName(tfBookName.getText());
                        if (book != null) {
                            try {
                                validForm(Constant.FormField.BOOK_CODE);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            getImageBook(book.getImage());
                            tfBookName.setText(book.getName());
                            tfPrice.setDisable(true);
                            tfPublisher.setDisable(true);
                            tfAuthor.setDisable(true);
                            tfPrice.setText(String.valueOf(book.getPrice()));
                            tfPublisher.setText(book.getPublisher());
                            tfAuthor.setText(book.getAuth());
                            cbCategory.setValue(book.getCategoryName());
                            cbCategory.setDisable(true);
                            tfDescription.setText(book.getDescription());
                            bookImage.setDisable(true);
                            oldImageBook = System.getProperty("user.dir").replace("\\", "/") + "/src/manage_library/assets/image/bookImage/" + book.getImage();
                            InputStream inputStream = null;
                            try {
                                inputStream = new FileInputStream(oldImageBook);
                                Image image = new Image(inputStream);
                                inputStream.close();
                                bookImage.setImage(image);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            labelClickNone.setText(null);

                        }
                    } else {
                        tfPrice.setDisable(false);
                        tfPublisher.setDisable(false);
                        tfAuthor.setDisable(false);
                        cbCategory.setDisable(false);
                        bookImage.setDisable(false);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            cbShelfLayer.setOnAction(event -> {
                try {
                    validForm(Constant.FormField.BOOK_CODE);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            cbShelf.setOnAction(event -> {
                try {
                    validForm(Constant.FormField.BOOK_CODE);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            cbDivision.setOnAction(event -> {
                try {
                    validForm(Constant.FormField.BOOK_CODE);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            cbNumber.setOnAction(event -> {
                try {
                    validForm(Constant.FormField.BOOK_CODE);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            cbFloor.setOnAction(event -> {
                try {
                    validForm(Constant.FormField.BOOK_CODE);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            tfPublisher.setOnKeyReleased(event -> {
                try {
                    validForm(Constant.FormField.BOOK_PUBLISHER);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            tfAuthor.setOnKeyReleased(event -> {
                try {
                    validForm(Constant.FormField.BOOK_AUTH);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            tfPrice.setOnKeyReleased(event -> {
                try {
                    validForm(Constant.FormField.PRICE);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        cbCategory.getItems().addAll(categories);
        cbFloor.getItems().addAll(floors);
        cbDivision.getItems().addAll(divisions);
        cbShelf.getItems().addAll(shelves);
        cbShelfLayer.getItems().addAll(shelfLayers);
        cbNumber.getItems().addAll(numbers);
        cbCategory.setOnAction(this::getCatevalue);

        backBtn.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.LIST_BOOK, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                bookDetailId = null;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        btnAddBook.setOnMouseClicked(e -> {
            String bookName = tfBookName.getText();
            String publisher = tfPublisher.getText();
            String author = tfAuthor.getText();
            category = cbCategory.getValue();
            String description = tfDescription.getText();
            String note = tfNote.getText();
            int price = Integer.parseInt(tfPrice.getText());
            int categoryId = BookModel.getCategoryId(category);
            if (bookDetailId == null) {
                try {
                    if (Boolean.TRUE.equals(BookModel.checkExistsByBookName(DatabaseHandler.getInstance().getDbConnection(), bookName))) {
                        String sqlGetBookId = "SELECT id from library.book where name = '" + bookName + "'";
                        int bookId = BookModel.getBookId(sqlGetBookId);
                        BookDetail bookDetail = new BookDetail(bookId, getCodeBook(), note);
                        BookModel.insertBookDetail(bookDetail);
                    } else {
                        if (file != null) {
                            String newNameFile = null;
                            try {
                                newNameFile = HandleFile.saveBookImage(file, getCodeBook(), oldImageBook);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            Book book = new Book(categoryId, bookName, publisher, author, newNameFile, description, price);
                            BookModel.insertBook(book);
                            String sqlGetBookId = "SELECT id from library.book where name = '" + bookName + "'";
                            int bookId = BookModel.getBookId(sqlGetBookId);
                            BookDetail bookDetail = new BookDetail(bookId, getCodeBook(), note);
                            BookModel.insertBookDetail(bookDetail);
                        } else {
                            SnackBar.show(Constant.SnackBarAction.ERROR, "Bạn cần tải ảnh sách!");
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            } else {
                BookDto bookDto = BookModel.findBookById(sqlFindBook);
                if (file != null || !imageBook.trim().equals("")) {
                    String newNameFile = null;
                    if (!imageBook.trim().equals("") && file == null) {
                        newNameFile = this.imageBook;
                    } else if (file != null) {
                        try {
                            newNameFile = HandleFile.saveBookImage(file, bookDto.getCode(), imageBook);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    String sqlUpdateBook = "UPDATE book SET auth = '" + tfAuthor.getText() +
                            "' , image = '" + newNameFile +
                            "' , category_id = '" + categoryId +
                            "' , description = '" + tfDescription.getText() +
                            "' WHERE id = " + bookDto.getBookId();
                    BookModel.updateBook(sqlUpdateBook);
                    String sqlUpdateBookDetail = "UPDATE book_detail SET note = '" + tfNote.getText() +
                            "' WHERE id = " + bookDetailId;
                    BookModel.updateBookDetail(sqlUpdateBookDetail);
                    bookDetailId = null;
                } else {
                    ButtonType button = AlertCustom.show(Alert.AlertType.INFORMATION, "upload Avatar", null, "You have to upload Avatar!");
                    if (button == ButtonType.OK) {
                        return;
                    }
                }
            }
        });
    }

    private String getCodeBook() {
        String code = null;
        if (cbFloor.getValue() != null && cbDivision.getValue() != null && cbShelf.getValue() != null &&
                cbShelfLayer.getValue() != null && cbNumber.getValue() != null) {
            code = cbFloor.getValue() + "." + cbDivision.getValue() + "." + cbShelf.getValue()
                    + "." + cbShelfLayer.getValue() + "." + cbNumber.getValue();
        } else {
            code = null;
        }
        return code;
    }
    private void validForm(String field) throws SQLException {
        if (bookDetailId == null) {
            switch (field) {
                case Constant.FormField.BOOK_CODE:
                    if (getCodeBook() == null) {
                        bookCodeError.setText("Bạn phải chọn đủ các mục");
                    } else {
                        bookCodeError.setText(null);
                        if (Boolean.TRUE.equals(BookModel.checkExistsByBookCode(DatabaseHandler.getInstance().getDbConnection(), getCodeBook()))) {
                            bookCodeError.setText("Mã sách đã tồn tại");
                        }
                    }
                    break;
                case Constant.FormField.BOOK_NAME:
                    bookNameError.setText(Validation.validationField(Constant.FormField.BOOK_NAME, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), tfBookName.getText(), patternBookName, btnAddBook));
                    break;
                case Constant.FormField.BOOK_PUBLISHER:
                    publisherError.setText(Validation.validationField(Constant.FormField.BOOK_PUBLISHER, Arrays.asList(Constant.Validator.REQUIRED), tfPublisher.getText(), null, btnAddBook));
                    break;
                case Constant.FormField.BOOK_AUTH:
                    authorError.setText(Validation.validationField(Constant.FormField.BOOK_AUTH, Arrays.asList(Constant.Validator.REQUIRED), tfAuthor.getText(), null, btnAddBook));
                    break;
                case Constant.FormField.PRICE:
                    priceError.setText(Validation.validationField(Constant.FormField.PRICE, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), tfPrice.getText(), bookPrice, btnAddBook));
                    break;
                default:
                    break;
            }
            if (
                    bookCodeError.getText() == null
                    && bookNameError.getText() == null
                            && publisherError.getText() == null
                            && priceError.getText() == null
                            && authorError.getText() == null) {
                btnAddBook.setDisable(false);
            } else {
                btnAddBook.setDisable(true);
            }
        } else {
            publisherError.setText(Validation.validationField(Constant.FormField.BOOK_PUBLISHER, Arrays.asList(Constant.Validator.REQUIRED), tfPublisher.getText(), null, btnAddBook));
            authorError.setText(Validation.validationField(Constant.FormField.BOOK_AUTH, Arrays.asList(Constant.Validator.REQUIRED), tfAuthor.getText(), null, btnAddBook));
            priceError.setText(Validation.validationField(Constant.FormField.PRICE, Arrays.asList(Constant.Validator.REQUIRED, Constant.Validator.REGEX_PATTERN), tfPrice.getText(), bookPrice, btnAddBook));
            if (publisherError.getText() == null
                    && priceError.getText() == null
                    && authorError.getText() == null) {
                btnAddBook.setDisable(false);
            }else {
                btnAddBook.setDisable(true);
            }
        }
    }

    private String getCatevalue(ActionEvent event) {
        category = cbCategory.getValue();
        return category;
    }

    private String getFloor(ActionEvent event) {
        floor = cbFloor.getValue();
        return floor;
    }

    private String getShelf(ActionEvent event) {
        shelf = cbShelf.getValue();
        return shelf;
    }

    private String getDivision(ActionEvent event) {
        division = cbDivision.getValue();
        return division;
    }

    private String getShelfLayer(ActionEvent event) {
        shelfLayer = cbShelfLayer.getValue();
        return shelfLayer;
    }

    private String getNumberLocation(ActionEvent event) {
        number = cbNumber.getValue();
        return number;
    }

}
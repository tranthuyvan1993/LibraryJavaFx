package manage_library.controller.book_controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import manage_library.data_object.book.BookDto;
import manage_library.data_object.book.BookItem;
import manage_library.data_object.book.BookRenderDto;
import manage_library.database.DatabaseHandler;
import manage_library.model.BookModel;
import manage_library.util.AlertCustom;
import manage_library.util.Constant;
import manage_library.util.Navigator;

import javax.swing.text.Element;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ListBooksController {
    @FXML
    private TableView<BookRenderDto> bookTable;
    @FXML
    private TableColumn<BookRenderDto, String> indexCol;

    @FXML
    private TableColumn<BookRenderDto, String> codeCol;

    @FXML
    private TableColumn<BookRenderDto, String> nameCol;
    @FXML
    private TableColumn<BookRenderDto, String> priceCol;
    @FXML
    private TableColumn<BookRenderDto, String> publisherCol;

    @FXML
    private TableColumn<BookRenderDto, String> authorCol;

    @FXML
    private TableColumn<BookRenderDto, String> categoryCol;

    @FXML
    private TableColumn<BookRenderDto, ImageView> imageCol;

    @FXML
    private TableColumn<BookRenderDto, String> descriptionCol;

    @FXML
    private TableColumn<BookRenderDto, String> noteCol;

    @FXML
    private FontAwesomeIconView btnAddBook;

    @FXML
    private TextField searchBook;
    @FXML
    private Pagination pagination;
//    @FXML
//    private ChoiceBox<?> floor;
//
//    @FXML
//    private ChoiceBox<?> devision;
//
//    @FXML
//    private ChoiceBox<?> shelf;
//
//    @FXML
//    private ChoiceBox<?> shelfLayer;
//
//    @FXML
//    private ChoiceBox<?> number;
    private static final int ROWS_PER_PAGE = 10;
    private BookRenderDto BookDtoSelected = null;
    private final List<String> actions = Arrays.asList(Constant.NavigationNameAction.EDIT_ICON, Constant.NavigationNameAction.TRASH_ICON, Constant.NavigationNameAction.INFO_ICON);
    ObservableList<BookRenderDto> bookList = FXCollections.observableArrayList();
        String sqlGetBookList = "select b.category_id as categoryId, b.name as bookName, " +
                "b.description as description, b.publisher as publisher, b.auth as auth, b.price as price, " +
                "b.image as image, bd.status as status, bd.code as code, bd.id as bookDetailId, " +
                "b.id bookId, bd.note as note, c.name as categoryName from library.book b, library.book_detail bd, " +
                "library.category c " +
                "where b.id = bd.book_id and b.category_id = c.id and bd.status != -1 order by bd.id desc ";
    private FilteredList<BookRenderDto> bookFilter;
    SortedList<BookRenderDto> sortedList;
    @FXML
    void initialize() throws IOException {
        List<BookItem> bookDtos = BookModel.getBookList(sqlGetBookList);
        List<BookRenderDto> bookLists = new ArrayList<>();
        for (BookItem bookItem: bookDtos) {
            bookLists.add(new BookRenderDto(bookItem));
        }
        bookList = FXCollections.observableArrayList(bookLists);
        showBookList(bookList);
        searchBook.setOnKeyReleased(e -> {
            try {
                searchBook(searchBook.getText().trim(), searchBook.getText().trim());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        btnAddBook.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.CRUD_BOOK, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private void showBookList(ObservableList<BookRenderDto> bookList) {
        bookTable.getColumns().clear(); //fix error -> Duplicate TableColumns detected in TableView columns list
        indexCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("auth"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
        addButtonToTable();
        bookTable.getColumns().addAll(imageCol,codeCol, nameCol, priceCol,publisherCol, authorCol, categoryCol, indexCol,descriptionCol, noteCol);
        bookTable.setItems(bookList);
        pagination.getStylesheets().add(Objects.requireNonNull(AlertCustom.class.getResource("/manage_library/assets/css/pagination.css")).toString());
        pagination.setPageCount((int) ((bookList.size() - 0.1)/ ROWS_PER_PAGE + 1));
        pagination.setPageFactory(this::setBookTable);
    }
        private void searchBook(String name, String bookCode) throws IOException {
        String sql2 = "select b.category_id as categoryId, b.name as bookName, b.price as price, b.description as description, " +
                "b.publisher as publisher, b.auth as auth, b.image as image, bd.status as status, bd.code as code, " +
                "bd.id as bookDetailId, b.id bookId,bd.note as note, c.name as categoryName from library.book b, " +
                "library.book_detail bd, library.category c " +
                "where bd.status != -1 and b.id = bd.book_id and b.category_id = c.id and (b.name like '%" + name + "%' or bd.code like '%"
                + bookCode + "%') order by bd.id desc";
        List<BookItem> bookItems = BookModel.getBookList(sql2);
        List<BookRenderDto> bookRenderDtos = new ArrayList<>();
        for (BookItem bookItem: bookItems) {
            bookRenderDtos.add(new BookRenderDto(bookItem));
        }
        ObservableList<BookRenderDto> bookNewList = FXCollections.observableArrayList(bookRenderDtos);
        bookTable.setItems(bookNewList);
    }

    private HBox createAction() {
        HBox hBox = new HBox();
        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        FontAwesomeIconView trashIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT);
        editIcon.setSize("24");
        trashIcon.setSize("24");
        hBox.getChildren().addAll(editIcon, trashIcon);
        hBox.setAlignment(Pos.CENTER);

        editIcon.setId("editIcon");
        trashIcon.setId("trashIcon");

        editIcon.setWrappingWidth(36);
        editIcon.setFill(Paint.valueOf("green"));
        trashIcon.setFill(Paint.valueOf("red"));

        return hBox;
    }

    private void editIconHandle() {
        BookDtoSelected.getBookDetailId();
        CreateOrUpdateBookController.setbookDetailId(BookDtoSelected.getBookDetailId());
        if (BookDtoSelected != null) {
            try {
                Navigator.getInstance().redirectTo(Navigator.CRUD_BOOK, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"), BookDtoSelected.getBookDetailId(), Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void trashIconHandle() {
        if (BookDtoSelected != null) {
            ButtonType buttonType = AlertCustom.show(Alert.AlertType.CONFIRMATION, "Xoá Sách", String.format("Bạn có muốn xoá quyển sách " + BookDtoSelected.getBookName() +" không?"), "");
            if (buttonType == ButtonType.OK) {
                BookModel.deleteBook(BookDtoSelected.getBookDetailId());
            }
        }
    }
    private void addButtonToTable() {
        TableColumn<BookRenderDto, Void> colBtn = new TableColumn("Action");
        Callback<TableColumn<BookRenderDto, Void>, TableCell<BookRenderDto, Void>> cellFactory = new Callback<TableColumn<BookRenderDto, Void>, TableCell<BookRenderDto, Void>>() {
            @Override
            public TableCell<BookRenderDto, Void> call(final TableColumn<BookRenderDto, Void> param) {
                return new TableCell<BookRenderDto, Void>() {
                    private final HBox action = createAction();

                    {
                        action.setOnMouseClicked(event -> {
                            String btnId = event.getPickResult().getIntersectedNode().getId();
                            BookDtoSelected = getTableView().getItems().get(getIndex());
                            if (btnId != null && actions.contains(btnId)) {
                                if (btnId.equals(Constant.NavigationNameAction.EDIT_ICON)) editIconHandle();
                                if (btnId.equals(Constant.NavigationNameAction.TRASH_ICON)) trashIconHandle();
                            }
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(action);
                        }
                    }
                };
            }
        };
        colBtn.setCellFactory(cellFactory);
        bookTable.getColumns().add(colBtn);
    }
    private Node setBookTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, bookList.size());
        bookFilter = new FilteredList<>(FXCollections.observableArrayList(bookList.subList(fromIndex, toIndex)), x -> true);
        sortedList = new SortedList<>(bookFilter);
        sortedList.comparatorProperty().bind(bookTable.comparatorProperty());
        bookTable.setItems(sortedList);
        return bookTable;
    }
}

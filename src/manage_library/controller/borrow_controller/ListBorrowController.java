package manage_library.controller.borrow_controller;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import lombok.SneakyThrows;
import manage_library.data_object.borrow.BorrowItem;
import manage_library.database.DatabaseHandler;
import manage_library.model.BorrowModel;
import manage_library.util.AlertCustom;
import manage_library.util.Constant;
import manage_library.util.Constant.NavigationNameAction;
import manage_library.util.Navigator;
import manage_library.util.Validation;
import manage_library.util.format.DateFormat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ListBorrowController {
    @FXML
    private TextField textSearch;

    @FXML
    private Pagination pagination;

    @FXML
    private TableView<BorrowItem> borrowTable;

    @FXML
    private TableColumn<BorrowItem, String> borrowDateCol;

    @FXML
    private TableColumn<BorrowItem, String> readerCodeCol;

    @FXML
    private TableColumn<BorrowItem, String> readerNameCol;

    @FXML
    private TableColumn<BorrowItem, String> readerPhoneCol;

    @FXML
    private TableColumn<BorrowItem, String> bookNameCol;

    @FXML
    private TableColumn<BorrowItem, String> bookCodeCol;

    @FXML
    private TableColumn<BorrowItem, String> authCol;

    @FXML
    private TableColumn<BorrowItem, String> priceCol;

    @FXML
    private TableColumn<BorrowItem, String> depositCol;

    @FXML
    private TableColumn<BorrowItem, String> dueDateCol;

    @FXML
    private TableColumn<BorrowItem, String> returnDateCol;

    @FXML
    private TableColumn<BorrowItem, String> noteCol;

    @FXML
    private JFXButton addBorrowBtn;

    private final BorrowModel borrowModel = new BorrowModel();
    private static final int ROWS_PER_PAGE = 10;
    private BorrowItem borrowItemDtoSelected = null;
    private final List<String> actions = Collections.singletonList(NavigationNameAction.EDIT_ICON);

    private ObservableList<BorrowItem> borrowItems = FXCollections.observableArrayList();
    private FilteredList<BorrowItem> borrowItemsFilter;
    SortedList<BorrowItem> sortedList;



    @FXML
    void initialize() throws SQLException {
        CreateOrUpdateBorrowController.setSelectedReaderCode(null);
        CreateOrUpdateBorrowController.setBookLists(FXCollections.observableArrayList());
        Connection connection = DatabaseHandler.getInstance().getDbConnection();
        addBorrowBtn.setOnAction(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.CREATE_BORROW, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        loadBorrow(connection);
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        readerCodeCol.setCellValueFactory(new PropertyValueFactory<>("readerCode"));
        readerNameCol.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        readerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("readerPhone"));
        bookNameCol.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        bookCodeCol.setCellValueFactory(new PropertyValueFactory<>("bookCode"));
        authCol.setCellValueFactory(new PropertyValueFactory<>("auth"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        depositCol.setCellValueFactory(new PropertyValueFactory<>("deposit"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        addButtonToTable();
        addStatusCell();
        pagination.getStylesheets().add(Objects.requireNonNull(AlertCustom.class.getResource("/manage_library/assets/css/pagination.css")).toString());
        pagination.setPageCount((int) ((borrowItems.size() - 0.1)/ ROWS_PER_PAGE + 1));
        pagination.setPageFactory(this::setBorrowTable);
    }
    private HBox createAction() {
        HBox hBox = new HBox();
        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        editIcon.setSize("24");

        hBox.getChildren().addAll(editIcon);
        hBox.setAlignment(Pos.CENTER);

        editIcon.setId("editIcon");

        editIcon.setWrappingWidth(36);

        editIcon.setFill(Paint.valueOf("green"));

        return hBox;
    }


    private void editIconHandle() {
        if (borrowItemDtoSelected != null) {
            try {
                Navigator.getInstance().redirectTo(Navigator.CREATE_BORROW, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"), borrowItemDtoSelected.getId(), Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addButtonToTable() {
        TableColumn<BorrowItem, Void> btnCol = new TableColumn("Hành động");

        Callback<TableColumn<BorrowItem, Void>, TableCell<BorrowItem, Void>> cellFactory = new Callback<TableColumn<BorrowItem, Void>, TableCell<BorrowItem, Void>>() {
            @Override
            public TableCell<BorrowItem, Void> call(final TableColumn<BorrowItem, Void> param) {
                return new TableCell<BorrowItem, Void>() {

                    private final HBox action = createAction();

                    {
                        action.setOnMouseClicked(event -> {
                            String btnId = event.getPickResult().getIntersectedNode().getId();
                            borrowItemDtoSelected = getTableView().getItems().get(getIndex());
                            if (btnId != null && actions.contains(btnId)) {
                                if (btnId.equals(NavigationNameAction.EDIT_ICON)) editIconHandle();
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

        btnCol.setCellFactory(cellFactory);

        borrowTable.getColumns().add(0, btnCol);

    }

    private void addStatusCell() {
        TableColumn<BorrowItem, Void> statusCol = new TableColumn("Trạng thái");
        statusCol.setPrefWidth(150);
        Callback<TableColumn<BorrowItem, Void>, TableCell<BorrowItem, Void>> cellFactory = new Callback<TableColumn<BorrowItem, Void>, TableCell<BorrowItem, Void>>() {
            @Override
            public TableCell<BorrowItem, Void> call(final TableColumn<BorrowItem, Void> param) {
                return new TableCell<BorrowItem, Void>() {
                    private final Label statusLabel = new Label();
                    @SneakyThrows
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                                String[] itemsArray = this.getTableRow().getItem().toString().split(", ");
                                String dueDate = itemsArray[itemsArray.length - 3];
                                if (this.getTableRow().getItem().toString().contains("status=0")) {
                                    if (DateFormat.parseDateStringToDate(DateFormat.parseDateRenderToDatabase(dueDate.split("=")[1])).before(new Date())) {
                                        statusLabel.setText("Quá hạn");
                                        statusLabel.setStyle("-fx-text-fill: red");
                                        statusLabel.setUnderline(true);
                                    } else {
                                        statusLabel.setText("Chưa đến hạn");
                                        statusLabel.setStyle("-fx-text-fill: #9C2C77");
                                    }

                                } else if (this.getTableRow().getItem().toString().contains("status=1")) {
                                    statusLabel.setText("Đã trả");
                                    statusLabel.setStyle("-fx-text-fill: green");
                                } else if (this.getTableRow().getItem().toString().contains("status=2")) {
                                    statusLabel.setText("Xin trả muộn");
                                    statusLabel.setStyle("-fx-text-fill: blue");
                                } else if (this.getTableRow().getItem().toString().contains("status=3")) {
                                    statusLabel.setText("Không trả");
                                    statusLabel.setStyle("-fx-text-fill: red");
                                }
                            }
                            setGraphic(statusLabel);
                        }
                    }
                };
            }
        };

        statusCol.setCellFactory(cellFactory);

        borrowTable.getColumns().add(2, statusCol);

    }

    private Node setBorrowTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, borrowItems.size());
        borrowItemsFilter = new FilteredList<>(FXCollections.observableArrayList(borrowItems.subList(fromIndex, toIndex)), x -> true);

        textSearch.textProperty().addListener(((observable, oldValue, newValue) -> {
            borrowItemsFilter.setPredicate(borrow -> {
                if (!Validation.textNotEmpty(newValue)) {
                    return true;
                }

                if (borrow.getReaderCode().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))
                || borrow.getReaderName().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))
                || borrow.getReaderPhone().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))
                || borrow.getBookName().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))
                || borrow.getAuth().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))) {
                    return true;
                } else return false;
            });
        }));

        sortedList = new SortedList<>(borrowItemsFilter);
        sortedList.comparatorProperty().bind(borrowTable.comparatorProperty());

        borrowTable.setItems(sortedList);
        return borrowTable;
    }

    public void loadBorrow(Connection connection) throws SQLException {
        borrowItems = borrowModel.getAllBorrow(connection, textSearch.getText());
    }

}

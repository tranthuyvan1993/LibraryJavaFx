package manage_library.controller.reader_controller;

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
import manage_library.data_object.reader.ReaderItemDto;
import manage_library.database.DatabaseHandler;
import manage_library.model.ReaderModel;
import manage_library.util.AlertCustom;
import manage_library.util.Constant;
import manage_library.util.Constant.NavigationNameAction;
import manage_library.util.Navigator;
import manage_library.util.SnackBar;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ListReaderController {
    @FXML
    private TableView<ReaderItemDto> readerTable;

    @FXML
    private TableColumn<ReaderItemDto, String> createdAtCol;

    @FXML
    private TableColumn<ReaderItemDto, String> codeCol;

    @FXML
    private TableColumn<ReaderItemDto, String> statusCol;

    @FXML
    private TableColumn<ReaderItemDto, String> nameCol;

    @FXML
    private TableColumn<ReaderItemDto, String> phoneCol;

    @FXML
    private TableColumn<ReaderItemDto, String> emailCol;

    @FXML
    private TableColumn<ReaderItemDto, String> addressCol;

    @FXML
    private TextField textSearch;

    @FXML
    private JFXButton addReaderBtn;

    @FXML
    private Pagination pagination;

    private final ReaderModel readerModel = new ReaderModel();
    private static final int ROWS_PER_PAGE = 10;
    private ReaderItemDto readerItemDtoSelected = null;
    private final List<String> actions = Arrays.asList(NavigationNameAction.EDIT_ICON, NavigationNameAction.TRASH_ICON);

    private ObservableList<ReaderItemDto> readerItems = FXCollections.observableArrayList();
    private FilteredList<ReaderItemDto> readerItemsFilter;
    SortedList<ReaderItemDto> sortedList;



    @FXML
    void initialize() throws SQLException {
        CreateOrUpdateReaderController.setReaderId(null);
        Connection connection = DatabaseHandler.getInstance().getDbConnection();
        addReaderBtn.setOnAction(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.CREATE_READER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        loadReader(connection);
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        addButtonToTable(connection);
        pagination.getStylesheets().add(Objects.requireNonNull(AlertCustom.class.getResource("/manage_library/assets/css/pagination.css")).toString());
        pagination.setPageCount((int) ((readerItems.size() - 0.1)/ ROWS_PER_PAGE + 1));
        pagination.setPageFactory(this::setReaderTable);
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
        if (readerItemDtoSelected != null) {
            try {
                Navigator.getInstance().redirectTo(Navigator.CREATE_READER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"), readerItemDtoSelected.getId(), Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void trashIconHandle (Connection connection) {
        if (readerItemDtoSelected != null) {
            ButtonType buttonType = AlertCustom.show(Alert.AlertType.CONFIRMATION, "Xóa Người đọc", String.format("Bạn có muốn xóa Người đọc %s?", readerItemDtoSelected.getName()), "");
            if (buttonType == ButtonType.OK) {
                try {
                    if (readerModel.deleteReader(connection, readerItemDtoSelected.getId()) > 0) {
                        readerItems = readerModel.refreshReader(connection);
                        Navigator.getInstance().redirectTo(Navigator.LIST_READER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                        SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.DELETE_SUCCESS);
                    } else {
                        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.DELETE_ERROR);
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addButtonToTable(Connection connection) {
        TableColumn<ReaderItemDto, Void> btnCol = new TableColumn("Hành động");

        Callback<TableColumn<ReaderItemDto, Void>, TableCell<ReaderItemDto, Void>> cellFactory = new Callback<TableColumn<ReaderItemDto, Void>, TableCell<ReaderItemDto, Void>>() {
            @Override
            public TableCell<ReaderItemDto, Void> call(final TableColumn<ReaderItemDto, Void> param) {
                return new TableCell<ReaderItemDto, Void>() {

                    private final HBox action = createAction();

                    {
                        action.setOnMouseClicked(event -> {
                            String btnId = event.getPickResult().getIntersectedNode().getId();
                            readerItemDtoSelected = getTableView().getItems().get(getIndex());
                            if (btnId != null && actions.contains(btnId)) {
                                if (btnId.equals(NavigationNameAction.EDIT_ICON)) editIconHandle();
                                if (btnId.equals(NavigationNameAction.TRASH_ICON)) trashIconHandle(connection);
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

        readerTable.getColumns().add(0, btnCol);

    }

    private Node setReaderTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, readerItems.size());
        readerItemsFilter = new FilteredList<>(FXCollections.observableArrayList(readerItems.subList(fromIndex, toIndex)), x -> true);

        textSearch.textProperty().addListener(((observable, oldValue, newValue) -> {
            readerItemsFilter.setPredicate(reader -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (reader.getName().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))
                || reader.getEmail().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))
                || reader.getCode().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))
                || reader.getPhone().toLowerCase(Locale.ROOT).contains(newValue.toLowerCase(Locale.ROOT))) {
                    return true;
                } else return false;
            });
        }));

        sortedList = new SortedList<>(readerItemsFilter);
        sortedList.comparatorProperty().bind(readerTable.comparatorProperty());

        readerTable.setItems(sortedList);
        return readerTable;
    }

    public void loadReader(Connection connection) throws SQLException {
        readerItems = readerModel.getAllReader(connection);
    }

}

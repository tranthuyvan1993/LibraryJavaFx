package manage_library.controller.user_controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import manage_library.data_object.admin.AdminDto;
import manage_library.database.DatabaseHandler;
import manage_library.model.AdminModel;
import manage_library.data_object.CurrentAdmin;
import manage_library.util.AlertCustom;
import manage_library.util.Constant;
import manage_library.util.Navigator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ListUserController {
    @FXML
    private TableView<AdminDto> adminTable;
    @FXML
    private TableColumn<AdminDto, Integer> indexCol;
    @FXML
    private TableColumn<AdminDto, String> usernameCol;
    @FXML
    private TableColumn<AdminDto, String> fullnameCol;
    @FXML
    private TableColumn<AdminDto, String> phoneCol;
    @FXML
    private TableColumn<AdminDto, String> emailCol;
    @FXML
    private TableColumn<AdminDto, Integer> statusCol;
    @FXML
    private TableColumn<AdminDto, Integer> twoAuthCol;
    @FXML
    private TableColumn<AdminDto, String> roleCol;
    @FXML
    private FontAwesomeIconView btnAddUser;
    @FXML
    private TextField searchUser;
    private AdminDto adminDtoSelected = null;
    private final List<String> actions = Arrays.asList(Constant.NavigationNameAction.EDIT_ICON, Constant.NavigationNameAction.TRASH_ICON, Constant.NavigationNameAction.INFO_ICON);

    AdminDto adminDto = CurrentAdmin.getInstance().getCurrentAdmin();

    @FXML
    void initialize() throws SQLException {
        String sqlGetAdminList = "select * from admin where role != 'SUPER_ADMIN' and username != '" + adminDto.getUsername() + "'";
        ObservableList<AdminDto> adminList = FXCollections.observableArrayList(AdminModel.getAdminList(sqlGetAdminList));
        showAdminList(adminList);
        searchUser.setOnKeyReleased(e -> {
            searchUser(searchUser.getText().trim(), searchUser.getText().trim());
        });
        btnAddUser.setOnMouseClicked(e -> {
            try {
                Navigator.getInstance().redirectTo(Navigator.CRUD_USER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private void showAdminList(ObservableList<AdminDto> adminList) {
        adminTable.getColumns().clear(); //fix error -> Duplicate TableColumns detected in TableView columns list
        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullnameCol.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        addButtonToTable();
        adminTable.getColumns().addAll(indexCol, usernameCol, fullnameCol, phoneCol, emailCol, roleCol, statusCol);
        adminTable.setItems(adminList);
    }

    private void searchUser(String fullname, String username) {
        String sql2 = "select * from admin where role != 'SUPER_ADMIN' and username != '"
                + adminDto.getUsername() + "'and (fullname like '%" + fullname + "%' or username like '%"
                + username + "%')";
        ObservableList<AdminDto> adminNewList = FXCollections.observableArrayList(AdminModel.getAdminList(sql2));
        adminTable.setItems(adminNewList);
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
        CreateOrUpdateUserController.setAdminId(adminDtoSelected.getId());
        if (adminDtoSelected != null) {
            try {
                Navigator.getInstance().redirectTo(Navigator.CRUD_USER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"), adminDtoSelected.getId(), Constant.NAVIGATION_REDIRECT_ACTION.UPDATE.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void trashIconHandle() {
        if (adminDtoSelected != null) {
            Connection con = null;
            ButtonType buttonType = AlertCustom.show(Alert.AlertType.CONFIRMATION, "Xoá tài khoản người dùng", String.format("Bạn có muốn xoá người dùng: " + adminDtoSelected.getFullname()+" không?"), "");
            if (buttonType == ButtonType.OK) {
                String sqlDeleteUser = "delete from admin where id = " + adminDtoSelected.getId();
                AdminModel.deleteUser(sqlDeleteUser);
            }
        }
    }

    private void addButtonToTable() {
        TableColumn<AdminDto, Void> colBtn = new TableColumn("Action");
        Callback<TableColumn<AdminDto, Void>, TableCell<AdminDto, Void>> cellFactory = new Callback<TableColumn<AdminDto, Void>, TableCell<AdminDto, Void>>() {
            @Override
            public TableCell<AdminDto, Void> call(final TableColumn<AdminDto, Void> param) {
                return new TableCell<AdminDto, Void>() {

                    private final HBox action = createAction();

                    {
                        action.setOnMouseClicked(event -> {
                            String btnId = event.getPickResult().getIntersectedNode().getId();
                            adminDtoSelected = getTableView().getItems().get(getIndex());
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
        adminTable.getColumns().add(colBtn);
    }
}

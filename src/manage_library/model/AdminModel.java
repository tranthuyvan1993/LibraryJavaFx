package manage_library.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import manage_library.data_object.admin.AdminDto;
import manage_library.database.DatabaseHandler;
import manage_library.data_object.admin.Admin;
import manage_library.util.*;
import manage_library.util.email.EmailHandler;
import org.mindrot.jbcrypt.BCrypt;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdminModel {
    public static List<AdminDto> getAdminList(String sql) {
        List<AdminDto> adminList = new ArrayList<>();
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            int index = 1;
            while (resultSet.next()) {
                String status = String.valueOf(resultSet.getInt("status"));
                if (status.equals("1")){
                    status = "Đang kích hoạt";
                }else if (status.equals("0")){
                    status = "Đang khóa";
                }
                String role = resultSet.getString("role");
                if (role.equalsIgnoreCase("READER_MANAGER")){
                    role = "Quản lý người đọc";
                }else if (role.equalsIgnoreCase("Admin")){
                    role = "Admin";
                }else if (role.equalsIgnoreCase("ADMIN_MANAGER")){
                    role = "Quản lý nhân viên";
                }
                AdminDto admin = new AdminDto(
                        index++,
                        resultSet.getInt("id"),
                        resultSet.getInt("two_auth"),
                        status,
                        resultSet.getString("fullname"),
                        resultSet.getString("username"),
                        resultSet.getString(
                                "phone"),
                        resultSet.getString("email"),
                        role,
                        resultSet.getString("avatar"));
                adminList.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return adminList;
    }
    public static List<AdminDto> getAdminPass(String sql) {
        List<AdminDto> adminList = new ArrayList<>();
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return adminList;
    }
    public static Admin login(String username) {
        Admin admin = null;
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            String sql = "select * from admin where username = ? ";
            statement = con.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                admin = new Admin(
                        resultSet.getString("fullname"),
                        resultSet.getString("username"),
                        resultSet.getString("phone"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("role"),
                        resultSet.getInt("status"),
                        resultSet.getInt("two_auth"),
                        resultSet.getString("avatar"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return admin;
    }
    public static void insertAdmin(Admin admin) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            String sql = "insert into admin (fullname, username, phone, password, email,role, status, two_auth, avatar)"
                    + "value(?,?,?,?,?,?,?,?,?)";
            statement = con.prepareStatement(sql);
            statement.setString(1, admin.getFullname());
            statement.setString(2, admin.getUsername());
            statement.setString(3, admin.getPhone());
            statement.setString(4, admin.getPassword());
            statement.setString(5, admin.getEmail());
            statement.setString(6, admin.getRole());
            statement.setInt(7, admin.getStatus());
            statement.setInt(8, admin.getTwo_auth());
            statement.setString(9, admin.getavatar());
            int count = statement.executeUpdate();
            if (count > 0) {
                SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.CREATE_SUCCESS);
                Navigator.getInstance().redirectTo(Navigator.LIST_USER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
            } else {
                SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.CREATE_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void updateAdminPasswordByEmail(String username, String email) {
        Connection con = null;
        PreparedStatement statement = null;
        String password = "";
        int length = 10;
        String symbol = "-/.^&*_!@%=+>)";
        String cap_letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String small_letter = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String finalString = symbol + cap_letter + small_letter + numbers;
        Random random = new Random();

        char[] code = new char[length];

        for (int i = 0; i < length; i++) {
            code[i] = finalString.charAt(random.nextInt(finalString.length()));
        }
        String hashPassword = BCrypt.hashpw(String.valueOf(code), BCrypt.gensalt(12));
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            String sql = "update admin set password = ? where username = ?";
            statement = con.prepareStatement(sql);
            statement.setString(1, hashPassword);
            statement.setString(2, username);
            int count = statement.executeUpdate();
            if (count > 0) {
                new EmailHandler(Constant.ActionEmail.RESET_PASSWORD, email, String.valueOf(code)).start();
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.INFORMATION, "Reset Password", null, "Mật khẩu mới đã được gửi về mail!");
                if (buttonType == ButtonType.OK) {
                    try {
                        Navigator.getInstance().redirectTo(Navigator.LOGIN);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.WARNING, "Reset Password", null, "Không thể reset mật khẩu, hãy thử lại!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static void changeAdminPassword(String username, String password) {
        Connection con = null;
        PreparedStatement statement = null;
        String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            String sql = "update admin set password = ? where username = ?";
            statement = con.prepareStatement(sql);
            statement.setString(1, hashPassword);
            statement.setString(2, username);
            int count = statement.executeUpdate();
            if (count > 0) {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.INFORMATION, "Thay đổi mật khẩu", null, "Đổi mật khẩu thành công!");
                if (buttonType == ButtonType.OK) {
                    try {
                        Navigator.getInstance().redirectTo(Navigator.LOGIN);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.WARNING, "Thay đổi mật khẩu", null, "Không thể thay đổi mật khẩu, hãy thử lại!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static AdminDto findAdminById(String sql) {
        AdminDto admin = null;
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            int index = 1;
            while (resultSet.next()) {
                String status = String.valueOf(resultSet.getInt("status"));
                if (status.equals("1")){
                    status = "active";
                }else if (status.equals("0")){
                    status = "non-active";
                }
                admin = new AdminDto(
                        index++,
                        resultSet.getInt("id"),
                        resultSet.getInt("two_auth"),
                        status,
                        resultSet.getString("fullname"),
                        resultSet.getString("username"),
                        resultSet.getString(
                                "phone"),
                        resultSet.getString("email"),
                        resultSet.getString("role"),
                        resultSet.getString("avatar"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return admin;
    }
    public static void deleteUser(String sql) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            int count = statement.executeUpdate();
            if (count > 0) {
                ButtonType button = AlertCustom.show(Alert.AlertType.INFORMATION, "Xoá người dùng",
                        null, "Xoá người dùng thành công");
                if (button == ButtonType.OK) {
                    Navigator.getInstance().redirectTo(Navigator.LIST_USER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                }
            } else {
                ButtonType button = AlertCustom.show(Alert.AlertType.WARNING, "Xoá người dùng",
                        null, "Không thể Xoá người dùng");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static void updateUser(String sql, String roleCurrentAdmin) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            int count = statement.executeUpdate();
            if (count > 0) {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.INFORMATION, "Cập nhật thông tin người dùng", null, "Cập nhật thành công");
                if (buttonType == ButtonType.OK) {
                    switch (roleCurrentAdmin) {
                        case Constant.Role.SUPER_ADMIN:
                        case Constant.Role.ADMIN:
                            Navigator.getInstance().redirectTo(Navigator.LIST_READER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                            Navigator.getInstance().redirectTo(Navigator.LIST_BOOK, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                            Navigator.getInstance().redirectTo(Navigator.LIST_USER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                            break;
                        case Constant.Role.ADMIN_MANAGER:
                            Navigator.getInstance().redirectTo(Navigator.LIST_USER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                            break;
                        case Constant.Role.READER_MANAGER:
                            Navigator.getInstance().redirectTo(Navigator.LIST_READER, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                            break;
                        default:
                            break;
                    }
                }
            } else {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.WARNING, "Cập nhật thông tin người dùng", null, "Không thể cập nhật!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static Boolean checkExistsByEmail(Connection connection, String email) {
        String sql = "select * from admin where email = ?";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    public static Boolean checkExistsByUsername(Connection connection, String username) {
        String sql = "select * from admin where username = ?";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

}
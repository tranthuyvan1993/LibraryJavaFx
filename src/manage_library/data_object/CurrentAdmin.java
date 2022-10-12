package manage_library.data_object;

import manage_library.database.DatabaseHandler;
import manage_library.data_object.admin.AdminDto;

import java.sql.*;

public class CurrentAdmin {

        private AdminDto currentAdmin = null;
        private static CurrentAdmin instance;
	private CurrentAdmin() {
    }

        public static CurrentAdmin getInstance() {
            if (instance == null) {
                instance = new CurrentAdmin();
            }
            return instance;
        }

        public void getAdminInfo(String username) {
            Connection con = null;
            PreparedStatement statement = null;
            try {
                con = DatabaseHandler.getInstance().getDbConnection();
                String sql = "select * from admin where username = ? and status != -1";
                statement = con.prepareStatement(sql);
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String fullname = resultSet.getString("fullname");
                    String usena = resultSet.getString("username");
                    String phone = resultSet.getString("phone");
                    String email = resultSet.getString("email");
                    String role = resultSet.getString("role");
                    String status = resultSet.getString("status");
                    int two_auth = resultSet.getInt("two_auth");
                    String avatar = resultSet.getString("avatar");
                    this.currentAdmin=new AdminDto(id, two_auth, status,fullname,usena,phone,email,role, avatar);
                }
                resultSet.close();
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {

                if(statement!=null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(con!=null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public AdminDto getCurrentAdmin() {
            return this.currentAdmin;
        }
    }

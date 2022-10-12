package manage_library.data_object;

import manage_library.data_object.admin.Admin;
import manage_library.database.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrentAdminPass {
    private Admin currentAdminPass = null;
    private static CurrentAdminPass instance;
    private CurrentAdminPass() {
    }

    public static CurrentAdminPass getInstance() {
        if (instance == null) {
            instance = new CurrentAdminPass();
        }
        return instance;
    }
    public void getAdminPassword(String username) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            String sql = "select password from admin where username = ?";
            statement = con.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String password = resultSet.getString("password");
                this.currentAdminPass=new Admin(password);
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

    public Admin getCurrentAdminPass() {
        return this.currentAdminPass;
    }
}

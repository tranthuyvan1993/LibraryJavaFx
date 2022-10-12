package manage_library.database;

import manage_library.data_object.announce.Announce;
import manage_library.model.BorrowModel;
import manage_library.util.Constant;
import manage_library.util.Constant.Database;
import manage_library.util.TaskTimer;
import manage_library.util.email.EmailHandler;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class DatabaseHandler {
    private static DatabaseHandler databaseHandler;
    private final BorrowModel borrowModel = new BorrowModel();

    private DatabaseHandler() {}

    public static DatabaseHandler getInstance() {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler();
        }
        return databaseHandler;
    }

    public Connection getDbConnection() throws SQLException {
        String connectionString = "jdbc:mysql://" + Database.HOST + "/"
                + Database.DATABASE_NAME +"?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=UTF-8";
        return DriverManager.getConnection(connectionString, Database.USERNAME, Database.PASSWORD);
    }

    public void getAnnounceSendReader() throws SQLException {
        List<Announce> announces = borrowModel.getAnnounceToReader(getDbConnection());
        announces.forEach(announce -> {
            try {
                TaskTimer.sendEmailDueDate(announce);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    public void initDatabase() throws SQLException, FileNotFoundException {
        String rootPath = System.getProperty("user.dir");
        String sql = rootPath.replace("\\", "/") + "/data/data.sql";
        String mysqlUrl = "jdbc:mysql://" + Constant.Database.HOST;
        Connection con = DriverManager.getConnection(mysqlUrl, Constant.Database.USERNAME, Constant.Database.PASSWORD);
        ScriptRunner sr = new ScriptRunner(con);
        Reader reader = new BufferedReader(new FileReader(sql));
        sr.runScript(reader);
    }
}

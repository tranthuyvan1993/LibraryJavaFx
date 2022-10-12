package manage_library.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import manage_library.data_object.reader.ReaderDetail;
import manage_library.data_object.reader.ReaderItemDto;
import manage_library.util.*;
import manage_library.data_object.reader.Reader;
import manage_library.util.format.DateFormat;

import java.sql.*;

public class ReaderModel {

    public ObservableList<ReaderItemDto> getAllReader(Connection connection) throws SQLException {
        ObservableList<ReaderItemDto> readerItems = FXCollections.observableArrayList();
        String sql = "select * from reader where status != -1 order by created_at desc";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {

                ReaderItemDto reader = new ReaderItemDto(
                        rs.getInt("id"), rs.getInt("status") == 1 ? "Đang kích hoạt" : "Đang khóa", rs.getString("code"), rs.getString("name"), rs.getString("phone"), rs.getString("email"), rs.getString("address"), DateFormat.parseDateDatabaseToRender(rs.getString("created_at"))
                );
                readerItems.add(reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readerItems;

    }

    public int createOrUpdateReader(Connection connection, Reader reader) throws SQLException {
        String sqlCreate = "insert into reader(avatar, name, phone, email, address, status, accept_email, gender, code) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlUpdate = "update reader set avatar = ?, name = ?, phone = ?, email = ?, address = ?, status = ?, accept_email = ?, gender = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(reader.getId() == null ? sqlCreate : sqlUpdate)) {
            reader.trim();
            if (reader.getId() == null) {
                ConvertQuery.convertQuery(preparedStatement, reader.getAvatar(), reader.getName(), reader.getPhone(), reader.getEmail(), reader.getAddress(), reader.getStatus(), reader.getAcceptEmail(), reader.getGender(), reader.getCode());
            } else {
                ConvertQuery.convertQuery(preparedStatement, reader.getAvatar(), reader.getName(), reader.getPhone(), reader.getEmail(), reader.getAddress(), reader.getStatus(), reader.getAcceptEmail(), reader.getGender(), reader.getId());
            }
            preparedStatement.addBatch();
            return preparedStatement.executeBatch().length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteReader(Connection connection, int id) throws SQLException {
        String sql = "update reader set status = -1 where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, id);
            preparedStatement.addBatch();
            return preparedStatement.executeBatch().length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ReaderDetail findReaderById(Connection connection, int id) {
        String sql = "select * from reader where id = ? and status != - 1";
        ReaderDetail readerDetail = new ReaderDetail();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                readerDetail = new ReaderDetail(
                        rs.getInt("id"), rs.getString("code"), rs.getString("name"), rs.getString("avatar"), rs.getString("phone"), rs.getString("email"), rs.getString("address"), rs.getInt("status"), rs.getBoolean("accept_email"), rs.getString("gender")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readerDetail;
    }

    public Boolean checkExistsByCode(Connection connection, String code) {
        String sql = "select * from reader where code = ? and status != -1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, code);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.INTERNAL_ERROR);
        return true;
    }

    public static Boolean checkExistsByEmail(Connection connection, String email) {
        String sql = "select * from reader where email = ? and status != -1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, email);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.INTERNAL_ERROR);
        return true;
    }

    public static Boolean checkExistsByPhone(Connection connection, String phone) {
        String sql = "select * from reader where phone = ? and status != -1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, phone);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.INTERNAL_ERROR);
        return true;
    }

    public ObservableList<ReaderItemDto> refreshReader(Connection connection) throws SQLException {
        return getAllReader(connection);
    }

}

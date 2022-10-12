package manage_library.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import manage_library.data_object.book.Book;
import manage_library.data_object.book.BookDetail;
import manage_library.data_object.book.BookDto;
import manage_library.data_object.book.BookItem;
import manage_library.data_object.report.CategoryBookReport;
import manage_library.data_object.report.StatusBookReport;
import manage_library.database.DatabaseHandler;
import manage_library.util.AlertCustom;
import manage_library.util.Constant;
import manage_library.util.Navigator;
import manage_library.util.SnackBar;
import manage_library.util.format.CurrencyFormat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookModel {
    public static List<BookItem> getBookList(String sql) {
        List<BookItem> bookList = new ArrayList<>();
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String status = String.valueOf(resultSet.getInt("status"));
                if (status.equals("1")) {
                    status = "Có sẵn";
                } else if (status.equals("0")) {
                    status = "Đang cho mượn";
                }
                resultSet.getString(
                        "image");
                BookItem bookItem = new BookItem(
                        resultSet.getInt("categoryId"),
                        resultSet.getInt("bookId"),
                        resultSet.getInt("bookDetailId"),
                        resultSet.getString("bookName"),
                        resultSet.getString("categoryName"),
                        resultSet.getString("publisher"),
                        resultSet.getString("auth"),
                        resultSet.getString("image"),
                        resultSet.getString("description"),
                        resultSet.getString("code"),
                        resultSet.getString("note"),
                        status,
                        CurrencyFormat.simpleCurrencyFormat(resultSet.getLong("price")));

                bookList.add(bookItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public static void insertBookDetail(BookDetail bookDetail) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            String sql = "insert into book_detail (code, book_id,note)"
                    + "value(?,?,?)";
            statement = con.prepareStatement(sql);
            statement.setString(1, bookDetail.getCode());
            statement.setInt(2, bookDetail.getBook_id());
            statement.setString(3, bookDetail.getNote());
            int count = statement.executeUpdate();
            if (count > 0) {
                SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.CREATE_SUCCESS);
                Navigator.getInstance().redirectTo(Navigator.LIST_BOOK, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
            } else {
                SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.CREATE_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertBook(Book book) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            String sql = "insert into book (name, description, publisher, auth, image,category_id, price)"
                    + "value(?,?,?,?,?,?,?)";
            statement = con.prepareStatement(sql);
            statement.setString(1, book.getName());
            statement.setString(2, book.getDescription());
            statement.setString(3, book.getPublisher());
            statement.setString(4, book.getAuth());
            statement.setString(5, book.getImage());
            statement.setInt(6, book.getCategory_id());
            statement.setLong(7, book.getPrice());
            int count = statement.executeUpdate();
            if (count > 0) {
                SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.CREATE_SUCCESS);
                Navigator.getInstance().redirectTo(Navigator.LIST_BOOK, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
            } else {
                SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.CREATE_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getBookId(String sql) {
        int bookId = 0;
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                bookId = resultSet.getInt("id");
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
        return bookId;
    }
    public static Book findBookByName(String bookName) {
        Book book= null;
        String sql = "select bd.status as status,b.name as bookName, b.auth as auth,b.description, " +
                "b.image as image, b.price as price, b.publisher as publisher, c.name as categoryName " +
                "from library.book b join library.book_detail bd join library.category c " +
                "where b.category_id = c.id and b.id = bd.book_id and bd.status!=-1 and b.name = ?";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, bookName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
//                String status = String.valueOf(resultSet.getInt("status"));
                book = new Book(
                        resultSet.getString("categoryName"),
                        resultSet.getString("bookName"),
                        resultSet.getString("publisher"),
                        resultSet.getString("auth"),
                        resultSet.getString("image"),
                        resultSet.getString("description"),
                        resultSet.getLong("price"));
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
        return book;
    }
    public static BookDto findBookById(String sql) {
        BookDto bookDto = null;
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String status = String.valueOf(resultSet.getInt("status"));
                if (status.equals("1")) {
                    status = "Có sẵn";
                } else if (status.equals("0")) {
                    status = "Đang cho mượn";
                }
                bookDto = new BookDto(
                        resultSet.getInt("bookDetailId"),
                        resultSet.getInt("bookId"),
                        status,
                        resultSet.getString("bookName"),
                        resultSet.getString("categoryName"),
                        resultSet.getString("publisher"),
                        resultSet.getString("auth"),
                        resultSet.getString(
                                "image"),
                        resultSet.getString("description"),
                        resultSet.getString("code"),
                        resultSet.getString("note"),
                        resultSet.getLong("price"));
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
        return bookDto;
    }

    public static int getCategoryId(String cateName) {
        int categoryId = 1;
        Connection con = null;
        PreparedStatement statement = null;
        String sql = "SELECT id FROM library.category where name = '" + cateName + "'";
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categoryId = resultSet.getInt("id");
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
        return categoryId;
    }

    public static int getBookId(int bookDetailId) {
        int bookId = 0;
        Connection con = null;
        PreparedStatement statement = null;
        String sql = "select b.id from library.book b, library.book_detail bd where b.id = bd.book_id and bd.id = " + bookDetailId;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                bookId = resultSet.getInt("b.id");
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
        return bookId;
    }

    private static boolean checkStatusBook(int id) {
        Connection con = null;
        PreparedStatement statement = null;
        Integer statusBookDetail = null;
        try {
            String sql = "select status from book_detail where id = ?";
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                statusBookDetail = resultSet.getInt("status");
            }
            if (statusBookDetail == 1) {
                return true;
            } else {
                return false;
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
        return false;
    }

    public static void deleteBook(int id) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            if (!checkStatusBook(id)) {
                AlertCustom.show(Alert.AlertType.WARNING, "Xoá Sách",
                        null, "Sách đang cho mượn, không thể xoá!");
            } else {
                String sql = "update book_detail set status = -1 where id = " + id;
                con = DatabaseHandler.getInstance().getDbConnection();
                statement = con.prepareStatement(sql);
                int count = statement.executeUpdate();

                if (count > 0) {
                    ButtonType button = AlertCustom.show(Alert.AlertType.INFORMATION, "Xoá Sách",
                            null, "Xoá thành công!");
                    if (button == ButtonType.OK) {
                        Navigator.getInstance().redirectTo(Navigator.LIST_BOOK, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                    }
                } else {
                    AlertCustom.show(Alert.AlertType.WARNING, "Xoá Sách",
                            null, "Không thể xoá!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
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

    public static Boolean checkExistsByBookName(Connection connection, String bookName) {
//        String sql = "select * from book where name = ?";
        String sql = "select * from library.book b join library.book_detail bd where b.id = bd.book_id and bd.status!=-1 and b.name = ?";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            statement.setString(1, bookName);
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

    public static Boolean checkExistsByBookCode(Connection connection, String bookCode) {
        String sql = "select * from book_detail where code = ? and status != -1";
        Connection con = null;
        try (PreparedStatement statement =  DatabaseHandler.getInstance().getDbConnection().prepareStatement(sql)){
            statement.setString(1, bookCode);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void updateBook(String sql) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            statement.executeUpdate();
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

    public static void updateBookDetail(String sql) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseHandler.getInstance().getDbConnection();
            statement = con.prepareStatement(sql);
            int count = statement.executeUpdate();
            if (count > 0) {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.INFORMATION, "Cập nhật thông tin", null, "Cập nhật thành công");
                if (buttonType == ButtonType.OK) {
                    try {
                        Navigator.getInstance().redirectTo(Navigator.LIST_BOOK, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                ButtonType buttonType = AlertCustom.show(Alert.AlertType.WARNING, "Cập nhật thông tin", null, "Không thể cập nhật!");
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

    public List<CategoryBookReport> getCategoryBookReport(Connection connection) {
        String sql = "select c.name as categoryName, count(bd.id) as totalBook from book_detail bd left join book bk on bk.id = bd.book_id left join category c on c.id = bk.category_id where bd.status != -1 group by c.name order by c.name";

        List<CategoryBookReport> categoryBookReports = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                CategoryBookReport revenueReport = new CategoryBookReport(
                        rs.getString("categoryName"), rs.getInt("totalBook")
                );
                categoryBookReports.add(revenueReport);
            }
            return categoryBookReports;
        } catch (Exception e) {
            e.printStackTrace();
        }
        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.INTERNAL_ERROR);
        return null;
    }

    public List<StatusBookReport> getStatusAllBook(Connection connection) {
        String sql = "select count(*) as totalBook, case bd.status when -1 then 'LOST_BOOK' when 0 then 'BORROWED' when 1 then 'AVAILABLE' end as statusBook from book_detail bd group by bd.status ORDER BY bd.status desc";

        List<StatusBookReport> statusBookReports = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                StatusBookReport revenueReport = new StatusBookReport(
                        convertStatusBook(rs.getString("statusBook")), rs.getInt("totalBook")
                );
                statusBookReports.add(revenueReport);
            }
            return statusBookReports;
        } catch (Exception e) {
            e.printStackTrace();
        }
        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.INTERNAL_ERROR);
        return null;
    }

    private String convertStatusBook(String statusBook) {
        return statusBook.equals(Constant.STATUS_BOOK.LOST_BOOK.name()) ? "Mất sách" : (statusBook.equals(Constant.STATUS_BOOK.AVAILABLE.name()) ? "Có sẵn" : "Đang mượn");
    }
}

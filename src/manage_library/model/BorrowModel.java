package manage_library.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import manage_library.data_object.announce.Announce;
import manage_library.data_object.announce.AnnounceBorrowItem;
import manage_library.data_object.borrow.*;
import manage_library.data_object.report.RevenueReport;
import manage_library.util.*;
import manage_library.util.format.CurrencyFormat;
import manage_library.util.format.DateFormat;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BorrowModel {
    private final Gson gson = new Gson();
    public ObservableList<BorrowItem> getAllBorrow(Connection connection, String textSearch) throws SQLException {
        ObservableList<BorrowItem> borrowItems = FXCollections.observableArrayList();
        String sql = "select b.id, b.borrow_date as borrowDate, b.status, r.id as readerId, r.name as readerName, r.code as readerCode, r.phone as readerPhone, bk.name as bookName, bk.auth, bk.price, b.due_date as dueDate, b.return_date as returnDate, bd.code as bookCode, b.note from borrow b left join reader r on r.id = b.reader_id left join book_detail bd on bd.id = b.book_detail_id left join book bk on bk.id = bd.book_id where (r.name like concat('%', ?, '%') or r.code like concat('%', ?, '%') or r.phone like concat('%', ?, '%') or bk.name like concat('%', ?, '%') or bk.auth like concat('%', ?, '%')) and r.status != -1 order by b.borrow_date desc";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, textSearch, textSearch, textSearch, textSearch, textSearch);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                BorrowItem borrow = new BorrowItem(
                        rs.getInt("id"), rs.getInt("readerId"), DateFormat.parseDateDatabaseToRender(rs.getString("borrowDate")), rs.getInt("status"), rs.getString("readerCode"), rs.getString("readerName"), rs.getString("readerPhone"),rs.getString("bookName"), rs.getString("bookCode"), rs.getString("auth"), CurrencyFormat.simpleCurrencyFormat(rs.getLong("price")), CurrencyFormat.ceilCurrencyFormat(rs.getLong("price") * 1.1), DateFormat.parseDateDatabaseToRender(rs.getString("dueDate")), DateFormat.parseDateDatabaseToRender(rs.getString("returnDate")), rs.getString("note")
                );
                borrowItems.add(borrow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return borrowItems;

    }

    public boolean createOrUpdateBorrow(Connection connection, BorrowDetail borrowDetail) throws IOException {
        if (borrowDetail.getBorrowAddBookItems().isEmpty()) {
            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.MUST_HAS_LEAST_A_BOOK_VALID);
            return false;
        }

        BorrowAddBookItem lastBook = borrowDetail.getBorrowAddBookItems().get(borrowDetail.getBorrowAddBookItems().size() - 1);
        String sqlUpdateReader = "update reader set accept_email = 1 where status = 1 and id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateReader)) {
            ConvertQuery.convertQuery(preparedStatement, borrowDetail.getReaderId());
            int rs = preparedStatement.executeUpdate();
            if (rs != 1) {
                SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.UPDATE_ACCEPT_EMAIL_READER_ERROR);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lastBook.getBorrowId() != null) {
            if (lastBook.getBorrowStatus() == 3 || lastBook.getBorrowStatus() == 1 || lastBook.getBorrowStatus() == 2) {
                Timestamp returnDate = null;
                if (lastBook.getBorrowStatus() != 2) {
                    returnDate = Timestamp.from(Instant.now());
                    int statusBook = 1;
                    if (lastBook.getBorrowStatus() == 3) {
                        statusBook = -1;
                        if (borrowDetail.getBorrowAddBookItems().stream().anyMatch(book -> !Validation.textNotEmpty(book.getBorrowNote()))) {
                            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.MUST_HAS_REASON_READER_DO_NOT_RETURN_BOOK);
                            return false;
                        }
                        ButtonType buttonType = AlertCustom.show(Alert.AlertType.CONFIRMATION, "Xác nhận trả sách", "Sách vẫn chưa được trả, bạn vẫn muốn tiếp tục?\n(Khách hàng sẽ mất tiền cọc!)", "");
                        if (buttonType == ButtonType.CANCEL) {
                            return false;
                        }
                    }
                    String sqlUpdateBookReturn = "update book_detail set status = ? where status = 0 and id = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateBookReturn)) {
                        ConvertQuery.convertQuery(preparedStatement, statusBook, borrowDetail.getBorrowAddBookItems().get(0).getBookDetailId());
                        int rs = preparedStatement.executeUpdate();
                        if (rs == 0) {
                            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.UPDATE_STATUS_BOOK_ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (lastBook.getBorrowStatus() == 1) {
                        String deleteAnnounce = "delete from announce where user_id = ? and target_id = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteAnnounce)) {
                            ConvertQuery.convertQuery(preparedStatement, borrowDetail.getReaderId(), borrowDetail.getBorrowAddBookItems().get(0).getBookDetailId());
                            int rs = preparedStatement.executeUpdate();
                            if (rs == 0) {
                                SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.DELETE_ANNOUNCE_ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String updateAnnounce = "delete from announce where user_id = ? and target_id = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(updateAnnounce)) {
                            ConvertQuery.convertQuery(preparedStatement, borrowDetail.getReaderId(), borrowDetail.getBorrowAddBookItems().get(0).getBookDetailId());
                            preparedStatement.executeUpdate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String findEmailAndBookNameByBorrowId = "select r.email as email, bk.name as bookName from borrow b left join reader r on b.reader_id = r.id left join book_detail bd on b.book_detail_id = bd.id left join book bk on bk.id = bd.book_id where b.id = ?";
                        try (PreparedStatement ps = connection.prepareStatement(findEmailAndBookNameByBorrowId)) {
                            ConvertQuery.convertQuery(ps, borrowDetail.getBorrowAddBookItems().get(0).getBorrowId());
                            ResultSet data = ps.executeQuery();
                            while (data.next()) {
                                List<AnnounceBorrowItem> announceBorrowItems = new ArrayList<>();
                                announceBorrowItems.add(new AnnounceBorrowItem(data.getString("bookName"), null));
                                Announce announce = new Announce(data.getString("email"), DateFormat.parseDateToDateString(new Date()), Constant.AnnounceType.ANNOUNCE_EXPIRED_BORROW, announceBorrowItems);
                                TaskTimer.sendEmailDueDate(announce);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    String updateAnnounce = "update announce set announce_time = ? where user_id = ? and target_id = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateAnnounce)) {
                        ConvertQuery.convertQuery(preparedStatement, borrowDetail.getBorrowAddBookItems().get(0).getDueDate().minusDays(1), borrowDetail.getReaderId(), borrowDetail.getBorrowAddBookItems().get(0).getBookDetailId());
                        int rs = preparedStatement.executeUpdate();
                        if (rs == 0) {
                            String sqlCreateAnnounce = "insert into announce(user_id, announce_time, announce_type, target_id) values (?, ?, ?, ?)";
                            try (PreparedStatement ps = connection.prepareStatement(sqlCreateAnnounce)) {
                                ConvertQuery.convertQuery(ps, borrowDetail.getReaderId(), borrowDetail.getBorrowAddBookItems().get(0).getDueDate().minusDays(1), Constant.AnnounceType.ANNOUNCE_DUE_BORROW, borrowDetail.getBorrowAddBookItems().get(0).getBookDetailId());
                                ps.addBatch();
                                ps.executeBatch();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String sqlUpdateBorrow = "update borrow set status = ?, note = ?, due_date = ?, return_date = ? where id = ?";
                for (int i = 0; i < borrowDetail.getBorrowAddBookItems().size(); i ++) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateBorrow)) {
                        ConvertQuery.convertQuery(preparedStatement, borrowDetail.getBorrowAddBookItems().get(0).getBorrowStatus(), borrowDetail.getBorrowAddBookItems().get(i).getBorrowNote(), borrowDetail.getBorrowAddBookItems().get(i).getDueDate() != null ? borrowDetail.getBorrowAddBookItems().get(i).getDueDate() : LocalDate.now().plusDays(Constant.PlusDayDueDate.PLUS_DAY), borrowDetail.getBorrowAddBookItems().get(i).getReturnDate() != null ? borrowDetail.getBorrowAddBookItems().get(i).getReturnDate() : returnDate, borrowDetail.getBorrowAddBookItems().get(i).getBorrowId());
                        preparedStatement.addBatch();
                        int rs = preparedStatement.executeBatch().length;
                        if (rs > 0) {
                            SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.UPDATE_SUCCESS);
                            Navigator.getInstance().redirectTo(Navigator.LIST_BORROW, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                            return true;
                        } else {
                            SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.COMMON_ERROR);
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            } else {
                SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.UPDATE_SUCCESS);
                Navigator.getInstance().redirectTo(Navigator.LIST_BORROW, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                return true;
            }
        } else {
            String sqlUpdateBookBorrow = "update book_detail set status = 0 where status = 1 and id = ?" + String.join("", Collections.nCopies(borrowDetail.getBorrowAddBookItems().size() - 1, " or id = ?"));

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateBookBorrow)) {
                Integer[] listIds = new Integer[borrowDetail.getBorrowAddBookItems().size()];
                for (int i = 0; i < borrowDetail.getBorrowAddBookItems().size(); i ++) {
                    listIds[i] = borrowDetail.getBorrowAddBookItems().get(i).getBookDetailId();
                }
                ConvertQuery.convertQuery(preparedStatement, gson.toJson(listIds));
                int rs = preparedStatement.executeUpdate();
                if (rs == 0) {
                    SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.UPDATE_STATUS_BOOK_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String sqlCreateAnnounce = "insert into announce(user_id, announce_time, announce_type, target_id) values (?, ?, ?, ?)" + String.join("", Collections.nCopies(borrowDetail.getBorrowAddBookItems().size() - 1, ", (?, ?, ?, ?)"));
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateAnnounce)) {
                ConvertQuery.convertQuery(preparedStatement, new CreateBorrowAnnounce(borrowDetail));
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String sqlCreateBorrow = "insert into borrow(reader_id, book_detail_id, due_date, return_date, note) values (?, ?, ?, null, ?)" + String.join("", Collections.nCopies(borrowDetail.getBorrowAddBookItems().size() - 1, ", (?, ?, ?, null, ?)"));
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateBorrow)) {
                ConvertQuery.convertQuery(preparedStatement, borrowDetail);
                preparedStatement.addBatch();
                int rs = preparedStatement.executeBatch().length;
                if (rs > 0) {
                    SnackBar.show(Constant.SnackBarAction.SUCCESS, Constant.SnackBarMessage.BORROW_SUCCESS);
                    Navigator.getInstance().redirectTo(Navigator.LIST_BORROW, (ScrollPane) Navigator.getInstance().getStage().getScene().lookup("#rootScroll"));
                    return true;
                } else {
                    SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.COMMON_ERROR);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public BorrowDetail findCurrentBorrowOfUserByCode(Connection connection, String code) {
        String sql = "select r.id as readerId, r.status as readerStatus, r.code as readerCode, r.name as readerName, r.phone as readerPhone, r.accept_email as readerAcceptEmail, b.note as borrowNote, case when b.reader_id = r.id then concat('[', group_concat(JSON_OBJECT('borrowId', b.id, 'bookCode', bd.code, 'bookName', bk.name, 'auth', bk.auth, 'price', bk.price, 'bookDetailId', bd.id, 'bookDetailStatus', bd.status, 'note', bd.note, 'image', bk.image, 'borrowDate', b.borrow_date, 'dueDate', b.due_date, 'returnDate', b.return_date, 'borrowNote', b.note, 'borrowStatus', b.status)), ']') end as borrowBooks from reader r left join (select b.* from borrow b left join reader r on r.id = b.reader_id where (b.status = 0 or b.status = 2) and r.code = ?) b on b.reader_id = r.id left join book_detail bd on bd.id = b.book_detail_id left join book bk on bk.id = bd.book_id where r.status != -1 && r.code = ? group by r.id, r.code, r.name, r.phone, r.accept_email, r.status";
        BorrowDetail borrowDetail = null;
        List<BorrowAddBookItem> borrowAddBookItems = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, code, code);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String jsonBorrow = rs.getString("borrowBooks");
                List<BorrowOfUserItem> borrowOfUserItems = gson.fromJson(jsonBorrow, new TypeToken<List<BorrowOfUserItem>>(){}.getType());
                if (borrowOfUserItems != null) borrowAddBookItems = borrowOfUserItems.stream().map(b -> {
                    try {
                        return new BorrowAddBookItem(b);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
                borrowDetail = new BorrowDetail(
                        rs.getInt("readerId"), rs.getString("readerCode"), rs.getInt("readerStatus"), rs.getString("readerName"), rs.getString("readerPhone"), rs.getBoolean("readerAcceptEmail"), borrowAddBookItems
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return borrowDetail;
    }

    public BorrowDetail findAllBorrowOfUserByBorrowId(Connection connection, int borrowId) {
        String sql = "select r.id as readerId, r.status as readerStatus, r.code as readerCode, r.name as readerName, r.phone as readerPhone, r.accept_email as readerAcceptEmail, b.note as borrowNote, concat('[', group_concat(JSON_OBJECT('borrowId', b.id, 'bookCode', bd.code, 'bookName', bk.name, 'auth', bk.auth, 'price', bk.price, 'bookDetailId', bd.id, 'bookDetailStatus', bd.status, 'note', bd.note, 'image', bk.image, 'borrowDate', b.borrow_date, 'dueDate', b.due_date, 'returnDate', b.return_date, 'borrowNote', b.note, 'borrowStatus', b.status)), ']') as borrowBooks from reader r left join borrow b on b.reader_id = r.id left join book_detail bd on bd.id = b.book_detail_id left join book bk on bk.id = bd.book_id where r.status != -1 and b.id = ? group by r.id, r.code, r.name, r.phone, r.accept_email, r.status";
        BorrowDetail borrowDetail = null;
        List<BorrowAddBookItem> borrowAddBookItems = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, borrowId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String jsonBorrow = rs.getString("borrowBooks");
                List<BorrowOfUserItem> borrowOfUserItems = gson.fromJson(jsonBorrow, new TypeToken<List<BorrowOfUserItem>>(){}.getType());
                if (borrowOfUserItems != null) borrowAddBookItems = borrowOfUserItems.stream().map(b -> {
                    try {
                        return new BorrowAddBookItem(b);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
                borrowDetail = new BorrowDetail(
                        rs.getInt("readerId"), rs.getString("readerCode"), rs.getInt("readerStatus"), rs.getString("readerName"), rs.getString("readerPhone"), rs.getBoolean("readerAcceptEmail"), borrowAddBookItems
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return borrowDetail;
    }

    public List<Announce> getAnnounceToReader(Connection connection) {
        String sql = "select r.email as email, a.announce_time as announceTime, a.announce_type as announceType, concat('[', group_concat(JSON_OBJECT('bookName', bk.name, 'announceId', a.id)), ']') as announceBorrowItems from announce a left join reader r on a.user_id = r.id left join book_detail bd on bd.id = a.target_id left join book bk on bd.book_id = bk.id where r.email != '' && r.status = 1 and r.accept_email = 1 and a.announce_time >= ? and a.announce_time <= ? group by a.user_id, a.announce_time, a.announce_type";
        List<Announce> announces = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, DateFormat.parseLocalDateToDateString(LocalDate.now(), Constant.AddTimeToLocalDate.OPEN), DateFormat.parseLocalDateToDateString(LocalDate.now(), Constant.AddTimeToLocalDate.CLOSE));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String jsonBorrow = rs.getString("announceBorrowItems");
                List<AnnounceBorrowItem> announceBorrowItems = gson.fromJson(jsonBorrow, new TypeToken<List<AnnounceBorrowItem>>(){}.getType());
                Announce announce = new Announce(
                        rs.getString("email"), rs.getString("announceTime"), rs.getString("announceType"), announceBorrowItems
                );
                announces.add(announce);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return announces;
    }

    public BorrowAddBookItem findBookByCode(Connection connection, String bookCode) {
        String sql = "select bd.id, bd.status, b.image, bd.code, b.name, b.auth, b.price, bd.note as bookDetailNote from book_detail bd left join book b on b.id = bd.book_id where bd.status != -1 and bd.code = ?";
        BorrowAddBookItem borrowAddBookItem = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, bookCode);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                borrowAddBookItem = new BorrowAddBookItem(
                        null, rs.getInt("id"), rs.getInt("status"), rs.getString("image"), rs.getString("code"), rs.getString("name"), rs.getString("auth"), rs.getLong("price"), LocalDate.now(), LocalDate.now().plusDays(Constant.PlusDayDueDate.PLUS_DAY), null, rs.getString("bookDetailNote"), null, null
                );
            }
            return borrowAddBookItem;
        } catch (Exception e) {
            e.printStackTrace();
        }
        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.INTERNAL_ERROR);
        return null;
    }

    public List<RevenueReport> getRevenueReport(Connection connection, String interval, String startDate, String endDate) throws SQLException {
        String timeReader = "";
        String timeRevenue = "";
        List<RevenueReport> revenueReports = new ArrayList<>();
        switch (interval) {
            case Constant.ReportInterval.MONTH:
                timeReader = "concat(MONTH(r.created_at), '/', YEAR(r.created_at)) ";
                timeRevenue = "concat(MONTH(b.return_date), '/', YEAR(b.return_date)) ";
                break;
            case Constant.ReportInterval.YEAR:
                timeReader = "YEAR(r.created_at) ";
                timeRevenue = "YEAR(b.return_date) ";
                break;
            default:
                timeReader = "concat(DAY(r.created_at), '/', MONTH(r.created_at), '/', YEAR(r.created_at)) ";
                timeRevenue = "concat(DAY(b.return_date), '/', MONTH(b.return_date), '/', YEAR(b.return_date)) ";
                break;
        }

        String sql = "select rs.time, max(rs.totalReader) as totalReader, max(rs.totalRevenue) as totalRevenue from (select r.time, ifnull(r.totalReader, 0) as totalReader, 0 as totalRevenue from (select count(r.id) as totalReader, " + timeReader + " as time from reader r where r.created_at >= ? and r.created_at <= ? group by " + timeReader + " ) r union all select b.time, 0 as totalReader, ifnull(b.totalRevenue, 0) as totalRevenue from (select sum(bk.price) as totalRevenue, " + timeRevenue + " as time from borrow b left join book_detail bd on bd.id = b.book_detail_id left join book bk on bk.id = bd.book_id where b.status = 3 and b.return_date is not null and b.return_date >= ? and b.return_date <= ? group by " + timeRevenue + " ) b) rs group by rs.time order by rs.time";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ConvertQuery.convertQuery(preparedStatement, startDate, endDate, startDate, endDate);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RevenueReport revenueReport = new RevenueReport(
                        rs.getString("time"), rs.getLong("totalRevenue"), rs.getLong("totalReader")
                );
                revenueReports.add(revenueReport);
            }
            return revenueReports;
        } catch (Exception e) {
            e.printStackTrace();
        }
        SnackBar.show(Constant.SnackBarAction.ERROR, Constant.SnackBarMessage.INTERNAL_ERROR);
        return revenueReports;
    }

    public void deleteAnnounce(Connection connection, String ids) throws SQLException {
        String deleteAnnounce = "delete from announce where id = ?" + String.join("", Collections.nCopies(ids.split(",").length - 1, " or id = ?"));
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteAnnounce)) {
            ConvertQuery.convertQuery(preparedStatement, ids);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package manage_library.util;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import manage_library.data_object.announce.Announce;
import manage_library.database.DatabaseHandler;
import manage_library.model.BorrowModel;
import manage_library.util.email.EmailHandler;
import manage_library.util.format.DateFormat;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

public class TaskTimer {
    private final static BorrowModel borrowModel = new BorrowModel();
    private final static Gson gson = new Gson();
    public static void sendEmailDueDate(Announce announce) throws ParseException {
        TimerTask timerTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                String[] bookName = new String[announce.getAnnounceBorrowItems().size()];
                for (int i = 0; i < announce.getAnnounceBorrowItems().size(); i ++) {
                    bookName[i] = announce.getAnnounceBorrowItems().get(i).getBookName();
                }
                if (announce.getAnnounceType().equals(Constant.AnnounceType.ANNOUNCE_DUE_BORROW)) {
                    new EmailHandler(Constant.ActionEmail.ANNOUNCE_DUE_BORROW, announce.getEmail(), String.format(Constant.ContentEmail.ANNOUNCE_DUE_DATE_BORROW_BOOK, announce.getAnnounceBorrowItems().size(), StringUtils.join(bookName, ", "), DateFormat.parseDateDatabaseToRender(DateFormat.parseLocalDateToDateString(LocalDate.now(), Constant.AddTimeToLocalDate.OPEN)), DateFormat.parseDateDatabaseToRender(DateFormat.parseLocalDateToDateString(LocalDate.now(), Constant.AddTimeToLocalDate.CLOSE)), Constant.LibraryInfo.phoneNumber)).start();
                } else if (announce.getAnnounceType().equals(Constant.AnnounceType.ANNOUNCE_EXPIRED_BORROW)) {
                    new EmailHandler(Constant.ActionEmail.ANNOUNCE_EXPIRED_BORROW, announce.getEmail(), String.format(Constant.ContentEmail.ANNOUNCE_EXPIRED_DATE_BORROW_BOOK, announce.getAnnounceBorrowItems().size(), StringUtils.join(bookName, ", "), DateFormat.parseDateDatabaseToRender(DateFormat.parseLocalDateToDateString(LocalDate.now(), Constant.AddTimeToLocalDate.OPEN)), DateFormat.parseDateDatabaseToRender(DateFormat.parseLocalDateToDateString(LocalDate.now(), Constant.AddTimeToLocalDate.CLOSE)), Constant.LibraryInfo.phoneNumber)).start();
                }

                Integer[] listIds = new Integer[announce.getAnnounceBorrowItems().size()];
                for (int i = 0; i < announce.getAnnounceBorrowItems().size(); i ++) {
                    listIds[i] = announce.getAnnounceBorrowItems().get(i).getAnnounceId();
                }
                borrowModel.deleteAnnounce(DatabaseHandler.getInstance().getDbConnection(), gson.toJson(listIds));

            }
        };
        Timer timer = new Timer("Timer");
        if (new Date().before(DateFormat.parseDateStringToDate(announce.getAnnounceTime()))) {
            timer.schedule(timerTask, DateFormat.parseDateStringToDate(announce.getAnnounceTime()));
        } else {
            timer.schedule(timerTask, 0);
        }

    }
}

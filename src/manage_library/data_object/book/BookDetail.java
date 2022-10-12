package manage_library.data_object.book;

import java.time.LocalDate;

public class BookDetail {
    int id, book_id;
    String code, note;

    public BookDetail() {
    }

    public BookDetail(int book_id, String code, String note) {
        this.id = id;
        this.book_id = book_id;
        this.code = code;
        this.note = note;
    }

    @Override
    public String toString() {
        return "BookDetail{" +
                "id=" + id +
                ", book_id=" + book_id +
                ", code='" + code + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}

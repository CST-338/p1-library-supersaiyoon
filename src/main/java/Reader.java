import Utilities.Code;

import java.util.ArrayList;
import java.util.List;

/**
 * Part 2 of Library Project. Represents a person reading the book(s).
 * @author Brian Yoon
 * @since 2023-11-05
 */
public class Reader {
    /**
     * Holds integers used to index reader information in String array.
     */
    public static final int CARD_NUMBER_ = 0;
    public static final int NAME = 1;
    public static final int PHONE_ = 2;
    public static final int BOOK_COUNT_ = 3;
    public static final int BOOK_START_ = 4;

    private int cardNumber;
    private String name;
    private String phone;
    private List<Book> books;

    public Reader(int cardNumber, String name, String phone) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.phone = phone;
        this.books = new ArrayList<>();
    }

    public String toString() {
        return name + " (#" + cardNumber + ") has checked out " + books.toString();
    }

    public Code addBook(Book book) {
        if (books.contains(book)) {
            return Code.BOOK_ALREADY_CHECKED_OUT_ERROR;
        }

        books.add(book);
        return Code.SUCCESS;
    }

    public Code removeBook(Book book) {
        try {
            if (books.contains(book)) {
                books.remove(book);
                return Code.SUCCESS;
            }
            else {
                return Code.READER_DOESNT_HAVE_BOOK_ERROR;
            }
        }
        catch (Exception e) {
            return Code.READER_COULD_NOT_REMOVE_BOOK_ERROR;
        }
    }

    public boolean hasBook(Book book) {
        return books.contains(book);
    }

    public int getBookCount() {
        return books.size();
    }

    /**
     * Auto-generated methods below.
     */
    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reader reader = (Reader) o;

        if (getCardNumber() != reader.getCardNumber()) return false;
        if (getName() != null ? !getName().equals(reader.getName()) : reader.getName() != null) return false;
        return getPhone() != null ? getPhone().equals(reader.getPhone()) : reader.getPhone() == null;
    }

    @Override
    public int hashCode() {
        int result = getCardNumber();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        return result;
    }
}
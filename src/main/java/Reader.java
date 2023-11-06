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

    /**
     * Information about the reader.
     */
    private int cardNumber;
    private String name;
    private String phone;
    private List<Book> books;

    /**
     * Constructs a new Reader object with the provided card number, name, and phone number.
     * Initializes list of books.
     *
     * @param cardNumber The unique card number assigned to the reader.
     * @param name The name of the reader.
     * @param phone The phone number of the reader.
     */
    public Reader(int cardNumber, String name, String phone) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.phone = phone;
        this.books = new ArrayList<>();
    }

    /**
     * Returns a string representation of the Reader object.
     *
     * @return A string containing the reader's name, card number, and a list of checked-out books.
     */
    public String toString() {
        return name + " (#" + cardNumber + ") has checked out " + books.toString();
    }

    /**
     * Adds a book to the reader's collection of checked-out books.
     *
     * @param book The book to be added to the collection.
     * @return A {@link Code} indicating the result of the operation:<br>
     *         - {@link Code#SUCCESS} if the book is successfully added.<br>
     *         - {@link Code#BOOK_ALREADY_CHECKED_OUT_ERROR} if the book is already in the collection.
     */
    public Code addBook(Book book) {
        if (books.contains(book)) {
            return Code.BOOK_ALREADY_CHECKED_OUT_ERROR;
        }

        books.add(book);
        return Code.SUCCESS;
    }

    /**
     * Attempts to remove a book from the reader's collection of checked-out books.
     *
     * @param book The book to be removed from the collection.
     * @return A {@link Code} indicating the result of the removal operation:<br>
     *         - {@link Code#SUCCESS} if the book is successfully removed.<br>
     *         - {@link Code#READER_DOESNT_HAVE_BOOK_ERROR} if the reader does not have the book.<br>
     *         - {@link Code#READER_COULD_NOT_REMOVE_BOOK_ERROR} if any other issue occurs during removal.
     */
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

    /**
     * Checks if the reader has a specific book in their collection of checked-out books.
     *
     * @param book The book to be checked for in the collection.
     * @return {@code true} if the reader has the specified book; {@code false} otherwise.
     */
    public boolean hasBook(Book book) {
        return books.contains(book);
    }

    /**
     * Returns the number of books in the reader's collection of checked-out books.
     *
     * @return The count of books in the reader's collection.
     */
    public int getBookCount() {
        return books.size();
    }

    /**
     * Auto-generated setters, getters, equals(), and hashCode() below.
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
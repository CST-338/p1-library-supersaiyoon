import java.time.LocalDate;

/**
 * Part 1 of Library Project. Represents a book.
 * @author Brian Yoon
 * @since 2023-11-05
 */
public class Book {
    /**
     * Holds integers used to index book information in String array.
     */
    public static final int ISBN_ = 0;
    public static final int TITLE_ = 1;
    public static final int SUBJECT_ = 2;
    public static final int PAGE_COUNT_ = 3;
    public static final int AUTHOR_ = 4;
    public static final int DUE_DATE_ = 5;

    /**
     * Book information.
     */
    private String isbn;
    private String title;
    private String subject;
    private int pageCount;
    private String author;
    private LocalDate dueDate;

    /**
     * Creates a new Book with the specified properties.
     *
     * @param isbn      The ISBN (International Standard Book Number) of the book.
     * @param title     The title of the book.
     * @param subject   The genre of the book.
     * @param pageCount The number of pages in the book.
     * @param author    The author of the book.
     * @param dueDate   The due date for the book if it's borrowed from a library.
     */
    public Book(String isbn, String title, String subject, int pageCount, String author, LocalDate dueDate) {
        this.isbn = isbn;
        this.title = title;
        this.subject = subject;
        this.pageCount = pageCount;
        this.author = author;
        this.dueDate = dueDate;
    }

    /**
     * Returns a string representation of the book, including the title, author, and ISBN.
     *
     * @return A string containing the book's title, author, and ISBN.
     */
    @Override
    public String toString() {
        return title + " by " + author + " ISBN: " + isbn;
    }

    /**
     * Retrieves the ISBN (International Standard Book Number) of the book.
     *
     * @return A string representing the ISBN of the book.
     */
    public String getISBN() {
        return isbn;
    }

    /**
     * Sets the ISBN (International Standard Book Number) for the book.
     *
     * @param isbn A string representing the ISBN to be set for the book.
     */
    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Retrieves the title of the book.
     *
     * @return A string representing the title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for the book.
     *
     * @param title A string representing the title to be set for the book.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the subject/genre of the book.
     *
     * @return A string representing the subject/genre of the book.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject/genre for the book.
     *
     * @param subject A string representing the subject/genre to be set for the book.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Retrieves the page count of the book.
     *
     * @return An integer representing the number of pages in the book.
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Sets the page count for the book.
     *
     * @param pageCount An integer representing the number of pages to be set for the book.
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Retrieves the author of the book.
     *
     * @return A string representing the author of the book.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author for the book.
     *
     * @param author A string representing the author to be set for the book.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Retrieves the due date of the book, if it's borrowed from a library.
     *
     * @return A LocalDate object representing the due date of the book.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date for the book if it's borrowed from a library.
     *
     * @param dueDate A LocalDate object representing the due date to be set for the book (year, month, day).
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Indicates whether some other book is identical to this book.
     * Compares all fields except the due date.
     *
     * @param o The book with which to compare.
     * @return true if this book is identical to the given book, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (getPageCount() != book.getPageCount()) return false;
        if (getISBN() != null ? !getISBN().equals(book.getISBN()) : book.getISBN() != null) return false;
        if (getTitle() != null ? !getTitle().equals(book.getTitle()) : book.getTitle() != null) return false;
        if (getSubject() != null ? !getSubject().equals(book.getSubject()) : book.getSubject() != null) return false;
        return getAuthor() != null ? getAuthor().equals(book.getAuthor()) : book.getAuthor() == null;
    }

    /**
     * Returns a hash code value for the book.
     *
     * @return An integer representing the hash code for the book.
     */
    @Override
    public int hashCode() {
        int result = getISBN() != null ? getISBN().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        result = 31 * result + getPageCount();
        result = 31 * result + (getAuthor() != null ? getAuthor().hashCode() : 0);
        return result;
    }
}
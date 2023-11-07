import Utilities.Code;

import java.util.HashMap;

/**
 * Part 3 of Library Project. Represents a bookshelf that contains books.
 * @author Brian Yoon
 * @since 2023-11-06
 */
public class Shelf {
    /**
     * Holds integers used to index shelf information in String array.
     */
    public static final int SHELF_NUMBER_ = 0;
    public static final int SUBJECT_ = 1;

    /**
     * Information about shelf and the books it stores.
     */
    private HashMap<Book, Integer> books;  // Books on this shelf.
    private int shelfNumber;
    private String subject;

    /**
     * Default constructor for creating a Shelf.
     * Will be removed in a future version.
     */
    public Shelf() {
    }

    /**
     * Constructs a Shelf with the specified shelf number and subject.
     *
     * @param shelfNumber The unique identifier for the shelf.
     * @param subject     The subject (or genre) of the books stored on the shelf.
     */
    public Shelf(int shelfNumber, String subject) {
        this.shelfNumber = shelfNumber;
        this.subject = subject;
    }

    /**
     * Returns a string representation of this Shelf object.
     *
     * @return A string containing the shelf's number and subject, separated by a colon.<br>
     * e.g., "2 : education"
     */
    @Override
    public String toString() {
        return shelfNumber + " : " + subject;
    }

    /**
     * Adds a book to the shelf and updates its count in the HashMap of books.
     *
     * @param book The book to add to the shelf.
     * @return A code indicating the result of the operation.
     */
    public Code addBook(Book book) {
        if (books.containsKey(book)) {
            // Book already exists on the shelf, increment the count.
            int newBookCount = books.get(book) + 1;
            books.put(book, newBookCount);
            return Code.SUCCESS;
        }
        else if (book.getSubject().equals(this.subject)) {
            // Book doesn't exist on the shelf but subject matches, add the book with a count of 1.
            books.put(book, 1);
            System.out.println(book + " added to shelf " + this);
            return Code.SUCCESS;
        }
        else {
            // Book does not exist on the shelf, and subject does not match.
            return Code.SHELF_SUBJECT_MISMATCH_ERROR;
        }
    }

    /**
     * Removes a book from the shelf and updates its count in the HashMap of books.
     *
     * @param book The book to remove from the shelf.
     * @return A code indicating the result of the operation.
     */
    public Code removeBook(Book book) {
        String bookTitle = book.getTitle();
        String shelfSubject = this.subject;

        if (!books.containsKey(book)) {
            // Book not stored on this shelf.
            System.out.println(bookTitle + " is not on shelf " + shelfSubject);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        int bookCount = books.get(book);
        if (bookCount == 0) {
            // Book is stored on this shelf but 0 quantity.
            System.out.println("No copies of " + bookTitle + " remain on shelf " + shelfSubject);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        // Book is present, and there are more than 0 copies.
        int newBookCount = bookCount - 1;
        books.put(book, newBookCount);
        System.out.println(bookTitle + " successfully removed from shelf " + shelfSubject);

        return Code.SUCCESS;
    }

    /**
     * Retrieves the count of a specific book on this shelf.
     *
     * @param book The book to check the count for.
     * @return The count of the specified book on the shelf, or -1 if the book is not found.
     */
    public int getBookCount(Book book) {
        Integer bookCount = books.get(book);  // Use Integer instead of 'int' so that bookCount can be null.
        return (bookCount != null) ? bookCount : -1;
    }

    public String listBooks() {
        return "listBooks() not implemented yet!";
    }

    /**
     * Auto-generated setters, getters, equals(), and hashCode() below.
     */
    public HashMap<Book, Integer> getBooks() {
        return books;
    }

    public void setBooks(HashMap<Book, Integer> books) {
        this.books = books;
    }

    public int getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(int shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shelf shelf = (Shelf) o;

        if (getShelfNumber() != shelf.getShelfNumber()) return false;
        return getSubject() != null ? getSubject().equals(shelf.getSubject()) : shelf.getSubject() == null;
    }

    @Override
    public int hashCode() {
        int result = getShelfNumber();
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        return result;
    }
}
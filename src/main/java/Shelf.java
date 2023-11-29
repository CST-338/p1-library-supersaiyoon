import Utilities.Code;

import java.util.HashMap;
import java.util.Map;

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
     * Default constructor for creating a Shelf.<br>
     * Per Shelf.java doc: "The no parameter constructor does nothing
     * and is a deprecated constructor.  It will be removed in future versions."<br>
     * However, the parameterized constructor is called here because
     * {@code ShelfTest.java} never calls the parameterized constructor.
     * {@code MainTest.java} fails if {@code books} is initialized in the no-parameter constructor.
     */
    public Shelf() {
        this(0, "");
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
        books = new HashMap<>();
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

    /**
     * Generates a formatted list of books and their details on the bookshelf, including the shelf's information.
     *
     * @return A string containing information about the books on the shelf, along with shelf number and subject.
     */
    public String listBooks() {
        StringBuilder bookList = new StringBuilder();

        // Compute quantity of all books on shelf
        int numBooksOnShelf = 0;
        for (int num : books.values()) {
            numBooksOnShelf += num;
        }

        // Determine 'book' or 'books' for correct output grammar
        String bookGrammar = (numBooksOnShelf == 1) ? "book" : "books";

        // Format and generate info about shelf
        String shelfInfo = numBooksOnShelf + " " + bookGrammar + " on shelf: " + shelfNumber + " : " + subject + "\n";
        bookList.append(shelfInfo);

        // Generate and format output about each book on shelf then append to bookList
        for (Map.Entry<Book, Integer> book : books.entrySet()) {
            String bookTitle = book.getKey().getTitle();
            String bookAuthor = book.getKey().getAuthor();
            String bookIsbn = book.getKey().getISBN();
            int numBooks = book.getValue();
            String bookInfo = bookTitle + " by " + bookAuthor + " ISBN: " + bookIsbn + " " + numBooks + "\n";
            bookList.append(bookInfo);
        }
        return bookList.toString();
    }

    /**
     * Retrieves a HashMap containing information about books and their corresponding quantities.
     *
     * @return A HashMap where the keys are Book objects, and the values are integers representing the quantities of each book.
     */
    public HashMap<Book, Integer> getBooks() {
        return books;
    }

    /**
     * Sets the collection of books and their corresponding quantities.
     *
     * @param books A HashMap where the keys are Book objects, and the values are integers representing the quantities of each book.
     */
    public void setBooks(HashMap<Book, Integer> books) {
        this.books = books;
    }

    /**
     * Retrieves the shelf number of a bookshelf.
     *
     * @return An integer representing the shelf number.
     */
    public int getShelfNumber() {
        return shelfNumber;
    }

    /**
     * Sets the shelf number for a bookshelf.
     *
     * @param shelfNumber An integer representing the shelf number to be set.
     */
    public void setShelfNumber(int shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    /**
     * Retrieves the subject of a book.
     *
     * @return A string representing the subject of the book.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject of a book.
     *
     * @param subject A string representing the subject to be set for the book.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Indicates whether the provided bookshelf
     * has the same shelf number and subject as this bookshelf.
     *
     * @param o The shelf with which to compare.
     * @return true if this shelf has same shelf number and subject as the provided bookshelf, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shelf shelf = (Shelf) o;

        if (getShelfNumber() != shelf.getShelfNumber()) return false;
        return getSubject() != null ? getSubject().equals(shelf.getSubject()) : shelf.getSubject() == null;
    }

    /**
     * Returns a hash code value for the bookshelf.
     *
     * @return An integer representing the hash code for the bookshelf.
     */
    @Override
    public int hashCode() {
        int result = getShelfNumber();
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        return result;
    }
}
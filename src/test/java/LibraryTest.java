import Utilities.Code;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 5/4 of Library Project. JUnit tests to verify correct output for methods.
 * @author Brian Yoon
 * @since 2023-11-21
 * @version  1.4.0
 */
class LibraryTest {

    Library csumb = null;

    String library00 = "Library00.csv";
    String library01 = "Library01.csv";
    String badBooks0 = "badBooks0.csv";
    String badBooks1 = "badBooks1.csv";
    String badShelves0 = "badShelves0.csv";
    String badShelves1 = "badShelves1.csv";
    String badReader0 = "badReader0.csv";
    String badReader1 = "badReader1.csv";

    // "Hitchhikers Guide To the Galaxy" book
    LocalDate hitchhikersDueDate = Library.convertDate("0000", Code.UNKNOWN_ERROR);
    Book hitchhikersBook = new Book("42-w-87", "Hitchhikers Guide To the Galaxy", "sci-fi", 42, "Douglas Adams", hitchhikersDueDate);

    // "Headfirst Java" book
    LocalDate headfirstDueDate = Library.convertDate("0000", Code.UNKNOWN_ERROR);
    Book headfirstBook = new Book("e1337", "Headfirst Java", "education", 1337, "Grady Booch", headfirstDueDate);

    // "Count of Monte Cristo" book
    LocalDate monteCristoDueDate = Library.convertDate("0000", Code.UNKNOWN_ERROR);
    Book monteCristoBook = new Book("5297", "Count of Monte Cristo", "Adventure", 999, "Alexandre Dumas", monteCristoDueDate);

    // "Dune" book
    LocalDate duneDueDate = Library.convertDate("0000", Code.UNKNOWN_ERROR);
    Book duneBook = new Book("34-w-34", "Dune", "sci-fi", 235, "Frank Herbert", duneDueDate);

    // sci-fi book
    LocalDate sciFiDueDate = Library.convertDate("1592-03-14", Code.UNKNOWN_ERROR);
    Book sciFiBook = new Book("3-141592", "Intro to Pi", "sci-fi", 1234, "Archimedes of Syracuse", sciFiDueDate);

    // Romance book (Romance shelf doesn't exist)
    LocalDate romanceDueDate = Library.convertDate("2007-02-14", Code.UNKNOWN_ERROR);
    Book romanceBook = new Book("4eva", "The Journal", "Romance", 143, "Nicholas Jolts", romanceDueDate);

    @BeforeEach
    void setUp() {
        csumb = new Library("CSUMB");
    }

    @AfterEach
    void tearDown() {
        csumb = null;
    }

    @Test
    void init_test() {
        //Bad file
        assertEquals(Code.FILE_NOT_FOUND_ERROR, csumb.init("nope.csv"));
        assertEquals(Code.BOOK_COUNT_ERROR, csumb.init(badBooks0));
        assertEquals(Code.BOOK_COUNT_ERROR, csumb.init(badBooks1) );
        assertEquals( Code.SHELF_COUNT_ERROR,csumb.init(badShelves0));
        assertEquals( Code.SHELF_NUMBER_PARSE_ERROR,csumb.init(badShelves1));
        // Non-number reader count
        assertEquals(Code.READER_COUNT_ERROR, csumb.init(badReader0));
        // Error with converting book count string to int
        assertEquals(Code.UNKNOWN_ERROR, csumb.init(badReader1));
    }

    @Test
    void init_goodFile_test() {
        assertEquals(Code.SUCCESS, csumb.init(library00));
    }

    @Test
    void addBook() {
        // Creates sci-fi shelf.
        csumb.init(library00);

        // Add book to shelf with matching book subject.
        assertEquals(Code.SUCCESS, csumb.addBook(sciFiBook));

        // Add book to non-existing shelf.
        assertEquals(Code.SHELF_EXISTS_ERROR, csumb.addBook(romanceBook));
    }

    @Test
    void returnBook() {
        // Creates sci-fi shelf.
        csumb.init(library00);

        // Return book to existing shelf (sci-fi).
        assertEquals(Code.SUCCESS, csumb.returnBook(sciFiBook));

        // Return book to non-existing shelf (Romance).

        assertEquals(Code.SHELF_EXISTS_ERROR, csumb.returnBook(romanceBook));
    }

    @Test
    void testReturnBook() {
        // Test returning book from reader.
        // Creates sci-fi shelf.
        csumb.init(library00);

        Reader drew = csumb.getReaderByCard(1);

        // Reader doesn't possess book at all.
        assertEquals(Code.READER_DOESNT_HAVE_BOOK_ERROR, csumb.returnBook(drew, romanceBook));

        // Reader has book but book doesn't exist in library.
        drew.addBook(romanceBook);
        assertEquals(Code.BOOK_NOT_IN_INVENTORY_ERROR, csumb.returnBook(drew, romanceBook));

        // Remove book that reader actually possesses.
        assertEquals(Code.SUCCESS, csumb.returnBook(drew, hitchhikersBook));
    }

    @Test
    void listBooks() {
        // 9 books total in this library.
        csumb.init(library00);
        int expectedBookCount = 9;

        assertEquals(expectedBookCount, csumb.listBooks());
    }

    @Test
    void checkOutBook() {
        csumb.init(library00);

        // Reader isn't registered with library.
        Reader jordan = new Reader(23, "Jordan", "555-555-5555");
        assertEquals(Code.READER_NOT_IN_LIBRARY_ERROR, csumb.checkOutBook(jordan, hitchhikersBook));

        // Reader exists but reached lending limit (5).
        Reader drew = csumb.getReaderByCard(1);
        // Add more than enough books to reader.
        drew.addBook(headfirstBook);
        drew.addBook(monteCristoBook);
        drew.addBook(duneBook);
        drew.addBook(sciFiBook);
        // Ensures there is inventory of book being checked out.
        csumb.addBook(hitchhikersBook);
        assertEquals(Code.BOOK_LIMIT_REACHED_ERROR, csumb.checkOutBook(drew, hitchhikersBook));

        // Book doesn't exist in library.
        Reader jennifer = csumb.getReaderByCard(2);
        assertEquals(Code.BOOK_NOT_IN_INVENTORY_ERROR, csumb.checkOutBook(jennifer, romanceBook));

        // Shelf for book doesn't exist.
        hitchhikersBook.setSubject("Travel");
        // Ensures there is inventory of book being checked out.
        csumb.addBook(hitchhikersBook);
        assertEquals(Code.SHELF_EXISTS_ERROR, csumb.checkOutBook(jennifer, hitchhikersBook));

        // Reader already possesses same book.
        jennifer.addBook(sciFiBook);
        // Ensures there is inventory of book being checked out.
        csumb.addBook(sciFiBook);
        assertEquals(Code.BOOK_ALREADY_CHECKED_OUT_ERROR, csumb.checkOutBook(jennifer, sciFiBook));

        // Book is successfully checked out.
        // Ensures there is inventory of book being checked out.
        csumb.addBook(duneBook);
        assertEquals(Code.SUCCESS, csumb.checkOutBook(jennifer, duneBook));
    }

    @Test
    void getBookByISBN() {
        csumb.init(library00);

        // Book exists in library.
        assertEquals(hitchhikersBook, csumb.getBookByISBN("42-w-87"));

        // Book doesn't exist in library.
        assertEquals(null, csumb.getBookByISBN("0000"));
    }

    @Test
    void listShelves() {
        csumb.init(library00);

        // 3 shelves total in this library.
        int expectedShelfCount = 3;

        assertEquals(expectedShelfCount, csumb.listShelves());
    }

    @Test
    void addShelf() {
        csumb.init(library00);

        // Pass Shelf object.
        Shelf sciFiShelf = new Shelf(4, "sci-fi");
        Shelf romanceShelf = new Shelf(5, "Romance");

        // Add shelf when another shelf with same subject exists.
        assertEquals(Code.SHELF_EXISTS_ERROR, csumb.addShelf(sciFiShelf));

        // Add new shelf.
        assertEquals(Code.SUCCESS, csumb.addShelf(romanceShelf));
    }

    @Test
    void testAddShelf() {
        csumb.init(library00);

        // Pass subject only instead of shelf object.
        // Add shelf when another shelf with same subject exists.
        assertEquals(Code.SHELF_EXISTS_ERROR, csumb.addShelf("sci-fi"));

        // Add new shelf.
        assertEquals(Code.SUCCESS, csumb.addShelf("Romance"));
    }

    @Test
    void getShelf() {
        csumb.init(library00);

        // Pass shelf subject.
        Shelf sciFiShelf = new Shelf(1, "sci-fi");

        // Shelf exists.
        assertEquals(sciFiShelf, csumb.getShelf("sci-fi"));

        // Shelf doesn't exist.
        assertEquals(null, csumb.getShelf("Romance"));
    }

    @Test
    void testGetShelf() {
        csumb.init(library00);

        // Pass shelf number.
        Shelf sciFiShelf = new Shelf(1, "sci-fi");

        // Shelf exists.
        assertEquals(sciFiShelf, csumb.getShelf(1));

        // Shelf doesn't exist.
        assertEquals(null, csumb.getShelf(99));

    }

    @Test
    void listReaders() {
        csumb.init(library00);

        // 4 readers total in this library.
        int expectedReaderCount = 4;

        assertEquals(expectedReaderCount, csumb.listReaders());
    }

    @Test
    void testListReaders() {
        csumb.init(library00);

        // 4 readers total in this library.
        int expectedReaderCount = 4;

        // List readers with books checked out by each reader.
        assertEquals(expectedReaderCount, csumb.listReaders(true));
    }

    @Test
    void getReaderByCard() {
        csumb.init(library00);

        // Reader exists.
        Reader drew = new Reader(1, "Drew Clinkenbeard", "831-582-4007");
        assertEquals(drew, csumb.getReaderByCard(1));

        // Reader doesn't exist.
        assertEquals(null, csumb.getReaderByCard(99));
    }

    @Test
    void addReader() {
        csumb.init(library00);

        // Reader already exists.
        Reader drew = new Reader(1, "Drew Clinkenbeard", "831-582-4007");
        assertEquals(Code.READER_ALREADY_EXISTS_ERROR, csumb.addReader(drew));

        // Reader with same card number exists.
        Reader pippen = new Reader(1, "Pippen", "444-444-4444");
        assertEquals(Code.READER_CARD_NUMBER_ERROR, csumb.addReader(pippen));

        // Reader doesn't exist.
        Reader jordan = new Reader(23, "Jordan", "555-555-5555");
        assertEquals(Code.SUCCESS, csumb.addReader(jordan));
    }

    @Test
    void removeReader() {
        csumb.init(library00);

        // Reader exists but has books checked out.
        Reader drew = csumb.getReaderByCard(1);
        assertEquals(Code.READER_STILL_HAS_BOOKS_ERROR, csumb.removeReader(drew));

        // Reader doesn't exist.
        Reader jordan = new Reader(23, "Jordan", "555-555-5555");
        assertEquals(Code.READER_NOT_IN_LIBRARY_ERROR, csumb.removeReader(jordan));

        // Reader exists and has no books checked out.
        Reader jennifer = csumb.getReaderByCard(2);
        // Remove all books from Jennifer.
        jennifer.removeBook(hitchhikersBook);
        assertEquals(Code.SUCCESS, csumb.removeReader(jennifer));
    }

    @Test
    void convertInt() {
        // Test non-integer values
        assertEquals(Code.BOOK_COUNT_ERROR.getCode(), Library.convertInt("0$", Code.BOOK_COUNT_ERROR));
        assertEquals(Code.PAGE_COUNT_ERROR.getCode(), Library.convertInt("0$0", Code.PAGE_COUNT_ERROR));
        assertEquals(Code.DATE_CONVERSION_ERROR.getCode(), Library.convertInt("0$0$0", Code.DATE_CONVERSION_ERROR));
        assertEquals(Code.UNKNOWN_ERROR.getCode(), Library.convertInt("0$0$0$0", Code.UNKNOWN_ERROR));

        // Test integer values
        Code unKnownError = Code.UNKNOWN_ERROR;
        assertEquals(0, Library.convertInt("0", unKnownError));
        assertEquals(1, Library.convertInt("1", unKnownError));
        assertEquals(10, Library.convertInt("10", unKnownError));
        assertEquals(100, Library.convertInt("100", unKnownError));
        assertEquals(1000, Library.convertInt("1000", unKnownError));
    }

    @Test
    void convertDate() {
        LocalDate defaultDate = LocalDate.of(1970, 1, 1);
        Code defaultCode = Code.UNKNOWN_ERROR;  // Code object isn't used anymore but still required as parameter.

        // Test date "0000".
        assertEquals(defaultDate, Library.convertDate("0000", defaultCode));

        // Test incomplete date that doesn't split into 3 parts on '-'.
        assertEquals(defaultDate, Library.convertDate("2020", defaultCode));
        assertEquals(defaultDate, Library.convertDate("2020-01", defaultCode));
        assertEquals(defaultDate, Library.convertDate("2020-08-13-25", defaultCode));

        // Non-integer values in different components of date string
        assertEquals(defaultDate, Library.convertDate("2$20-08-13", defaultCode));
        assertEquals(defaultDate, Library.convertDate("2020-0$-13", defaultCode));
        assertEquals(defaultDate, Library.convertDate("2020-08-1$", defaultCode));

        // No errors
        LocalDate date = LocalDate.of(2023, 11, 19);
        assertEquals(date, Library.convertDate("2023-11-19", defaultCode));
    }

    @Test
    void getLibraryCardNumber() {
        csumb.init(library00);

        // 4 library card numbers already assigned. Next should be 5.
        int nextLibaryCardNumber = 5;
        assertEquals(nextLibaryCardNumber, Library.getLibraryCardNumber());

        // Library card number should be highest value + 1.
        Reader jordan = new Reader(23, "Jordan", "555-555-5555");
        csumb.addReader(jordan);
        assertEquals(24, Library.getLibraryCardNumber());
    }
}
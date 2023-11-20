import Utilities.Code;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Library {
  public static final int LENDING_LIMIT = 5;  // Maximum number of books a reader can check out at a time.
  private HashMap<Book, Integer> books;       // Contains Book objects registered to library and count of the books.
  private static int libraryCard;             // Current max library card number.
  private String name;                        // Name of library.
  private List<Reader> readers;               // List of readers registered to library.
  private HashMap<String, Shelf> shelves;     // Contains shelf subject (String) and Shelf object.

  public Library(String name) {
    this.name = name;
  }

  public Code init(String filename) {
    File file = new File(filename);
    Scanner fileScanner = null;

    try {
      fileScanner = new Scanner(file);
    }
    catch (Exception e) {
      System.out.println("Error opening file: " + filename);
      System.out.println("Error message: " + e.getMessage());
      return Code.FILE_NOT_FOUND_ERROR;
    }

    String line = null;
    try {
      // This line should be the number of books.
      line = fileScanner.nextLine();
    }
    catch (Exception e) {
      System.out.println("Error reading file: " + filename);
      System.out.println("Error message: " + e.getMessage());
      return Code.UNKNOWN_ERROR;
    }

    int recordCount = convertInt(line, Code.BOOK_COUNT_ERROR);

    // If recordCount < 0, return Code object associated with number.
    // Get Code.UNKNOWN_ERROR if no error associated with number.
    if (recordCount < 0) {
      return getCodeByNumber(recordCount);
    }

    initBooks(recordCount, fileScanner);
    listBooks();

    initShelves(recordCount, fileScanner);
    listShelves();

    initReader(recordCount, fileScanner);
    listReaders();

    return Code.FILE_NOT_FOUND_ERROR;
  }

  private Code initBooks(int bookCount, Scanner scan) {
    if (bookCount < 1) {
      return Code.LIBRARY_ERROR;
    }

    // Iterate through records and convert each line to book.
    for (int i = 0; i < bookCount; i++) {
      String line = scan.nextLine();
      String[] splitLine = line.split(",");

      // There should be six fields per line.
      int numFields = 6;
      int splitLineLength = splitLine.length;
      if (splitLineLength != numFields) {
        System.out.println("Expected " + numFields + " fields, found " + splitLineLength);
        return Code.BOOK_RECORD_COUNT_ERROR;
      }

      // Safe to assume there are six fields at this point.
      String isbn = splitLine[Book.ISBN_];
      String title = splitLine[Book.TITLE_];
      String subject = splitLine[Book.SUBJECT_];
      String pageCountString = splitLine[Book.PAGE_COUNT_];
      String author = splitLine[Book.AUTHOR_];
      String dueDateString = splitLine[Book.DUE_DATE_];

      // Convert page count to int and verify it's valid.
      int pageCount = convertInt(pageCountString, Code.PAGE_COUNT_ERROR);
      if (pageCount <= 0) {
        return Code.PAGE_COUNT_ERROR;
      }

      // Convert due date to LocalDate and verify it's valid.
      LocalDate dueDate = convertDate(dueDateString, Code.DUE_DATE_ERROR);
      if (dueDate == null) {
        return Code.DATE_CONVERSION_ERROR;
      }

      // Everything went well. Create Book object and add it to library.
      Book book = new Book(isbn, title, subject, pageCount, author, dueDate);
      addBook(book);
    }
    return Code.SUCCESS;
  }

  private Code initReader(int CHANGE_ME1, Scanner CHANGE_ME2) {
    System.out.println("initReader() not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  private Code initShelves(int CHANGE_ME1, Scanner CHANGE_ME2) {
    System.out.println("initShelves() not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  private Code getCodeByNumber(int codeNumber) {
    // Find Code object with corresponding codeNumber.
    for (Code code : Code.values()) {
      if (code.getCode() == codeNumber) {
          return code;
      }
    }
    // No corresponding error with codeNumber found.
    return Code.UNKNOWN_ERROR;
  }

  public Code addBook(Book newBook) {
    if (books.containsKey(newBook)) {
      // Book already exists in library, increment the count.
      int newBookCount = books.get(newBook) + 1;
      String newBookTitle = newBook.getTitle();
      books.put(newBook, newBookCount);
      System.out.println(newBookCount + " copies of " + newBookTitle + " in the stacks");
      return Code.SUCCESS;
    }
    else {
      // Book doesn't exist in library, add it with a count of 1.
      books.put(newBook, 1);

      // Check if shelf with matching subject exists.
      String newBookSubject = newBook.getSubject();
      if (shelves.containsKey(newBookSubject)) {
        // Add book to shelf with matching subject.
        Shelf shelf = shelves.get(newBookSubject);
        addBookToShelf(newBook, shelf);
        return Code.SUCCESS;
      }
      else {
        // No shelf with matching subject exists, return error.
        System.out.println("No shelf for " + newBookSubject + " books");
        return Code.SHELF_EXISTS_ERROR;
      }
    }
  }

  private Code addBookToShelf(Book book, Shelf shelf) {
    // Try returning book to shelf with returnBook(Book) method first.
    Code returnBookCode = returnBook(book);
    if (returnBookCode == Code.SUCCESS) {
      // Successfully returned book to shelf.
      return Code.SUCCESS;
    }

    // returnBook() was unsuccessful.
    // Check if book subject matches shelf subject.
    String bookSubject = book.getSubject();
    String shelfSubject = shelf.getSubject();
    if (!bookSubject.equals(shelfSubject)) {
      return Code.SHELF_SUBJECT_MISMATCH_ERROR;
    }

    // Book subject matches shelf subject, add book to shelf.
    Code addBookToShelfCode = shelf.addBook(book);
    if (addBookToShelfCode == Code.SUCCESS) {
      // Successfully added book to shelf.
      System.out.println(book + "added to shelf");
      return Code.SUCCESS;
    }
    else {
      // Shelf.addBook() returned an error.
      System.out.println("Could not add " + book + " to shelf");
      return addBookToShelfCode;
    }
  }

  public Code addReader(Reader CHANGE_ME) {
    System.out.println("addReader() not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  public Code addShelf(Shelf CHANGE_ME) {
    System.out.println("addShelf(Shelf) not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  public Code addShelf(String CHANGE_ME) {
    System.out.println("addShelf(String) not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  public Code checkOutBook(Reader CHANGE_ME1, Book CHANGE_ME2) {
    System.out.println("checkOutBook() not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  public static LocalDate convertDate(String date, Code errorCode) {
    LocalDate defaultDate = LocalDate.of(1970, 1, 1);
    // If date string is "0000", return LocalDate set to 01-Jan-1970.
    if (date.equals("0000")) {
      return defaultDate;
    }

    // If date string doesn't split into 3 elements on a '-' char,
    // print message and return LocalDate set to 01-Jan-1970.
    String[] splitDate = date.split("-");
    int splitDateLength = splitDate.length;
    if (splitDateLength != 3) {
      System.out.println("ERROR: date conversion error, could not parse " + date);
      System.out.println("Using default date (01-jan-1970)");
      return defaultDate;
    }

    // Convert date values to int to verify they are valid.
    int year = Integer.parseInt(splitDate[0]);
    int month = Integer.parseInt(splitDate[1]);
    int day = Integer.parseInt(splitDate[2]);

    // If any of converted values from split String are less than 0,
    // print message and return LocalDate object set to 01-jan-1970.
    if (year < 0 || month < 0 || day < 0) {
      System.out.println("Error converting date: Year " + year);
      System.out.println("Error converting date: Month " + month);
      System.out.println("Error converting date: Day " + day);
      System.out.println("Using default date (01-jan-1970)");
      return defaultDate;
    }

    // No errors, so return LocalDate object set to parsed date values.
    return LocalDate.of(year, month, day);
  }

  public static int convertInt(String recordCountString, Code code) {
    int recordCount = -1;

    try {
      recordCount = Integer.parseInt(recordCountString);
      return recordCount;
    }
    catch (NumberFormatException e) {
      System.out.println("Value which caused the error: " + recordCountString);
      System.out.println("Error message: " + code.getMessage());

      // Print message depending on code provided as parameter.
      switch (code) {
        case BOOK_COUNT_ERROR:
          System.out.println("Error: Could not read number of books");
          break;
        case PAGE_COUNT_ERROR:
          System.out.println("Error: Could not parse page count");
          break;
        case DATE_CONVERSION_ERROR:
          System.out.println("Error: Could not parse date component");
          break;
        default:
          System.out.println("Error: Unknown conversion error");
          break;
      }
      // Return code number associated with provided code object.
      return code.getCode();
    }
  }

  private Code errorCode(int codeNumber) {
    for (Code code : Code.values()) {
      if (code.getCode() == codeNumber) {
        return code;
      }
    }
    return Code.UNKNOWN_ERROR;
  }

  public Book getBookByISBN(String CHANGE_ME) {
    System.out.println("getBookByISBN() not implemented");
    return null;
  }

  public static int getLibraryCardNumber() {
    return libraryCard + 1;
  }

  public Reader getReaderByCard(int CHANGE_ME) {
    System.out.println("getReaderByCard() not implemented");
    return null;
  }

  public Shelf getShelf(String CHANGE_ME) {
    System.out.println("getShelf(String) not implemented");
    return null;
  }

  public Shelf getShelf(Integer CHANGE_ME) {
    System.out.println("getShelf(Integer) not implemented");
    return null;
  }

  public int listBooks() {
    System.out.println("listBooks() not implemented");
    return -1;
  }

  public int listReaders() {
    System.out.println("listReaders() not implemented");
    return -1;
  }

  public int listReaders(boolean CHANGE_ME) {
    System.out.println("listReaders(boolean) not implemented");
    return -1;
  }

  public int listShelves(boolean CHANGE_ME) {
    System.out.println("listShelves(boolean) not implemented");
    return -1;
  }

  public int listShelves() {
    System.out.println("listShelves() not implemented");
    return -1;
  }

  public Code removeReader(Reader CHANGE_ME) {
    System.out.println("removeReader() not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  public Code returnBook(Reader CHANGE_ME1, Book CHANGE_ME2) {
    System.out.println("returnBook() not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  public Code returnBook(Book book) {
    String bookSubject = book.getSubject();

    // Check for shelf with matching subject.
    if (shelves.containsKey(bookSubject)) {
      // Shelf with matching subject exists, add book to shelf.
      Shelf shelf = shelves.get(bookSubject);
      shelf.addBook(book);
      return Code.SUCCESS;
    }
    else {
      // No shelf with matching subject exists, return error.
      System.out.println("No shelf for " + book);
      return Code.SHELF_EXISTS_ERROR;
    }
  }

  /**
   * Getters and setters auto-generated by IntelliJ.
   */
  public HashMap<Book, Integer> getBooks() {
    return books;
  }

  public void setBooks(HashMap<Book, Integer> books) {
    this.books = books;
  }

  public static int getLibraryCard() {
    return libraryCard;
  }

  public static void setLibraryCard(int libraryCard) {
    Library.libraryCard = libraryCard;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Reader> getReaders() {
    return readers;
  }

  public void setReaders(List<Reader> readers) {
    this.readers = readers;
  }

  public HashMap<String, Shelf> getShelves() {
    return shelves;
  }

  public void setShelves(HashMap<String, Shelf> shelves) {
    this.shelves = shelves;
  }
}
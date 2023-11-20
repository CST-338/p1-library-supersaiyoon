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
    books = new HashMap<>();
    readers = new ArrayList<>();
    shelves = new HashMap<>();
  }

  public Code init(String filename) {
    File file = new File(filename);
    Scanner fileScanner;

    try {
      fileScanner = new Scanner(file);
    }
    catch (Exception e) {
      System.out.println("Error opening file: " + filename);
      System.out.println("Error message: " + e.getMessage());
      return Code.FILE_NOT_FOUND_ERROR;
    }

    // First line should be record count of books.
    String line = fileScanner.nextLine();
    int recordCount = convertInt(line, Code.BOOK_COUNT_ERROR);

    // If recordCount < 0, return Code object associated with number.
    // Get Code.UNKNOWN_ERROR if no error associated with number.
    if (recordCount < 0) {
      return getCodeByNumber(recordCount);
    }

    // Parse books, shelves, and readers.
    // If any of init methods does NOT return Code.SUCCESS, return error code.
    Code initBooksCode = initBooks(recordCount, fileScanner);
    if (initBooksCode != Code.SUCCESS) {
      return initBooksCode;
    }
    listBooks();

    // Scanner should now be pointing at record count of shelves.
    line = fileScanner.nextLine();
    recordCount = convertInt(line, Code.SHELF_COUNT_ERROR);

    // If recordCount < 0, return Code object associated with number.
    // Get Code.UNKNOWN_ERROR if no error associated with number.
    if (recordCount < 0) {
      return getCodeByNumber(recordCount);
    }

    Code initShelvesCode = initShelves(recordCount, fileScanner);
    if (initShelvesCode != Code.SUCCESS) {
      return initShelvesCode;
    }
    listShelves();

    // Scanner should now be pointing at record count of readers.
    line = fileScanner.nextLine();
    recordCount = convertInt(line, Code.READER_COUNT_ERROR);

    // If recordCount < 0, return Code object associated with number.
    // Get Code.UNKNOWN_ERROR if no error associated with number.
    if (recordCount < 0) {
      return getCodeByNumber(recordCount);
    }

    Code initReaderCode = initReader(recordCount, fileScanner);
    if (initReaderCode != Code.SUCCESS) {
      return initReaderCode;
    }
    listReaders();

    // File parsed successfully.
    return Code.SUCCESS;
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

  private Code initShelves(int shelfCount, Scanner scan) {
    if (shelfCount < 1) {
      return Code.SHELF_COUNT_ERROR;
    }

    // Iterate through records and convert each line to shelf.
    for (int i = 0; i < shelfCount; i++) {
      String line = scan.nextLine();
      String[] splitLine = line.split(",");

      // There should be two fields per line.
      int numFields = 2;
      int splitLineLength = splitLine.length;
      if (splitLineLength != numFields) {
        System.out.println("Expected " + numFields + " fields, found " + splitLineLength);
        return Code.SHELF_NUMBER_PARSE_ERROR;
      }

      // Safe to assume there are two fields at this point.
      String shelfNumberString = splitLine[Shelf.SHELF_NUMBER_];
      String subject = splitLine[Shelf.SUBJECT_];

      // Convert shelf number to int and verify it's valid.
      int shelfNumber = convertInt(shelfNumberString, Code.SHELF_COUNT_ERROR);
      if (shelfNumber <= 0) {
        return Code.SHELF_NUMBER_PARSE_ERROR;
      }

      Shelf shelf = new Shelf(shelfNumber, subject);
      addShelf(shelf);
    }
    // Verify size of shelves object matches shelfCount.
    int numShelves = shelves.size();
    if (numShelves != shelfCount) {
      System.out.println("Number of shelves doesn't match expected");
      return Code.SHELF_NUMBER_PARSE_ERROR;
    }
    return Code.SUCCESS;
  }

  private Code initReader(int readerCount, Scanner scan) {
    if (readerCount <= 0) {
      return Code.READER_COUNT_ERROR;
    }

    // Iterate through records and convert each line to reader.
    for (int i = 0; i < readerCount; i++) {
      String line = scan.nextLine();
      String[] splitLine = line.split(",");

      String cardNumberString = splitLine[Reader.CARD_NUMBER_];
      String name = splitLine[Reader.NAME];
      String phoneNumber = splitLine[Reader.PHONE_];
      String bookCountString = splitLine[Reader.BOOK_COUNT_];

      // Convert card number string to int and verify it's valid.
      int cardNumber = convertInt(cardNumberString, Code.READER_CARD_NUMBER_ERROR);
      if (cardNumber <= 0) {
        return Code.READER_CARD_NUMBER_ERROR;
      }

      // Convert book count string to int and verify it's valid.
      int bookCount = convertInt(bookCountString, Code.UNKNOWN_ERROR);
      if (bookCount < 0) {
        System.out.println("initReader() error converting book count: " + bookCountString);
        return Code.UNKNOWN_ERROR;
      }

      // Initialize reader. Still need to determine books checked out with due dates.
      Reader reader = new Reader(cardNumber, name, phoneNumber);
      addReader(reader);

      // Determine books checked out by reader.
      int currBookCount = 0;
      int bookInfoIndex = Reader.BOOK_START_;
      while (currBookCount < bookCount) {
        // Only ISBN and due date provided.
        String isbn = splitLine[bookInfoIndex];
        bookInfoIndex++;
        String dueDateString = splitLine[bookInfoIndex];
        bookInfoIndex++;

        Book book = getBookByISBN(isbn);

        // Book doesn't exist in library. Print message and continue parsing.
        if (book == null) {
          System.out.println("ERROR");
          continue;
        }

        // Book found. Set due date.
        LocalDate dueDate = convertDate(dueDateString, Code.DATE_CONVERSION_ERROR);
        book.setDueDate(dueDate);

        // Add book to reader.
        checkOutBook(reader, book);
        currBookCount++;
      }
    }
    return Code.SUCCESS;
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
    // Get book title here because it's needed in multiple places.
    String newBookTitle = newBook.getTitle();

    if (books.containsKey(newBook)) {
      // Book already exists in library, increment the count.
      int newBookCount = books.get(newBook) + 1;
      books.put(newBook, newBookCount);
      System.out.println(newBookCount + " copies of " + newBookTitle + " in the stacks");
      return Code.SUCCESS;
    }
    else {
      // Book doesn't exist in library, add it with a count of 1.
      books.put(newBook, 1);
      System.out.println(newBookTitle + " added to the stacks");

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

  public Code addReader(Reader reader) {
    // Reader already exists
    if (readers.contains(reader)) {
      System.out.println(reader.getName() + " already has an account!");
      return Code.READER_ALREADY_EXISTS_ERROR;
    }

    // Check if reader with same card number exists.
    String readerName = reader.getName();
    int readerCardNumber = reader.getCardNumber();
    Reader readerWithSameCardNumber = getReaderByCard(readerCardNumber);
    if (readerWithSameCardNumber != null) {
      // Reader with same card number exists.
      String readerWithSameCardNumberName = readerWithSameCardNumber.getName();
      System.out.println(readerWithSameCardNumberName + " and " + readerName + " have the same card number!");
      return Code.READER_CARD_NUMBER_ERROR;
    }

    readers.add(reader);
    System.out.println(readerName + " added to the library!");

    if (readerCardNumber > libraryCard) {
      libraryCard = readerCardNumber;
    }
    return Code.SUCCESS;
  }

  public Code addShelf(Shelf shelf) {
    String shelfSubject = shelf.getSubject();
    if (shelves.containsKey(shelfSubject)) {
      // Shelf with matching subject already exists, return error.
      System.out.println("ERROR: Shelf already exists " + shelfSubject);
      return Code.SHELF_EXISTS_ERROR;
    }

    // Shelf with matching subject does not exist, add it.
    // Find current largest shelf number.
    int maxCurrShelfNumber = 0;
    for (Shelf currShelf : shelves.values()) {
      int currShelfNumber = currShelf.getShelfNumber();
      if (currShelfNumber > maxCurrShelfNumber) {
        maxCurrShelfNumber = currShelfNumber;
      }
    }
    // Next shelf number is largest number in HashMap plus one.
    int nextShelfNumber = maxCurrShelfNumber + 1;

    // Assign new shelf number to shelf then add to shelves.
    shelf.setShelfNumber(nextShelfNumber);
    shelves.put(shelfSubject, shelf);
    return Code.SUCCESS;
  }

  public Code addShelf(String shelfSubject) {
    int shelfNumber = shelves.size() + 1;
    Shelf shelf = new Shelf(shelfNumber, shelfSubject);
    return addShelf(shelf);
  }

  public Code checkOutBook(Reader reader, Book book) {
    String readerName = reader.getName();
    // Check if reader has account with library.
    if (!readers.contains(reader)) {
      System.out.println(readerName + " doesn't have an account here");
      return Code.READER_NOT_IN_LIBRARY_ERROR;
    }

    // Check if reader has reached lending limit.
    int readerBookCount = reader.getBooks().size();
    if (readerBookCount >= LENDING_LIMIT) {
      System.out.println(readerName + " has reached the lending limit, " + LENDING_LIMIT);
      return Code.BOOK_LIMIT_REACHED_ERROR;
    }

    // Check if book is in library.
    if (!books.containsKey(book)) {
      System.out.println("ERROR: could not find " + book);
      return Code.BOOK_NOT_IN_INVENTORY_ERROR;
    }

    // Check if shelf for book exists.
    String bookSubject = book.getSubject();
    if (!shelves.containsKey(bookSubject)) {
      System.out.println("No shelf for " + bookSubject + " books!");
      return Code.SHELF_EXISTS_ERROR;
    }

    // Shelf exists but verify there are enough copies.
    Shelf shelf = shelves.get(bookSubject);
    int booksOnShelf = shelf.getBookCount(book);
    if (booksOnShelf < 1) {
      System.out.println("ERROR: no copies of " + book + " remain");
      return Code.BOOK_NOT_IN_INVENTORY_ERROR;
    }

    // Everything checks out, add book to reader and remove from shelf.
    Code addBookToReaderCode = reader.addBook(book);
    if (addBookToReaderCode == Code.SUCCESS) {
      Code removeBookCode = shelf.removeBook(book);
      System.out.println(book + " checked out successfully");
      return removeBookCode;
    }
    else {
      // Reader might already have book, or some other error occurred.
      System.out.println("Couldn't checkout " + book);
      return addBookToReaderCode;
    }
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

  public Book getBookByISBN(String isbn) {
    for (Book book : books.keySet()) {
      if (book.getISBN().equals(isbn)) {
        // Book with same ISBN exists.
        return book;
      }
    }
    // Book with same ISBN doesn't exist.
    System.out.println("ERROR: Could not find a book with ISBN: " + isbn);
    return null;
  }

  public static int getLibraryCardNumber() {
    return libraryCard + 1;
  }

  public Reader getReaderByCard(int cardNumber) {
    for (Reader reader : readers) {
      if (reader.getCardNumber() == cardNumber) {
        // Reader with same card number exists.
        return reader;
      }
    }
    // Reader with same card number doesn't exist.
    System.out.println("Could not find a reader with card #" + cardNumber);
    return null;
  }

  public Shelf getShelf(String subject) {
    for (Shelf shelf : shelves.values()) {
      if (shelf.getSubject().equals(subject)) {
        // Shelf with same subject exists.
        return shelf;
      }
    }
    // Shelf with same subject doesn't exist.
    System.out.println("No shelf for " + subject + " books");
    return null;
  }

  public Shelf getShelf(Integer shelfNumber) {
    for (Shelf shelf : shelves.values()) {
      if (shelf.getShelfNumber() == shelfNumber) {
        // Shelf with same shelf number exists.
        return shelf;
      }
    }
    // Shelf with same shelf number doesn't exist.
    System.out.println("No shelf number " + shelfNumber + " found");
    return null;
  }

  public int listBooks() {
    int totalBooks = 0;
    int numCopies;
    String bookTitle;
    String bookAuthor;
    String bookIsbn;

    // List all books at library, even those not on shelves.
    for (Book book : books.keySet()) {
      numCopies = books.get(book);
      bookTitle = book.getTitle();
      bookAuthor = book.getAuthor();
      bookIsbn = book.getISBN();

      System.out.println(numCopies + " copies of " + bookTitle + " by " + bookAuthor + " ISBN:" + bookIsbn);
      totalBooks += numCopies;
    }
    return totalBooks;
  }

  public int listReaders() {
    int numReaders = 0;

    for (Reader reader : readers) {
      System.out.println(reader.toString());
      numReaders++;
    }

    return numReaders;
  }

  public int listReaders(boolean CHANGE_ME) {
    System.out.println("listReaders(boolean) not implemented");
    return -1;
  }

  public int listShelves(boolean showBooks) {
    int numShelves = 0;
    // If showBooks is true, list books on each shelf.
    if (showBooks) {
      for (Shelf shelf : shelves.values()) {
        shelf.listBooks();
        numShelves++;
      }
    }
    // If showBooks is false, list shelf information only.
    else {
      for (Shelf shelf : shelves.values()) {
        System.out.println(shelf.toString());
        numShelves++;
      }
    }
    return numShelves;
  }

  public int listShelves() {
    return listShelves(false);
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
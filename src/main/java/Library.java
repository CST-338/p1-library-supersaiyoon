import Utilities.Code;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

/**
 * Part 4 of Library Project. Represents a library that contains readers, shelves, and books.
 * Performs functions like a real library such as checking out books, storing books on shelves, and more.
 * @author Brian Yoon
 * @since 2023-11-18
 */
public class Library {
  /**
   * Maximum number of books a reader can check out at a time.
   */
  public static final int LENDING_LIMIT = 5;

  /**
   * Contains Book objects registered to the library and the count of each book.
   */
  private HashMap<Book, Integer> books;

  /**
   * Current maximum library card number. Initializes with '0'.
   */
  private static int libraryCard;

  /**
   * Name of the library.
   */
  private String name;

  /**
   * List of readers registered to the library.
   */
  private List<Reader> readers;

  /**
   * Contains shelf subject (String) and Shelf object.
   */
  private HashMap<String, Shelf> shelves;

  /**
   * Constructor for creating a new library with the specified name.
   *
   * @param name The name of the library.
   */
  public Library(String name) {
    this.name = name;
    books = new HashMap<>();
    readers = new ArrayList<>();
    shelves = new HashMap<>();
  }

  /**
   * Initializes the library with data from the specified file.
   *
   * @param filename The name of the file containing information on books, shelves, and readers.
   * @return A {@code Code} object indicating the success or failure of the initialization.<br>
   *         Returns {@code Code.SUCCESS} if initialization is successful.<br>
   *         Returns {@code Code.FILE_NOT_FOUND_ERROR} if the specified file is not found.<br>
   *         Returns other error codes for various initialization failures.
   */
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

  /**
   * Initializes the books in the library with the specified number of books contained in the given {@code Scanner}.
   *
   * @param bookCount The number of books to initialize in the library.
   * @param scan      The Scanner object containing the book data.
   * @return A {@code Code} object indicating the success or failure of the initialization.<br>
   *         Returns {@code Code.SUCCESS} if initialization is successful.<br>
   *         Returns {@code Code.LIBRARY_ERROR} if the book count is less than 1.<br>
   *         Returns {@code Code.BOOK_COUNT_ERROR} if the input data for a book has an unexpected number of fields.<br>
   *         Returns {@code Code.PAGE_COUNT_ERROR} if the page count of a book is not a positive integer.<br>
   *         Returns {@code Code.DUE_DATE_ERROR} if the due date of a book cannot be converted to LocalDate.<br>
   *         Returns {@code Code.DATE_CONVERSION_ERROR} if there is an error converting the due date to LocalDate.
   */
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
        return Code.BOOK_COUNT_ERROR;  // Following unit test output per Dr. C.
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

  /**
   * Initializes the shelves in the library with the specified number of shelves contained in the given {@code Scanner}.
   *
   * @param shelfCount The number of shelves to initialize in the library.
   * @param scan       The Scanner object containing the shelf data.
   * @return A {@code Code} object indicating the success or failure of the initialization.<br>
   *         Returns {@code Code.SUCCESS} if initialization is successful.<br>
   *         Returns {@code Code.SHELF_COUNT_ERROR} if the shelf count is less than 1.<br>
   *         Returns {@code Code.SHELF_NUMBER_PARSE_ERROR} if the input data for a shelf has an unexpected number of fields
   *         or if the shelf number cannot be converted to a positive integer.
   */
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

  /**
   * Initializes the readers registered with the library with the specified number of readers contained in the given {@code Scanner}.
   *
   * @param readerCount The number of readers to initialize in the library.
   * @param scan        The Scanner object containing the reader data.
   * @return A Code object indicating the success or failure of the initialization.<br>
   *         Returns {@code Code.SUCCESS} if initialization is successful.<br>
   *         Returns {@code Code.READER_COUNT_ERROR} if the reader count is less than or equal to 0.<br>
   *         Returns {@code Code.READER_CARD_NUMBER_ERROR} if the card number of a reader cannot be converted to a positive integer.<br>
   *         Returns {@code Code.UNKNOWN_ERROR} if there is an error converting the book count to an integer or for other unknown errors.<br>
   *         Returns {@code Code.DATE_CONVERSION_ERROR} if there is an error converting the due date to LocalDate.
   */
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

        // Book doesn't exist in library. Print message, skip book, and continue parsing.
        if (book == null) {
          System.out.println("ERROR");
          currBookCount++;
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

  /**
   * Retrieves the Code object associated with the specified code number.
   *
   * @param codeNumber The numeric value of the Code object to retrieve.
   * @return The Code object corresponding to the given codeNumber.<br>
   *         Returns {@code Code.UNKNOWN_ERROR} if no Code object is found with the specified codeNumber.
   */
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

  /**
   * Adds a new book to the library or increments the count if the book already exists.
   *
   * @param newBook The Book object to be added to the library.
   * @return A Code object indicating the success or failure of the addition.<br>
   *         Returns {@code Code.SUCCESS} if the book is successfully added or its count is incremented.<br>
   *         Returns {@code Code.SHELF_EXISTS_ERROR} if no shelf with a matching subject exists.
   */
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

  /**
   * Adds/returns a book to the specified shelf.
   *
   * @param book  The Book object to be added/returned to the shelf.
   * @param shelf The Shelf object to which the book is to be added/returned.
   * @return A Code object indicating the success or failure of the operation.<br>
   *         Returns {@code Code.SUCCESS} if the book is successfully added/returned to the shelf.<br>
   *         Returns {@code Code.SHELF_SUBJECT_MISMATCH_ERROR} if the book subject does not match the shelf subject.<br>
   *         Returns the error code returned by the {@code Shelf.addBook} method if it encounters an error.
   */
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

  /**
   * Adds a new reader to the library.
   *
   * @param reader The Reader object to be added to the library.
   * @return A Code object indicating the success or failure of the addition.<br>
   *         Returns {@code Code.SUCCESS} if the reader is successfully added.<br>
   *         Returns {@code Code.READER_ALREADY_EXISTS_ERROR} if a reader with the same information already exists.<br>
   *         Returns {@code Code.READER_CARD_NUMBER_ERROR} if a reader with the same card number already exists.
   */
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

  /**
   * Adds a new shelf to the library and adds any books (not on a shelf)
   * with the matching subject to the shelf.
   *
   * @param shelf The Shelf object to be added to the library.
   * @return A Code object indicating the success or failure of the addition.<br>
   *         Returns {@code Code.SUCCESS} if the shelf is successfully added.<br>
   *         Returns {@code Code.SHELF_EXISTS_ERROR} if a shelf with the same subject already exists.
   */
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

    // Add all books with matching subjects to new shelf.
    for (Map.Entry<Book, Integer> entry : books.entrySet()) {
      Book book = entry.getKey();
      String bookSubject = entry.getKey().getSubject();
      Integer numCopies = entry.getValue();
      if (bookSubject.equals(shelfSubject)) {
        // Need to add multiple copies of same book.
        for (int i = 0; i < numCopies; i++) {
          addBookToShelf(book, shelf);
        }
      }
    }
    return Code.SUCCESS;
  }

  /**
   * Adds a new shelf to the library with the specified subject.
   *
   * @param shelfSubject The subject of the new shelf.
   * @return A Code object resulting from the call of addShelf(Shelf).<br>
   *         Returns {@code Code.SUCCESS} if the shelf is successfully added.<br>
   *         Returns {@code Code.SHELF_EXISTS_ERROR} if a shelf with the same subject already exists.
   */
  public Code addShelf(String shelfSubject) {
    int shelfNumber = shelves.size() + 1;
    Shelf shelf = new Shelf(shelfNumber, shelfSubject);
    return addShelf(shelf);
  }

  /**
   * Checks out a book to the specified reader.
   *
   * @param reader The Reader object to whom the book is checked out.
   * @param book   The Book object to be checked out.
   * @return A Code object indicating the success or failure of the checkout.<br>
   *         Returns {@code Code.SUCCESS} if the book is successfully checked out.<br>
   *         Returns {@code Code.READER_NOT_IN_LIBRARY_ERROR} if the specified reader does not have an account with the library.<br>
   *         Returns {@code Code.BOOK_LIMIT_REACHED_ERROR} if the reader has reached the lending limit.<br>
   *         Returns {@code Code.BOOK_NOT_IN_INVENTORY_ERROR} if the specified book is not in the library's inventory or there are no copies on the shelf.<br>
   *         Returns the error code returned by the Reader.addBook method if there is an error adding the book to the reader.<br>
   *         Returns the error code returned by the Shelf.removeBook method if there is an error removing the book from the shelf.
   */
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

  /**
   * Converts a date string to a LocalDate object.
   *
   * @param date      The date string to be converted.
   * @param errorCode The Code object to be returned in case of conversion errors. This is no longer used for the project.
   * @return A LocalDate object representing the converted date.<br>
   *         Returns {@code LocalDate.of(1970, 1, 1)} if the input date string is "0000".<br>
   *         Returns {@code LocalDate.of(1970, 1, 1)} if the date string cannot be split into three elements on a '-' character.<br>
   *         Returns {@code LocalDate.of(1970, 1, 1)} if there are errors converting the date values to integers or if any converted value is less than 0.<br>
   *         Returns a LocalDate object set to the parsed date values if there are no errors.
   */
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

  /**
   * Converts a string to an integer.
   *
   * @param recordCountString The string to be converted to an integer.
   * @param code              The Code object to be returned in case of conversion errors.
   * @return An integer value representing the converted string.<br>
   *         Returns the converted integer value if there are no errors.<br>
   *         Returns the code number associated with the provided Code object and prints an error message in case of conversion errors.
   */
  public static int convertInt(String recordCountString, Code code) {
    int recordCount;

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

  /**
   * Deprecated: This method is to be removed in the future. It is not used in the current program.
   *
   * @param codeNumber The numeric value of the Code object to retrieve.
   * @return The Code object associated with the specified code number.<br>
   *         Returns {@code Code.UNKNOWN_ERROR} if no Code object is found with the specified code number.
   */
  private Code errorCode(int codeNumber) {
    for (Code code : Code.values()) {
      if (code.getCode() == codeNumber) {
        return code;
      }
    }
    return Code.UNKNOWN_ERROR;
  }

  /**
   * Retrieves a Book object with the specified ISBN from the library's inventory.
   *
   * @param isbn The ISBN of the book to retrieve.
   * @return The Book object with the specified ISBN.<br>
   *         Returns {@code null} if no book with the specified ISBN is found in the library's inventory.
   */
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

  /**
   * Gets the next available library card number.
   *
   * @return The next available library card number.
   */
  public static int getLibraryCardNumber() {
    return libraryCard + 1;
  }

  /**
   * Retrieves a Reader object with the specified card number from the library.
   *
   * @param cardNumber The card number of the reader to retrieve.
   * @return The Reader object with the specified card number.<br>
   *         Returns {@code null} if no reader with the specified card number is found.
   */
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

  /**
   * Retrieves a Shelf object with the specified subject from the library.
   *
   * @param subject The subject of the shelf to retrieve.
   * @return The Shelf object with the specified subject.<br>
   *         Returns {@code null} if no shelf with the specified subject is found.
   */
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

  /**
   * Retrieves a Shelf object with the specified shelf number from the library.
   *
   * @param shelfNumber The shelf number of the shelf to retrieve.
   * @return The Shelf object with the specified shelf number.<br>
   *         Returns {@code null} if no shelf with the specified shelf number is found.
   */
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

  /**
   * Lists all books in the library, including those not on shelves.
   *
   * @return The total number of books in the library.
   */
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

  /**
   * Lists all readers in the library.
   *
   * @return The total number of readers in the library.
   */
  public int listReaders() {
    int numReaders = 0;

    for (Reader reader : readers) {
      System.out.println(reader.toString());
      numReaders++;
    }

    return numReaders;
  }

  /**
   * Lists all readers in the library, optionally showing the books checked out by each reader.
   *
   * @param showBooks If true, displays the information of each reader along with the books they have checked out.<br>
   *                  If false, displays the string representation of the {@code readers} collection.
   * @return The total number of readers in the library.
   */
  public int listReaders(boolean showBooks) {
    if (showBooks) {
      for (Reader reader : readers) {
        System.out.println(reader.toString());
      }
    }
    else {
      System.out.println(readers.toString());
    }

    return readers.size();
  }

  /**
   * Lists all shelves in the library, optionally showing the books on each shelf.
   *
   * @param showBooks If true, lists the books on each shelf.<br>
   *                  If false, only lists shelf number and subject for each shelf without showing the books.
   * @return The total number of shelves in the library.
   */
  public int listShelves(boolean showBooks) {
    int numShelves = 0;
    // If showBooks is true, list books on each shelf.
    if (showBooks) {
      for (Shelf shelf : shelves.values()) {
        shelf.listBooks();
        numShelves++;
      }
    }
    // If showBooks is false, list shelf number and subject only.
    else {
      for (Shelf shelf : shelves.values()) {
        System.out.println(shelf.toString());
        numShelves++;
      }
    }
    return numShelves;
  }

  /**
   * Lists all shelves in the library, displaying only shelf number and subject of each shelf.
   *
   * @return The total number of shelves in the library.
   */
  public int listShelves() {
    return listShelves(false);
  }

  /**
   * Removes a reader from the library.
   *
   * @param reader The Reader object to be removed.
   * @return A Code object indicating the success or failure of the removal.<br>
   *         Returns {@code Code.SUCCESS} if the reader is successfully removed.<br>
   *         Returns {@code Code.READER_STILL_HAS_BOOKS_ERROR} if the reader still has books checked out and must return all books before removal.<br>
   *         Returns {@code Code.READER_NOT_IN_LIBRARY_ERROR} if the specified reader is not part of the library.
   */
  public Code removeReader(Reader reader) {
    String readerName = reader.getName();
    // Check if reader still has books checked out.
    boolean readerHasBooks = !reader.getBooks().isEmpty();
    if (readerHasBooks) {
      System.out.println(readerName + " must return all books!");
      return Code.READER_STILL_HAS_BOOKS_ERROR;
    }

    // Check if reader exists in library list of readers.
    if (!readers.contains(reader)) {
      System.out.println(readerName + " is not part of this library.");
      return Code.READER_NOT_IN_LIBRARY_ERROR;
    }

    // Reader exists in library and has no books checked out.
    readers.remove(reader);
    return Code.SUCCESS;
  }

  /**
   * Returns a book to the library, removing it from the specified reader's list of checked-out books.
   *
   * @param reader The Reader object returning the book.
   * @param book   The Book object to be returned.
   * @return A Code object indicating the success or failure of the return.<br>
   *         Returns {@code Code.SUCCESS} if the book is successfully returned to a shelf.<br>
   *         Returns {@code Code.READER_DOESNT_HAVE_BOOK_ERROR} if the specified reader does not have the book checked out.<br>
   *         Returns {@code Code.BOOK_NOT_IN_INVENTORY_ERROR} if the book is not found in the library's inventory.<br>
   *         Prints an error message if the book cannot be returned and returns the associated code.
   */
  public Code returnBook(Reader reader, Book book) {
    String readerName = reader.getName();

    // Reader does not have book in their list.
    if (!reader.hasBook(book)) {
      String bookTitle = book.getTitle();
      System.out.println(readerName + " doesn't have " + bookTitle + " checked out");
      return Code.READER_DOESNT_HAVE_BOOK_ERROR;
    }

    // Verify book exists in library.
    if (!books.containsKey(book)) {
      return Code.BOOK_NOT_IN_INVENTORY_ERROR;
    }

    // Book exists in library, remove book from reader.
    System.out.println(readerName + " is returning " + book);
    Code removeBookCode = reader.removeBook(book);

    // Book removed from reader, return book to shelf.
    if (removeBookCode == Code.SUCCESS) {
      return returnBook(book);
    }
    else {
      System.out.println("Could not return " + book);
      return removeBookCode;
    }
  }

  /**
   * Returns a book to the library, adding it back to the shelf with matching subject.
   *
   * @param book The Book object to be returned.
   * @return A Code object indicating the success or failure of the return.<br>
   *         Returns {@code Code.SUCCESS} if the book is successfully returned.<br>
   *         Returns {@code Code.SHELF_EXISTS_ERROR} if there is no shelf with a matching subject for the book.<br>
   *         Prints an error message if the book cannot be returned and returns the associated code.
   */
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
   * Getters and setters auto-generated by IntelliJ. Individual Javadoc comments not required per Dr. C.
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
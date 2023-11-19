import Utilities.Code;

import java.time.LocalDate;
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
    System.out.println("Not implemented");
  }

  public Code init(String filename) {
    System.out.println("Not implemented");
    return Code.NOT_IMPLEMENTED_ERROR;
  }

  private Code initBooks(int CHANGE_ME1, Scanner CHANGE_ME2) {
    System.out.println("initBooks() not implemented");
    return null;
  }

  private Code initReader(int CHANGE_ME1, Scanner CHANGE_ME2) {
    System.out.println("initReader() not implemented");
    return null;
  }

  private Code initShelves(int CHANGE_ME1, Scanner CHANGE_ME2) {
    System.out.println("initShelves() not implemented");
    return null;
  }

  public Code addBook(Book CHANGE_ME) {
    System.out.println("addBook() not implemented");
    return null;
  }

  private Code addBookToShelf(Book CHANGE_ME1, Shelf CHANGE_ME2) {
    System.out.println("addBookToShelf() not implemented");
    return null;
  }

  public Code addReader(Reader CHANGE_ME) {
    System.out.println("addReader() not implemented");
    return null;
  }

  public Code addShelf(Shelf CHANGE_ME) {
    System.out.println("addShelf(Shelf) not implemented");
    return null;
  }

  public Code addShelf(String CHANGE_ME) {
    System.out.println("addShelf(String) not implemented");
    return null;
  }

  public Code checkOutBook(Reader CHANGE_ME1, Book CHANGE_ME2) {
    return null;
  }

  public static LocalDate convertDate(String CHANGE_ME1, Code CHANGE_ME2) {
    return null;
  }

  public static int convertInt(String CHANGE_ME1, Code CHANGE_ME2) {
    return -1;
  }

  private Code errorCode(int CHANGE_ME) {
    return null;
  }

  public Book getBookByISBN(String CHANGE_ME) {
    return null;
  }

  public static int getLibraryCardNumber() {
    return -1;
  }

  public String getName() {
    return "getName() not implemented yet!";
  }

  public Reader getReaderByCard(int CHANGE_ME) {
    return null;
  }

  public Shelf getShelf(String CHANGE_ME) {
    return null;
  }

  public Shelf getShelf(Integer CHANGE_ME) {
    return null;
  }

  public int listBooks() {
    return -1;
  }

  public int listReaders() {
    return -1;
  }

  public int listReaders(boolean CHANGE_ME) {
    return -1;
  }

  public int listShelves(boolean CHANGE_ME) {
    return -1;
  }

  public int listShelves() {
    return -1;
  }

  public Code removeReader(Reader CHANGE_ME) {
    return null;
  }

  public Code returnBook(Reader CHANGE_ME1, Book CHANGE_ME2) {
    return null;
  }

  public Code returnBook(Book CHANGE_ME) {
    return null;
  }
}
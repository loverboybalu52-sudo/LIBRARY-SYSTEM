
import java.util.*;
import java.sql.*;


class Book
 {
    private String title;
    private String author;
    private boolean isAvailable;

    
    public Book(String title, String author, boolean available)
     {
        this.title = title;
        this.author = author;
        this.isAvailable = available;
    }

    
    public String getTitle()
     {
         return title;
     }
    public String getAuthor() 
    {
         return author;
         }
    public boolean isAvailable()
     {
         return isAvailable; 
    }

    public void setAvailable(boolean available) 
    {
         isAvailable = available;
         }

    @Override
    public String toString()
     {
        return "Book{'" + title + "', Author='" + author + "', Available=" + isAvailable + "}";
    }
}


abstract class User 
{
    protected String name;
    protected int id;

    public User(String name, int id) 
    {
        this.name = name;
        this.id = id;
    }

    public abstract void borrowBook(Book b);

    public void showDetails()
     {
        System.out.println("User: " + name + " (ID: " + id + ")");
    }
}


class Student extends User
 {
    public Student(String name, int id) 
    { 
        super(name, id);
     }

    @Override
    public void borrowBook(Book b)
     {
        if (b.isAvailable())
         {
            b.setAvailable(false);
            System.out.println(name + " borrowed book: " + b.getTitle());
        } 
        else
         {
            System.out.println("Sorry, " + b.getTitle() + " is not available.");
        }
    }
}


final class Librarian extends User 
{
    public Librarian(String name, int id)
     {
         super(name, id);
         }

    @Override
    public void borrowBook(Book b)
     {
        System.out.println("Librarian " + name + " manages borrowing of: " + b.getTitle());
    }

    public void addBookToDB(Connection con, Book b) throws SQLException
     {
        String query = "INSERT INTO books (title, author, available) VALUES (?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, b.getTitle());
        pst.setString(2, b.getAuthor());
        pst.setBoolean(3, b.isAvailable());
        pst.executeUpdate();
        System.out.println("Book added to DB: " + b.getTitle());
    }
}


class Library
 {
    static String libraryName = "City Central Library";

    public static void displayLibraryInfo()
     {
        System.out.println("Welcome to " + libraryName);
    }
}


public class LibrarySystem
 {
    public static void main(String[] args)
     {
        Library.displayLibraryInfo();

        
        String url = "jdbc:mysql://localhost:3306/librarydb";
        String user = "root";      
        
        String password = "root"; 
        

        try (Connection con = DriverManager.getConnection(url, user, password)) 
        {
            System.out.println("✅ Connected to MySQL Database");

          
            
            Student s1 = new Student("Mahesh", 101);
            Librarian l1 = new Librarian("Ravi", 201);

           
            
            l1.addBookToDB(con, new Book("Java Programming", "James Gosling", true));
            l1.addBookToDB(con, new Book("Database Systems", "C.J. Date", true));
            l1.addBookToDB(con, new Book("Operating Systems", "Silberschatz", true));

            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            List<Book> catalog = new ArrayList<>();
            while (rs.next())
             {
                Book b = new Book(rs.getString("title"),
                                  rs.getString("author"),
                                  rs.getBoolean("available"));
                catalog.add(b);
            }

            
            System.out.println("\n Library Catalog from DB:");
            for (Book b : catalog) {
                System.out.println(b);
            }

            s1.borrowBook(catalog.get(0));

        }
         catch (SQLException e)
         {
            System.out.println(" JDBC Error: " + e.getMessage());
        }
    }
}

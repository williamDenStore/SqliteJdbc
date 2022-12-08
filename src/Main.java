import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main{
    private static final Scanner s = new Scanner(System.in);
    public static void main(String[] args) {
        select();
    }
    private static void printMenu(){
        System.out.println("""
                0. shutdown system
                1. show all books
                2. add a book
                3. update book
                4. delete a book
                5. Show average cost
                6. create category
                7. show category
                8. update category
                9. delete category
                10. show books with categories
                11. search for a book
                """);
    }
    private static void select(){
        int choice=-1;
        while(choice!=0){
            printMenu();
            choice=s.nextInt();
            switch (choice){
                case 0 -> System.out.println("shutting down sysstem");
                case 1 -> showAll("bok");
                case 2 -> insertBook(createBook());
                case 3 -> updateBook(selectId("book"),createBook());
                case 4 -> delete(selectId("book"), "bok");
                case 5 -> showAvgCost();
                case 6 -> insertCategory(createCategory());
                case 7 -> showAll("kategori");
                case 8 -> updateCategory(selectId(""),createCategory());
                case 9 -> delete(selectId("kategori"),"kategori");
                case 10 -> innerJoin();
                case 11 -> search(searchQuery());
            }
        }
    }

    private static String searchQuery() {
        System.out.println("skriv ordet du vill söka på");
        s.nextLine();
        return s.nextLine();
    }

    private static void search(String searchQuery) {
            try{
                ResultSet rs = connectSQL().createStatement().executeQuery("SELECT bokNamn, bokPris FROM bok WHERE bokNamn LIKE '%"+searchQuery+"%'");
                while (rs.next()){
                    System.out.println("bokNamn: "+rs.getString("bokNamn")+
                            "\t bokPris: "+rs.getInt("bokPris"));
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
    }

    private static void updateCategory(int id, ArrayList<String> category) {
        try{
            PreparedStatement prps = connectSQL().prepareStatement("UPDATE kategori SET kategoriNamn = ?, bokKategoriId = ? WHERE kategoriId = ?");
            prps.setString(1, category.get(0));
            prps.setInt(2, Integer.parseInt(category.get(1)));
            prps.setInt(3, id);
            prps.executeUpdate();
            System.out.println("category is updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void innerJoin() {
        try{
            ResultSet rs = connectSQL().createStatement().executeQuery("SELECT bok.bokNamn, bok.bokPris, kategori.kategoriNamn FROM kategori\n" +
                    "INNER JOIN bok on bok.bokId = kategori.bokKategoriId;");
            while (rs.next()){
                System.out.println("bokNamn: "+rs.getString("bokNamn")+
                        "\t bokPris: "+rs.getInt("bokPris")+
                        "\t kategoriNamn: " +rs.getString("kategoriNamn"));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void showAvgCost() {
        try{
            ResultSet rs = connectSQL().createStatement().executeQuery("SELECT AVG(bokpris) FROM bok");
            while (rs.next()){
                System.out.println("Average bok pris: " +rs.getInt("AVG(bokPris)"));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    private static Connection connectSQL(){
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:C:/Users/46738/Desktop/db labbar/jdbcbok.db");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return c;
    }
    private static int selectId(String table){
        System.out.println("choose id");
        if (table.equals("book"))
            showAll("bok");
        else
            showAll("kategori");
        return s.nextInt();
    }
    private static void delete(int id, String table) {
        try {
            PreparedStatement prpst = connectSQL().prepareStatement("DELETE  FROM "+table+" WHERE "+table+"Id = ?");
            prpst.setInt(1,id);
            prpst.executeUpdate();
            System.out.println("the book is now deleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static ArrayList<String> createBook(){
        ArrayList<String> bookStr = new ArrayList<>();
        System.out.println("write book name");
        s.nextLine();
        bookStr.add(s.nextLine());
        System.out.println("write book price");
        bookStr.add(s.nextLine());
        return bookStr;
    }
    private static void updateBook(int id, ArrayList<String> book) {
        try{
            PreparedStatement prps = connectSQL().prepareStatement("UPDATE bok SET bokNamn = ? , bokPris = ? WHERE bokId = ?");
            prps.setString(1, book.get(0));
            prps.setInt(2,Integer.parseInt(book.get(1)));
            prps.setInt(3, id);
            prps.executeUpdate();
            System.out.println("book is updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertBook(ArrayList<String> book) {
        try {
            PreparedStatement prps = connectSQL().prepareStatement("INSERT INTO bok(bokNamn, bokPris) VALUES(?,?)");
            prps.setString(1,book.get(0));
            prps.setInt(2, Integer.parseInt(book.get(1)));
            prps.executeUpdate();
            System.out.println("the book has been added");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void showAll(String table) {
        try{
            ResultSet rs = connectSQL().createStatement().executeQuery("SELECT *FROM "+table);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()){

                System.out.println(rsmd.getColumnName(1)+": "+rs.getInt(1)+
                        "\t"+rsmd.getColumnName(2)+": "+rs.getString(2)+
                        "\t"+rsmd.getColumnName(3)+": " +rs.getString(3));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    private static ArrayList<String> createCategory(){
        ArrayList<String> categoryStr = new ArrayList<>();
        System.out.println("write category name");
        s.nextLine();
        categoryStr.add(s.nextLine());
        System.out.println("select the id of which book has that category");
        showAll("bok");
        categoryStr.add(s.nextLine());
        return categoryStr;
    }
    private static void insertCategory(ArrayList<String> category) {
        try {
            PreparedStatement prps = connectSQL().prepareStatement("INSERT INTO kategori(kategoriNamn, bokKategoriId) VALUES(?,?)");
            prps.setString(1,category.get(0));
            prps.setInt(2,Integer.parseInt(category.get(1)));
            prps.executeUpdate();
            System.out.println("the category has been added");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
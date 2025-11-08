package db;
import java.sql.*;
public class DBConnection {
    private static Connection connection = null;
     public  static  final String URL = "jdbc:mysql://localhost:3306/dept_sync_db";
     public static  final  String USER = "root";
     public  static final String PASSWORD = "";


    // private constructor to prevent instantiation
    private   DBConnection (){}


    public static Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()){
                // loading driver

                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection

                connection = DriverManager.getConnection(URL, USER , PASSWORD);

                System.out.println("Connection successfull ");


            }

        }catch (ClassNotFoundException | SQLException e){
             // print out error
            System.err.println("Connection Failed"+ e.getMessage());
        }


        return connection;

    }

    // method to close connection
    public static void  closeConnection(){
        try {
            if(connection != null && !connection.isClosed()){
                connection.close();
                System.out.println("Database connection closed");

            }

        } catch (Exception e) {
            System.err.println("Error on closing connection ");
        }
    }




    public static void main(String[] args) throws Exception {
        getConnection();
    }
}

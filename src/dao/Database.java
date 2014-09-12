package dao;
 
import java.sql.Connection;
import java.sql.DriverManager;
 
public class Database {
	
	private static String user="root";
	private static String password="gestionadmin";
	private static String bdName="scanoa";
	private static String encryptPassword="gatin";
 
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/"+bdName,
                    user, password);
            return con;
        } catch (Exception ex) {
            System.out.println("Database.getConnection() Error -->" + ex.getMessage());
            return null;
        }
    }
 
    public static void close(Connection con) {
        try {
            con.close();
        } catch (Exception ex) {
        }
    }
    public static String getEncryptPwd(){
    	return encryptPassword;
    }
    
}
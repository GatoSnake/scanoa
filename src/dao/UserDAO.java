package dao;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.UserBean;
  
public class UserDAO {
	static Connection con = null;
    static PreparedStatement ps = null;
    static Statement cs = null;
		
	public static UserBean login(String correo, String contraseña){
		Connection con=null;
		Statement stmt=null;
		ResultSet rs;
		UserBean bean=new UserBean();
		String searchQuery="SELECT correo,nombre,AES_DECRYPT(contraseña,'"+Database.getEncryptPwd()
				+"'),fecha_registro,tipo_usuario FROM usuarios WHERE correo='"+correo+"'";
		try {
			con = Database.getConnection();
			stmt=con.createStatement();
			rs = stmt.executeQuery(searchQuery);
			if(rs.next()){
				if(rs.getString(3).equals(contraseña)){
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					bean.setCorreo(rs.getString(1));
					bean.setNombre(rs.getString(2));
					bean.setContraseña(rs.getString(3));
					java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(4);
					bean.setFecha_registro(dateFormat.format(dbSqlTimestamp));
					bean.setTipo(rs.getInt(5));
					bean.setValid(true);
				} else {
					bean.setValid(false);
				}
			} else {
				bean.setValid(false);
			}
			return bean;
		} catch (Exception e) {
			System.out.println("Error en login() --> "+e);
			e.printStackTrace();
			bean.setValid(false);
			return bean;
		}
		finally {
			Database.close(con);
		}
	}
	
	public static UserBean createUser(int tipo, String nombre, String correo, String contraseña) throws ParseException {
		Connection con=null;
		Statement stmt=null;
		ResultSet rs;
		UserBean bean=new UserBean();
		correo = correo.toLowerCase();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = new Date();
    	java.util.Date temp = dateFormat.parse(dateFormat.format(date));
    	java.sql.Date sqlDate = new java.sql.Date(temp.getTime());
    	Time sqlTime = new java.sql.Time(temp.getTime());
 	   	String fecha = sqlDate+" "+sqlTime;
		String query="INSERT INTO usuarios (correo,nombre,contraseña,fecha_registro,tipo_usuario) VALUES ('"
				+correo+"','"+nombre
        		+"',AES_ENCRYPT('"+contraseña+"','"+Database.getEncryptPwd()+"'),'"
        		+fecha+"',"
        		+tipo+") ";
		String searchQuery="SELECT correo,nombre,AES_DECRYPT(contraseña,'"+Database.getEncryptPwd()
				+"'),fecha_registro,tipo_usuario FROM usuarios WHERE correo='"+correo+"'";
		try {    		
     	   	con = Database.getConnection();
     	   	stmt=con.createStatement();
     	   	rs = stmt.executeQuery(searchQuery);
            
            if (rs.next()) //caso en que encontro usuario con correo ya registrado
            {
            	bean.setValid(false);
                return bean;
            }
            else { //caso en que no existe un usuario con el correo registrado
            	DateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
            	stmt.executeUpdate(query);
            	rs = stmt.executeQuery(searchQuery);
            	while(rs.next()){
            		bean.setCorreo(rs.getString(1));
            		bean.setNombre(rs.getString(2));
            		bean.setContraseña(rs.getString(3));
            		java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(4);
					bean.setFecha_registro(dateFormat2.format(dbSqlTimestamp));
					bean.setTipo(rs.getInt(5));
					bean.setValid(true);
            	}
                return bean;
            }
        } catch (Exception ex) {
            System.out.println("Error en createUser() -->" + ex.getMessage());
            bean.setValid(false);
            return bean;
        } finally {
            Database.close(con);
        }
	}

	public static boolean editUser(String nameColumn, String updateValue, String correo) {
        try {
        	String query = "UPDATE usuarios SET "+nameColumn+"='"+updateValue+"' WHERE correo='"+correo+"'";
        	con = Database.getConnection();
            ps = con.prepareStatement(query);            
            ps.executeUpdate();
            return true;
        } catch (Exception ex) {
            System.out.println("Error en editUser() -->" + ex.getMessage());
            return false;
        } finally {
            Database.close(con);
        }
	}
	
	public static boolean deleteUser(String correo) {
        try {
        	con = Database.getConnection();
            ps = con.prepareStatement(
                    "DELETE FROM usuarios WHERE correo='"+correo+"'");            
            ps.executeUpdate();
            return true;
        } catch (Exception ex) {
            System.out.println("Error en deleteUser() -->" + ex.getMessage());
            return false;
        } finally {
            Database.close(con);
        }
	}
	
	public static boolean existUser(String correo){
		Connection con = null;
        Statement cs = null;
        boolean exist = false;
        try {
        	String searchQuery = "SELECT * FROM usuarios WHERE correo='"+correo+"'";
        	con = Database.getConnection();
            cs = con.createStatement();
            ResultSet rs = cs.executeQuery(searchQuery);
            if(rs.next()){
            	exist = true;
            }
            return exist;
        } catch (Exception ex) {
            System.out.println("Error en existUser() -->" + ex.getMessage());
            return exist;
        } finally {
            Database.close(con);
        }
	}
	
	public static List<UserBean> allUsersSpecificType(int tipo) {
		Connection con = null;
        Statement cs = null;
        try {
        	List<UserBean> users = new ArrayList<UserBean>();
        	String searchQuery ="SELECT correo,nombre,AES_DECRYPT(contraseña,'"+Database.getEncryptPwd()
				+"'),fecha_registro,tipo_usuario FROM usuarios WHERE tipo_usuario="+tipo;
        	con = Database.getConnection();
            cs = con.createStatement();
            ResultSet rs = cs.executeQuery(searchQuery);
            while(rs.next()){
            	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            	UserBean user = new UserBean();
            	user.setCorreo(rs.getString(1));
            	user.setNombre(rs.getString(2));
            	user.setContraseña(rs.getString(3));
            	java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(4);
				user.setFecha_registro(dateFormat.format(dbSqlTimestamp));;
            	user.setTipo(rs.getInt(5));
            	users.add(user);
            }
            return users;
        } catch (Exception ex) {
            System.out.println("Error en allUsers() -->" + ex.getMessage());
            return null;
        } finally {
            Database.close(con);
        }
	}
	
	public static List<UserBean> allUsers(String correo) {
		Connection con = null;
        Statement cs = null;
        try {
        	List<UserBean> users = new ArrayList<UserBean>();
        	String searchQuery ="SELECT correo,nombre,AES_DECRYPT(contraseña,'"+Database.getEncryptPwd()
				+"'),fecha_registro,tipo_usuario FROM usuarios WHERE correo!='"+correo+"'";
        	con = Database.getConnection();
            cs = con.createStatement();
            ResultSet rs = cs.executeQuery(searchQuery);
            while(rs.next()){
            	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            	UserBean user = new UserBean();
            	user.setCorreo(rs.getString(1));
            	user.setNombre(rs.getString(2));
            	user.setContraseña(rs.getString(3));
            	java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(4);
				user.setFecha_registro(dateFormat.format(dbSqlTimestamp));;
            	user.setTipo(rs.getInt(5));
            	users.add(user);
            }
            return users;
        } catch (Exception ex) {
            System.out.println("Error en allUsers() -->" + ex.getMessage());
            return null;
        } finally {
            Database.close(con);
        }
	}
  
}
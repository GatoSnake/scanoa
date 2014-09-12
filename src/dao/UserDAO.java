package dao;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beans.Util;
import util.UserBean;
  
public class UserDAO {
	static Connection con = null;
    static PreparedStatement ps = null;
    static Statement cs = null;
    static ResultSet rs = null;
    
	public static UserBean loginUser(String correo, String contraseña) {
		UserBean bean = new UserBean();
		try {
			String searchQuery = "SELECT id,correo,nombre,AES_DECRYPT(contrasena,'"+Database.getEncryptPwd()
					+"'),fecha_registro,tipo_usuario,path_img FROM usuarios WHERE correo='"+correo+"'";
			con = Database.getConnection();
			cs = con.createStatement();
			rs = cs.executeQuery(searchQuery);
			if(rs.next()){
				if(rs.getString(4).equals(contraseña)){
					bean.setId(rs.getInt(1));
					bean.setCorreo(rs.getString(2));
					bean.setNombre(rs.getString(3));
					bean.setContraseña(rs.getString(4));
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(5);
					bean.setFecha_registro(dateFormat.format(dbSqlTimestamp));
					bean.setTipo(rs.getInt(6));
					bean.setPath_img(rs.getString(7));
					bean.setValid(true);
				} else {
					bean.setValid(false);
				}
			} else {
				bean.setValid(false);
			}
		} catch (Exception e) {
			System.out.println("Error en loginUser() --> "+e);
			e.printStackTrace();
			bean.setValid(false);
			return bean;
		}
		finally {
			Database.close(con);
		}
		return bean;
	}
	
	public static UserBean createUser(int tipo, String nombre, String correo, String contraseña, String name_img) {
		UserBean bean = new UserBean();
		correo = correo.toLowerCase();
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	Date date = new Date();
			java.util.Date temp = dateFormat.parse(dateFormat.format(date));
	    	java.sql.Date sqlDate = new java.sql.Date(temp.getTime());
	    	Time sqlTime = new java.sql.Time(temp.getTime());
	 	   	String fecha = sqlDate+" "+sqlTime;
			String query = "INSERT INTO usuarios (correo,nombre,contrasena,fecha_registro,tipo_usuario,path_img) VALUES ('"
					+correo+"','"+nombre
	        		+"',AES_ENCRYPT('"+contraseña+"','"+Database.getEncryptPwd()+"'),'"
	        		+fecha+"',"+tipo+",'"+name_img+"') ";
			String searchQuery = "SELECT id,correo,nombre,AES_DECRYPT(contrasena,'"+Database.getEncryptPwd()
					+"'),fecha_registro,tipo_usuario,path_img FROM usuarios WHERE correo='"+correo+"'";
     	   	con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	rs = cs.executeQuery(searchQuery);            
            if (rs.next()) { //caso correo usuario existente
            	bean.setValid(false);
            } else {
            	DateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
            	cs.executeUpdate(query);
            	rs = cs.executeQuery(searchQuery);
            	while(rs.next()){
            		bean.setId(rs.getInt(1));
            		bean.setCorreo(rs.getString(2));
            		bean.setNombre(rs.getString(3));
            		bean.setContraseña(rs.getString(4));
            		java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(5);
					bean.setFecha_registro(dateFormat2.format(dbSqlTimestamp));
					bean.setTipo(rs.getInt(6));
					bean.setPath_img(rs.getString(7));
					bean.setValid(true);
            	}
            }
        } catch (Exception ex) {
            System.out.println("Error en createUser() -->" + ex.getMessage());
            bean.setValid(false);
            return bean;
        } finally {
            Database.close(con);
        }
		return bean;
	}
	
	public static List<UserBean> allUsersWithoutCurrent(String correo) {
		List<UserBean> users = new ArrayList<UserBean>();
        try {
        	String query ="SELECT id,correo,nombre,AES_DECRYPT(contrasena,'"+Database.getEncryptPwd()
				+"'),fecha_registro,tipo_usuario,path_img FROM usuarios WHERE correo!='"+correo+"'";
        	con = Database.getConnection();
            cs = con.createStatement();
            rs = cs.executeQuery(query);
            while(rs.next()){
            	UserBean user = new UserBean();
            	user.setId(rs.getInt(1));
            	user.setCorreo(rs.getString(2));
            	user.setNombre(rs.getString(3));
            	user.setContraseña(rs.getString(4));
            	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            	java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(5);
				user.setFecha_registro(dateFormat.format(dbSqlTimestamp));;
            	user.setTipo(rs.getInt(6));
            	if(rs.getString(7).contains(Util.name_img_default)){
            		user.setPath_img(Util.dir_img_default+rs.getString(7));
				}else{
					user.setPath_img(Util.dir_img_users+rs.getString(7));
				}
            	users.add(user);
            }
            
        } catch (Exception ex) {
            System.out.println("Error en allUsersWithoutCurrent() -->" + ex.getMessage());
            return null;
        } finally {
            Database.close(con);
        }
        return users;
	}
  
}
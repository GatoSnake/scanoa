package dao;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenericDAO {
	
	   static Connection currentCon = null;
	   static ResultSet rs = null;  
	
	 /**
	    * Este metodo busca utilizar una abstraccion de tipo update para las columnas de tipo String donde el servlet 
	    * ingresa los valores de la tabla y los parametros junto con los datos a actualizar y de identificacion.
	    * Un ejemplo de como llamar a esta funcion seria:
	    * UserDAO.genericStringUpdate("alumnos", "primer_nombre",user.getPrimerNombre,"run", user.getRut())  
	    */
	   public static boolean genericStringUpdate(String tableName, String parameter, String updatedValue, String whereIdentifier, String whereValue){
		   Statement stmt = null;
		   String query= "UPDATE "+tableName+" SET "+parameter+"='"+updatedValue+"' WHERE "+whereIdentifier+"='"+whereValue+"'";
//		   System.out.println(query);
		   try {
		         currentCon = Database.getConnection();
		         stmt=currentCon.createStatement();
	        	 stmt.executeUpdate(query);
	        	 return true;
		   }catch (Exception ex) {
		         System.out.println("Error en genericStringUpdate() -->" + ex);
		         return false;
		      }
	   }
	   
	   public static boolean genericIntegerUpdate(String tableName, String parameter, String updatedValue, String whereIdentifier, int whereValue){
		   Statement stmt = null;
		   String query= "UPDATE "+tableName+" SET "+parameter+"='"+updatedValue+"' WHERE "+whereIdentifier+"="+whereValue;
//		   System.out.println(query);
		   try {
		         currentCon = Database.getConnection();
		         stmt=currentCon.createStatement();
	        	 stmt.executeUpdate(query);
	        	 return true;
		   }catch (Exception ex) {
		         System.out.println("Error en genericIntegerUpdate() -->" + ex);
		         return false;
		      }
	   }
	   
	   public static boolean genericIntegerUpdate2(String tableName, String parameter, int updatedValue, String whereIdentifier, String whereValue){
		   Statement stmt = null;
		   String query= "UPDATE "+tableName+" SET "+parameter+"="+updatedValue+" WHERE "+whereIdentifier+"='"+whereValue+"'";
//		   System.out.println(query);
		   try {
		         currentCon = Database.getConnection();
		         stmt=currentCon.createStatement();
	        	 stmt.executeUpdate(query);
	        	 return true;
		   }catch (Exception ex) {
		         System.out.println("Error en genericIntegerUpdate() -->" + ex);
		         return false;
		      }
	   }
	   
	   /** Este metodo busca utilizar una abstraccion de tipo update para las columnas de tipo int donde el servlet 
	   * ingresa los valores de la tabla y los parametros junto con los datos a actualizar y de identificacion.
	   * Un ejemplo de como llamar a esta funcion seria:
	   * UserDAO.genericStringUpdate("empresas", "numero_trabajadores",user.getTrabajadores(),"rut", user.getRut())  
	   */
	   public static boolean genericNumberUpdate(String tableName, String parameter, String updatedValue, String whereIdentifier, String whereValue){
		   Statement stmt = null;
		   String query= "UPDATE "+tableName+" SET "+parameter+"="+updatedValue+" WHERE "+whereIdentifier+"="+whereValue;
//		   System.out.println(query);
		   try {
		         currentCon = Database.getConnection();
		         stmt=currentCon.createStatement();
	        	 stmt.executeUpdate(query);
	        	 return true;
		   }catch (Exception ex) {
		         System.out.println("Error en genericNumberUpdate() -->" + ex);
		         return false;
		      }
	   }
	   
	    /** Este metodo busca utilizar una abstraccion de tipo update para las columnas de tipo string donde el servlet para la password
	    * ingresa los valores de la tabla y los parametros junto con los datos a actualizar y de identificacion.
	    * Un ejemplo de como llamar a esta funcion seria:
	    * UserDAO.genericDateUpdate("alumnos", "nacimiento",user.getNacimiento(),"rut", user.getRut())  
	    */
	   public static boolean genericPassUpdate(String tableName, String updatedValue, String whereIdentifier, String whereValue){
		   Statement stmt = null;
		   String query= "UPDATE "+tableName+" SET `contraseña`=AES_ENCRYPT('"+updatedValue+"','"+Database.getEncryptPwd()+"') WHERE "+whereIdentifier+"='"+whereValue+"'";
//		   System.out.println(query);
		   try {
		         currentCon = Database.getConnection();
		         stmt=currentCon.createStatement();
	        	 stmt.executeUpdate(query);
	        	 System.out.println("pass true!");
	        	 return true;
		   }catch (Exception ex) {
		         System.out.println("Error en genericPassUpdate() -->" + ex);
		         return false;
		      }
	   }
		public static boolean genericDateUpdate(String tableName, String parameter, String fechaNacimiento, String whereIdentifier, int whereValue) {
			PreparedStatement ps;
			String query ="UPDATE "+tableName+" SET "+parameter+"=? WHERE "+whereIdentifier+"=?";
//			System.out.println(query);
			try{
				currentCon = Database.getConnection();
				ps = currentCon.prepareStatement(query);
				ps.setString(1, fechaNacimiento);
				ps.setInt(2, whereValue);
				ps.executeUpdate();
			}catch(Exception ex) {
		         System.out.println("Error en genericDateUpdate() -->" + ex);
		         return false;
		    }
			
			return true;
		}
		
		public static boolean genericIntegerDelete(String tableName, String whereIdentifier, int whereValue){
			PreparedStatement ps;
			String query = "DELETE FROM "+tableName+" WHERE "+whereIdentifier+"="+whereValue;
//			System.out.println(query);
			try{
				currentCon = Database.getConnection();
				ps = currentCon.prepareStatement(query);
				ps.executeUpdate();
			}catch(Exception ex) {
		         System.out.println("Error en genericIntDelete() -->" + ex);
		         ex.printStackTrace();
		         return false;
		    }
			return true;
		}
		
		public static boolean genericStringDelete(String tableName, String whereIdentifier, String whereValue){
			PreparedStatement ps;
			String query = "DELETE FROM "+tableName+" WHERE "+whereIdentifier+"='"+whereValue+"'";
//			System.out.println(query);
			try{
				currentCon = Database.getConnection();
				ps = currentCon.prepareStatement(query);
				ps.executeUpdate();
			}catch(Exception ex) {
		         System.out.println("Error en genericStringDelete() -->" + ex);
		         ex.printStackTrace();
		         return false;
		    }
			return true;
		}
		
		public static String genericSelectLastRowID(String tableName){
			String query = "SELECT * FROM "+tableName+" ORDER BY id DESC LIMIT 1";
			String fecha = "";
			try{
				currentCon = Database.getConnection();
	     	   	Statement stmt = currentCon.createStatement();
	     	   	rs = stmt.executeQuery(query);
				if(rs.next()){
					fecha = rs.getDate(2)+" "+rs.getTime(2);
				}
			}catch(Exception ex) {
		         System.out.println("Error en genericSelectLastRowID() -->" + ex);
		         ex.printStackTrace();
		         return "";
		    }
			return fecha;
		}
		
		public static void genericInsertDate(String tableName, String nameColumn, String string){
			String query="INSERT INTO "+tableName+" ("+nameColumn+") VALUE ('"+string+"')";
			currentCon = Database.getConnection();
     	   	Statement stmt = null;
			try {
				stmt = currentCon.createStatement();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				stmt.executeUpdate(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public static boolean genericInsertDate2(String tableName, String nameColumn){
			Connection con = null;
			Statement stmt = null;
	    	try {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    	Date date = new Date();
		    	java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFormat.format(date));
		    	java.sql.Date sqlDate = new java.sql.Date(temp.getTime());
		    	Time sqlTime = new java.sql.Time(temp.getTime());
		 	   	String fecha = sqlDate+" "+sqlTime;
				String query = "INSERT INTO "+tableName+" ("+nameColumn+") VALUES ('"+fecha+"');";
	     	   	con = Database.getConnection();
	     	   	stmt = con.createStatement();
	     	   	stmt.executeUpdate(query);
	     	   	return true;
	        } catch (Exception ex) {
	            System.out.println("Error en insertLogRecatg() -->" + ex.getMessage());
	            return false;
	        } finally {
	            Database.close(con);
	        }
		}
		
		public static String genericSelectSpecificValue(String tableName, String column, String whereIdentifier, String whereValue){
			String query = "SELECT "+column+" FROM "+tableName+" WHERE "+whereIdentifier+"='"+whereValue+"'";
			String value = null;
			try{
				currentCon = Database.getConnection();
	     	   	Statement stmt = currentCon.createStatement();
	     	   	rs = stmt.executeQuery(query);
				if(rs.next()){
					value = rs.getString(1);
				}
			}catch(Exception ex) {
		         System.out.println("Error en genericSelectLastRowID() -->" + ex);
		         ex.printStackTrace();
		         return null;
		    }
			return value;
		}
	   
	}
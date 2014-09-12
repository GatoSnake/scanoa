package dao;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenericDAO {
	static Connection con = null;
    static PreparedStatement ps = null;
    static Statement cs = null;
    static ResultSet rs = null;
    
    public static boolean stringUpdateWhereString (String tableName, String parameter, String updatedValue, String whereIdentifier, String whereValue) {
    	try {
        	String query= "UPDATE "+tableName+" SET "+parameter+"='"+updatedValue+"' WHERE "+whereIdentifier+"='"+whereValue+"'";
    		con = Database.getConnection();
			cs = con.createStatement();
			cs.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Error en stringUpdateWhereString() --> " + ex);
			return false;
		} finally {
            Database.close(con);
        }
    	return true;
    }
    
    public static boolean stringUpdateWhereInteger (String tableName, String parameter, String updatedValue, String whereIdentifier, String whereValue) {
    	try {
        	String query= "UPDATE "+tableName+" SET "+parameter+"='"+updatedValue+"' WHERE "+whereIdentifier+"="+whereValue;
    		con = Database.getConnection();
			cs = con.createStatement();
			cs.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Error en stringUpdateWhereInteger() --> " + ex);
			return false;
		} finally {
            Database.close(con);
        }
    	return true;
    }
    
    public static boolean integerUpdateWhereString (String tableName, String parameter, int updatedValue, String whereIdentifier, String whereValue) {
    	try {
        	String query= "UPDATE "+tableName+" SET "+parameter+"="+updatedValue+" WHERE "+whereIdentifier+"='"+whereValue+"'";
    		con = Database.getConnection();
			cs = con.createStatement();
			cs.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Error en integerUpdateWhereString() --> " + ex);
			return false;
		} finally {
            Database.close(con);
        }
    	return true;
    }
    
    public static boolean deleteWhereString (String tableName, String whereIdentifier, String whereValue) {
    	try {
    		String query = "DELETE FROM "+tableName+" WHERE "+whereIdentifier+"='"+whereValue+"'";
    		con = Database.getConnection();
			cs = con.createStatement();
			cs.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Error en deleteWhereString() --> " + ex);
			return false;
		} finally {
            Database.close(con);
        }
    	return true;
    }
    
    public static boolean deleteWhereInteger (String tableName, String whereIdentifier, String whereValue) {
    	try {
    		String query = "DELETE FROM "+tableName+" WHERE "+whereIdentifier+"="+whereValue;
    		con = Database.getConnection();
			cs = con.createStatement();
			cs.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Error en deleteWhereInteger() --> " + ex);
			return false;
		} finally {
            Database.close(con);
        }
    	return true;
    }
    
    public static boolean passwordUpdateWhereString (String tableName, String parameter, String updatedValue, String whereIdentifier, String whereValue) {
    	try {
    		String query= "UPDATE "+tableName+" SET "+parameter+"=AES_ENCRYPT('"+updatedValue+"','"+Database.getEncryptPwd()+"') WHERE "+whereIdentifier+"='"+whereValue+"'";
    		con = Database.getConnection();
			cs = con.createStatement();
			cs.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Error en passwordUpdateWhereString() --> " + ex);
			return false;
		} finally {
            Database.close(con);
        }
    	return true;
    }
    
    public static String selectStringWhereString (String parameter, String tableName, String whereIdentifier, String whereValue) {
    	try{
    		String query = "SELECT "+parameter+" FROM "+tableName+" WHERE "+whereIdentifier+"='"+whereValue+"'";
			con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	rs = cs.executeQuery(query);
			if(rs.next()){
				return rs.getString(1);
			}
		}catch(Exception ex) {
	         System.out.println("Error en selectStringWhereString() -->" + ex);
	         ex.printStackTrace();
	         return null;
	    } finally {
            Database.close(con);
        }
    	return null;
    }
    
    public static boolean existValueInTable (String tableName, String whereIdentifier, String whereValue) {
    	try{
    		String query = "SELECT * FROM "+tableName+" WHERE "+whereIdentifier+"='"+whereValue+"' LIMIT 1";
			con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	rs = cs.executeQuery(query);
			if(rs.next()){
				return true;
			}
		}catch(Exception ex) {
	         System.out.println("Error en existValueInTable() -->" + ex);
	         ex.printStackTrace();
	         return false;
	    } finally {
            Database.close(con);
        }
    	return false;
    }
    
    public static String getLastDateInTableByID (String tableName) {
    	try{
    		String value = null;
    		String query = "SELECT * FROM "+tableName+" ORDER BY id DESC LIMIT 1";
			con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	rs = cs.executeQuery(query);
			if(rs.next()){
				value = rs.getDate(2)+" "+rs.getTime(2);
				return value;
			}
		}catch(Exception ex) {
	         System.out.println("Error en getLastStringInTableByID() -->" + ex);
	         return null;
	    } finally {
            Database.close(con);
        }
    	return null;
    }
    
    public static boolean stringInsertWhereString (String tableName, String parameter, String value) {
    	try {
    		String query="INSERT INTO "+tableName+" ("+parameter+") VALUE ('"+value+"')";
    		con = Database.getConnection();
			cs = con.createStatement();
			cs.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Error en dateInsertWhereString() --> " + ex);
			return false;
		} finally {
            Database.close(con);
        }
    	return true;
    }
    
    public static boolean dateAutomaticInsertWhereString (String tableName, String parameter){
    	try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	Date date = new Date();
	    	java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFormat.format(date));
	    	java.sql.Date sqlDate = new java.sql.Date(temp.getTime());
	    	Time sqlTime = new java.sql.Time(temp.getTime());
	 	   	String fecha = sqlDate+" "+sqlTime;
			String query = "INSERT INTO "+tableName+" ("+parameter+") VALUES ('"+fecha+"');";
     	   	con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	cs.executeUpdate(query);
        } catch (Exception ex) {
            System.out.println("Error en insertLogRecatg() -->" + ex.getMessage());
            return false;
        } finally {
            Database.close(con);
        }
    	return true;
	}
    
    public static boolean NULLUpdateWhereString (String tableName, String parameter, String whereIdentifier, String whereValue) {
    	try {
        	String query= "UPDATE "+tableName+" SET "+parameter+"=NULL WHERE "+whereIdentifier+"='"+whereValue+"'";
    		con = Database.getConnection();
			cs = con.createStatement();
			cs.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Error en NULLUpdateWhereString() --> " + ex);
			return false;
		} finally {
            Database.close(con);
        }
    	return true;
    }
    
    public static int countTable(String tableName){
    	int value = 0;
		try{
			String query = "SELECT count(*) FROM "+tableName;
			con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	rs = cs.executeQuery(query);
			if(rs.next()){
				value = rs.getInt(1);
			}
		} catch(Exception ex) {
	         System.out.println("Error en countTable() -->" + ex);
	         ex.printStackTrace();
	         return 0;
	    } finally {
            Database.close(con);
        }
		return value;
	}
    
}
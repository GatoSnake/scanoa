package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.OABean;
import util.TopicBean;

public class OADAO {
	static Connection con = null;
    static PreparedStatement ps = null;
    static Statement cs = null;
    static ResultSet rs;
    
    public static List<OABean> getAllOAs(){
    	Connection con = null;
        Statement cs = null;
        try {
        	List<OABean> oas = new ArrayList<OABean>();
        	String searchQuery ="SELECT * FROM learning_objects";
        	con = Database.getConnection();
            cs = con.createStatement();
            ResultSet rs = cs.executeQuery(searchQuery);
            while(rs.next()){
            	List<TopicBean> topicsCatRank = new ArrayList<TopicBean>();
            	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            	OABean oa = new OABean();
            	oa.setId(rs.getInt(1));
            	oa.setTitulo(rs.getString(2));
            	oa.setDescripcion(rs.getString(3));
            	oa.setUrl(rs.getString(4));
            	java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(5);
				oa.setFecha_ingreso(dateFormat.format(dbSqlTimestamp));
            	oa.setUsuario(rs.getString(6));
            	String rankTopics = rs.getString(7);
				String[] topicosRank = rankTopics.split(";");
				for(int i=0; i<topicosRank.length; i++){
					topicsCatRank.add(new TopicBean(null,topicosRank[i],0));
				}
				oa.setRankTopicos(topicsCatRank);
            	oas.add(oa);
            }
            return oas;
        } catch (Exception ex) {
            System.out.println("Error en getAllOAs() -->" + ex.getMessage());
            return null;
        } finally {
            Database.close(con);
        }
    }
    
    public static List<OABean> getOAsUser(String correo){
    	Connection con = null;
        Statement cs = null;
        try{
        	List<OABean> listOAs = new ArrayList<OABean>();
        	String query = "SELECT * FROM learning_objects WHERE usuario_correo='"+correo+"'";
        	con = Database.getConnection();
            cs = con.createStatement();
            ResultSet rs = cs.executeQuery(query);
            while(rs.next()){
            	List<TopicBean> topicsCatRank = new ArrayList<TopicBean>();
            	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            	OABean oa = new OABean();
            	oa.setId(rs.getInt(1));
            	oa.setTitulo(rs.getString(2));
            	oa.setDescripcion(rs.getString(3));
            	oa.setUrl(rs.getString(4));
            	java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(5);
				oa.setFecha_ingreso(dateFormat.format(dbSqlTimestamp));
				String rankTopics = rs.getString(7);
				String[] topicosRank = rankTopics.split(";");
				for(int i=0; i<topicosRank.length; i++){
					topicsCatRank.add(new TopicBean(null,topicosRank[i],0));
				}
				oa.setRankTopicos(topicsCatRank);
            	listOAs.add(oa);
            }
        	return listOAs;
        } catch (Exception ex) {
            System.out.println("Error en allUsers() -->" + ex.getMessage());
            return null;
        } finally {
            Database.close(con);
        }
    }
    
    public static boolean insertOA(String url, String titulo, String descripcion, String correo) throws ParseException {
    	Connection con = null;
		Statement stmt = null;
    	try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	Date date = new Date();
	    	java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFormat.format(date));
	    	java.sql.Date sqlDate = new java.sql.Date(temp.getTime());
	    	Time sqlTime = new java.sql.Time(temp.getTime());
	 	   	String fecha = sqlDate+" "+sqlTime;
			String query = "INSERT INTO learning_objects (titulo,descripcion,url,fecha_ingreso,usuario_correo) "
					+"VALUES ('"
					+titulo+"','"
					+descripcion+"','"
					+url+"','"
					+fecha+"','"
					+correo+"'"
					+");";
     	   	con = Database.getConnection();
     	   	stmt = con.createStatement();
     	   	stmt.executeUpdate(query);
     	   	return true;
        } catch (Exception ex) {
            System.out.println("Error en insertOA() -->" + ex.getMessage());
            return false;
        } finally {
            Database.close(con);
        }
	}
    
    public static boolean editOA(String url, String titulo, String descripcion, String correo) throws ParseException {
    	Connection con = null;
		Statement stmt = null;
    	try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	Date date = new Date();
	    	java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFormat.format(date));
	    	java.sql.Date sqlDate = new java.sql.Date(temp.getTime());
	    	Time sqlTime = new java.sql.Time(temp.getTime());
	 	   	String fecha = sqlDate+" "+sqlTime;
			String query = "INSERT INTO learning_objects (titulo,descripcion,url,fecha_ingreso,usuario_correo) "
					+"VALUES ('"
					+titulo+"','"
					+descripcion+"','"
					+url+"','"
					+fecha+"','"
					+correo+"'"
					+");";
     	   	con = Database.getConnection();
     	   	stmt = con.createStatement();
     	   	stmt.executeUpdate(query);
     	   	return true;
        } catch (Exception ex) {
            System.out.println("Error en insertOA() -->" + ex.getMessage());
            return false;
        } finally {
            Database.close(con);
        }
	}
    
    public static String getIdActualOA(String correo, String url){
    	Connection con = null;
		Statement stmt = null;
		String id = "";
    	String query = "SELECT id FROM learning_objects WHERE usuario_correo='"+correo+"' AND url='"+url+"' ORDER BY id DESC LIMIT 1";
//		System.out.println(query);
    	try{
			con = Database.getConnection();
     	   	stmt=con.createStatement();
     	   	rs = stmt.executeQuery(query);
			if(rs.next()){
				id = rs.getString(1);
			}
		}catch(Exception ex) {
	         System.out.println("Error en genericSelectLastRowID() -->" + ex);
	         ex.printStackTrace();
	         return "";
	    }
		return id;
	}
    
    public static List<OABean> getOAForSearch(String column, String busqueda){
    	Connection con = null;
		Statement stmt = null;
		List<OABean> oas = new ArrayList<OABean>();
    	String query = "SELECT * FROM learning_objects WHERE "+column+" LIKE '%"+busqueda+"%'";
//		System.out.println(query);
    	try{
			con = Database.getConnection();
     	   	stmt=con.createStatement();
     	   	rs = stmt.executeQuery(query);
			while(rs.next()){
				List<TopicBean> topicsCatRank = new ArrayList<TopicBean>();
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				OABean oa = new OABean();
				oa.setId(rs.getInt(1));
				oa.setTitulo(rs.getString(2));
				oa.setDescripcion(rs.getString(3));
				oa.setUrl(rs.getString(4));
				java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(5);
				oa.setFecha_ingreso(dateFormat.format(dbSqlTimestamp));
				oa.setUsuario(rs.getString(6));
				String rankTopics = rs.getString(7);
				String[] topicosRank = rankTopics.split(";");
				for(int i=0; i<topicosRank.length; i++){
					topicsCatRank.add(new TopicBean(null,topicosRank[i],0));
				}
				oa.setRankTopicos(topicsCatRank);
				oas.add(oa);
			}
		}catch(Exception ex) {
	         System.out.println("Error en getOAForSearch() -->" + ex);
	         ex.printStackTrace();
	         return null;
	    }
		return oas;
    }
    
    public static OABean getOAForId(String id){
    	Connection con = null;
		Statement stmt = null;
		OABean oa = new OABean();
    	String query = "SELECT * FROM learning_objects WHERE id="+id;
//		System.out.println(query);
    	try{
			con = Database.getConnection();
     	   	stmt=con.createStatement();
     	   	rs = stmt.executeQuery(query);
			if(rs.next()){
				List<TopicBean> topicsCatRank = new ArrayList<TopicBean>();
				oa.setId(rs.getInt(1));
				oa.setTitulo(rs.getString(2));
				oa.setDescripcion(rs.getString(3));
				oa.setUrl(rs.getString(4));
				oa.setFecha_ingreso(rs.getString(5));
				oa.setUsuario(rs.getString(6));
				String rankTopics = rs.getString(7);
				String[] topicosRank = rankTopics.split(";");
				for(int i=0; i<topicosRank.length; i++){
					topicsCatRank.add(new TopicBean(null,topicosRank[i],0));
				}
				oa.setRankTopicos(topicsCatRank);
			}
		}catch(Exception ex) {
	         System.out.println("Error en getOAForId() -->" + ex);
	         ex.printStackTrace();
	         return null;
	    }
		return oa;    	
    }
    
    public static boolean insertLogRecatg(){
    	Connection con = null;
		Statement stmt = null;
    	try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	Date date = new Date();
	    	java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFormat.format(date));
	    	java.sql.Date sqlDate = new java.sql.Date(temp.getTime());
	    	Time sqlTime = new java.sql.Time(temp.getTime());
	 	   	String fecha = sqlDate+" "+sqlTime;
			String query = "INSERT INTO log_recategorization (fecha_recategorizacion) VALUES ('"+fecha+"');";
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
    
    public static Date getLastLogRecatg(){
    	Connection con = null;
		Statement stmt = null;
		Date time = null;
    	String query = "SELECT * FROM log_recategorization ORDER BY id DESC LIMIT 1";
//		System.out.println(query);
    	try{
			con = Database.getConnection();
     	   	stmt=con.createStatement();
     	   	rs = stmt.executeQuery(query);
			if(rs.next()){
				java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(2);
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String fecha = dateFormat.format(dbSqlTimestamp);
//				Date date1 = dateFormat.parse(fecha);
//	            Date date2 = dateFormat.parse(fecha);
//				System.out.println(date1);
				System.out.println(fecha);
			}
		}catch(Exception ex) {
	         System.out.println("Error en getLastLogRecatg() -->" + ex);
	         ex.printStackTrace();
	         return null;
	    }
		return time;
    }
    
}

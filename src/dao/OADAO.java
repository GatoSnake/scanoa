package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
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
    static ResultSet rs = null;
    
    public static List<OABean> getAllOAs(){
    	List<OABean> oas = new ArrayList<OABean>();
        try {
        	String searchQuery ="SELECT id,titulo,descripcion,url,fecha_ingreso,usuario_correo,id_usuario,ranking_topicos FROM learning_objects";
        	con = Database.getConnection();
            cs = con.createStatement();
            rs = cs.executeQuery(searchQuery);
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
            	oa.setId_usuario(rs.getInt(7));
            	String rankTopics = rs.getString(8);
            	if(rankTopics != null){
            		String[] topicosRank = rankTopics.split(";");
    				for(int i=0; i<topicosRank.length; i++){
    					topicsCatRank.add(new TopicBean(null,topicosRank[i],0));
    				}
    				oa.setRankTopicos(topicsCatRank);
            	}
            	oas.add(oa);
            }
        } catch (Exception ex) {
            System.out.println("Error en getAllOAs() -->" + ex.getMessage());
            return null;
        } finally {
            Database.close(con);
        }
        return oas;
    }
    
    public static List<OABean> getOAsUser(String id){
    	List<OABean> listOAs = new ArrayList<OABean>();
        try{
        	String query = "SELECT id,titulo,descripcion,url,fecha_ingreso,ranking_topicos FROM learning_objects WHERE id_usuario="+id;
        	con = Database.getConnection();
            cs = con.createStatement();
            rs = cs.executeQuery(query);
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
				String rankTopics = rs.getString(6);
				if(rankTopics!=null){
					String[] topicosRank = rankTopics.split(";");
					for(int i=0; i<topicosRank.length; i++){
						topicsCatRank.add(new TopicBean(null,topicosRank[i],0));
					}
					oa.setRankTopicos(topicsCatRank);
				}
				listOAs.add(oa);
            }
        	
        } catch (Exception ex) {
            System.out.println("Error en getOAsUser() -->" + ex.getMessage());
            return null;
        } finally {
            Database.close(con);
        }
        return listOAs;
    }
    
    public static boolean insertOA(String url, String titulo, String descripcion, String correo, String id) {
    	try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	Date date = new Date();
	    	java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFormat.format(date));
	    	java.sql.Date sqlDate = new java.sql.Date(temp.getTime());
	    	Time sqlTime = new java.sql.Time(temp.getTime());
	 	   	String fecha = sqlDate+" "+sqlTime;
			String query = "INSERT INTO learning_objects (titulo,descripcion,url,fecha_ingreso,usuario_correo,id_usuario) "
					+"VALUES ('"
					+titulo+"','"
					+descripcion+"','"
					+url+"','"
					+fecha+"','"
					+correo+"',"
					+id+");";
     	   	con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	cs.executeUpdate(query);
        } catch (Exception ex) {
            System.out.println("Error en insertOA() -->" + ex.getMessage());
            return false;
        } finally {
            Database.close(con);
        }
    	return true;
	}
    
    public static boolean editOAUser(String correo, String id, String whereValue){
    	try {
    		String query= "UPDATE learning_objects SET usuario_correo='"+correo+"', id_usuario="+id+" WHERE usuario_correo='"+whereValue+"'";
     	   	con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	cs.executeUpdate(query);
        } catch (Exception ex) {
            System.out.println("Error en insertOA() -->" + ex.getMessage());
            return false;
        } finally {
            Database.close(con);
        }
    	return true;
	}
    
    public static String getIdActualOA(String id, String url){
		String idOA = null;
    	String query = "SELECT id FROM learning_objects WHERE id_usuario="+id+" AND url='"+url+"' ORDER BY id DESC LIMIT 1";
    	try{
			con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	rs = cs.executeQuery(query);
			if(rs.next()){
				idOA = rs.getString(1);
			}
		} catch(Exception ex) {
	         System.out.println("Error en getIdActualOA() -->" + ex);
	         ex.printStackTrace();
	         return null;
	    } finally {
            Database.close(con);
        }
		return idOA;
	}
    
    public static int countOAsCat(){
    	int value = 0;
		try{
			String query = "SELECT count(*) FROM learning_objects WHERE ranking_topicos!='NULL'";
			con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	rs = cs.executeQuery(query);
			if(rs.next()){
				value = rs.getInt(1);
			}
		} catch(Exception ex) {
	         System.out.println("Error en countOAsCat() -->" + ex);
	         ex.printStackTrace();
	         return 0;
	    } finally {
            Database.close(con);
        }
		return value;
	}
    
    public static int countOAsNoCat(){
    	int value = 0;
		try{
			String query = "SELECT count(*) FROM learning_objects WHERE ranking_topicos is NULL";
			con = Database.getConnection();
     	   	cs = con.createStatement();
     	   	rs = cs.executeQuery(query);
			if(rs.next()){
				value = rs.getInt(1);
			}
		} catch(Exception ex) {
	         System.out.println("Error en countOAsCat() -->" + ex);
	         ex.printStackTrace();
	         return 0;
	    } finally {
            Database.close(con);
        }
		return value;
	}
    
}

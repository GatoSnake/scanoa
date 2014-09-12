package beans;

import java.io.File;
import java.util.Random;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
 
public class Util {
	
	//path del project
	public static String dir_img_default = "/BD_images/default/";
	public static String dir_img_users = "/BD_images/users/";
	public static String name_img_default = "img_default.jpeg";
	public static String basePath_img_users = "C:\\Repositorios\\Won\\BD_images\\users\\";
	public static String extension_img = ".jpeg";
	public static String dir_tbd = "C:\\Repositorios\\Won\\ontologia\\tbd";
	public static String dir_rdf= "C:\\Repositorios\\Won\\ontologia\\topicos.rdf";
	public static String dir_oas = "C:\\Repositorios\\Won\\temp_oas\\";
	public static String url_project = "http://10.100.6.200:8089/scanoa/";
	
	//para mandar correos electronicos (JavaMail)
	public static String username = "noreply.scanoa@gmail.com";
	public static String password = "adminscanoanoreply123";
	
	public static HttpSession getSession() {
		return (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}
       
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	//metodo que crea un valor random hexadecimal de n largo
	public static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }
	
	//metodo que elimina un archivo
	public static boolean deleteFile(String path){
		File file = new File(path);
		if(file.delete()){
			return true;
		}else{
			return false;
		}
	}
	
}
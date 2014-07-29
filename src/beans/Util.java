package beans;
 
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
 
 
public class Util {

	public static HttpSession getSession() {
		return (HttpSession)
				FacesContext.
				getCurrentInstance().
				getExternalContext().
				getSession(false);
	}
       
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.
				getCurrentInstance().
				getExternalContext().getRequest();
	}

	public static String getCorreo() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		return session.getAttribute("correo").toString();
      }
       
	public static String getNombre() {
		HttpSession session = getSession();
		if ( session != null )
			return (String) session.getAttribute("nombre");
		else
			return null;
	}
      
	public static String getContraseña() {
		HttpSession session = getSession();
		if ( session != null )
			return (String) session.getAttribute("contraseña");
		else
			return null;
	}
	
}
package beans;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import dao.UserDAO;
import util.UserBean;

@ManagedBean(name = "loginBean")

public class LoginBean {
	
	public String correoLB;
	public String contraseñaLB;	
	
	public void clearParameters(){
		this.correoLB=null;
		this.contraseñaLB=null;
	}
	
	public String loginUser() {
		UserBean resDAO = UserDAO.login(this.correoLB,this.contraseñaLB);
		if (resDAO.isValid()) {
			HttpSession session = Util.getSession();			
			session.setAttribute("correo", resDAO.getCorreo());
			session.setAttribute("nombre", resDAO.getNombre());
			session.setAttribute("contraseña", resDAO.getContraseña());
			session.setAttribute("tipo", resDAO.getTipo());
			session.setAttribute("fecha_registro", resDAO.getFecha_registro());
			clearParameters();
			return "home";
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Datos inválidos!",
							"Por favor, intente denuevo."));
			return null;
		}
	}
	
	public String logout() {
		HttpSession session = Util.getSession();
		session.invalidate();
		return "login";
	}
	
	public void isAdmin(ComponentSystemEvent event){
//		System.out.println("Comprobando si es admin ...");
		HttpSession session = Util.getSession();
		if ((Integer)session.getAttribute("tipo") != 1){
			FacesContext fc = FacesContext.getCurrentInstance();
			ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler)
					fc.getApplication().getNavigationHandler();
			nav.performNavigation("access-denied");
		}
		//EL SIGUIENTE EVENTO NOS PERMITE LLAMAR AL METODO ISADMIN() DESDE
		// UN XHTML PARA MOSTRAR LA PAGINA SI CORRESPONDE AL ADMINISTRADOR -->
		//<f:event listener="#{userBean.isAdmin}" type="preRenderView" />
	}

	public String getCorreoLB() {
		return correoLB;
	}

	public void setCorreoLB(String correoLB) {
		this.correoLB = correoLB;
	}

	public String getContraseñaLB() {
		return contraseñaLB;
	}

	public void setContraseñaLB(String contraseñaLB) {
		this.contraseñaLB = contraseñaLB;
	}	
	
}

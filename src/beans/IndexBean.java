package beans;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

//import ontology.ConexionOntology;
import dao.UserDAO;
import util.UserBean;

@ManagedBean(name = "indexBean")
public class IndexBean {
	
	private HttpSession session = Util.getSession();
	private String correoLogin;
	private String contraseñaLogin;	
	private String correoRegister;
	private String nombreRegister;
	private String contraseñaRegister;
	private int tipoUser;
	
	@ManagedProperty(value="#{param.error}")
	private String error;
	
	public String registerUser() {
		try {
			this.tipoUser = 2;
	    	UserBean resDAO = UserDAO.createUser(this.tipoUser,this.nombreRegister,this.correoRegister,this.contraseñaRegister,Util.name_img_default);
	    	if (resDAO.isValid()) {
	    		session.setAttribute("id", resDAO.getId());
	    		session.setAttribute("correo", resDAO.getCorreo());
				session.setAttribute("nombre", resDAO.getNombre());
				session.setAttribute("contraseña", resDAO.getContraseña());
				session.setAttribute("tipo", resDAO.getTipo());
				session.setAttribute("fecha_registro", resDAO.getFecha_registro());
				if(resDAO.getPath_img().contains(Util.name_img_default)){
					session.setAttribute("path_img", Util.dir_img_default+resDAO.getPath_img());
				}else{
					session.setAttribute("path_img", Util.dir_img_users+resDAO.getPath_img());
				}
				clearParametersRegister();				
	    		return "home";
	    	} else {
	    		Integer error = 0;
	    		return "register?faces-redirect=true&error="+error;
	    	}
		}catch (Exception ex) {
            System.out.println("Error en registerUser() -->" + ex.getMessage());
            return null;
        }
	}
	
	public String loginUser() {
		try {
			UserBean resDAO = UserDAO.loginUser(this.correoLogin,this.contraseñaLogin);
			if(resDAO.isValid()){
	//			ConexionOntology.confirmarModelo();
				session.setAttribute("id", resDAO.getId());
				session.setAttribute("correo", resDAO.getCorreo());
				session.setAttribute("nombre", resDAO.getNombre());
				session.setAttribute("contraseña", resDAO.getContraseña());
				session.setAttribute("tipo", resDAO.getTipo());
				session.setAttribute("fecha_registro", resDAO.getFecha_registro());
				if(resDAO.getPath_img().contains(Util.name_img_default)){
					session.setAttribute("path_img", Util.dir_img_default+resDAO.getPath_img());
				}else{
					session.setAttribute("path_img", Util.dir_img_users+resDAO.getPath_img());
				}
				clearParametersLogin();
				return "home";
			}else{
				Integer error = 0;
	    		return "login?faces-redirect=true&error="+error;
			}
		}catch (Exception ex) {
            System.out.println("Error en loginUser() -->" + ex.getMessage());
            return null;
        }
	}
	
	public String logoutUser() {
		try {
			session.invalidate();
			return "index";
		}catch (Exception ex) {
            System.out.println("Error en logoutUser() -->" + ex.getMessage());
            return "index";
        }
	}
	
	public void isAdmin(ComponentSystemEvent event){
		if ((Integer)session.getAttribute("tipo") != 1){
			FacesContext fc = FacesContext.getCurrentInstance();
			ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler)
					fc.getApplication().getNavigationHandler();
			nav.performNavigation("accessDenied");
		}
	}
	
	public void timerSession(){
		System.out.println(session.getMaxInactiveInterval());
		System.out.println(session.getLastAccessedTime());
	}

	public void clearParametersRegister() {
		this.correoRegister=null;
		this.nombreRegister=null;
		this.contraseñaRegister=null;
		this.tipoUser=0;
	}

	public void clearParametersLogin() {
		this.correoLogin=null;
		this.contraseñaLogin=null;
	}

	public String getCorreoLogin() {
		return correoLogin;
	}

	public void setCorreoLogin(String correoLogin) {
		this.correoLogin = correoLogin;
	}

	public String getContraseñaLogin() {
		return contraseñaLogin;
	}

	public void setContraseñaLogin(String contraseñaLogin) {
		this.contraseñaLogin = contraseñaLogin;
	}

	public String getCorreoRegister() {
		return correoRegister;
	}

	public void setCorreoRegister(String correoRegister) {
		this.correoRegister = correoRegister;
	}

	public String getNombreRegister() {
		return nombreRegister;
	}

	public void setNombreRegister(String nombreRegister) {
		this.nombreRegister = nombreRegister;
	}

	public String getContraseñaRegister() {
		return contraseñaRegister;
	}

	public void setContraseñaRegister(String contraseñaRegister) {
		this.contraseñaRegister = contraseñaRegister;
	}

	public int getTipoUser() {
		return tipoUser;
	}

	public void setTipoUser(int tipoUser) {
		this.tipoUser = tipoUser;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
}

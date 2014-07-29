package beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import dao.UserDAO;
import util.UserBean;

@ManagedBean(name = "registerBean")

public class RegisterBean {
	
	public String correoRB;
	public String nombreRB;
	public String contraseñaRB;	
	public int tipoRB;
	
	public void clearParameters(){
		this.correoRB=null;
		this.nombreRB=null;
		this.contraseñaRB=null;
		this.tipoRB=0;
	}

	public String registerUser() {
		try {
			this.tipoRB = 2;
	    	UserBean resDAO = UserDAO.createUser(this.tipoRB,this.nombreRB,this.correoRB,this.contraseñaRB);
	    	if (resDAO.isValid()) {
	    		HttpSession session = Util.getSession();
	    		session.setAttribute("correo", resDAO.getCorreo());
				session.setAttribute("nombre", resDAO.getNombre());
				session.setAttribute("contraseña", resDAO.getContraseña());
				session.setAttribute("tipo", resDAO.getTipo());
				session.setAttribute("fecha_registro", resDAO.getFecha_registro());
				System.out.println("Cuenta Registrada");
				clearParameters();				
	    		return "home";
	    	} else {
	    		FacesContext.getCurrentInstance().addMessage(
	    				null,
	    				new FacesMessage(FacesMessage.SEVERITY_WARN,
	    						"Correo registrado!",
	    						"Por favor, intentar con otro correo electrónico."));
	    		return null;
	    	}
		}catch (Exception ex) {
            System.out.println("Error en registerUser() -->" + ex.getMessage());
            return null;
        }
	}

	public String getCorreoRB() {
		return correoRB;
	}

	public void setCorreoRB(String correoRB) {
		this.correoRB = correoRB;
	}

	public String getNombreRB() {
		return nombreRB;
	}

	public void setNombreRB(String nombreRB) {
		this.nombreRB = nombreRB;
	}

	public String getContraseñaRB() {
		return contraseñaRB;
	}

	public void setContraseñaRB(String contraseñaRB) {
		this.contraseñaRB = contraseñaRB;
	}

	public int getTipoRB() {
		return tipoRB;
	}

	public void setTipoRB(int tipoRB) {
		this.tipoRB = tipoRB;
	}
}

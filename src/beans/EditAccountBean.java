package beans;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import util.UserBean;
import dao.GenericDAO;

@ManagedBean(name = "editAccountBean")

public class EditAccountBean {
	
	ELContext elContext = FacesContext.getCurrentInstance().getELContext();
	UserBean userBean = (UserBean) FacesContext.getCurrentInstance().getApplication()
	    .getELResolver().getValue(elContext, null, "userBean");
	
	HttpSession session = Util.getSession();
	
	private String nuevaContraseña;	
	
	public String editAccount() {
		boolean flag1 = false;
		boolean flag2 = false;
		
		boolean rs=false;
			
		boolean confContraseña = userBean.getContraseña().equals(session.getAttribute("contraseña"));
		
		if(confContraseña){
			if(!userBean.getCorreo().equals("") ){
				rs = GenericDAO.genericStringUpdate("usuarios", "correo", userBean.getCorreo(), "correo", (String)session.getAttribute("correo"));
				if(rs){ //entra si el nuevo correo no esta registrado
					session.setAttribute("correo", userBean.getCorreo());
					flag1=true;
				}
			}
			if(!userBean.getNombre().equals("") ){
				rs = GenericDAO.genericStringUpdate("usuarios", "nombre", userBean.getNombre(), "correo", (String)session.getAttribute("correo"));
				if(rs){
					session.setAttribute("nombre", userBean.getNombre());
					flag2=true;
				}
			}
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (!confContraseña){ //entra si la contraseña es incorrecta
			context.addMessage(
					"growlEditAccount",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Contraseña incorrecta!",
							"Por favor, ingrese nuevamente su contraseña."));
			System.out.println("Contraseña incorrecta");
		} else {
			if (flag1){
				context.addMessage(
						"growlEditAccount",
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Exito!",
								"Se ha modificado su correo electrónico."));
				System.out.println("Correo electronico modificado");
			}else{
				if(!userBean.getCorreo().equals("")){ //entra si el correo estaba ya registrado
					context.addMessage(
							"growlEditAccount",
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Correo registrado!",
									"Por favor, intentar con otro correo electrónico."));
					System.out.println("Correo electronico ya usado");
				}
			}
			if (flag2 ){
				context.addMessage(
						"growlEditAccount",
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Exito!",
								"Se ha modificado su nombre."));
				System.out.println("Nombre modificado");
			}else{
				if(!userBean.getNombre().equals("")){
					context.addMessage(
							"growlEditAccount",
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Problema",
									"No se ha podido modificar su nombre. Por favor, intente más tarde."));
					System.out.println("Nombre imposible modificar");
				}
			}
		}
		userBean.cleanUserBean();			
		return "editAccount";
	}
	
	public String editContraseñaAccount(){
		boolean confContraseña = userBean.getContraseña().equals(session.getAttribute("contraseña"));
		boolean rs = false;
		boolean flag=false;
			
		if(confContraseña){
			if(!userBean.getContraseña().equals("")){
				rs = GenericDAO.genericPassUpdate("usuarios", this.nuevaContraseña, "correo", (String)session.getAttribute("correo"));
				if(rs){
					flag=rs;
					session.setAttribute("contraseña", this.nuevaContraseña);
				}
				if(flag){
					FacesContext.getCurrentInstance().addMessage(
							"growlEditCont",
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Exito!",
									"Se ha modificado su contraseña."));
				} else {
					FacesContext.getCurrentInstance().addMessage(
							"growlEditCont",
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Problema!",
									"No se ha podido modificar su contraseña. Por favor, intente más tarde."));
				}
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(
					"growlEditCont",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Contraseña incorrecta!",
							"Por favor, ingrese nuevamente su contraseña."));
		}
		
		userBean.cleanUserBean();
		this.nuevaContraseña=null;
		return "editAccount";
	}

	public String getNuevaContraseña() {
		return nuevaContraseña;
	}

	public void setNuevaContraseña(String nuevaContraseña) {
		this.nuevaContraseña = nuevaContraseña;
	}
	
}

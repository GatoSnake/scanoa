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
	
	private String nuevaContrase�a;	
	
	public String editAccount() {
		boolean flag1 = false;
		boolean flag2 = false;
		
		boolean rs=false;
			
		boolean confContrase�a = userBean.getContrase�a().equals(session.getAttribute("contrase�a"));
		
		if(confContrase�a){
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
		
		if (!confContrase�a){ //entra si la contrase�a es incorrecta
			context.addMessage(
					"growlEditAccount",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Contrase�a incorrecta!",
							"Por favor, ingrese nuevamente su contrase�a."));
			System.out.println("Contrase�a incorrecta");
		} else {
			if (flag1){
				context.addMessage(
						"growlEditAccount",
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Exito!",
								"Se ha modificado su correo electr�nico."));
				System.out.println("Correo electronico modificado");
			}else{
				if(!userBean.getCorreo().equals("")){ //entra si el correo estaba ya registrado
					context.addMessage(
							"growlEditAccount",
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Correo registrado!",
									"Por favor, intentar con otro correo electr�nico."));
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
									"No se ha podido modificar su nombre. Por favor, intente m�s tarde."));
					System.out.println("Nombre imposible modificar");
				}
			}
		}
		userBean.cleanUserBean();			
		return "editAccount";
	}
	
	public String editContrase�aAccount(){
		boolean confContrase�a = userBean.getContrase�a().equals(session.getAttribute("contrase�a"));
		boolean rs = false;
		boolean flag=false;
			
		if(confContrase�a){
			if(!userBean.getContrase�a().equals("")){
				rs = GenericDAO.genericPassUpdate("usuarios", this.nuevaContrase�a, "correo", (String)session.getAttribute("correo"));
				if(rs){
					flag=rs;
					session.setAttribute("contrase�a", this.nuevaContrase�a);
				}
				if(flag){
					FacesContext.getCurrentInstance().addMessage(
							"growlEditCont",
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Exito!",
									"Se ha modificado su contrase�a."));
				} else {
					FacesContext.getCurrentInstance().addMessage(
							"growlEditCont",
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Problema!",
									"No se ha podido modificar su contrase�a. Por favor, intente m�s tarde."));
				}
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(
					"growlEditCont",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Contrase�a incorrecta!",
							"Por favor, ingrese nuevamente su contrase�a."));
		}
		
		userBean.cleanUserBean();
		this.nuevaContrase�a=null;
		return "editAccount";
	}

	public String getNuevaContrase�a() {
		return nuevaContrase�a;
	}

	public void setNuevaContrase�a(String nuevaContrase�a) {
		this.nuevaContrase�a = nuevaContrase�a;
	}
	
}

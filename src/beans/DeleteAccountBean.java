package beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import dao.GenericDAO;

@ManagedBean(name = "deleteAccountBean")

public class DeleteAccountBean {
	
	public String deleteAccount() {
		HttpSession session = Util.getSession();
			
		Boolean rs=null;	
		rs = GenericDAO.genericStringDelete("usuarios", "correo", (String)session.getAttribute("correo"));

		if (rs){
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Exito!",
					"Se ha eliminado su cuenta."));
			context.getExternalContext().getFlash().setKeepMessages(true);
				
			session.invalidate();			

			System.out.println("Cuenta Eliminada");
			return "login";
		}else{
			FacesContext.getCurrentInstance().addMessage(
					"growlDelete",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Problema!",
							"Hubo un problema con su petición. Por favor, intente más tarde."));
			return "editAccount";
		}
	}

}

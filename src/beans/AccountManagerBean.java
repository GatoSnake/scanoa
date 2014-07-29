package beans;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import dao.GenericDAO;
import dao.UserDAO;
import util.UserBean;

@ManagedBean(name = "accountManagerBean")
@ViewScoped
public class AccountManagerBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<UserBean> users;	
	private UserBean selectedUser;
	private List<UserBean> filteredUsers;
	
	private String correoAMB;
	private String nombreAMB;
	private String contrase�aAMB;
	private int tipoUsuarioAMB;
	
    @PostConstruct
    public void init() {
    	HttpSession session = Util.getSession();
    	String correo = (String)session.getAttribute("correo");
        users = UserDAO.allUsers(correo);
    }
    
    public void clearParameters(){
		this.correoAMB=null;
		this.nombreAMB=null;
		this.contrase�aAMB=null;
		this.setTipoUsuarioAMB(0);
	}

	public void addUser(){		
		UserBean resDAO = null;
		try {
			resDAO = UserDAO.createUser(this.tipoUsuarioAMB,this.nombreAMB,this.correoAMB, this.contrase�aAMB);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	if (resDAO.isValid()) {
			System.out.println("Cuenta Registrada");
			FacesContext.getCurrentInstance().addMessage(
    				null,
    				new FacesMessage(FacesMessage.SEVERITY_INFO,
    						"�xito!",
    						"Usuario \""+this.nombreAMB+"\" agregado."));
    	} else {
    		System.out.println("Correo electr�nico ya usado");
    		FacesContext.getCurrentInstance().addMessage(
    				null,
    				new FacesMessage(FacesMessage.SEVERITY_WARN,
    						"Correo ya registrado!",
    						"Por favor, intentar con otro correo electr�nico."));
    	}
    	clearParameters();
		init();
	}
	
	public void editUser() {
			ArrayList<Boolean> rs = new ArrayList<Boolean>();
			rs.add(false);
			rs.add(false);
			rs.add(false);
			rs.add(false);		
			if(!this.nombreAMB.equals("")){
				rs.add(1, GenericDAO.genericStringUpdate("usuarios", "nombre", this.nombreAMB, "correo", selectedUser.getCorreo()));
			}
			if(!this.contrase�aAMB.equals("")){
				rs.add(2, GenericDAO.genericPassUpdate("usuarios", this.contrase�aAMB, "correo", selectedUser.getCorreo()));
			}
			if(this.tipoUsuarioAMB!=0){
				rs.add(3, GenericDAO.genericIntegerUpdate2("usuarios", "tipo_usuario", this.tipoUsuarioAMB, "correo", selectedUser.getCorreo()));
			}
			if(!this.correoAMB.equals("")){
				rs.add(0, GenericDAO.genericStringUpdate("usuarios", "correo", this.correoAMB, "correo", selectedUser.getCorreo()));
			}
			if(this.correoAMB.equals("") && this.nombreAMB.equals("") && this.contrase�aAMB.equals("") && this.tipoUsuarioAMB==0){
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					"Atenci�n!",
    					"Debe agregar valores para modificar un usuario.");
    			FacesContext.getCurrentInstance().addMessage(null, message);
			}else{
				if(rs.get(0)||rs.get(1)||rs.get(2)||rs.get(3)){
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
							"�xito!",
	    					"Usuario \""+selectedUser.getNombre()+"\" modificado.");
	    			FacesContext.getCurrentInstance().addMessage(null, message);
				}else{
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
							"Error!",
	    					"Puede que algunos de los datos no se haya podido modificar. Por favor, intente m�s tarde.");
	    			FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
		clearParameters();
		init();
	}
	
	public void deleteUser(){
		Boolean rs=null;			
		rs = GenericDAO.genericStringDelete("usuarios", "correo", selectedUser.getCorreo());
		if (rs){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"�xito!",
							"Usuario \""+selectedUser.getNombre()+"\" eliminado."));
			System.out.println("Cuenta Eliminada");
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Problema!",
							"Hubo un problema con su petici�n. Por favor, intente m�s tarde."));
		}
		init();
	}
     
    public List<UserBean> getUsers() {
        return users;
    }
    
    public UserBean getSelectedUser() {
        return selectedUser;
    }
 
    public void setSelectedUser(UserBean selectedUser) {
        this.selectedUser = selectedUser;
    }

	public UserBean getSearchUsers() {
        return selectedUser;
    }

	public String getCorreoAMB() {
		return correoAMB;
	}

	public void setCorreoAMB(String correoAMB) {
		this.correoAMB = correoAMB;
	}

	public String getNombreAMB() {
		return nombreAMB;
	}

	public void setNombreAMB(String nombreAMB) {
		this.nombreAMB = nombreAMB;
	}

	public String getContrase�aAMB() {
		return contrase�aAMB;
	}

	public void setContrase�aAMB(String contrase�aAMB) {
		this.contrase�aAMB = contrase�aAMB;
	}
	
	public List<UserBean> getFilteredUsers() {
		return filteredUsers;
	}

	public void setFilteredUsers(List<UserBean> filteredUsers) {
		this.filteredUsers = filteredUsers;
	}

	public int getTipoUsuarioAMB() {
		return tipoUsuarioAMB;
	}

	public void setTipoUsuarioAMB(int tipoUsuarioAMB) {
		this.tipoUsuarioAMB = tipoUsuarioAMB;
	}
	
}

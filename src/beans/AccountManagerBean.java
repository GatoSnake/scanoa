package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.SelectEvent;

import util.UserBean;
import dao.GenericDAO;
import dao.UserDAO;

@ManagedBean(name = "accountManagerBean")
@ViewScoped
public class AccountManagerBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private HttpSession session = Util.getSession();
	private List<UserBean> users;	
	private UserBean selectedUser;
	private List<UserBean> filteredUsers;
	
	private String correoAMB;
	private String nombreAMB;
	private String contraseñaAMB;
	private int tipoAMB;
	private int estadoExito;
	private int estadoError;
	private ArrayList<Boolean> mensajesExito = new ArrayList<Boolean>();
	private ArrayList<Boolean> mensajesError = new ArrayList<Boolean>();
	private Boolean buttonDisable = true;
		
    @PostConstruct
    public void init() {
    	String correo = (String) session.getAttribute("correo");
        users = UserDAO.allUsersWithoutCurrent(correo);
    }

    public void clearParameters(){
		this.correoAMB = null;
		this.nombreAMB = null;
		this.contraseñaAMB = null;
		this.tipoAMB = 0;
	}
    
    public void restartMessages(){
		this.mensajesExito.clear();
		this.mensajesError.clear();
		for(int i=0; i<6; i++){
			this.mensajesExito.add(false);
		}
		for(int i=0; i<6; i++){
			this.mensajesError.add(false);
		}
		this.estadoExito = 0;
		this.estadoError = 0;
    }

	public void addUser(){
		restartMessages();
		UserBean resDAO = UserDAO.createUser(this.tipoAMB, this.nombreAMB, this.correoAMB, this.contraseñaAMB, Util.name_img_default);
    	if(resDAO.isValid()) {
    		this.estadoExito = 1;
    		this.mensajesExito.set(0, true);
    	}else{
    		this.estadoError = 1;
    		this.mensajesError.set(0, true);
    	}
    	clearParameters();
		init();
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public void editUser() {
		restartMessages();
		Boolean rs1 = null;
		Boolean rs2 = null;
		Boolean rs3 = null;
		Boolean rs4 = null;
		Boolean existCorreo = null;
		this.correoAMB = this.correoAMB.toLowerCase();
		if(!this.correoAMB.equals(selectedUser.getCorreo())){
			existCorreo = GenericDAO.existValueInTable("usuarios", "correo", this.correoAMB);
			if(!existCorreo){
				rs1 = GenericDAO.stringUpdateWhereString("usuarios", "correo", this.correoAMB, "correo", selectedUser.getCorreo());
				if(rs1)	selectedUser.setCorreo(this.correoAMB);
			}
		}
		if(!this.nombreAMB.equals(selectedUser.getNombre())){
			rs2 = GenericDAO.stringUpdateWhereString("usuarios", "nombre", this.nombreAMB, "correo", selectedUser.getCorreo());
			if(rs2) selectedUser.setNombre(this.nombreAMB);
		}
		if(!this.contraseñaAMB.equals(selectedUser.getContraseña())){
			rs3 = GenericDAO.passwordUpdateWhereString("usuarios", "contrasena", this.contraseñaAMB, "correo", selectedUser.getCorreo());
			if(rs3) selectedUser.setContraseña(this.contraseñaAMB);
		}
		if(this.tipoAMB != selectedUser.getTipo() && this.tipoAMB != 0){
			rs4 = GenericDAO.integerUpdateWhereString("usuarios", "tipo_usuario", this.tipoAMB, "correo", selectedUser.getCorreo());
			if(rs4) selectedUser.setTipo(this.tipoAMB);
		}
		if(rs1!=null || rs2!=null || rs3!=null || rs4!=null || existCorreo!=null){
			if(existCorreo!=null && !existCorreo){
				if(rs1!=null && rs1){
					this.estadoExito = 1;
					this.mensajesExito.set(1, true);
				}else if(rs1!=null && !rs1){
					this.estadoError = 1;
					this.mensajesError.set(1, true);
				}
			}else if(existCorreo!=null && existCorreo){
				this.estadoError = 1;
				this.mensajesError.set(0, true);
			}
			if(rs2!=null && rs2){
				this.estadoExito = 1;
				this.mensajesExito.set(2, true);
			}else if(rs2!=null && !rs2){
				this.estadoError = 1;
				this.mensajesError.set(2, true);
			}
			if(rs3!=null && rs3){
				this.estadoExito = 1;
				this.mensajesExito.set(3, true);
			}else if(rs3!=null && !rs3){
				this.estadoError = 1;
				this.mensajesError.set(3, true);
			}
			if(rs4!=null && rs4){
				this.estadoExito = 1;
				this.mensajesExito.set(4, true);
			}else if(rs4!=null && !rs4){
				this.estadoError = 1;
				this.mensajesError.set(4, true);
			}
		}else{
			this.estadoExito = 0;
			this.estadoError = 0;
		}
		clearParameters();
		init();
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public void deleteUser(){
		restartMessages();
		Boolean rs = null;			
		rs = GenericDAO.deleteWhereString("usuarios", "correo", selectedUser.getCorreo());
		if(rs){
			String sesPathImg = selectedUser.getPath_img();
			if(!sesPathImg.contains(Util.name_img_default)){
	        	Util.deleteFile(Util.basePath_img_users+sesPathImg.substring(sesPathImg.lastIndexOf("/") + 1, sesPathImg.length()));
	        }
			this.estadoExito = 1;
			this.mensajesExito.set(5, true);
			this.buttonDisable = true;
		}else{
			this.estadoError = 1;
			this.mensajesError.set(5, true);
		}
		init();
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public void setValuesAdd(){
		this.nombreAMB = null;
		this.correoAMB = null;
		this.contraseñaAMB = null;
		this.tipoAMB = 0;
	}
	
	public void setValuesEdit(){
		this.nombreAMB = selectedUser.getNombre();
		this.correoAMB = selectedUser.getCorreo();
		this.contraseñaAMB = selectedUser.getContraseña();
		this.tipoAMB = selectedUser.getTipo();
	}
	
	public void onRowSelect(SelectEvent event) {
		this.buttonDisable = false;
    }

	public List<UserBean> getUsers() {
		return users;
	}

	public void setUsers(List<UserBean> users) {
		this.users = users;
	}

	public UserBean getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(UserBean selectedUser) {
		this.selectedUser = selectedUser;
	}

	public List<UserBean> getFilteredUsers() {
		return filteredUsers;
	}

	public void setFilteredUsers(List<UserBean> filteredUsers) {
		this.filteredUsers = filteredUsers;
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

	public String getContraseñaAMB() {
		return contraseñaAMB;
	}

	public void setContraseñaAMB(String contraseñaAMB) {
		this.contraseñaAMB = contraseñaAMB;
	}

	public int getTipoAMB() {
		return tipoAMB;
	}

	public void setTipoAMB(int tipoAMB) {
		this.tipoAMB = tipoAMB;
	}

	public int getEstadoExito() {
		return estadoExito;
	}

	public void setEstadoExito(int estadoExito) {
		this.estadoExito = estadoExito;
	}

	public int getEstadoError() {
		return estadoError;
	}

	public void setEstadoError(int estadoError) {
		this.estadoError = estadoError;
	}

	public ArrayList<Boolean> getMensajesExito() {
		return mensajesExito;
	}

	public void setMensajesExito(ArrayList<Boolean> mensajesExito) {
		this.mensajesExito = mensajesExito;
	}

	public ArrayList<Boolean> getMensajesError() {
		return mensajesError;
	}

	public void setMensajesError(ArrayList<Boolean> mensajesError) {
		this.mensajesError = mensajesError;
	}

	public Boolean getButtonDisable() {
		return buttonDisable;
	}

	public void setButtonDisable(Boolean buttonDisable) {
		this.buttonDisable = buttonDisable;
	}

}

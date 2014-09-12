package util;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String correo;
	private String nombre;
	private String contraseña;
	private int tipo;
	private String fecha_registro;
	private String path_img;
	private boolean isValid;

	public void cleanUserBean(){
		this.id = 0;
		this.correo = null;
		this.nombre = null;
		this.contraseña = null;
		this.tipo = 0;
		this.fecha_registro = null;
		this.path_img = null;
		this.isValid = false;
	}	
	 
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getContraseña() {
		return contraseña;
	}
	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public String getFecha_registro() {
		return fecha_registro;
	}	
	public void setFecha_registro(String string) {
		this.fecha_registro = string;
	}
	public boolean isValid() {
		return isValid;
	}	
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}	
	public String getPath_img() {
		return path_img;
	}	
	public void setPath_img(String path_img) {
		this.path_img = path_img;
	}	
	public int getId() {
		return id;
	}	
	public void setId(int id) {
		this.id = id;
	}	
}

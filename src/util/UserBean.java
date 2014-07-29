package util;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
@ManagedBean(name = "userBean")
@SessionScoped

public class UserBean implements Serializable {
	 private static final long serialVersionUID = 1L;
	 private String correo;
	 private String nombre;
	 private String contrase�a;
	 private int tipo;
	 private String fecha_registro;
	 private boolean isValid;
	 
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
	public String getContrase�a() {
		return contrase�a;
	}
	public void setContrase�a(String contrase�a) {
		this.contrase�a = contrase�a;
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

	public void cleanUserBean(){
		this.correo=null;
		this.nombre=null;
		this.contrase�a=null;
		this.tipo=0;
		this.fecha_registro=null;
		this.isValid=false;
	}
	
}

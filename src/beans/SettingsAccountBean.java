package beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import dao.GenericDAO;

@ManagedBean(name = "settingsAccountBean")
@ViewScoped
public class SettingsAccountBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private HttpSession session = Util.getSession();
	private String nombreEdit = (String) session.getAttribute("nombre");
	private String correoEdit = (String) session.getAttribute("correo");
	private String pathImg;
	private Part part;
	private String contraseñaActual;
	private String contraseñaEdit;
	private String contraseñaConf;
	private String mensaje;
	private int estado;
	
	public void editAccount() throws IOException, InterruptedException {
		System.out.println("En metodo editAccount");
		Boolean rs1 = null;
		Boolean rs2 = null;
		Boolean rs3 = null;
		Boolean existCorreo = null;
		this.correoEdit = this.correoEdit.toLowerCase();
		//Modificacion de correo
		if(!this.correoEdit.equals(this.session.getAttribute("correo"))){
			existCorreo = GenericDAO.existValueInTable("usuarios", "correo", this.correoEdit);
			if(!existCorreo){
				rs1 = GenericDAO.stringUpdateWhereString("usuarios", "correo", this.correoEdit, "correo", (String) this.session.getAttribute("correo"));
				if(rs1){
					this.session.setAttribute("correo", this.correoEdit);
				}
			}
			this.correoEdit = (String) session.getAttribute("correo");
		}
		//Modificacion de nombre
		if(!this.nombreEdit.equals(this.session.getAttribute("nombre"))){
			rs2 = GenericDAO.stringUpdateWhereString("usuarios", "nombre", this.nombreEdit, "correo", (String) this.session.getAttribute("correo"));
			if(rs2){
				this.session.setAttribute("nombre", this.nombreEdit);
			}
			this.nombreEdit = (String) session.getAttribute("nombre");
		}
		//Modificacion de avatar
		if(this.part != null){
			String sesPathImg = (String) session.getAttribute("path_img");
	        String nameFile = "img_"+session.getAttribute("id")+"_"+Util.getRandomHexString(32)+Util.extension_img;
	        if(!sesPathImg.contains(Util.name_img_default)){
	        	Util.deleteFile(Util.basePath_img_users+sesPathImg.substring(sesPathImg.lastIndexOf("/") + 1, sesPathImg.length()));
	        }
	        File outputFilePath = new File(Util.basePath_img_users+nameFile);
	        InputStream inputStream = null;
	        OutputStream outputStream = null;	        
	        try {
	            inputStream = this.part.getInputStream();
	            outputStream = new FileOutputStream(outputFilePath);
	            int read = 0;
	            final byte[] bytes = new byte[1024];
	            while ((read = inputStream.read(bytes)) != -1) {
	                outputStream.write(bytes, 0, read);
	            }
	            rs3 = GenericDAO.stringUpdateWhereString("usuarios", "path_img", nameFile, "correo", (String) session.getAttribute("correo"));
	            if(rs3){
	            	session.setAttribute("path_img", Util.dir_img_users+nameFile);
	            }
	        } catch (IOException ex) {
	        	System.out.println("Error en editAccount() -->" + ex.getMessage());
	        } finally {
	            if (outputStream != null) {
	                outputStream.close();
	            }
	            if (inputStream != null) {
	                inputStream.close();
	            }
	        }
//	        Thread.sleep(3000);	        
	        this.part = null;
		}
		//si todos son nulos entonces el usuario no ingreso nada
		if(rs1!=null || rs2!=null || rs3!=null || existCorreo!=null){
//			System.out.println("lol1");
//			System.out.println(existCorreo);
			if((rs1!=null&&rs1!=false) || (rs2!=null&&rs2!=false) || (rs3!=null&&rs3!=false) || (existCorreo!=null&&existCorreo!=true)){
				this.mensaje = "Se han guardado los cambios satisfactoriamente";
				this.estado = 1;
			}else{
				this.mensaje = "Se ha producido un error al guardar uno de los valores";
				this.estado = 2;
				if(existCorreo!=null&&existCorreo==true){
					this.mensaje = this.mensaje+", el correo electrónico ya se ha utilizado";
				}
			}
		}
	}
	
	public void editContraseña() {
		Boolean rs1 = null;
		Boolean actual = null;
		if(this.contraseñaActual.equals(this.session.getAttribute("contraseña"))){
			actual = true;
			if(!this.contraseñaEdit.equals(this.session.getAttribute("contraseña"))){
				rs1 = GenericDAO.passwordUpdateWhereString("usuarios", "contrasena", this.contraseñaEdit, "correo", (String) this.session.getAttribute("correo"));
				if(rs1){
					this.session.setAttribute("contraseña", this.contraseñaEdit);
				}
			}
		}else actual = false;
		if(actual){
			if(rs1!=null){
				if(rs1!=null && rs1){
					this.mensaje = "Se han guardado los cambios satisfactoriamente";
					this.estado = 1;
				}else{
					this.mensaje = "Se ha producido un error en uno de los valores";
					this.estado = 2;
				}
			}else{
				this.estado = 0;
			}
		}else{
			this.mensaje = "La contraseña no es igual a la actual";
			this.estado = 2;
		}
		this.contraseñaActual = null;
		this.contraseñaEdit = null;
	}
	
	public String deleteAccount() {
		Boolean rs1 = null;
		if(this.contraseñaConf.equals(this.session.getAttribute("contraseña"))){
			rs1 = GenericDAO.deleteWhereString("usuarios", "correo", (String) this.session.getAttribute("correo"));
			this.contraseñaConf = null;
			if(rs1){
				String sesPathImg = (String) session.getAttribute("path_img");
				if(!sesPathImg.contains(Util.name_img_default)){
		        	Util.deleteFile(Util.basePath_img_users+sesPathImg.substring(sesPathImg.lastIndexOf("/") + 1, sesPathImg.length()));
		        }
				session.invalidate();
				return "index";
			}else{
				this.mensaje = "Se ha producido un error al intentar eliminar la cuenta";
				this.estado = 2;
				return null;
			}
		}else{
			this.mensaje = "La contraseña no es correcta";
			this.estado = 2;
			return null;
		}
	}

	public String getNombreEdit() {
		return nombreEdit;
	}

	public void setNombreEdit(String nombreEdit) {
		this.nombreEdit = nombreEdit;
	}

	public String getCorreoEdit() {
		return correoEdit;
	}

	public void setCorreoEdit(String correoEdit) {
		this.correoEdit = correoEdit;
	}

	public String getPathImg() {
		return pathImg;
	}

	public void setPathImg(String pathImg) {
		this.pathImg = pathImg;
	}

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public String getContraseñaActual() {
		return contraseñaActual;
	}

	public void setContraseñaActual(String contraseñaActual) {
		this.contraseñaActual = contraseñaActual;
	}

	public String getContraseñaEdit() {
		return contraseñaEdit;
	}

	public void setContraseñaEdit(String contraseñaEdit) {
		this.contraseñaEdit = contraseñaEdit;
	}

	public String getContraseñaConf() {
		return contraseñaConf;
	}

	public void setContraseñaConf(String contraseñaConf) {
		this.contraseñaConf = contraseñaConf;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}
	
}

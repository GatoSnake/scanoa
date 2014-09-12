package beans;

import java.io.Serializable;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import dao.GenericDAO;

@ManagedBean(name = "recoveryAccountBean")
@ViewScoped
public class RecoveryAccountBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int showRecovery;
	private String code;
	private String correoRAB;
	private String newPass;
	private Integer mensaje;

	public void init(ComponentSystemEvent event){
		String code = (String) event.getComponent().getAttributes().get("code");
		String email = (String) event.getComponent().getAttributes().get("email");
		if(code!=null && email!=null){
			String resCode = GenericDAO.selectStringWhereString("recovery_random", "usuarios", "correo", this.correoRAB);
			if(resCode!=null && resCode.equals(code)){
				this.showRecovery = 1;
			}else{
				this.showRecovery = 0;
			}
		}else{
			this.showRecovery = 0;
		}
	}
	
	public void sendMail() {
		this.correoRAB = this.correoRAB.toLowerCase();
		Boolean exist = GenericDAO.existValueInTable("usuarios", "correo", this.correoRAB);
		if(exist){
			String randomHex = Util.getRandomHexString(128);
			GenericDAO.stringUpdateWhereString("usuarios", "recovery_random", randomHex, "correo", this.correoRAB);

			String to = this.correoRAB;
			String from = Util.username;
			
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			
			// Get the Session object.
			Session session = Session.getInstance(props,new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(Util.username, Util.password);
				}
			});
			
			try {
				// Create a default MimeMessage object.
				Message message = new MimeMessage(session);
				
				// Set From: header field of the header.
				message.setFrom(new InternetAddress(from));
				
				// Set To: header field of the header.
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
				
				// Set Subject: header field
				message.setSubject("Recuperación de Cuenta - SCANOA");
				
				// Send the actual HTML message, as big as you like
		        message.setContent("<h2>Recuperación de cuenta de SCANOA</h2>"
		        					+"<br/>"
		        					+"<p>Se ha solicitado la recuperación de la contraseña del correo "+this.correoRAB+". "
		        					+"Si usted no ha solicitado recuperar su cuenta, ignore o elimine este mensaje. En el caso contrario, ingrese al siguiente link: </p>"
		        					+"<a href='"+Util.url_project+"recoveryAccount.xhtml?code="+randomHex+"&email="+this.correoRAB+"'>"+Util.url_project+"recoveryAccount.xhtml</a>."
		        					,"text/html" );
				
				// Send message
				Transport.send(message);
				
				System.out.println("Correo enviado");
				
			} catch (MessagingException ex) {
				System.out.println("Error en sendMail() -->" + ex);
//				throw new RuntimeException(e);
			}
			this.mensaje = 2;
		}else{
			this.mensaje = 1;
		}
		this.correoRAB = null;
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public String restablecerPass(){
		GenericDAO.passwordUpdateWhereString("usuarios", "contrasena", this.newPass, "correo", this.correoRAB);
		GenericDAO.NULLUpdateWhereString("usuarios", "recovery_random", "correo", this.correoRAB);
		Integer error = 1;
		return "login?faces-redirect=true&error="+error;
	}

	public int getShowRecovery() {
		return showRecovery;
	}

	public void setShowRecovery(int showRecovery) {
		this.showRecovery = showRecovery;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCorreoRAB() {
		return correoRAB;
	}

	public void setCorreoRAB(String correoRAB) {
		this.correoRAB = correoRAB;
	}

	public String getNewPass() {
		return newPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}

	public Integer getMensaje() {
		return mensaje;
	}

	public void setMensaje(Integer mensaje) {
		this.mensaje = mensaje;
	}
	
}

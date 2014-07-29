package beans;

import java.io.Serializable;
import java.util.Properties;
import java.util.Random;

import javax.faces.application.FacesMessage;
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
import dao.UserDAO;

@ManagedBean(name = "recoveryAccountBean")
@ViewScoped
public class RecoveryAccountBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private int showRecovery;
	private String code;
	private String correoRAB;
	private String newPass;

	public void init(ComponentSystemEvent event){
		String code = (String) event.getComponent().getAttributes().get("code");
		String email = (String) event.getComponent().getAttributes().get("email");
		if(code!=null && email!=null){
			String resCode = GenericDAO.genericSelectSpecificValue("usuarios", "recovery_random", "correo", this.correoRAB);
			if(resCode!=null && resCode.equals(code)){
				this.showRecovery = 1;
			}else{
				this.showRecovery = 0;
			}
		}else{
			this.showRecovery = 0;
		}
	}
	
	public String sendMail() {
		this.correoRAB = this.correoRAB.toLowerCase();
		boolean exist = UserDAO.existUser(this.correoRAB);
		if(exist){
//			String existValue = GenericDAO.genericSelectSpecificValue("usuarios", "recovery_random", "correo", this.correoRAB);
//			if(existValue == null ){

				String randomHex = getRandomHexString(128);
				GenericDAO.genericStringUpdate("usuarios", "recovery_random", randomHex, "correo", this.correoRAB);

				// Recipient's email ID needs to be mentioned.
				String to = this.correoRAB;

				// Sender's email ID needs to be mentioned
				String from = "noreply.scanoa";
				final String username = "noreply.scanoa";//change accordingly
				final String password = "adminscanoanoreply123";//change accordingly
				
				// Assuming you are sending email through relay.jangosmtp.net
				String host = "smtp.gmail.com";
				
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", host);
				props.put("mail.smtp.port", "25");
				
				// Get the Session object.
				Session session = Session.getInstance(props,new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
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
		        message.setContent("<h2>Recuperación de cuenta del sistema de categorización automática de OAs</h2>"
		        					+"<br/>"
		        					+"<p>Se ha solicitado la recuperación de la contraseña del correo "+this.correoRAB+". "
		        					+"Si usted no ha solicitado recuperar su cuenta, elimine este mensaje. En el caso contrario, ingrese al siguiente link: </p>"
		        					+"<a href='http://localhost:8080/scanoa/recoveryAccount.xhtml?code="+randomHex+"&email="+this.correoRAB+"'>http://localhost:8080/scanoa/recoveryAccount.xhtml</a>."
		        					,"text/html" );
				
				// Send message
				Transport.send(message);
				
				System.out.println("Correo enviado");
				
				} catch (MessagingException e) {
				throw new RuntimeException(e);
				}
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Exito!",
						"Se ha enviado el mail de recuperación de su cuenta."));
				context.getExternalContext().getFlash().setKeepMessages(true);
				this.correoRAB=null;
				return "login";
//			}else{
//				FacesContext.getCurrentInstance().addMessage(
//	    				null,
//	    				new FacesMessage(FacesMessage.SEVERITY_WARN,
//	    						"Correo ya enviado",
//	    						"Su correo de recuperación de cuenta ya ha sido enviado."));
//			}
		}else{
			FacesContext.getCurrentInstance().addMessage(
    				null,
    				new FacesMessage(FacesMessage.SEVERITY_WARN,
    						"Correo no registrado",
    						"Por favor, intentar con otro correo electrónico."));			
			this.correoRAB=null;
			return "recoveryAccount";
		}
	}
	
	public String restablecerPass(){
		GenericDAO.genericPassUpdate("usuarios", this.newPass, "correo", this.correoRAB);
		GenericDAO.genericStringUpdate("usuarios", "recovery_random", "NULL", "correo", this.correoRAB);
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Exito!",
				"Se ha restablecido su contraseña."));
		context.getExternalContext().getFlash().setKeepMessages(true);
		
		return "login";
	}
	
	public String getRandomHexString (int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }
	
	public String getCorreoRAB() {
		return correoRAB;
	}

	public void setCorreoRAB(String correoRAB) {
		this.correoRAB = correoRAB;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getShowRecovery() {
		return showRecovery;
	}

	public void setShowRecovery(int showRecovery) {
		this.showRecovery = showRecovery;
	}

	public String getNewPass() {
		return newPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}
	
}

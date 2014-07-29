package validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("urlValidator")
public class URLValidator implements Validator {
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		String urlValue = value.toString();
		
		if(urlValue.length()<7){
			FacesMessage msg = 
					new FacesMessage("Tama�o m�nimo", 
							"La URL debe ser de m�nimo 7 car�cteres");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		else{
			if(!urlValue.startsWith("http://", 0) && !urlValue.startsWith("https://", 0)){
				FacesMessage msg = 
						new FacesMessage("Formato de identificador URL incorrecto", 
								"Debe ser una URL con formato http");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}else if(!urlValue.endsWith(".pdf") && !urlValue.endsWith(".doc") && !urlValue.endsWith(".docx") ){
				FacesMessage msg = 
						new FacesMessage("Formato de identificador URL incorrecto", 
								"Debe ser un .pdf, .doc o .docx");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}		
	}

}

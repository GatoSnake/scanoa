package validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("inputValidator")
public class InputValidator implements Validator {
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String input = (String) value;
		input = input.replaceAll("\\s+", "");
//		System.out.println("INPUT: "+input);
		
		if(input == null || input.isEmpty()){
			FacesMessage msg = 
				new FacesMessage("Error!", 
						"No puede ingresar un valor vacío");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
 
		}
		
	}

}

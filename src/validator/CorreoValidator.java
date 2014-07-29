package validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("correoValidator")
public class CorreoValidator implements Validator {
	
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\." +
			"[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*" +
			"(\\.[A-Za-z]{2,})$";

	private Pattern pattern;
	private Matcher matcher;
 
	public CorreoValidator(){
		  pattern = Pattern.compile(EMAIL_PATTERN);
	}
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		matcher = pattern.matcher(value.toString());
		if(!matcher.matches()){
 
			FacesMessage msg = 
				new FacesMessage("Formato de correo electrónico inválido!", 
						"Ingrese una dirección de correo electrónico válido");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
 
		}
		
	}

}

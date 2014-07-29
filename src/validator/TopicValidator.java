package validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("topicValidator")
public class TopicValidator implements Validator {
	
	private static final String EMAIL_PATTERN = "^[A-Za-z0-9]*";

	private Pattern pattern;
	private Matcher matcher;
 
	public TopicValidator(){
		  pattern = Pattern.compile(EMAIL_PATTERN);
	}
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		matcher = pattern.matcher(value.toString());
		if(!matcher.matches()){
			FacesMessage msg = 
				new FacesMessage("Formato de identificador uri incorrecto.", 
						"Ingrese un valor alfanumérico, sin tildes y sin espacios.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
 
		}
		
	}

}

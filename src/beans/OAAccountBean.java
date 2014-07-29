package beans;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import ontology.ConsultasRead;
import ontology.ConsultasWrite;
import dao.GenericDAO;
import dao.OADAO;
import util.OABean;
import util.TopicBean;

@ManagedBean(name="oaAccountBean")
@ViewScoped
public class OAAccountBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<OABean> oas;
	private List<OABean> selectedOAs;
	private OABean selectedOA;
	
	private String urlOAMB;
	private String tituloOAMB;
	private String descripcionOAMB;
 
	@ManagedProperty("#{oaManagerBean}")
	private OAManagerBean OAMB;
	
    @PostConstruct
    public void init() {
    	HttpSession session = Util.getSession();
    	String correo = (String)session.getAttribute("correo");
    	oas = OADAO.getOAsUser(correo);
    	for(OABean oa : oas){
    		oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
    	}
    	System.out.println("Busqueda de los oas del usuario");
    }
    
    public void clearParameters(){
    	this.urlOAMB=null;
    	this.tituloOAMB=null;
    	this.descripcionOAMB=null;
    }

    public void addOA() throws ParseException, IOException{
    	boolean resultCat = false;
    	HttpSession session = Util.getSession();
    	String correo = (String)session.getAttribute("correo");
    	//insercion del oa a la bd
    	OADAO.insertOA(this.urlOAMB, this.tituloOAMB, this.descripcionOAMB, correo);
    	String id = OADAO.getIdActualOA(correo, this.urlOAMB);
    	//proceso de categorizacion
    	resultCat = OAMB.processCategorization(this.urlOAMB,id,null,1);
    	//mensajes growl
		if(resultCat){
    		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
					"Éxito!",
					"Se ha agregado el nuevo objeto de aprendizaje.");
			FacesContext.getCurrentInstance().addMessage(null, message);
    	}else{
    		int idDelete = Integer.parseInt(id);
    		GenericDAO.genericIntegerDelete("learning_objects", "id", idDelete);
    		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
					"Error!",
					"Se ha producido un error en el ingreso. Por favor, intente más tarde.");
			FacesContext.getCurrentInstance().addMessage(null, message);
    	}
		clearParameters();
    	init();
    }
    
    public void editOA() throws IOException {
    	if(selectedOA != null){
			HttpSession session = Util.getSession();
	    	String correo = (String)session.getAttribute("correo");  				
			ArrayList<Boolean> rs = new ArrayList<Boolean>();
			rs.add(false);
			rs.add(false);
			rs.add(false);
			if(!this.urlOAMB.equals("")){
				rs.add(0, GenericDAO.genericIntegerUpdate("learning_objects", "url", this.urlOAMB, "id", selectedOA.getId()));
				String id = OADAO.getIdActualOA(correo, this.urlOAMB);
				OAMB.processCategorization(this.urlOAMB,id,selectedOA,2);
			}
			if(!this.tituloOAMB.equals("")){
				rs.add(1, GenericDAO.genericIntegerUpdate("learning_objects", "titulo", this.tituloOAMB, "id", selectedOA.getId()));
			}
			if(!this.descripcionOAMB.equals("")){
				rs.add(2, GenericDAO.genericIntegerUpdate("learning_objects", "descripcion", this.descripcionOAMB, "id", selectedOA.getId()));
			}
			if(this.urlOAMB.equals("") && this.tituloOAMB.equals("") && this.descripcionOAMB.equals("")){
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					"Atención!",
    					"Debe agregar valores para modificar un objeto de aprendizaje.");
    			FacesContext.getCurrentInstance().addMessage(null, message);
			}else{
				if(rs.get(0)||rs.get(1)||rs.get(2)){
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
							"Éxito!",
	    					"Se ha modificado el objeto de aprendizaje.");
	    			FacesContext.getCurrentInstance().addMessage(null, message);
				}else{
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
							"Error!",
	    					"Puede que algunos de los datos no se haya podido modificar. Por favor, intente más tarde.");
	    			FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
    	}else{
    		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
					"Atención!",
					"Debe seleccionar un objeto de aprendizaje a editar.");
			FacesContext.getCurrentInstance().addMessage(null, message);
    	}
    	clearParameters();
    	init();
    }
    
    public void deleteOA() {
    	if(selectedOA != null){
    		List<Boolean> flags = new ArrayList<Boolean>();
//    		for(OABean oa : oas){
    			flags.add(GenericDAO.genericIntegerDelete("learning_objects", "id", selectedOA.getId()));
    			for(TopicBean topic : selectedOA.getTopicos()){
    				String value = "'"+topic.getTopico()+"OA"+selectedOA.getId()+"'";
    				ConsultasWrite.deleteProperty("ContCompTI:topicIsReferencedBy",topic.getTopico(),value);
    			}
//    		}
    		if (flags.contains(false)) {
    			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					"Error!",
    					"Puede que algunos de los datos no se haya podido eliminar.");
    			FacesContext.getCurrentInstance().addMessage(null, message);
    		}else{
    			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
    					"Éxito!",
    					"Se han eliminado los objetos de aprendizaje seleccionados.");
    			FacesContext.getCurrentInstance().addMessage(null, message);
    		}
    	}else{
    		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
					"Atención!",
					"Debe seleccionar al menos un objeto de aprendizaje a eliminar.");
			FacesContext.getCurrentInstance().addMessage(null, message);
    	}
    	clearParameters();
    	init();
    }
    
	public List<OABean> getOas() {
		return oas;
	}

	public void setOas(List<OABean> oas) {
		this.oas = oas;
	}

	public String getUrlOAMB() {
		return urlOAMB;
	}

	public void setUrlOAMB(String urlOAMB) {
		this.urlOAMB = urlOAMB;
	}

	public String getTituloOAMB() {
		return tituloOAMB;
	}

	public void setTituloOAMB(String tituloOAMB) {
		this.tituloOAMB = tituloOAMB;
	}

	public String getDescripcionOAMB() {
		return descripcionOAMB;
	}

	public void setDescripcionOAMB(String descripcionOAMB) {
		this.descripcionOAMB = descripcionOAMB;
	}

	public List<OABean> getSelectedOAs() {
		return selectedOAs;
	}

	public void setSelectedOAs(List<OABean> selectedOAs) {
		this.selectedOAs = selectedOAs;
	}

	public OABean getSelectedOA() {
		return selectedOA;
	}

	public void setSelectedOA(OABean selectedOA) {
		this.selectedOA = selectedOA;
	}

	public OAManagerBean getOAMB() {
		return OAMB;
	}

	public void setOAMB(OAManagerBean oAMB) {
		OAMB = oAMB;
	}

}
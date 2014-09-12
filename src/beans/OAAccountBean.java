package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.SelectEvent;

import categorization.Categorization;
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
	private HttpSession session = Util.getSession();
	private List<OABean> oas;
	private OABean selectedOA;
	private List<OABean> filteredOAs;
	
	private String urlOAMB;
	private String tituloOAMB;
	private String descripcionOAMB;
	private int estadoExito;
	private int estadoError;
	private ArrayList<Boolean> mensajesExito = new ArrayList<Boolean>();
	private ArrayList<Boolean> mensajesError = new ArrayList<Boolean>();
	private Boolean buttonDisable;
 
	
    @PostConstruct
    public void init() {
    	System.out.println("En OAAccountBean");
    	oas = OADAO.getOAsUser(Integer.toString((Integer)session.getAttribute("id")));
    	this.buttonDisable = true;
    }
    
    public void clearParameters(){
    	this.urlOAMB = null;
    	this.tituloOAMB = null;
    	this.descripcionOAMB = null;
    }
    
    public void restartMessages(){
    	this.mensajesExito.clear();
		this.mensajesError.clear();
		for(int i=0; i<5; i++){
			this.mensajesExito.add(false);
		}
		for(int i=0; i<7; i++){
			this.mensajesError.add(false);
		}
		this.estadoExito = 0;
		this.estadoError = 0;
    }

    public void addOA() {
    	restartMessages();
    	//insercion del oa a la bd
    	Integer resultCat = null;
    	String idOA = null;
    	Boolean rs = OADAO.insertOA(this.urlOAMB, this.tituloOAMB, this.descripcionOAMB, (String)session.getAttribute("correo"), Integer.toString((Integer)session.getAttribute("id")));
    	if(rs){
    		idOA = OADAO.getIdActualOA(Integer.toString((Integer)session.getAttribute("id")), this.urlOAMB);
    		//proceso de categorizacion
    		resultCat = Categorization.processCategorization(this.urlOAMB, idOA, null, 1);
    	}
    	//1- si no se pudo descargar el OA
		//2- si no se pudo extraer el texto (esto es por posibles errores de lectura)
		//3- si el OA paso el proceso de categorizacion pero no cayo en ningun tópico
		//4- si el OA paso el proceso de categorizacion y pertenece a 1 o mas topicos
    	if(rs){
    		if(resultCat==1){
    			GenericDAO.deleteWhereInteger("learning_objects", "id", idOA);
    			this.estadoError = 1;
    			this.mensajesError.set(1, true);
    		}
    		else if(resultCat==2){
    			GenericDAO.deleteWhereInteger("learning_objects", "id", idOA);
    			this.estadoError = 1;
    			this.mensajesError.set(2, true);
    		}
    		else if(resultCat==3){
    			this.estadoExito = 1;
    			this.mensajesExito.set(1, true);
    		}
    		else if(resultCat==4){
    			this.estadoExito = 1;
    			this.mensajesExito.set(0, true);
    		}
    	}else{
    		this.estadoError = 1 ;
    		this.mensajesError.set(0, true);
    	}
    	clearParameters();
    	init();
    	FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
    }
    
    public void editOA() {
    	restartMessages();
    	Integer resultCat = null;
    	Boolean rs1 = null;
    	Boolean rs2 = null;
    	Boolean rs3 = null;
    	if(!this.urlOAMB.equals(selectedOA.getUrl())){
    		rs1 = GenericDAO.stringUpdateWhereInteger("learning_objects", "url", this.urlOAMB, "id", Integer.toString(selectedOA.getId()));
    		resultCat = Categorization.processCategorization(this.urlOAMB, Integer.toString(selectedOA.getId()), selectedOA, 2);
    	}
    	if(!this.tituloOAMB.equals(selectedOA.getTitulo())){
    		rs2 = GenericDAO.stringUpdateWhereInteger("learning_objects", "titulo", this.tituloOAMB, "id", Integer.toString(selectedOA.getId()));
    	}
    	if(!this.descripcionOAMB.equals(selectedOA.getDescripcion())){
    		rs3 = GenericDAO.stringUpdateWhereInteger("learning_objects", "descripcion", this.descripcionOAMB, "id", Integer.toString(selectedOA.getId()));
    	}
       	if(rs1!=null || rs2!=null || rs3!=null || resultCat!=null){
    		if(rs1!=null && rs1){
    			if(resultCat==1){
    				GenericDAO.stringUpdateWhereInteger("learning_objects", "url", selectedOA.getUrl(), "id", Integer.toString(selectedOA.getId()));
        			this.estadoError = 1;
        			this.mensajesError.set(1, true);
        		}
        		else if(resultCat==2){
        			GenericDAO.stringUpdateWhereInteger("learning_objects", "url", selectedOA.getUrl(), "id", Integer.toString(selectedOA.getId()));
        			this.estadoError = 1;
        			this.mensajesError.set(2, true);
        		}
        		else if(resultCat==3){
        			this.estadoExito = 1;
        			this.mensajesExito.set(1, true);
        		}
        		else if(resultCat==4){
        			this.estadoExito = 1;
        			this.mensajesExito.set(0, true);
        		}
    		}else if(rs1!=null && !rs1){
    			this.estadoError = 1;
    			this.mensajesError.set(3, true);
    		}
    		if(rs2!=null && rs2){
    			this.estadoExito = 1;
    			this.mensajesExito.set(2, true);
    		}else if(rs2!=null && !rs2){
    			this.estadoError = 1;
    			this.mensajesError.set(4, true);
    		}
    		if(rs3!=null && rs3){
    			this.estadoExito = 1;
    			this.mensajesExito.set(3, true);
    		}else if(rs3!=null && !rs3){
    			this.estadoError = 1;
    			this.mensajesError.set(5, true);
    		}
    	}else{
    		this.estadoExito = 0;
    		this.estadoError = 0;
    	}
    	
    	clearParameters();
    	init();
    	FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
    }
    
    public void deleteOA() {
    	restartMessages();
    	Boolean rs = GenericDAO.deleteWhereInteger("learning_objects", "id", Integer.toString(selectedOA.getId()));
    	if(rs){
    		selectedOA.setTopicos(ConsultasRead.getValuesByIdOA(selectedOA.getId()));
    		for(TopicBean topic : selectedOA.getTopicos()){
    			String oaref = topic.getTopico()+"OA"+selectedOA.getId();
    			ConsultasWrite.deletePropertyValueString("ContCompTI:topicIsReferencedBy", topic.getTopico(), oaref);
    		}
    	}
    	if(rs){
    		this.estadoExito = 1;
    		this.mensajesExito.set(4, true);
    	}else{
    		this.estadoError = 1;
    		this.mensajesError.set(6, true);
    	}
    	clearParameters();
    	init();
    	FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
    }
    
    public void setValuesAdd(){
    	System.out.println("Seteando valores para el addOA");
    	this.urlOAMB = null;
    	this.tituloOAMB = null;
    	this.descripcionOAMB = null;
	}
	
	public void setValuesEdit(){
		this.urlOAMB = selectedOA.getUrl();
		this.tituloOAMB = selectedOA.getTitulo();
		this.descripcionOAMB = selectedOA.getDescripcion();
	}
	
	public void onRowSelect(SelectEvent event) {
		this.buttonDisable = false;
    }

	public List<OABean> getOas() {
		return oas;
	}

	public void setOas(List<OABean> oas) {
		this.oas = oas;
	}

	public OABean getSelectedOA() {
		return selectedOA;
	}

	public void setSelectedOA(OABean selectedOA) {
		this.selectedOA = selectedOA;
	}

	public List<OABean> getFilteredOAs() {
		return filteredOAs;
	}

	public void setFilteredOAs(List<OABean> filteredOAs) {
		this.filteredOAs = filteredOAs;
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

	public int getEstadoExito() {
		return estadoExito;
	}

	public void setEstadoExito(int estadoExito) {
		this.estadoExito = estadoExito;
	}

	public int getEstadoError() {
		return estadoError;
	}

	public void setEstadoError(int estadoError) {
		this.estadoError = estadoError;
	}

	public ArrayList<Boolean> getMensajesExito() {
		return mensajesExito;
	}

	public void setMensajesExito(ArrayList<Boolean> mensajesExito) {
		this.mensajesExito = mensajesExito;
	}

	public ArrayList<Boolean> getMensajesError() {
		return mensajesError;
	}

	public void setMensajesError(ArrayList<Boolean> mensajesError) {
		this.mensajesError = mensajesError;
	}

	public Boolean getButtonDisable() {
		return buttonDisable;
	}

	public void setButtonDisable(Boolean buttonDisable) {
		this.buttonDisable = buttonDisable;
	}

}
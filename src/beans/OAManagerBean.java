package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import categorization.Categorization;
import ontology.ConsultasRead;
import ontology.ConsultasWrite;
import util.OABean;
import util.TopicBean;
import dao.GenericDAO;
import dao.OADAO;

@ManagedBean(name = "oaManagerBean")
@ViewScoped
public class OAManagerBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<OABean> oas;	
	private OABean selectedOA;
	private List<OABean> filteredOAs;
	private String inputSearchTitulo;
	private String inputSearchDescripcion;
	
	private String urlOAMB;
	private String tituloOAMB;
	private String descripcionOAMB;
	private String correoOAMB;
	private int estadoExito;
	private int estadoError;
	private ArrayList<Boolean> mensajesExito = new ArrayList<Boolean>();
	private ArrayList<Boolean> mensajesError = new ArrayList<Boolean>();
	private Boolean buttonDisable;
	
	
    @PostConstruct
    public void init() {
    	oas = OADAO.getAllOAs();
    	this.buttonDisable = true;
    }
    
    public void clearParameters(){
    	this.urlOAMB = null;
    	this.tituloOAMB = null;
    	this.descripcionOAMB = null;
    	this.correoOAMB = null;
    }    
    
	public void restartMessages(){
		this.mensajesExito.clear();
		this.mensajesError.clear();
		for(int i=0; i<6; i++){
			this.mensajesExito.add(false);
		}
		for(int i=0; i<8; i++){
			this.mensajesError.add(false);
		}
		this.estadoExito = 0;
		this.estadoError = 0;
    }
    
    public void addOA() {
    	restartMessages();
    	this.correoOAMB = this.correoOAMB.toLowerCase();    	
    	Boolean rs = null;
    	Integer resultCat = null;
    	String idOA = null;
    	Boolean existCorreo = GenericDAO.existValueInTable("usuarios", "correo", this.correoOAMB);
    	if(existCorreo){
    		String idUser = GenericDAO.selectStringWhereString("id", "usuarios", "correo", this.correoOAMB);
    		rs = OADAO.insertOA(this.urlOAMB, this.tituloOAMB, this.descripcionOAMB, this.correoOAMB, idUser);
    		idOA = OADAO.getIdActualOA(idUser, this.urlOAMB);
    		//proceso de categorizacion
    		resultCat = Categorization.processCategorization(this.urlOAMB, idOA, null, 1);
    	}
    	if(existCorreo){
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
    	}else{
    		this.estadoError = 1 ;
    		this.mensajesError.set(7, true);
    	}
    	
    	clearParameters();
    	init();
    	FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public void editOA() {
		restartMessages();
		Boolean existCorreo = null;
    	Integer resultCat = null;
    	Boolean rs1 = null;
    	Boolean rs2 = null;
    	Boolean rs3 = null;
    	if(!this.correoOAMB.equals(selectedOA.getUsuario())){
    		existCorreo = GenericDAO.existValueInTable("usuarios", "correo", this.correoOAMB);
    		if(existCorreo){
        		String idUser = GenericDAO.selectStringWhereString("id", "usuarios", "correo", this.correoOAMB);
        		OADAO.editOAUser(this.correoOAMB, idUser, selectedOA.getUsuario());
    		}
    	}
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
    	
    	if(rs1!=null || rs2!=null || rs3!=null || resultCat!=null || existCorreo!=null){
    		if(existCorreo!=null && existCorreo){
    			this.estadoExito = 1;
    			this.mensajesExito.set(5, true);
    		}else if(existCorreo!=null && !existCorreo){
    			this.estadoError = 1;
    			this.mensajesError.set(7, true);
    		}
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
	
	public void deleteOA(){
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
    		this.estadoError = 2;
    		this.mensajesError.set(6, true);
    	}
    	clearParameters();
    	init();
    	FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public void setValuesAdd(){
    	this.urlOAMB = null;
    	this.tituloOAMB = null;
    	this.descripcionOAMB = null;
    	this.correoOAMB = null;
	}
	
	public void setValuesEdit(){
		this.correoOAMB = selectedOA.getUsuario();
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

	public String getInputSearchTitulo() {
		return inputSearchTitulo;
	}

	public void setInputSearchTitulo(String inputSearchTitulo) {
		this.inputSearchTitulo = inputSearchTitulo;
	}

	public String getInputSearchDescripcion() {
		return inputSearchDescripcion;
	}

	public void setInputSearchDescripcion(String inputSearchDescripcion) {
		this.inputSearchDescripcion = inputSearchDescripcion;
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

	public String getCorreoOAMB() {
		return correoOAMB;
	}

	public void setCorreoOAMB(String correoOAMB) {
		this.correoOAMB = correoOAMB;
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

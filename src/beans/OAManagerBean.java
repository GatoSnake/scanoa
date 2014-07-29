package beans;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import categorization.DownloadOAs;
import categorization.TextCovering;
import categorization.TextExtractionOffice;
import categorization.TextExtractionPDF;
import ontology.ConsultasRead;
import ontology.ConsultasWrite;
import util.OABean;
import util.TopicBean;
import dao.GenericDAO;
import dao.OADAO;
import dao.UserDAO;

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
	
	@ManagedProperty("#{topicManagerBean}")
	private TopicManagerBean topicMB;
	
    @PostConstruct
    public void init() {
    	oas = OADAO.getAllOAs();
    	for(OABean oa : oas){
			oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
		}
    }
    
    public void clearParameters(){
    	this.urlOAMB=null;
    	this.tituloOAMB=null;
    	this.descripcionOAMB=null;
    	this.correoOAMB=null;
    }
    
    public void searchByTitulo(){
		if(!inputSearchTitulo.equals("")){
			List<OABean> listOAs = new ArrayList<OABean>();
			listOAs = OADAO.getOAForSearch("titulo",inputSearchTitulo);
			for(OABean oa : listOAs){
				oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
			}
			oas = listOAs;
		}
		else{
			init();
		}
	}
    
    public void searchByDescripcion(){
		if(!inputSearchDescripcion.equals("")){
			List<OABean> listOAs = new ArrayList<OABean>();
			listOAs = OADAO.getOAForSearch("descripcion",inputSearchDescripcion);
			for(OABean oa : listOAs){
				oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
			}
			oas = listOAs;
		}
		else{
			init();
		}
	}
    
    public void searchByTopic(){
    	List<OABean> listOAs = new ArrayList<OABean>();
		listOAs = topicMB.searchByTopic();
		for(OABean oa : listOAs){
			oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
		}
		oas = listOAs;
    }
    
    public void addOA() throws ParseException, IOException{
    	System.out.println(this.urlOAMB);
    	System.out.println(this.tituloOAMB);
    	System.out.println(this.descripcionOAMB);
    	System.out.println(this.correoOAMB);
    	boolean exist = UserDAO.existUser(this.correoOAMB);
    	if(exist){
    		//insercion del oa a la bd
        	OADAO.insertOA(this.urlOAMB, this.tituloOAMB, this.descripcionOAMB, this.correoOAMB);
        	String id = OADAO.getIdActualOA(this.correoOAMB, this.urlOAMB);
        	//proceso de categorizacion
        	boolean rsCat = processCategorization(this.urlOAMB,id,null,1);
        	//mensajes growl
    		if(rsCat){
        		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
    					"Éxito!",
    					"Se ha agregado el objeto de aprendizaje.");
    			FacesContext.getCurrentInstance().addMessage(null, message);
        	}else{
        		int idDelete = Integer.parseInt(id);
        		GenericDAO.genericIntegerDelete("learning_objects", "id", idDelete);
        		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					"Error!",
    					"Se ha producido un error en el ingreso. Por favor, intente más tarde.");
    			FacesContext.getCurrentInstance().addMessage(null, message);
        	}
    	}else{
    		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
					"Atención!",
					"No existe un usuario con el correo ingresado. Por favor, ingrese un correo existente.");
			FacesContext.getCurrentInstance().addMessage(null, message);
    	}
    	clearParameters();
    	init();
	}
	
	public void editOA() throws IOException {
		if(selectedOA != null){ 				
			ArrayList<Boolean> rs = new ArrayList<Boolean>();
			rs.add(false);
			rs.add(false);
			rs.add(false);
			rs.add(false);
			if(!this.urlOAMB.equals("")){
				rs.add(0, GenericDAO.genericIntegerUpdate("learning_objects", "url", this.urlOAMB, "id", selectedOA.getId()));
				String id = OADAO.getIdActualOA(selectedOA.getUsuario(), this.urlOAMB);
				processCategorization(this.urlOAMB,id,selectedOA,2);
			}
			if(!this.tituloOAMB.equals("")){
				rs.add(1, GenericDAO.genericIntegerUpdate("learning_objects", "titulo", this.tituloOAMB, "id", selectedOA.getId()));
			}
			if(!this.descripcionOAMB.equals("")){
				rs.add(2, GenericDAO.genericIntegerUpdate("learning_objects", "descripcion", this.descripcionOAMB, "id", selectedOA.getId()));
			}
			if(!this.correoOAMB.equals("")){
				rs.add(3, GenericDAO.genericIntegerUpdate("learning_objects", "usuario_correo", this.correoOAMB, "id", selectedOA.getId()));
			}
			if(this.urlOAMB.equals("") && this.tituloOAMB.equals("") && this.descripcionOAMB.equals("") && this.correoOAMB.equals("")){
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					"Atención!",
    					"Debe agregar valores para modificar un objeto de aprendizaje.");
    			FacesContext.getCurrentInstance().addMessage(null, message);
			}else{
				if(rs.get(0)||rs.get(1)||rs.get(2)||rs.get(3)){
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
	
	public void deleteOA(){
		if(selectedOA != null){
    		boolean flag = false;
			flag = GenericDAO.genericIntegerDelete("learning_objects", "id", selectedOA.getId());
			for(TopicBean topic : selectedOA.getTopicos()){
				String value = "'"+topic.getTopico()+"OA"+selectedOA.getId()+"'";
				ConsultasWrite.deleteProperty("ContCompTI:topicIsReferencedBy",topic.getTopico(),value);
			}
    		if (flag == false) {
    			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
    					"Error!",
    					"Puede que algunos de los datos no se haya podido eliminar.");
    			FacesContext.getCurrentInstance().addMessage(null, message);
    		}else{
    			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
    					"Éxito!",
    					"Objeto de aprendizaje \""+selectedOA.getTitulo()+"\" eliminado.");
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
	
	public boolean processCategorization(String URL, String id, OABean oa, int operacion) throws IOException{
    	boolean result = false;
    	boolean resultCat = false;
    	String texto = null;
    	if(URL.endsWith(".pdf")){
    		result = DownloadOAs.downloadOA(URL,"temp.pdf");
    		texto = TextExtractionPDF.extractionTextPDF();
    	}
    	else if(URL.endsWith(".doc")){
    		result = DownloadOAs.downloadOA(URL,"temp.doc");
    		texto = TextExtractionOffice.readDoc();
    	}
    	else if(URL.endsWith(".docx")){
    		result = DownloadOAs.downloadOA(URL,"temp.docx");
    		texto = TextExtractionOffice.readDocx();
    	}
    	if(result){
    		texto = TextCovering.normalizationText(texto);
    		List<TopicBean> topicosOntology = ConsultasRead.getAllTopics();
    		List<String> topicos = new ArrayList<String>();
    		for(TopicBean topic : topicosOntology){
    			String temp = topic.getLabel().toLowerCase();
    			temp = temp.replaceAll("\\s+", " ");
    			temp = temp.trim();
    			topicos.add(temp);
    		}
    		Map<List<String>, Double> categorizados = TextCovering.getTextCovering(texto,topicos);
    		List<String> menoresTopicos = TextCovering.getKeyListFromValue(categorizados, Collections.min(categorizados.values()));
    		System.out.println("*************** CATEGORIZADOS ***************");
    		System.out.println(menoresTopicos+" - "+Collections.min(categorizados.values()));
    		List<String> topicClasified = new ArrayList<String>();
    		for(TopicBean topico : topicosOntology){
    			String labelTopico = topico.getLabel();
    			labelTopico = labelTopico.toLowerCase();
    			labelTopico = labelTopico.replaceAll("\\s+", " ");
    			labelTopico = labelTopico.trim();
    			for(String elegido : menoresTopicos){
    				if(labelTopico.equals(elegido)){
    					topicClasified.add(topico.getTopico());
    				}
    			}
    		}
//    		System.out.println(topicClasified);
    		List<Entry<String, Double>> resRelev = TextCovering.topicRelevance(texto,menoresTopicos,topicos,topicClasified);
    		List<String> listRank = new ArrayList<String>();
    		for(Map.Entry<String, Double> entry : resRelev){
    			listRank.add(entry.getKey());
    		}
    		System.out.println("*************** RANKING ASC ***************");
    		List<String> listTopicRanking = new ArrayList<String>();
    		for(String elegido : listRank){
    			for(TopicBean topico : topicosOntology){
	    			String labelTopico = topico.getLabel();
	    			labelTopico = labelTopico.toLowerCase();
	    			labelTopico = labelTopico.replaceAll("\\s+", " ");
	    			labelTopico = labelTopico.trim();
    				if(labelTopico.equals(elegido)){
    					String saveLabel = topico.getLabel().replaceAll("\\s+", " ");
    					saveLabel = saveLabel.trim();
    					listTopicRanking.add(saveLabel);
    				}
    			}
    		}
    		System.out.println(listTopicRanking);
    		String rankTopic = "";
    		for(String itemRank : listTopicRanking){
    			rankTopic = rankTopic+itemRank+";";
    		}
    		if(!rankTopic.equals("")){
    			GenericDAO.genericStringUpdate("learning_objects", "ranking_topicos", rankTopic, "id", id);
    		}
    		if(operacion==2){
    			List<TopicBean> oaTopics = oa.getTopicos();
    			for(TopicBean topic : oaTopics){
    				String value = "'"+topic.getTopico()+"OA"+oa.getId()+"'";
    				ConsultasWrite.deleteProperty("ContCompTI:topicIsReferencedBy",topic.getTopico(),value);
    			}
    		}
    		for(String topico : topicClasified){
    			String nameOA = "'"+topico+"OA"+id+"'";
    			ConsultasWrite.insertProperty("ContCompTI:topicIsReferencedBy", topico, nameOA);
    		}
    		resultCat = true;
    	}else{
    		resultCat = false;
    	}
    	return resultCat;
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

	public TopicManagerBean getTopicMB() {
		return topicMB;
	}

	public void setTopicMB(TopicManagerBean topicMB) {
		this.topicMB = topicMB;
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
     
    
	
}

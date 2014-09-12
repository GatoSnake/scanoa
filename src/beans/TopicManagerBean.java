package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import dao.GenericDAO;
import ontology.*;
import util.TopicBean;

@ManagedBean(name="topicManagerBean")
@ViewScoped

public class TopicManagerBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private TreeNode root;
	private TreeNode selectedNode;
	private TopicBean selectedTopic;
	
	private String topicoTMB;
	private String labelTMB;
	private int estadoExito;
	private int estadoError;
	private ArrayList<Boolean> mensajesExito = new ArrayList<Boolean>();
	private ArrayList<Boolean> mensajesError = new ArrayList<Boolean>();
	private Boolean buttonDisable;

	@PostConstruct
	public void init() {
		root = new DefaultTreeNode(new TopicBean(null,null,0), null);
		ConexionOntology.confirmarModelo();
		List<TopicBean> topicslvl = ConsultasRead.getTopicsGeneral();
		root = getTopicosRecursivo(topicslvl,root);
		this.buttonDisable = true;
	}
		
	public TreeNode getTopicosRecursivo(List<TopicBean> topicslvl,TreeNode father){
		for (TopicBean itemList : topicslvl) {
			List<String> cantOAs = ConsultasRead.getOAsOfTopic(itemList.getTopico());
			TreeNode lvl = new DefaultTreeNode(new TopicBean(itemList.getTopico(),itemList.getLabel(),cantOAs.size()), father);
			List<TopicBean> topicsChild = ConsultasRead.getChildsTopics(itemList.getTopico());
			getTopicosRecursivo(topicsChild,lvl);
		}
		return root;
	}
	
	public void clearParameters(){
    	this.topicoTMB = null;
    	this.labelTMB = null;
    }
	
    public void restartMessages(){
    	this.mensajesExito.clear();
		this.mensajesError.clear();
		for(int i=0; i<4; i++){
			this.mensajesExito.add(false);
		}
		for(int i=0; i<3; i++){
			this.mensajesError.add(false);
		}
		this.estadoExito = 0;
		this.estadoError = 0;
    }
	
	public void addTopic(){
		restartMessages();
		boolean exist = ConsultasRead.confirmTopic(this.topicoTMB);
		if (!exist){
			if(selectedNode != null){
				ConsultasWrite.addTopicToFather(selectedNode.toString(),this.topicoTMB,this.labelTMB);
			}else{
				ConsultasWrite.addNewTopic(this.topicoTMB, this.labelTMB);
			}
			GenericDAO.dateAutomaticInsertWhereString("log_topicos", "fecha_modificacion");
			this.estadoExito = 1;
			this.mensajesExito.set(0, true);
		}else{
			this.estadoError = 1;
			this.mensajesError.set(0, true);
		}
		selectedNode = null;
		selectedTopic = null;
		init();
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public void editTopic(){
		restartMessages();
		Boolean existTopic = null;
		Boolean rs1 = null;
		Boolean rs2 = null;
		//si el id es distinto al antiguo entonces hacemos el cambio
		if(!this.topicoTMB.equals(selectedNode.toString())){
			rs1 = false;
//			confirmamos de que existe un topico con ese id
			existTopic = ConsultasRead.confirmTopic(this.topicoTMB);
			if(!existTopic){
				//obtenemos los topicos hijos del actual
				List<TopicBean> hijos = ConsultasRead.getChildsTopics(selectedNode.toString());
				//si el actual tiene padre o no
				if(selectedNode.getParent().toString() == null){
					//no tiene padre!
					//agregamos el nuevo topico
					ConsultasWrite.addNewTopic(this.topicoTMB, this.labelTMB);
					//obtenemos todas las referencias de todos los OAs hacia el topico actual
					List<String> oasTopic = ConsultasRead.getOAsOfTopic(selectedNode.toString());
					//aca cambiamos el id de la referencia de los OA hacia el topico nuevo
					for(String oaRef : oasTopic){
						String temp = oaRef.replaceAll("([A-Za-z0-9]+)OA", "");
						String newOaRef = this.topicoTMB+"OA"+temp;
						ConsultasWrite.updatePropertyValueString("ContCompTI:topicIsReferencedBy", this.topicoTMB, oaRef, newOaRef);
					}
					//aca agregamos la relacion de los topicos hijos al nuevo topico
					for(TopicBean itemHijo : hijos ){
						ConsultasWrite.insertPropertyNarrower("skos:narrower", this.topicoTMB, itemHijo.getTopico());
					}
					//borramos el topico antiguo
					ConsultasWrite.deleteTopic(selectedNode.toString());
					rs1 = true;
				}else{
					//tiene un padre!
					//agregamos el nuevo topico
					ConsultasWrite.addTopicToFather(selectedNode.getParent().toString(), this.topicoTMB, this.labelTMB);
					//obtenemos todas las referencias de todos los OAs hacia el topico actual
					List<String> oasTopic = ConsultasRead.getOAsOfTopic(selectedNode.toString());
					for(String oaRef : oasTopic){
						String temp = oaRef.replaceAll("([A-Za-z0-9]+)OA", "");
						String newOaRef = this.topicoTMB+"OA"+temp;
						ConsultasWrite.updatePropertyValueString("ContCompTI:topicIsReferencedBy", this.topicoTMB, oaRef, newOaRef);
					}
					//aca agregamos la relacion de los topicos hijos al nuevo topico
					for(TopicBean itemHijo : hijos ){
						ConsultasWrite.insertPropertyNarrower("skos:narrower", this.topicoTMB, itemHijo.getTopico());
					}
					//borramos el topico antiguo
					ConsultasWrite.deleteTopic(selectedNode.toString());
					rs1 = true;
				}
			}
		}
		//si es null entonces nunca se cambio el id del topico
		if(existTopic == null && !this.labelTMB.equals(selectedTopic.getLabel())){
			rs2 = false;
			System.out.println("A cambiar solo el nombre!");
			//hay que actualizar solo el label del topico actual
			ConsultasWrite.updatePropertyValueString("rdfs:label", selectedNode.toString(), selectedTopic.getLabel(), this.labelTMB);
			rs2 = true;
		}		
		if(rs1!=null || rs2!=null || existTopic!=null){
			if(existTopic!=null && !existTopic){
				if(rs1!=null && rs1){
					this.estadoExito = 1;
					this.mensajesExito.set(1, true);
				}else if(rs1!=null && !rs1){
					this.estadoError = 1;
					this.mensajesError.set(1, true);
				}
			}else if(existTopic!=null && existTopic){
				this.estadoError = 1;
				this.mensajesError.set(0, true);
			}
			if(rs2!=null && rs2){
				this.estadoExito = 1;
				this.mensajesExito.set(2, true);
			}else if(rs2!=null && !rs2){
				this.estadoError = 1;
				this.mensajesError.set(2, true);
			}
		}else{
			this.estadoExito = 0;
			this.estadoError = 0;
		}		
		selectedNode = null;
		selectedTopic = null;
		init();
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public void deleteTopic() {
		restartMessages();
		ConsultasWrite.deleteTopic(selectedNode.toString());
		deleteChildsRecursivo(selectedNode.getChildren());
		GenericDAO.dateAutomaticInsertWhereString("log_topicos", "fecha_modificacion");
		this.estadoExito = 1;
		this.mensajesExito.set(3, true);
		selectedNode = null;
		selectedTopic = null;
		init();
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}
	
	public void deleteChildsRecursivo(List<TreeNode> list){
		if(list != null && list.size() > 0){
			for(TreeNode node: list){
				ConsultasWrite.deleteTopic(node.getData().toString());
				deleteChildsRecursivo(node.getChildren());
			}
		}
	}
	
	public void setValuesAdd(){
		this.topicoTMB = null;
		this.labelTMB = null;
	}
	
	public void setValuesEdit(){
		this.topicoTMB = selectedTopic.getTopico();
		this.labelTMB = selectedTopic.getLabel();
	}
		
	public void onNodeSelect(NodeSelectEvent event) {
		List<String> label = ConsultasRead.getValuesOfPropertyLiteral(event.getTreeNode().toString(), "rdfs:label");
		List<String> cantOAs = ConsultasRead.getOAsOfTopic(event.getTreeNode().toString());
		selectedTopic = new TopicBean(event.getTreeNode().getData().toString(),label.get(0),cantOAs.size());
		this.buttonDisable = false;

    }
	
	public void onNodeUnselect(NodeUnselectEvent event) {
		selectedNode = null;
		selectedTopic = null;
		this.buttonDisable = true;
	}
	
	public void resetTable(){
		selectedNode = null;
		selectedTopic = null;
		init();
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public TopicBean getSelectedTopic() {
		return selectedTopic;
	}

	public void setSelectedTopic(TopicBean selectedTopic) {
		this.selectedTopic = selectedTopic;
	}

	public String getTopicoTMB() {
		return topicoTMB;
	}

	public void setTopicoTMB(String topicoTMB) {
		this.topicoTMB = topicoTMB;
	}

	public String getLabelTMB() {
		return labelTMB;
	}

	public void setLabelTMB(String labelTMB) {
		this.labelTMB = labelTMB;
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
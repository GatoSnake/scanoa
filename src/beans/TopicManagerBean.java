package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import dao.GenericDAO;
import dao.OADAO;
import ontology.*;
import util.OABean;
import util.TopicBean;

@ManagedBean(name="topicManagerBean")
@ViewScoped
public class TopicManagerBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private TreeNode root;
	private TreeNode selectedNode;
//	private TreeNode[] selectedNodes;
	
	private String topicoTMB;
	private String labelTMB;

	@PostConstruct
	public void init() {
		System.out.println("Busqueda a todos los topicos");
		root = new DefaultTreeNode(new TopicBean(null,null,0), null);
		ConexionOntology.confirmarModelo();
		List<TopicBean> topicslvl = ConsultasRead.getTopicsGeneral();
		root = getTopicosRecursivo(topicslvl,root);
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
    	this.topicoTMB=null;
    	this.labelTMB=null;
    }
	
	public void addTopic(){
		System.out.println(selectedNode);
		boolean exist = ConsultasRead.confirmTopic(this.topicoTMB);
		if (!exist){
			if(selectedNode != null){
				ConsultasWrite.addTopicToFather(selectedNode.toString(),this.topicoTMB,this.labelTMB);
			} else{
				ConsultasWrite.addNewTopic(this.topicoTMB, this.labelTMB);
			}
			GenericDAO.genericInsertDate2("log_topicos", "fecha_modificacion");
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
					"Exito!",
					"Tópico \""+this.topicoTMB+"\" agregado.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}else{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
					"Atención!",
					"Ya existe un tópico con el identificador descrito.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}

		clearParameters();
		this.selectedNode=null;
		init();
	}
	
	public void editTopic(){
		boolean exist = ConsultasRead.confirmTopic(this.topicoTMB);
		if(!exist){
			List<TopicBean> hijos = ConsultasRead.getChildsTopics(selectedNode.toString());
			if(selectedNode.getParent().toString() == null){
				System.out.println("ROOT");
				ConsultasWrite.addNewTopic(this.topicoTMB, this.labelTMB);
				List<String> oasTopic = ConsultasRead.getOAsOfTopic(selectedNode.toString());
				for(String oaRef : oasTopic){
//					System.out.println(oaRef);
					oaRef = oaRef.replaceAll("([A-Za-z0-9]+)OA", "");
					String newOaRef = "'"+this.topicoTMB+"OA"+oaRef+"'";
//					System.out.println(newOaRef);
					ConsultasWrite.insertProperty("ContCompTI:topicIsReferencedBy", this.topicoTMB, newOaRef);
				}
				for(TopicBean itemHijo : hijos ){
//					System.out.println(itemHijo);
					ConsultasWrite.insertPropertyNarrower("skos:narrower", this.topicoTMB, itemHijo.getTopico());
				}
				ConsultasWrite.deleteTopic(selectedNode.toString());
			}else{
				System.out.println("NO ROOT");
				ConsultasWrite.addTopicToFather(selectedNode.getParent().toString(), this.topicoTMB, this.labelTMB);
				//aca hay que obtener los valores de las propiedades de los OAS
				List<String> oasTopic = ConsultasRead.getOAsOfTopic(selectedNode.toString());
				for(String oaRef : oasTopic){
//					System.out.println(oaRef);
					oaRef = oaRef.replaceAll("([A-Za-z0-9]+)OA", "");
					String newOaRef = "'"+this.topicoTMB+"OA"+oaRef+"'";
					ConsultasWrite.insertProperty("ContCompTI:topicIsReferencedBy", this.topicoTMB, newOaRef);
				}
				for(TopicBean itemHijo : hijos ){
					ConsultasWrite.insertPropertyNarrower("skos:narrower", this.topicoTMB, itemHijo.getTopico());
				}
				List<String> padres = ConsultasRead.getTopicsFathers(selectedNode.toString());
				if(padres.size() == 1){
					ConsultasWrite.deleteTopic(selectedNode.toString());
				}
				ConsultasWrite.deletePropertyNarrower("skos:narrower", selectedNode.getParent().toString(), selectedNode.toString());
			}
			GenericDAO.genericInsertDate2("log_topicos", "fecha_modificacion");
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
					"Exito!",
					"Tópico \""+selectedNode.toString()+"\" modificado.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}else{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
					"Atención!",
					"Ya existe un tópico con el identificador descrito.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		clearParameters();
		init();
//		
//		collapsingORexpanding(root,false);
//		if(selectedNodes.length != 0){
//			boolean exist = ConsultasRead.confirmTopic(this.topicoTMB);
//			if(!exist){
//				if(selectedNodes.length == 1){
//					System.out.println("UNO!");
//					for(TreeNode node : nodes) {
//						List<TopicBean> hijos = ConsultasRead.getChildsTopics(node.getData().toString());
//						if(node.getParent().toString() == null){
//							System.out.println("ROOT");
//							ConsultasWrite.addNewTopic(this.topicoTMB, this.labelTMB);
//							for(TopicBean itemHijo : hijos ){
//								ConsultasWrite.insertPropertyNarrower("skos:narrower", this.topicoTMB, itemHijo.getTopico());
//							}
//							ConsultasWrite.deleteTopic(node.getData().toString());
//						}else{
//							System.out.println("NOOO ROOT");
//							ConsultasWrite.addTopicToFather(node.getParent().toString(), this.topicoTMB, this.labelTMB);
//							for(TopicBean itemHijo : hijos ){
//								ConsultasWrite.insertPropertyNarrower("skos:narrower", this.topicoTMB, itemHijo.getTopico());
//							}
//							List<String> padres = ConsultasRead.getTopicsFathers(node.getData().toString());
//							if(padres.size() == 1){
//								ConsultasWrite.deleteTopic(node.getData().toString());
//							}
//							ConsultasWrite.deletePropertyNarrower("skos:narrower", node.getParent().toString(), node.getData().toString());
//						}
//						FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
//								"Exito!",
//								"Tópico \""+node.getData().toString()+"\" modificado.");
//						FacesContext.getCurrentInstance().addMessage(null, message);
//					}
//				}else if(selectedNodes.length > 1){
//					System.out.println("MUCHOS");
//					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
//							"Atención!",
//							"Debe seleccionar solo un tópico para editar.");
//					FacesContext.getCurrentInstance().addMessage(null, message);
//				}
//			} else {
//				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
//						"Problema!",
//						"Ya existe un tópico con el identificador descrito.");
//				FacesContext.getCurrentInstance().addMessage(null, message);
//			}
//		}else{
//			System.out.println("NINGUNO!");
//			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
//					"Atención!",
//					"Seleccione el tópico donde quiere añadir el nuevo tópico.");
//			FacesContext.getCurrentInstance().addMessage(null, message);
//		}
//		this.topicoTMB=null;
//		this.labelTMB=null;
//		this.selectedNodes=null;
//		init();
	}
	
	public void deleteTopic() {
//		System.out.println(selectedNode);
		List<String> padres = ConsultasRead.getTopicsFathers(selectedNode.toString());
		if(padres.size() == 0 || padres.size() == 1){
//			System.out.println("UN PAPA O NO TIENE");
			ConsultasWrite.deleteTopic(selectedNode.toString());
			deleteChildsRecursivo(selectedNode.getChildren());
		}
		else if (padres.size() > 1){
//			System.out.println("MAS DE UN PAPA");
			ConsultasWrite.deletePropertyNarrower("skos:narrower", selectedNode.getParent().toString(), selectedNode.toString());
			selectedNode.getParent().getChildren().remove(selectedNode);
		}
		GenericDAO.genericInsertDate2("log_topicos", "fecha_modificacion");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
				"Exito!","Tópico \""+selectedNode.toString()+"\" eliminado.");
		FacesContext.getCurrentInstance().addMessage(null, message);
		init();
	}
	
	public void deleteChildsRecursivo(List<TreeNode> list){
		if(list != null && list.size() > 0){
			for(TreeNode node: list){
				ConsultasWrite.deleteTopic(node.getData().toString());
				deleteChildsRecursivo(node.getChildren());
			}
		}
	}
	
	public List<OABean> searchByTopic(){
		if(selectedNode != null){
			List<OABean> listOAs = new ArrayList<OABean>();
			List<String> oas = ConsultasRead.getOAsOfTopic(selectedNode.toString());
			for(String oa : oas){
				System.out.println(oa);
				oa = oa.replaceAll("([A-Za-z0-9]+)OA", "");
				System.out.println(oa);
				listOAs.add(OADAO.getOAForId(oa));
			}
			return listOAs;
		}else{
			return null;
		}
	}
	
//	public void onDragDrop(TreeDragDropEvent event) {
//        TreeNode dragNode = event.getDragNode(); //topico quien arrastro
//        TreeNode dropNode = event.getDropNode(); //donde tiramos el topico
//        int dropIndex = event.getDropIndex();
//        
//        System.out.println(dragNode);
//        System.out.println(dropNode);
//        System.out.println(ConsultasRead.getTopicFather(dragNode.toString()));
//        System.out.println(selectedNodes.length);
//        for(TreeNode nodo : selectedNodes){
//        	System.out.println(nodo.getParent());
//        }
//        
//        String padre = ConsultasRead.getTopicFather(dragNode.toString()); //de donde provenia el topico
//        if(!padre.equals("") && dropNode.toString() != null){
//        	if(padre.equals("")){
//            	System.out.println("ROOT");
////            	ConsultasWrite.updateProperty("ContCompTI:EsPadreGeneral", dragNode.toString(), "true", "false");
////            	ConsultasWrite.deleteProperty("ContCompTI:EsPadreGeneral", dragNode.toString(), "false");
////            	ConsultasWrite.insertPropertyNarrower("skos:narrower", dropNode.getData().toString(), dragNode.toString());
//            }else{
////            	ConsultasWrite.deletePropertyNarrower("skos:narrower", padre, dragNode.toString());
////                ConsultasWrite.insertPropertyNarrower("skos:narrower", dropNode.getData().toString(), dragNode.toString());
//            }
//        }else{
//        	System.out.println("NO HACER NINGUNA WEA");
//        }
//        
//        
//        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Dragged " + dragNode.getData(), "Dropped on " + dropNode.getData() + " at " + dropIndex);
//        FacesContext.getCurrentInstance().addMessage(null, message);
//    }
	
//	public void collapsingORexpanding(TreeNode n, boolean option) {
//		if(n.getChildren().size() == 0) {
//			n.setSelected(false);
//		}
//		else {
//			for(TreeNode s: n.getChildren()) {
//				collapsingORexpanding(s, option);
//			}
//			n.setExpanded(option);
//			n.setSelected(false);
//		}
//	}
	 
	public TreeNode getRoot() {
		 return root;
	}
	
//	public TreeNode[] getSelectedNodes() {
//		return selectedNodes;
//	}
//
//	public void setSelectedNodes(TreeNode[] selectedNodes) {
//		this.selectedNodes = selectedNodes;
//	}

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

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	    
}
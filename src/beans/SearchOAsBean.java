package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ontology.ConexionOntology;
import ontology.ConsultasRead;
import dao.OADAO;
import util.OABean;
import util.TopicBean;

@ManagedBean(name="searchOAsBean")
@ViewScoped
public class SearchOAsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private TreeNode root;
	private TreeNode selectedNode;
	private List<OABean> oas;
	private OABean selectedOA;
	private List<OABean> filteredOAs;
	
	private String inputTitulo;
	private Boolean buttonDisable;
	private Boolean buttonDisableTree;
	
	@PostConstruct
    public void init() {
		//Para busqueda por topicos
		root = new DefaultTreeNode(new TopicBean(null,null,0), null);
		ConexionOntology.confirmarModelo();
		List<TopicBean> topicslvl = ConsultasRead.getTopicsGeneral();
		root = getTopicosRecursivo(topicslvl,root);
		this.buttonDisable = true;
		//Tabla de resultados
		oas = OADAO.getAllOAs();
		this.buttonDisable = true;
		this.buttonDisableTree = true;
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
	
	public void searchByTitulo(AjaxBehaviorEvent e ) {
	    oas = OADAO.getAllOAs();
	    List<OABean> resultadoOAs = new ArrayList<OABean>();
	    for(OABean oa : oas){
	    	if(oa.getTitulo().toLowerCase().contains(this.inputTitulo.toLowerCase())) resultadoOAs.add(oa);
		}
		oas = resultadoOAs;
	}
    	
	public void onRowSelect(SelectEvent event) {
		this.buttonDisable = false;
    }
	
	public void onSearchSelected(NodeSelectEvent event) {
		oas = OADAO.getAllOAs();
		List<OABean> resultadoOAs = new ArrayList<OABean>();
		List<String> label = ConsultasRead.getValuesOfPropertyLiteral(event.getTreeNode().toString(), "rdfs:label");
		for(OABean oa : oas){
			if(oa.getRankTopicos()!=null){
				for(TopicBean topico : oa.getRankTopicos()){
					if(topico.getLabel().equals(label.get(0))) resultadoOAs.add(oa);
				}
			}
		}
		oas = resultadoOAs;
		this.buttonDisableTree = false;
		this.buttonDisable = true;
	}
	
	public void onSearchUnselected(NodeUnselectEvent event) {
		oas = OADAO.getAllOAs();
		this.buttonDisableTree = true;
	}
	
	public void resetTree(){
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

	public String getInputTitulo() {
		return inputTitulo;
	}

	public void setInputTitulo(String inputTitulo) {
		this.inputTitulo = inputTitulo;
	}

	public Boolean getButtonDisable() {
		return buttonDisable;
	}

	public void setButtonDisable(Boolean buttonDisable) {
		this.buttonDisable = buttonDisable;
	}

	public Boolean getButtonDisableTree() {
		return buttonDisableTree;
	}

	public void setButtonDisableTree(Boolean buttonDisableTree) {
		this.buttonDisableTree = buttonDisableTree;
	}

}

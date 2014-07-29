package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ontology.ConsultasRead;
import dao.OADAO;
import util.OABean;

@ManagedBean(name="searchOAsBean")
@ViewScoped
public class SearchOAsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<OABean> oas;
	private OABean selectedOA;
	private String inputSearchTitulo;
	private String inputSearchDescripcion;
	private boolean disable;
	
	@ManagedProperty("#{topicManagerBean}")
	private TopicManagerBean topicMB;
	
	public void searchByTitulo(){
		if(!inputSearchTitulo.equals("")){
			List<OABean> listOAs = new ArrayList<OABean>();
			listOAs = OADAO.getOAForSearch("titulo",inputSearchTitulo);
			for(OABean oa : listOAs){
				oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
			}
			if(listOAs.size()>0){
				this.disable = true;
			}else{
				this.disable = false;
			}
			oas = listOAs;
		}
		else{
			oas = null;
		}
	}
    
    public void searchByDescripcion(){
		if(!inputSearchDescripcion.equals("")){
			List<OABean> listOAs = new ArrayList<OABean>();
			listOAs = OADAO.getOAForSearch("descripcion",inputSearchDescripcion);
			for(OABean oa : listOAs){
				oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
			}
			if(listOAs.size()>0){
				this.disable = true;
			}else{
				this.disable = false;
			}
			oas = listOAs;
		}
		else{
			oas = null;
		}
	}
	
	public void searchByTopic(){
		List<OABean> listOAs = new ArrayList<OABean>();
		listOAs = topicMB.searchByTopic();
		if(listOAs != null){
			for(OABean oa : listOAs){
				oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
			}
			if(listOAs.size()>0){
				this.disable = true;
			}else{
				this.disable = false;
			}
		}else{
			this.disable = false;
		}
		oas = listOAs;
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

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
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

}

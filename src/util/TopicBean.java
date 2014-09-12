package util;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
@ManagedBean(name = "topicBean")
@SessionScoped

public class TopicBean implements Serializable {
	 private static final long serialVersionUID = 1L;
	 private String topico;
	 private String label;
	 private int cantOAs;

	public TopicBean(String topico, String label, int cantOAs) {
		this.topico = topico;
		this.label = label;
		this.cantOAs = cantOAs;
	}

	public String getTopico() {
		return topico;
	}

	public void setTopico(String topico) {
		this.topico = topico;
	}
	
	public void cleanUserBean(){
		this.topico=null;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
    public String toString() {
        return topico;
    }

	public int getCantOAs() {
		return cantOAs;
	}

	public void setCantOAs(int cantOAs) {
		this.cantOAs = cantOAs;
	}
	
}

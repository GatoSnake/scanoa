package beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import categorization.Categorization;
import util.OABean;
import dao.GenericDAO;
import dao.OADAO;

@ManagedBean(name = "recategorizationBean")
@ViewScoped
public class RecategorizationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String lastRecatgDate;
	private String lastRecatgTime;
	private Integer flagConsejo;
	private int estado;
	private ArrayList<Boolean> mensajes = new ArrayList<>(Arrays.asList(
			false));
	
	@PostConstruct
    public void init() {
    	try {   
    		SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    		String fechaRecat = GenericDAO.getLastDateInTableByID("log_recategorization");
    		String fechaTopics = GenericDAO.getLastDateInTableByID("log_topicos");
    		this.lastRecatgDate = dateFormat.format(originalFormat.parse(fechaRecat));
    		this.lastRecatgTime = timeFormat.format(originalFormat.parse(fechaRecat));    		
    		Date date1 = originalFormat.parse(fechaRecat);
    		Date date2 = originalFormat.parse(fechaTopics);
    		if(date2.after(date1)){
                this.flagConsejo = 1;
            }
    		else{
    			this.flagConsejo = 0;
    		}
		} catch (ParseException ex) {
			System.out.println("Error en init() de recategorizationBean -->" + ex);
		}
    }
		
	public void recategorizarOAs() {
		Integer resultCat = null;
		List<OABean> listOAs = OADAO.getAllOAs();
		for(OABean oa : listOAs){
			 resultCat = Categorization.processCategorization(oa.getUrl(), Integer.toString(oa.getId()), oa, 2);
		}
		GenericDAO.dateAutomaticInsertWhereString("log_recategorization", "fecha_recategorizacion");
		if(resultCat!=null){
			if(resultCat==3 || resultCat==4){
				this.estado = 1;
				this.mensajes.set(0, true);
			}else{
				this.estado = 2;
				this.mensajes.set(0, true);
			}
		}
		init();
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("loading");
	}

	public String getLastRecatgDate() {
		return lastRecatgDate;
	}

	public void setLastRecatgDate(String lastRecatgDate) {
		this.lastRecatgDate = lastRecatgDate;
	}

	public String getLastRecatgTime() {
		return lastRecatgTime;
	}

	public void setLastRecatgTime(String lastRecatgTime) {
		this.lastRecatgTime = lastRecatgTime;
	}

	public Integer getFlagConsejo() {
		return flagConsejo;
	}

	public void setFlagConsejo(Integer flagConsejo) {
		this.flagConsejo = flagConsejo;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public ArrayList<Boolean> getMensajes() {
		return mensajes;
	}

	public void setMensajes(ArrayList<Boolean> mensajes) {
		this.mensajes = mensajes;
	}
    	
}

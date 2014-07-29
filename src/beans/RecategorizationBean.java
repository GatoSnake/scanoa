package beans;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ontology.ConsultasRead;
import util.OABean;
import dao.GenericDAO;
import dao.OADAO;

@ManagedBean(name = "recategorizationBean")
@ViewScoped
public class RecategorizationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String lastRecatgDate;
	private String lastRecatgTime;
	private int flagConsejo;
	
	@PostConstruct
    public void init() {
		SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    	String fechaRecat = GenericDAO.genericSelectLastRowID("log_recategorization");
    	String fechaTopics = GenericDAO.genericSelectLastRowID("log_topicos");
    	try {    		
    		this.lastRecatgDate = dateFormat.format(originalFormat.parse(fechaRecat));
    		this.lastRecatgTime = timeFormat.format(originalFormat.parse(fechaRecat));    		
    		Date date1 = originalFormat.parse(fechaRecat);
    		Date date2 = originalFormat.parse(fechaTopics);
    		if(date2.after(date1)){
                this.flagConsejo=1;
            }
    		else{
    			this.flagConsejo=0;
    		}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }
	
	@ManagedProperty("#{oaManagerBean}")
	private OAManagerBean OAMB;
	
	public void recategorizarOAs() throws IOException{
		List<OABean> listOAs = OADAO.getAllOAs();
		for(OABean oa : listOAs){
			oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
		}
		for(OABean oa : listOAs){
			String id = Integer.toString(oa.getId());
			OAMB.processCategorization(oa.getUrl(), id, oa, 2);
		}
		GenericDAO.genericInsertDate2("log_recategorization", "fecha_recategorizacion");
		init();
	}

	public OAManagerBean getOAMB() {
		return OAMB;
	}

	public void setOAMB(OAManagerBean oAMB) {
		OAMB = oAMB;
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

	public int getFlagConsejo() {
		return flagConsejo;
	}

	public void setFlagConsejo(int flagConsejo) {
		this.flagConsejo = flagConsejo;
	}
    	
}

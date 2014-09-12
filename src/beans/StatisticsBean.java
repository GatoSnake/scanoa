package beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ontology.ConsultasRead;
import dao.GenericDAO;
import dao.OADAO;

@ManagedBean(name = "statisticsBean")
@ViewScoped
public class StatisticsBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int cantUsers;
	private int cantOAs;
	private String cantTopics;
	private int cantOAsCat;
	private int cantOAsNoCat;

	@PostConstruct
	public void init() {
		this.cantUsers = GenericDAO.countTable("usuarios");
		this.cantOAs = GenericDAO.countTable("learning_objects");
		this.cantTopics = ConsultasRead.getCountTopics();
		this.cantOAsCat = OADAO.countOAsCat();
		this.cantOAsNoCat = OADAO.countOAsNoCat();
	}

	public int getCantUsers() {
		return cantUsers;
	}

	public void setCantUsers(int cantUsers) {
		this.cantUsers = cantUsers;
	}

	public int getCantOAs() {
		return cantOAs;
	}

	public void setCantOAs(int cantOAs) {
		this.cantOAs = cantOAs;
	}

	public String getCantTopics() {
		return cantTopics;
	}

	public void setCantTopics(String cantTopics) {
		this.cantTopics = cantTopics;
	}

	public int getCantOAsCat() {
		return cantOAsCat;
	}

	public void setCantOAsCat(int cantOAsCat) {
		this.cantOAsCat = cantOAsCat;
	}

	public int getCantOAsNoCat() {
		return cantOAsNoCat;
	}

	public void setCantOAsNoCat(int cantOAsNoCat) {
		this.cantOAsNoCat = cantOAsNoCat;
	}
}

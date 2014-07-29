package beans;

import javax.faces.bean.ManagedBean;

@ManagedBean(name = "navigationBean")

public class NavigationBean {
	
	public String goHome(){
		return "home";
	}
	
	public String goEditAccount(){
		return "editAccount";
	}
	
	public String goAccountManager(){
		return "accountManager";
	}
	
	public String goTopicsManager(){
		return "topicsManager";
	}
	
	public String goOaAccount(){
		return "oaAccount";
	}

}

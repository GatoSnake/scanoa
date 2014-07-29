package util;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "oaBean")
@SessionScoped

public class OABean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String titulo;
	private String fecha_ingreso;
	private String usuario;
	private String descripcion;
	private String url;
	private int id;
	private List<TopicBean> topicos;
	private List<TopicBean> rankTopicos;
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getFecha_ingreso() {
		return fecha_ingreso;
	}
	public void setFecha_ingreso(String fecha_ingreso) {
		this.fecha_ingreso = fecha_ingreso;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<TopicBean> getTopicos() {
		return topicos;
	}
	public void setTopicos(List<TopicBean> topicos) {
		this.topicos = topicos;
	}
	public List<TopicBean> getRankTopicos() {
		return rankTopicos;
	}
	public void setRankTopicos(List<TopicBean> rankTopicos) {
		this.rankTopicos = rankTopicos;
	}

}

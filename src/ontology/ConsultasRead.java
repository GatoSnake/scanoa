package ontology;

import java.util.ArrayList;
import java.util.List;

import util.TopicBean;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ConsultasRead {
	
	static private String prefix_rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	static private String prefix_rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	static private String prefix_skos = "http://www.w3.org/2004/02/skos/core#";
	static private String prefix_ContCompTI = "http://www.toeska.cl/ns/contentcompass/cc-ontology/ti/v1.0#";
	static private String uri_topico = "http://www.toeska.cl/ns/contentcompass/cc-ontology/ti/v1.0/informatica/topicos.rdf#";
	
	//metodo que se obtienen todos los topicos
	public static List<TopicBean> getAllTopics(){
//		System.out.println("en metodo --> allTopics()");
	   	List<TopicBean> topics = new ArrayList<TopicBean>();
	    Dataset dataset = ConexionOntology.ConnectModelForRead();
	    try{
	    	String q = "PREFIX rdf: <"+prefix_rdf+">\n"
		    		+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
		    		+ "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
		    		+ "SELECT (STRAFTER(str(?categoria), '#') as ?subject) (str(?label) as ?object) \n"
					+ "WHERE { \n"
		    		+ " ?categoria rdf:type ContCompTI:Category . \n"
		    		+ " ?categoria rdfs:label ?label . \n"
		    		+ "}";
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
		    ResultSet results = qexec.execSelect();
		    try{
			    while (results.hasNext()){
			    	QuerySolution soln = results.nextSolution();
			    	RDFNode name = soln.get("subject");
			    	RDFNode label = soln.get("object");
			    	TopicBean topic = new TopicBean(name.toString(),label.toString(),0);
			    	topics.add(topic);
			    }
		    }finally{
				   qexec.close();
			}
	    }finally{
	    	dataset.end();
	    }
	    return topics;
	}
	
	//metodo que se buscan los topicos generales y se identifican por su propiedad EsPadreGeneral a true
	public static void searchGeneralTopics(List<TopicBean> listTopics){
//		System.out.println("en metodo --> generalTopics(listTopics)");
		Dataset dataset = ConexionOntology.ConnectModelForRead();
		try{
			for (TopicBean itemList : listTopics) {
				String q = "PREFIX skos: <"+prefix_skos+">\n"
						+ "SELECT (STRAFTER(str(?padre), '#') as ?subject) \n"
						+ "WHERE { \n"
						+ " ?padre skos:narrower <"+uri_topico+itemList.getTopico()+"> \n"
						+ "}";
				QueryExecution qexec = QueryExecutionFactory.create(q, dataset);
				ResultSet results = qexec.execSelect();
				try{
					if (!results.hasNext()){
						String propiedad = "ContCompTI:EsPadreGeneral";
						String deleteValue = "false";
						String insertValue = "true";
						ConsultasWrite.updateProperty(propiedad,itemList.getTopico(),deleteValue,insertValue);
					}
				}finally{
					qexec.close();
				}
			}
		}finally{	
			dataset.end();
		}
	}
	
	//metodo que se obtienen los padres generales
	public static List<TopicBean> getTopicsGeneral(){
//		System.out.println("en metodo --> getTopicsGeneral()");
		List<TopicBean> topics = new ArrayList<TopicBean>();
		Dataset dataset = ConexionOntology.ConnectModelForRead();
		try{
			String q = "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
					+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
					+ "SELECT (STRAFTER(str(?categoria), '#') as ?subject) (str(?label) as ?object) \n"
					+ "WHERE { \n"
					+ " ?categoria ContCompTI:EsPadreGeneral true . \n"
		    		+ " ?categoria rdfs:label ?label . \n"
		    		+ "}";
		    QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		while (results.hasNext()){
	        		QuerySolution soln = results.nextSolution();
	    	    	RDFNode subject = soln.get("subject");
	    	    	RDFNode object = soln.get("object");
	    	    	TopicBean topic = new TopicBean(subject.toString(),object.toString(),0);
			    	topics.add(topic);
	        	}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return topics;
	}
	
	//metodo que se obtienen los topicos hijos de un topico
	public static List<TopicBean> getChildsTopics(String fatherTopic){
		List<TopicBean> topics = new ArrayList<TopicBean>();
		Dataset dataset = ConexionOntology.ConnectModelForRead();
	    try{
	    	String q = "PREFIX skos: <"+prefix_skos+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "SELECT (STRAFTER(str(?hijo), '#') as ?subject) (str(?label) as ?object) \n"
					+ "WHERE { \n"
		    		+ " <"+uri_topico+fatherTopic+"> skos:narrower ?hijo . \n"
		    		+ " ?hijo rdfs:label ?label . \n"
		    		+ " ?hijo rdf:type ContCompTI:Category"
		    		+ "}";
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		while (results.hasNext()){
	    			QuerySolution soln = results.nextSolution();
	    	    	RDFNode subject = soln.get("subject");
	    	    	RDFNode object = soln.get("object");
	    	    	TopicBean topic = new TopicBean(subject.toString(),object.toString(),0);
			    	topics.add(topic);
	    		}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return topics;
	}
	
	//metodo que confirma si es un topico
	public static boolean confirmTopic(String topico){
		boolean flag = false;
		Dataset dataset = ConexionOntology.ConnectModelForRead();
	    try{
	    	String q = "SELECT * \n"
					+ "WHERE { \n"
		    		+ " <"+uri_topico+topico+"> ?a ?b . \n"
		    		+ "}";
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		if (results.hasNext()){
	    			flag=true;
	    		}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return flag;
	}
	
	//metodo en el que se obtiene el topico padre de un topico
	public static String getTopicFather(String topico){
		Dataset dataset = ConexionOntology.ConnectModelForRead();
	    String padre = null;
	    try{
	    	String q = "PREFIX skos: <"+prefix_skos+">\n"
	    			+ "SELECT (STRAFTER(str(?padre), '#') as ?subject) \n"
					+ "WHERE { \n"
		    		+ " ?padre skos:narrower <"+uri_topico+topico+"> . \n"
		    		+ "}";
//	    	System.out.println(q);
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		if (results.hasNext()){
	    			QuerySolution soln = results.nextSolution();
	    	    	RDFNode subject = soln.get("subject");
	    	    	padre = subject.toString();
	    		}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return padre;
	}
	
	//metodo en el que se obtiene los topicos padres de un topico
	public static List<String> getTopicsFathers(String topico){
		Dataset dataset = ConexionOntology.ConnectModelForRead();
	    List<String> padres = new ArrayList<String>();
	    try{
	    	String q = "PREFIX skos: <"+prefix_skos+">\n"
	    			+ "SELECT (STRAFTER(str(?padre), '#') as ?subject) \n"
					+ "WHERE { \n"
		    		+ " ?padre skos:narrower <"+uri_topico+topico+"> . \n"
		    		+ "}";
//		    	System.out.println(q);
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		while (results.hasNext()){
	    			QuerySolution soln = results.nextSolution();
	    	    	RDFNode subject = soln.get("subject");
	    	    	padres.add(subject.toString());
	    		}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return padres;
	}
	
	//metodo en el que se obtiene los valores de una propiedad de un topico
	public static List<String> getValuesOfPropertyLiteral(String topico, String property){
		Dataset dataset = ConexionOntology.ConnectModelForRead();
	    List<String> valores = new ArrayList<String>();
	    try{
	    	String q = "PREFIX skos: <"+prefix_skos+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "SELECT (str(?a) as ?object) \n"
					+ "WHERE { \n"
		    		+ " <"+uri_topico+topico+"> "+property+" ?a . \n"
		    		+ "}";
//	    	System.out.println(q);
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		while (results.hasNext()){
	    			QuerySolution soln = results.nextSolution();
	    	    	RDFNode subject = soln.get("object");
	    	    	valores.add(subject.toString());
	    		}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return valores;
	}
	
	//metodo en el que se obtiene el topico a traves del label
	public static String getTopicForLabel(String label){
		Dataset dataset = ConexionOntology.ConnectModelForRead();
	    String topico = null;
	    try{
	    	String q = "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "SELECT (STRAFTER(str(?topico), '#') as ?subject) \n"
					+ "WHERE { \n"
		    		+ " ?topico rdfs:label ?label . \n"
		    		+ "FILTER (STR(?label)='"+label+"') "
		    		+ "}";
//	    	System.out.println(q);
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		if (results.hasNext()){
	    			QuerySolution soln = results.nextSolution();
	    	    	RDFNode subject = soln.get("subject");
	    	    	topico = subject.toString();
	    		}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return topico;
	}

	//metodo en el que se obtiene los valores de una propiedad de un topico
	public static List<TopicBean> getValuesByIdOA(int id){
		String idOA = Integer.toString(id);
		Dataset dataset = ConexionOntology.ConnectModelForRead();
	    List<TopicBean> topicosCategorizados = new ArrayList<TopicBean>();
	    try{
	    	String q = "PREFIX skos: <"+prefix_skos+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "SELECT (STRAFTER(str(?a), '#') as ?subject) (str(?label) as ?object) ?b \n"
					+ "WHERE { \n"
		    		+ " ?a ContCompTI:topicIsReferencedBy ?b . \n"
					+ " ?a rdfs:label ?label . \n"
		    		+ " ?a rdf:type ContCompTI:Category . \n"
		    		+ " FILTER (REGEX(STR(?b), '[A-Za-z]"+idOA+"$', 'i'))"
		    		+ "}";
//		    System.out.println(q);
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		while (results.hasNext()){
	    			QuerySolution soln = results.nextSolution();
	    			RDFNode subject = soln.get("subject");
	    	    	RDFNode object = soln.get("object");
	    	    	topicosCategorizados.add(new TopicBean(subject.toString(),object.toString(),0));
	    		}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return topicosCategorizados;
	}
	
	//metodo en el que se obtienen los oas categorizados de cada topico
	public static List<String> getOAsOfTopic(String topico){
		Dataset dataset = ConexionOntology.ConnectModelForRead();
	    List<String> oas = new ArrayList<String>();
	    try{
	    	String q = "PREFIX skos: <"+prefix_skos+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "SELECT ?oas \n"
					+ "WHERE { \n"
		    		+ " <"+uri_topico+topico+"> ContCompTI:topicIsReferencedBy ?oas . \n"
		    		+ "}";
//		    System.out.println(q);
	    	QueryExecution qexec = QueryExecutionFactory.create(q,dataset);
	    	ResultSet results = qexec.execSelect();
	    	try{
	    		while (results.hasNext()){
	    			QuerySolution soln = results.nextSolution();
	    			RDFNode subject = soln.get("oas");
//	    	    	RDFNode object = soln.get("oas");
	    			oas.add(subject.toString());
	    		}
	    	}finally{
	    		qexec.close();
	    	}
	    }finally{
	    	dataset.end();
	    }
	    return oas;
	}
	
}

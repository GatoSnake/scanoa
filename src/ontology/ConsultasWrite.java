package ontology;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class ConsultasWrite {
	
	static private String prefix_rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	static private String prefix_rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	static private String prefix_skos = "http://www.w3.org/2004/02/skos/core#";
	static private String prefix_ContCompTI = "http://www.toeska.cl/ns/contentcompass/cc-ontology/ti/v1.0#";
	static private String uri_topico = "http://www.toeska.cl/ns/contentcompass/cc-ontology/ti/v1.0/informatica/topicos.rdf#";
	
	public static void addNewTopic(String nuevoTopico, String label){
//		System.out.println("en metodo --> addNewTopic() to "+nuevoTopico);
		Dataset dataset = ConexionOntology.ConnectModelForWrite();
		try{
	    	GraphStore graphStore = GraphStoreFactory.create(dataset) ;
	    	String q = "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX skos: <"+prefix_skos+">\n"
	    			+ "INSERT DATA { \n"
	    			+ " <"+uri_topico+nuevoTopico+"> rdf:type ContCompTI:Category ;\n"
	        		+ " rdfs:label '"+label+"' ; \n"
	        		+ " ContCompTI:EsPadreGeneral true . \n"
	        		+ "}";
//	    	System.out.println(q);
	    	UpdateRequest request = UpdateFactory.create(q);
	        UpdateProcessor proc = UpdateExecutionFactory.create(request,graphStore);
	        proc.execute();
	        dataset.commit();
		}finally{
	    	dataset.end();
	    }
	}
	
	public static void addTopicToFather(String topico, String nuevoTopico, String label){
//		System.out.println("en metodo --> addTopicToFather() to "+topico+" and "+nuevoTopico);
		Dataset dataset = ConexionOntology.ConnectModelForWrite();
	    try{
	    	GraphStore graphStore = GraphStoreFactory.create(dataset) ;
	    	String q = "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX skos: <"+prefix_skos+">\n"
	    			+ "INSERT DATA { \n"
	    			+ " <"+uri_topico+nuevoTopico+"> rdf:type ContCompTI:Category ;\n"
	        		+ " rdfs:label '"+label+"' ; \n"
	        		+ " ContCompTI:EsPadreGeneral false . \n"
	        		+ " <"+uri_topico+topico+"> skos:narrower <"+uri_topico+nuevoTopico+"> . \n"
	        		+ "}";
//	    	System.out.println(q);
	    	UpdateRequest request = UpdateFactory.create(q);
	        UpdateProcessor proc = UpdateExecutionFactory.create(request,graphStore);
	        proc.execute();
	        dataset.commit();
	    }finally{
	    	dataset.end();
	    }
	}

	//metodo que elimina un topico
	public static void deleteTopic(String topico){
//		System.out.println("en metodo --> deleteTopic() to "+topico);
	    Dataset dataset = ConexionOntology.ConnectModelForWrite();
	    try{
	        GraphStore graphStore = GraphStoreFactory.create(dataset) ;
	        String q = "DELETE WHERE { <"+uri_topico+topico+"> ?p ?o } ;\n"
	        		+ "DELETE WHERE { ?s <"+uri_topico+topico+"> ?o } ;\n"
	        		+ "DELETE WHERE { ?s ?p <"+uri_topico+topico+"> }";
	    	UpdateRequest request = UpdateFactory.create(q);
	        UpdateProcessor proc = UpdateExecutionFactory.create(request,graphStore);
	        proc.execute();
	        dataset.commit();
	    }finally{
	    	dataset.end();
	    }
	}

	//metodo que inserta un valor de una propiedad de un topico
	public static void insertPropertyNarrower(String propiedad, String topico, String topicoHijo){
//		System.out.println("en metodo --> insertProperty()");
	    Dataset dataset = ConexionOntology.ConnectModelForWrite();
	    try{
	        GraphStore graphStore = GraphStoreFactory.create(dataset) ;
	        String q = "PREFIX skos: <"+prefix_skos+">\n"
	        		+ "INSERT DATA {"
	    			+ "<"+uri_topico+topico+"> "+propiedad+" <"+uri_topico+topicoHijo+"> ."
	        		+ "}";
//	        System.out.println(q);
	    	UpdateRequest request = UpdateFactory.create(q);
	        UpdateProcessor proc = UpdateExecutionFactory.create(request,graphStore);
	        proc.execute();
	        dataset.commit();
	    }finally{
	    	dataset.end();
	    }
	}
	
	//metodo que elimina un valor de una propiedad de un topico
	public static void deletePropertyNarrower(String propiedad, String topico, String topicoHijo){
//		System.out.println("en metodo --> deleteProperty()");
	    Dataset dataset = ConexionOntology.ConnectModelForWrite();
	    try{
	        GraphStore graphStore = GraphStoreFactory.create(dataset) ;
	        String q = "PREFIX skos: <"+prefix_skos+">\n"
	        		+ "DELETE DATA {"
	    			+ "<"+uri_topico+topico+"> "+propiedad+" <"+uri_topico+topicoHijo+"> ."
	        		+ "}";
//	        System.out.println(q);
	    	UpdateRequest request = UpdateFactory.create(q);
	        UpdateProcessor proc = UpdateExecutionFactory.create(request,graphStore);
	        proc.execute();
	        dataset.commit();
	    }finally{
	    	dataset.end();
	    }
	}

	//metodo que ingresa el valor de una propiedad de un topico
	public static void insertProperty(String propiedad, String topico, String value){
//		System.out.println("en metodo --> insertProperty()");
	    Dataset dataset = ConexionOntology.ConnectModelForWrite();
	    try{
	        GraphStore graphStore = GraphStoreFactory.create(dataset) ;
	        String q2 = "PREFIX skos: <"+prefix_skos+">\n"
	        		+ "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	        		+ "INSERT DATA {"
	    			+ "<"+uri_topico+topico+"> "+propiedad+" "+value+" ."
	        		+ "}";
//	        System.out.println(q2);
	    	UpdateRequest request = UpdateFactory.create(q2);
	        UpdateProcessor proc = UpdateExecutionFactory.create(request,graphStore);
	        proc.execute();
	        dataset.commit();
	    }finally{
	    	dataset.end();
	    }
	}

	//metodo que borra el valor de una propiedad de un topico
	public static void deleteProperty(String propiedad, String topico, String value){
//		System.out.println("en metodo --> insertProperty()");
	    Dataset dataset = ConexionOntology.ConnectModelForWrite();
	    try{
	        GraphStore graphStore = GraphStoreFactory.create(dataset) ;
	        String q = "PREFIX skos: <"+prefix_skos+">\n"
	        		+ "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	        		+ "DELETE DATA {"
	    			+ "<"+uri_topico+topico+"> "+propiedad+" "+value+" ."
	        		+ "}";
//	        System.out.println(q);
	    	UpdateRequest request = UpdateFactory.create(q);
	        UpdateProcessor proc = UpdateExecutionFactory.create(request,graphStore);
	        proc.execute();
	        dataset.commit();
	    }finally{
	    	dataset.end();
	    }
	}

	//metodo que actualiza el valor de una propiedad de un topico
	public static void updateProperty(String propiedad, String topico, String lastValue, String newValue){
//		System.out.println("en metodo --> updateProperty() to "+topico);
	    Dataset dataset = ConexionOntology.ConnectModelForWrite();
	    try{
	        GraphStore graphStore = GraphStoreFactory.create(dataset) ;
	        String q = "PREFIX ContCompTI: <"+prefix_ContCompTI+">\n"
	    			+ "PREFIX rdfs: <"+prefix_rdfs+">\n"
	    			+ "PREFIX rdf: <"+prefix_rdf+">\n"
	    			+ "PREFIX skos: <"+prefix_skos+">\n"
	        		+ "DELETE DATA {"
	    			+ " <"+uri_topico+topico+"> "+propiedad+" "+lastValue+" . "
	        		+ "};\n"
	        		+ "INSERT DATA {"
	        		+ " <"+uri_topico+topico+"> "+propiedad+" "+newValue+" . "
	        		+ "};";
//	        System.out.println(q);
	    	UpdateRequest request = UpdateFactory.create(q);
	        UpdateProcessor proc = UpdateExecutionFactory.create(request,graphStore);
	        proc.execute();
	        dataset.commit();
	    }finally{
	    	dataset.end();
	    }
	}
	
}

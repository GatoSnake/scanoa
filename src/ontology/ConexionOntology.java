package ontology;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

import dao.GenericDAO;
import util.TopicBean;

public class ConexionOntology {
	
	static private String directory = "C:\\Users\\Cristhian\\workspace\\scanoa\\ontologia\\tbd";
	static private String rdf= "C:\\Users\\Cristhian\\workspace\\scanoa\\ontologia\\topicos.rdf";
	
	//metodo de conexion al modelo para lectura
	public static Dataset ConnectModelForRead(){
		Dataset dataset = TDBFactory.createDataset(directory);
	    dataset.begin(ReadWrite.READ);
	    return dataset;
	}
	
	//metodo de conexion al modelo para escritura
	public static Dataset ConnectModelForWrite(){
		Dataset dataset = TDBFactory.createDataset(directory);
	    dataset.begin(ReadWrite.WRITE);
	    return dataset;
	}
	
	//metodo que confirma si el rdf se ha modificado y de la existencia del modelo
	public static void confirmarModelo(){
		System.out.println("en metodo --> confirmarModelo()");
//		List<TopicBean> topicsGeneral = new ArrayList<TopicBean>();
		boolean rdfModified = getRDFLastModified(rdf);
		File dir = new File(directory);
		if(rdfModified){
			deleteDir(dir);
			doModel();
			List<TopicBean> topics = ConsultasRead.getAllTopics();
			ConsultasRead.searchGeneralTopics(topics);
		}else{
			if(!dir.exists()){
				doModel();
				List<TopicBean> topics = ConsultasRead.getAllTopics();
				ConsultasRead.searchGeneralTopics(topics);
			}		
		}
//		topicsGeneral = ConsultasRead.getTopicsGeneral();
//		return topicsGeneral;
	}
	
	//metodo que crea el modelo
	public static void doModel(){
		System.out.println("en metodo --> doModel()");
		Dataset dataset = TDBFactory.createDataset(directory);
		Model tdb = dataset.getDefaultModel();
		// read the input file
		FileManager.get().readModel( tdb, rdf);
		tdb.close();
		dataset.close();
	}
	
	//metodo que analiza si el rdf se ha modificado
	public static boolean getRDFLastModified(String rdf){
		System.out.println("en metodo --> getRDFLastModified()");
		File file = new File(rdf);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fecha_archivo = sdf.format(file.lastModified());
		String fecha = GenericDAO.genericSelectLastRowID("log_rdf");
		if(fecha.equals(fecha_archivo)){
			return false;
		}else{
			GenericDAO.genericInsertDate("log_rdf","fecha_archivo",fecha_archivo);
			return true;
		}
		
	}
		
	//metodo que elimina la carpeta del modelo
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}

}

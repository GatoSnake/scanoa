package categorization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import beans.Util;
import ontology.ConsultasRead;
import ontology.ConsultasWrite;
import util.OABean;
import util.TopicBean;
import dao.GenericDAO;

public class Categorization {
	
	public static Integer processCategorization(String URL, String id, OABean oa, int operacion){
		//resultados a retornar
		//1- si no se pudo descargar el OA
		//2- si no se pudo extraer el texto (esto es por posibles errores de lectura)
		//3- si el OA paso el proceso de categorizacion pero no cayo en ningun tópico
		//4- si el OA paso el proceso de categorizacion y pertenece a 1 o mas topicos
		Boolean resultDownload;
    	String texto = null;
    	String randomNameFile = Util.getRandomHexString(64);
    	String formatFile = URL.substring(URL.lastIndexOf(".") + 1, URL.length()).toLowerCase();
    	if(formatFile.endsWith("pdf")){
    		resultDownload = DownloadOAs.downloadOA(URL,randomNameFile+".pdf");
    		if(resultDownload) texto = TextExtractionPDF.extractionTextPDF(randomNameFile+".pdf");
    		else return 1;
    		Util.deleteFile(Util.dir_oas+randomNameFile+".pdf");    	
    	}
    	else if(formatFile.endsWith("doc")){
    		resultDownload = DownloadOAs.downloadOA(URL,randomNameFile+".doc");
    		if(resultDownload) texto = TextExtractionOffice.readDoc(randomNameFile+".doc");
    		else return 1;
    		Util.deleteFile(Util.dir_oas+randomNameFile+".doc");   
    	}
    	else if(formatFile.endsWith("docx")){
    		resultDownload = DownloadOAs.downloadOA(URL,randomNameFile+".docx");
    		if(resultDownload) texto = TextExtractionOffice.readDocx(randomNameFile+".docx");
    		else return 1;
    		Util.deleteFile(Util.dir_oas+randomNameFile+".docx");   
    	}
    	if(texto!=null){
    		System.out.println("Comienza el proceso de categorizacion!");
    		//normalizamos el texto
    		texto = TextCovering.normalizationText(texto);
    		//obtenemos todos los topicos de la ontologia y lo agregamos
    		List<TopicBean> topicosOntology = ConsultasRead.getAllTopics();
    		//una lista de los topicos normalizadas
    		List<String> topicos = new ArrayList<String>();
    		for(TopicBean topic : topicosOntology){
    			String temp = topic.getLabel().toLowerCase();
    			temp = temp.replaceAll("\\s+", " ");
    			temp = temp.trim();
    			topicos.add(temp);
    		}
    		//ejecutamos el algoritmo Text Covering para obtener la lista de topicos categorizados del OA
    		Map<List<String>, Double> categorizados = TextCovering.getTextCovering(texto,topicos);
    		//si en el proceso de categorizacion el OA no pertenece a ningun topico, entonces aca termina este metodo
    		if(categorizados.isEmpty()) return 3;
    		
    		List<String> menoresTopicos = TextCovering.getKeyListFromValue(categorizados, Collections.min(categorizados.values()));
    		System.out.println("*************** CATEGORIZADOS ***************");
    		System.out.println(menoresTopicos+" - "+Collections.min(categorizados.values()));
    		
    		//obtenemos los topicos id clasificados
    		List<String> topicClasified = new ArrayList<String>();
    		for(TopicBean topico : topicosOntology){
    			String labelTopico = topico.getLabel();
    			labelTopico = labelTopico.toLowerCase();
    			labelTopico = labelTopico.replaceAll("\\s+", " ");
    			labelTopico = labelTopico.trim();
    			for(String elegido : menoresTopicos){
    				if(labelTopico.equals(elegido)){
    					topicClasified.add(topico.getTopico());
    				}
    			}
    		}
    		
    		//ejecutamos el algoritmo de relevancia de topicos a nivel jerarquico de la ontologia
    		List<Entry<String, Double>> resRelev = TextCovering.topicRelevance(texto,menoresTopicos,topicos,topicClasified);
    		List<String> listRank = new ArrayList<String>();
    		for(Map.Entry<String, Double> entry : resRelev){
    			listRank.add(entry.getKey());
    		}
    		System.out.println("*************** RANKING ASC ***************");
    		//obtenemos la lista de los topicos (label) ordenados segun su relevancia
    		List<String> listTopicRanking = new ArrayList<String>();
    		for(String elegido : listRank){
    			for(TopicBean topico : topicosOntology){
	    			String labelTopico = topico.getLabel();
	    			labelTopico = labelTopico.toLowerCase();
	    			labelTopico = labelTopico.replaceAll("\\s+", " ");
	    			labelTopico = labelTopico.trim();
    				if(labelTopico.equals(elegido)){
    					String saveLabel = topico.getLabel().replaceAll("\\s+", " ");
    					saveLabel = saveLabel.trim();
    					listTopicRanking.add(saveLabel);
    				}
    			}
    		}
    		System.out.println(listTopicRanking);
    		
    		//aqui agregamos en una linea todos los topicos obtenidos para ingresarlos a la bd
    		String rankTopic = "";
    		for(String itemRank : listTopicRanking){
    			rankTopic = rankTopic+itemRank+";";
    		}
    		if(!rankTopic.equals("")){
    			GenericDAO.stringUpdateWhereString("learning_objects", "ranking_topicos", rankTopic, "id", id);
    		}
    		//esto es en caso de actualizar un OA, la cual se obtienen los topicos que ya tenia categorizados y se eliminan de la ontologia
    		if(operacion==2){
    			oa.setTopicos(ConsultasRead.getValuesByIdOA(oa.getId()));
    			List<TopicBean> oaTopics = oa.getTopicos();
    			for(TopicBean topic : oaTopics){
    				String value = topic.getTopico()+"OA"+oa.getId();
    				ConsultasWrite.deletePropertyValueString("ContCompTI:topicIsReferencedBy",topic.getTopico(),value);
    			}
    		}
    		//aqui agregamos a la ontologia los topicos categorizados perteneciente al OA
    		for(String topico : topicClasified){
    			String nameOA = topico+"OA"+id;
    			ConsultasWrite.insertPropertyValueString("ContCompTI:topicIsReferencedBy", topico, nameOA);
    		}
    		return 4;
    	}
    	return 2;
	}

}

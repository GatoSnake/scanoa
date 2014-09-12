package categorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ontology.ConsultasRead;
import util.TopicBean;

public class TextCovering {

	public static String normalizationText(String texto){
		//Normalizando texto
		texto = texto.replaceAll(",", " ");
		texto = texto.replaceAll(":", " ");
		texto = texto.replaceAll("/", " ");
		texto = texto.replaceAll("-", " ");
		texto = texto.replaceAll("\"", " ");
		texto = texto.replaceAll("\\+", " ");
		texto = texto.replaceAll("\\=", " ");
		texto = texto.replaceAll("\\(", " ");
		texto = texto.replaceAll("\\)", " ");
		texto = texto.replaceAll("\\¿", " ");
		texto = texto.replaceAll("\\?", " ");
		texto = texto.replaceAll("\\¡", " ");
		texto = texto.replaceAll("\\!", " ");
		texto = texto.replaceAll("\\{", " ");
		texto = texto.replaceAll("\\}", " ");
		texto = texto.replaceAll("\\[", " ");
		texto = texto.replaceAll("\\]", " ");
		texto = texto.replaceAll("\\“", " ");
		texto = texto.replaceAll("\\”", " ");
		texto = texto.replaceAll("\\”", " ");
		texto = texto.replaceAll("\\s+", " ");
		texto = texto.toLowerCase();		
		return texto;	
	}

	public static String getKeyFromValue(Map<?, ?> hm, Object value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return (String) o;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getKeyListFromValue(Map<?, ?> hm, Object value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return (List<String>) o;
			}
		}
		return null;
	}

	public static double getKLDiv(Map<String, Double> _s, Map<String, Double> _t){
//		System.out.println(_s);
//		System.out.println(_t);
		
		if(_s.size() == 0){
			return 1e33;
		}
		if(_t.size() == 0){
			return 1e33;
		}
		
		//esto es como el ssum = 0. + sum(_s.values())
		Double sum = 0.0;
		for(Double item : _s.values()){
			sum = sum + item;
		}
		Double ssum = 0. + sum;
		
		//esto es como el tsum = 0. + sum(_t.values())
		sum = 0.0;
		for(Double item : _t.values()){
			sum = sum + item;
		}
		Double tsum = 0. + sum;		
//		int lenvocabdiff = 0;
		
		//epsilon
		Double min_s = Collections.min(_s.values());
		Double min_t = Collections.min(_t.values());
		double epsilon = Math.min(min_s/ssum, min_t/tsum * 0.001);
		
		//gamma
		double gamma = 1 - 0 * epsilon;
		
		//checkea si la distribuciones de probabilidad suman igual a 1
		Double sc = 0.0;
		for(Double item : _s.values()){
			sc = sc + item/ssum;
		}
		Double st = 0.0;
		for(Double item : _t.values()){
			st = st + item/tsum;
		}
		
		if( sc < 9e-6){
			System.out.println( "Sum P: "+sc+", Sum Q: "+st);
			System.out.println("*** ERROR: sc does not sum up to 1. Bailing out ..");
			System.exit(2);
		}
		if( st < 9e-6){
			System.out.println( "Sum P: "+sc+", Sum Q: "+st);
			System.out.println("*** ERROR: st does not sum up to 1. Bailing out ..");
			System.exit(2);
		}
		
		Double div = 0.;
		for (Map.Entry<String, Double> entry : _s.entrySet()){
		    double pts = entry.getValue() / ssum ;
		    
		    double ptt = epsilon;
		    if(_t.containsKey(entry.getKey())){
		    	ptt = gamma * (_t.get(entry.getKey())/tsum );
		    }
		    
		    double ck1 = (pts - ptt) * Math.log(pts / ptt);
		    div += ck1;
		}

		return div;
	}
	
	public static Map<List<String>, Double> getTextCovering(String doc, List<String> topicos){
//		System.out.println("en metodo --> getTextCovering()");
		
		Map<String, Double> frequencyTopics = new HashMap<String, Double>();
		Map<String, Double> probTopics = new HashMap<String, Double>();
		
		for(String topico : topicos){
			if(doc.contains(topico)){
				Map<String, Double> frecTopic = getFrequencyTopic(topico,doc);
				frequencyTopics.put(topico,frecTopic.get(topico));
				probTopics.put(topico, 1.0);
			}
		}
		
		Map<String, Double> dicKL = new HashMap<String, Double>();
		Map<List<String>, Double> listElegidos = new HashMap<List<String>, Double>();
		List<String> elegidos = new ArrayList<String>();
		
		int i = 0;
		while(i < frequencyTopics.size()){
			for(Map.Entry<String, Double> entry : frequencyTopics.entrySet()){

				if( elegidos.contains(entry.getKey()) ){
					continue;
				}
				else{
					List<String> topic = new ArrayList<String>(elegidos);
					topic.add(entry.getKey());

					Map<String, Double> probTopic = new HashMap<String, Double>();
					for(String item : topic){
						probTopic.put(item, probTopics.get(item));
					}
					dicKL.put(entry.getKey(), getKLDiv(probTopic,frequencyTopics));

					topic.clear();
				}
			}
			
			String minKey = getKeyFromValue(dicKL,Collections.min(dicKL.values()));
			Double minValue = Collections.min(dicKL.values());
			
//			System.out.println("MENOR KL: "+elegidos+" "+minKey+" - "+minValue);
			
			if(listElegidos.size() == 0){
				List<String> temp = new ArrayList<String>(elegidos);
				temp.add(minKey);
				listElegidos.put(temp, minValue);
				elegidos.add(minKey);
			}
			else{
				List<String> menorKLElegido = getKeyListFromValue(listElegidos,Collections.min(listElegidos.values()));
				if(dicKL.get(minKey) <= listElegidos.get(menorKLElegido) ){
					List<String> temp = new ArrayList<String>(elegidos);
					temp.add(minKey);
					listElegidos.put(temp, minValue);
					elegidos.add(minKey);
				}
				else{
					break;
				}
			}
			dicKL.clear();
			i++;
		}		
//		System.out.println(frequencyTopics);
		return listElegidos;
	}
	
	public static List<Entry<String, Double>> topicRelevance(String texto, List<String> menoresTopicos, List<String> topicos, List<String> topicClasified){
		Map<String, Double> frequencyTopics = new HashMap<String, Double>();
		Map<String, Double> probTopics = new HashMap<String, Double>();
		
		for(String topico : topicos){
			if(texto.contains(topico)){
				Map<String, Double> frecTopic = getFrequencyTopic(topico,texto);
				frequencyTopics.put(topico,frecTopic.get(topico));
				probTopics.put(topico, 1.0);
			}
		}
		
		Map<String, Double> listKL = new HashMap<String, Double>();
		Map<String, Double> dicKL = new HashMap<String, Double>();
		
		for(String topicFather : topicClasified){
			List<String> label = ConsultasRead.getValuesOfPropertyLiteral(topicFather, "rdfs:label");
			String temp = label.get(0).toLowerCase();
			temp = temp.replaceAll("\\s+", " ");
			temp = temp.trim();
			List<String> hijos = hasChildInOA(topicFather,frequencyTopics);
			String father = null;
			if(hijos.size()>0){
				Map<String, Double> probTopic = new HashMap<String, Double>();
				for(Map.Entry<String, Double> entry : probTopics.entrySet()){
					if(entry.getKey().equals(temp)){
//						System.out.println("-------"+temp+"-------");
						father = entry.getKey();
						probTopic.put(entry.getKey(), entry.getValue());
						dicKL.put(entry.getKey(), getKLDiv(probTopic,frequencyTopics));
						break;
					}
				}
				for(String hijo : hijos){
					for(Map.Entry<String, Double> entry : probTopics.entrySet()){
						if(entry.getKey().equals(hijo)){
							List<String> topic = new ArrayList<String>();
							topic.add(father);
							topic.add(entry.getKey());
							
							Map<String, Double> probTopic2 = new HashMap<String, Double>();
							for(String item : topic){
								probTopic2.put(item, probTopics.get(item));
							}
							dicKL.put(entry.getKey(), getKLDiv(probTopic2,frequencyTopics));
							topic.clear();
						}
					}
				}
//				String minKey = getKeyFromValue(dicKL,Collections.min(dicKL.values()));
				Double minValue = Collections.min(dicKL.values());
//				System.out.println(minKey+" - "+minValue);
				listKL.put(father, minValue);
			}else{
				double valueKL = 0;
				Map<String, Double> probTopic = new HashMap<String, Double>();
				for(Map.Entry<String, Double> entry : probTopics.entrySet()){
					if(entry.getKey().equals(temp)){
						probTopic.put(entry.getKey(), entry.getValue());
						valueKL = getKLDiv(probTopic,frequencyTopics);
					}
				}
				listKL.put(temp, valueKL);
			}
			dicKL.clear();
		}
		
//		System.out.println(listKL);
		List<Entry<String, Double>> listKLASC = entriesSortedByValues(listKL);
//		for(Map.Entry<String, Double> entry : listKLASC){
//			System.out.println(entry.getKey());
//		}
		return listKLASC;
	}
	
	public static List<String> hasChildInOA(String topicFather, Map<String, Double> frequencyTopics){
		List<String> hijos = new ArrayList<String>();
		List<TopicBean> childsTopic = ConsultasRead.getChildsTopics(topicFather);
		for(TopicBean childTopic: childsTopic){
			String temp = childTopic.getLabel().toLowerCase();
			temp = temp.replaceAll("\\s+", " ");
			temp = temp.trim();
			for(Map.Entry<String, Double> entry : frequencyTopics.entrySet()){
				if(entry.getKey().equals(temp)){
//					System.out.println(entry.getKey());
					hijos.add(entry.getKey());
//					return true;
				}
			}
		}
		return hijos;
	}
	
	public static Map<String, Double> getFrequencyTopic(String topico,String doc){
		Map<String, Double> frequencyTopic = new HashMap<String, Double>();
		frequencyTopic.put(topico, 0.0);
		
		int nWordsTopico = topico.isEmpty() ? 0 : topico.split("\\s+").length;
		int nWordsDoc = doc.isEmpty() ? 0 : doc.split("\\s+").length;
		List<String> listWords = Arrays.asList(doc.split(" "));

		for (int i = 0; i < listWords.size(); i++) {
			int j = 0;
			int pos = i;
			String temp = "";
			while(j < nWordsTopico){
				if(pos <  nWordsDoc){
					temp = temp +" "+ listWords.get(pos);
					temp = temp.trim();
				}
				pos++;
				j++;
			}
			if(topico.equals(temp)){
				frequencyTopic.put(topico, frequencyTopic.get(topico) + 1.0);
			}
		}
		return(frequencyTopic);
	}
	
	public static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {	
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
		
		Collections.sort(sortedEntries, 
		    new Comparator<Entry<K,V>>() {
		        @Override
		        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
		            return e1.getValue().compareTo(e2.getValue());
		        }
		    }
		);
		
		return sortedEntries;
	}
	
}

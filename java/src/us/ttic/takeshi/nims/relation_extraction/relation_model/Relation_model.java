/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction.relation_model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.stanford.nlp.international.arabic.process.ArabicDocumentReaderAndWriter;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.tagger.common.Tagger;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import us.ttic.takeshi.nims.relation_extraction.Factor;
import us.ttic.takeshi.nims.relation_extraction.Gold_relations;
import us.ttic.takeshi.nims.relation_extraction.Iiterator;
import us.ttic.takeshi.nims.relation_extraction.Ireader;
//import us.ttic.takeshi.nims.relation_extraction.Olson_chart;
import us.ttic.takeshi.nims.relation_extraction.Phrase;
import us.ttic.takeshi.nims.relation_extraction.Processing;
import us.ttic.takeshi.nims.relation_extraction.Property;
import us.ttic.takeshi.nims.relation_extraction.Relation;
import us.ttic.takeshi.nims.relation_extraction.Structure;
//import us.ttic.takeshi.nims.relation_extraction.factor_recgnizer.Continuous_NN;
import us.ttic.takeshi.nims.relation_extraction.factor_recgnizer.Factor_dict;
import us.ttic.takeshi.nims.relation_extraction.factor_recgnizer.Factor_map;
//import us.ttic.takeshi.nims.relation_extraction.factor_recgnizer.Ifactor_recognizer;
import us.ttic.takeshi.tools.Pair;
import us.ttic.takeshi.tools.Scored_obj;

/**
 * @author takeshi.onishi
 *
 */
public abstract class Relation_model implements Irelation_model, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5382654183609707433L;
	
	//Factors
	Factor_map dict = null;
	
	public static final String PREFIX = "rlm";
	
	//hyper parameter to split
	//public static String SPLITTERS = ",|\\(|\\)|:";
	//public static String WORD_SPLITTERS =",|\\(|\\)|:| |\\.|-";
	
	//hyper parameter to draw
	public static int MAX_FACTS = 10;
	
	//some hyper to print
	protected static final String NEWLINE_TAG = "<br>";
	protected static final String FONT_TAG = "font";
	protected static final String COLOR_TAG = "background-color";
	
	//lauguage resorce to search
	protected Ireader train_corpus = null;
	
	/**
	 * initialize with dictionaries for each factor type
	 * @param process_dict
	 * @param structure_dict
	 * @param property_dict
	 */
	public Relation_model(List<Processing> process_dict, List<Structure> structure_dict, List<Property> property_dict){
				
		dict = new Factor_dict(process_dict, structure_dict, property_dict);

//		for(Phrase p : phrase2prc.keySet()) System.out.println(p.toString());
//		for(Phrase p : phrase2str.keySet()) System.out.println(p.toString());
//		for(Phrase p : phrase2prp.keySet()) System.out.println(p.toString());
	}

	
//	@Override
//	public Olson_chart get_chart(List<Property> props){
//		return null;
//	}
//	@Override
//	public Olson_chart get_chart(List<Processing> process, List<Property> props){
//		List<Structure> str = dict.get_str(train_corpus);
//		push_relation(process, str, props);
//		
//		//add essencials
////		for(Structure s : structures){
////			boolean isConnected = false;
////			for(Processing p : process)if(!p.get_relation(s).get_relation().equals(Relation.VOID)){
////				isConnected=true; break;
////			}
////			for(Property p : props)if(!s.get_relation(p).get_relation().equals(Relation.VOID)){
////				isConnected=true;break;
////			}
////			if(isConnected){
////				structures.add(s);
////			}
////		}
//		
//		//hub strength
//		List<Structure> structures = new ArrayList<Structure>(); 
//		List<Scored_obj<Structure>> hubs =new ArrayList<Scored_obj<Structure>>();
//		for(Structure s : str){
//			double score = 0.0;
//			for(Processing p : process) score+=p.get_relation(s).get_score();
//			for(Property p : props) score+=s.get_relation(p).get_score();
//			
//			hubs.add(new Scored_obj<Structure>(s, score));
//		}
//		Collections.sort(hubs);
//		Collections.reverse(hubs);
//		for(int i=0;i<MAX_FACTS;i++) structures.add(hubs.get(i).obj);
//		
//		return new Olson_chart(process, structures, props);
//	}
//	@Override
//	public Olson_chart get_chart(List<Processing> process, List<Structure> structures, List<Property> props){
//		push_relation(process, structures, props);
//		return new Olson_chart(process, structures, props);
//	}
	public abstract void push_relation(List<Processing> process, List<Structure> structures, List<Property> props);
	
	//shared methods
	
	
	
	/**
	 * pairs of structure and property in a sentence
	 * @param assigned_str : assigned structures with null
	 * @param assigned_prp assigned property with null
	 * @return set of pairs of head and tail index of factor
	 */
	protected Collection<Pair<Pair<Integer>>> str2prp_pairs(List<Structure> assigned_str, List<Property> assigned_prp){
		List<Pair<Pair<Integer>>> pairs = new ArrayList<Pair<Pair<Integer>>>();
		
		//str->prp
		for(int sh=0;sh<assigned_str.size();sh++)if(assigned_str.get(sh)!=null){
			int st=sh;
			while(st<assigned_str.size() && assigned_str.get(sh).equals(assigned_str.get(st)))st++;
			st--;
			
			Pair<Integer> str = new Pair<Integer>(sh,st);
			sh=st+1;
			
			for(int ph=0;ph<assigned_prp.size();ph++)if(assigned_prp.get(ph)!=null){
				int pt=ph;
				while(pt<assigned_prp.size() && assigned_prp.get(ph).equals(assigned_prp.get(pt)))pt++;
				pt--;
				
				pairs.add(new Pair<Pair<Integer>>(str, new Pair<Integer>(ph,pt)));
				ph=pt+1;
			}	
		}
		
		return pairs;
	}
	/**
	 * pairs of processing and structure in a sentence
	 * @param assigned_prc : assigned processing with null
	 * @param assigned_str assigned structure with null
	 * @return set of pairs of head and tail index of factor
	 */
	protected Collection<Pair<Pair<Integer>>> prc2str_pairs(List<Processing> assigned_prc, List<Structure> assigned_str){
		List<Pair<Pair<Integer>>> pairs = new ArrayList<Pair<Pair<Integer>>>();
		
		//prc -> str
		for(int ph=0;ph<assigned_prc.size();ph++)if(assigned_prc.get(ph)!=null){
			int pt=ph;
			while(pt<assigned_prc.size() && assigned_prc.get(ph).equals(assigned_prc.get(pt)))pt++;
			pt--;
			
			Pair<Integer> prc =new Pair<Integer>(ph, pt);
			ph=pt+1;
			
			for(int sh=0;sh<assigned_str.size();sh++)if(assigned_str.get(sh)!=null){
				int st=sh;
				while(st<assigned_str.size() && assigned_str.get(sh).equals(assigned_str.get(st)))st++;
				st--;
				
				pairs.add(new Pair<Pair<Integer>>(prc, new Pair<Integer>(sh,st)));
				sh=st+1;
			}	
		}
		
		return pairs;
	}
	
//	public static void main(String[] args) throws Exception {
//		String corpus_file="/home/ikumu/workspace/material_text/ScienceDirect_mini";
//		String prc_csv = "/home/ikumu/workspace/material_text/annotations/processing.dct";
//		String str_csv = "/home/ikumu/workspace/material_text/annotations/structure.dct";
//		String prp_csv = "/home/ikumu/workspace/material_text/annotations/property.dct";
//		String relation_csv = "/home/ikumu/workspace/material_text/annotations/relation.csv";
//		String parser_file = "/home/ikumu/workspace/stanford-english-corenlp-2016-01-10-models/edu/stanford/nlp/models/pos-tagger/english-bidirectional/english-bidirectional-distsim.tagger";
//		String l_parser_file = "/home/ikumu/workspace/stanford-english-corenlp-2016-01-10-models/edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
//
//		//term recognizer
//		Tagger tagger = new MaxentTagger(parser_file);
//		LexicalizedParser parser = LexicalizedParser.loadModel(l_parser_file);
//		Ifactor_recognizer rgn = new Continuous_NN( new File(prc_csv), new File(str_csv), new File(prp_csv), tagger);
//		//rgn.obtain_factors(new Ris_reader(new File(corpus_file), parser));
//		
//		//relation model
//		Relation_model model = new Relation_model(rgn.get_processings(), rgn.get_structures(), rgn.get_properties()) {
//			
//			@Override
//			public void train(Ireader corpus, List<Processing> props, List<Structure> structures) throws Exception {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void train(Ireader corpus) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public String get_proof(Structure s, Property p) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public String get_proof(Processing p, Structure s) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public void push_relation(List<Processing> process, List<Structure> structures, List<Property> props) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
//		
//		//corpus and gold relation
//		Ireader corpus = new Ris_reader(new File(corpus_file));
//		Gold_relations golds = new Gold_relations(new File(prc_csv), new File(prp_csv), new File(str_csv), new File(relation_csv));
//		List<Processing> process = golds.get_processings();
//		List<Structure> structures = golds.get_structures();
//		
//		//counting
//		int pos_pair_prc2str = 0;
//		int neg_pair_prc2str = 0;
//		int pos_pair_str2prp = 0;
//		int neg_pair_str2prp = 0;
//		
//		Map<Processing, Integer> prc_freq_pair = new HashMap<Processing, Integer>();
//		for(Processing p : process) prc_freq_pair.put(p, 0);
//		Map<Structure, Integer> str_freq_pair = new HashMap<Structure, Integer>();
//		for(Structure s : structures) str_freq_pair.put(s, 0);
//		Map<Property, Integer> prp_freq_pair = new HashMap<Property, Integer>();
//		for(Property p : golds.get_propeties()) prp_freq_pair.put(p, 0);
//		
//		Map<Processing, Integer> prc_freq = new HashMap<Processing, Integer>();
//		for(Processing p : process) prc_freq.put(p, 0);
//		Map<Structure, Integer> str_freq = new HashMap<Structure, Integer>();
//		for(Structure s : structures) str_freq.put(s, 0);
//		Map<Property, Integer> prp_freq = new HashMap<Property, Integer>();
//		for(Property p : golds.get_propeties()) prp_freq.put(p, 0);
//		
//		
//		Iiterator<List<HasWord>> snt_itr = corpus.itr_snt();
//		while(snt_itr.hasNext()){
//			List<HasWord> snt = snt_itr.next();
//			List<Processing> prc= model.dict.assign_processing(snt);
//			List<Structure> str = model.dict.assign_structure(snt);
//			List<Property> prp =  model.dict.assign_property(snt);
//			
//			Set<Processing> prc_snt = new TreeSet<Processing>();
//			for(Processing p : prc)if(p!=null) prc_snt.add(p);
//			Set<Structure> str_snt = new TreeSet<Structure>();
//			for(Structure s : str)if(s!=null) str_snt.add(s);
//			Set<Property> prp_snt = new TreeSet<Property>();
//			for(Property p : prp)if(p!=null) prp_snt.add(p);
//			for(Processing p : prc_snt) prc_freq.put(p, prc_freq.get(p)+1);
//			for(Structure s : str_snt) str_freq.put(s, str_freq.get(s)+1);
//			for(Property p : prp_snt) prp_freq.put(p, prp_freq.get(p)+1);
//			
//			//prc -> str
//			Collection<Pair<Pair<Integer>>> prc2str = model.prc2str_pairs(prc, str);
//			for(Pair<Pair<Integer>> f_pair : prc2str){
//				Pair<Integer> p_idces = f_pair.first;
//				Pair<Integer> s_idces = f_pair.second;
//				
//				//ignore UNKNOWN factors <= in dictionary but not on training data
//				if(!process.contains(prc.get(p_idces.first)) || !structures.contains(str.get(s_idces.first))) continue;
//				
//				Processing p = process.get(process.indexOf(prc.get(p_idces.first)));
//				Structure s = str.get(s_idces.first);
//				Relation r = p.get_relation(s);
//				
//				//if(!prc_freq.containsKey(p)) prc_freq.put(p, 0);
//				prc_freq_pair.put(p, prc_freq_pair.get(p)+1);
//				//if(!str_freq.containsKey(s)) str_freq.put(s, 0);
//				str_freq_pair.put(s, str_freq_pair.get(s)+1);
//				
//				if(r.get_score()>0.5){
//					pos_pair_prc2str++;
//				}else{
//					neg_pair_prc2str++;
//				}
//			}
//			
//
//			//str->prp
//			Collection<Pair<Pair<Integer>>> str2prp = model.str2prp_pairs(str, prp);
//			for(Pair<Pair<Integer>> f_pair : str2prp){
//				Pair<Integer> s_idces = f_pair.first;
//				Pair<Integer> p_idces = f_pair.second;
//				
//				//ignore UNKNOWN factors
//				if(!structures.contains(str.get(s_idces.first))) continue;
//				
//				Structure s = structures.get(structures.indexOf(str.get(s_idces.first)));
//				Property p  = prp.get(p_idces.first);
//				Relation r = s.get_relation(p);
//				
//				//if(!str_freq.containsKey(s)) str_freq.put(s, 0);
//				str_freq_pair.put(s, str_freq_pair.get(s)+1);
//				//if(!prp_freq.containsKey(p)) prp_freq.put(p, 0);
//				prp_freq_pair.put(p, prp_freq_pair.get(p)+1);
//				if(r.get_score()>0.5){
//					pos_pair_str2prp++;
//				}else{
//					neg_pair_str2prp++;
//				}
//				
//			}
//			
//		}
//		
//		//print results
//		System.out.println("==== CORPUS ANALYSIS ====");
//		System.out.println(String.format("PRC2STR_PAIR_POS : %d", pos_pair_prc2str));
//		System.out.println(String.format("PRC2STR_PAIR_NEG : %d", neg_pair_prc2str));
//		System.out.println(String.format("STR2PRP_PAIR_POS : %d", pos_pair_str2prp));
//		System.out.println(String.format("STR2PRP_PAIR_NEG : %d", neg_pair_str2prp));
//		
//		System.out.println(String.format("TOTAL_PAIR_NEG : %d", neg_pair_prc2str+neg_pair_str2prp));
//		System.out.println(String.format("TOTAL_PAIR_POS : %d", pos_pair_prc2str+pos_pair_str2prp));
//		System.out.println(String.format("TOTAL_PAIR : %d", pos_pair_prc2str+pos_pair_str2prp+neg_pair_prc2str+neg_pair_str2prp));
//		
//		System.out.println("---- FREQ OF FACTORS IN PAIRS-----");
//		List<Scored_obj<Processing>> scored_prc = new ArrayList<Scored_obj<Processing>>();
//		for(Map.Entry<Processing, Integer> e : prc_freq_pair.entrySet()) scored_prc.add(new Scored_obj<Processing>(e.getKey(), e.getValue()));
//		Collections.sort(scored_prc);
//		Collections.reverse(scored_prc);
//		List<Scored_obj<Structure>> scored_str = new ArrayList<Scored_obj<Structure>>();
//		for(Map.Entry<Structure, Integer> e : str_freq_pair.entrySet()) scored_str.add(new Scored_obj<Structure>(e.getKey(), e.getValue()));
//		Collections.sort(scored_str);
//		Collections.reverse(scored_str);
//		List<Scored_obj<Property>> scored_prp = new ArrayList<Scored_obj<Property>>();
//		for(Map.Entry<Property, Integer> e : prp_freq_pair.entrySet()) scored_prp.add(new Scored_obj<Property>(e.getKey(), e.getValue()));
//		Collections.sort(scored_prp);
//		Collections.reverse(scored_prp);
//		
//		System.out.println("PROCESSING :");
//		for(Scored_obj<Processing> p : scored_prc) System.out.println(p.toString());
//		System.out.println("STRUCTURE :");
//		for(Scored_obj<Structure> s : scored_str) System.out.println(s.toString());
//		System.out.println("PROPETY :");
//		for(Scored_obj<Property> p : scored_prp) System.out.println(p.toString());
//		
//		
//		System.out.println("----- FREQ OF FACTORS IN CORPUS ------");
//		scored_prc = new ArrayList<Scored_obj<Processing>>();
//		for(Map.Entry<Processing, Integer> e : prc_freq.entrySet()) scored_prc.add(new Scored_obj<Processing>(e.getKey(), e.getValue()));
//		Collections.sort(scored_prc);
//		Collections.reverse(scored_prc);
//		scored_str = new ArrayList<Scored_obj<Structure>>();
//		for(Map.Entry<Structure, Integer> e : str_freq.entrySet()) scored_str.add(new Scored_obj<Structure>(e.getKey(), e.getValue()));
//		Collections.sort(scored_str);
//		Collections.reverse(scored_str);
//		scored_prp = new ArrayList<Scored_obj<Property>>();
//		for(Map.Entry<Property, Integer> e : prp_freq.entrySet()) scored_prp.add(new Scored_obj<Property>(e.getKey(), e.getValue()));
//		Collections.sort(scored_prp);
//		Collections.reverse(scored_prp);
//		
//		System.out.println("PROCESSING :");
//		for(Scored_obj<Processing> p : scored_prc) System.out.println(p.toString());
//		System.out.println("STRUCTURE :");
//		for(Scored_obj<Structure> s : scored_str) System.out.println(s.toString());
//		System.out.println("PROPETY :");
//		for(Scored_obj<Property> p : scored_prp) System.out.println(p.toString());
//		
//		System.out.println("DONE");
//	}
}

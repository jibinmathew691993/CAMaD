package us.ttic.takeshi.nims.relation_extraction.factor_recgnizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import us.ttic.takeshi.nims.relation_extraction.Factor;
import us.ttic.takeshi.nims.relation_extraction.Ireader;
import us.ttic.takeshi.nims.relation_extraction.Phrase;
import us.ttic.takeshi.nims.relation_extraction.Processing;
import us.ttic.takeshi.nims.relation_extraction.Property;
import us.ttic.takeshi.nims.relation_extraction.Structure;
import us.ttic.takeshi.nims.relation_extraction.relation_model.Irelation_model;

public class Factor_dict extends Factor_map implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6435483453311895352L;
	
	//Dictionaries should be GIVEN ( except relation )
	private List<Property> prp_dict = new ArrayList<Property>();
	private List<Structure> str_dict= new ArrayList<Structure>();
	private List<Processing> prc_dict=new ArrayList<Processing>();
	
	//map: phrase->factor
	private Map<Phrase, Property> phrase2prp = new HashMap<Phrase, Property>();
	private Map<Phrase, Structure> phrase2str= new HashMap<Phrase, Structure>();
	private Map<Phrase, Processing> phrase2prc=new HashMap<Phrase, Processing>();
	//private Set<String> hook_prp = new HashSet<String>();
	//private Set<String> hook_str = new HashSet<String>();
	//private Set<String> hook_prc = new HashSet<String>();
	private int phrase_max=0;

	public Factor_dict(List<Processing> process_dict, List<Structure> structure_dict, List<Property> property_dict){
		prp_dict = property_dict;
		str_dict = structure_dict;
		prc_dict = process_dict;
		
		set_phrase_map(phrase2prc, prc_dict);
		set_phrase_map(phrase2str, str_dict);
		set_phrase_map(phrase2prp, prp_dict);
	}
	
	private <T extends Factor<?>> void set_phrase_map(Map<Phrase, T> mapping,  Collection<T> factors){
		for(T f : factors){
			for(String p : f.get_varieties()){
				
				//a phrase for a variety to say
				Phrase ph = new Phrase(p);
				
				//check factor priority and update? rank and source type
				if(mapping.containsKey(ph) 
					&& (mapping.get(ph).get_source_type().equalsIgnoreCase("g") ||mapping.get(ph).get_rank()<f.get_rank())) continue;
				mapping.put(ph, f);
				if(phrase_max<ph.size()) phrase_max=ph.size();
				
			}
		}
		//register hooks to search
		//for(Phrase p : mapping.keySet())for(String w : p.phrase().split(Phrase.SPLITTERS)) hooks.add(w.trim().toLowerCase());
	} 
	
	public final List<Processing> get_prc(Ireader corpus){
		return prc_dict;
	}
	public final List<Structure> get_str(Ireader corpus){
		return str_dict;
	}
	public final List<Property> get_prp(Ireader corpus){
		return prp_dict;
	}
	
	/**
	 * assign processing factor for each word
	 * @param snt
	 * @return might contain null
	 */
	public final List<Processing> assign_processing(final List<? extends HasWord> snt){		
		//initialize returning list
		List<Processing> re = new ArrayList<Processing>();
		for(int i=0;i<snt.size();i++) re.add(null);
		
		//trick of hook
//		boolean isContain=false;
//		for(HasWord w : snt)if(hook_prc.contains(w.word().toLowerCase())){
//			isContain=true;
//			break;
//		}
//		if(!isContain) return re;
		
		//each phrase size
		for(int ph_size=phrase_max;ph_size>0;ph_size--){
			
			//phrase for each starting position
			for(int s_idx=0;s_idx<=snt.size()-ph_size;s_idx++){
				//avoid over wrap
				boolean isOverwrap = false;
				
				List<HasWord> words = new ArrayList<HasWord>();
				for(int i=0;i<ph_size;i++){
					words.add(snt.get(s_idx+i));
					if(re.get(s_idx+i)!=null) isOverwrap=true;
				}
				
				if(isOverwrap) continue;
				Phrase p = new Phrase(words);
				
				if(phrase2prc.containsKey(p))for(int i=0;i<ph_size;i++)re.set(s_idx+i, phrase2prc.get(p));
			}
		}
		
		return re;
	}
	/**
	 * assign structure factor for each word
	 * @param snt
	 * @return might contain null
	 */
	public final List<Structure> assign_structure(final List<? extends HasWord> snt){
		//initialize returning list
		List<Structure> re = new ArrayList<Structure>();
		for(int i=0;i<snt.size();i++) re.add(null);
		
		//trick of hook
//		boolean isContain=false;
//		for(HasWord w : snt)if(hook_str.contains(w.word().toLowerCase())){
//			isContain=true;
//			break;
//		}
//		if(!isContain) return re;
		
		//each phrase size
		for(int ph_size=phrase_max;ph_size>0;ph_size--){
			
			//phrase for each starting position
			for(int s_idx=0;s_idx<=snt.size()-ph_size;s_idx++){
				//avoid over wrap
				boolean isOverwrap = false;
				
				List<HasWord> words = new ArrayList<HasWord>();
				for(int i=0;i<ph_size;i++){
					words.add(snt.get(s_idx+i));
					if(re.get(s_idx+i)!=null) isOverwrap=true;
				}
				
				if(isOverwrap) continue;
				Phrase p = new Phrase(words);
				
				if(phrase2str.containsKey(p))for(int i=0;i<ph_size;i++)re.set(s_idx+i, phrase2str.get(p));
			}
		}
		
		return re;
	}
	/**
	 * assign property factor for each word
	 * @param snt
	 * @return might contain null
	 */
	public final List<Property> assign_property(final List<? extends HasWord> snt){
		//initialize returning list
		List<Property> re = new ArrayList<Property>();
		for(int i=0;i<snt.size();i++) re.add(null);
		
		//trick of hook
//		boolean isContain=false;
//		for(HasWord w : snt)if(hook_prp.contains(w.word().toLowerCase())){
//			isContain=true;
//			break;
//		}
//		if(!isContain) return re;
		
		//each phrase size
		for(int ph_size=phrase_max;ph_size>0;ph_size--){
			
			//phrase for each starting position
			for(int s_idx=0;s_idx<=snt.size()-ph_size;s_idx++){
				//avoid over wrap
				boolean isOverwrap = false;
				
				List<HasWord> words = new ArrayList<HasWord>();
				for(int i=0;i<ph_size;i++){
					words.add(snt.get(s_idx+i));
					if(re.get(s_idx+i)!=null) isOverwrap=true;
				}
				
				if(isOverwrap) continue;
				Phrase p = new Phrase(words);
				
				if(phrase2prp.containsKey(p))for(int i=0;i<ph_size;i++)re.set(s_idx+i, phrase2prp.get(p));
			}
		}
		
		return re;
	}
	
	public static void main(String[] args){
		
	}

}

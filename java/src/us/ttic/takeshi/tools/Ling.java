package us.ttic.takeshi.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

public class Ling {

	public static boolean is_same_phrase(List<HasWord> ph1, List<HasWord> ph2){
		if(ph1.size()!=ph2.size()) return false;
		
		for(int i=0;i<ph1.size();i++){
			if(!ph1.get(i).word().equalsIgnoreCase(ph2.get(i).word())) return false;
		}
		return true;
	}
	
	public static String toString(List<HasWord> snt){
		StringBuffer sb = new StringBuffer();
		for(HasWord w : snt)sb.append(w.word()+" ");
		return sb.toString().trim();
	}
	
	public static final Map<String, String> token_map = new TreeMap<String, String>(){{
		put("-LRB-", "(");
		put("-RRB-", ")");
		put("-LCB-", "{");
		put("-RCB-", "}");
		put("-LSB-", "[");
		put("-RSB-", "]");
		put("-lrb-", "(");
		put("-rrb-", ")");
		put("-lcb-", "{");
		put("-rcb-", "}");
		put("-lsb-", "[");
		put("-rsb-", "]");
	}};
	public static String simple_string(List<HasWord> snt) {
		StringBuffer sb = new StringBuffer();
		for(HasWord w : snt) {
			sb.append( (token_map.containsKey(w.word()))? token_map.get(w.word()) :w.word());
			sb.append(" ");
		}
		return sb.toString().trim();
	}
	public static String simple_form(HasWord w) {
		return token_map.containsKey(w.word())? token_map.get(w.word()) :w.word();
	}

	public static String tagged2string(List<TaggedWord> tagged){
		StringBuffer sb = new StringBuffer();
		for(TaggedWord t : tagged) sb.append(t.tag()+" ");
		return sb.toString().trim();
	}
	
	public static Sentence sentence(List<HasWord> snt){
		List<String> seq = new ArrayList<>();
		for(HasWord w : snt) seq.add(w.word());
		return new Sentence(seq);
	}
	
}
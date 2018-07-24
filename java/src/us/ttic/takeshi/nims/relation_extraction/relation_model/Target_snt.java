/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction.relation_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import us.ttic.takeshi.tools.Ling;
import us.ttic.takeshi.tools.Pair;

/**
 * @author ikumu
 *
 */
public class Target_snt {
	
	public static final String PRC_TAG = "PRC";
	public static final String STR_TAG = "STR";
	public static final String PRP_TAG = "PRP";
	public static final String TYPE_FORMAT = "%s2%s";

	private List<HasWord> snt = null;
	private Pair<Integer> orign=null;
	private Pair<Integer> dest =null;
	private double score=0.0;
	private Pair<String> type;
	
	//optional
	private String orign_label="";
	private String dest_label ="";
	
	public String art_id = "";
	
//	public Target_snt(List<HasWord> sentence, Pair<Pair<Integer>> target_idx, double relation_score, String relation_type) {
//		snt = new ArrayList<>(sentence);
//		orign = target_idx.first;
//		dest  = target_idx.second;
//		score = relation_score;
//		type = relation_type;
//		
//		orign_label = toString(sentence.subList(orign.first, orign.second+1));
//		dest_label  = toString(sentence.subList(dest.first, dest.second+1)); 
//	}
	private String toString(List<HasWord> phrase){
		StringBuffer sb =new StringBuffer();
		for(HasWord w : phrase) sb.append(w.word()+" ");
		return sb.toString().trim();
	}
	
	public Target_snt(List<HasWord> sentence, Pair<Pair<Integer>> target_idx
						, double relation_score, Pair<String> relation_type, String org_phrase_label, String dest_phrase_label) {
		snt = new ArrayList<>(sentence);
		orign = target_idx.first;
		dest  = target_idx.second;
		score = relation_score;
		type = relation_type;
		
		orign_label = org_phrase_label;
		dest_label = dest_phrase_label;
		
		//check
		if(!type.equals(new Pair<String>(PRC_TAG, STR_TAG))&& !type.equals(new Pair<String>(STR_TAG, PRP_TAG)) ){
			System.err.println("un-expected relation type : "+type.toString());
		}
	}
	
	public Target_snt(String lrs_line){
		lrs_line=lrs_line.trim();
		String[] elms = lrs_line.split("\t");
		//check
		if(elms.length>=5) System.err.println("INVALID_FILE_FORMAT : "+lrs_line);
		
		//score and type
		score = Double.parseDouble(elms[0]);
		type = new Pair<String>(elms[1].trim().split("2")[0].trim(), elms[1].trim().split("2")[1].trim());
		String o = type.first;
		String d = type.second;
		
		//label
		orign_label =elms[3].trim();
		dest_label = elms[4].trim();
		
		art_id = elms[5].trim();
		
		//target indeces
		orign=new Pair<Integer>(-1,-1);
		dest =new Pair<Integer>(-1,-1);
		
		//sentence
		snt = new ArrayList<HasWord>();
		for(String w : elms[2].split(" ")){
			w = w.trim();
			if(w.startsWith("@")){
				if(w.endsWith(o)){
					orign.first = snt.size();
				}else if(w.endsWith(d)){
					dest.first = snt.size();
				}else{
					System.err.println("INVALID_FILE_FORMAT : "+lrs_line);
				}
			}else if(w.endsWith("@")){
				if(w.startsWith(o)){
					orign.second=snt.size()-1;
				}else if(w.startsWith(d)){
					dest.second=snt.size()-1;
				}else{
					System.err.println("INVALID_FILE_FORMAT : "+lrs_line);
				}
			}else{
				snt.add(new Word(w));
			}
		}
		
		//check
		if(orign.first<0 || orign.second<0 || dest.first<0 || dest.second<0){
			System.err.println("INVALID_FILE_FORMAT : "+lrs_line);
		}
		//check
		if(!type.equals(new Pair<String>(PRC_TAG, STR_TAG))&& !type.equals(new Pair<String>(STR_TAG, PRP_TAG)) ){
			System.err.println("un-expected relation type : "+type.toString());
		}
	}

	public final List<HasWord> getSnt() {
		return snt;
	}

	public final Pair<Integer> getOrign() {
		return orign;
	}

	public final Pair<Integer> getDest() {
		return dest;
	}

	public final double getScore() {
		return score;
	}
	
//	public final String getType(){
//		return type;
//	}
	
	public final Pair<String> getType(){
		return type;
	}
	
	public String get_orig_label(){
		return orign_label;
	}
	public String get_dest_label(){
		return dest_label;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		String org_tag = type.first;
		String dst_tag = type.second;
		
		sb.append(score+"\t");
		sb.append(String.format(TYPE_FORMAT, org_tag, dst_tag)+"\t");
		for(int i=0;i<snt.size();i++)if(!snt.get(i).word().contains("\n")){
			if(i==orign.first)sb.append(String.format("@%s ", org_tag));
			if(i==dest.first) sb.append(String.format("@%s ", dst_tag));
			sb.append(snt.get(i).word().toLowerCase()+" ");
			if(i==orign.second)sb.append(String.format("%s@ ", org_tag));
			if(i==dest.second) sb.append(String.format("%s@ ", dst_tag));
		}
		sb.append("\t"+orign_label);
		sb.append("\t"+dest_label);
		return sb.toString().trim();
	}
	
	public String get_html() {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<snt.size();i++){
			if(i==orign.first || i==dest.first) sb.append("<u>");
			sb.append( (Ling.token_map.containsKey(snt.get(i).word()))? Ling.token_map.get(snt.get(i).word()):snt.get(i).word().toLowerCase() +" ");
			if(i==orign.second || i==dest.second) sb.append("</u>");
		}
		sb.append("</br>");
		sb.append(art_id);
		
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		String txt = "/home/ikumu/workspace/relation_extraction_java/texts/Geometry.txt.lrs";
		
		BufferedReader reader = new BufferedReader(new FileReader(txt));
		BufferedWriter writer = new BufferedWriter(new FileWriter("tmp.lrs"));
		String line = null;
		while((line=reader.readLine())!=null){
			writer.write((new Target_snt(line).toString()));
			writer.newLine();
		}
		reader.close();
		writer.close();
	}

}

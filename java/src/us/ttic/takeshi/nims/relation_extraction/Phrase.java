/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.Tree;

/**
 * This is a phrase which does not know any capital of words
 * @author takeshi.onishi
 *
 */
public class Phrase implements Comparable<Phrase>, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2549617241152617318L;
	
	//phrase
	private String phrase;
	private int size;
	
	private String label = "";
	
	//some hyper parameters
	public static String SPLITTERS = " +|,|\\(|\\)";
	
	
	//constructors
	public Phrase(String[] words){
		set_phrase(words);
	}
	public Phrase(String phrase){
		PTBTokenizer<Word> toknizer = new PTBTokenizer<>(new StringReader(phrase), new WordTokenFactory(), "");
		List<String> words =new ArrayList<String>(); while(toknizer.hasNext()) words.add(toknizer.next().word());
		set_phrase(words);
	}
	public Phrase(ArrayList<Word> words){
		String[] raws = new String[words.size()];
		for(int i=0;i<words.size();i++) raws[i]=words.get(i).word();
		set_phrase(raws);
	}
	public Phrase(ArrayList<Word> words, String pos){
		this(words);
		label = pos;
	}
	public Phrase(Tree node){
		label = node.label().value();
		List<Tree> leaves = node.getLeaves();
		String[] raws = new String[leaves.size()];
		for(int i=0;i<leaves.size();i++) raws[i]=leaves.get(i).label().value();
		set_phrase(raws);
	}
	public Phrase(List<HasWord> words){
		String[] raws = new String[words.size()];
		for(int i=0;i<words.size();i++) raws[i]=words.get(i).word();
		set_phrase(raws);
	}
	private void set_phrase(String[] words){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<words.length;i++) sb.append(words[i]+" ");
		
		phrase = sb.toString().trim().toLowerCase();
		size=words.length;
	}
	private void set_phrase(List<String> words){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<words.size();i++) sb.append(words.get(i)+" ");
		
		phrase = sb.toString().trim().toLowerCase();
		size=words.size();
	}
	
	public boolean contains(Phrase smaller){
		String[] big = phrase.split(SPLITTERS);
		String[] small=smaller.phrase.split(SPLITTERS);
		
		if(big.length<small.length) return false; 
		for(int i=0;i<=big.length-small.length;i++){
			for(int j=0;j<small.length && big[i+j].equals(small[j]);j++)if(j==small.length-1) return true;
		}
		return false;
	}
	
	//getter
	public String phrase(){
		return phrase;
	}
	
	public int size(){
		return size;
	}
	
	public String label(){
		return label;
	}
	
	public String toString(){
		return this.phrase();
	}

	
	//compare for each other
	@Override
	public int compareTo(Phrase another) {
		if(this.equals(another)) return 0;
		return this.hashCode()>another.hashCode()? 1 : -1;
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null || !this.getClass().isInstance(o)) return false;
		return this.phrase().equals(((Phrase)o).phrase());
	}
	
	@Override
	public int hashCode(){
		return this.phrase().hashCode();
	}
	
	
	public static void main(String[] args){
		String ph1 = "Fe2O Tom's alloy";
		List<HasWord> ph2 = new ArrayList<HasWord>();
		ph2.add(new Word("streNgthed")); ph2.add(new Word("pen")); ph2.add(new Word("tate"));
		List<HasWord> ph3 = new ArrayList<HasWord>();
		ph3.add(new Word("streNgthed pen demo")); 
		
		Phrase p1 = new Phrase(ph1);
		Phrase p2 = new Phrase(ph2);
		Phrase p3 = new Phrase(ph3);
		
		System.out.println(p1.toString());
		System.out.println(p1.size());
		System.out.println(p2.toString());
		System.out.println(p2.size());
		System.out.println(p3.toString());
		System.out.println(p3.size());
		
		if(p1.equals(p2)) System.out.println("p1==p2");
		if(p2.equals(p3)) System.out.println("p2==p3");
		if(p1.equals(p3)) System.out.println("p3==p1");
	
		System.out.println(p2.contains(p1));
		
	}

}

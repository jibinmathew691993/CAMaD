/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import edu.stanford.nlp.ling.HasWord;
import us.ttic.takeshi.tools.Scored_obj;

/**
 * @author ikumu
 *
 */
public class ArticleSet extends Reader implements Comparable<ArticleSet>{

	private final String path;
	private final List<Article> articles = new ArrayList<Article>();
	
	/**
	 * 
	 */
	public ArticleSet(File dir) {
		for(File f : get_xmls(dir)){
			try {
				articles.add(new Article(f));
			} catch (SAXException | IOException | ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		path = dir.getAbsolutePath();
	}
	
	private static Collection<File> get_xmls(File dir){
		Collection<File> files = new ArrayList<File>();
		for(File f : dir.listFiles()) {
			if(f.getName().endsWith(".html") || f.getName().endsWith(".xml")){
				files.add(f);
			}else if(f.isDirectory()) {
				files.addAll(get_xmls(f));
			}
		}
		return files;
	}
	
	public final List<Article> getArticles(){
		return articles;
	}
	
	public final String getPath(){
		return path;
	}
	
	public final List<String> getKeywords(){
		
		//count keywords
		Map<String, Integer> freq= new HashMap<String, Integer>();
		for(Article a : articles)for(String w : a.getKeywords()){
			String k = w.toLowerCase();
			if(!freq.containsKey(k)) freq.put(k, 0);
			freq.put(k, freq.get(k)+1);
		}
		
		//sort them
		List<Scored_obj<String>> scored =new ArrayList<Scored_obj<String>>();
		for(Map.Entry<String, Integer> e : freq.entrySet()) scored.add( new Scored_obj<String>(e.getKey(), (int) e.getValue()) );
		Collections.sort(scored);
		Collections.reverse(scored);
		
		//->string
		List<String> ranked =new ArrayList<String>();
		for(Scored_obj<String> s : scored) ranked.add(s.obj);
		
		return ranked;
	}

	/**
	 * this is slow and consume huge memory
	 * @deprecated
	 */
	@Override
	public List<String> paragraph() {
		List<String> huge = new ArrayList<String>();
		for(Article a : articles) huge.addAll(a.paragraph());
		return huge;
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Ireader#itr_snt()
	 */
	@Override
	public Iiterator<List<HasWord>> itr_snt() {
		return new Sentence_itr_wrapper(articles);
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Ireader#itr_snt(int)
	 */
	@Override
	public Iiterator<List<HasWord>> itr_snt(int chapter_idx) {
		return articles.get(chapter_idx).itr_snt();
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Ireader#chapter_size()
	 */
	@Override
	public int chapter_size() {
		return articles.size();
	}
	
	public String get_id(){
		return path;
	}
	

	/**
	 * 
	 * option for command line
	 * 	-keysearch : keyword search
	 * 		return : list of file path whose file contains the keywords
	 * 	-dir : dir path
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	static final String KEYSEARCH_OPT = "-keysearch";
	static final String DIR_OPT = "-dir";
	static final String LISTKEY_OPT = "-listkey";
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		String[] keywords = null;
		String xml_file = null;
		boolean listKey = false;
		
		for(int i=0;i<args.length;i++){
			if(args[i].equals(KEYSEARCH_OPT)){
				keywords = args[++i].split(" ");
			}else if(args[i].equals(DIR_OPT)){
				xml_file = args[++i];
			}else if(args[i].equals(LISTKEY_OPT)){
				listKey=true;
			}
		}		
		//check option
		if(xml_file==null){
			System.err.println("invalid args");
			System.err.println("-dir : "+((xml_file!=null)? xml_file : "NULL"));
			return;
		}
		
		
		ArticleSet set = new ArticleSet(new File(xml_file));
		
		if(keywords!=null){
			searchKey(set, keywords);
		}else if(listKey==true){
			for(String k: set.getKeywords()){
				System.out.println(k);
			}
		}
		
//		Iiterator<List<HasWord>> snt_itr = set.itr_snt();
//		int i=0;
//		while(snt_itr.hasNext()){
//			for(HasWord w : snt_itr.next()){
//				System.out.print(w+" ");
//			}
//			System.out.println();
//		}
//		Article a = new Article((new File(xml_files).listFiles()[1]));
//		for(String p : a.paragraph()){
//			System.out.println(p);
//		}
	}
	
	static void searchKey(ArticleSet set , String[] keywords){
		for(Article a : set.getArticles()){
			//initialize flags
			boolean[] isContain = new boolean[keywords.length];
			for(int i=0;i<isContain.length;i++) isContain[i]=false;
			
			//search each keyword
			for(int search_idx=0;search_idx<keywords.length;search_idx++){
				for(String art_k : a.getKeywords())if(art_k.contains(keywords[search_idx])){
					isContain[search_idx]=true;
				}
			}
			
			//is all contained?
			boolean isAll=true;
			for(boolean f : isContain)if(f==false) isAll=false;
			if(isAll) System.out.println(a.getPath());
		}
		
	}

	@Override
	public int compareTo(ArticleSet o) {
		return path.compareTo(o.path);
	}
	@Override
	public boolean equals(Object o){
		if(!(o instanceof ArticleSet)) return false;
		ArticleSet another = (ArticleSet) o;
		return this.path.equals(another.path);
	}

}

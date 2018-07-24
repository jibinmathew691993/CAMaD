/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import edu.stanford.nlp.ling.HasWord;

/**
 * @author ikumu
 *
 */
public class Article extends Reader {

	private Handler xml = new Handler();
	private final String path;
	
	/**
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * 
	 */
	public Article(File xml_file) throws SAXException, IOException, ParserConfigurationException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(xml_file, xml);
		
		path = xml_file.getAbsolutePath();
	}
	
	public final Collection<String> getKeywords(){
		return xml.getKeywords();
	}

	public final Collection<String> getSection_titles(){
		return xml.getSection_titles();
	}
	
	public final String getPath(){
		return path;
	}
	
	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Reader#paragraph()
	 */
	@Override
	public final List<String> paragraph() {
		return xml.getParagraphs();
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Reader#itr_snt()
	 */
	@Override
	public Iiterator<List<HasWord>> itr_snt() {
		return new Sentence_itr(xml.getParagraphs());
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Reader#itr_snt(int)
	 */
	@Override
	public Iiterator<List<HasWord>> itr_snt(int chapter_idx) {
		return new Sentence_itr(xml.getParagraphs().get(chapter_idx));
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Reader#chapter_size()
	 */
	@Override
	public int chapter_size() {
		return xml.getParagraphs().size();
	}
	
	public String get_id(){
		return xml.title;
	}
	
	public String get_auth() {
		return xml.auth;
	}


}

class Handler extends DefaultHandler{
	
	//stored inf
	private List<String> section_titles = new ArrayList<String>();
	private List<String> paragraphs = new ArrayList<String>();
	private List<String> keywords = new ArrayList<String>();
	public String title;
	public String auth;
	
	
	//for parsing
	private Stack<String> stack = new Stack<String>();
	private StringBuffer keyword;
	private StringBuffer txt;
	private StringBuffer sec_title;
	private StringBuffer title_bff;
	
	//tags format
	public static final String TAG_FORMAT = "%s:%s";
	
	//ce tags
	public static final String CE_TAG = "ce";
	public static final String LABEL_TAG = String.format(TAG_FORMAT, CE_TAG, "label");
	public static final String SEC_TAG = String.format(TAG_FORMAT, CE_TAG, "section");
	public static final String SEC_TITLE_TAG = String.format(TAG_FORMAT, CE_TAG, "section-title");
	public static final String TITLE_TAG = String.format(TAG_FORMAT, CE_TAG, "title");
	public static final String PARA_TAG = String.format(TAG_FORMAT, CE_TAG, "para");
	public static final String SIMPLE_PARA_TAG = String.format(TAG_FORMAT, CE_TAG, "simple-para");
	public static final String INF_TAG = String.format(TAG_FORMAT, CE_TAG, "inf");
	public static final String SUP_TAG = String.format(TAG_FORMAT, CE_TAG, "sup");
	public static final String KEYWORD_TAG  = String.format(TAG_FORMAT, CE_TAG, "keyword");
	public static final String AUTH_SUR = String.format(TAG_FORMAT, CE_TAG, "surname");
	public static final String AUTH = String.format(TAG_FORMAT, CE_TAG, "author");
	
	//mml tags
	public static final String MML_TAG = "mml";
	
	//other tag
	public static final String NO_TITLE_TAG = "NO_TITLE";
	

	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
				
		if(qName.equals(KEYWORD_TAG)){ //keyword
			keyword = new StringBuffer();
		}else if(qName.equals(SEC_TAG) && !stack.contains(SEC_TAG)){ //new section
			//nothing to do
		}else if(qName.equals(SEC_TITLE_TAG)){ 
			sec_title = new StringBuffer();
			
		}else if( (qName.equals(PARA_TAG) || qName.equals(SIMPLE_PARA_TAG)) && !stack.contains(PARA_TAG) && !stack.contains(SIMPLE_PARA_TAG)){
			txt = new StringBuffer();
		}else if(qName.equals(TITLE_TAG) && !stack.contains(TITLE_TAG)){
			title_bff = new StringBuffer();
		}
		
		stack.push(qName);
	}

	public void endElement(String uri, String localName,String qName) throws SAXException {
		String crr = stack.pop();
		if(!crr.equals(qName)) throw new SAXException("Invalid format : "+crr+" ends without closing with "+qName);
		
		//keyword
		if(qName.equals(KEYWORD_TAG) && !stack.contains(KEYWORD_TAG)){
			keywords.add(keyword.toString());
			keyword = null;
		}else if(qName.equals(SEC_TAG) && !stack.contains(SEC_TAG)){
			//nothing to do
		}else if(qName.equals(SEC_TITLE_TAG)){ 
			section_titles.add(sec_title.toString());
			sec_title=null;
		}else if((qName.equals(PARA_TAG) || qName.equals(SIMPLE_PARA_TAG)) && !stack.contains(PARA_TAG) && !stack.contains(SIMPLE_PARA_TAG)){
			String p = txt.toString().trim();
			if(p.length()>10)paragraphs.add(p);
			txt=null;
		}else if(qName.equals(TITLE_TAG)){
			title = title_bff.toString();
			title_bff = null;
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {

		//System.out.println(new String(ch, start, length));
		//in keyword tag
		if(stack.contains(KEYWORD_TAG)){
			keyword.append(ch, start, length);
		}else if(stack.contains(SEC_TITLE_TAG)){ 
			sec_title.append(ch,start,length);
		}else if((stack.contains(PARA_TAG) || stack.contains(SIMPLE_PARA_TAG)) && stack.contains(SEC_TAG)){
			for(int i=start;i<start+length;i++) {
				if(ch[i]!=' ' && ch[i]!='\t' && ch[i]!='\n') {
					txt.append(ch,start,length);
					break;
				}
			}
		}else if(stack.contains(SEC_TAG)){
			//nothing to do
		}else if(stack.contains(TITLE_TAG)){
			title_bff.append(ch, start, length);
		}else if(stack.contains(AUTH) && stack.contains(AUTH_SUR)) {
			auth = new String(ch, start, length);
		}
	}

	public final List<String> getSection_titles() {
		return section_titles;
	}

	public final List<String> getParagraphs(){
		return paragraphs;
	}

	public final List<String> getKeywords() {
		return keywords;
	}
	
}

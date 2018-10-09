/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction.online_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author takeshi.onishi
 *
 */
public class Html_file {
	
	private List<String> lines = new ArrayList<String>();
	private Stack<String> closers = new Stack<String>();
	
	//html hypers
	public static final String HTML_TAG = "html";
	public static final String BODY_TAG = "body";
	public static final String P_TAG = "p";
	public static final String SElECT_TAG = "select";
	public static final String OPTION_TAG = "option";
	public static final String BUTTON_TAG = "button";
	public static final String FORM_TAG = "form";
	public static final String INPUT_TAG = "input";
	public static final String IMG_TAG = "img";
	public static final String CHECKBOX_TAG = "checkbox";
	public static final String TXT_AREA_TAG = "textarea";
	public static final String DIV_TAG = "div";
	public static final String SCRIPT_TAG = "script";
	public static final String DATALIST_TAG = "datalist";
	
	public static final String TYPE_ATTR = "type";
	public static final String METHOD_ATTR = "method";
	public static final String ACTION_ATTR = "action";
	public static final String VALUE_ATTR = "value";
	public static final String NAME_ATTR = "name";
	public static final String ALT_ATTR ="alt";
	public static final String SRC_ATTR = "src";
	public static final String WIDTH_ATTR="width";
	public static final String HEGHT_ATTR="height";
	public static final String STYLE_ATTR="style";
	public static final String ROWS_ATTR = "rows";
	public static final String COLS_ATTR = "cols";
	public static final String ID_ATTR = "id";
	public static final String JS_ATTR = "text/javascript";
	public static final String ACCECPT_CHR_ATTR = "accept-charset";
	public static final String AUTO_COMP_ATTR="autocomplete";
	public static final String LIST_ATTR = "list";
	
	
	public Html_file(){
		//do nothing
	}
	
	//method to write dot file
	public void clearn_stacks(){
		if(lines!=null) lines.clear();
		if(closers!=null) closers.clear();
		lines = new ArrayList<String>();
		closers=new Stack<String>();
	}
	public void stack_txt(String txt){
		StringBuffer tabs = new StringBuffer();
		for(int i=0;i<closers.size();i++) tabs.append("\t");
		lines.add(tabs.toString()+txt);
	}
	public void stack_tag(String tag){
		stack_tag(tag, new String[]{}, new String[]{});
	}
	public void stack_tag(String tag, String[] attrs, String[] values ){
		StringBuffer tabs = new StringBuffer();
		for(int i=0;i<closers.size();i++) tabs.append("\t");
		
		//attr
		StringBuffer att = new StringBuffer();
		for(int i=0;i<Math.min(attrs.length, values.length);i++){
			att.append(String.format(" %s=\"%s\"", attrs[i],values[i]));
		}
		
		lines.add(tabs.toString()+String.format("<%s %s>",tag, att.toString()));
		closers.push(String.format("</%s>",tag));
	}
	/**
	 * lazy way to say stack_tag(String tag, String[] attrs, String[] values )
	 * 	tag[0] : tag
	 *  tag[i] : attr
	 *  tag[i+1] : value for attr, tag[i]
	 * @param tag
	 */
	public void stack_tag(String... tags){
		String tag = tags[0];
		String[] atts = new String[(tags.length-1)/2];
		String[] vals = new String[(tags.length-1)/2];
		int c =0;
		for(int i=1;i<tags.length;i+=2){
			atts[c]=tags[i];
			vals[c]=tags[i+1];
			c++;
		}
		this.stack_tag(tag, atts, vals);
	}
	
	public void close_tag(){
		StringBuffer tabs = new StringBuffer();
		for(int i=0;i<closers.size()-1;i++) tabs.append("\t");
		lines.add(tabs.toString()+closers.pop());
	}
	public void finalize_stack(){
		while(!closers.isEmpty()){
			close_tag();
		}
	}
	public void newLine(){
		StringBuffer tabs = new StringBuffer();
		for(int i=0;i<closers.size();i++) tabs.append("\t");
		lines.add(tabs.toString()+"<br>");
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(String l : lines){
			sb.append(l+"\n");
		}
		return sb.toString();
	}

}

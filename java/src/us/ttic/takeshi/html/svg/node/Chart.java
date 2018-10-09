/**
 * 
 */
package us.ttic.takeshi.html.svg.node;

import java.util.ArrayList;
import java.util.List;

import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Integer;
import us.ttic.takeshi.html.svg.style.Style_Class;

/**
 * @author tonishi
 *
 */
public class Chart extends Node{
	
	//static
	//attribute names
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String TAG = "svg";
	
	private List<Style_Class> styles = new ArrayList<>();
	
	public Chart(int width, int height) {
		attributes.add(new Attribute<Integer>(WIDTH, new Integer(width)));
		attributes.add(new Attribute<Integer>(HEIGHT,new Integer(height)));
		
	}
	
	public void add_style(Style_Class style) {
		styles.add(style);
	}
	
	@Override
	public String get_html(int n_tab) {
		StringBuffer sb = new StringBuffer();
		sb.append("<"+Style_Class.TAG+">\n");
		for(Style_Class s : styles) sb.append(s.get_css());
		sb.append("</"+Style_Class.TAG+">\n");
		sb.append(super.get_html(n_tab));
		
		return sb.toString();
	}
	

	@Override
	public String get_tag() {
		return TAG;
	}

}

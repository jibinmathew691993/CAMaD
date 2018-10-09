package us.ttic.takeshi.html.svg.style;

import java.util.ArrayList;
import java.util.List;

public class Style_Class {
	
	//tag
	public static final String TAG = "style";
	
	private final String name;
	private List<String> atts = new ArrayList<>();
	private List<String> vals = new ArrayList<>();
	
	public Style_Class(String class_name) {
		name = class_name;
	}
	
	public void add_style(String att, String val) {
		int idx = atts.indexOf(att);
		if(idx<0) {
			atts.add(att);
			vals.add(val);
		}else {
			vals.set(idx, val);
		}
	}

	public String get_css() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(" {\n");
		for(int i=0;i<atts.size();i++) {
			sb.append("\t");
			sb.append(atts.get(i));
			sb.append(": ");
			sb.append(vals.get(i));
			sb.append(";\n");
		}
		sb.append("}\n");
		
		return sb.toString();
	}
}

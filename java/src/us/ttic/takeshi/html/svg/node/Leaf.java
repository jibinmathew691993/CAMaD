package us.ttic.takeshi.html.svg.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import us.ttic.takeshi.html.svg.node.att.IAttribute;

public abstract class Leaf implements INode{

	//attribute
	protected Set<IAttribute> attributes = new TreeSet<>();
	
	//text
	protected String txt="";
		
	@Override
	public Collection<IAttribute> get_atts() {
		return attributes;
	}

	@Override
	public void add_att(IAttribute att) {
		attributes.add(att);
	}

	@Override
	public String get_html() {
		return get_html(0);
	}

	@Override
	public String get_html(int n_tab) {
		StringBuffer sb = new StringBuffer();
		String tab = "";
		for(int i=0;i<n_tab;i++) tab+="\t";
		
		//tag and attt
		sb.append(tab+"<"+get_tag());
		for(IAttribute a : attributes) {
			sb.append(" ");
			sb.append(a.get_name());
			sb.append("=");
			sb.append(a.get_val());
		}
		sb.append(">");
		sb.append(txt);
		sb.append("</"+get_tag()+">\n");
		return sb.toString();
	}

	@Override
	public List<INode> get_children() {
		return new ArrayList<>();
	}

	@Override
	public void add_child(INode node) {
		System.err.println("invalid method add_child"+toString());
	}


}

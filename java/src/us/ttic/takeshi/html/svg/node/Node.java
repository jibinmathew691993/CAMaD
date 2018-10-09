/**
 * 
 */
package us.ttic.takeshi.html.svg.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import us.ttic.takeshi.html.svg.node.att.IAttribute;

/**
 * @author tonishi
 *
 */
public abstract class Node implements INode{
	
	//static
	public static final String CLASS = "class";
	public static final String ID = "id";
	public static final String VISIBILITY = "visibility";
	
	//attribute
	protected Set<IAttribute> attributes = new TreeSet<>();
	//structure
	protected List<INode> children = new ArrayList<>();
	
	public Node() {
		
	}
	public Node(Node n) {
		attributes = new TreeSet<>(n.attributes);
		children   = new ArrayList<>(n.children);
	}
	
	public Collection<IAttribute> get_atts(){
		return attributes;
	}
	public void add_att(IAttribute att) {
		attributes.add(att);
	}
	public List<INode> get_children(){
		return children;
	}
	public void add_child(INode node) {
		children.add(node);
	}
	
	//html
	public String get_html() {
		return get_html(0);
	}

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
		sb.append(">\n");
		
		//children
		for(INode c : children) {
			sb.append(c.get_html(n_tab+1));
		}
		
		sb.append(tab+"</"+get_tag()+">\n");
		return sb.toString();
	}
}

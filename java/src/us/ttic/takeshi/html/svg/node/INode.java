package us.ttic.takeshi.html.svg.node;

import java.util.Collection;
import java.util.List;

import us.ttic.takeshi.html.svg.node.att.IAttribute;


public interface INode{

	public String get_tag();
	
	//tag and attributes
	public Collection<IAttribute> get_atts();
	public void add_att(IAttribute att);

	//html
	public String get_html();
	public String get_html(int n_tab);
	
	//structure
	public List<INode> get_children();
	public void add_child(INode node);
		
}

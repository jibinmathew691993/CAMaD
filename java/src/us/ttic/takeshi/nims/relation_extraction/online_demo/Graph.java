package us.ttic.takeshi.nims.relation_extraction.online_demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import us.ttic.takeshi.html.svg.node.Chart;
import us.ttic.takeshi.html.svg.node.Div;
import us.ttic.takeshi.html.svg.node.Group;
import us.ttic.takeshi.html.svg.node.INode;
import us.ttic.takeshi.html.svg.node.Line;
import us.ttic.takeshi.html.svg.node.Linked_Rects;
import us.ttic.takeshi.html.svg.node.Rectangle;
import us.ttic.takeshi.html.svg.node.TextBox;
import us.ttic.takeshi.html.svg.node.TextLine;
import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Str;
import us.ttic.takeshi.html.svg.style.Style_Class;
import us.ttic.takeshi.nims.relation_extraction.online_demo.json.Node;
import us.ttic.takeshi.nims.relation_extraction.relation_model.Target_snt;
import us.ttic.takeshi.tools.Ling;
import us.ttic.takeshi.tools.Pair;

public class Graph implements Igraph {
	
	//hyper paras for setting
	static final String CMB_AVE = "AVE";
	static final String CMB_MAX = "MAX";
	static final String CMB_MIN = "MIN";
	static final String CMB_SUM = "SUM";
	
	//Json format
	static final String NODES_TAG = "nodes";
	static final String LINKS_TAG = "links";
	static final String NAME_TAG = "name";
	static final String TYPE_TAG = "type";
	static final String COUNT_TAG= "count";
	static final String SOURCE_TAG = "source";
	static final String STRENGTH_TAG = "strength";
	static final String TARGET_TAG = "target";
	static final String LABEL_TAG = "label";
	static final String PH_BASE_TAG = "<font class=\"ph_%s\">";

	
	//setting
	protected String cmb;
	
	//sentence to reade
	protected List<Target_snt> snts;
	
	//Graph
	protected List<Edge> edges; //element might duplicate in pair of nodes
	
	//map:node->edge
//	protected Map<Node, List<Edge>> node2edges;

	public Graph(List<Target_snt> sentences, String comb) throws Exception {
		this(sentences, comb, null);
	}
	
	public Graph(List<Target_snt> sentences, String comb, List<String> target_prp_labels) throws Exception {
		if(comb.equals(CMB_AVE) || comb.equals(CMB_MAX) || comb.equals(CMB_MIN) || comb.equals(CMB_SUM)){
			cmb = comb;
		}else{
			throw new Exception("invalid setting CMB :"+comb);
		}
		
		snts = sentences;
		Map<Edge, List<Target_snt>> duplicates = new HashMap<Edge, List<Target_snt>>();
		for(Target_snt t : snts){
			//skip un-targeted
			if(target_prp_labels!=null && !target_prp_labels.isEmpty()){
				if(t.getType().first.equals(Target_snt.PRP_TAG) && !target_prp_labels.contains(t.get_orig_label())) continue;
				if(t.getType().second.equals(Target_snt.PRP_TAG) && !target_prp_labels.contains(t.get_dest_label())) continue;
			}
			
			Edge e = new Edge(t);
			if(!duplicates.containsKey(e)) duplicates.put(e, new ArrayList<Target_snt>());
			duplicates.get(e).add(t);
		}
		
		//uniqufy edges
		edges = new ArrayList<Edge>();
		for(Map.Entry<Edge, List<Target_snt>> e : duplicates.entrySet()){
			double score = -1.0;
			if(cmb.equals(CMB_MAX)){
				score = max_score(e.getValue());
			}else if(cmb.equals(CMB_MIN)){
				score = min_score(e.getValue());
			}else if(cmb.equals(CMB_SUM)){
				score = sum_score(e.getValue());
			}else if(cmb.equals(CMB_AVE)){
				score = ave_score(e.getValue());
			}
			Target_snt rep = strongetst(e.getValue());
			Edge edge = new Edge(rep);
			edge.setStrength(score);
			
			if(edges.contains(edge)){
				System.out.println("invalid!!");
			}
			edges.add(edge);
		}
		
		//map node2edge
//		node2edges = new HashMap<Node, List<Edge>>();
//		for(Edge e : edges){
//			if(!node2edges.containsKey(e.getOrg())) node2edges.put(e.getOrg(),new ArrayList<Edge>());
//			if(!node2edges.containsKey(e.getDest()))node2edges.put(e.getDest(),new ArrayList<Edge>());
//			node2edges.get(e.getOrg()).add(e);
//			node2edges.get(e.getDest()).add(e);
//		}
	}
	
	private Target_snt strongetst(List<Target_snt> snts){
		double score = -9999.9;
		Target_snt strangest = null;
		for(Target_snt s : snts)if(score<s.getScore()){
			score = s.getScore();
			strangest = s;
		}
		return strangest;
	}
	private double max_score(List<Target_snt> snts){
		double score = -9999.9;
		for(Target_snt s : snts)if(score<s.getScore()) score = s.getScore();
		return score;
	}
	private double min_score(List<Target_snt> snts){
		double score = 9999.9;
		for(Target_snt s : snts)if(s.getScore()<score) score = s.getScore();
		return score;
	}
	private double sum_score(List<Target_snt> snts){
		double score = 0.0;
		for(Target_snt s : snts)score += s.getScore();
		return score;
	}
	private double ave_score(List<Target_snt> snts){
		double score = 0.0;
		for(Target_snt s : snts)score += s.getScore();
		return score/(double)snts.size();
	}
/**	
	public JSONObject get_json(){
		//uniqfy nodes
		Set<Node> node_set = new HashSet<Node>();
		for(Edge e : edges){
			node_set.add(e.getOrg());
			node_set.add(e.getDest());
		}
		
		//data to write json
		List<Node> node_list = new ArrayList<Node>(node_set);
		Map<Pair<Integer>, Edge> nodeIdx2edge = new TreeMap<Pair<Integer>, Edge>();
		for(Edge e : edges){
			int org_idx = node_list.indexOf(e.getOrg());
			int dest_idx= node_list.indexOf(e.getDest());
			Pair<Integer> p = new Pair<Integer>(org_idx, dest_idx);
			
			if(nodeIdx2edge.containsKey(p)) System.err.println("edge duplication : "+org_idx+"\t"+dest_idx+"\t"+e.toString());
			nodeIdx2edge.put(p, e);
		}
		
		JSONArray j_nodes = new JSONArray();
		JSONArray j_edges = new JSONArray();
		for(Node node : node_list){
			JSONObject n = new JSONObject();
			n.put(NAME_TAG, node.getName());
			n.put(TYPE_TAG, node.getType());
			n.put(COUNT_TAG, "200");
			j_nodes.add(n);
		}
		for(Map.Entry<Pair<Integer>, Edge> entry : nodeIdx2edge.entrySet()){
			JSONObject e = new JSONObject();
			e.put(SOURCE_TAG, entry.getKey().first);
			e.put(TARGET_TAG, entry.getKey().second);
			e.put(STRENGTH_TAG, entry.getValue().getStrength());
			e.put(LABEL_TAG, toHtml(entry.getValue().getSnt()));
			j_edges.add(e);
		}
		
		JSONObject root = new JSONObject();
		root.put(NODES_TAG, j_nodes);
		root.put(LINKS_TAG, j_edges);
		
		return root;
	}
**/
	
	
	private static final Map<String, Integer> TYPE_IDX = new TreeMap<String, Integer>(){{
		put(Target_snt.PRC_TAG, 0);
		put(Target_snt.STR_TAG, 1);
		put(Target_snt.PRP_TAG, 2);
	}};
	private static final String GREEN = "rgb(46,204,113);";
	private static final String RED   = "rgb(231,76,60);";
	private static final String BLUE  ="rgb(52,152,219);";
	private static final String LINKS = "links";
	private static final Style_Class DEF_LINK_STYLE = new Style_Class(String.format(".%s .%s", LINKS, Linked_Rects.DEFLINE)){{
		add_style("visibility", "visible");
	}};
	private static final Style_Class POP_LINK_STYLE = new Style_Class(String.format(".%s:hover .%s", LINKS, Linked_Rects.DEFLINE)){{
		add_style("visibility", "hidden");
	}};
	private static final Style_Class DEF_RECT_STYLE = new Style_Class(String.format(".%s .%s", LINKS, Linked_Rects.DEF_RECT)){{
		add_style("visibility", "visible");
	}};
	private static final Style_Class POP_RECT_STYLE = new Style_Class(String.format(".%s:hover .%s", LINKS, Linked_Rects.DEF_RECT)){{
		add_style("visibility", "hidden");
	}};
	private static final int LINK_MAX_STROKE = 10;
	private static final int LINK_MIN_STROKE = 1;
	private static final String TYPE_STYLE_TEMP
		="overflow-y:auto; color:%s; text-align:center; font-family: Sans-serif; vertical-align: middle; font-size:2em";
	public Chart get_svg(int WIDTH){
		//first collomn for each category
		String[] categories =new String[] {"Process", "Structure", "Property"};
		String[] colors     =new String[] {GREEN, RED, BLUE};
	    List<INode> boces = new ArrayList<>();
		for(int i=0;i<categories.length;i++) {
			Div name = new Div(categories[i]);
			name.add_att(new Attribute<Str>(Div.STYLE, new Str(String.format(TYPE_STYLE_TEMP, colors[i]))));
			name.add_att(new Attribute<Str>(Div.XMLNS, new Str(TextBox.XMLNS_VAL)));
			
			
			int w = WIDTH/TYPE_IDX.size();
			int x = w*i;
			TextBox box = new TextBox(x, 0, (int)(w*RECT_WIDTH_RATIO), (int)(RECT_HIGHT*0.6), RECT_ROUND_RATIO, name);
			box.rect.add_att(new Attribute<Str>("visibility", new Str("hidden")));
			boces.add(box);
		}
		
		//map: ( type x node ) -> idx
	    Map<String, Map<Node, Integer>> rect_idx  = new TreeMap<String, Map<Node, Integer>>();
	    double max_strength = 0.0;
	    double min_strength = Double.MAX_VALUE;  
	    for(Edge e : edges){
	    	Node n = e.getOrg();
	    	if(!rect_idx.containsKey(n.getType())) rect_idx.put(n.getType(), new TreeMap<Node, Integer>());
	    	if(!rect_idx.get(n.getType()).containsKey(n)) rect_idx.get(n.getType()).put(n, rect_idx.get(n.getType()).size()+1);
	    	
	    	n = e.getDest();
	    	if(!rect_idx.containsKey(n.getType())) rect_idx.put(n.getType(), new TreeMap<Node, Integer>());
	    	if(!rect_idx.get(n.getType()).containsKey(n)) rect_idx.get(n.getType()).put(n, rect_idx.get(n.getType()).size()+1);
	    	
	    	if(e.getStrength()<min_strength) min_strength = e.getStrength();
	    	if(max_strength<e.getStrength()) max_strength = e.getStrength();
	    }
	    
		//max y
		int N_MAX = 0;
		for(Map<Node, Integer> idx_map: rect_idx.values()) N_MAX = (idx_map.size()>N_MAX)? idx_map.size(): N_MAX; 
		int HEIGHT =  (int) (RECT_HIGHT*(1.0+RECT_HIGHT_MARG)*(N_MAX+1));
	    
		
	    for(int i=0;i<edges.size();i++){
			Edge e = edges.get(i);
			
			
			int y = (int) ((HEIGHT/(rect_idx.get(e.getOrg().getType()).size()+1))
									*rect_idx.get(e.getOrg().getType()).get(e.getOrg()));
			TextBox org = svg_fact(e.getOrg(), y, WIDTH);
		
			y = (int) ((HEIGHT/(rect_idx.get(e.getDest().getType()).size()+1))
									*rect_idx.get(e.getDest().getType()).get(e.getDest()));
			TextBox dst = svg_fact(e.getDest(), y, WIDTH);
			
			double stroke = (LINK_MAX_STROKE-LINK_MIN_STROKE)*(e.getStrength()-min_strength)/(max_strength-min_strength) +LINK_MIN_STROKE;
			
			
			TextLine link = svg_link(e, stroke, org, dst, WIDTH);

			//invisible
			boces.add(link);
		}
		

		
		//main chart and styles
		Chart svg = new Chart(WIDTH, HEIGHT);
		for(Style_Class s : Linked_Rects.DEF_STYLES) svg.add_style(s);
		svg.add_style(DEF_LINK_STYLE);
		svg.add_style(POP_LINK_STYLE);
		svg.add_style(DEF_RECT_STYLE);
		svg.add_style(POP_RECT_STYLE);
		
		//links in group
		Group links = new Group();
		links.add_att(new Attribute<Str>(us.ttic.takeshi.html.svg.node.Node.CLASS, new Str(LINKS)));

		for(INode n : boces) links.add_child(n);
		svg.add_child(links);

	    return svg; 
	}
	
	public static final int RECT_HIGHT = 70;
	public static final double RECT_HIGHT_MARG=0.3;
	public static final double RECT_WIDTH_RATIO = 0.6;
	public static final double RECT_ROUND_RATIO = 0.2;
	private static final String[] RECT_TYPE_STYLES = new String[] {
			"fill:"+GREEN, "fill:"+RED, "fill:"+BLUE			
	};
	private static final String NAME_TYPE_STYLE 
			="overflow-y:auto; color:white; text-align:center; font-family: Sans-serif; vertical-align: middle;";
	private TextBox svg_fact(Node node, int y, int WIDTH){
		String type = node.getType();
		int type_idx = TYPE_IDX.get(type);
		
		int w = WIDTH/TYPE_IDX.size();
		int x = w*type_idx;
		
		Div name = new Div(node.getName());
		name.add_att(new Attribute<Str>(Div.STYLE, new Str(NAME_TYPE_STYLE)));
		name.add_att(new Attribute<Str>(Div.XMLNS, new Str(TextBox.XMLNS_VAL)));
		TextBox box = new TextBox(x, y, (int)(w*RECT_WIDTH_RATIO), RECT_HIGHT, RECT_ROUND_RATIO, name);
		box.rect.add_att(new Attribute<Str>(Rectangle.STYLE, new Str(RECT_TYPE_STYLES[type_idx])));
		
		return box;
	}
	private static final String LINK_STYLE_TEMP
		="stroke:rgb(50, 67, 68); stroke-width:%f;";
	private TextLine svg_link(Edge edge, double stroke, int y_org, int y_dst, int WIDTH) {
		int org_type_idx = TYPE_IDX.get(edge.getOrg().getType());
		int dst_type_idx = TYPE_IDX.get(edge.getDest().getType());
		
		//swap
		Node left_node = null;
		Node right_node= null;
		int y1  = -1;
		int y2  = -1;
		if(org_type_idx<dst_type_idx) {
			left_node = edge.getOrg();
			y1  = y_org;
			right_node= edge.getDest();
			y2 = y_dst;
		}else {
			left_node = edge.getDest();
			y1 =  y_dst;
			right_node= edge.getOrg();
			y2 = y_org;
		}
		
		//position
		int w = WIDTH/TYPE_IDX.size();
		int x1 = (int)(w*(TYPE_IDX.get(left_node.getType())+RECT_WIDTH_RATIO));
		int x2 = (int)(w*(TYPE_IDX.get(right_node.getType())));
		
		//link
		Div snt = new Div(Ling.simple_string(edge.getSnt().getSnt()));
		snt.add_att(new Attribute<Str>(Div.XMLNS, new Str(TextLine.XMLNS_VAL)));
		snt.add_att(new Attribute<Str>(Div.STYLE, new Str(TextLine.STYLE_VAL)));
		
		TextLine line = new TextLine(x1, y1, x2, y2, snt, WIDTH);
		line.def_line.add_att(new Attribute<Str>(Line.STYLE, new Str(String.format(LINK_STYLE_TEMP, stroke))));
		line.pop_line.add_att(new Attribute<Str>(Line.STYLE, new Str(String.format(LINK_STYLE_TEMP, stroke))));
		
		return line;
	}
	private Linked_Rects svg_link(Edge edge, double stroke, TextBox org, TextBox dst, int WIDTH) {
		//link
		Div snt = new Div(edge.getSnt().get_html());
		snt.add_att(new Attribute<Str>(Div.XMLNS, new Str(TextLine.XMLNS_VAL)));
		snt.add_att(new Attribute<Str>(Div.STYLE, new Str(TextLine.STYLE_VAL)));
		
		Linked_Rects line = new Linked_Rects(org, dst, snt, WIDTH);
		line.def_line.add_att(new Attribute<Str>(Line.STYLE, new Str(String.format(LINK_STYLE_TEMP, stroke))));
		line.pop_line.add_att(new Attribute<Str>(Line.STYLE, new Str(String.format(LINK_STYLE_TEMP, stroke))));
		
		return line;
	}
	
	
	static String toHtml(Target_snt targeted){
		StringBuffer sb = new StringBuffer();
		//String[] type = targeted.getType().split("2");
		
		sb.append(String.format("%4.5f : ", targeted.getScore()));
		for(int i=0;i<targeted.getSnt().size();i++){
			if(i==targeted.getOrign().first)sb.append(String.format(PH_BASE_TAG, targeted.getType().first));
			if(i==targeted.getDest().first) sb.append(String.format(PH_BASE_TAG, targeted.getType().second));
			sb.append(targeted.getSnt().get(i).word()+" ");
			if(i==targeted.getOrign().second)sb.append("</font>");
			if(i==targeted.getDest().second) sb.append("</font>");
		}
		return sb.toString();		
	}
	

	
}

/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction.online_demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

//import org.json.simple.JSONObject;

import us.ttic.takeshi.nims.relation_extraction.online_demo.json.Node;
import us.ttic.takeshi.nims.relation_extraction.relation_model.Target_snt;
import us.ttic.takeshi.tools.Scored_obj;

/**
 * @author ikumu
 * algorithm where greedlly find max_bottle_neccks in each layer
 */
public class N_necks extends Graph {
	
	/**
	 * @param sentences
	 * @param comb
	 * @throws Exception
	 */
	public N_necks(List<Target_snt> sentences, String comb, List<String> targeted_prp_labels, int max_str, int max_prc) throws Exception {
		super(sentences, comb, targeted_prp_labels);
		remove_str_necks(max_str);
		remove_prc_necks(max_prc);
	}
	
	private void remove_prc_necks(int max_prc){
		//score for each prc_node
		Map<Node, Double> prc2str_sum = new TreeMap<Node, Double>();
		for(Edge e : edges){
			if(e.getOrg().getType().equals(Target_snt.PRC_TAG) && e.getDest().getType().equals(Target_snt.STR_TAG)){
				Node prc = e.getOrg();
				if(!prc2str_sum.containsKey(prc)) prc2str_sum.put(prc, 0.0);
				prc2str_sum.put(prc, prc2str_sum.get(prc)+e.getStrength());
			}
		}
		
		//rank prc nodes
		List<Scored_obj<Node>> prcs = new ArrayList<Scored_obj<Node>>();
		for(Map.Entry<Node, Double> e : prc2str_sum.entrySet()) prcs.add(new Scored_obj<Node>(e.getKey(), e.getValue()));
		Collections.sort(prcs);
		Collections.reverse(prcs);
		
		//necks
		List<Node> necks = new ArrayList<Node>();
		for(Scored_obj<Node> n : prcs.subList(0, Math.min(prcs.size(), max_prc))) necks.add(n.obj);
		
		//renew edges
		List<Edge> involved = new ArrayList<Edge>();
		for(Edge e: edges){
			if(e.getOrg().getType().equals(Target_snt.PRC_TAG) && !necks.contains(e.getOrg())){
				//nothing to do
			}else{
				involved.add(e);
			}
		}
		edges = involved;
	}
	
	private void remove_str_necks(int max_str){
		Map<Node, Double> str2prp_sum = new TreeMap<Node, Double>();
		Map<Node, Double> str2prc_sum = new TreeMap<Node, Double>();
		for(Edge e : edges){
			//only edge which contains structure 
			Node str = null;
			Map<Node, Double> sum = null;
			if(e.getOrg().getType().equals(Target_snt.STR_TAG) 
					&& e.getDest().getType().equals(Target_snt.PRP_TAG)){
				str = e.getOrg();
				sum = str2prp_sum;
			}else if(e.getOrg().getType().equals(Target_snt.PRC_TAG) 
					&& e.getDest().getType().equals(Target_snt.STR_TAG)){
				str = e.getDest();
				sum = str2prc_sum;
			}else{
				continue;
			}
			
			if(!sum.containsKey(str)) sum.put(str, 0.0);
			sum.put(str, sum.get(str)+e.getStrength());
		}
		
		//rank str nodes
		List<Scored_obj<Node>> strs = new ArrayList<Scored_obj<Node>>();
		for(Map.Entry<Node,Double> e : str2prc_sum.entrySet()){
			double score = Math.min(e.getValue(), 
									(str2prp_sum.containsKey(e.getKey()))? str2prp_sum.get(e.getKey()): 0.0);
					
			strs.add(new Scored_obj<Node>(e.getKey(), score));
		}
		Collections.sort(strs);
		Collections.reverse(strs);
		
		//nods to find
		Set<Node>necks = new TreeSet<Node>();
		for(Scored_obj<Node> scored : strs.subList(0, Math.min(max_str, strs.size()))) necks.add(scored.obj);
		
		//renew edges
		List<Edge> involved = new ArrayList<Edge>();
		for(Edge e: edges){
			if(necks.contains(e.getOrg()) || necks.contains(e.getDest())) involved.add(e);
		}
		edges = involved;
	}


	/**
	 * test code here
	 */
	static final String LRS_TAG = "--lrs";
	static final String HTML_TAG = "--html";
	static final String TARGET_PRP_TAG = "--target_prp";
	static final String HTML_HEAD = 
					"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"ja\" lang=\"ja\">"
					+ "<head profile=\"http://purl.org/net/ns/metaprof\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>";
	public static void main(String[] args) throws Exception {
		//read args
		String lrf_file = null;
		String html_file = "tmp.html";
		List<String> target = new ArrayList<>();
		
		//default
		int max_str = 5;
		int max_prc = 5;
		for(int i=0;i<args.length;i++){
			if(args[i].equals(LRS_TAG)){
				lrf_file = args[++i];
			}else if(args[i].equals(HTML_TAG)){
				html_file = args[++i];
			}else if(args[i].equals(TARGET_PRP_TAG)) {
				i++;
				while(i<args.length && !args[i].startsWith("--")) {
					target.add(args[i++]);
				}
				if(!(i<args.length)) i--;
			}
		}
		if(lrf_file==null || html_file==null){
			System.err.println("invalid arg :");
		}
		
		//readd  file
		List<Target_snt> targeted = new ArrayList<Target_snt>();
		BufferedReader reader = new BufferedReader(new FileReader(lrf_file));
		System.out.println("loading lrs : "+lrf_file);
		String line=null;
		while((line=reader.readLine())!=null){ 
			targeted.add(new Target_snt(line));
		}
		reader.close();
		
		//List<String> targets = new ArrayList<String>(Arrays.asList("tensile strength", "toughness", "corrosion resistance", "xxx"));
		Igraph g = new N_necks(targeted, Graph.CMB_MAX, target, max_str, max_prc);
		BufferedWriter writer = new BufferedWriter(new FileWriter(html_file));
		writer.write(HTML_HEAD+"\n");
		writer.write("<body><graph>\n");
		writer.write(g.get_svg(1000).get_html());
		writer.write("</body></graph></html>");
		writer.close();
		
		System.out.println("DONE");

	}

}

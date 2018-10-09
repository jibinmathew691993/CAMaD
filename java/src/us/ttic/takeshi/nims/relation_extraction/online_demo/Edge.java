/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction.online_demo;

import us.ttic.takeshi.nims.relation_extraction.online_demo.json.Node;
import us.ttic.takeshi.nims.relation_extraction.relation_model.Target_snt;

/**
 * @author ikumu
 *
 */
public class Edge implements Comparable<Edge>{
	
	private final Node org;
	private final Node dest;
	private double strength = -1;
	private final Target_snt snt;

	
	public Edge(Target_snt target){
		snt = target;
		
		//read sentence
		//String[] types = snt.getType().split("2");
		String type1 = snt.getType().first;
		String type2 = snt.getType().second;
		//nodes
		org=new Node(snt.get_orig_label(), type1);
		dest=new Node(snt.get_dest_label(), type2);
		
		//strenght
		strength = snt.getScore();
	}
	
	public Node getOrg(){
		return org;
	}
	
	public Node getDest(){
		return dest;
	}
	
	public double getStrength(){
		return strength;
	}
	
	public void setStrength(double score){
		strength = score;
	}
	
	public Target_snt getSnt(){
		return snt;
	}
	
	public String toString(){
		return strength+"\t"+org.toString()+"->"+dest.toString();
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(!this.getClass().isInstance(o)) return false;
		
		Edge other = (Edge)o;
		if(other.org.equals(this.org) && other.dest.equals(this.dest)) return true;
		return false;
	}
	
	@Override
	public int compareTo(Edge o) {
		String self = org.toString()+" : "+dest.toString();
		String other= o.org.toString()+" : "+o.dest.toString();
		return self.compareTo(other);
	}
	
	@Override
	public int hashCode(){
		return org.hashCode()+dest.hashCode();
	}
	
	

}

/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

import java.io.Serializable;

/** this is a class which express relations between factors
 * @author takeshi.onishi
 *
 */
public class Relation implements Comparable<Relation>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 291385741825188333L;
	
	//hyper paras
	public static final String UNKNOWN = "UNKOWN";
	public static final String VOID = "VOID";
	public static final String GOLD = "GOLD";
	public static final String[] LABELS = {UNKNOWN,VOID, GOLD};
	
	//privates
	private String relation = UNKNOWN;
	private double score = 0.0;
	
	
	public Relation(String r, double s) {
		for(String l : LABELS)if(l.equals(r)){
			relation = r;
			score = s;
		}
		if(r.equals(UNKNOWN)) System.err.println("invalid relation label : "+r);
	}
	public Relation(double s){
		score = s;
	}
	
	public final String get_relation(){
		return relation;
	}
	public final double get_score(){
		return score;
	}
	@Override
	public String toString(){
		return relation+"("+score+")";
	}
	
	//this is comparable
	@Override
	public int compareTo(Relation o) {
		if(o.score==this.score) return 0;
		return (this.score>o.score)? +1 : -1; 
	}
	
}

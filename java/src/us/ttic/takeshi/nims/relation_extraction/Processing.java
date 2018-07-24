package us.ttic.takeshi.nims.relation_extraction;


import java.util.Map;
import java.util.TreeMap;

public class Processing extends Factor<Processing> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7342348226110555629L;
	
	//relation
	Map<Structure, Relation> relations = new TreeMap<Structure, Relation>();
	
	public Processing(String NAME, String SOURCE, String TYPE){
		super(NAME, SOURCE, TYPE);
	}
	public Processing(String NAME, String SOURCE, String TYPE, String[] VARIETY_TO_SAY) {
		super(NAME, SOURCE, TYPE, VARIETY_TO_SAY);
	}
//	public Processing(Factor<Processing> f){
//		super(f.get_name(), f.get_source());
//		this.super_factor = f.get_super();
//		this.subs = f.get_subs();
//	}

	public final Relation get_relation(Structure struct) {
		if(relations.containsKey(struct)) return relations.get(struct);
		return new Relation(Relation.VOID, 0.0);
	}
	
	public void set_relation(Structure s, Relation r){
		relations.put(s, r);
	}
	
	public void clear_relations(){
		relations = new TreeMap<Structure, Relation>();
	}
	
	public String toString(){
		StringBuffer sb =new StringBuffer();
		
		sb.append(super.toString()+" -> {");
		for(Structure s : relations.keySet()){
			sb.append(s.get_id()+"("+relations.get(s).toString()+"), ");
		}
		sb.append("}");
		
		return sb.toString();
	}

}


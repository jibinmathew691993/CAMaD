package us.ttic.takeshi.nims.relation_extraction;

import java.util.Map;
import java.util.TreeMap;

public class Structure extends Factor<Structure> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7997711515080953495L;
	
	
	//relation to property
	private Map<Property, Relation> relations = new TreeMap<Property, Relation>();
	
	public Structure(String NAME, String SOURCE, String TYPE){
		super(NAME,SOURCE, TYPE);
	}
	public Structure(String NAME, String SOURCE, String TYPE, String[] VARIETY_TO_SAY) {
		super(NAME, SOURCE, TYPE, VARIETY_TO_SAY);
	}
//	public Structure(Factor<?> f){
//		super(f.get_name(), f.get_source());
//		
//		//import trees converting class
//		if(!f.get_super().getClass().equals(this.getClass())) throw new ClassCastException("Invalid super factor : "+f.get_super().getClass()+" for "+this.getClass());
//		for(Ifactor<?> sub : f.get_subs())if(!sub.getClass().equals(this.getClass())) throw new ClassCastException("Invalid sub factor : "+sub.getClass()+" for "+this.getClass());
//		
//		this.super_factor = (Structure)f.get_super();
//		this.subs = new ArrayList<Structure>();
//		for(Ifactor<?> sub : f.get_subs())this.subs.add((Structure)sub);
//	}
	
	public final Relation get_relation(Property prop){
		if(relations.containsKey(prop)) return relations.get(prop);
		return new Relation(Relation.VOID, 0.0);
	}

	public void set_relation(Property p, Relation r){
		relations.put(p, r);
	}
	
	public void clear_relations(){
		relations = new TreeMap<Property, Relation>();
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append(super.toString()+" -> {");
		for(Property s : relations.keySet()){
			sb.append(s.get_id()+"("+relations.get(s).get_score()+"), ");
		}
		sb.append("}");
		
		return sb.toString();
	}
}
